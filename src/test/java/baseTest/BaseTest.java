package baseTest;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.concurrent.TimeUnit;
import static cofig.Config.*;
import static org.hamcrest.CoreMatchers.*;

public class BaseTest {

    /**
     * Драйвер браузера
     *
     * @author Алехнович Александр
     */
    protected WebDriver driver;

    /**
     * Явное ожидание
     *
     * @author Алехнович Александр
     */
    protected WebDriverWait wait;

    /**
     * Перед каждым тестом открывает браузер на весь экран и устанавливает ожидания
     *
     * @author Алехнович Александр
     */
    @Before
    public void before() {
        buildDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(IMPLICITLY_WAITE, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(IMPLICITLY_WAITE, TimeUnit.SECONDS);
        wait = new WebDriverWait(driver, EXPLICITLY_WAITE_TIME_OUT, EXPLICITLY_WAITE_SLEEP);
        driver.get("http://www.rgs.ru");
    }

    /**
     * Выбор драйвера
     *
     * @author Алехнович Александр
     */
    private void buildDriver() {
        switch (BROWSER) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                driver = new ChromeDriver();
                break;
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                driver = new FirefoxDriver();
                break;
            default:
                System.out.println("Не корректный браузер: " + BROWSER);
        }
    }

    /**
     * После каждого теста закрывает браузер
     *
     * @author Алехнович Александр
     */
    @After
    public void closeDriver() {
        driver.quit();
    }

    /**
     * Скрол до элемента на js коде
     *
     * @param element - веб элемент до которого нужно проскролить
     * @author Алехнович Александр
     */
    public void scrollToElementJs(WebElement element) {
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
        javascriptExecutor.executeScript("arguments[0].scrollIntoView(true);", element);
    }

    /**
     * Скрол до элемента через Actions
     *
     * @param element - веб элемент до которого нужно проскролить
     * @author Алехнович Александр
     */
    public void scrollToElementActions(WebElement element) {
        Actions actions = new Actions(driver);
        actions.moveToElement(element).build().perform();
    }

    /**
     * Клик по элементу на js коде
     *
     * @param element - веб элемент на который нужно кликнуть
     * @author Алехнович Александр
     */
    public void elementClickJs(WebElement element) {
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
        javascriptExecutor.executeScript("arguments[0].click();", element);
    }

    /**
     * Проверяет наличие элемента на странице
     *
     * @param element - веб элемент который нужно найти
     * @author Алехнович Александр
     */
    public boolean isElementExist(By element) {
        try {
            driver.manage().timeouts().pageLoadTimeout(1, TimeUnit.SECONDS);
            driver.findElement(element);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        } finally {
            driver.manage().timeouts().pageLoadTimeout(IMPLICITLY_WAITE, TimeUnit.SECONDS);
        }
    }

    /**
     * Проверяет наличие элемента на странице
     *
     * @param iframeXpath      - веб элемент который нужно переключиться
     * @param iframeCloseXpath - веб элемент на который нужно кликнуть
     * @author Алехнович Александр
     */
    public void iframeClose(By iframeXpath, By iframeCloseXpath) {
        try {
            driver.manage().timeouts().pageLoadTimeout(1, TimeUnit.SECONDS);
            driver.switchTo().frame(driver.findElement(iframeXpath)).findElement(iframeCloseXpath).click();
        } catch (Exception ignore) {
        } finally {
            driver.manage().timeouts().pageLoadTimeout(IMPLICITLY_WAITE, TimeUnit.SECONDS);
            driver.switchTo().parentFrame();
        }
    }

    /**
     * Явное ожидание того что элемент станет видимым
     *
     * @param element - веб элемент который мы ожидаем что будет виден на странице
     * @author Алехнович Александр
     */
    public void waitUtilElementToBeVisible(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    /**
     * Явное ожидание того что элемент станет кликабельный
     *
     * @param element - веб элемент до которого нужно проскролить
     * @author Алехнович Александр
     */
    public void waitUtilElementToBeClickable(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    /**
     * Ожидает что элемент станет кликабельный и производит клик
     *
     * @param element - веб элемент до которого нужно проскролить
     * @author Алехнович Александр
     */
    public void waitUtilElementToBeClickableAndClick(WebElement element) {
        waitUtilElementToBeClickable(element);
        element.click();
    }

    /**
     * Заполнение полей определённым значений
     *
     * @param element   - веб элемент (поле какое-то) которое планируем заполнить
     * @param value     - значение которым мы заполняем веб элемент (поле какое-то)
     * @param nameField - название поля для заполнения
     * @author Алехнович Александр
     */
    public void fillInputField(WebElement element, String value, String nameField) {
        scrollToElementJs(element);
        waitUtilElementToBeClickable(element);
        element.sendKeys(value);
        element.sendKeys(Keys.TAB);
        MatcherAssert.assertThat("Поле: " + nameField + ", было заполнено некорректно", value,
                containsString(element.getAttribute("value")));
    }

    /**
     * Заполнение поля телефона определённым значений
     *
     * @param element   - веб элемент (поле какое-то) которое планируем заполнить
     * @param value     - значение которы мы заполняем веб элемент (поле какое-то)
     * @param nameField - название поля для заполнения
     * @author Алехнович Александр
     */
    public void fillInputFieldPhone(WebElement element, String value, String nameField) {
        scrollToElementJs(element);
        waitUtilElementToBeClickable(element);
        element.sendKeys(value.replaceFirst("\\+7", ""));
        MatcherAssert.assertThat("Поле: " + nameField + ", было заполнено некорректно", value.replaceAll("\\D", ""),
                containsString(element.getAttribute("value").replaceAll("\\D", "")));
    }
}
