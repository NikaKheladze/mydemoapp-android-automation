package com.mydemoapp.automation.data;

import java.math.BigDecimal;
public enum Product {

    BACKPACK("Sauce Labs Backpack", "29.99"),
    BACKPACK_ORANGE("Sauce Labs Backpack (orange)", "29.99");

    private final String displayName;
    private final BigDecimal price;

    Product(String displayName, String price) {
        this.displayName = displayName;
        this.price = new BigDecimal(price);
    }

    public String displayName() {
        return displayName;
    }

    public BigDecimal price() {
        return price;
    }
}
