package com.mydemoapp.automation.tests;

import com.mydemoapp.automation.data.TestUsers;
import com.mydemoapp.automation.pages.LoginPage;
import com.mydemoapp.automation.pages.ProductsPage;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;

import static com.mydemoapp.automation.data.ExpectedTexts.PASSWORD_REQUIRED;
import static com.mydemoapp.automation.data.ExpectedTexts.USERNAME_REQUIRED;
import static com.mydemoapp.automation.data.ExpectedTexts.USER_LOCKED_OUT;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Feature("Login")
public class LoginTest extends BaseTest {

    @Test(description = "A valid user can log in and lands on the product catalog")
    public void validUserCanLogIn() {
        ProductsPage productsPage = openLoginScreen().login(TestUsers.STANDARD);

        assertTrue(productsPage.isDisplayed(), "Product catalog should be shown after a successful login");
        assertTrue(productsPage.openMenu().isLogoutOptionAvailable(),
                "Menu should offer 'Log Out' once the user is logged in");
    }

    @Test(description = "A locked-out user is rejected with an explanatory error")
    public void lockedOutUserCannotLogIn() {
        LoginPage loginPage = openLoginScreen().attemptLogin(TestUsers.LOCKED_OUT);

        assertEquals(loginPage.getPasswordErrorMessage(), USER_LOCKED_OUT, "Locked-out error message");
        assertTrue(loginPage.isDisplayed(), "User should stay on the login screen");
    }

    @Test(description = "Login requires a username")
    public void loginWithoutUsernameShowsValidationError() {
        LoginPage loginPage = openLoginScreen().attemptLogin(TestUsers.MISSING_USERNAME);

        assertEquals(loginPage.getUsernameErrorMessage(), USERNAME_REQUIRED, "Username validation message");
        assertTrue(loginPage.isDisplayed(), "User should stay on the login screen");
    }

    @Test(description = "Login requires a password")
    public void loginWithoutPasswordShowsValidationError() {
        LoginPage loginPage = openLoginScreen().attemptLogin(TestUsers.MISSING_PASSWORD);

        assertEquals(loginPage.getPasswordErrorMessage(), PASSWORD_REQUIRED, "Password validation message");
        assertTrue(loginPage.isDisplayed(), "User should stay on the login screen");
    }
}
