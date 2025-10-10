package net.rubrion.text.common.serializer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.rubrion.text.api.adapter.TextAdapter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class HtmlSerializer implements TextAdapter.ForString {

    private static final Map<TextColor, String> COLOR_TO_NAME = new HashMap<>();

    static {
        COLOR_TO_NAME.put(NamedTextColor.BLACK, "black");
        COLOR_TO_NAME.put(NamedTextColor.DARK_BLUE, "darkblue");
        COLOR_TO_NAME.put(NamedTextColor.DARK_GREEN, "darkgreen");
        COLOR_TO_NAME.put(NamedTextColor.DARK_AQUA, "darkcyan");
        COLOR_TO_NAME.put(NamedTextColor.DARK_RED, "darkred");
        COLOR_TO_NAME.put(NamedTextColor.DARK_PURPLE, "darkmagenta");
        COLOR_TO_NAME.put(NamedTextColor.GOLD, "goldenrod");
        COLOR_TO_NAME.put(NamedTextColor.GRAY, "gray");
        COLOR_TO_NAME.put(NamedTextColor.DARK_GRAY, "darkgray");
        COLOR_TO_NAME.put(NamedTextColor.BLUE, "blue");
        COLOR_TO_NAME.put(NamedTextColor.GREEN, "lime");
        COLOR_TO_NAME.put(NamedTextColor.AQUA, "cyan");
        COLOR_TO_NAME.put(NamedTextColor.RED, "red");
        COLOR_TO_NAME.put(NamedTextColor.LIGHT_PURPLE, "magenta");
        COLOR_TO_NAME.put(NamedTextColor.YELLOW, "yellow");
        COLOR_TO_NAME.put(NamedTextColor.WHITE, "white");
    }

    @Override
    public Component from(String html) {
        return Component.text(stripHtmlTags(html));
    }

    @Override
    public String to(Component component) {
        StringBuilder html = new StringBuilder();
        appendComponent(component, html);
        return html.toString();
    }

    private void appendComponent(@NotNull Component component, StringBuilder html) {
        Style style = component.style();
        boolean hasSpan = false;

        if (hasStyles(style)) {
            html.append("<span style=\"");
            appendStyles(style, html);
            html.append("\"");

            HoverEvent<?> hover = style.hoverEvent();
            if (hover != null && hover.action() == HoverEvent.Action.SHOW_TEXT) {
                html.append(" title=\"");
                Component hoverComponent = (Component) hover.value();
                html.append(escapeHtml(extractPlainText(hoverComponent)));
                html.append("\"");
            }

            html.append(">");
            hasSpan = true;
        }

        ClickEvent click = style.clickEvent();
        if (click != null) {
            if (click.action() == ClickEvent.Action.OPEN_URL) {
                html.append("<a href=\"").append(escapeHtml(click.value())).append("\">");
            } else if (click.action() == ClickEvent.Action.COPY_TO_CLIPBOARD) {
                html.append("<span onclick=\"navigator.clipboard.writeText('")
                        .append(escapeJs(click.value()))
                        .append("')\" style=\"cursor:pointer;\">");
            } else {
                html.append("<span data-click-action=\"").append(click.action().toString())
                        .append("\" data-click-value=\"").append(escapeHtml(click.value()))
                        .append("\" style=\"cursor:pointer;\">");
            }
        }

        if (component instanceof TextComponent) {
            String content = ((TextComponent) component).content();
            if (!content.isEmpty()) {
                html.append(escapeHtml(content));
            }
        }

        for (Component child : component.children()) {
            appendComponent(child, html);
        }

        if (click != null) {
            if (click.action() == ClickEvent.Action.OPEN_URL) {
                html.append("</a>");
            } else {
                html.append("</span>");
            }
        }

        if (hasSpan) {
            html.append("</span>");
        }
    }

    private void appendStyles(@NotNull Style style, StringBuilder html) {
        boolean first = true;

        TextColor color = style.color();
        if (color != null) {
            if (!first) html.append(";");
            html.append("color:");
            String colorName = COLOR_TO_NAME.get(color);
            if (colorName != null) {
                html.append(colorName);
            } else {
                html.append("#").append(String.format("%06x", color.value()));
            }
            first = false;
        }

        if (style.decoration(TextDecoration.BOLD) == TextDecoration.State.TRUE) {
            if (!first) html.append(";");
            html.append("font-weight:bold");
            first = false;
        }

        if (style.decoration(TextDecoration.ITALIC) == TextDecoration.State.TRUE) {
            if (!first) html.append(";");
            html.append("font-style:italic");
            first = false;
        }

        if (style.decoration(TextDecoration.UNDERLINED) == TextDecoration.State.TRUE) {
            if (!first) html.append(";");
            html.append("text-decoration:underline");
            first = false;
        }

        if (style.decoration(TextDecoration.STRIKETHROUGH) == TextDecoration.State.TRUE) {
            if (!first) html.append(";");
            html.append("text-decoration:line-through");
            first = false;
        }

        if (style.decoration(TextDecoration.OBFUSCATED) == TextDecoration.State.TRUE) {
            if (!first) html.append(";");
            html.append("filter:blur(2px)");
            first = false;
        }
    }

    private boolean hasStyles(@NotNull Style style) {
        return style.color() != null
                || style.decoration(TextDecoration.BOLD) == TextDecoration.State.TRUE
                || style.decoration(TextDecoration.ITALIC) == TextDecoration.State.TRUE
                || style.decoration(TextDecoration.UNDERLINED) == TextDecoration.State.TRUE
                || style.decoration(TextDecoration.STRIKETHROUGH) == TextDecoration.State.TRUE
                || style.decoration(TextDecoration.OBFUSCATED) == TextDecoration.State.TRUE
                || style.hoverEvent() != null;
    }

    private @NotNull String extractPlainText(Component component) {
        StringBuilder text = new StringBuilder();
        if (component instanceof TextComponent) {
            text.append(((TextComponent) component).content());
        }
        for (Component child : component.children()) {
            text.append(extractPlainText(child));
        }
        return text.toString();
    }

    private @NotNull String escapeHtml(@NotNull String text) {
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private @NotNull String escapeJs(@NotNull String text) {
        return text.replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");
    }

    @Contract(pure = true)
    private @NotNull String stripHtmlTags(@NotNull String html) {
        return html.replaceAll("<[^>]*>", "");
    }
}