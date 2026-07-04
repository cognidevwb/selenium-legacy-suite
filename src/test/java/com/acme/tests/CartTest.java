package com.acme.tests;

import com.acme.tests.pageobjects.CartPage;
import com.acme.tests.pageobjects.LoginPage;
import com.acme.tests.pageobjects.ProductPage;
import com.acme.tests.support.BaseTest;
import com.acme.tests.support.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Cart add/remove. Note the dependsOnMethods chain — removeItemFromCart
 * assumes addItemToCart passed first (a legacy ordering smell: each method
 * still gets a fresh driver from BaseTest, so it re-does the add anyway).
 */
public class CartTest extends BaseTest {

    private static final String SKU = "sku-1001";
    private static final String PRODUCT_NAME = "Acme Anvil (10 lb)";

    @Test(groups = {"smoke", "regression"})
    public void addItemToCart() {
        new LoginPage(driver).open(baseUrl);
        new LoginPage(driver).loginAs(ConfigReader.defaultUser(), ConfigReader.defaultPassword());

        ProductPage product = new ProductPage(driver);
        product.open(baseUrl, SKU);
        Assert.assertEquals(product.titleText(), PRODUCT_NAME);
        product.addToCart(1);
        Assert.assertEquals(product.cartBadgeCount(), "1", "cart badge after adding one item");

        CartPage cart = new CartPage(driver);
        cart.open(baseUrl);
        Assert.assertEquals(cart.rowCount(), 1);
        Assert.assertTrue(cart.itemNames().contains(PRODUCT_NAME),
                "cart should list " + PRODUCT_NAME + " but had " + cart.itemNames());
    }

    @Test(groups = {"regression"}, dependsOnMethods = "addItemToCart")
    public void removeItemFromCart() {
        new LoginPage(driver).open(baseUrl);
        new LoginPage(driver).loginAs(ConfigReader.defaultUser(), ConfigReader.defaultPassword());

        ProductPage product = new ProductPage(driver);
        product.open(baseUrl, SKU);
        product.addToCart(1);

        CartPage cart = new CartPage(driver);
        cart.open(baseUrl);
        Assert.assertEquals(cart.rowCount(), 1, "precondition: one row in cart");

        cart.removeItem(PRODUCT_NAME);
        Assert.assertEquals(cart.rowCount(), 0, "row should be gone after remove");
        Assert.assertTrue(cart.isEmpty(), "empty-cart message should be visible");
    }

    @Test(groups = {"regression"}, dependsOnMethods = "addItemToCart")
    public void subtotalReflectsQuantity() throws InterruptedException {
        new LoginPage(driver).open(baseUrl);
        new LoginPage(driver).loginAs(ConfigReader.defaultUser(), ConfigReader.defaultPassword());

        ProductPage product = new ProductPage(driver);
        product.open(baseUrl, SKU);
        product.addToCart(2);

        CartPage cart = new CartPage(driver);
        cart.open(baseUrl);
        // subtotal is recalculated by an async price service; the overlay has
        // no reliable spinner here, so the original author just slept.
        Thread.sleep(1500);
        Assert.assertEquals(cart.subtotalText(), "$39.98",
                "2 x $19.99 anvils");
    }
}
