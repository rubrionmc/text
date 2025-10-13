package net.rubrion.text.api.adapter;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.rubrion.text.api.TextApiProvider;
import org.jetbrains.annotations.*;

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
            return s == null ? Component.empty() : serializer.deserialize(s);
        }

        @Override
        public @NotNull String to(Component component) {
            return component == null ? "" : serializer.serialize(component);
        }

    }

    record Adventure(char o, char c,  MiniMessage serializer) implements TextAdapter.ForString {
        public static Adventure MINI_MESSAGE = new Adventure('<', '>', MiniMessage.miniMessage());

        @Override
        public @NotNull Component from(String s) {
            return s == null ? Component.empty() : serializer.deserialize(s);
        }

        @Override
        public @NotNull String to(Component component) {
            return component == null ? "" : serializer.serialize(component);
        }
    }

    record Ansi(TextAdapter<String> serializer) implements TextAdapter.ForString {
        public static Ansi ANSI = new Ansi(TextApiProvider.get().adapterFor("ansi"));

        @Override
        public @NotNull Component from(String s) {
            return s == null ? Component.empty() : serializer.from(s);
        }

        @Override
        public @NotNull String to(Component component) {
            return component == null ? "" : serializer.to(component);
        }

    }


    record Ascii(TextAdapter<String> serializer) implements TextAdapter.ForString {
        public static Ascii ASCII = new Ascii(TextApiProvider.get().adapterFor("ascii"));

        @Override
        public @NotNull Component from(String s) {
            return s == null ? Component.empty() : serializer.from(s);
        }

        @Override
        public @NotNull String to(Component component) {
            return component == null ? "" : serializer.to(component);
        }

    }

    record Html(TextAdapter<String> serializer) implements TextAdapter.ForString {
        public static Html HTML = new Html(TextApiProvider.get().adapterFor("html"));

        @Override
        public @NotNull Component from(String s) {
            return s == null ? Component.empty() : serializer.from(s);
        }

        @Override
        public @NotNull String to(Component component) {
            return component == null ? "" : serializer.to(component);
        }

    }

}