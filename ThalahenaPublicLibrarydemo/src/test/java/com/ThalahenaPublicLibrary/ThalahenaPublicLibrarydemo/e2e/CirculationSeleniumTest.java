package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.e2e;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Selenium end-to-end regression test for TEST 6 — the one-book-per-member circulation rule
 * (TransactionManagement.jsx, "Circulation Management" page).
 *
 * A brand-new member is registered via the public /api/auth/register endpoint before the UI
 * interaction, so the test is self-contained and does not depend on (or disturb) any pre-existing
 * member's borrow state in the shared dev database. Only a fresh member and a fresh borrow
 * transaction are created; nothing is deleted.
 *
 * PREREQUISITES (start manually first, same as LoginSeleniumTest):
 *   1. Backend on http://localhost:8081  2. Frontend on http://localhost:5173
 *   3. Chrome installed  4. staff1/staff123 seed account present
 *
 * Run with:  mvnw test -Pselenium -Dtest=CirculationSeleniumTest
 */
public class CirculationSeleniumTest {

    private static final String BASE_URL = "http://localhost:5173";
    private static final String API_URL = "http://localhost:8081/api";

    private WebDriver driver;
    private WebDriverWait wait;
    private static String memberUsername;

    @BeforeAll
    static void registerFreshMember() throws Exception {
        memberUsername = "sel_member_" + System.currentTimeMillis();
        String body = String.format(
                "{\"username\":\"%s\",\"email\":\"%s@example.com\",\"password\":\"Passw0rd123\",\"firstName\":\"Selenium\",\"lastName\":\"Tester\"}",
                memberUsername, memberUsername);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + "/auth/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(),
                "Setup precondition failed: could not register a fresh test member. Response: " + response.body());
    }

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--window-size=1400,1000", "--disable-gpu", "--no-sandbox");
        org.openqa.selenium.logging.LoggingPreferences logPrefs = new org.openqa.selenium.logging.LoggingPreferences();
        logPrefs.enable(org.openqa.selenium.logging.LogType.BROWSER, java.util.logging.Level.ALL);
        options.setCapability("goog:loggingPrefs", logPrefs);
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void loginAsStaff() {
        driver.get(BASE_URL + "/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='login-username']")))
                .sendKeys("staff1");
        driver.findElement(By.cssSelector("[data-testid='login-password']")).sendKeys("staff123");
        driver.findElement(By.cssSelector("[data-testid='login-submit']")).click();
        wait.until(ExpectedConditions.urlContains("/staff"));
    }

    private void jsClick(WebElement element) {
        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
    }

    private void openIssueModalFor(String memberUsername) {
        WebElement issueBtn = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("[data-testid='issue-book-btn']")));
        jsClick(issueBtn);

        Select memberSelect = new Select(wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='issue-member-select']"))));
        WebElement matchingOption = memberSelect.getOptions().stream()
                .filter(o -> o.getText().startsWith(memberUsername + " "))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Freshly registered member '" + memberUsername +
                        "' was not found in the Issue Book member dropdown"));
        memberSelect.selectByVisibleText(matchingOption.getText());

        Select bookSelect = new Select(driver.findElement(By.cssSelector("[data-testid='issue-book-select']")));
        List<WebElement> bookOptions = bookSelect.getOptions();
        assertTrue(bookOptions.size() > 1, "Expected at least one available book in the Issue Book dropdown");
        bookSelect.selectByIndex(1); // first real book (index 0 is the "Choose a book" placeholder)
    }

    @Test
    void issueBook_freshMemberFirstBorrow_succeeds_thenSecondBorrowIsRejected() {
        loginAsStaff();
        driver.get(BASE_URL + "/staff/transactions");

        // --- First issue: must succeed ---
        openIssueModalFor(memberUsername);
        jsClick(driver.findElement(By.cssSelector("[data-testid='issue-submit']")));

        // Modal closes and the table refreshes with the new ISSUED row for this member
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("[data-testid='issue-member-select']")));
        WebElement newRow = wait.until(d -> {
            List<WebElement> rows = d.findElements(By.xpath("//table//tr[td[contains(., '" + memberUsername + "')]]"));
            return rows.isEmpty() ? null : rows.get(0);
        });
        assertTrue(newRow.getText().contains("Borrowed"),
                "Expected the new transaction row to show status 'Borrowed'. Row text: " + newRow.getText());

        // --- Second issue attempt for the SAME member: must be rejected (business rule, HTTP 400) ---
        openIssueModalFor(memberUsername);
        jsClick(driver.findElement(By.cssSelector("[data-testid='issue-submit']")));

        // The rejection is surfaced to the user via a native window.alert(); in headless Chrome that
        // dialog can occasionally be suppressed by the browser itself ("another modal already
        // showing"), so the authoritative check is the actual network response captured in the
        // browser's console logs, not the fragile native dialog.
        boolean sawRejection = wait.until(d -> d.manage().logs().get(org.openqa.selenium.logging.LogType.BROWSER).getAll()
                .stream()
                .anyMatch(entry -> entry.getMessage().contains("/transactions/issue")
                        && entry.getMessage().contains("400")));
        assertTrue(sawRejection, "Expected the second issue attempt to receive an HTTP 400 rejection from the backend");

        // Best-effort: dismiss the alert if it is actually showing, so it doesn't block later navigation.
        try {
            driver.switchTo().alert().accept();
        } catch (org.openqa.selenium.NoAlertPresentException ignored) {
            // Alert was suppressed by the browser or already dismissed — the network-level
            // assertion above is the authoritative evidence for this test.
        }

        // Requirement: the second (rejected) book must NOT appear as an additional Borrowed row
        // for this member — exactly one active transaction must exist.
        driver.get(BASE_URL + "/staff/transactions");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("table")));
        List<WebElement> memberRows = driver.findElements(
                By.xpath("//table//tr[td[contains(., '" + memberUsername + "')]][.//span[contains(text(), 'Borrowed')]]"));
        assertEquals(1, memberRows.size(),
                "Rejected second issue must not create an additional active (Borrowed) transaction for this member");
    }
}
