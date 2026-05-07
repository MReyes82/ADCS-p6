package Tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class CartModule {
    private WebDriver driver;
    private WebDriverWait wait;

    private final By usernameField = By.id("user-name");
    private final By passwordField = By.id("password");
    private final By loginButton = By.xpath("//input[@class='submit-button btn_action']");
    private final By addToCartBtn = By.id("add-to-cart-sauce-labs-backpack");
    private final By removeCartBtn = By.id("remove-sauce-labs-backpack");
    private final By goToCartLink = By.xpath("//a[@class='shopping_cart_link']");
    private final By badgeCart = By.xpath("//span[@class='shopping_cart_badge']");

    @BeforeMethod
    public void setUp(){
        final Map<String, Object> chromePrefs = new HashMap<>();
        chromePrefs.put("credentials_enable_service", false);
        chromePrefs.put("profile.password_manager_enabled", false);
        chromePrefs.put("profile.password_manager_leak_detection", false);
        final ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setExperimentalOption("prefs", chromePrefs);
        WebDriverManager.chromedriver().setup();

        driver = new ChromeDriver(chromeOptions);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("https://www.saucedemo.com/");
    }

    @Test // P-CART-01P
    public void addItemToCart(){
        login("standard_user");
        addToCart();

        wait.until(ExpectedConditions.and(
                ExpectedConditions.textToBePresentInElementLocated(badgeCart, "1"),
                ExpectedConditions.visibilityOfElementLocated(removeCartBtn)));

        Assert.assertEquals(driver.findElement(badgeCart).getText(), "1");
        Assert.assertTrue(driver.findElement(removeCartBtn).isDisplayed());

    }

    @Test // P-CART-02P
    public void removeItemFromCart(){
        login("standard_user");
        addToCart();

        wait.until(ExpectedConditions.and(
                ExpectedConditions.textToBePresentInElementLocated(badgeCart, "1"),
                ExpectedConditions.visibilityOfElementLocated(removeCartBtn)));

        driver.findElement(removeCartBtn).click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(badgeCart));
        Assert.assertTrue(driver.findElement(addToCartBtn).isDisplayed());

    }


    @AfterMethod
    public void tearDown(){
        if (driver != null)
            driver.quit();
    }

    public void login(String username){
        driver.findElement(usernameField).sendKeys(username);
        driver.findElement(passwordField).sendKeys("secret_sauce");
        driver.findElement(loginButton).click();
    }

    public void addToCart(){
        driver.findElement(addToCartBtn).click();
    }


}
