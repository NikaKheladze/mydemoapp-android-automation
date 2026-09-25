package com.mydemoapp.automation.driver;

import com.mydemoapp.automation.config.Config;
import io.qameta.allure.Step;

/** Controls the application under test inside the running session. */
public final class AppManager {

    private AppManager() {
    }

    /**
     * Kills and relaunches the app. My Demo App keeps login and cart state in memory only, so a
     * restart gives every test the same starting point (product catalog, logged out, empty cart)
     * without the cost of a new Appium session.
     */
    @Step("Restart the application in a clean state")
    public static void restartApp() {
        String appPackage = Config.appPackage();
        DriverManager.getDriver().terminateApp(appPackage);
        DriverManager.getDriver().activateApp(appPackage);
    }
}
