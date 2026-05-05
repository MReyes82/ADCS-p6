package Tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

public class AuthModule
{
    WebDriver driver;
    WebDriverWait wait;
    private final By usernameField = By.id("user-name");
    private final By passwordField = By.id("password");
    private final By loginButton = By.xpath("//input[@class='submit-button btn_action']");
    private final By errorButton = By.xpath("//button[@class='error-button']");
    private final By errorH3 = By.xpath("//*[@id=\"login_button_container\"]/div/form/div[3]/h3");


    @BeforeMethod
    public void setUp()
    {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("https://www.saucedemo.com/");
    }

    @Test // P-AUTH-01P
    public void testLoginPositive()
    {
        driver.findElement(usernameField).sendKeys("standard_user");
        driver.findElement(passwordField).sendKeys("secret_sauce");
        driver.findElement(loginButton).click();
        wait.until(ExpectedConditions.urlContains("inventory"));
        Assert.assertEquals(driver.getCurrentUrl(), "https://www.saucedemo.com/inventory.html");
    }
    @Test //P-AUTH-01N
    public void testLoginNegative()
    {
        driver.findElement(usernameField).sendKeys("standard_user");
        driver.findElement(passwordField).sendKeys("secret_sauce123");
        driver.findElement(loginButton).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(errorButton));
        Assert.assertEquals(driver.getCurrentUrl(), "https://www.saucedemo.com/");
    }
    @Test //P-AUTH-02N
    public void testLoginLockedOut()
    {
        driver.findElement(usernameField).sendKeys("locked_out_user");
        driver.findElement(passwordField).sendKeys("secret_sauce");
        driver.findElement(loginButton).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(errorH3));
        Assert.assertTrue(driver.findElement(errorH3).isDisplayed()); // Revisar si aparece el header de error
    }

    @AfterMethod
    public void tearDown()
    {
        if (driver != null)
            driver.quit();
    }
}
