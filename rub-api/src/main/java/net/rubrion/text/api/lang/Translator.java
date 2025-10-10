package net.rubrion.text.api.lang;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

public interface Translator extends Function<Locale, Component> {

    @Contract("_ -> new")
    static @NotNull Translator create(Function<Locale, Component> translationFunction) {
        return new FunctionalTranslator(translationFunction);
    }

    Map<String, String> mappings();

    Component translate(Locale lang);

    @Override
    default Component apply(Locale locale) {
        Component translated = translate(locale);

        for (Map.Entry<String, String> entry : mappings().entrySet()) {
            String placeholder = "%" + entry.getKey() + "%";
            String replacement = entry.getValue();

            TextReplacementConfig replacementConfig = TextReplacementConfig.builder()
                    .match(Pattern.quote(placeholder))
                    .replacement(replacement)
                    .build();

            translated = translated.replaceText(replacementConfig);
        }

        return translated;
    }

    @SuppressWarnings("UnusedReturnValue")
    Translator map(String key, String value);

    @SuppressWarnings("UnusedReturnValue")
    Translator map(Map<String, String> map);

    class FunctionalTranslator implements Translator {
        private final Map<String, String> mappings = new HashMap<>();
        private final Function<Locale, Component> translationFunction;

        public FunctionalTranslator(Function<Locale, Component> translationFunction) {
            this.translationFunction = translationFunction;
        }

        @Override
        public Map<String, String> mappings() {
            return Collections.unmodifiableMap(mappings);
        }

        @Override
        public Component translate(Locale locale) {
            return translationFunction.apply(locale);
        }

        @Override
        public Component apply(Locale locale) {
            Component translated = translate(locale);

            for (Map.Entry<String, String> entry : mappings().entrySet()) {
                String placeholder = "%" + entry.getKey() + "%";
                String replacement = entry.getValue();

                TextReplacementConfig replacementConfig = TextReplacementConfig.builder()
                        .match(Pattern.quote(placeholder))
                        .replacement(replacement)
                        .build();

                translated = translated.replaceText(replacementConfig);
            }

            return translated;
        }

        @Override
        public Translator map(String key, String value) {
            mappings.put(key, value);
            return this;
        }

        @Override
        public Translator map(Map<String, String> map) {
            mappings.putAll(map);
            return this;
        }
    }
}