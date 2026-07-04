package com.acme.tests.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Order history (/account/orders) — a server-rendered table with classic
 * page-number pagination. The "next" arrow is an anchor styled as a button;
 * when disabled the anchor is still in the DOM with class 'disabled'.
 */
public class OrderHistoryPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By ordersTable = By.cssSelector("table.orders-table");
    private final By orderRows = By.cssSelector("table.orders-table tbody tr.order-row");
    private final By orderIdCells = By.cssSelector("table.orders-table tbody tr.order-row td.order-id");
    private final By nextPageLink = By.cssSelector("nav.pagination a.pagination-next");
    private final By prevPageLink = By.cssSelector("nav.pagination a.pagination-prev");
    private final By currentPageMarker = By.cssSelector("nav.pagination li.active > a");

    public OrderHistoryPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void open(String baseUrl) {
        driver.get(baseUrl + "/account/orders");
        wait.until(ExpectedConditions.presenceOfElementLocated(ordersTable));
    }

    public int rowsOnPage() {
        return driver.findElements(orderRows).size();
    }

    public List<String> orderIds() {
        return driver.findElements(orderIdCells)
                .stream().map(e -> e.getText().trim()).toList();
    }

    public String currentPageNumber() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(currentPageMarker));
        return driver.findElement(currentPageMarker).getText().trim();
    }

    public boolean hasNextPage() {
        List<?> links = driver.findElements(nextPageLink);
        if (links.isEmpty()) {
            return false;
        }
        String cls = driver.findElement(nextPageLink).getAttribute("class");
        return cls == null || !cls.contains("disabled");
    }

    public void goToNextPage() {
        wait.until(ExpectedConditions.elementToBeClickable(nextPageLink));
        driver.findElement(nextPageLink).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(ordersTable));
    }

    public void goToPreviousPage() {
        wait.until(ExpectedConditions.elementToBeClickable(prevPageLink));
        driver.findElement(prevPageLink).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(ordersTable));
    }
}
