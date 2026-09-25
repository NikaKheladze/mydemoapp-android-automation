package com.mydemoapp.automation.pages;

import com.mydemoapp.automation.utils.TextParser;
import com.mydemoapp.automation.utils.UiSelectors;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.math.BigDecimal;
import java.util.List;

/**
 * Product details. The screen is scrollable and shares several resource-ids with the catalog and
 * cart, so it is identified by its colour picker and elements are scrolled into view before use.
 */
public class ProductDetailsPage extends AppScreen {

    private static final String PRODUCT_NAME_ID = APP_ID + "productTV";
    private static final String ADD_TO_CART = "Tap to add product to cart";
    private static final String INCREASE_QUANTITY = "Increase item quantity";
    private static final String COLOR_PICKER = "Displays available colors of selected product";

    @AndroidFindBy(accessibility = ADD_TO_CART)
    private WebElement addToCartButton;

    @AndroidFindBy(accessibility = INCREASE_QUANTITY)
    private WebElement increaseQuantityButton;

    @Override
    public boolean isDisplayed() {
        return canReveal(UiSelectors.description(COLOR_PICKER), waits.defaultTimeout());
    }

    /** Waits until the details of the given product are shown, so a wrong tap fails here with a clear message. */
    public ProductDetailsPage waitForProduct(String productName) {
        waits.until(driver -> {
            List<WebElement> titles = driver.findElements(By.id(PRODUCT_NAME_ID));
            return !titles.isEmpty() && productName.equals(titles.get(0).getText());
        }, "details of \"" + productName + "\" to be shown");
        return this;
    }

    public String getProductName() {
        return reveal(UiSelectors.resourceId(PRODUCT_NAME_ID)).getText();
    }

    public BigDecimal getPrice() {
        return TextParser.parsePrice(reveal(UiSelectors.resourceId(APP_ID + "priceTV")).getText());
    }

    public int getQuantity() {
        return TextParser.parseFirstInteger(reveal(UiSelectors.resourceId(APP_ID + "noTV")).getText());
    }

    @Step("Increase the quantity by one")
    public ProductDetailsPage increaseQuantity() {
        int expected = getQuantity() + 1;
        reveal(UiSelectors.description(INCREASE_QUANTITY));
        tap(increaseQuantityButton);
        waits.until(driver -> getQuantity() == expected, "quantity to become " + expected);
        return this;
    }

    @Step("Tap 'Add to cart'")
    public ProductDetailsPage addToCart() {
        int badgeBefore = getCartBadgeCount();
        reveal(UiSelectors.description(ADD_TO_CART));
        tap(addToCartButton);
        waits.until(driver -> getCartBadgeCount() > badgeBefore, "cart badge to increase from " + badgeBefore);
        return this;
    }
}
