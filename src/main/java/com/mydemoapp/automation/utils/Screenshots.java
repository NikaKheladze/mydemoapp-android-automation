package com.mydemoapp.automation.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

/** Captures visual evidence from the device. */
public final class Screenshots {

    private Screenshots() {
    }

    public static byte[] capture(WebDriver driver) {
        return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
    }
}
