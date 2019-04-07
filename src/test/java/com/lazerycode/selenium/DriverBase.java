package com.lazerycode.selenium;

import com.lazerycode.selenium.config.DriverFactory;
import com.lazerycode.selenium.entities.UserAccount;
import com.lazerycode.selenium.listeners.ScreenshotListener;
import com.lazerycode.selenium.page_objects.CreateNewTodoPageObject;
import com.lazerycode.selenium.page_objects.HomePageObject;
import com.lazerycode.selenium.page_objects.LogInPageObject;
import org.assertj.core.api.SoftAssertions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Listeners(ScreenshotListener.class)
public class DriverBase {
    protected LogInPageObject loginPage;
    protected HomePageObject homePage;
    protected CreateNewTodoPageObject newTodoPage;
    protected UserAccount userAccount;
    protected WebDriver driver;
    protected SoftAssertions softAssertions;

    private static List<DriverFactory> webDriverThreadPool = Collections.synchronizedList(new ArrayList<DriverFactory>());
    private static ThreadLocal<DriverFactory> driverFactoryThread;

    @BeforeClass
    public void beforeClass() throws Exception {
        loginPage = new LogInPageObject();
        homePage = new HomePageObject();
        newTodoPage = new CreateNewTodoPageObject();
        userAccount = UserAccount.getRegisteredUserAccount();
        driver = getDriver();
        driver.manage().window().maximize();
    }

    @BeforeSuite(alwaysRun = true)
    public static void instantiateDriverObject() {
        driverFactoryThread = ThreadLocal.withInitial(() -> {
            DriverFactory driverFactory = new DriverFactory();
            webDriverThreadPool.add(driverFactory);
            return driverFactory;
        });
    }

    public static RemoteWebDriver getDriver() throws Exception {
        return driverFactoryThread.get().getDriver();
    }

    @AfterMethod(alwaysRun = true)
    public static void clearCookies() {
        try {
            driverFactoryThread.get().getStoredDriver().manage().deleteAllCookies();
        } catch (Exception ignored) {
            System.out.println("Unable to clear cookies, driver object is not viable...");
        }
    }

    @AfterSuite(alwaysRun = true)
    public static void closeDriverObjects() {
        for (DriverFactory driverFactory : webDriverThreadPool) {
            driverFactory.quitDriver();
        }
    }
}