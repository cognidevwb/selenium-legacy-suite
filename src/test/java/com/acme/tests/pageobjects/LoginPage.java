package com.acme.tests.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Classic Selenium Page Object: raw {@link By} locators stored as fields,
 * an explicit {@link WebDriverWait} per interaction. Every locator here is
 * a migration worklist item — id/name are salvageable, the XPath/CSS ones
 * become role/label locators in the Playwright target.
 */
public class LoginPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // brittle locators — the migration's translation worklist
    private final By email = By.id("email");
    private final By password = By.cssSelector("input[name='password']");
    private final By submit = By.xpath("//button[@class='btn-primary submit']");
    private final By cartIcon = By.id("cart-icon");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void open(String baseUrl) {
        driver.get(baseUrl + "/login");
    }

    public void loginAs(String user, String pass) {
        driver.findElement(email).sendKeys(user);
        driver.findElement(password).sendKeys(pass);
        driver.findElement(submit).click();
        // explicit wait — to be deleted in favor of Playwright auto-waiting
        wait.until(ExpectedConditions.visibilityOfElementLocated(cartIcon));
    }
}
