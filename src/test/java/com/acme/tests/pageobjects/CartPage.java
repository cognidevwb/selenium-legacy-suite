package com.acme.tests.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * The cart page (/cart). The row XPaths are anchored on the product name
 * cell — every markup change in the cart table breaks them, which is
 * exactly why this suite is migration input.
 */
public class CartPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By cartTable = By.cssSelector("table#cart-table");
    private final By cartRows = By.cssSelector("table#cart-table tbody tr.cart-row");
    private final By subtotal = By.id("cart-subtotal");
    private final By emptyMessage = By.cssSelector("div.cart-empty");
    private final By checkoutBtn = By.xpath("//button[contains(text(),'Checkout')]");

    public CartPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void open(String baseUrl) {
        driver.get(baseUrl + "/cart");
        wait.until(ExpectedConditions.presenceOfElementLocated(cartTable));
    }

    public int rowCount() {
        return driver.findElements(cartRows).size();
    }

    public List<String> itemNames() {
        return driver.findElements(By.cssSelector("table#cart-table tbody tr.cart-row td.item-name"))
                .stream().map(e -> e.getText().trim()).toList();
    }

    public void removeItem(String productName) {
        // row-relative XPath keyed on the visible product name — peak brittleness
        By removeBtn = By.xpath("//tr[contains(@class,'cart-row')][td[@class='item-name' and normalize-space()='"
                + productName + "']]//button[@class='remove-item']");
        driver.findElement(removeBtn).click();
        // the row is removed by an XHR + DOM splice; wait for it to be gone
        wait.until(ExpectedConditions.invisibilityOfElementLocated(removeBtn));
    }

    public boolean isEmpty() {
        return !driver.findElements(emptyMessage).isEmpty()
                && driver.findElement(emptyMessage).isDisplayed();
    }

    public String subtotalText() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(subtotal));
        return driver.findElement(subtotal).getText();
    }

    public void proceedToCheckout() {
        wait.until(ExpectedConditions.elementToBeClickable(checkoutBtn));
        driver.findElement(checkoutBtn).click();
    }
}
