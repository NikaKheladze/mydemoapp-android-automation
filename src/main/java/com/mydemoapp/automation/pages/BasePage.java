package com.mydemoapp.automation.pages;

import com.mydemoapp.automation.config.Config;
import com.mydemoapp.automation.driver.DriverManager;
import com.mydemoapp.automation.utils.UiSelectors;
import com.mydemoapp.automation.utils.Waits;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import java.time.Duration;
import java.util.List;

/**
 * Common behaviour of all page objects: Page Factory initialisation, explicit waits and the few
 * low-level interactions (tap, type, scroll into view) every screen needs.
 */
public abstract class BasePage {

    /** Resource-id prefix of My Demo App views. */
    protected static final String APP_ID = "com.saucelabs.mydemoapp.android:id/";
    /** Resource-id prefix of Android framework views (e.g. alert dialog buttons). */
    protected static final String ANDROID_ID = "android:id/";
    /** Short timeout for checks whose expected answer may legitimately be "no". */
    protected static final Duration QUICK_CHECK = Duration.ofSeconds(3);

    protected final AndroidDriver driver;
    protected final Waits waits;

    protected BasePage() {
        this.driver = DriverManager.getDriver();
        this.waits = new Waits(driver, Config.explicitWaitTimeout());
        // Duration.ZERO: each Page Factory lookup is a single attempt; all waiting is done by explicit waits.
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ZERO), this);
    }

    /** Waits (up to the default timeout) for the element that identifies this screen. */
    public abstract boolean isDisplayed();

    protected boolean isVisible(WebElement element) {
        return waits.isVisibleWithin(element, waits.defaultTimeout());
    }

    protected void tap(WebElement element) {
        waits.clickable(element).click();
    }

    protected void tap(By locator) {
        waits.visible(locator).click();
    }

    protected void type(WebElement element, String text) {
        WebElement field = waits.visible(element);
        field.clear();
        if (!text.isEmpty()) {
            field.sendKeys(text);
        }
    }

    protected String textOf(WebElement element) {
        return waits.visible(element).getText();
    }

    /** Immediate, non-waiting check used for elements that are removed from the UI when hidden. */
    protected boolean isPresentNow(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (NoSuchElementException | StaleElementReferenceException e) {
            return false;
        }
    }

    /** Returns the element matching the UiSelector, scrolling the first scrollable container if needed. */
    protected WebElement reveal(String targetSelector) {
        return waits.until(driver -> findRevealed(null, targetSelector), "element to be scrolled into view: " + targetSelector);
    }

    /** Returns the element matching the UiSelector, scrolling the given container if needed. */
    protected WebElement reveal(String scrollableSelector, String targetSelector) {
        return waits.until(driver -> findRevealed(scrollableSelector, targetSelector),
                "element to be scrolled into view: " + targetSelector);
    }

    protected boolean canReveal(String targetSelector, Duration timeout) {
        return waits.holdsWithin(driver -> findRevealed(null, targetSelector), timeout);
    }

    protected void hideKeyboardIfShown() {
        if (driver.isKeyboardShown()) {
            driver.hideKeyboard();
        }
    }

    /**
     * Looks for an on-screen match first, so screens whose content fits without scrolling (and thus
     * expose no scrollable container) work too; otherwise asks UiScrollable to scroll to it.
     */
    private WebElement findRevealed(String scrollableSelector, String targetSelector) {
        List<WebElement> onScreen = driver.findElements(UiSelectors.by(targetSelector));
        if (!onScreen.isEmpty() && onScreen.get(0).isDisplayed()) {
            return onScreen.get(0);
        }
        By scrolled = scrollableSelector == null
                ? UiSelectors.scrolledIntoView(targetSelector)
                : UiSelectors.scrolledIntoView(scrollableSelector, targetSelector);
        List<WebElement> found = driver.findElements(scrolled);
        return !found.isEmpty() && found.get(0).isDisplayed() ? found.get(0) : null;
    }
}
