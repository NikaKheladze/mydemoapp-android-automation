package com.mydemoapp.automation.pages;

import com.mydemoapp.automation.utils.TextParser;
import com.mydemoapp.automation.utils.UiSelectors;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.qameta.allure.Step;
import org.openqa.selenium.WebElement;

import java.math.BigDecimal;
import java.util.List;

/** Product catalog: a grid of product tiles, each with image, title and price. */
public class ProductsPage extends AppScreen {

    private static final String PRODUCT_TITLE_ID = APP_ID + "titleTV";
    private static final String PRODUCT_PRICE_ID = APP_ID + "priceTV";
    private static final String PRODUCT_IMAGE_ID = APP_ID + "productIV";

    @AndroidFindBy(id = APP_ID + "productTV")
    private WebElement screenTitle;

    @AndroidFindBy(accessibility = "Displays all products of catalog")
    private WebElement productGrid;

    @AndroidFindBy(id = PRODUCT_TITLE_ID)
    private List<WebElement> productTitles;

    @Override
    public boolean isDisplayed() {
        return isVisible(productGrid);
    }

    public String getTitle() {
        return textOf(screenTitle);
    }

    /** Names of the products currently on screen (the list is lazily rendered). */
    public List<String> getVisibleProductNames() {
        waits.until(driver -> !productTitles.isEmpty(), "the catalog to show products");
        return productTitles.stream().map(WebElement::getText).toList();
    }

    public BigDecimal getProductPrice(String productName) {
        revealProduct(productName);
        WebElement price = waits.visible(UiSelectors.siblingOf(PRODUCT_TITLE_ID, productName, PRODUCT_PRICE_ID));
        return TextParser.parsePrice(price.getText());
    }

    @Step("Open product \"{productName}\"")
    public ProductDetailsPage openProduct(String productName) {
        revealProduct(productName);
        tap(UiSelectors.siblingOf(PRODUCT_TITLE_ID, productName, PRODUCT_IMAGE_ID));
        return new ProductDetailsPage().waitForProduct(productName);
    }

    @Step("Add \"{productName}\" to the cart")
    public ProductDetailsPage addProductToCart(String productName) {
        return openProduct(productName).addToCart();
    }

    private void revealProduct(String productName) {
        waits.visible(productGrid);
        reveal(titleSelector(productName));
    }

    private static String titleSelector(String productName) {
        return UiSelectors.resourceIdAndText(PRODUCT_TITLE_ID, productName);
    }
}
