package com.acme.tests;

import com.acme.tests.pageobjects.LoginPage;
import com.acme.tests.pageobjects.OrderHistoryPage;
import com.acme.tests.support.BaseTest;
import com.acme.tests.support.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Order history pagination. The seeded test account has 23 historical
 * orders, 10 per page — those magic numbers are baked into the assertions
 * (and break every time someone reseeds the environment).
 */
public class OrderHistoryTest extends BaseTest {

    private static final int PAGE_SIZE = 10;

    @Test(groups = {"smoke", "regression"})
    public void orderHistoryShowsFirstPage() {
        new LoginPage(driver).open(baseUrl);
        new LoginPage(driver).loginAs(ConfigReader.defaultUser(), ConfigReader.defaultPassword());

        OrderHistoryPage orders = new OrderHistoryPage(driver);
        orders.open(baseUrl);

        Assert.assertEquals(orders.currentPageNumber(), "1");
        Assert.assertEquals(orders.rowsOnPage(), PAGE_SIZE, "first page should be full");
        for (String id : orders.orderIds()) {
            Assert.assertTrue(id.startsWith("ORD-"), "order id format: " + id);
        }
    }

    @Test(groups = {"regression"}, dependsOnMethods = "orderHistoryShowsFirstPage")
    public void nextPageShowsDifferentOrders() throws InterruptedException {
        new LoginPage(driver).open(baseUrl);
        new LoginPage(driver).loginAs(ConfigReader.defaultUser(), ConfigReader.defaultPassword());

        OrderHistoryPage orders = new OrderHistoryPage(driver);
        orders.open(baseUrl);
        List<String> pageOneIds = orders.orderIds();

        Assert.assertTrue(orders.hasNextPage(), "23 seeded orders => must have a page 2");
        orders.goToNextPage();
        // table swap re-renders rows in place; wait-for-table races the swap
        Thread.sleep(1000);

        Assert.assertEquals(orders.currentPageNumber(), "2");
        List<String> pageTwoIds = orders.orderIds();
        Assert.assertFalse(pageTwoIds.isEmpty(), "page 2 should have rows");
        for (String id : pageTwoIds) {
            Assert.assertFalse(pageOneIds.contains(id),
                    "order " + id + " appears on both page 1 and page 2");
        }
    }

    @Test(groups = {"regression"}, dependsOnMethods = "nextPageShowsDifferentOrders")
    public void lastPageHasRemainderAndNoNextLink() throws InterruptedException {
        new LoginPage(driver).open(baseUrl);
        new LoginPage(driver).loginAs(ConfigReader.defaultUser(), ConfigReader.defaultPassword());

        OrderHistoryPage orders = new OrderHistoryPage(driver);
        orders.open(baseUrl);
        orders.goToNextPage();
        Thread.sleep(1000);
        orders.goToNextPage();
        Thread.sleep(1000);

        Assert.assertEquals(orders.currentPageNumber(), "3");
        Assert.assertEquals(orders.rowsOnPage(), 3, "23 orders, 10/page => 3 on the last page");
        Assert.assertFalse(orders.hasNextPage(), "no next link on the last page");
    }
}
