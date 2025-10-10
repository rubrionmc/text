package net.rubrion.text.common;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.rubrion.common.api.id.NamespacedId;
import net.rubrion.config.api.field.ConfigField;

import net.rubrion.text.api.TextApiModule;
import net.rubrion.text.api.adapter.TextAdapter;

import net.rubrion.text.api.lang.Label;
import net.rubrion.text.api.lang.Translator;
import net.rubrion.text.common.lang.TranslationManager;

import net.rubrion.text.common.serializer.AnsiSerializer;
import net.rubrion.text.common.serializer.AsciiSerializer;
import net.rubrion.text.common.serializer.HtmlSerializer;
import net.rubrion.text.common.text.TextLabel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Locale;

public class TextBootstrap implements TextApiModule {
    private final TextAdapter<String> reader;
    private final NamespacedId loader;
    @Getter private final String direction;
    private Logger logger;

    public TextBootstrap(NamespacedId loader, TextAdapter<String> reader, String direction) {
        this.loader = loader;
        this.reader = reader;
        this.direction = direction;

        TranslationManager.getInstance().reload();
    }

    @Override
    public Component translate(String key, Component text, Locale locale, TextAdapter<String> adapter) {
        return TranslationManager.getInstance()
                .translate(key, text, locale, adapter);
    }

    @Override
    public void reload() {
        TranslationManager.getInstance().reload();
    }

    @Override
    public TextAdapter<String> readerAdapter() {
        return reader;
    }

    @Override
    public TextAdapter<String> adapterFor(@NotNull String key) {
        return switch (key.toLowerCase()) {
            case "ansi", "ansii" -> new AnsiSerializer();
            case "asci", "ascii" -> new AsciiSerializer();
            case "html", "http" -> new HtmlSerializer();
            default -> readerAdapter();
        };
    }

    @Override
    public Label createLabel(String key, @Nullable String text, @NotNull TextAdapter<String> adapter, Translator translator) {
        return new TextLabel(key, text, adapter, translator);
    }

    @Override
    public Label createLabel(String label) {
        return TextLabel.fromString(label);
    }

    @Override
    public NamespacedId loader() {
        return loader;
    }

    @Override
    public Logger logger() {
        return logger;
    }

}
