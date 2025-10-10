package net.rubrion.text.api.color;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class ColorTypeRegistry {

    private static final Set<ColorType> types = new HashSet<>();

    public static void register(ColorType type) {
        types.add(type);
    }

    @Contract(value = " -> new", pure = true)
    public static @NotNull Set<ColorType> getTypes() {
        return new HashSet<>(types);
    }

    // ------------------- LEGACY -------------------

    public static String replaceLegacy(String s) {
        return replaceLegacy(s, '§');
    }

    public static String replaceLegacy(String s, char legacyChar) {
        if (s == null) return null;

        for (ColorType type : types) {
            String code = type.legacy();
            String color = type.color();

            s = s.replace(legacyChar + code, legacyChar + "#" + color);
        }

        return s;
    }

    public static String replaceLegacyBack(String s) {
        return replaceLegacyBack(s, '§');
    }

    public static String replaceLegacyBack(String s, char legacyChar) {
        if (s == null) return null;

        for (ColorType type : types) {
            String code = type.legacy();
            String color = type.color();

            s = s.replace(legacyChar + "#" + color, legacyChar + code);
        }

        return s;
    }

    // ------------------- MINI / CUSTOM -------------------

    public static String replaceMini(String s) {
        return replaceMini(s, '<', '>');
    }

    public static String replaceMini(String s, char open, char close) {
        if (s == null) return null;

        for (ColorType type : types) {
            String name = type.name();
            String color = type.color();

            s = s.replace(open + name + close, open + "#" + color + close);
        }

        return s;
    }

    public static String replaceMiniBack(String s) {
        return replaceMiniBack(s, '<', '>');
    }

    public static String replaceMiniBack(String s, char open, char close) {
        if (s == null) return null;

        for (ColorType type : types) {
            String name = type.name();
            String color = type.color();

            s = s.replace(open + "#" + color + close, open + name + close);
        }

        return s;
    }

    // ------------------- GENERIC COLOR CONVERSION -------------------

    public static String hexToName(String hexColor) {
        if (hexColor == null) return null;

        for (ColorType type : types) {
            if (hexColor.equalsIgnoreCase(type.color())) {
                return type.name();
            }
        }
        return hexColor;
    }

    public static String nameToHex(String colorName) {
        if (colorName == null) return null;

        for (ColorType type : types) {
            if (colorName.equalsIgnoreCase(type.name())) {
                return type.color();
            }
        }
        return colorName;
    }

    public static String legacyToHex(String legacyCode) {
        if (legacyCode == null || legacyCode.length() != 1) return null;

        for (ColorType type : types) {
            if (legacyCode.equals(type.legacy())) {
                return type.color();
            }
        }
        return null;
    }

    public static String hexToLegacy(String hexColor) {
        if (hexColor == null) return null;

        for (ColorType type : types) {
            if (hexColor.equalsIgnoreCase(type.color())) {
                return type.legacy();
            }
        }
        return null;
    }
}