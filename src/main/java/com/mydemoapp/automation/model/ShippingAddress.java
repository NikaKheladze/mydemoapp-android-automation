package com.mydemoapp.automation.model;

/** Data entered on the checkout shipping form. */
public record ShippingAddress(
        String fullName,
        String addressLine1,
        String addressLine2,
        String city,
        String state,
        String zipCode,
        String country) {
}
