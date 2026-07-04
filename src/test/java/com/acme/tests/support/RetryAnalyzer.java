package com.acme.tests.support;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Retries a failed test exactly once. Added in 2022 to paper over the
 * flaky checkout iframe; now applied to anything that fails on Tuesdays.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final int MAX_RETRIES = 1;

    private int attempt = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (attempt < MAX_RETRIES) {
            attempt++;
            System.out.println("[RetryAnalyzer] retrying "
                    + result.getTestClass().getRealClass().getSimpleName()
                    + "." + result.getMethod().getMethodName()
                    + " (attempt " + (attempt + 1) + ")");
            return true;
        }
        return false;
    }
}
