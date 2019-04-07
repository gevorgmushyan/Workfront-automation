package com.lazerycode.selenium.tests;

import com.lazerycode.selenium.DriverBase;
import com.lazerycode.selenium.entities.UserAccount;
import com.lazerycode.selenium.page_objects.HomePageObject;
import com.lazerycode.selenium.page_objects.LogInPageObject;
import org.assertj.core.api.SoftAssertions;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class LogInTest extends DriverBase {
    protected LogInPageObject loginPage;
    protected HomePageObject homePage;
    protected WebDriver driver;
    protected SoftAssertions softAssertions;

    protected static final String ALERT_ERROR_MSG = "Login failed! please fix the following errors:";
    protected static final String ALERT_ERROR_EMAIL_PASS_MSG = "Login Error: invalid email/password";
    protected static final String INVALID_EMAIL_MSG = "Email has an invalid format";
    protected static final String INVALID_PASSWORD_MSG = "Password must have at least 6 characters";

    @BeforeClass
    public void beforeClass() throws Exception {
        loginPage = new LogInPageObject();
        homePage = new HomePageObject();
        driver = getDriver();
        driver.manage().window().maximize();
    }

    @BeforeMethod
    public void beforeMethod() {
        softAssertions = new SoftAssertions();
        driver.get("https://ancient-taiga-22967.herokuapp.com/login.do");
    }

    @Test(dataProvider = "getInvalidEmailInvalidPassword")
    public void invalidEmailInvalidPasswordTest(String email, String password) {
        loginPage.login(email, password);
        softAssertions
                .assertThat(loginPage.getAlertErrorMessage())
                .isEqualTo(ALERT_ERROR_MSG);
        softAssertions
                .assertThat(loginPage.getEmailErrorMessage())
                .isEqualTo(INVALID_EMAIL_MSG);
        softAssertions
                .assertThat(loginPage.getPasswordErrorMessage())
                .isEqualTo(INVALID_PASSWORD_MSG);
        softAssertions.assertAll();
    }

    @Test(dataProvider = "getValidEmailInvalidPassword")
    public void validEmailInvalidPasswordTest(String email, String password) {
        loginPage.login(email, password);
        softAssertions
                .assertThat(loginPage.getAlertErrorMessage())
                .isEqualTo(ALERT_ERROR_MSG);
        softAssertions
                .assertThat(loginPage.doseEmailErrorMessageDisplayed())
                .isFalse();
        softAssertions
                .assertThat(loginPage.getPasswordErrorMessage())
                .isEqualTo(INVALID_PASSWORD_MSG);
        softAssertions.assertAll();
    }

    @Test(dataProvider = "getValidNotRegisteredCredentials")
    public void validNotRegisteredCredentialsTest(String email, String password) {
        loginPage.login(email, password);
        softAssertions
                .assertThat(loginPage.getAlertErrorMessage())
                .isEqualTo(ALERT_ERROR_EMAIL_PASS_MSG);
        softAssertions
                .assertThat(loginPage.doseEmailErrorMessageDisplayed())
                .isFalse();
        softAssertions
                .assertThat(loginPage.dosePasswordErrorMessageDisplayed())
                .isFalse();
        softAssertions.assertAll();
    }

    @Test
    public void registeredEmailWithWrongPass() {
        UserAccount user = UserAccount.getRegisteredUserAccount();

        loginPage.login(user.getEmail(), "wrong pass");
        softAssertions
                .assertThat(loginPage.getAlertErrorMessage())
                .isEqualTo(ALERT_ERROR_EMAIL_PASS_MSG);
        softAssertions
                .assertThat(loginPage.doseEmailErrorMessageDisplayed())
                .isFalse();
        softAssertions
                .assertThat(loginPage.dosePasswordErrorMessageDisplayed())
                .isFalse();
        softAssertions.assertAll();
    }

    @Test
    public void logInWithExistingCredentialsTest() {
        UserAccount user = UserAccount.getRegisteredUserAccount();

        loginPage.login(user.getEmail(), user.getPassword());
        softAssertions
                .assertThat(homePage.isHomePageDisplayed())
                .isTrue();
        softAssertions.assertAll();
    }

    @DataProvider
    public static Object[][] getInvalidEmailInvalidPassword() {
        return new Object[][]{
                {";uh;h", "233@$"},
                {"plainaddress", "    "},
                {"#@%^%#$ @#$ @#.com", "AAA"},
                {"example.com", "wq"},
                {"Joe Smith <email@example.com>", "5454"},
                {"email.example.com", "!@###"},
                {"email@gmail.c", "!@###"},
                {"email@example@example.com", " "},
                {".email @example.com ", "8888"},
        };
    }

    @DataProvider
    public static Object[][] getValidEmailInvalidPassword() {
        return new Object[][]{
                {"email@example.co", "12345"},
                {"firstname.lastname@example.co", "12345"},
                {"email@subdomain.example.co", "12345"},
                {"firstname+lastname@example.co", "12345"},
                {"email@123.123.123.12", "12345"},
                {"email@[123.123.123.123", "12345"},
                {"\"email\"@example.co", " 12345"},
        };
    }

    @DataProvider
    public static Object[][] getValidNotRegisteredCredentials() {
        return new Object[][]{
                {"email@example.co", "a12345"},
                {"firstname.lastname@example.co", "!@%#$&%"},
                {"email@subdomain.example.co", "AAAAAAA"},
                {"firstname+lastname@example.co", "a@#$%1211"},
                {"email@123.123.123.12", "       "},
                {"email@[123.123.123.123", "687643"},
                {"\"email\"@example.co", "41635135"},
        };
    }
}
