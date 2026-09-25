package com.mydemoapp.automation.pages;

import io.appium.java_client.pagefactory.AndroidFindBy;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/** Confirmation dialog shown after choosing "Log Out" (a standard Android alert dialog). */
public class LogoutDialog extends BasePage {

    private static final String MESSAGE_ID = ANDROID_ID + "message";

    @AndroidFindBy(id = MESSAGE_ID)
    private WebElement message;

    @AndroidFindBy(id = ANDROID_ID + "button1")
    private WebElement confirmButton;

    @AndroidFindBy(id = ANDROID_ID + "button2")
    private WebElement cancelButton;

    @Override
    public boolean isDisplayed() {
        return isVisible(message);
    }

    public String getMessage() {
        return textOf(message);
    }

    @Step("Confirm logout")
    public LoginPage confirm() {
        tap(confirmButton);
        return new LoginPage();
    }

    /** Dismisses the dialog; the app stays on the screen from which the menu was opened. */
    @Step("Cancel logout")
    public void cancel() {
        tap(cancelButton);
        waits.invisible(By.id(MESSAGE_ID));
    }
}
