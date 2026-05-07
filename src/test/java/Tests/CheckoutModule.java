package Tests;


import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class CheckoutModule {
    WebDriver driver;
    WebDriverWait wait;

    private final By usernameField = By.id("user-name");
    private final By passwordField = By.id("password");
    private final By loginButton = By.xpath("//input[@class='submit-button btn_action']");
    private final By addToCartBtn = By.id("add-to-cart-sauce-labs-backpack");
    private final By goToCartBtm = By.xpath("//a[@class='shopping_cart_link']");
    private final By checkoutBtn = By.id("checkout");
    private final By firstNameField = By.id("first-name");
    private final By lastNameField = By.id("last-name");
    private final By postalCodeField = By.id("postal-code");
    private final By continueBtn = By.id("continue");
    private final By finishBtn = By.id("finish");
    private final By orderConfirmation = By.id("checkout_complete_container");
    private final By errorMessage = By.xpath("//div[@class='error-message-container error']");



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

    @Test // P-CHOUT-01P
    public void successfullyCheckout(){
        login("standard_user");
        addToCart();

        driver.findElement(goToCartBtm).click();
        driver.findElement(checkoutBtn).click();
        driver.findElement(firstNameField).sendKeys("Juan");
        driver.findElement(lastNameField).sendKeys("Perez");
        driver.findElement(postalCodeField).sendKeys("12345");
        driver.findElement(continueBtn).click();
        driver.findElement(finishBtn).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(orderConfirmation));
        Assert.assertTrue(driver.findElement(orderConfirmation).isDisplayed());
    }

    @Test // P-CHOUT-01N
    public void emptyFieldCheckout(){
        login("standard_user");
        addToCart();

        driver.findElement(goToCartBtm).click();
        driver.findElement(checkoutBtn).click();
        driver.findElement(continueBtn).click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessage));
        Assert.assertTrue(driver.findElement(errorMessage).isDisplayed());
    }

    @Test // P-CHOUT-02P
    public void changeFieldCheckout(){
        login("problem_user");
        addToCart();

        driver.findElement(goToCartBtm).click();
        driver.findElement(checkoutBtn).click();
        driver.findElement(firstNameField).sendKeys("Juan");
        driver.findElement(lastNameField).sendKeys("Perez");
        driver.findElement(postalCodeField).sendKeys("12345");
        driver.findElement(continueBtn).click();

        Assert.assertEquals(driver.getCurrentUrl(), "https://www.saucedemo.com/checkout-step-two.html");
    }

    @Test // P-CHOUT-03P
    public void errorCheckout(){
        login("error_user");
        addToCart();

        driver.findElement(goToCartBtm).click();
        driver.findElement(checkoutBtn).click();
        driver.findElement(firstNameField).sendKeys("Juan");
        driver.findElement(lastNameField).sendKeys("Perez");
        driver.findElement(postalCodeField).sendKeys("12345");
        driver.findElement(continueBtn).click();

        Assert.assertEquals(driver.getCurrentUrl(), "https://www.saucedemo.com/checkout-step-one.html");
    }

    @Test // P-CHOUT-02N
    public void failCheckout(){
        login("error_user");
        addToCart();

        driver.findElement(goToCartBtm).click();
        driver.findElement(checkoutBtn).click();
        driver.findElement(firstNameField).sendKeys("Juan");
        driver.findElement(postalCodeField).sendKeys("12345");
        driver.findElement(continueBtn).click();

        Assert.assertTrue(driver.findElement(errorMessage).isDisplayed());
    }

    @AfterMethod
    public void tearDown()
    {
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
