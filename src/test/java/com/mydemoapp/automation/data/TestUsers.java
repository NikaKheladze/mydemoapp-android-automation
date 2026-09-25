package com.mydemoapp.automation.data;

import com.mydemoapp.automation.model.Credentials;

public final class TestUsers {

    private static final String VALID_PASSWORD = "10203040";

    public static final Credentials STANDARD = new Credentials("bod@example.com", VALID_PASSWORD);
    public static final Credentials LOCKED_OUT = new Credentials("alice@example.com", VALID_PASSWORD);
    public static final Credentials MISSING_USERNAME = new Credentials("", VALID_PASSWORD);
    public static final Credentials MISSING_PASSWORD = new Credentials(STANDARD.username(), "");

    private TestUsers() {
    }
}
