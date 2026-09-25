package com.mydemoapp.automation.driver;

/** Thrown when an Appium session cannot be created. */
public class DriverInitializationException extends RuntimeException {

    public DriverInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
