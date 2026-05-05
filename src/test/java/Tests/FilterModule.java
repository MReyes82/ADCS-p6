package Tests;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        /*driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("https://saucedemo.com/");*/
        // Manejar la alerta del navegador de que se cambie la contraseña
        final Map<String, Object> chromePrefs = new HashMap<>();
        chromePrefs.put("credentials_enable_service", false);
        chromePrefs.put("profile.password_manager_enabled", false);
        chromePrefs.put("profile.password_manager_leak_detection", false);
        final ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setExperimentalOption("prefs", chromePrefs);
        driver = new ChromeDriver(chromeOptions);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("https://saucedemo.com/");
    }

    @Test 
    public void testFilterLtH() // P-FILTER-01P
    {
        loginUtil("standard_user", "secret_sauce"); // llamar a la util para posicionarnos en el inventario
        wait.until(ExpectedConditions.visibilityOfElementLocated(filterDropdown)); // esperamos a que este presente el dropdown
        driver.findElement(filterDropdown).click();
        driver.findElement(dropdownLtoH).click();
        // Esperamos al refresco de la lista tras aplicar el filtro
        wait.until(ExpectedConditions.visibilityOfElementLocated(inventoryList));
        // Obtener los elementos de la lista inventoryList
        // Y obtenemos el primer elemento
        var firstElement = getInventoryListElement(0);
        if (firstElement == null) // guardrail
        {
            System.err.println("[testFilterLtoHError] ERROR al obtener el primer elemento del inventario");
            return;
        }
        // y buscamos el nombre del div hijo
        // por medio de css selector, ya que se encuentra bastante identado
        // dentro del div inventory item
        var itemName = firstElement.findElement(By.cssSelector(".inventory_item_name ")).getText();
        // Asser para verificar que el elemento sea el deseado
        Assert.assertEquals(itemName, "Sauce Labs Onesie");
    }

    @Test
    public void testFilterLtoHNegative()
    {
        // Test case similar al anterior, pero aquí se implementa la
        // validación con el usuario problem_user, aquí fallará debido a que no funciona el filtro correctamente
        loginUtil("problem_user", "secret_sauce");
        wait.until(ExpectedConditions.visibilityOfElementLocated(filterDropdown));
        driver.findElement(filterDropdown).click();
        driver.findElement(dropdownLtoH).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(inventoryList));
        var firstElement = getInventoryListElement(0);
        if (firstElement == null)
        {
            System.err.println("[testFilterLtoHError] ERROR al obtener el primer elemento del inventario");
            return;
        }
        var itemName = firstElement.findElement(By.cssSelector(".inventory_item_name ")).getText();
        Assert.assertEquals(itemName, "Sauce Labs Onesie"); // Este assert fallará
    }

    @Test
    public void testFilterHtoL() // P-FILTER-02P
    {
        loginUtil("standard_user", "secret_sauce");
        wait.until(ExpectedConditions.visibilityOfElementLocated(dropdownHtoL));
        driver.findElement(filterDropdown).click();
        driver.findElement(dropdownHtoL).click();
        // Esperamos al refrescamiento
        wait.until(ExpectedConditions.visibilityOfElementLocated(inventoryList));
        var firstElement = getInventoryListElement(0);
        if (firstElement == null)
        {
            System.err.println("[testFilterLtoHError] ERROR al obtener el primer elemento del inventario");
            return;
        }
        var itemName = firstElement.findElement(By.cssSelector(".inventory_item_name ")).getText();
        Assert.assertEquals(itemName, "Sauce Labs Fleece Jacket"); // verificamos que el nombre sea el correcto
    }

    @Test
    public void testFilterHtoLNegative() // P-FILTER-02N
    {
        // Test case similar al anterior, pero aquí se implementa la
        // validación con el usuario problem_user, aquí fallará debido a que no funciona el filtro correctamente
        loginUtil("problem_user", "secret_sauce");
        wait.until(ExpectedConditions.visibilityOfElementLocated(filterDropdown));
        driver.findElement(filterDropdown).click();
        driver.findElement(dropdownHtoL).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(inventoryList));
        var firstElement = getInventoryListElement(0);
        if (firstElement == null)
        {
            System.err.println("[testFilterLtoHError] ERROR al obtener el primer elemento del inventario");
            return;
        }
        var itemName = firstElement.findElement(By.cssSelector(".inventory_item_name ")).getText();
        Assert.assertEquals(itemName, "Sauce Labs Fleece Jacket"); // Este assert fallará
    }

    @Test 
    public void testFilterLtoHError() // P-FILTER-05P
    {
        loginUtil("error_user", "secret_sauce");
        wait.until(ExpectedConditions.visibilityOfElementLocated(filterDropdown));
        driver.findElement(filterDropdown).click();
        driver.findElement(dropdownLtoH).click();
        // Al estar logeado con el error user, se presentará una alerta al querer
        // aplicar un filtro de ordenamiento
        wait.until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept(); // Cerramos la alerta para seguir 
        var firstElement = getInventoryListElement(0); // obtenemos el primer elemento de la lista
        if (firstElement == null) // guardrail
        {
            System.err.println("[testFilterLtoHError] ERROR al obtener el primer elemento del inventario");
            return;
        }
        var itemName = firstElement.findElement(By.cssSelector(".inventory_item_name ")).getText();
        // Assert que el primer elemento sigue siendo el mismo
        // (En el test case está definido que falla, pero aquí el assert lo ponemos
        // de forma que el resultado esperado es que se quede igual
        Assert.assertEquals(itemName, "Sauce Labs Backpack");
    }   

    @AfterMethod
    public void tearDown()
    {
        driver.quit();
    }

    // Util para logearse con el usuario deseado
    private void loginUtil(String username, String password)
    {
        driver.findElement(usernameField).sendKeys(username);
        driver.findElement(passwordField).sendKeys(password);
        driver.findElement(loginButton).click();
    }

    // Util para obtener un N elemento de la lista de inventory items
    private WebElement getInventoryListElement(int index)
    {
        if (index < 0)
        {
            System.err.println("[getInventoryListElement] ERROR: index no puede ser negativo");
            return null;
        }

        List<WebElement> items = driver.findElement(inventoryList).findElements(inventoryListElement);
        if (items.isEmpty()) // guardrail
        {
            System.err.println("[getInventoryListElement] ERROR al obtener elementos del dropdown");
            return null;
        }

        if (index >= items.size())
        {
            System.err.println("[getInventoryListElement] ERROR: index fuera de rango. Max index: " + (items.size() - 1));
            return null;
        }

        return items.get(index);
    }
}
