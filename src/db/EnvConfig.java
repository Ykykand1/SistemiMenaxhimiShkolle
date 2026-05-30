package db;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public final class EnvConfig {
    private static final Map<String, String> DOT_ENV = loadDotEnv();

    private EnvConfig() {}

    public static String get(String key, String defaultValue) {
        String fromOs = System.getenv(key);
        if (fromOs != null && !fromOs.isBlank()) {
            return fromOs;
        }
        String fromFile = DOT_ENV.get(key);
        if (fromFile != null && !fromFile.isBlank()) {
            return fromFile;
        }
        return defaultValue;
    }

    private static Map<String, String> loadDotEnv() {
        Map<String, String> values = new HashMap<>();
        Path envFile = findEnvFile();
        if (!Files.isRegularFile(envFile)) {
            return values;
        }
        try {
            for (String line : Files.readAllLines(envFile)) {
                String trimmed = line.trim();
                if (trimmed.isEmpty() || trimmed.startsWith("#")) {
                    continue;
                }
                int eq = trimmed.indexOf('=');
                if (eq <= 0) {
                    continue;
                }
                String key = trimmed.substring(0, eq).trim();
                String value = unquote(trimmed.substring(eq + 1).trim());
                values.put(key, value);
            }
        } catch (IOException e) {
            System.err.println("Could not read .env file: " + e.getMessage());
        }
        return values;
    }

    private static String unquote(String value) {
        if (value.length() >= 2) {
            char first = value.charAt(0);
            char last = value.charAt(value.length() - 1);
            if ((first == '"' && last == '"') || (first == '\'' && last == '\'')) {
                return value.substring(1, value.length() - 1);
            }
        }
        return value;
    }

    /** Looks for .env in the working directory, then walks up to the Maven project root (pom.xml). */
    private static Path findEnvFile() {
        Path dir = Paths.get(System.getProperty("user.dir")).toAbsolutePath().normalize();
        for (int i = 0; i < 6; i++) {
            Path env = dir.resolve(".env");
            if (Files.isRegularFile(env)) {
                return env;
            }
            if (Files.isRegularFile(dir.resolve("pom.xml"))) {
                return env;
            }
            Path parent = dir.getParent();
            if (parent == null) {
                break;
            }
            dir = parent;
        }
        return Paths.get(System.getProperty("user.dir"), ".env");
    }
}
