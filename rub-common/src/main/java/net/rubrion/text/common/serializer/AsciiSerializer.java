package net.rubrion.text.common.serializer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.rubrion.text.api.adapter.TextAdapter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class AsciiSerializer implements TextAdapter.ForString {

    private static final PlainTextComponentSerializer PLAIN = PlainTextComponentSerializer.plainText();

    @Override
    public Component from(String s) {
        String asciiOnly = toAscii(s);
        return PLAIN.deserialize(asciiOnly);
    }

    @Override
    public String to(Component component) {
        String serialized = PLAIN.serialize(component);
        return toAscii(serialized);
    }

    @Contract(pure = true)
    private static @NotNull String toAscii(String input) {
        if (input == null) return "";
        return input.replaceAll("[^\\x00-\\x7F]", "?");
    }
}
