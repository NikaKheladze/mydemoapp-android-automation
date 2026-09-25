package com.mydemoapp.automation.utils;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/**
 * Builds Android UiAutomator locators for cases Page Factory annotations cannot express:
 * list items identified by their text, siblings inside a list row, and elements that must first
 * be scrolled into view.
 */
public final class UiSelectors {

    private static final String FIRST_SCROLLABLE = "new UiSelector().scrollable(true)";
    /** Caps each scroll search (UiScrollable's default is 30 swipes), keeping lookups short and the device stable. */
    private static final int MAX_SEARCH_SWIPES = 8;

    private UiSelectors() {
    }

    public static String resourceId(String id) {
        return "new UiSelector().resourceId(" + quote(id) + ")";
    }

    public static String resourceIdAndText(String id, String text) {
        return resourceId(id) + ".text(" + quote(text) + ")";
    }

    public static String resourceIdAndTextMatches(String id, String regex) {
        return resourceId(id) + ".textMatches(" + quote(regex) + ")";
    }

    public static String description(String contentDescription) {
        return "new UiSelector().description(" + quote(contentDescription) + ")";
    }

    public static By by(String selector) {
        return AppiumBy.androidUIAutomator(selector);
    }

    /**
     * Element with resource-id {@code siblingId} in the same list row (same parent) as the element with
     * resource-id {@code anchorId} and text {@code anchorText}. A relative XPath is used because
     * UiSelector.fromParent() does not reliably stay inside the matched row of a list.
     */
    public static By siblingOf(String anchorId, String anchorText, String siblingId) {
        if (anchorText.contains("'")) {
            throw new IllegalArgumentException("Texts containing apostrophes are not supported: " + anchorText);
        }
        return By.xpath("//*[@resource-id='" + anchorId + "' and @text='" + anchorText + "']"
                + "/../*[@resource-id='" + siblingId + "']");
    }

    /** Scrolls the first scrollable container on screen until {@code target} is visible. */
    public static By scrolledIntoView(String target) {
        return scrolledIntoView(FIRST_SCROLLABLE, target);
    }

    /** Scrolls the container matching {@code scrollable} until {@code target} is visible. */
    public static By scrolledIntoView(String scrollable, String target) {
        return AppiumBy.androidUIAutomator("new UiScrollable(" + scrollable + ").setMaxSearchSwipes("
                + MAX_SEARCH_SWIPES + ").scrollIntoView(" + target + ")");
    }

    private static String quote(String value) {
        return '"' + value.replace("\\", "\\\\").replace("\"", "\\\"") + '"';
    }
}
