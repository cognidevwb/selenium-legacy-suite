package com.acme.tests;

import com.acme.tests.pageobjects.SearchResultsPage;
import com.acme.tests.support.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Locale;

/**
 * Search box + category filter + price sort. No login needed — search is
 * anonymous. The category filter test depends on the plain search test
 * passing first (classic ordering smell).
 */
public class SearchAndFilterTest extends BaseTest {

    @Test(groups = {"smoke", "regression"})
    public void searchReturnsMatchingProducts() {
        SearchResultsPage search = new SearchResultsPage(driver);
        search.searchFor("anvil");

        Assert.assertTrue(search.resultCardCount() > 0, "expected at least one result for 'anvil'");
        List<String> titles = search.resultTitleTexts();
        for (String title : titles) {
            Assert.assertTrue(title.toLowerCase(Locale.ROOT).contains("anvil"),
                    "every result title should mention 'anvil' but got: " + title);
        }
    }

    @Test(groups = {"regression"})
    public void gibberishSearchShowsNoResultsMessage() {
        SearchResultsPage search = new SearchResultsPage(driver);
        search.searchFor("zzqqxx-no-such-product");

        Assert.assertEquals(search.resultCardCount(), 0);
        Assert.assertTrue(search.noResultsShown(), "no-results panel should be visible");
    }

    @Test(groups = {"regression"}, dependsOnMethods = "searchReturnsMatchingProducts")
    public void categoryFilterNarrowsResults() {
        SearchResultsPage search = new SearchResultsPage(driver);
        search.searchFor("acme");
        int before = search.resultCardCount();
        Assert.assertTrue(before > 1, "need >1 result before filtering, got " + before);

        search.filterByCategory("Hardware");
        int after = search.resultCardCount();
        Assert.assertTrue(after > 0, "filter should leave at least one result");
        Assert.assertTrue(after < before,
                "Hardware filter should narrow results (" + before + " -> " + after + ")");
        Assert.assertTrue(search.resultCountText().contains(String.valueOf(after)),
                "result-count label should match the visible grid");
    }

    @Test(groups = {"regression"}, dependsOnMethods = "searchReturnsMatchingProducts")
    public void sortByPriceLowToHighOrdersTheGrid() throws InterruptedException {
        SearchResultsPage search = new SearchResultsPage(driver);
        search.searchFor("acme");
        search.sortBy("Price: Low to High");

        // grid re-sort is animated; the overlay wait misses the reshuffle
        Thread.sleep(1000);

        List<String> titles = search.resultTitleTexts();
        Assert.assertFalse(titles.isEmpty(), "sorted grid should not be empty");
        // cheapest catalog item is always the novelty sticker pack
        Assert.assertEquals(titles.get(0), "Acme Sticker Pack",
                "cheapest item should be first after low-to-high sort");
    }
}
