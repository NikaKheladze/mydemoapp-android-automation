package com.mydemoapp.automation.pages;

import com.mydemoapp.automation.model.Credentials;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;

public class LoginPage extends AppScreen {

    @AndroidFindBy(id = APP_ID + "loginTV")
    private WebElement title;

    @AndroidFindBy(id = APP_ID + "nameET")
    private WebElement usernameField;

    @AndroidFindBy(id = APP_ID + "passwordET")
    private WebElement passwordField;

    @AndroidFindBy(accessibility = "Tap to login with given credentials")
    private WebElement loginButton;

    @AndroidFindBy(id = APP_ID + "nameErrorTV")
    private WebElement usernameError;

    @AndroidFindBy(id = APP_ID + "passwordErrorTV")
    private WebElement passwordError;

    @Override
    public boolean isDisplayed() {
        return isVisible(loginButton);
    }

    public String getTitle() {
        return textOf(title);
    }

    /** Logs in from the menu entry point; the app then shows the product catalog. */
    @Step("Log in as {credentials}")
    public ProductsPage login(Credentials credentials) {
        submit(credentials);
        return new ProductsPage();
    }

    /** Logs in after being redirected from the cart; the app then continues to the shipping form. */
    @Step("Log in as {credentials} to continue the checkout")
    public CheckoutAddressPage loginToContinueCheckout(Credentials credentials) {
        submit(credentials);
        return new CheckoutAddressPage();
    }

    /** Submits credentials that are expected to be rejected; the user stays on this screen. */
    @Step("Attempt to log in as {credentials}")
    public LoginPage attemptLogin(Credentials credentials) {
        submit(credentials);
        return this;
    }

    public String getUsernameErrorMessage() {
        return textOf(usernameError);
    }

    public String getPasswordErrorMessage() {
        return textOf(passwordError);
    }

    private void submit(Credentials credentials) {
        type(usernameField, credentials.username());
        type(passwordField, credentials.password());
        hideKeyboardIfShown();
        tap(loginButton);
    }
}
