package com.lazerycode.selenium.tests;

import com.lazerycode.selenium.DriverBase;
import com.lazerycode.selenium.entities.Priority;
import com.lazerycode.selenium.entities.ToDoItem;
import com.lazerycode.selenium.entities.UserAccount;
import com.lazerycode.selenium.helpers.DateHelper;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Iterator;

import static com.lazerycode.selenium.BaseUrls.CREATE_TODO_PAGE_URL;
import static com.lazerycode.selenium.BaseUrls.HOME_PAGE_URL;
import static com.lazerycode.selenium.BaseUrls.LOGIN_PAGE_URL;
import static org.testng.Assert.assertTrue;

public class CreateToDoTest extends DriverBase {
    private static final String dateFormat = "dd/MM/yyyy";

    @BeforeMethod
    public void beforeMethod() {
        softAssertions = new SoftAssertions();
        driver.get(LOGIN_PAGE_URL);
        userAccount = UserAccount.getRegisteredUserAccount();
        loginPage.login(userAccount.getEmail(), userAccount.getPassword());
        assertTrue(homePage.isHomePageDisplayed(), "The home page is not displayed.");
        driver.get(CREATE_TODO_PAGE_URL);
    }

    @Test(dataProvider = "getInvalidTitles")
    public void taskWithInvalidTitleTest() throws Exception {
        ToDoItem item = ToDoItem.getToDoItem();
        item.setTitle("    ");
        newTodoPage.createTodo(item);
        softAssertions.assertThat(driver.getCurrentUrl())
                .withFailMessage("User should not allowed to create task with invalid title.")
                .isEqualTo(CREATE_TODO_PAGE_URL);
        softAssertions.assertAll();
    }

    @Test
    public void taskWithPastDateTest() throws Exception {
        ToDoItem item = ToDoItem.getToDoItem();
        item.setDate(DateHelper.getPreviousYearFor(dateFormat));
        newTodoPage.createTodo(item);
        softAssertions.assertThat(driver.getCurrentUrl())
                .withFailMessage("User should not allowed to create task with past date.")
                .isEqualTo(CREATE_TODO_PAGE_URL);
        softAssertions.assertAll();
    }

    @Test(dataProvider = "getValidToDoes")
    public void taskWithValidDateTest(ToDoItem item) throws Exception {
        int number = 0;
        newTodoPage.createTodo(item);
        softAssertions.assertThat(driver.getCurrentUrl())
                .withFailMessage("Cannot create task: " + item + " .")
                .isEqualTo(HOME_PAGE_URL);

        softAssertions.assertThat(homePage.getTitle(number)).isEqualTo(item.getTitle());
        softAssertions.assertThat(homePage.getDate(number)).isEqualTo(item.getDate());
        softAssertions.assertThat(homePage.getPriority(number)).isEqualTo(item.getPriority().name());
        softAssertions.assertThat(homePage.getStatus(number)).isEqualTo(item.getStatus().name());

        softAssertions.assertThat(homePage.getTotalCount()).isEqualTo("1");
        softAssertions.assertThat(homePage.getToDoCount()).isEqualTo("1");
        softAssertions.assertThat(homePage.getDoneCount()).isEqualTo("0");

        softAssertions.assertAll();

    }

    @DataProvider
    public static Object[][] getInvalidTitles() {
        return new Object[][]{
                new Object[]{"    "},
                new Object[]{""},
        };
    }

    @DataProvider
    public static Iterator<Object[]> getValidToDoes() {
        ToDoItem[] array = new ToDoItem[]{
            new ToDoItem("task1", DateHelper.getCurrentYear(dateFormat), Priority.HIGH),
                    new ToDoItem("task2", DateHelper.getNextDay(dateFormat), Priority.MEDIUM),
                    new ToDoItem("task3", DateHelper.getNextYear(dateFormat), Priority.LOW),
                    new ToDoItem("task4", DateHelper.getCurrentYear(dateFormat), Priority.HIGH)
        } ;
        return Arrays.stream(array).map(e -> new Object[]{e}).iterator();
    }

}
