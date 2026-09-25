package com.mydemoapp.automation.config;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;

/**
 * Read-only access to framework configuration.
 *
 * <p>For every key the first <em>defined</em> value wins:
 * <ol>
 *   <li>JVM system property, e.g. {@code -Dapp.path=apps/my.apk}</li>
 *   <li>environment variable, e.g. {@code APP_PATH} (dots become underscores, upper case)</li>
 *   <li>{@code config/config.local.properties} on the classpath (optional, git-ignored)</li>
 *   <li>{@code config/config.properties} on the classpath (committed defaults)</li>
 * </ol>
 * A defined but blank value means "not set", which lets a caller clear a default (e.g. {@code -Dapp.path=}).
 */
public final class Config {

    private static final String DEFAULTS_FILE = "config/config.properties";
    private static final String LOCAL_OVERRIDES_FILE = "config/config.local.properties";

    private static Properties fileProperties;

    private Config() {
    }

    public static URL appiumServerUrl() {
        String value = required("appium.server.url");
        try {
            return URI.create(value).toURL();
        } catch (IllegalArgumentException | MalformedURLException e) {
            throw new ConfigurationException("Invalid 'appium.server.url': '" + value + "'", e);
        }
    }

    public static String platformName() {
        return required("platform.name");
    }

    public static Optional<String> platformVersion() {
        return optional("platform.version");
    }

    public static String automationName() {
        return required("automation.name");
    }

    public static String deviceName() {
        return required("device.name");
    }

    public static Optional<String> udid() {
        return optional("udid");
    }

    public static String appPackage() {
        return required("app.package");
    }

    public static String appActivity() {
        return required("app.activity");
    }

    public static String appWaitActivity() {
        return required("app.wait.activity");
    }

    /**
     * Absolute path of the APK to install, or empty when the app is expected to be installed already.
     * Relative paths are resolved against the working directory (the project root when run via Maven).
     */
    public static Optional<Path> appPath() {
        return optional("app.path").map(Config::toExistingApk);
    }

    public static Duration explicitWaitTimeout() {
        return seconds("explicit.wait.timeout.seconds");
    }

    public static Duration newCommandTimeout() {
        return seconds("new.command.timeout.seconds");
    }

    public static Duration serverInstallTimeout() {
        return seconds("server.install.timeout.seconds");
    }

    private static Path toExistingApk(String value) {
        Path path;
        try {
            path = Path.of(value).toAbsolutePath().normalize();
        } catch (InvalidPathException e) {
            throw new ConfigurationException("Invalid 'app.path': '" + value + "'", e);
        }
        if (!Files.isRegularFile(path)) {
            throw new ConfigurationException("APK not found at '" + path + "' (app.path=" + value + "). "
                    + "Download it from https://github.com/saucelabs/my-demo-app-android/releases into the "
                    + "'apps' folder, or point app.path / APP_PATH at the file.");
        }
        return path;
    }

    private static Duration seconds(String key) {
        String value = required(key);
        long seconds;
        try {
            seconds = Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new ConfigurationException("'" + key + "' must be a whole number of seconds, got '" + value + "'", e);
        }
        if (seconds <= 0) {
            throw new ConfigurationException("'" + key + "' must be a positive number of seconds, got " + value);
        }
        return Duration.ofSeconds(seconds);
    }

    private static String required(String key) {
        return optional(key).orElseThrow(() -> new ConfigurationException(
                "Missing required configuration '" + key + "'. Set it in " + DEFAULTS_FILE
                        + ", pass -D" + key + "=<value>, or export " + toEnvName(key) + "."));
    }

    private static Optional<String> optional(String key) {
        String value = System.getProperty(key);
        if (value == null) {
            value = System.getenv(toEnvName(key));
        }
        if (value == null) {
            value = fileProperties().getProperty(key);
        }
        return Optional.ofNullable(value).map(String::trim).filter(v -> !v.isEmpty());
    }

    private static String toEnvName(String key) {
        return key.replace('.', '_').toUpperCase(Locale.ROOT);
    }

    private static synchronized Properties fileProperties() {
        if (fileProperties == null) {
            Properties properties = new Properties();
            loadInto(properties, DEFAULTS_FILE, true);
            loadInto(properties, LOCAL_OVERRIDES_FILE, false);
            fileProperties = properties;
        }
        return fileProperties;
    }

    private static void loadInto(Properties target, String resource, boolean mandatory) {
        try (InputStream in = Config.class.getClassLoader().getResourceAsStream(resource)) {
            if (in == null) {
                if (mandatory) {
                    throw new ConfigurationException("Configuration file not found on the classpath: " + resource);
                }
                return;
            }
            target.load(in);
        } catch (IOException e) {
            throw new ConfigurationException("Could not read configuration file " + resource, e);
        }
    }
}
