import net.rubrion.common.api.id.NamespacedId;
import net.rubrion.config.api.ConfigApiProvider;
import net.rubrion.config.common.ConfigBootstrap;
import net.rubrion.text.api.TextApiProvider;
import net.rubrion.text.api.adapter.TextSerializer;
import net.rubrion.text.api.lang.Label;
import net.rubrion.text.common.TextBootstrap;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class LangTest {

    public static void main(String[] args) {
        ConfigApiProvider.register(new ConfigBootstrap());
        TextApiProvider.register(new TextBootstrap(new NamespacedId("test:"), TextSerializer.Adventure.MINI_MESSAGE));

        String[] keys = {
                "test.color.basic", "test.color.verbose", "test.color.hex",
                "test.shadow.basic", "test.shadow.hex", "test.shadow.disable",
                "test.decoration.bold", "test.decoration.italic", "test.decoration.underlined",
                "test.decoration.strikethrough", "test.decoration.obfuscated",
                "test.reset.basic", "test.click.run_command", "test.click.copy_to_clipboard",
                "test.hover.show_text", "test.hover.show_item", "test.hover.show_entity",
                "test.keybind.basic", "test.translatable.basic",
                "test.translatable.with_args", "test.translatable.fallback", "test.insertion.basic",
                "test.rainbow.basic", "test.rainbow.reverse", "test.rainbow.phase",
                "test.gradient.basic", "test.gradient.two_colors", "test.gradient.three_colors",
                "test.transition.basic", "test.transition.phase", "test.font.basic",
                "test.font.custom", "test.newline.basic", "test.newline.hover",
                "test.selector.basic", "test.score.basic",
                "test.nbt.basic", "test.pride.basic",
                "test.pride.trans", "test.sprite.basic",
                "test.sprite.item", "test.head.basic",
                "test.head.entity", "test.head.outer_layer"
        };


        System.out.println("=== MINI MESSAGE & LEGACY ===");
        for (String key : keys) {
            Label label = Label.of(key);
            printMiniMessage(label);
        }

        System.out.println("=== CONSOLE ANSI ===");
        for (String key : keys) {
            Label label = Label.of(key);
            printConsole(label);
        }

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n<html>\n<head><meta charset='UTF-8'><title>Label Test</title></head>\n<body>\n<ul>\n");
        for (String key : keys) {
            Label label = Label.of(key);
            String htmlText = label.in(Locale.US, TextSerializer.Html.HTML);
            html.append("<li><strong>").append(key).append(":</strong> ").append(htmlText).append("</li>\n");
        }
        html.append("</ul>\n</body>\n</html>");

        System.out.println("=== FINAL HTML ===");
        System.out.println(html);
    }

    private static void printMiniMessage(@NotNull Label label) {
        Locale locale = Locale.US;
        System.out.println(label.in(locale, TextSerializer.Adventure.MINI_MESSAGE));
        System.out.println(label.in(locale, TextSerializer.Legacy.AMPERSAND));
    }

    private static void printConsole(@NotNull Label label) {
        Locale locale = Locale.US;
        System.out.println(label.in(locale, TextSerializer.Ansi.ANSI));
    }
}
