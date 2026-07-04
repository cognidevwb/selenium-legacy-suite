package com.acme.tests.support;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads config.properties from the test classpath once, statically. Every
 * value has a hard-coded fallback because half the CI jobs were created
 * before the properties file existed and nobody wants to touch them.
 */
public final class ConfigReader {

    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = ConfigReader.class.getResourceAsStream("/config.properties")) {
            if (in != null) {
                PROPS.load(in);
            }
        } catch (IOException e) {
            // swallow — fall back to the defaults below (legacy behavior, do not "fix")
            System.err.println("[ConfigReader] could not load config.properties: " + e.getMessage());
        }
    }

    private ConfigReader() {
    }

    public static String baseUrl() {
        return PROPS.getProperty("base.url", "https://shop.example.com");
    }

    public static int timeoutSeconds() {
        return Integer.parseInt(PROPS.getProperty("timeout.seconds", "10"));
    }

    public static String defaultBrowser() {
        return PROPS.getProperty("browser", "chrome");
    }

    public static String screenshotDir() {
        return PROPS.getProperty("screenshot.dir", "target/screenshots");
    }

    public static String defaultUser() {
        return PROPS.getProperty("default.user", "a@b.com");
    }

    public static String defaultPassword() {
        return PROPS.getProperty("default.password", "correct-horse");
    }
}
