package com.mydemoapp.automation.tests;

import com.mydemoapp.automation.data.Product;
import com.mydemoapp.automation.pages.CartPage;
import com.mydemoapp.automation.pages.ProductDetailsPage;
import com.mydemoapp.automation.pages.ProductsPage;
import io.qameta.allure.Feature;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.math.BigDecimal;

import static com.mydemoapp.automation.data.ExpectedTexts.CART_TITLE;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

@Feature("Cart")
public class CartTest extends BaseTest {

    @Test(description = "A logged-in user can add a product and find it in the cart")
    public void loggedInUserCanAddProductToCart() {
        ProductsPage productsPage = loginAsStandardUser();

        ProductDetailsPage details = productsPage.addProductToCart(Product.BACKPACK.displayName());
        assertEquals(details.getCartBadgeCount(), 1, "Cart badge after adding one product");

        CartPage cartPage = details.openCart();
        assertEquals(cartPage.getTitle(), CART_TITLE, "Cart title");
        assertTrue(cartPage.isProductDisplayed(Product.BACKPACK.displayName()),
                Product.BACKPACK.displayName() + " should be in the cart");
    }

    @Test(description = "The cart lists every added product with its price and correct totals")
    public void cartShowsItemDetailsAndTotals() {
        CartPage cartPage = addToCart(Product.BACKPACK, Product.BACKPACK_ORANGE).openCart();

        SoftAssert softly = new SoftAssert();
        for (Product product : new Product[] {Product.BACKPACK, Product.BACKPACK_ORANGE}) {
            softly.assertTrue(cartPage.isProductDisplayed(product.displayName()),
                    product.displayName() + " should be in the cart");
            softly.assertEquals(cartPage.getProductPrice(product.displayName()), product.price(),
                    "Unit price of " + product.displayName());
        }
        softly.assertEquals(cartPage.getTotalItemCount(), 2, "Total number of items");
        softly.assertEquals(cartPage.getTotalPrice(), Product.BACKPACK.price().add(Product.BACKPACK_ORANGE.price()),
                "Cart total price");
        softly.assertEquals(cartPage.getCartBadgeCount(), 2, "Cart badge");
        softly.assertAll();
    }

    @Test(description = "The quantity chosen on the product screen is carried into the cart totals")
    public void selectedQuantityIsReflectedInCart() {
        ProductDetailsPage details = catalog().openProduct(Product.BACKPACK_ORANGE.displayName())
                .increaseQuantity();
        assertEquals(details.getQuantity(), 2, "Quantity selector value");

        CartPage cartPage = details.addToCart().openCart();

        SoftAssert softly = new SoftAssert();
        softly.assertEquals(cartPage.getProductQuantity(Product.BACKPACK_ORANGE.displayName()), 2, "Quantity in cart row");
        softly.assertEquals(cartPage.getTotalItemCount(), 2, "Total number of items");
        softly.assertEquals(cartPage.getTotalPrice(), Product.BACKPACK_ORANGE.price().multiply(BigDecimal.valueOf(2)),
                "Cart total price");
        softly.assertAll();
    }

    @Test(description = "Removing the only product empties the cart")
    public void removingLastProductEmptiesCart() {
        CartPage cartPage = addToCart(Product.BACKPACK).openCart();

        cartPage.removeProduct(Product.BACKPACK.displayName());

        assertTrue(cartPage.isEmpty(), "Cart should show the empty state");
        assertEquals(cartPage.getCartBadgeCount(), 0, "Cart badge should disappear");
    }

    /** Adds each product from the catalog, returning to the catalog after every addition. */
    private ProductsPage addToCart(Product... products) {
        ProductsPage productsPage = catalog();
        for (Product product : products) {
            productsPage = productsPage.addProductToCart(product.displayName()).openMenu().openCatalog();
        }
        return productsPage;
    }
}
