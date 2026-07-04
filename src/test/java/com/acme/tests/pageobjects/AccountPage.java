package com.acme.tests.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Account settings page (/account). Profile form + the green "saved"
 * banner that fades out after 5 seconds — hence the wait-then-read-fast
 * pattern in {@link #successBannerText()}.
 */
public class AccountPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By accountNavLink = By.cssSelector("a#nav-account");
    private final By displayNameInput = By.id("display-name");
    private final By phoneInput = By.cssSelector("input[name='phone']");
    private final By saveBtn = By.xpath("//form[@id='profile-form']//button[@type='submit']");
    private final By successBanner = By.cssSelector("div.alert.alert-success");
    private final By phoneFieldError = By.cssSelector("div.field-error.phone");

    public AccountPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void open(String baseUrl) {
        driver.get(baseUrl + "/account");
        wait.until(ExpectedConditions.visibilityOfElementLocated(displayNameInput));
    }

    public void openViaNav() {
        driver.findElement(accountNavLink).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(displayNameInput));
    }

    public String displayNameValue() {
        return driver.findElement(displayNameInput).getAttribute("value");
    }

    public void updateDisplayName(String newName) {
        driver.findElement(displayNameInput).clear();
        driver.findElement(displayNameInput).sendKeys(newName);
        driver.findElement(saveBtn).click();
    }

    public void updatePhone(String phone) {
        driver.findElement(phoneInput).clear();
        driver.findElement(phoneInput).sendKeys(phone);
        driver.findElement(saveBtn).click();
    }

    public String successBannerText() {
        // the banner auto-dismisses after 5s, so read it immediately after the wait
        wait.until(ExpectedConditions.visibilityOfElementLocated(successBanner));
        return driver.findElement(successBanner).getText();
    }

    public boolean phoneErrorShown() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(phoneFieldError));
        return driver.findElement(phoneFieldError).isDisplayed();
    }
}
