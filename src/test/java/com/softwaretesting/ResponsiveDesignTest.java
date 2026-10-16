
package com.softwaretesting;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ResponsiveDesignTest {

    @DataProvider(name = "screenSizes")
    public Object[][] screenSizes() {
        return new Object[][] {
            {"Desktop", 1366, 768},
            {"Tablet", 768, 1024},
            {"Mobile", 375, 812}
        };
    }

    @Test(dataProvider = "screenSizes")
    public void testResponsiveLayout(
            String device, int width, int height) {

        WebDriver driver = new ChromeDriver();

        try {
            // Set the exact browser viewport using Chrome DevTools
            Map<String, Object> metrics = new HashMap<>();
            metrics.put("width", width);
            metrics.put("height", height);
            metrics.put("deviceScaleFactor", 1);
            metrics.put("mobile", false);

            ((ChromeDriver) driver).executeCdpCommand(
                "Emulation.setDeviceMetricsOverride", metrics
            );

            driver.get("https://www.saucedemo.com/");

            Assert.assertTrue(
                driver.getTitle().contains("Swag Labs"),
                "Incorrect page title on " + device
            );

            WebElement username =
                driver.findElement(By.id("user-name"));
            WebElement password =
                driver.findElement(By.id("password"));
            WebElement loginButton =
                driver.findElement(By.id("login-button"));

            Assert.assertTrue(
                username.isDisplayed(),
                "Username field not visible on " + device
            );
            Assert.assertTrue(
                password.isDisplayed(),
                "Password field not visible on " + device
            );
            Assert.assertTrue(
                loginButton.isDisplayed(),
                "Login button not visible on " + device
            );

            // Check actual CSS viewport dimensions
            Map<String, Object> viewport =
                (Map<String, Object>) ((ChromeDriver) driver)
                    .executeScript(
                        "return {width: window.innerWidth, " +
                        "height: window.innerHeight};"
                    );

            int actualWidth =
                ((Number) viewport.get("width")).intValue();
            int actualHeight =
                ((Number) viewport.get("height")).intValue();

            Assert.assertEquals(
                actualWidth, width,
                "Incorrect viewport width on " + device
            );
            Assert.assertEquals(
                actualHeight, height,
                "Incorrect viewport height on " + device
            );

            System.out.println(
                "PASS: " + device + " (" +
                actualWidth + "x" + actualHeight +
                ") - Login elements visible."
            );

        } finally {
            // Reset emulation before closing the browser
            try {
                ((ChromeDriver) driver).executeCdpCommand(
                    "Emulation.clearDeviceMetricsOverride",
                    new HashMap<String, Object>()
                );
            } finally {
                driver.quit();
            }
        }
    }
}
