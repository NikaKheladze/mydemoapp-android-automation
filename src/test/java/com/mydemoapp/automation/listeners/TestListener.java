package com.mydemoapp.automation.listeners;

import com.mydemoapp.automation.driver.DriverManager;
import com.mydemoapp.automation.utils.Screenshots;
import io.appium.java_client.android.AndroidDriver;
import io.qameta.allure.Allure;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;

public class TestListener implements ITestListener, IInvokedMethodListener {

    private static final Logger LOG = LoggerFactory.getLogger(TestListener.class);

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult result) {
        if (method.isTestMethod() && result.getStatus() == ITestResult.FAILURE) {
            attachFailureEvidence();
        }
    }

    @Override
    public void onTestStart(ITestResult result) {
        LOG.info("STARTED  {}", testName(result));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        LOG.info("PASSED   {}", testName(result));
    }

    @Override
    public void onTestFailure(ITestResult result) {
        LOG.error("FAILED   {}", testName(result), result.getThrowable());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        LOG.warn("SKIPPED  {}", testName(result), result.getThrowable());
    }

    private void attachFailureEvidence() {
        if (!DriverManager.hasDriver()) {
            LOG.warn("No active Appium session, so no screenshot or page source can be attached");
            return;
        }
        AndroidDriver driver = DriverManager.getDriver();
        try {
            Allure.addAttachment("Screenshot on failure", "image/png",
                    new ByteArrayInputStream(Screenshots.capture(driver)), "png");
        } catch (WebDriverException e) {
            LOG.warn("Could not capture a screenshot for the failed test", e);
        }
        try {
            Allure.addAttachment("UI hierarchy on failure", "text/xml", driver.getPageSource(), "xml");
        } catch (WebDriverException e) {
            LOG.warn("Could not capture the page source for the failed test", e);
        }
    }

    private static String testName(ITestResult result) {
        return result.getTestClass().getRealClass().getSimpleName() + "." + result.getMethod().getMethodName();
    }
}
