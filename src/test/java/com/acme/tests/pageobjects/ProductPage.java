package com.acme.tests.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Product detail page (/products/&lt;sku&gt;). Same brittle shape as
 * {@link LoginPage}: raw By locators, explicit waits per interaction.
 */
public class ProductPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By title = By.cssSelector("h1.product-title");
    private final By price = By.cssSelector("span.product-price");
    private final By qtyInput = By.cssSelector("input[name='qty']");
    private final By addToCartBtn = By.xpath("//button[contains(@class,'add-to-cart')]");
    private final By addedToast = By.cssSelector("div.toast.toast-success");
    private final By cartBadge = By.cssSelector("span.cart-count");

    public ProductPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void open(String baseUrl, String sku) {
        driver.get(baseUrl + "/products/" + sku);
        wait.until(ExpectedConditions.visibilityOfElementLocated(title));
    }

    public String titleText() {
        return driver.findElement(title).getText();
    }

    public String priceText() {
        return driver.findElement(price).getText();
    }

    public void addToCart(int quantity) {
        driver.findElement(qtyInput).clear();
        driver.findElement(qtyInput).sendKeys(String.valueOf(quantity));
        driver.findElement(addToCartBtn).click();
        // toast fades in via CSS animation — explicit wait, as always
        wait.until(ExpectedConditions.visibilityOfElementLocated(addedToast));
    }

    public String cartBadgeCount() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(cartBadge));
        return driver.findElement(cartBadge).getText();
    }
}
