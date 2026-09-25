package com.mydemoapp.automation.tests;

import com.mydemoapp.automation.pages.LoginPage;
import com.mydemoapp.automation.pages.LogoutDialog;
import com.mydemoapp.automation.pages.ProductsPage;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;

import static com.mydemoapp.automation.data.ExpectedTexts.LOGOUT_CONFIRMATION;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Feature("Logout")
public class LogoutTest extends BaseTest {

    @Test(description = "A logged-in user can log out and ends up logged out on the login screen")
    public void userCanLogOut() {
        LogoutDialog dialog = loginAsStandardUser().openMenu().logout();
        assertEquals(dialog.getMessage(), LOGOUT_CONFIRMATION, "Logout confirmation message");

        LoginPage loginPage = dialog.confirm();

        assertTrue(loginPage.isDisplayed(), "Login screen should be shown after logout");
        assertTrue(loginPage.openMenu().isLoginOptionAvailable(),
                "Menu should offer 'Log In' again once the user is logged out");
    }

    @Test(description = "Cancelling the logout confirmation keeps the user logged in")
    public void cancellingLogoutKeepsUserLoggedIn() {
        ProductsPage productsPage = loginAsStandardUser();

        productsPage.openMenu().logout().cancel();

        assertTrue(productsPage.isDisplayed(), "User should remain on the catalog");
        assertTrue(productsPage.openMenu().isLogoutOptionAvailable(), "User should still be logged in");
    }
}
