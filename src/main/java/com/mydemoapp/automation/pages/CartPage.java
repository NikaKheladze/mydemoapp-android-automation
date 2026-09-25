package com.mydemoapp.automation.pages;

import com.mydemoapp.automation.utils.TextParser;
import com.mydemoapp.automation.utils.UiSelectors;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.math.BigDecimal;

/** Cart screen. Shows either the list of cart rows with totals, or an empty-cart placeholder. */
public class CartPage extends AppScreen {

    private static final String ITEM_TITLE_ID = APP_ID + "titleTV";
    private static final String ITEM_PRICE_ID = APP_ID + "priceTV";
    private static final String ITEM_QUANTITY_ID = APP_ID + "noTV";
    private static final String REMOVE_BUTTON_ID = APP_ID + "removeBt";

    @AndroidFindBy(accessibility = "Displays list of selected products")
    private WebElement itemList;

    @AndroidFindBy(id = APP_ID + "productTV")
    private WebElement screenTitle;

    @AndroidFindBy(id = APP_ID + "noItemTitleTV")
    private WebElement emptyCartTitle;

    @AndroidFindBy(id = APP_ID + "shoppingBt")
    private WebElement goShoppingButton;

    @AndroidFindBy(id = APP_ID + "itemsTV")
    private WebElement totalItems;

    @AndroidFindBy(id = APP_ID + "totalPriceTV")
    private WebElement totalPrice;

    @AndroidFindBy(accessibility = "Confirms products for checkout")
    private WebElement proceedToCheckoutButton;

    @Override
    public boolean isDisplayed() {
        return waits.holdsWithin(ExpectedConditions.or(
                ExpectedConditions.visibilityOf(itemList),
                ExpectedConditions.visibilityOf(emptyCartTitle)), waits.defaultTimeout());
    }

    public String getTitle() {
        return textOf(screenTitle);
    }

    public boolean isEmpty() {
        waitUntilLoaded();
        return isPresentNow(emptyCartTitle);
    }

    public String getEmptyCartTitle() {
        return textOf(emptyCartTitle);
    }

    public boolean isProductDisplayed(String productName) {
        waitUntilLoaded();
        return !isPresentNow(emptyCartTitle) && canReveal(titleSelector(productName), QUICK_CHECK);
    }

    public BigDecimal getProductPrice(String productName) {
        reveal(titleSelector(productName));
        WebElement price = waits.visible(UiSelectors.siblingOf(ITEM_TITLE_ID, productName, ITEM_PRICE_ID));
        return TextParser.parsePrice(price.getText());
    }

    public int getProductQuantity(String productName) {
        reveal(titleSelector(productName));
        return TextParser.parseFirstInteger(waits.visible(rowElement(productName, ITEM_QUANTITY_ID)).getText());
    }

    @Step("Remove \"{productName}\" from the cart")
    public CartPage removeProduct(String productName) {
        reveal(titleSelector(productName));
        tap(rowElement(productName, REMOVE_BUTTON_ID));
        waits.invisible(UiSelectors.by(titleSelector(productName)));
        return this;
    }

    public int getTotalItemCount() {
        return TextParser.parseFirstInteger(textOf(totalItems));
    }

    public BigDecimal getTotalPrice() {
        return TextParser.parsePrice(textOf(totalPrice));
    }

    @Step("Tap 'Go Shopping'")
    public ProductsPage goShopping() {
        tap(goShoppingButton);
        return new ProductsPage();
    }

    /** Proceeds as a logged-in user, which leads to the shipping address form. */
    @Step("Proceed to checkout")
    public CheckoutAddressPage proceedToCheckout() {
        tap(proceedToCheckoutButton);
        return new CheckoutAddressPage();
    }

    /** Proceeds without a session, which makes the app ask the user to log in first. */
    @Step("Proceed to checkout as a guest")
    public LoginPage proceedToCheckoutAsGuest() {
        tap(proceedToCheckoutButton);
        return new LoginPage();
    }

    private void waitUntilLoaded() {
        if (!isDisplayed()) {
            throw new IllegalStateException("Cart screen did not load within " + waits.defaultTimeout());
        }
    }

    private static String titleSelector(String productName) {
        return UiSelectors.resourceIdAndText(ITEM_TITLE_ID, productName);
    }

    /**
     * Element of the cart row that contains the given product title. The title and the row's quantity /
     * remove controls live in different sub-layouts, which UiSelector cannot relate, so a relative
     * (never absolute) XPath is used: go up to the nearest ancestor that also holds the target control.
     */
    private static By rowElement(String productName, String elementId) {
        if (productName.contains("'")) {
            throw new IllegalArgumentException("Product names containing apostrophes are not supported: " + productName);
        }
        return By.xpath("//*[@resource-id='" + ITEM_TITLE_ID + "' and @text='" + productName + "']"
                + "/ancestor::*[.//*[@resource-id='" + elementId + "']][1]"
                + "//*[@resource-id='" + elementId + "']");
    }
}
