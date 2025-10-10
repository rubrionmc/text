package net.rubrion.text.common.serializer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.rubrion.text.api.adapter.TextAdapter;

public class AsciiSerializer implements TextAdapter.ForString {

    @Override
    public Component from(String s) {
        return PlainTextComponentSerializer.plainText().deserialize(s);
    }

    @Override
    public String to(Component component) {
        return PlainTextComponentSerializer.plainText().serialize(component);
    }

}
