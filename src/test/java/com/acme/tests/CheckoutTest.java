package com.acme.tests;

import com.acme.tests.pageobjects.CheckoutPage;
import com.acme.tests.pageobjects.LoginPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

/**
 * The end-to-end checkout flow, written the brittle Selenium way:
 * hand-rolled ChromeDriver lifecycle, XPath/CSS locators, and an explicit
 * {@link WebDriverWait} before every assertion. The migration playbook
 * extracts each @Test into a declarative intent spec, then re-expresses it
 * as a Playwright test with role locators and web-first assertions (and no
 * explicit waits).
 */
public class CheckoutTest {

    private static final String BASE_URL = "https://shop.example.com";
    private WebDriver driver;

    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.get(BASE_URL + "/login");
    }

    @Test
    public void loggedInUserCanPlaceAnOrder() {
        LoginPage login = new LoginPage(driver);
        login.loginAs("a@b.com", "correct-horse");

        CheckoutPage checkout = new CheckoutPage(driver);
        checkout.openCart();
        checkout.placeOrder();

        Assert.assertEquals(checkout.confirmationText(), "Order placed");
    }

    @Test
    public void checkoutRejectsEmptyCardNumber() {
        new LoginPage(driver).loginAs("a@b.com", "correct-horse");
        CheckoutPage checkout = new CheckoutPage(driver);
        checkout.openCart();

        // leave the card number empty, then place the order
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.findElement(By.cssSelector("input[name='card-number']")).clear();
        driver.findElement(By.cssSelector("button#place-order")).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//div[@class='field-error card-number']")));
        Assert.assertTrue(checkout.cardErrorShown(), "card-number error should show");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
