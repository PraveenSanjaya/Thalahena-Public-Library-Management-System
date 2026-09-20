# Thalahena Public Library Management System
## Automated Testing Report

Date: 2026-09-20
Branch tested: `bug_fixed`

### Environment

| Component | Version |
|---|---|
| Java | 21.0.10 (project targets 17, compiles/runs fine on 21) |
| Spring Boot | 3.2.4 |
| React | 18.2.0 |
| Vite | 5.4.21 |
| Node.js / npm | project `node_modules` already installed |
| Browser | Google Chrome 153.0.8010.52 (headless) |
| Selenium | selenium-java 4.27.0 (Selenium Manager auto-resolved matching ChromeDriver) |
| JUnit | JUnit 5 (Jupiter), via spring-boot-starter-test |
| MySQL | Live local instance, `library_db` (localhost:3306), already populated with real dev data |
| Maven | 3.9.14 (via `mvnw`/`mvnw.cmd`) |

All backend tests below ran against the **real MySQL dev database** (this project's existing/pre-existing pattern — see "Risks" section). Controller/service tests use `@MockBean` on repositories so they never write member/book/transaction data; the two Selenium tests that do write data (`CirculationSeleniumTest`) created their own disposable, uniquely-named test accounts and returned every book they issued before this report was written (see "Test Data Cleanup").

### Test Summary

**JUnit (pure Mockito unit tests, no Spring context, no DB):**

| Class | Tests | Pass | Fail |
|---|---|---|---|
| `TransactionServiceTest` (new) | 14 | 14 | 0 |
| `FineServiceTest` (new) | 10 | 10 | 0 |
| `ReservationServiceTest` (new) | 13 | 13 | 0 |
| `IssuanceServiceTest` (new) | 3 | 3 | 0 |
| `FineCalculatorServiceTest` (pre-existing) | 12 | 12 | 0 |
| `OtpServiceTest` (pre-existing) | 4 | 4 | 0 |
| **Total** | **56** | **56** | **0** |

**MockMvc / API (`@SpringBootTest` + `@AutoConfigureMockMvc`, repositories/services mocked):**

| Class | Tests | Pass | Fail |
|---|---|---|---|
| `BookControllerTest` (new) | 15 | 15 | 0 |
| `TransactionControllerTest` (new) | 12 | 12 | 0 |
| `MemberControllerTest` (new) | 6 | 6 | 0 |
| `AuthControllerTest` (pre-existing) | 2 | 2 | 0 |
| `OtpControllerTest` (pre-existing) | 4 | 4 | 0 |
| **Total** | **39** | **39** | **0** |

**Other pre-existing:** `ThalahenaPublicLibrarydemoApplicationTests` (context-load smoke test) — 1/1 pass.

**Full `mvnw test` run total: 96 tests, 96 passed, 0 failed, 0 skipped. BUILD SUCCESS.**

**Selenium (real Chrome, real running backend on :8081 and frontend on :5173):**

| Class | Tests | Pass | Fail |
|---|---|---|---|
| `LoginSeleniumTest` | 6 | 6 | 0 |
| `CirculationSeleniumTest` | 1 | 1 | 0 |
| **Total** | **7** | **7** | **0** |

Run with: `mvnw test -Pselenium -Dtest=LoginSeleniumTest,CirculationSeleniumTest` (excluded from the default `mvn test` run — see `pom.xml`).

**Grand total across all three suites: 103 tests run, 103 passed, 0 failed, 0 skipped.**

### Functional Areas Tested

- **Authentication**: login success/failure, JWT issuance and validation (existing `AuthControllerTest`/`OtpControllerTest`), role-based redirect for ADMIN/STAFF/MEMBER (Selenium), unauthenticated access to protected routes (Selenium + MockMvc 401s).
- **Authorization**: `@PreAuthorize` role gates on Book/Transaction/Member endpoints (403 for wrong role, 401 for no auth) via MockMvc.
- **Book Management**: CRUD via `BookControllerTest` — create, duplicate-ISBN rejection, missing required field, get by id (found/404), update (including ISBN-change collision and same-ISBN no-op), delete (found/404/forbidden for MEMBER).
- **Member Management**: CRUD via `MemberControllerTest` — list, duplicate username, not-found, non-member-id guard, and the newly-fixed FK-conflict-on-delete case.
- **Borrow/Return (Circulation)**: `TransactionServiceTest` + `TransactionControllerTest` + `CirculationSeleniumTest` — issue success, issue rejected when book unavailable, **one-book-per-member rule** (success path, rejection path, no partial state change), return success/already-returned/not-found, fine creation on return.
- **Fine Management**: `FineCalculatorServiceTest` (pre-existing, Rs. 5/day verified for 0/1/5/10+ days, early/on-time returns) + `FineServiceTest` (new: mark-paid, payment-date-before-return-date rejection, zero-amount forces status NONE, soft-delete, stats aggregation).
- **Reservations**: `ReservationServiceTest` + `IssuanceServiceTest` — create, missing user/book id, approve→auto-issue, reject, cancel-guard on COMPLETED/CANCELLED, auto-issue's own one-book-per-member enforcement. Reservation auto-expiry (`ReservationExpiryTask`, hourly `@Scheduled`, 3-day `expiryDate`) was confirmed correct by reading the implementation but not exercised end-to-end (it needs either a 3-day wall-clock wait or a clock-injection refactor that wasn't in scope for this pass).
- **Notifications**: read via code inspection only (ownership checks on `/api/notifications/{id}` and mark-as-read); no dedicated new tests were added this pass.

### Bugs Found

**BUG-1: Missing/invalid request parameters return HTTP 500 instead of 400**
- Feature: Book Management (and any endpoint with required `@RequestParam`s)
- Expected: A request missing a required field (e.g. `title` when creating a book) returns 400 Bad Request.
- Actual: `GlobalExceptionHandler`'s catch-all `@ExceptionHandler(Exception.class)` intercepted Spring's `MissingServletRequestParameterException` and returned 500.
- Root Cause: No specific handler for `MissingServletRequestParameterException` / `MissingServletRequestPartException` / `MethodArgumentTypeMismatchException` before the generic handler.
- Fix: `GlobalExceptionHandler.java` — added a dedicated handler for those three exception types returning 400.
- Test: `BookControllerTest.createBook_missingRequiredField_returns400` (failed before the fix with 500, passes now).
- Retest Result: PASS.

**BUG-2: Duplicate ISBN on book create/update was not validated — surfaced as an uncaught DB error**
- Feature: Book Management CREATE/UPDATE
- Expected: Creating/updating a book with an ISBN that already exists returns a clean 400.
- Actual: No pre-check existed; `Book.isbn` has a DB-level unique constraint, so a duplicate would throw `DataIntegrityViolationException`, previously also swallowed into a 500 by the generic handler. It would also have left an orphaned uploaded cover-image file on disk (the file is saved to disk *before* the DB save is attempted).
- Root Cause: `BookController.createBook`/`updateBook` had no `existsByIsbn` check, unlike the analogous username/email checks already present in `MemberController`/`AuthController`.
- Fix: Added `BookRepository.existsByIsbn(String)`; `BookController` now checks it before creating (any non-empty ISBN) and before updating (only when the ISBN actually changed), returning 400 with a message. Business rule (ISBN uniqueness) was already implied by the entity's `@Column(unique = true)` — this only makes the existing rule fail cleanly instead of crashing.
- Test: `BookControllerTest.createBook_duplicateIsbn_isRejectedWithoutSaving`, `updateBook_changingIsbnToAnotherBooksIsbn_isRejected`, `updateBook_keepingSameIsbn_isAllowed`.
- Retest Result: PASS.

**BUG-3: Deleting a member with existing transaction/fine history crashed with HTTP 500**
- Feature: Admin Member Management DELETE
- Expected: Deleting a member who has borrow history should fail gracefully (it *should* fail — the history must be preserved), not crash the server.
- Actual: Confirmed live against the real dev database: `DELETE /api/admin/members/{id}` for a member with a transaction record threw `DataIntegrityViolationException` (MySQL FK constraint `transactions.user_id → users.id`), caught only by the generic `Exception` handler, returning 500 with a raw SQL error message leaked to the client.
- Root Cause: `MemberController.deleteMember` calls `userRepository.delete(user)` with no handling for referential-integrity failures; same class of bug as BUG-1/BUG-2 (a legitimate business constraint hitting the generic exception handler).
- Fix: `GlobalExceptionHandler` — added `@ExceptionHandler(DataIntegrityViolationException.class)` returning 409 Conflict with a clear message, instead of leaking the SQL statement at 500.
- Test: `MemberControllerTest.deleteMember_withExistingTransactionHistory_returns409NotInternalServerError`. Also verified live: re-ran the exact failing `DELETE` request against the restarted backend and confirmed it now returns 409 with a clean message instead of 500 with a raw SQL exception string.
- Retest Result: PASS (unit test + live re-verification).

### One-Book-Per-Member Rule — Explicit Verification

This was already correctly implemented in `TransactionService.issueBook` / `updateTransaction` and `IssuanceService.issueBookForReservation` (confirmed by git history — commit `8a5021b` had already fixed this rule on this branch before this session). No regression was found. It is now covered by:
- `TransactionServiceTest`: issue-when-no-active-book succeeds; issue-when-ISSUED-active rejected; issue-when-OVERDUE-active rejected; issue-after-RETURNED succeeds; rejection does not create a transaction, does not touch `availableCopies`, and the error message names the currently-held book.
- `TransactionControllerTest.issueBook_ruleViolation_returns400WithMessage`: confirms the API contract (400 + message), matching the intentional business-rule rejection — **not** "fixed" into a success, per the task's explicit instruction.
- `IssuanceServiceTest`: the same rule enforced on the reservation-auto-issue path.
- `CirculationSeleniumTest` (real browser, real backend, real MySQL): registered a fresh member, issued a real book to them (succeeded, row appeared as "Borrowed"), attempted to issue a second book to the same member, confirmed the backend returned HTTP 400 (captured from the real browser network log) and that no second "Borrowed" row was created for that member.

### Bugs Intentionally NOT Fixed (and why)

- **Plaintext DB and Gmail app-password credentials committed in `application.properties`.** This is a real security smell, but rotating credentials or moving them to environment variables/`application-local.properties` is a config/ops change outside "fix a bug demonstrated by a test," and touching it without the user's say-so risks breaking their local setup or leaking a rotation they didn't ask for. Flagged here for the user's attention, not changed.
- **`ReservationController.getReservationsByUserId` has no ownership check** (any authenticated MEMBER can pass another member's `userId` and read their reservations — an IDOR, in contrast to `TransactionController`'s correct `#userId == principal.id` guard). This is a real authorization gap, but fixing it changes an access-control rule for a live production-style app without a test-first reproduction in this pass; flagging for a follow-up rather than changing security behavior speculatively.
- **`RegistrationController.updateRegistration`** does a raw `Role.valueOf(...)` on user input without a try/catch, so an invalid role string would 500 instead of 400 — same class of bug as BUG-1, but in a file outside this pass's tested scope; not fixed to keep the change set reviewable and test-verified.
- **Frontend forms without a `submitting` guard** (`Feedback.jsx`, `FineManagement.jsx`, `MemberManagement.jsx`) — these were fixed (see "Files Modified") since they were small, safe, and directly requested by Phase 10, but no duplicate-submission bug was actually reproduced (no `onSubmit`+`onClick` double-fire pattern exists anywhere in the codebase); the fix is preventive hardening, not a confirmed-bug fix.
- **Reservation auto-expiry end-to-end test**: the 3-day expiry + hourly `@Scheduled` job is implementation-correct by inspection but wasn't exercised live (would need a 3-day wait or a refactor to inject a clock, which is out of scope for a testing pass that must not modify business logic).

### Selenium E2E — What Was and Wasn't Run

Given a live browser (Chrome) and a live, already-running MySQL instance were available in this environment, two real Selenium suites were written **and executed** against the actual app (not simulated):
- `LoginSeleniumTest`: valid login for ADMIN/STAFF/MEMBER with correct dashboard redirect, invalid password, nonexistent username, unauthenticated access to `/member` redirects to `/login`.
- `CirculationSeleniumTest`: the one-book-per-member regression (TEST 6), using a freshly-registered member so it's independent of the shared dev DB's existing state.

Not attempted in this pass (would need more time to build reliably against the *shared, already-populated* dev database without flakiness): Book CRUD, Member CRUD, Return Book, Reservation approve/reject, and Additional Services Selenium flows. `data-testid` attributes were added to the relevant elements in `TransactionManagement.jsx` (issue/return buttons, selects) and `Login.jsx` so these can be added later without further selector work. Per the project's own rule ("do not report a test as PASS unless it was actually executed"), these are reported as **not run**, not as passing.

### Test Data Cleanup

`CirculationSeleniumTest` registers a real member via `/api/auth/register` and issues a real book via the UI on every run. After all runs in this session:
- All books issued to synthetic `sel_member_*` accounts were returned via the API (`PUT /transactions/return/{id}`) — confirmed 200 on each.
- 4 of the 7 synthetic member accounts created across repeated runs were hard-deleted (no transaction history).
- 3 synthetic member accounts (`sel_member_1789920728804`, `...794966`, `...931591`) **could not** be hard-deleted — correctly blocked by the same FK constraint behind BUG-3, since they now have a `RETURNED` transaction in their history. This is the same constraint that protects every real member's borrow history; leaving these 3 harmless "Selenium Tester" accounts in the dev DB is the correct outcome, not a defect (deleting a member with any borrow history, even a returned one, should not be silently allowed).

### Final Regression Result

| Check | Result |
|---|---|
| Backend starts | ✓ (verified via `mvnw spring-boot:run` against real MySQL) |
| Frontend starts | ✓ (`npm run dev`, port 5173) |
| Login works | ✓ (Selenium, all 3 roles) |
| Admin role works | ✓ |
| Staff role works | ✓ |
| Member role works | ✓ |
| Book CRUD works | ✓ (MockMvc; UI CRUD not Selenium-tested this pass) |
| Member CRUD works | ✓ (MockMvc; UI CRUD not Selenium-tested this pass) |
| Issue works | ✓ (unit + MockMvc + Selenium) |
| Second active issue is prevented | ✓ (unit + MockMvc + **live Selenium**, HTTP 400 confirmed in real browser network log) |
| Return works | ✓ (unit + MockMvc) |
| Fine calculation works | ✓ (pre-existing + new FineService tests, Rs. 5/day confirmed) |
| Reservations work | ✓ (unit tests; UI not Selenium-tested this pass) |
| Additional Services works | Not tested this pass |
| Selenium tests pass | ✓ (7/7, Login + Circulation) |
| JUnit tests pass | ✓ (96/96) |
| Existing functionality remains operational | ✓ — all 23 pre-existing tests still pass unchanged |

**Overall: PASS** (within the scope actually executed; see "Not Attempted" above for what's outside that scope).
