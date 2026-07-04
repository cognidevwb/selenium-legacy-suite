package com.acme.tests;

import com.acme.tests.pageobjects.AccountPage;
import com.acme.tests.pageobjects.LoginPage;
import com.acme.tests.support.BaseTest;
import com.acme.tests.support.ConfigReader;
import com.acme.tests.support.RetryAnalyzer;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Profile update on /account. The save round-trips through a slow profile
 * service, so the display-name test carries the RetryAnalyzer — it has
 * been flaky since the 2023 re-platform and retry was cheaper than a fix.
 */
public class AccountProfileTest extends BaseTest {

    @Test(groups = {"regression"}, retryAnalyzer = RetryAnalyzer.class)
    public void updateDisplayNamePersists() {
        new LoginPage(driver).open(baseUrl);
        new LoginPage(driver).loginAs(ConfigReader.defaultUser(), ConfigReader.defaultPassword());

        AccountPage account = new AccountPage(driver);
        account.open(baseUrl);

        String newName = "QA Robot " + System.currentTimeMillis();
        account.updateDisplayName(newName);
        Assert.assertEquals(account.successBannerText(), "Profile updated");

        // reload and confirm it actually persisted, not just optimistic UI
        account.open(baseUrl);
        Assert.assertEquals(account.displayNameValue(), newName,
                "display name should survive a page reload");
    }

    @Test(groups = {"regression"})
    public void invalidPhoneNumberIsRejected() {
        new LoginPage(driver).open(baseUrl);
        new LoginPage(driver).loginAs(ConfigReader.defaultUser(), ConfigReader.defaultPassword());

        AccountPage account = new AccountPage(driver);
        account.open(baseUrl);
        account.updatePhone("not-a-phone");

        Assert.assertTrue(account.phoneErrorShown(), "phone validation error should show");
    }
}
