package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.e2e;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Selenium end-to-end tests for the login flow and role-based dashboard redirection.
 *
 * PREREQUISITES (not started by this test — start them first):
 *   1. Backend running on http://localhost:8081  (mvnw spring-boot:run)
 *   2. Frontend running on http://localhost:5173 (npm run dev, inside /frontend)
 *   3. A real Chrome browser installed (Selenium Manager resolves the matching driver automatically)
 *   4. Default seed accounts present (created by DataInitializer on backend startup):
 *      admin1/admin123 (ADMIN), staff1/staff123 (STAFF), user1/user123 (MEMBER)
 *
 * Run with:  mvnw test -Pselenium -Dtest=LoginSeleniumTest
 * (excluded from the default `mvn test` run — see pom.xml surefire excludes)
 */
public class LoginSeleniumTest {

    private static final String BASE_URL = "http://localhost:5173";

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--window-size=1400,1000", "--disable-gpu", "--no-sandbox");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void login(String username, String password) {
        driver.get(BASE_URL + "/login");
        WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='login-username']")));
        WebElement passwordField = driver.findElement(By.cssSelector("[data-testid='login-password']"));
        usernameField.clear();
        usernameField.sendKeys(username);
        passwordField.clear();
        passwordField.sendKeys(password);
        driver.findElement(By.cssSelector("[data-testid='login-submit']")).click();
    }

    @Test
    void adminLogin_validCredentials_redirectsToAdminDashboard() {
        login("admin1", "admin123");
        wait.until(ExpectedConditions.urlContains("/admin"));
        assertTrue(driver.getCurrentUrl().contains("/admin"),
                "Expected redirect to /admin dashboard, got: " + driver.getCurrentUrl());
    }

    @Test
    void staffLogin_validCredentials_redirectsToStaffDashboard() {
        login("staff1", "staff123");
        wait.until(ExpectedConditions.urlContains("/staff"));
        assertTrue(driver.getCurrentUrl().contains("/staff"),
                "Expected redirect to /staff dashboard, got: " + driver.getCurrentUrl());
    }

    @Test
    void memberLogin_validCredentials_redirectsToMemberDashboard() {
        login("user1", "user123");
        wait.until(ExpectedConditions.urlContains("/member"));
        assertTrue(driver.getCurrentUrl().contains("/member"),
                "Expected redirect to /member dashboard, got: " + driver.getCurrentUrl());
    }

    @Test
    void login_invalidPassword_showsErrorAndStaysOnLoginPage() {
        login("admin1", "wrong-password-123");

        WebElement errorMessage = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(text(), 'Invalid username or password')]")));

        assertTrue(errorMessage.isDisplayed());
        assertTrue(driver.getCurrentUrl().contains("/login"),
                "Should remain on /login after a failed attempt, got: " + driver.getCurrentUrl());
    }

    @Test
    void login_nonexistentUsername_showsError() {
        login("no_such_user_xyz", "whatever123");

        WebElement errorMessage = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.xpath("//*[contains(text(), 'Invalid username or password')]")));

        assertTrue(errorMessage.isDisplayed());
    }

    @Test
    void memberDashboard_isNotReachableForAnUnauthenticatedUser() {
        // No login performed — directly hitting a protected route must not show the member dashboard.
        driver.get(BASE_URL + "/member");
        wait.until(ExpectedConditions.urlContains("/login"));
        assertTrue(driver.getCurrentUrl().contains("/login"),
                "Unauthenticated access to /member must redirect to /login, got: " + driver.getCurrentUrl());
    }
}
