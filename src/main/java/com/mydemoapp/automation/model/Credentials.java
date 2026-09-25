package com.mydemoapp.automation.model;

/** Login credentials. {@link #toString()} omits the password so it never reaches logs or reports. */
public record Credentials(String username, String password) {

    @Override
    public String toString() {
        return username.isEmpty() ? "<empty username>" : username;
    }
}
