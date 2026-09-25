package com.mydemoapp.automation.tests;

import com.mydemoapp.automation.data.Product;
import com.mydemoapp.automation.data.TestAddresses;
import com.mydemoapp.automation.data.TestUsers;
import com.mydemoapp.automation.pages.CheckoutAddressPage;
import com.mydemoapp.automation.pages.CheckoutAddressPage.Field;
import com.mydemoapp.automation.pages.CheckoutPaymentPage;
import com.mydemoapp.automation.pages.LoginPage;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.Map;

import static com.mydemoapp.automation.data.ExpectedTexts.ADDRESS_REQUIRED;
import static com.mydemoapp.automation.data.ExpectedTexts.CITY_REQUIRED;
import static com.mydemoapp.automation.data.ExpectedTexts.FULL_NAME_REQUIRED;
import static com.mydemoapp.automation.data.ExpectedTexts.PAYMENT_HEADING;
import static com.mydemoapp.automation.data.ExpectedTexts.SHIPPING_HEADING;
import static com.mydemoapp.automation.data.ExpectedTexts.ZIP_CODE_REQUIRED;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Feature("Checkout - shipping form")
public class CheckoutTest extends BaseTest {

    @Test(description = "A guest must log in before checkout, then continues to the shipping form")
    public void guestMustLogInBeforeCheckout() {
        LoginPage loginPage = catalog().addProductToCart(Product.BACKPACK.displayName())
                .openCart()
                .proceedToCheckoutAsGuest();
        assertTrue(loginPage.isDisplayed(), "Guest should be sent to the login screen");

        CheckoutAddressPage addressPage = loginPage.loginToContinueCheckout(TestUsers.STANDARD);

        assertTrue(addressPage.isDisplayed(), "Shipping form should open after logging in");
        assertEquals(addressPage.getHeading(), SHIPPING_HEADING, "Shipping form heading");
    }

    @Test(description = "Submitting an empty shipping form shows a validation error for every required field")
    public void emptyShippingFormShowsValidationErrors() {
        CheckoutAddressPage addressPage = openShippingFormAsLoggedInUser().submitExpectingValidationErrors();

        Map<Field, String> expectedMessages = Map.of(
                Field.FULL_NAME, FULL_NAME_REQUIRED,
                Field.ADDRESS_LINE_1, ADDRESS_REQUIRED,
                Field.CITY, CITY_REQUIRED,
                Field.ZIP_CODE, ZIP_CODE_REQUIRED);

        SoftAssert softly = new SoftAssert();
        expectedMessages.forEach((field, message) ->
                softly.assertEquals(addressPage.getErrorMessage(field), message, "Validation message for " + field));
        // The app's country message is truncated ("Please provide your"), so only its presence is checked.
        softly.assertTrue(addressPage.isErrorDisplayed(Field.COUNTRY), "Validation error for COUNTRY");
        softly.assertTrue(addressPage.isDisplayed(), "User should stay on the shipping form");
        softly.assertAll();
    }

    @Test(description = "A complete shipping address is accepted and the user moves on to payment")
    public void validShippingAddressContinuesToPayment() {
        CheckoutPaymentPage paymentPage = openShippingFormAsLoggedInUser()
                .enterShippingAddress(TestAddresses.VALID)
                .continueToPayment();

        assertTrue(paymentPage.isDisplayed(), "Payment step should open after a valid shipping address");
        assertEquals(paymentPage.getHeading(), PAYMENT_HEADING, "Payment step heading");
    }

    private CheckoutAddressPage openShippingFormAsLoggedInUser() {
        return loginAsStandardUser()
                .addProductToCart(Product.BACKPACK.displayName())
                .openCart()
                .proceedToCheckout();
    }
}
