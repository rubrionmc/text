package net.rubrion.text.common.text;

import net.kyori.adventure.text.Component;
import net.rubrion.text.api.TextApiProvider;
import net.rubrion.text.api.adapter.TextAdapter;
import net.rubrion.text.api.lang.Label;
import net.rubrion.text.api.lang.Translator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextLabel implements Label {
    protected final TextAdapter<String> adapter;
    protected final Translator translator;
    protected final Component text;
    protected final String key;

    private static final Pattern LABEL_PATTERN = Pattern.compile(
            "<label:(?:<text:([^>]*)>)?(?:<key:([^>]*)>)?>"
    );

    public TextLabel(String key, String text,
                     @NotNull TextAdapter<String> adapter,
                     Translator translator) {
        this.adapter = adapter;
        this.translator = translator;
        this.key = key;
        this.text = adapter.from(text);
    }

    @Override
    public Component in(Locale lang) {
        return translator.apply(lang);
    }

    @Override
    public <O> O in(Locale lang, @NotNull TextAdapter<O> adapter) {
        return adapter.to(translator.apply(lang));
    }

    @Override
    public Translator translator() {
        return translator;
    }

    @Override
    public String key() {
        return key;
    }

    @Override
    public Component text() {
        return text;
    }

    @Override
    public TextAdapter<String> adapter() {
        return adapter;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<label:");

        if (text != null && !text.equals(adapter.from(""))) {
            String textContent = adapter.to(text);
            if (textContent != null && !textContent.isEmpty()) {
                sb.append("<text:").append(escape(textContent)).append(">");
            }
        }

        sb.append("<key:").append(escape(key)).append(">");

        sb.append(">");
        return sb.toString();
    }

    /**
     * Creates a TextLabel from a string in the format: <label:<text:%text%><key:%key%>>
     */
    @NotNull
    public static Label fromString(@NotNull String input) {
        return fromString(input, TextApiProvider.get().readerAdapter());
    }

    @NotNull
    public static Label fromString(@NotNull String input, @NotNull TextAdapter<String> adapter) {
        Matcher matcher = LABEL_PATTERN.matcher(input);

        if (!matcher.matches())
            throw new IllegalArgumentException("Invalid label format: " + input);

        String textContent = null;
        String keyContent = null;

        if (matcher.group(1) != null) textContent = unescape(matcher.group(1));
        if (matcher.group(2) != null) keyContent = unescape(matcher.group(2));

        if (keyContent == null)
            throw new IllegalArgumentException("Key is required in label format: " + input);

        return Label.of(keyContent, textContent, adapter);
    }

    private static @NotNull String escape(@NotNull String input) {
        return input.replace("%", "%25")
                .replace(">", "%3E")
                .replace("<", "%3C");
    }

    private static @NotNull String unescape(@NotNull String input) {
        return input.replace("%3C", "<")
                .replace("%3E", ">")
                .replace("%25", "%");
    }
}