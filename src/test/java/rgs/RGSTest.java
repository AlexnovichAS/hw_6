package rgs;

import baseTest.BaseTest;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;

@RunWith(JUnitParamsRunner.class)
public class RGSTest extends BaseTest {

    @Test
    @Parameters({"Здоровье, Иванов Иван Иванович, +79034567895, qwertyqwerty, г Москва\\, ул Россошанская\\, д 1 к 1\\, кв 1",
                    "Здоровье, Пертов Петр Петрович, +79084561615, qwertyqwerty, г Ростов-на-Дону\\, Ворошиловский пр-кт\\, д 91/2\\, кв 1",
                    "Здоровье, Сидоров Сидр Сидорович, +79044568965, qwertyqwerty, г Воронеж\\, Железнодорожный пер\\, д 8\\, кв 7"})
    public void testApplicationsForVoluntaryMedicalInsurance(String underDirectory, String userName,
                                                             String userTel, String userEmail, String userAddress) {
        // Выбор пункта меню: Компаниям
        WebElement companies = driver.findElement(By.xpath("//a[text()='Компаниям' and contains (@href,'companies')]"));
        waitUtilElementToBeClickableAndClick(companies);
        wait.until(ExpectedConditions.attributeContains(companies, "class", "active"));

        //Работа с iframe
        By xpathIframe = (By.xpath("//iframe[contains(@class,'flocktory-widget')]"));
        By closeWidgetIframe = By.xpath("//div[contains(@class,'widget__close') and contains(@class,'js-collapse-login')]");
        if (isElementExist(xpathIframe)) {
            iframeClose(xpathIframe, closeWidgetIframe);
        }

        //Выбор пункта подменю: Здоровье
        WebElement healthButton = driver.findElement(By.xpath("//span[contains(text(),'" + underDirectory + "') and contains(@class,'padding')]"));
        waitUtilElementToBeClickableAndClick(healthButton);
        wait.until(ExpectedConditions.attributeContains(healthButton.findElement(By.xpath("./..")), "class", "active"));

        //Выбор пункта меню: Добровольное медицинское страхование
        WebElement healthInsurance = driver.findElement(By.xpath("//a[text()='Добровольное медицинское страхование' and contains (@href,'dobrovolnoe-meditsinskoe-strakhovanie')]"));
        waitUtilElementToBeClickableAndClick(healthInsurance);

        //Проверка наличия заголовка
        WebElement titleHealthInsurance = driver.findElement(By.xpath("//h1[contains (@class,'title')]"));
        MatcherAssert.assertThat("Заголовок \"Добровольное медицинское страхование\"отсутствует/не соответствует требуемому",
                "Добровольное медицинское страхование",
                containsString(titleHealthInsurance.getText()));

        //Нажатие на кнопку: Отправить заявку
        WebElement submitApplication = driver.findElement(By.xpath("//span[contains(text(),'Отправить заявку')]"));
        waitUtilElementToBeClickableAndClick(submitApplication);
        scrollToElementJs(submitApplication);

        //Проверка наличия заголовка
        WebElement titleToIssuePolicy = driver.findElement(By.xpath("//div[contains(@class,'mediator-wrapper') and contains(@class,'sectionForm')]//h2[contains(@class,'section-basic')]"));
        Assert.assertEquals("Заголовок\"Оперативно перезвоним\" отсутствует/не соответствует требуемому",
                "Оперативно перезвоним\n" + "для оформления полиса", titleToIssuePolicy.getText());

        //Заполнение полей данными
        String fieldXPath = "//input[@name='%s']";
        fillInputField(driver.findElement(By.xpath(String.format(fieldXPath, "userName"))), userName, "ФИО");
        fillInputFieldPhone(driver.findElement(By.xpath(String.format(fieldXPath, "userTel"))), userTel, "Номер телефона");
        fillInputField(driver.findElement(By.xpath(String.format(fieldXPath, "userEmail"))), userEmail, "Ваша почта");
        fillInputField(driver.findElement(By.xpath("//input[@placeholder='Введите' and @class='vue-dadata__input']")), userAddress, "Ваш адрес");

        //Клик по: Checkbox
        WebElement checkboxPersonalData = driver.findElement(By.xpath("//div[@class='form__checkbox']//input"));
        elementClickJs(checkboxPersonalData);
        wait.until(ExpectedConditions.attributeContains(checkboxPersonalData, "value", "true"));

        //Работа с iframe
        if (isElementExist(xpathIframe)) {
            iframeClose(xpathIframe, closeWidgetIframe);
        }

        //Поиск сообщения: Cookie
        By closeWidgetCookie = By.xpath("//button[contains(@class,'btn--text') and contains(text(),'Хорошо')]");
        boolean iframeCookie = isElementExist(closeWidgetCookie);
        if (iframeCookie) {
            waitUtilElementToBeVisible(driver.findElement(closeWidgetCookie));
            driver.findElement(closeWidgetCookie).click();
            boolean checkFlag = wait.until(ExpectedConditions.stalenessOf(driver.findElement(closeWidgetCookie)));
            Assert.assertTrue("Сообщение о cookie не было закрыто", checkFlag);
        }

        //Нажатие по кнопке: Свяжитесь со мной
        WebElement buttonContactMe = driver.findElement(By.xpath("//button[text()='Свяжитесь со мной' and contains (@class,'form__button-submit')]"));
        scrollToElementActions(buttonContactMe);
        elementClickJs(buttonContactMe);
        wait.until(ExpectedConditions.attributeContains(buttonContactMe, "disabled", "true"));

        //Проверка сообщения об ошибке
        WebElement textYourMail = driver.findElement(By.xpath("//input[@name='userEmail']/../../span[contains(@class,'input__error')]"));
        MatcherAssert.assertThat("У поля эл.почта отсутствует сообщение об ошибке",
                "Введите корректный адрес электронной почты", equalTo(textYourMail.getText()));
    }
}
