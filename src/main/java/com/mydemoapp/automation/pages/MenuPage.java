package com.mydemoapp.automation.pages;

import com.mydemoapp.automation.utils.UiSelectors;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;

/** Side navigation drawer. The last entry toggles between "Log In" and "Log Out" with the session state. */
public class MenuPage extends BasePage {

    private static final String MENU_LIST_ID = APP_ID + "menuRV";
    private static final String MENU_ITEM_ID = APP_ID + "itemTV";
    private static final String LOG_IN = "Log In";
    private static final String LOG_OUT = "Log Out";

    @AndroidFindBy(accessibility = "Recycler view for menu")
    private WebElement menuList;

    @AndroidFindBy(uiAutomator = "new UiSelector().resourceId(\"" + MENU_ITEM_ID + "\").text(\"Catalog\")")
    private WebElement catalogItem;

    @AndroidFindBy(accessibility = "Login Menu Item")
    private WebElement loginItem;

    @AndroidFindBy(accessibility = "Logout Menu Item")
    private WebElement logoutItem;

    @Override
    public boolean isDisplayed() {
        return isVisible(menuList);
    }

    @Step("Go to the product catalog via the menu")
    public ProductsPage openCatalog() {
        tap(catalogItem);
        return new ProductsPage();
    }

    @Step("Go to the login screen via the menu")
    public LoginPage openLogin() {
        revealSessionItem();
        tap(loginItem);
        return new LoginPage();
    }

    @Step("Choose 'Log Out' in the menu")
    public LogoutDialog logout() {
        revealSessionItem();
        tap(logoutItem);
        return new LogoutDialog();
    }

    public boolean isLogoutOptionAvailable() {
        return LOG_OUT.equals(revealSessionItem().getText());
    }

    public boolean isLoginOptionAvailable() {
        return LOG_IN.equals(revealSessionItem().getText());
    }

    private WebElement revealSessionItem() {
        waits.visible(menuList);
        return reveal(UiSelectors.resourceId(MENU_LIST_ID),
                UiSelectors.resourceIdAndTextMatches(MENU_ITEM_ID, LOG_IN + "|" + LOG_OUT));
    }
}
