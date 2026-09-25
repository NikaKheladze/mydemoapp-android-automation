package com.mydemoapp.automation.tests;

import com.mydemoapp.automation.data.TestUsers;
import com.mydemoapp.automation.driver.AppManager;
import com.mydemoapp.automation.driver.DriverManager;
import com.mydemoapp.automation.listeners.TestListener;
import com.mydemoapp.automation.pages.LoginPage;
import com.mydemoapp.automation.pages.ProductsPage;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

@Listeners(TestListener.class)
public abstract class BaseTest {

    @BeforeClass(alwaysRun = true)
    public void startSession() {
        DriverManager.startDriver();
    }

    @BeforeMethod(alwaysRun = true)
    public void startAppInCleanState() {
        if (!DriverManager.hasDriver()) {
            throw new SkipException("No Appium session: it failed to start in @BeforeClass (see that error for the cause)");
        }
        AppManager.restartApp();
    }

    @AfterClass(alwaysRun = true)
    public void endSession() {
        DriverManager.quitDriver();
    }

    protected ProductsPage catalog() {
        return new ProductsPage();
    }

    protected LoginPage openLoginScreen() {
        return catalog().openMenu().openLogin();
    }

    protected ProductsPage loginAsStandardUser() {
        return openLoginScreen().login(TestUsers.STANDARD);
    }
}
