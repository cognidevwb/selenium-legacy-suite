package com.acme.tests.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CheckoutPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By cartIcon = By.id("cart-icon");
    private final By checkoutBtn = By.xpath("//button[contains(text(),'Checkout')]");
    private final By placeOrderBtn = By.cssSelector("button#place-order");
    private final By confirmation = By.id("order-confirmation");
    private final By cardError = By.xpath("//div[@class='field-error card-number']");

    public CheckoutPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void openCart() {
        driver.findElement(cartIcon).click();
        wait.until(ExpectedConditions.elementToBeClickable(checkoutBtn));
        driver.findElement(checkoutBtn).click();
    }

    public void placeOrder() {
        driver.findElement(placeOrderBtn).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(confirmation));
    }

    public String confirmationText() {
        return driver.findElement(confirmation).getText();
    }

    public boolean cardErrorShown() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(cardError));
        return driver.findElement(cardError).isDisplayed();
    }
}
