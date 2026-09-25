package com.mydemoapp.automation.driver;

import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Owns the Appium session of the current thread. A {@link ThreadLocal} keeps the design ready
 * for parallel execution on several devices, while a single device simply uses one thread.
 */
public final class DriverManager {

    private static final Logger LOG = LoggerFactory.getLogger(DriverManager.class);
    private static final ThreadLocal<AndroidDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
    }

    /** Starts a session for the current thread, or returns the one already running. */
    public static AndroidDriver startDriver() {
        AndroidDriver driver = DRIVER.get();
        if (driver == null) {
            driver = DriverFactory.createAndroidDriver();
            DRIVER.set(driver);
            LOG.info("Appium session {} started", driver.getSessionId());
        }
        return driver;
    }

    public static AndroidDriver getDriver() {
        AndroidDriver driver = DRIVER.get();
        if (driver == null) {
            throw new IllegalStateException("No Appium session for thread '" + Thread.currentThread().getName()
                    + "'. Call DriverManager.startDriver() first (BaseTest does this before each test class).");
        }
        return driver;
    }

    public static boolean hasDriver() {
        return DRIVER.get() != null;
    }

    /**
     * Ends the session. A failure while quitting (typically a session that already died) is logged
     * with its stack trace rather than rethrown, so cleanup never masks the real test result.
     */
    public static void quitDriver() {
        AndroidDriver driver = DRIVER.get();
        if (driver == null) {
            return;
        }
        try {
            driver.quit();
            LOG.info("Appium session ended");
        } catch (WebDriverException e) {
            LOG.warn("Appium session could not be closed cleanly; it may have already terminated", e);
        } finally {
            DRIVER.remove();
        }
    }
}
