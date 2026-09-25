package com.mydemoapp.automation.tests;

import com.mydemoapp.automation.data.Product;
import com.mydemoapp.automation.pages.CartPage;
import com.mydemoapp.automation.pages.LoginPage;
import com.mydemoapp.automation.pages.ProductDetailsPage;
import com.mydemoapp.automation.pages.ProductsPage;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static com.mydemoapp.automation.data.ExpectedTexts.CATALOG_TITLE;
import static com.mydemoapp.automation.data.ExpectedTexts.EMPTY_CART_TITLE;
import static com.mydemoapp.automation.data.ExpectedTexts.LOGIN_TITLE;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Feature("Navigation")
public class NavigationTest extends BaseTest {

    @Test(description = "The app opens on the product catalog")
    public void appOpensOnProductCatalog() {
        ProductsPage productsPage = catalog();

        assertTrue(productsPage.isDisplayed(), "Product catalog should be the start screen");
        assertEquals(productsPage.getTitle(), CATALOG_TITLE, "Catalog title");
        assertTrue(productsPage.getVisibleProductNames().contains(Product.BACKPACK.displayName()),
                "Catalog should list " + Product.BACKPACK.displayName());
    }

    @Test(description = "Opening a product shows its details with the catalog price")
    public void productTileOpensMatchingDetails() {
        ProductsPage productsPage = catalog();
        BigDecimal catalogPrice = productsPage.getProductPrice(Product.BACKPACK.displayName());

        ProductDetailsPage details = productsPage.openProduct(Product.BACKPACK.displayName());

        assertTrue(details.isDisplayed(), "Product details screen should open");
        assertEquals(details.getProductName(), Product.BACKPACK.displayName(), "Product name on details screen");
        assertEquals(details.getPrice(), catalogPrice, "Price on details screen should match the catalog");
    }

    @Test(description = "The empty cart leads back to the catalog via 'Go Shopping'")
    public void emptyCartNavigatesBackToCatalog() {
        CartPage cartPage = catalog().openCart();

        assertTrue(cartPage.isEmpty(), "Cart should be empty for a fresh app start");
        assertEquals(cartPage.getEmptyCartTitle(), EMPTY_CART_TITLE, "Empty cart title");

        ProductsPage productsPage = cartPage.goShopping();
        assertTrue(productsPage.isDisplayed(), "'Go Shopping' should return to the catalog");
    }

    @Test(description = "The side menu navigates to the login screen and back to the catalog")
    public void menuNavigatesBetweenScreens() {
        LoginPage loginPage = catalog().openMenu().openLogin();

        assertTrue(loginPage.isDisplayed(), "Login screen should open from the menu");
        assertEquals(loginPage.getTitle(), LOGIN_TITLE, "Login screen title");

        ProductsPage productsPage = loginPage.openMenu().openCatalog();
        assertTrue(productsPage.isDisplayed(), "Catalog should open from the menu");
    }
}
