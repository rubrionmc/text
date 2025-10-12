package net.rubrion.text.common.lang;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.kyori.adventure.text.Component;
import net.rubrion.text.api.TextApiProvider;
import net.rubrion.text.api.adapter.TextAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TranslationManager {

    private static final TranslationManager INSTANCE = new TranslationManager();
    private final Map<Locale, Properties> translations = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();

    private Locale defaultLocale;

    private TranslationManager() {
    }

    public static TranslationManager getInstance() {
        return INSTANCE;
    }

    public synchronized void reload() {
        translations.clear();
        loadAll();
    }

    private void loadAll() {
        String source = ".lang"; // TODO: move this to a config
        String def = "en_US";

        if (source == null || source.isBlank()) {
            TextApiProvider.get().logger().error("No lang.direction specified in config.yml");
            return;
        }

        defaultLocale = def != null ? Locale.forLanguageTag(def.replace('_', '-')) : Locale.US;

        try {
            if (source.startsWith("http")) {
                loadFromUrl(source);
            } else {
                loadFromPath(source);
            }
        } catch (Exception e) {
            TextApiProvider.get().logger().error("Failed to load translations: {}", e.getMessage(), e);
        }
    }

    private void loadFromPath(String basePath) throws IOException {
        Path dir = Paths.get(basePath);
        if (!Files.exists(dir)) {
            TextApiProvider.get().logger().error("Directory not found: {}", dir.toAbsolutePath());
            return;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.json")) {
            for (Path path : stream) {
                Locale locale = parseLocale(path.getFileName().toString());
                try (var reader = Files.newBufferedReader(path)) {
                    JsonObject json = gson.fromJson(reader, JsonObject.class);
                    if (json == null) continue;

                    Properties props = new Properties();
                    for (var entry : json.entrySet()) {
                        props.setProperty(entry.getKey(), entry.getValue().getAsString());
                    }

                    translations.put(locale, props);
                    TextApiProvider.get().logger().error("Loaded locale {} from {}", locale, path.getFileName());
                } catch (JsonSyntaxException e) {
                    TextApiProvider.get().logger().error("Invalid JSON in {}: {}", path.getFileName(), e.getMessage());
                }
            }
        }
    }

    private void loadFromUrl(String baseUrl) {
        for (String lang : List.of("en_US", defaultLocale.toLanguageTag().replace('-', '_'))) {
            String urlString = baseUrl + "/" + lang + ".json";
            try (InputStreamReader reader = new InputStreamReader(URL.of(URI.create(urlString), null).openStream())) {
                JsonObject json = gson.fromJson(reader, JsonObject.class);
                if (json == null) continue;

                Properties props = new Properties();
                for (var entry : json.entrySet()) {
                    props.setProperty(entry.getKey(), entry.getValue().getAsString());
                }

                translations.put(Locale.forLanguageTag(lang.replace('_', '-')), props);
                System.out.println("[TranslationManager] Loaded remote locale " + lang + " from " + urlString);
            } catch (IOException e) {
                System.err.println("[TranslationManager] Failed to load " + urlString + ": " + e.getMessage());
            }
        }
    }

    private Locale parseLocale(@NotNull String filename) {
        String name = filename.replace(".json", "").replace(".lang", "");
        return Locale.forLanguageTag(name.replace('_', '-'));
    }

    public Component translate(String key, Component fallback, Locale locale, TextAdapter<String> adapter) {
        if (translations.isEmpty()) reload();

        String text = getTranslation(key, locale);

        if (text == null) text = getTranslation(key, defaultLocale);
        if (text == null) text = getTranslation(key, Locale.US);
        if (text == null) return fallback;

        return adapter.from(text);
    }

    private @Nullable String getTranslation(String key, Locale locale) {
        Properties props = translations.get(locale);
        if (props == null) return null;
        return props.getProperty(key);
    }
}
