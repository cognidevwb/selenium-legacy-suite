package com.acme.tests;

import com.acme.tests.pageobjects.CartPage;
import com.acme.tests.pageobjects.LoginPage;
import com.acme.tests.pageobjects.ProductPage;
import com.acme.tests.support.BaseTest;
import com.acme.tests.support.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Declined-card matrix, data-driven from testdata/declined-cards.csv
 * (Stripe-style test PANs). Hand-rolled CSV parsing in the DataProvider —
 * no library, split(",") and hope nobody puts a comma in the error text.
 */
public class CheckoutDeclinedCardTest extends BaseTest {

    @DataProvider(name = "declinedCards")
    public Object[][] declinedCards() throws IOException {
        List<Object[]> rows = new ArrayList<>();
        InputStream in = getClass().getResourceAsStream("/testdata/declined-cards.csv");
        Assert.assertNotNull(in, "testdata/declined-cards.csv missing from classpath");
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line = reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split(",");
                rows.add(new Object[] {parts[0].trim(), parts[1].trim(), parts[2].trim(), parts[3].trim()});
            }
        }
        return rows.toArray(new Object[0][]);
    }

    @Test(groups = {"regression"}, dataProvider = "declinedCards")
    public void declinedCardShowsGatewayError(String cardNumber, String expiry,
                                              String cvv, String expectedError) {
        new LoginPage(driver).open(baseUrl);
        new LoginPage(driver).loginAs(ConfigReader.defaultUser(), ConfigReader.defaultPassword());

        ProductPage product = new ProductPage(driver);
        product.open(baseUrl, "sku-1001");
        product.addToCart(1);

        CartPage cart = new CartPage(driver);
        cart.open(baseUrl);
        cart.proceedToCheckout();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("input[name='card-number']")));
        driver.findElement(By.cssSelector("input[name='card-number']")).sendKeys(cardNumber);
        driver.findElement(By.cssSelector("input[name='card-expiry']")).sendKeys(expiry);
        driver.findElement(By.cssSelector("input[name='card-cvv']")).sendKeys(cvv);
        driver.findElement(By.cssSelector("button#place-order")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("div.alert.alert-danger.payment-error")));
        Assert.assertEquals(
                driver.findElement(By.cssSelector("div.alert.alert-danger.payment-error")).getText(),
                expectedError,
                "gateway error for card " + cardNumber);

        // and we must still be on the checkout page, order NOT placed
        Assert.assertTrue(driver.getCurrentUrl().contains("/checkout"),
                "declined card must not leave the checkout page");
        Assert.assertTrue(driver.findElements(By.id("order-confirmation")).isEmpty(),
                "no confirmation element should exist after a decline");
    }
}
