package net.rubrion.text.api.adapter;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.rubrion.text.api.color.ColorTypeRegistry;
import net.rubrion.text.api.TextApiProvider;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public interface TextSerializer {

    record Legacy(char c, LegacyComponentSerializer serializer) implements TextAdapter.ForString {

        public static Legacy HEX = new Legacy('#', LegacyComponentSerializer.legacy('#'));
        public static Legacy SECTION = new Legacy('§', LegacyComponentSerializer.legacy('§'));
        public static Legacy AMPERSAND = new Legacy('&', LegacyComponentSerializer.legacy('&'));
        public static Legacy DOLLAR = new Legacy('$', LegacyComponentSerializer.legacy('$'));

        @Contract("_ -> new")
        public static @NotNull Legacy of (char character) {
            return switch (character) {
                case '#' -> HEX;
                case '§' -> SECTION;
                case '&' -> AMPERSAND;
                case '$' -> DOLLAR;
                default -> new Legacy(character, LegacyComponentSerializer.legacy(character));
            };
        }

        @Override
        public @NotNull Component from(String s) {
            return s == null ? Component.empty() : serializer.deserialize(ColorTypeRegistry.replaceLegacy(s, c));
        }

        @Override
        public @NotNull String to(Component component) {
            return component == null ? "" : ColorTypeRegistry.replaceLegacyBack(serializer.serialize(component), c);
        }

    }

    record Adventure(char o, char c,  MiniMessage serializer) implements TextAdapter.ForString {
        public static Adventure MINI_MESSAGE = new Adventure('<', '>', MiniMessage.miniMessage());

        @Override
        public @NotNull Component from(String s) {
            return s == null ? Component.empty() : serializer.deserialize(ColorTypeRegistry.replaceMini(s, o, c));
        }

        @Override
        public @NotNull String to(Component component) {
            return component == null ? "" : ColorTypeRegistry.replaceMiniBack(serializer.serialize(component), o, c);
        }
    }

    record Ansi(TextAdapter<String> serializer) implements TextAdapter.ForString {
        public static Ansi ANSI = new Ansi(TextApiProvider.get().adapterFor("ansi"));

        @Override
        public @NotNull Component from(String s) {
            return s == null ? Component.empty() : serializer.from(ColorTypeRegistry.replaceMini(s));
        }

        @Override
        public @NotNull String to(Component component) {
            return component == null ? "" : ColorTypeRegistry.replaceMiniBack(serializer.to(component));
        }

    }


    record Ascii(TextAdapter<String> serializer) implements TextAdapter.ForString {
        public static Ascii ASCII = new Ascii(TextApiProvider.get().adapterFor("ascii"));

        @Override
        public @NotNull Component from(String s) {
            return s == null ? Component.empty() : serializer.from(ColorTypeRegistry.replaceMini(s));
        }

        @Override
        public @NotNull String to(Component component) {
            return component == null ? "" : ColorTypeRegistry.replaceMiniBack(serializer.to(component));
        }

    }

    record Html(TextAdapter<String> serializer) implements TextAdapter.ForString {
        public static Html HTML = new Html(TextApiProvider.get().adapterFor("html"));

        @Override
        public @NotNull Component from(String s) {
            return s == null ? Component.empty() : serializer.from(ColorTypeRegistry.replaceMini(s));
        }

        @Override
        public @NotNull String to(Component component) {
            return component == null ? "" : ColorTypeRegistry.replaceMiniBack(serializer.to(component));
        }

    }

}
