package com.lazerycode.selenium.tests;

import com.lazerycode.selenium.DriverBase;
import com.lazerycode.selenium.entities.ToDoItem;
import com.lazerycode.selenium.entities.UserAccount;
import com.lazerycode.selenium.page_objects.DeletePopUpPageObject;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.lazerycode.selenium.BaseUrls.CREATE_TODO_PAGE_URL;
import static com.lazerycode.selenium.BaseUrls.LOGIN_PAGE_URL;
import static org.testng.Assert.assertTrue;

public class HomeTest extends DriverBase {

    @BeforeMethod
    public void beforeMethod() {
        softAssertions = new SoftAssertions();
        driver.get(LOGIN_PAGE_URL);
        userAccount = UserAccount.getRegisteredUserAccount();
        loginPage.login(userAccount.getEmail(), userAccount.getPassword());
        assertTrue(homePage.isHomePageDisplayed(), "The home page is not displayed.");
    }

    @Test
    public void homePageDefaultTest(){
        softAssertions.assertThat(homePage.getTableItemsCount()).isEqualTo(0);

        softAssertions.assertThat(homePage.getTotalCount()).isEqualTo("0");
        softAssertions.assertThat(homePage.getToDoCount()).isEqualTo("0");
        softAssertions.assertThat(homePage.getDoneCount()).isEqualTo("0");

        softAssertions.assertAll();
    }

    @Test
    public void createTaskTest() throws Exception {
        driver.get(CREATE_TODO_PAGE_URL);
        ToDoItem item = newTodoPage.createTodo();
        int number = 0;
        softAssertions.assertThat(homePage.getTitle(number)).isEqualTo(item.getTitle());
        softAssertions.assertThat(homePage.getDate(number)).isEqualTo(item.getDate());
        softAssertions.assertThat(homePage.getPriority(number)).isEqualTo(item.getPriority().name());
        softAssertions.assertThat(homePage.getStatus(number)).isEqualTo(item.getStatus().name());

        softAssertions.assertThat(homePage.getTotalCount()).isEqualTo("1");
        softAssertions.assertThat(homePage.getToDoCount()).isEqualTo("1");
        softAssertions.assertThat(homePage.getDoneCount()).isEqualTo("0");

        softAssertions.assertAll();
    }

    @Test
    public void deleteTaskTest() throws Exception {
        DeletePopUpPageObject deletePopUp = new DeletePopUpPageObject();

        driver.get(CREATE_TODO_PAGE_URL);
        newTodoPage.createTodo();

        homePage.clickOnDeleteButton(0);
        deletePopUp.clickOnConfirmButton();

        softAssertions.assertThat(homePage.getTotalCount()).isEqualTo("0");
        softAssertions.assertThat(homePage.getToDoCount()).isEqualTo("0");
        softAssertions.assertThat(homePage.getDoneCount()).isEqualTo("0");

        softAssertions.assertAll();
    }

    @Test
    public void listOfTaskTest() throws Exception {
        for(int i = 0; i < 20; i++ ) {
            driver.get(CREATE_TODO_PAGE_URL);
            ToDoItem item = newTodoPage.createTodo();
            softAssertions.assertThat(homePage.getTitle(i)).isEqualTo(item.getTitle());
            softAssertions.assertThat(homePage.getDate(i)).isEqualTo(item.getDate());
            softAssertions.assertThat(homePage.getPriority(i)).isEqualTo(item.getPriority().name());
            softAssertions.assertThat(homePage.getStatus(i)).isEqualTo(item.getStatus().name());

            softAssertions.assertThat(homePage.getTotalCount()).isEqualTo(""+(i+1));
            softAssertions.assertThat(homePage.getToDoCount()).isEqualTo(""+(i+1));
            softAssertions.assertThat(homePage.getDoneCount()).isEqualTo("0");

            softAssertions.assertAll();
        }
    }
}
