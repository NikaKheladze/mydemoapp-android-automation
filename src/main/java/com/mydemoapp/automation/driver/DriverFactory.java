package com.mydemoapp.automation.driver;

import com.mydemoapp.automation.config.Config;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

/** Builds capabilities from {@link Config} and opens Appium sessions. Tests use {@link DriverManager}. */
final class DriverFactory {

    private static final Logger LOG = LoggerFactory.getLogger(DriverFactory.class);

    private DriverFactory() {
    }

    static AndroidDriver createAndroidDriver() {
        URL serverUrl = Config.appiumServerUrl();
        UiAutomator2Options options = buildOptions();
        LOG.info("Starting Appium session on {} with capabilities {}", serverUrl, options.asMap());
        try {
            return new AndroidDriver(serverUrl, options);
        } catch (WebDriverException e) {
            throw new DriverInitializationException("Could not start an Appium session on " + serverUrl + ". Check that:"
                    + "\n  - the Appium server is running (`appium`) and reachable at that URL"
                    + "\n  - the UiAutomator2 driver is installed (`appium driver list --installed`)"
                    + "\n  - an emulator/device is online (`adb devices`) and ANDROID_HOME is set for the Appium server"
                    + "\nCause: " + firstLine(e.getMessage()), e);
        }
    }

    static UiAutomator2Options buildOptions() {
        UiAutomator2Options options = new UiAutomator2Options()
                .setPlatformName(Config.platformName())
                .setAutomationName(Config.automationName())
                .setDeviceName(Config.deviceName())
                .setAppPackage(Config.appPackage())
                .setAppActivity(Config.appActivity())
                .setAppWaitActivity(Config.appWaitActivity())
                .setAutoGrantPermissions(true)
                .setDisableWindowAnimation(true)
                .setNewCommandTimeout(Config.newCommandTimeout())
                .setUiautomator2ServerInstallTimeout(Config.serverInstallTimeout());
        Config.platformVersion().ifPresent(options::setPlatformVersion);
        Config.udid().ifPresent(options::setUdid);
        Config.appPath().ifPresent(apk -> options.setApp(apk.toString()));
        return options;
    }

    private static String firstLine(String message) {
        if (message == null) {
            return "<no message>";
        }
        int newLine = message.indexOf('\n');
        return newLine < 0 ? message : message.substring(0, newLine);
    }
}
