package com.mydemoapp.automation.pages;

import com.mydemoapp.automation.utils.TextParser;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;

/** A screen hosted in the app's main activity, which always shows the header with menu and cart. */
public abstract class AppScreen extends BasePage {

    @AndroidFindBy(accessibility = "View menu")
    private WebElement menuButton;

    @AndroidFindBy(accessibility = "View cart")
    private WebElement cartButton;

    @AndroidFindBy(id = APP_ID + "cartTV")
    private WebElement cartBadge;

    @Step("Open the side menu")
    public MenuPage openMenu() {
        tap(menuButton);
        return new MenuPage();
    }

    @Step("Open the cart")
    public CartPage openCart() {
        tap(cartButton);
        return new CartPage();
    }

    /** Number on the cart badge. The app hides the badge when the cart is empty, which is reported as 0. */
    public int getCartBadgeCount() {
        return isPresentNow(cartBadge) ? TextParser.parseFirstInteger(cartBadge.getText()) : 0;
    }
}
