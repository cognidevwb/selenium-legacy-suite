package com.acme.tests.support;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Saves a PNG into target/screenshots/ when a test fails. Only works for
 * tests extending BaseTest — the pre-2021 classes manage their own driver
 * and silently get no screenshot (known gap, JIRA QA-1482).
 */
public class ScreenshotListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        WebDriver driver = BaseTest.getCurrentDriver();
        if (!(driver instanceof TakesScreenshot)) {
            return;
        }
        try {
            byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            File dir = new File(ConfigReader.screenshotDir());
            if (!dir.exists() && !dir.mkdirs()) {
                System.err.println("[ScreenshotListener] could not create " + dir);
                return;
            }
            String name = result.getTestClass().getRealClass().getSimpleName()
                    + "." + result.getMethod().getMethodName()
                    + "-" + System.currentTimeMillis() + ".png";
            File out = new File(dir, name);
            Files.write(out.toPath(), png);
            System.out.println("[ScreenshotListener] saved " + out.getPath());
        } catch (IOException | RuntimeException e) {
            // never let the listener kill the run
            System.err.println("[ScreenshotListener] failed to capture screenshot: " + e.getMessage());
        }
    }
}
