package Tests;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;

public class FilterModule
{
    private WebDriver driver;
    private WebDriverWait wait;
    private final By usernameField = By.id("user-name");
    private final By passwordField = By.id("password");
    private final By loginButton = By.xpath("//input[@class='submit-button btn_action']");
    private final By filterDropdown = By.xpath("//*[@id=\"header_container\"]/div[2]/div/span/select");
    // Definir el locator para las opciones del dropdown
    private final By dropdownAtoZ = By.xpath("//*[@id=\"header_container\"]/div[2]/div/span/select/option[1]");
    private final By dropdownZtoA = By.xpath("//*[@id=\"header_container\"]/div[2]/div/span/select/option[2]");
    private final By dropdownLtoH = By.xpath("//*[@id=\"header_container\"]/div[2]/div/span/select/option[3]");
    private final By dropdownHtoL = By.xpath("//*[@id=\"header_container\"]/div[2]/div/span/select/option[4]");
    private final By inventoryList = By.xpath("//div[@class='inventory_list']");
    private final By inventoryListElement = By.cssSelector(".inventory_item");


    @BeforeMethod
    public void setUp()
    {
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("https://saucedemo.com/");
    }

    @Test
    public void testFilterLtH()
    {
        loginUtil(); // llamar a la util para posicionarnos en el inventario
        wait.until(ExpectedConditions.visibilityOfElementLocated(dropdownLtoH)); // esperamos a que este presente el dropdown
        driver.findElement(filterDropdown).click();
        driver.findElement(dropdownLtoH).click();
        // Esperamos al refresco de la lista tras aplicar el filtro
        wait.until(ExpectedConditions.visibilityOfElementLocated(inventoryList));
        // Obtener los elementos de la lista inventoryList
        List<WebElement> items = driver.findElement(inventoryList).findElements(inventoryListElement);
        if (items.isEmpty()) // guardrail
        {
            System.err.println("[testFilderLtH] ERROR al obtener elementos del dropdown");
            return;
        }
        // Ahora obtenemos el primer elemento y buscamos el nombre del div hijo
        // por medio de css selector ya que se encuentra bastante identado
        // dentro del div inventory item
        var firstElement = items.getFirst();
        var itemName = firstElement.findElement(By.cssSelector(".inventory_item_name ")).getText();
        // Asser para verificar que el elemento sea el deseado
        Assert.assertEquals(itemName, "Sauce Labs Onesie");
    }

    @AfterMethod
    public void tearDown()
    {
        driver.quit();
    }

    private void loginUtil()
    {
        driver.findElement(usernameField).sendKeys("standard_user");
        driver.findElement(passwordField).sendKeys("secret_sauce");
        driver.findElement(loginButton).click();
    }
}
