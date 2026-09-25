package com.mydemoapp.automation.pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import org.openqa.selenium.WebElement;

/** Checkout step 2: payment method. Only used to verify that the shipping step was accepted. */
public class CheckoutPaymentPage extends AppScreen {

    @AndroidFindBy(id = APP_ID + "enterPaymentMethodTV")
    private WebElement heading;

    @Override
    public boolean isDisplayed() {
        return isVisible(heading);
    }

    public String getHeading() {
        return textOf(heading);
    }
}
