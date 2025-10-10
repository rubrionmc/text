package net.rubrion.text.api.color;


import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public record ColorType(@NotNull String name, @NotNull String legacy, @NotNull String color) {

    public ColorType(@NotNull String name, @NotNull String legacy, @NotNull String color) {
        this.name = name;
        this.legacy = legacy;
        this.color = color;


    }

    @Contract("null -> fail")
    public static @NotNull ColorType fromString(String raw) {
        if (raw == null || !raw.contains(":")) throw new IllegalArgumentException("Invalid color string: " + raw);
        String[] parts = raw.split(":");
        if (parts.length != 3) throw new IllegalArgumentException("Color string must have 3 parts: " + raw);
        return new ColorType(parts[0], parts[1], parts[2]);
    }

    @Contract(pure = true)
    public @NotNull String asString() {
        return name + ":" + legacy + ":" + color;
    }

}
