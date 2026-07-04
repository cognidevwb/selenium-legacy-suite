package com.acme.tests.pageobjects;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Header search box + the /search results grid, including the category
 * filter sidebar and the price sort dropdown.
 */
public class SearchResultsPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By searchBox = By.cssSelector("input[name='q']");
    private final By resultsGrid = By.id("search-results");
    private final By resultCards = By.cssSelector("#search-results div.product-card");
    private final By resultTitles = By.cssSelector("#search-results div.product-card h3.card-title");
    private final By noResults = By.cssSelector("div.no-results");
    private final By sortDropdown = By.id("sort-by");
    private final By resultCount = By.cssSelector("span.result-count");

    public SearchResultsPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void searchFor(String term) {
        driver.findElement(searchBox).clear();
        driver.findElement(searchBox).sendKeys(term);
        driver.findElement(searchBox).sendKeys(Keys.ENTER);
        wait.until(ExpectedConditions.presenceOfElementLocated(resultsGrid));
    }

    public int resultCardCount() {
        return driver.findElements(resultCards).size();
    }

    public List<String> resultTitleTexts() {
        return driver.findElements(resultTitles)
                .stream().map(e -> e.getText().trim()).toList();
    }

    public boolean noResultsShown() {
        return !driver.findElements(noResults).isEmpty()
                && driver.findElement(noResults).isDisplayed();
    }

    public void filterByCategory(String category) {
        // sidebar checkboxes labelled by category name; label text drives the XPath
        By checkbox = By.xpath("//aside[@id='filters']//label[normalize-space()='"
                + category + "']/input[@type='checkbox']");
        driver.findElement(checkbox).click();
        // grid re-renders in place; wait until the spinner overlay goes away
        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.cssSelector("div.grid-loading-overlay")));
    }

    public void sortBy(String visibleText) {
        new Select(driver.findElement(sortDropdown)).selectByVisibleText(visibleText);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(
                By.cssSelector("div.grid-loading-overlay")));
    }

    public String resultCountText() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(resultCount));
        return driver.findElement(resultCount).getText();
    }
}
