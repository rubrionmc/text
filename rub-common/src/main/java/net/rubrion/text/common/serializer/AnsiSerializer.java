package net.rubrion.text.common.serializer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.rubrion.text.api.adapter.TextAdapter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class AnsiSerializer implements TextAdapter.ForString {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BOLD = "\u001B[1m";
    private static final String ANSI_ITALIC = "\u001B[3m";
    private static final String ANSI_UNDERLINE = "\u001B[4m";
    private static final String ANSI_STRIKETHROUGH = "\u001B[9m";
    private static final String ANSI_OBFUSCATED = "\u001B[8m"; // Hidden text

    private static final Map<TextColor, String> COLOR_TO_ANSI = new HashMap<>();

    static {
        COLOR_TO_ANSI.put(NamedTextColor.BLACK, "\u001B[30m");
        COLOR_TO_ANSI.put(NamedTextColor.DARK_BLUE, "\u001B[34m");
        COLOR_TO_ANSI.put(NamedTextColor.DARK_GREEN, "\u001B[32m");
        COLOR_TO_ANSI.put(NamedTextColor.DARK_AQUA, "\u001B[36m");
        COLOR_TO_ANSI.put(NamedTextColor.DARK_RED, "\u001B[31m");
        COLOR_TO_ANSI.put(NamedTextColor.DARK_PURPLE, "\u001B[35m");
        COLOR_TO_ANSI.put(NamedTextColor.GOLD, "\u001B[33m");
        COLOR_TO_ANSI.put(NamedTextColor.GRAY, "\u001B[37m");
        COLOR_TO_ANSI.put(NamedTextColor.DARK_GRAY, "\u001B[90m");
        COLOR_TO_ANSI.put(NamedTextColor.BLUE, "\u001B[94m");
        COLOR_TO_ANSI.put(NamedTextColor.GREEN, "\u001B[92m");
        COLOR_TO_ANSI.put(NamedTextColor.AQUA, "\u001B[96m");
        COLOR_TO_ANSI.put(NamedTextColor.RED, "\u001B[91m");
        COLOR_TO_ANSI.put(NamedTextColor.LIGHT_PURPLE, "\u001B[95m");
        COLOR_TO_ANSI.put(NamedTextColor.YELLOW, "\u001B[93m");
        COLOR_TO_ANSI.put(NamedTextColor.WHITE, "\u001B[97m");
    }

    @Override
    public Component from(String ansi) {
        // This is a simplified implementation that strips ANSI codes
        return Component.text(stripAnsiCodes(ansi));
    }

    @Override
    public String to(Component component) {
        StringBuilder ansi = new StringBuilder();
        appendComponent(component, ansi, Style.empty());
        ansi.append(ANSI_RESET);
        return ansi.toString();
    }

    private void appendComponent(@NotNull Component component, StringBuilder ansi, @NotNull Style parentStyle) {
        Style style = component.style();
        Style mergedStyle = parentStyle.merge(style);

        applyStyleChanges(parentStyle, mergedStyle, ansi);

        if (component instanceof TextComponent) {
            String content = ((TextComponent) component).content();
            if (!content.isEmpty()) {
                ansi.append(content);
            }
        }

        for (Component child : component.children()) {
            appendComponent(child, ansi, mergedStyle);
        }

        if (hasStyleChanges(parentStyle, mergedStyle)) {
            ansi.append(ANSI_RESET);
            applyStyle(parentStyle, ansi);
        }
    }

    private void applyStyleChanges(Style oldStyle, Style newStyle, StringBuilder ansi) {
        if (needsReset(oldStyle, newStyle)) {
            ansi.append(ANSI_RESET);
            applyStyle(newStyle, ansi);
        } else {
            applyIncrementalChanges(oldStyle, newStyle, ansi);
        }
    }

    private void applyStyle(@NotNull Style style, StringBuilder ansi) {
        TextColor color = style.color();
        if (color != null) {
            String ansiColor = COLOR_TO_ANSI.get(color);
            if (ansiColor != null) {
                ansi.append(ansiColor);
            } else {
                int rgb = color.value();
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                ansi.append(String.format("\u001B[38;2;%d;%d;%dm", r, g, b));
            }
        }

        if (style.decoration(TextDecoration.BOLD) == TextDecoration.State.TRUE) ansi.append(ANSI_BOLD);
        if (style.decoration(TextDecoration.ITALIC) == TextDecoration.State.TRUE) ansi.append(ANSI_ITALIC);
        if (style.decoration(TextDecoration.UNDERLINED) == TextDecoration.State.TRUE) ansi.append(ANSI_UNDERLINE);
        if (style.decoration(TextDecoration.STRIKETHROUGH) == TextDecoration.State.TRUE) ansi.append(ANSI_STRIKETHROUGH);
        if (style.decoration(TextDecoration.OBFUSCATED) == TextDecoration.State.TRUE) ansi.append(ANSI_OBFUSCATED);
    }

    private void applyIncrementalChanges(@NotNull Style oldStyle, @NotNull Style newStyle, StringBuilder ansi) {
        TextColor oldColor = oldStyle.color();
        TextColor newColor = newStyle.color();
        if (newColor != null && !newColor.equals(oldColor)) {
            String ansiColor = COLOR_TO_ANSI.get(newColor);
            if (ansiColor != null) {
                ansi.append(ansiColor);
            } else {
                int rgb = newColor.value();
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                ansi.append(String.format("\u001B[38;2;%d;%d;%dm", r, g, b));
            }
        }

        if (newStyle.decoration(TextDecoration.BOLD) == TextDecoration.State.TRUE
                && oldStyle.decoration(TextDecoration.BOLD) != TextDecoration.State.TRUE)
            ansi.append(ANSI_BOLD);

        if (newStyle.decoration(TextDecoration.ITALIC) == TextDecoration.State.TRUE
                && oldStyle.decoration(TextDecoration.ITALIC) != TextDecoration.State.TRUE)
            ansi.append(ANSI_ITALIC);

        if (newStyle.decoration(TextDecoration.UNDERLINED) == TextDecoration.State.TRUE
                && oldStyle.decoration(TextDecoration.UNDERLINED) != TextDecoration.State.TRUE)
            ansi.append(ANSI_UNDERLINE);

        if (newStyle.decoration(TextDecoration.STRIKETHROUGH) == TextDecoration.State.TRUE
                && oldStyle.decoration(TextDecoration.STRIKETHROUGH) != TextDecoration.State.TRUE)
            ansi.append(ANSI_STRIKETHROUGH);

        if (newStyle.decoration(TextDecoration.OBFUSCATED) == TextDecoration.State.TRUE
                && oldStyle.decoration(TextDecoration.OBFUSCATED) != TextDecoration.State.TRUE)
            ansi.append(ANSI_OBFUSCATED);
    }

    private boolean needsReset(Style oldStyle, Style newStyle) {
        for (TextDecoration decoration : TextDecoration.values()) {
            if (oldStyle.decoration(decoration) == TextDecoration.State.TRUE
                    && newStyle.decoration(decoration) != TextDecoration.State.TRUE) {
                return true;
            }
        }
        return false;
    }

    private boolean hasStyleChanges(@NotNull Style oldStyle, @NotNull Style newStyle) {
        if (!equalColors(oldStyle.color(), newStyle.color())) {
            return true;
        }
        for (TextDecoration decoration : TextDecoration.values()) {
            if (oldStyle.decoration(decoration) != newStyle.decoration(decoration)) {
                return true;
            }
        }
        return false;
    }

    private boolean equalColors(TextColor c1, TextColor c2) {
        if (c1 == null && c2 == null) return true;
        if (c1 == null || c2 == null) return false;
        return c1.equals(c2);
    }

    @Contract(pure = true)
    private @NotNull String stripAnsiCodes(@NotNull String text) {
        return text.replaceAll("\u001B\\[[0-9;]*[a-zA-Z]", "");
    }

}
