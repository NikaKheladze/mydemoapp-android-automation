package com.mydemoapp.automation.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.function.Function;

/**
 * Reusable explicit waits. The framework configures no implicit wait, so this class is the only
 * place where the suite waits for the UI. Element-not-found errors are retried until the timeout.
 */
public final class Waits {

    private static final Duration POLLING_INTERVAL = Duration.ofMillis(250);

    private final WebDriver driver;
    private final Duration defaultTimeout;

    public Waits(WebDriver driver, Duration defaultTimeout) {
        this.driver = driver;
        this.defaultTimeout = defaultTimeout;
    }

    public Duration defaultTimeout() {
        return defaultTimeout;
    }

    public WebElement visible(WebElement element) {
        return until(ExpectedConditions.visibilityOf(element), "element to be visible: " + element);
    }

    public WebElement visible(By locator) {
        return until(ExpectedConditions.visibilityOfElementLocated(locator), "element to be visible: " + locator);
    }

    public WebElement clickable(WebElement element) {
        return until(ExpectedConditions.elementToBeClickable(element), "element to be clickable: " + element);
    }

    public void invisible(By locator) {
        until(ExpectedConditions.invisibilityOfElementLocated(locator), "element to disappear: " + locator);
    }

    public <T> T until(Function<WebDriver, T> condition, String description) {
        return newWait(defaultTimeout).withMessage("waiting for " + description).until(condition);
    }

    /** Returns whether the element becomes visible within the timeout; a timeout means {@code false}. */
    public boolean isVisibleWithin(WebElement element, Duration timeout) {
        return holdsWithin(ExpectedConditions.visibilityOf(element), timeout);
    }

    /** Returns whether the element becomes visible within the timeout; a timeout means {@code false}. */
    public boolean isVisibleWithin(By locator, Duration timeout) {
        return holdsWithin(ExpectedConditions.visibilityOfElementLocated(locator), timeout);
    }

    /** Returns whether the condition yields a non-null, non-false value within the timeout. */
    public boolean holdsWithin(Function<WebDriver, ?> condition, Duration timeout) {
        try {
            newWait(timeout).until(condition);
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    private WebDriverWait newWait(Duration timeout) {
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        wait.pollingEvery(POLLING_INTERVAL);
        // Android views are re-created during screen transitions; a stale reference is simply retried.
        wait.ignoring(StaleElementReferenceException.class);
        return wait;
    }
}
