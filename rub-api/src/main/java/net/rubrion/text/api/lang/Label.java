package net.rubrion.text.api.lang;

import net.kyori.adventure.text.Component;
import net.rubrion.text.api.TextApiProvider;
import net.rubrion.text.api.adapter.TextAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

public interface Label {

    default Label replace(String key, String value) {
        translator().map(key, value);
        return this;
    }

    default Label replace(Map<String, String> map) {
        translator().map(map);
        return this;
    }

    Component in(Locale lang);

    <O> O in(Locale lang, @NotNull TextAdapter<O> adapter);

    Translator translator();

    String key();

    Component text();

    TextAdapter<String> adapter();

    String toString();

    @NotNull
    static Label of(String key) {
        return of(key, "<gray>No text for </gray><red>\"" + key + "\" please report this");
    }

    @NotNull
    static Label of(String key, String text) {
        return of(key, text, TextApiProvider.get().readerAdapter());
    }

    @NotNull
    static Label of(String key, String text, TextAdapter<String> adapter) {
        return of(key, text, adapter, lang -> TextApiProvider.get().translate(key, adapter.from(text), lang, adapter));
    }


    @NotNull
    static Label of(String key, String text,
                    TextAdapter<String> adapter,
                    Function<Locale, Component> translationFunction) {
        return of(key, text, adapter, Translator.create(translationFunction));
    }

    @NotNull
    static Label of(String key, String text,
                    TextAdapter<String> adapter,
                    Translator translator) {
        return TextApiProvider.get().createLabel(key, text, adapter, translator);
    }

    @NotNull
    static Label fromString(@NotNull String input) {
        return TextApiProvider.get().createLabel(input);
    }

}