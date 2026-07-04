package com.acme.tests;

import com.acme.tests.pageobjects.CartPage;
import com.acme.tests.pageobjects.CheckoutPage;
import com.acme.tests.pageobjects.LoginPage;
import com.acme.tests.pageobjects.ProductPage;
import com.acme.tests.support.BaseTest;
import com.acme.tests.support.ConfigReader;
import com.acme.tests.support.RetryAnalyzer;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * The full happy path: login → add product → cart → checkout → paid order.
 * Newer sibling of {@link CheckoutTest} (which predates BaseTest). The card
 * fields are driven with raw By locators right here in the test — the page
 * object never grew payment methods and nobody dared refactor it.
 */
public class CheckoutHappyPathTest extends BaseTest {

    @Test(groups = {"smoke", "regression"}, retryAnalyzer = RetryAnalyzer.class)
    public void fullCheckoutWithValidCardPlacesOrder() throws InterruptedException {
        new LoginPage(driver).open(baseUrl);
        new LoginPage(driver).loginAs(ConfigReader.defaultUser(), ConfigReader.defaultPassword());

        ProductPage product = new ProductPage(driver);
        product.open(baseUrl, "sku-1001");
        product.addToCart(1);

        CartPage cart = new CartPage(driver);
        cart.open(baseUrl);
        cart.proceedToCheckout();

        // payment form — locators inlined, exactly how CheckoutTest does it
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[name='card-number']")));
        driver.findElement(By.cssSelector("input[name='card-number']")).sendKeys("4111111111111111");
        driver.findElement(By.cssSelector("input[name='card-expiry']")).sendKeys("12/27");
        driver.findElement(By.cssSelector("input[name='card-cvv']")).sendKeys("123");

        // the payment iframe swap is animated; 2s was "enough" in 2022
        Thread.sleep(2000);

        CheckoutPage checkout = new CheckoutPage(driver);
        checkout.placeOrder();
        Assert.assertEquals(checkout.confirmationText(), "Order placed");

        String orderNo = driver.findElement(By.cssSelector("span.order-number")).getText();
        Assert.assertTrue(orderNo.startsWith("ORD-"),
                "order number should look like ORD-xxxxx but was " + orderNo);
    }
}
