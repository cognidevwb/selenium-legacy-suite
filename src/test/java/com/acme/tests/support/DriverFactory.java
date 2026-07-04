package com.acme.tests.support;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

/**
 * Creates the WebDriver from the -Dbrowser system property (falling back to
 * config.properties). Chrome unless someone says otherwise; firefox was added
 * for the one release where Chrome 96 broke file downloads and never removed.
 */
public final class DriverFactory {

    private DriverFactory() {
    }

    public static WebDriver createDriver() {
        String browser = System.getProperty("browser", ConfigReader.defaultBrowser());
        boolean headless = Boolean.getBoolean("headless");

        if ("firefox".equalsIgnoreCase(browser)) {
            WebDriverManager.firefoxdriver().setup();
            FirefoxOptions options = new FirefoxOptions();
            if (headless) {
                options.addArguments("-headless");
            }
            return new FirefoxDriver(options);
        }

        // default: chrome
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--window-size=1440,900");
        if (headless) {
            options.addArguments("--headless=new");
        }
        return new ChromeDriver(options);
    }
}
