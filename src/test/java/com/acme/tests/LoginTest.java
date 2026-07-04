package com.acme.tests;

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

public class LoginTest {

    private static final String BASE_URL = "https://shop.example.com";
    private WebDriver driver;

    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
    }

    @Test
    public void validUserCanLogIn() {
        LoginPage login = new LoginPage(driver);
        login.open(BASE_URL);
        login.loginAs("a@b.com", "correct-horse");
        Assert.assertTrue(driver.findElement(By.id("cart-icon")).isDisplayed());
    }

    @Test
    public void emptyPasswordIsRejected() {
        driver.get(BASE_URL + "/login");
        driver.findElement(By.id("email")).sendKeys("a@b.com");
        driver.findElement(By.xpath("//button[@class='btn-primary submit']")).click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("div.field-error.password")));
        Assert.assertEquals(
            driver.findElement(By.cssSelector("div.field-error.password")).getText(),
            "Password is required");
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
