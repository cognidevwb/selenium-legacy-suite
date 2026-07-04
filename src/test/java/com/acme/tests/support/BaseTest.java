package com.acme.tests.support;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;

/**
 * Base class for the tests written after 2021. The older classes
 * (LoginTest, CheckoutTest) still manage their own ChromeDriver — they
 * predate this class and nobody has migrated them.
 *
 * The driver is also stashed in a static field so ScreenshotListener can
 * grab it on failure. This means the suite must never run parallel; see
 * testng.xml (verbose=1, no parallel attribute — keep it that way).
 */
public abstract class BaseTest {

    /** The driver of the currently running test. One at a time — no parallel. */
    private static WebDriver currentDriver;

    protected WebDriver driver;
    protected WebDriverWait wait;
    protected String baseUrl;

    @BeforeMethod(alwaysRun = true)
    public void baseSetUp() {
        driver = DriverFactory.createDriver();
        currentDriver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.timeoutSeconds()));
        baseUrl = ConfigReader.baseUrl();
        driver.get(baseUrl);
    }

    @AfterMethod(alwaysRun = true)
    public void baseTearDown() {
        if (driver != null) {
            driver.quit();
        }
        currentDriver = null;
    }

    /** Used by {@link ScreenshotListener} when a test fails. */
    public static WebDriver getCurrentDriver() {
        return currentDriver;
    }
}
