package com.mydemoapp.automation.pages;

import com.mydemoapp.automation.model.ShippingAddress;
import com.mydemoapp.automation.utils.UiSelectors;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;

/**
 * Checkout step 1: shipping address form. The form is taller than the screen, so inputs and
 * validation messages are scrolled into view by resource-id before use.
 */
public class CheckoutAddressPage extends AppScreen {

    private static final String TO_PAYMENT = "Saves user info for checkout";

    /** Form fields; each maps to an input ({@code <prefix>ET}) and a validation label ({@code <prefix>ErrorTV}). */
    public enum Field {
        FULL_NAME("fullName"),
        ADDRESS_LINE_1("address1"),
        ADDRESS_LINE_2("address2"),
        CITY("city"),
        STATE("state"),
        ZIP_CODE("zip"),
        COUNTRY("country");

        private final String idPrefix;

        Field(String idPrefix) {
            this.idPrefix = idPrefix;
        }

        String inputId() {
            return APP_ID + idPrefix + "ET";
        }

        String errorId() {
            return APP_ID + idPrefix + "ErrorTV";
        }
    }

    @AndroidFindBy(id = APP_ID + "enterShippingAddressTV")
    private WebElement heading;

    @AndroidFindBy(accessibility = TO_PAYMENT)
    private WebElement toPaymentButton;

    @Override
    public boolean isDisplayed() {
        return isVisible(heading);
    }

    public String getHeading() {
        return textOf(heading);
    }

    @Step("Enter the shipping address")
    public CheckoutAddressPage enterShippingAddress(ShippingAddress address) {
        fill(Field.FULL_NAME, address.fullName());
        fill(Field.ADDRESS_LINE_1, address.addressLine1());
        fill(Field.ADDRESS_LINE_2, address.addressLine2());
        fill(Field.CITY, address.city());
        fill(Field.STATE, address.state());
        fill(Field.ZIP_CODE, address.zipCode());
        fill(Field.COUNTRY, address.country());
        return this;
    }

    @Step("Continue to payment")
    public CheckoutPaymentPage continueToPayment() {
        tapToPayment();
        return new CheckoutPaymentPage();
    }

    /** Submits a form that is expected to fail validation; the user stays on this screen. */
    @Step("Submit the shipping form expecting validation errors")
    public CheckoutAddressPage submitExpectingValidationErrors() {
        tapToPayment();
        return this;
    }

    public boolean isErrorDisplayed(Field field) {
        return canReveal(UiSelectors.resourceId(field.errorId()), QUICK_CHECK);
    }

    public String getErrorMessage(Field field) {
        return reveal(UiSelectors.resourceId(field.errorId())).getText();
    }

    private void fill(Field field, String value) {
        type(reveal(UiSelectors.resourceId(field.inputId())), value);
    }

    private void tapToPayment() {
        hideKeyboardIfShown();
        reveal(UiSelectors.description(TO_PAYMENT));
        tap(toPaymentButton);
    }
}
