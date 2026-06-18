# Thalahena Public Library Management System — Product Backlog

> **System Overview:** A full-stack Library Management System built with Spring Boot 3.2 (backend), React 18 + Vite (frontend), MySQL (database), JWT + Email OTP (authentication), and Spring Security (authorization). Supports three roles: **Admin**, **Staff**, and **Member**.

---

## Epic 1: Authentication & Access Control

### 1.1 Login Page (UI + API)

**Description**
The Login Page provides a secure entry point for all users (Admin, Staff, Member) to authenticate via username/email and password. The backend validates credentials through Spring Security's `AuthenticationManager`, generates a JWT token via `JwtUtils`, and returns user details (id, username, email, role). The React frontend stores the JWT and role in `localStorage`, then redirects users to their role-specific dashboard (`/admin`, `/staff`, or `/member`).

**User Story**
- As a **registered user**, I want to log in with my username and password so that I can access the library system and my role-specific features.
- As a **system administrator**, I want unauthorized users to be blocked from accessing protected routes so that library data remains secure.

**Definition**
- A responsive login form rendered at `/login` using React, with fields for username and password.
- Backend endpoint `POST /api/auth/login` accepts `{ username, password }`, validates via `AuthenticationManager`, and returns a `LoginResponse` containing JWT, user ID, username, email, and role.
- The frontend `ProtectedRoute` component guards routes by checking JWT presence and role match.
- Failed login returns a 401 Unauthorized response with an error message.

**Business Value**
- Ensures only authorized personnel and registered members access the system.
- JWT-based stateless authentication enables scalable, secure session management without server-side session storage.
- Role-based redirection provides a seamless user experience immediately after login.

**Acceptance Criteria**

| # | Scenario | Given | When | Then |
|---|----------|-------|------|------|
| 1 | Successful Admin login | A valid Admin account exists | User submits correct username and password | System returns JWT with role `ADMIN` and redirects to `/admin` |
| 2 | Successful Staff login | A valid Staff account exists | User submits correct username and password | System returns JWT with role `STAFF` and redirects to `/staff` |
| 3 | Successful Member login | A valid Member account exists | User submits correct username and password | System returns JWT with role `MEMBER` and redirects to `/member` |
| 4 | Invalid credentials | Any user exists | User submits wrong password | System returns 401 with "Bad credentials" error; login form shows error message |
| 5 | Unauthenticated route access | User is not logged in | User navigates to `/admin` or `/staff` | System redirects to `/login` |
| 6 | Role mismatch | User is logged in as MEMBER | User navigates to `/admin` | System redirects to `/access-denied` |

**Definition of Done**
- [ ] Login form renders at `/login` with username/password fields and submit button
- [ ] `POST /api/auth/login` validates credentials and returns JWT + user details
- [ ] Frontend stores JWT, user info, and role in `localStorage`
- [ ] `ProtectedRoute` component enforces JWT presence and role-based access
- [ ] Successful login redirects to the correct role-based dashboard
- [ ] Invalid credentials display an error message without revealing which field is wrong
- [ ] JWT token is attached to all subsequent API requests via Axios interceptor
- [ ] Unit tests pass for `AuthController.authenticateUser()`
- [ ] No sensitive data (password) is returned in the login response

---

### 1.2 Passwordless Login via OTP

**Description**
The OTP login flow enables users to authenticate without a password by receiving a one-time password (OTP) via email. The user enters their registered email at the login page, clicks "Login with OTP," which triggers `POST /api/auth/request-otp`. The backend generates a 6-digit OTP via `OtpService`, stores it in the `otp_tokens` table with an expiry time, and sends it via `EmailService` (JavaMailSender). The user enters the OTP in a modal, and `POST /api/auth/verify-otp` validates it. On success, a JWT is issued identical to the password flow.

**User Story**
- As a **member who forgot my password**, I want to log in using an email OTP so that I can access the system without resetting my password.
- As a **security-conscious user**, I want the OTP to expire quickly so that it cannot be misused if intercepted.

**Definition**
- Frontend "Login with OTP" button triggers `POST /api/auth/request-otp` with `{ email }`.
- Backend generates a 6-digit OTP, persists to `OtpToken` entity with expiry, and sends via email.
- An OTP modal (`OtpLoginModal.jsx`) collects the 6-digit code and calls `POST /api/auth/verify-otp` with `{ email, otp }`.
- `OtpService.verifyOtp()` checks: OTP matches, not expired, and not already used.
- On success, a standard `LoginResponse` with JWT is returned.
- OTP tokens are single-use and time-limited.

**Business Value**
- Reduces friction for users who forget passwords, lowering support burden.
- Email-based OTP adds a second authentication factor, enhancing security.
- Single-use, expiring tokens prevent replay attacks.

**Acceptance Criteria**

| # | Scenario | Given | When | Then |
|---|----------|-------|------|------|
| 1 | OTP request for valid email | User has a registered email | User clicks "Login with OTP" and enters email | System sends 6-digit OTP to email; success message shown |
| 2 | OTP request for unregistered email | Email not in database | User enters unknown email | System returns "No account found with this email" |
| 3 | Successful OTP verification | OTP was sent to user's email | User enters correct OTP within expiry time | System returns JWT + user details; user is redirected to dashboard |
| 4 | Invalid OTP | OTP was sent | User enters wrong OTP | System returns "Invalid or expired OTP" error |
| 5 | Expired OTP | OTP was sent but expiry time passed | User enters correct but expired OTP | System returns "Invalid or expired OTP" error |
| 6 | OTP reuse prevention | OTP was already used successfully | User tries to reuse same OTP | System rejects with "Invalid or expired OTP" |

**Definition of Done**
- [ ] "Login with OTP" button and modal render correctly on the Login page
- [ ] `POST /api/auth/request-otp` generates OTP, stores with expiry, sends email
- [ ] `POST /api/auth/verify-otp` validates OTP correctness, expiry, and single-use
- [ ] Successful OTP verification returns identical `LoginResponse` as password login
- [ ] OTP email is delivered via JavaMailSender with clear instructions
- [ ] Expired and used OTPs are rejected
- [ ] Unit tests pass for `OtpService` (generation, verification, expiry)
- [ ] OTP tokens table schema includes expiry and used columns

---

### 1.3 Role-Based Authorization

**Description**
The system implements a three-tier role-based access control (RBAC) model using Spring Security. Authorization is enforced at two levels: (1) URL-based via `WebSecurityConfig.requestMatchers()` for broad route protection, and (2) method-level via `@PreAuthorize` SpEL expressions for fine-grained access. The three roles are **ADMIN** (full system access including user management, registrations, about statements), **STAFF** (operational access including books, transactions, fines, notifications, reservations), and **MEMBER** (self-service access including borrowing history, reservations, feedback, notifications). Frontend enforces matching via `ProtectedRoute` with `allowedRoles`.

**User Story**
- As an **Admin**, I want exclusive access to user management and system configuration so that only I can control who works in the library system.
- As a **Staff member**, I want access to daily operations (books, transactions, fines) without being able to modify admin settings.
- As a **Member**, I want to view my own borrowing history and make reservations without seeing other members' private data.

**Definition**
- Backend roles stored as `Role` enum (`ADMIN`, `STAFF`, `MEMBER`) in the `users` table.
- `WebSecurityConfig` defines URL matchers: `/api/admin/**` → ADMIN only; `/api/staff/**` → STAFF or ADMIN; `/api/transactions/user/**` → authenticated (with SpEL IDOR check for MEMBER self-access).
- `@PreAuthorize` annotations on each controller method enforce role requirements.
- Frontend `ProtectedRoute` checks `user.role` against `allowedRoles` array; mismatches redirect to `/access-denied`.
- Sidebar navigation (`Sidebar.jsx`) dynamically shows menu items based on user role.

**Business Value**
- Principle of least privilege: each role sees only what they need, reducing accidental misuse.
- Compliance with library governance policies separating administrative and operational duties.
- IDOR protection prevents members from accessing other members' data.

**Acceptance Criteria**

| # | Scenario | Given | When | Then |
|---|----------|-------|------|------|
| 1 | Admin accesses admin endpoint | User has ADMIN role | Calls `GET /api/admin/stats` | Returns 200 with admin statistics |
| 2 | Staff blocked from admin endpoint | User has STAFF role | Calls `GET /api/admin/stats` | Returns 403 Forbidden |
| 3 | Member blocked from staff endpoint | User has MEMBER role | Calls `GET /api/staff/transactions` | Returns 403 Forbidden |
| 4 | Member self-access with IDOR | User is MEMBER with id=16 | Calls `GET /api/transactions/user/16` | Returns 200 with own history |
| 5 | Member blocked from other's data | User is MEMBER with id=16 | Calls `GET /api/transactions/user/5` | Returns 403 Forbidden (SpEL check) |
| 6 | Frontend role enforcement | Member is logged in | Navigates to `/admin` | Redirected to `/access-denied` page |
| 7 | Dynamic sidebar | Staff user logs in | Sidebar renders | Shows only staff-relevant menu items (no "User Management") |
| 8 | ADMIN access to staff endpoints | Admin user | Calls any `/api/staff/**` endpoint | Returns 200 (ADMIN inherits STAFF access) |

**Definition of Done**
- [ ] All three roles (ADMIN, STAFF, MEMBER) are defined in the `Role` enum
- [ ] `WebSecurityConfig` configures URL-based authorization with correct ordering (specific before general)
- [ ] Every controller method has appropriate `@PreAuthorize` annotation
- [ ] SpEL-based IDOR protection on member self-access endpoints (`#userId == principal.id`)
- [ ] Frontend `ProtectedRoute` enforces role matching on all protected routes
- [ ] Sidebar dynamically renders role-appropriate navigation items
- [ ] 403 Forbidden responses are returned for unauthorized access attempts
- [ ] Integration tests verify role-based access for each endpoint group

---

## Epic 2: Admin Dashboard

### 2.1 Admin Dashboard Layout & Widgets

**Description**
The Admin Dashboard (`/admin`) is the landing page for Admin users, providing a high-level overview of the library's status through KPI widgets and charts. It displays: total users, staff count, admin count, active/inactive members, gender distribution (pie chart via Chart.js), age distribution (≤18 vs >18), and the 5 most recent feedback entries. Data is fetched from `GET /api/admin/stats` which returns an `AdminStatsDTO`. The dashboard uses a responsive glassmorphism dark-mode UI with `DashboardLayout` (Navbar + Sidebar + content area).

**User Story**
- As an **Admin**, I want a single-page overview of the library's key metrics so that I can quickly assess the system's health and user engagement.

**Definition**
- Frontend `AdminDashboard.jsx` renders KPI cards and charts within `DashboardLayout`.
- Backend `GET /api/admin/stats` aggregates data: `userRepository.count()`, `countByRole()`, `countByRoleAndIsActive()`, gender/age distribution from `findAll()`, top 5 feedback from `feedbackRepository`.
- Response mapped to `AdminStatsDTO` with fields: totalUsers, staffCount, adminCount, activeMembers, inactiveMembers, genderDistribution (Map), ageDistribution (Map), recentFeedback (List<FeedbackDTO>).
- Charts rendered using Chart.js (doughnut/pie for gender and age).

**Business Value**
- Executive-level visibility into library operations without navigating multiple pages.
- Data-driven decision making through visual gender/age demographics.
- Quick access to recent member feedback for service improvement.

**Acceptance Criteria**

| # | Scenario | Given | When | Then |
|---|----------|-------|------|------|
| 1 | Dashboard loads successfully | Admin is logged in | Navigates to `/admin` | KPI cards show correct totals; charts render with distribution data |
| 2 | Gender distribution chart | Users with gender data exist | Dashboard loads | Pie/doughnut chart shows Male/Female/Other distribution |
| 3 | Age distribution chart | Users with birth dates exist | Dashboard loads | Chart shows ≤18 vs >18 counts |
| 4 | Recent feedback widget | Members submitted feedback | Dashboard loads | Displays latest 5 feedback entries with username and timestamp |
| 5 | Unauthorized access | Non-admin user | Navigates to `/admin` | Redirected to `/access-denied` |

**Definition of Done**
- [ ] `GET /api/admin/stats` returns complete `AdminStatsDTO` with all aggregated fields
- [ ] Admin dashboard renders KPI cards (total users, staff, admin, active/inactive members)
- [ ] Gender and age distribution charts render using Chart.js
- [ ] Recent feedback list displays top 5 entries with author and date
- [ ] Dashboard is responsive across desktop and tablet viewports
- [ ] Loading state and error handling are implemented
- [ ] Only ADMIN role can access the dashboard

---

### 2.2 Member CRUD (Admin)

**Description**
The Admin Member Management page (`/admin/members`) provides full CRUD operations on library members (users with `Role.MEMBER`). Features include: listing all members with `MemberDTO` projection (includes `isActive`, membership date, gender, phone, email), searching by name/username/email via `?search=` query parameter, toggling member active/inactive status, creating new members with extended fields (first name, last name, username, email, phone, gender, birth date, membership date, status), editing member details including username with uniqueness validation, and deleting members. Backend endpoints: `GET /api/admin/members`, `POST /api/admin/members`, `PUT /api/admin/members/{id}`, `DELETE /api/admin/members/{id}`, `PATCH /api/admin/members/{id}/toggle-status`.

**User Story**
- As an **Admin**, I want to create, view, edit, deactivate, and delete member accounts so that I can manage the library's membership roster.
- As an **Admin**, I want to search for members by name or email so that I can quickly find specific accounts.

**Definition**
- Frontend `MemberManagement.jsx` (Admin) renders a data table with search, add/edit/delete modals, and status toggle buttons.
- Backend `MemberController` handles CRUD at `/api/admin/members` with `@PreAuthorize("hasRole('ADMIN')")`.
- Member creation includes: username, email, password, first name, last name, phone, gender, birth date, membership date (defaults to today), status (defaults to Active).
- Username and email uniqueness are validated on create and update.
- Status toggle flips `isActive` boolean and persists immediately.
- `MemberDTO` ensures consistent JSON serialization with `@JsonProperty("isActive")`.

**Business Value**
- Centralized member lifecycle management from registration to deactivation.
- Active/Inactive status controls borrowing eligibility without deleting member records.
- Search functionality reduces time to locate member accounts in large databases.

**Acceptance Criteria**

| # | Scenario | Given | When | Then |
|---|----------|-------|------|------|
| 1 | List all members | Members exist in database | Admin navigates to `/admin/members` | Table displays all members with correct Active/Inactive status badges |
| 2 | Search members | Members "Amara" and "Nimal" exist | Admin types "Amara" in search | Only matching members are displayed |
| 3 | Create new member | Admin clicks "Add Member" | Fills form with unique username/email and submits | Member created; appears in table with Active status |
| 4 | Duplicate username on create | Username "amara123" already exists | Admin creates member with same username | Returns "Username is already taken" error |
| 5 | Edit member username | Member has username "oldname" | Admin changes to "newname" (unique) | Username updated and persisted |
| 6 | Toggle member status | Member is Active | Admin clicks status toggle | Status changes to Inactive (red badge); member cannot borrow |
| 7 | Delete member | Member exists | Admin clicks delete and confirms | Member removed from database and table |

**Definition of Done**
- [ ] `GET /api/admin/members` returns `List<MemberDTO>` with correct `isActive` serialization
- [ ] `POST /api/admin/members` creates member with all extended fields and validates uniqueness
- [ ] `PUT /api/admin/members/{id}` updates member details including username with uniqueness check
- [ ] `DELETE /api/admin/members/{id}` removes member from database
- [ ] `PATCH /api/admin/members/{id}/toggle-status` flips `isActive` and persists
- [ ] Search filters members by name, username, and email
- [ ] Status badges show green (#10b981) for Active, red (#f43f5e) for Inactive
- [ ] Form validation prevents empty required fields

---

### 2.3 Registration CRUD

**Description**
The Registration Management page (`/admin/registrations`) allows Admins to create and manage Staff and Admin user accounts. Unlike member registration (which is self-service), this is the admin-controlled registration for library employees. Endpoints at `/api/admin/registrations` support: listing all staff/admin users, creating new staff/admin accounts with role selection (STAFF or ADMIN), updating account details including role changes, and deleting staff/admin accounts. Password is required on creation (min 8 characters) and optional on update.

**User Story**
- As an **Admin**, I want to register new staff members and admin users so that they can access the system with appropriate permissions.
- As an **Admin**, I want to change a staff member's role to admin (or vice versa) so that I can adjust responsibilities.

**Definition**
- Frontend `RegistrationManagement.jsx` renders a table of STAFF and ADMIN users with add/edit/delete functionality.
- Backend `RegistrationController` at `/api/admin/registrations` with `@PreAuthorize("hasRole('ADMIN')")`.
- Create: validates role is STAFF or ADMIN (rejects MEMBER), enforces password min 8 chars, checks username/email uniqueness.
- Update: allows changing first name, last name, email, phone, whatsapp, password (optional), role, and active status.
- Delete: only STAFF or ADMIN users can be deleted (role validation before deletion).

**Business Value**
- Admin controls the employee access lifecycle without needing database-level intervention.
- Role flexibility allows dynamic reassignment of responsibilities.
- Password enforcement ensures strong credential policies for privileged accounts.

**Acceptance Criteria**

| # | Scenario | Given | When | Then |
|---|----------|-------|------|------|
| 1 | List staff/admin users | Staff and Admin users exist | Admin navigates to `/admin/registrations` | Table shows only STAFF and ADMIN role users |
| 2 | Create Staff account | Admin fills form with role=STAFF | Submits with unique username, valid password | Staff account created; appears in table |
| 3 | Create with MEMBER role rejected | Admin selects MEMBER role | Submits form | Returns "Role must be STAFF or ADMIN" error |
| 4 | Short password rejected | Admin enters 5-char password | Submits form | Returns "Password must be at least 8 characters" error |
| 5 | Update role from STAFF to ADMIN | Staff user exists | Admin changes role dropdown to ADMIN and saves | User's role updated; reflected in table |
| 6 | Update password | Staff user exists | Admin enters new password (8+ chars) and saves | Password re-encrypted and updated |
| 7 | Delete staff account | Staff user exists | Admin clicks delete | Account removed; user can no longer log in |

**Definition of Done**
- [ ] `GET /api/admin/registrations` returns only STAFF and ADMIN users
- [ ] `POST /api/admin/registrations` creates user with role validation and password strength check
- [ ] `PUT /api/admin/registrations/{id}` updates details with optional password change
- [ ] `DELETE /api/admin/registrations/{id}` removes staff/admin user with role validation
- [ ] Username and email uniqueness enforced on both create and update
- [ ] Password minimum length (8 characters) validated on create and optional update
- [ ] Frontend table, form modals, and role dropdown function correctly
- [ ] Only ADMIN role can access registration management

---

### 2.4 About Statements CRUD

**Description**
The About Management page (`/admin/about`) allows Admins to manage the library's public "About" content displayed on the `/about` page. The content is stored in the `about_statements` table as a single record with `content` (TEXT) and `updatedAt` timestamp. Backend endpoints at `/api/admin/about` support: getting the current about content, creating/updating the about statement. The About page is accessible to all authenticated users via `GET /api/about`.

**User Story**
- As an **Admin**, I want to edit the library's About page content so that visitors and members see up-to-date information about our library.

**Definition**
- Frontend `AboutManagement.jsx` provides a text editor/form for the about statement.
- Backend `AboutController` handles `GET /api/about` (public for authenticated users) and `POST/PUT /api/admin/about` (ADMIN only).
- `About` entity uses `@Builder` with `id`, `content` (TEXT column), `updatedAt`.
- `AboutDTO` transfers data between frontend and backend.
- `updatedAt` is automatically set to `LocalDateTime.now()` on create/update.

**Business Value**
- Dynamic content management without code deployment.
- Keeps the library's public-facing information current and accurate.
- Single admin control point prevents conflicting content edits.

**Acceptance Criteria**

| # | Scenario | Given | When | Then |
|---|----------|-------|------|------|
| 1 | View current about content | About statement exists in DB | Admin navigates to `/admin/about` | Form pre-filled with existing content |
| 2 | Create about statement | No about statement exists | Admin enters content and saves | Statement saved with `updatedAt`; visible on `/about` page |
| 3 | Update about statement | About statement exists | Admin modifies content and saves | Content updated; `updatedAt` refreshed |
| 4 | View about page | About statement exists | Any authenticated user visits `/about` | Current about content is displayed |

**Definition of Done**
- [ ] `GET /api/about` returns current `AboutDTO` for all authenticated users
- [ ] `POST /api/admin/about` creates about statement (ADMIN only)
- [ ] `PUT /api/admin/about` updates existing about statement with new `updatedAt`
- [ ] About content renders correctly on the public `/about` page
- [ ] Text area/form supports multi-line content (TEXT column type)
- [ ] Only ADMIN role can create or modify about statements

---

## Epic 3: Staff Dashboard

### 3.1 Staff Dashboard Layout & KPIs

**Description**
The Staff Dashboard (`/staff`) is the operational landing page for Staff and Admin users. It displays KPI cards for circulation metrics (total borrows, total overdue, total returned) fetched from `GET /api/staff/transactions/counters`, along with quick-access widgets for daily operations. The dashboard uses `DashboardLayout` with a sidebar containing links to all staff modules: Members, Books, Authors, Transactions (Borrow & Return), Fines, Reservations, and Notifications. Transaction counters are returned as `TransactionCountersDTO` from `TransactionService.getCounters()`.

**User Story**
- As a **Staff member**, I want a dashboard showing today's key circulation numbers so that I can prioritize my daily tasks.

**Definition**
- Frontend `StaffDashboard.jsx` renders KPI cards and operational shortcuts within `DashboardLayout`.
- Backend `GET /api/staff/transactions/counters` returns `{ totalBorrows, totalOverdue, totalReturned }` as `TransactionCountersDTO`.
- Counters computed from `transactionRepository` using status-based count queries.
- Sidebar navigation links to all staff modules with appropriate icons (Lucide React).

**Business Value**
- Immediate visibility into circulation health reduces time spent searching for overdue issues.
- Centralized navigation hub improves staff productivity.
- At-a-glance KPIs support quick decision-making for daily operations.

**Acceptance Criteria**

| # | Scenario | Given | When | Then |
|---|----------|-------|------|------|
| 1 | KPI cards display | Transactions exist | Staff navigates to `/staff` | Cards show correct total borrows, overdue, and returned counts |
| 2 | Navigation to modules | Staff is on dashboard | Clicks "Books" in sidebar | Navigates to `/staff/books` |
| 3 | Counter accuracy | 5 ISSUED, 2 OVERDUE, 10 RETURNED transactions | Dashboard loads | Shows Borrows: 5, Overdue: 2, Returned: 10 |
| 4 | Unauthorized access | Member user | Navigates to `/staff` | Redirected to `/access-denied` |

**Definition of Done**
- [ ] `GET /api/staff/transactions/counters` returns accurate `TransactionCountersDTO`
- [ ] Staff dashboard renders KPI cards with correct counter values
- [ ] Sidebar contains links to all staff modules (Members, Books, Authors, Transactions, Fines, Reservations, Notifications)
- [ ] Dashboard loads within 2 seconds
- [ ] Loading states display while API calls are in progress
- [ ] Only STAFF and ADMIN roles can access the dashboard

---

### 3.2 Manage Members (View-only + Borrow History)

**Description**
The Staff Member Management page (`/staff/members`) provides Staff with read-only access to member details and the ability to view any member's borrowing history. Unlike the Admin version, Staff cannot create, edit, delete, or toggle member status. The page lists all members with their status (Active/Inactive), contact details, and membership date. Staff can click on a member to view their complete borrowing history via `GET /api/transactions/user/{userId}`. Backend returns `List<MemberDTO>` from `StaffController.getAllMembers()` to ensure consistent JSON serialization with `@JsonProperty("isActive")`.

**User Story**
- As a **Staff member**, I want to look up member details and their borrowing history so that I can assist them at the front desk.
- As a **Staff member**, I want to see whether a member is Active or Inactive so that I know if they're eligible to borrow.

**Definition**
- Frontend `StaffMemberManagement.jsx` (Staff) renders a searchable, read-only member table.
- Backend `StaffController.getAllMembers()` at `GET /api/staff/members` returns `List<MemberDTO>` with correct `isActive` field.
- Borrowing history accessible via a detail view or action button linking to `GET /api/transactions/user/{userId}`.
- Status badges: green (#10b981) for Active, red (#f43f5e) for Inactive.
- Search filters by member name, username, or email.

**Business Value**
- Front-desk staff can quickly verify member eligibility and review borrowing patterns.
- Read-only access prevents accidental modifications to member accounts by operational staff.
- Consistent status display across admin and staff views eliminates confusion.

**Acceptance Criteria**

| # | Scenario | Given | When | Then |
|---|----------|-------|------|------|
| 1 | List all members | Members exist | Staff navigates to `/staff/members` | Table shows all members with correct Active/Inactive status |
| 2 | View borrowing history | Member has 3 past borrows | Staff clicks "View History" on a member | Borrowing history table shows all 3 transactions |
| 3 | Search members | Member "Kamal" exists | Staff searches "Kamal" | Only matching member(s) displayed |
| 4 | No edit/delete actions | Staff views member table | Looks for edit/delete buttons | No CRUD actions available (view-only) |
| 5 | Status display consistency | Member is Active in database | Both Admin and Staff view the member | Both show green "Active" badge |

**Definition of Done**
- [ ] `GET /api/staff/members` returns `List<MemberDTO>` with correct `isActive` serialization
- [ ] Member table displays name, username, email, phone, membership date, and status
- [ ] Borrowing history view loads transactions for selected member
- [ ] Search filters members by name, username, email
- [ ] Status badges match admin view (green Active, red Inactive)
- [ ] No create, edit, delete, or toggle actions available to Staff
- [ ] Only STAFF and ADMIN roles can access this page

---

### 3.3 Book CRUD (with Cover Picture)

**Description**
The Book Management page (`/staff/books`) provides full CRUD for the library's book inventory. Books include: title, ISBN (unique), author (FK to `Author` entity), category (Dewey Decimal Classification with 10 predefined categories: 000–900), publisher, description, cover image (file upload to `uploads/books/`), total copies, available copies, and date received. The category selection uses an interactive grid with icons. Cover images are uploaded via `multipart/form-data` and stored as file paths. Backend endpoints at `/api/staff/books` support: listing all books, creating, updating, and deleting books. The `Book` entity uses `@Builder.Default` for `availableCopies` and `totalCopies`.

**User Story**
- As a **Staff member**, I want to add new books with cover images and Dewey Decimal categories so that the inventory is well-organized and visually appealing.
- As a **Staff member**, I want to edit book details and manage copy counts so that the catalog stays accurate.

**Definition**
- Frontend `BookManagement.jsx` renders a book table with add/edit modals featuring: text fields, Dewey Decimal category grid, file upload for cover image, and numeric copy fields.
- Backend `BookController` at `/api/staff/books` with `@PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")`.
- Cover image upload saves file to `uploads/books/` directory with UUID filename; stores path in `book.coverImage`.
- Category dropdown/grid uses 10 DDC categories (000-Generalities through 900-Geology and History).
- ISBN uniqueness enforced at database level (`@Column(unique = true)`).
- `availableCopies` and `totalCopies` managed with `@Builder.Default` annotations.

**Business Value**
- Professional catalog with cover images improves member browsing experience.
- DDC categorization follows international library standards for systematic organization.
- Copy tracking (total vs available) enables real-time availability checking.

**Acceptance Criteria**

| # | Scenario | Given | When | Then |
|---|----------|-------|------|------|
| 1 | List all books | Books exist in database | Staff navigates to `/staff/books` | Table shows all books with title, author, category, copies, cover thumbnail |
| 2 | Add new book | Staff clicks "Add Book" | Fills form with title, ISBN, author, DDC category, copies; uploads cover image | Book created; cover image saved to `uploads/books/` |
| 3 | Category grid selection | Staff is adding a book | Clicks "500 – Natural Science" in grid | Category selected with visual confirmation |
| 4 | Edit book details | Book exists | Staff edits title and available copies | Changes persisted; reflected in table |
| 5 | Duplicate ISBN rejected | ISBN "978-1234" already exists | Staff creates book with same ISBN | Database constraint or validation error returned |
| 6 | Delete book | Book exists | Staff deletes book | Book removed from database |
| 7 | Cover image display | Book has uploaded cover | Table/form renders | Cover thumbnail displays correctly |

**Definition of Done**
- [ ] `GET /api/staff/books` returns all books with author info and cover image paths
- [ ] `POST /api/staff/books` creates book with all fields including cover image upload
- [ ] `PUT /api/staff/books/{id}` updates book details
- [ ] `DELETE /api/staff/books/{id}` removes book from database
- [ ] Cover image upload saves to `uploads/books/` with UUID filename
- [ ] Interactive Dewey Decimal category grid with 10 categories and icons
- [ ] ISBN uniqueness enforced
- [ ] `availableCopies` and `totalCopies` fields function correctly
- [ ] Only STAFF and ADMIN roles can manage books

---

### 3.4 Author CRUD

**Description**
The Author Management page (`/staff/authors`) provides full CRUD for author records. Each author has a name (required, unique validation) and an optional bio. Authors are linked to books via a `@ManyToOne` relationship on the `Book` entity (FK `author_id`). Backend endpoints at `/api/staff/authors` support: listing all authors (with their associated books count), creating, updating, and deleting authors. The `AuthorService` handles DTO conversion and validates that an author cannot be deleted if they have associated books.

**User Story**
- As a **Staff member**, I want to manage author records so that books can be properly attributed to their authors.
- As a **Staff member**, I want to see how many books each author has so that I can make informed decisions before modifying author records.

**Definition**
- Frontend `AuthorManagement.jsx` renders an author table with name, bio, and book count columns.
- Backend `AuthorController` / `AuthorService` at `/api/staff/authors` with STAFF/ADMIN authorization.
- `Author` entity has `@OneToMany(mappedBy = "author")` relationship to `Book`.
- `AuthorDTO` includes `id`, `name`, `bio`, and `books` (list or count).
- Create/update validates name is not empty; delete checks for associated books.

**Business Value**
- Structured author data ensures consistent book attribution across the catalog.
- Preventing deletion of authors with linked books maintains data integrity.
- Author bios enrich the catalog with contextual information for members.

**Acceptance Criteria**

| # | Scenario | Given | When | Then |
|---|----------|-------|------|------|
| 1 | List all authors | Authors exist in database | Staff navigates to `/staff/authors` | Table shows name, bio, and book count for each author |
| 2 | Create author | Staff clicks "Add Author" | Enters name "J.K. Rowling" and bio; submits | Author created; appears in table |
| 3 | Edit author | Author exists | Staff changes name or bio | Changes persisted |
| 4 | Delete author without books | Author has 0 linked books | Staff deletes author | Author removed successfully |
| 5 | Delete author with books | Author has 3 linked books | Staff attempts delete | System prevents deletion; shows error message |

**Definition of Done**
- [ ] `GET /api/staff/authors` returns all authors with book associations
- [ ] `POST /api/staff/authors` creates author with name and bio
- [ ] `PUT /api/staff/authors/{id}` updates author details
- [ ] `DELETE /api/staff/authors/{id}` removes author (blocked if books are linked)
- [ ] Author name validation prevents empty values
- [ ] Frontend table, add/edit modals function correctly
- [ ] Only STAFF and ADMIN roles can manage authors

---

### 3.5 Borrow & Return CRUD with Filtering

**Description**
The Transaction Management page (`/staff/transactions`) is the core circulation module for Staff. It supports: issuing books to members (`POST /api/staff/transactions/issue?userId=X&bookId=Y`), returning books (`PUT /api/staff/transactions/return/{issueId}`), updating transaction status/condition (`PUT /api/staff/transactions/{id}/update`), and filtering transactions by status (`GET /api/staff/transactions?status=All|Issue|Overdue|Return`). The system enforces a strict **one-book-per-member** policy: a member can only have one active (ISSUED or OVERDUE) transaction at a time. Available copies are decremented on issue and incremented on return. Transaction statuses: ISSUED (Borrowed), OVERDUE (auto-detected), RETURNED.

**User Story**
- As a **Staff member**, I want to issue a book to a member by selecting the member and book so that the borrowing is recorded.
- As a **Staff member**, I want to filter transactions by status so that I can quickly find all overdue books.
- As a **Staff member**, I want to be prevented from issuing a second book to a member who already has one so that library policy is enforced.

**Definition**
- Frontend `TransactionManagement.jsx` renders a filterable transaction table with issue/return modals.
- Issue flow: Staff selects member (userId) and book (bookId); backend validates member exists, book exists, available copies > 0, member has no active transaction; then creates ISSUED transaction with 14-day due date.
- Return flow: Staff enters return date (required), selects book condition (Good/Fair/Poor/Damaged, required), and optionally adds condition notes; backend calculates fine, updates status to RETURNED, increments available copies.
- Status filter: dropdown with All, Issue (ISSUED), Overdue (OVERDUE), Return (RETURNED).
- `TransactionDTO` includes: member name, book title, issue date, due date, return date, status, fine amount, book condition.

**Business Value**
- Automated enforcement of the one-book-per-member policy prevents policy violations.
- Real-time inventory synchronization ensures accurate availability data.
- Status filtering enables efficient daily circulation workflows.

**Acceptance Criteria**

| # | Scenario | Given | When | Then |
|---|----------|-------|------|------|
| 1 | Issue book successfully | Member has no active borrows; book has copies | Staff issues book | Transaction created as ISSUED; due date = today + 14 days; available copies decremented |
| 2 | Block second borrow | Member already has ISSUED book | Staff tries to issue another book | Returns error: "Member can borrow only one book at a time" |
| 3 | Block when no copies | Book has 0 available copies | Staff tries to issue | Returns error: "Book is currently unavailable" |
| 4 | Return book | ISSUED transaction exists | Staff enters return date + condition | Status → RETURNED; fine calculated; available copies incremented |
| 5 | Filter by Overdue | 3 OVERDUE transactions exist | Staff selects "Overdue" filter | Only 3 overdue transactions displayed |
| 6 | Update transaction | Transaction exists | Staff updates status or condition | Changes persisted |
| 7 | Auto-detect overdue | ISSUED transaction with due date in the past | System runs `updateOverdueTransactions()` | Status changed from ISSUED to OVERDUE |

**Definition of Done**
- [ ] `POST /api/staff/transactions/issue` creates ISSUED transaction with full validation chain
- [ ] `PUT /api/staff/transactions/return/{id}` processes return with mandatory condition
- [ ] `PUT /api/staff/transactions/{id}/update` allows status/condition editing
- [ ] `GET /api/staff/transactions?status=` filters by status
- [ ] One-book-per-member policy enforced (checks ISSUED and OVERDUE statuses)
- [ ] Available copies decremented on issue, incremented on return
- [ ] 14-day borrowing period automatically calculated
- [ ] Overdue transactions auto-detected and status updated
- [ ] Only STAFF and ADMIN roles can manage transactions

---

### 3.6 Automatic Fine Calculation

**Description**
When a book is returned after its due date, the system automatically calculates a late-return fine using `FineCalculatorService`. The fine rate is **Rs. 5.00 per day** overdue. The calculation uses `ChronoUnit.DAYS.between(dueDate, returnDate)` and ensures the fine is never negative (validation: `if (fineAmount < 0) fineAmount = 0.0`). A `Fine` entity record is always created on return (even if amount is Rs. 0.00 with status `NONE`). Fine statuses: `UNPAID` (amount > 0), `PAID` (settled by member), `NONE` (no fine, returned on time).

**User Story**
- As a **Staff member**, I want fines to be calculated automatically when I process a book return so that I don't have to compute them manually.
- As a **Member**, I want to see my fine amount in my borrowing history so that I know what I owe.

**Definition**
- `FineCalculatorService.calculateFine(dueDate, returnDate)` returns `double` fine amount at Rs. 5/day.
- Called within `TransactionService.returnBook()` after setting return date.
- `Fine` entity created with: transaction FK, amount, return date, status (UNPAID if > 0, NONE if 0).
- Fine amount stored in both `Transaction.fineAmount` and `Fine.amount`.
- Negative fine prevention: `if (fineAmount < 0) fineAmount = 0.0`.

**Business Value**
- Eliminates human error in fine computation.
- Consistent, transparent fine policy builds member trust.
- Automatic fine record creation streamlines the return process.

**Acceptance Criteria**

| # | Scenario | Given | When | Then |
|---|----------|-------|------|------|
| 1 | On-time return | Due: Jan 15, Returned: Jan 14 | Book returned | Fine = Rs. 0.00; Fine status = NONE |
| 2 | Late return (3 days) | Due: Jan 15, Returned: Jan 18 | Book returned | Fine = Rs. 15.00 (3 × 5); Fine status = UNPAID |
| 3 | Same-day return | Due: Jan 15, Returned: Jan 15 | Book returned | Fine = Rs. 0.00; Fine status = NONE |
| 4 | Fine never negative | Any return date scenario | Return processed | Fine is always ≥ 0 |
| 5 | Fine record created | Any return | Return processed | `Fine` entity persisted with transaction reference |

**Definition of Done**
- [ ] `FineCalculatorService.calculateFine()` computes Rs. 5/day correctly
- [ ] Fine amount stored in `Transaction.fineAmount` and `Fine` entity
- [ ] Fine status set to UNPAID (amount > 0) or NONE (amount = 0)
- [ ] Negative fine validation in place (`fineAmount >= 0`)
- [ ] Fine visible in transaction DTO and member borrowing history
- [ ] Unit tests pass for `FineCalculatorService` covering on-time, late, and edge cases

---

### 3.7 Fines CRUD

**Description**
The Fine Management page (`/staff/fines`) allows Staff to view, filter, update, and manage all fine records. Endpoints at `/api/staff/fines` support: listing fines with status filter (`?status=ALL|UNPAID|PAID|NONE`), getting fine statistics (`GET /stats`), updating fine details (payment date, status), marking fines as paid (`PUT /{fineId}/pay`), and soft-deleting fines. `FineStatsDTO` provides aggregate data: total fines, unpaid count, paid count, total collected amount.

**User Story**
- As a **Staff member**, I want to see all unpaid fines so that I can follow up with members who owe money.
- As a **Staff member**, I want to mark a fine as paid when a member settles their dues so that records stay accurate.

**Definition**
- Frontend `FineManagement.jsx` renders a filterable fine table with member name, book title, fine amount, status, return date, and payment date.
- Backend `FineController` at `/api/staff/fines` with STAFF/ADMIN authorization.
- Status filter: ALL (default), UNPAID, PAID, NONE.
- `PUT /api/staff/fines/{fineId}/pay` sets status to PAID and records payment date.
- `GET /api/staff/fines/stats` returns aggregate statistics.
- `FineDTO` includes transaction details, member info, and book info.

**Business Value**
- Financial accountability with complete fine tracking and payment records.
- Statistics dashboard helps management assess fine revenue and delinquency rates.
- Status filtering enables efficient collection workflows.

**Acceptance Criteria**

| # | Scenario | Given | When | Then |
|---|----------|-------|------|------|
| 1 | List all fines | Fines exist | Staff navigates to `/staff/fines` | Table shows all fines with member, book, amount, status |
| 2 | Filter unpaid fines | 5 UNPAID fines exist | Staff selects "UNPAID" filter | Only 5 unpaid fines displayed |
| 3 | View fine statistics | Various fines exist | Stats widget loads | Shows total fines, unpaid count, paid count, total collected |
| 4 | Mark fine as paid | UNPAID fine exists | Staff clicks "Mark as Paid" | Status → PAID; payment date set to today |
| 5 | Update fine manually | Fine exists | Staff updates status or payment date | Changes persisted |
| 6 | Delete fine | Fine exists | Staff deletes fine | Fine soft-deleted |

**Definition of Done**
- [ ] `GET /api/staff/fines?status=` returns filtered fine list with `FineDTO`
- [ ] `GET /api/staff/fines/stats` returns accurate `FineStatsDTO`
- [ ] `PUT /api/staff/fines/{id}/pay` marks fine as PAID with payment date
- [ ] `PUT /api/staff/fines/{id}` updates fine details
- [ ] `DELETE /api/staff/fines/{id}` soft-deletes fine
- [ ] Frontend table with status filter, stats cards, and action buttons
- [ ] Only STAFF and ADMIN roles can manage fines

---

### 3.8 Return with Book Condition

**Description**
When processing a book return, Staff must record the physical condition of the returned book. The system supports four condition levels via the `BookCondition` enum: **GOOD** (no damage), **FAIR** (minor wear), **POOR** (noticeable damage), and **DAMAGED** (significant damage). Book condition is **mandatory** on return; Staff must also optionally add condition notes (free text). The condition and notes are stored on the `Transaction` entity (`bookCondition`, `conditionNotes` columns) and visible in transaction history.

**User Story**
- As a **Staff member**, I want to record the condition of a returned book so that the library tracks wear and damage over time.
- As a **Staff member**, I want to add notes about specific damage so that there's a record for potential fine disputes.

**Definition**
- Return form includes a mandatory dropdown for Book Condition (GOOD, FAIR, POOR, DAMAGED) and an optional text area for condition notes.
- Backend `PUT /api/staff/transactions/return/{issueId}` requires `bookCondition` parameter; returns 400 if empty.
- `BookCondition` enum values: `GOOD`, `FAIR`, `POOR`, `DAMAGED`.
- Condition and notes persisted on `Transaction` entity and included in `TransactionDTO`.
- Condition visible in staff transaction table and member borrowing history.

**Business Value**
- Systematic condition tracking enables proactive book maintenance and replacement planning.
- Condition records provide evidence for damage-related fine disputes.
- Mandatory condition entry ensures consistent data quality across all returns.

**Acceptance Criteria**

| # | Scenario | Given | When | Then |
|---|----------|-------|------|------|
| 1 | Return with GOOD condition | ISSUED transaction exists | Staff selects "Good" and submits return | Transaction updated with `bookCondition=GOOD` |
| 2 | Return with DAMAGED + notes | ISSUED transaction exists | Staff selects "Damaged", adds "Pages torn, cover detached" | Condition and notes persisted |
| 3 | Condition is mandatory | ISSUED transaction exists | Staff tries to return without selecting condition | Returns 400: "Book Condition is required" |
| 4 | Condition in history | Returned transaction with FAIR condition | Staff/Member views history | Condition column shows "FAIR" |

**Definition of Done**
- [ ] Return form has mandatory Book Condition dropdown (GOOD, FAIR, POOR, DAMAGED)
- [ ] Return form has optional Condition Notes text area
- [ ] Backend validates condition is not null/empty on return
- [ ] Condition and notes persisted on `Transaction` entity
- [ ] `TransactionDTO` includes `bookCondition` and `conditionNotes` fields
- [ ] Condition visible in both staff and member transaction views

---

### 3.9 Reservation (View-only for Staff)

**Description**
The Reservation Management page (`/staff/reservations`) provides Staff with a read-only view of all book reservations made by members, plus the ability to manage reservation statuses. Members create reservations via `POST /api/reservations`; Staff can view all reservations (`GET /api/staff/reservations`), acknowledge them (`PATCH /{id}/acknowledge`), and change status (`PATCH /{id}/status`). Supported statuses: **PENDING** (awaiting review), **APPROVED** (triggers auto-issuing of the book), **REJECTED** (denied by staff), **AVAILABLE** (book ready for pickup), **UNAVAILABLE** (book not available), **COMPLETED** (fulfilled), **CANCELLED** (expired or member-cancelled). PENDING reservations auto-expire after 3 days via a scheduled hourly task.

**User Story**
- As a **Staff member**, I want to see all pending reservations so that I can process them in order.
- As a **Staff member**, I want to approve a reservation which automatically issues the book so that the workflow is streamlined.
- As a **Staff member**, I want to reject invalid reservations so that the queue stays clean.

**Definition**
- Frontend `ReservationManagement.jsx` displays reservations table with member name, book title, reservation date, and status.
- Staff can change status via `PATCH /api/staff/reservations/{id}/status` with payload `{ "status": "APPROVED" }`.
- Setting status to APPROVED auto-issues the book (creates ISSUED transaction, enforces one-book rule).
- COMPLETED and CANCELLED reservations cannot be modified.
- Auto-expiry: PENDING reservations older than 3 days are automatically set to CANCELLED by a scheduled task.

**Business Value**
- Streamlined reservation-to-borrow workflow reduces manual data entry.
- Auto-expiry prevents stale reservations from blocking book availability.
- Status transparency keeps both Staff and Members informed of reservation progress.

**Acceptance Criteria**

| # | Scenario | Given | When | Then |
|---|----------|-------|------|------|
| 1 | View all reservations | Reservations exist | Staff navigates to `/staff/reservations` | Table shows all reservations with status badges |
| 2 | Approve reservation | PENDING reservation exists | Staff sets status to APPROVED | Book auto-issued (ISSUED transaction created); one-book rule enforced |
| 3 | Approve blocked by one-book rule | Member already has ISSUED book | Staff approves reservation | Returns error about existing active borrow |
| 4 | Reject reservation | PENDING reservation exists | Staff sets status to REJECTED | Reservation marked as REJECTED |
| 5 | Auto-expire stale reservations | PENDING reservation older than 3 days | Scheduled task runs (hourly) | Reservation status → CANCELLED |
| 6 | Cannot modify COMPLETED | Reservation is COMPLETED | Staff tries to change status | Returns error: cannot modify completed/cancelled |

**Definition of Done**
- [ ] `GET /api/staff/reservations` returns all reservations with `ReservationDTO`
- [ ] `PATCH /api/staff/reservations/{id}/status` changes status with validation
- [ ] APPROVED status triggers auto-issuing (creates borrow transaction)
- [ ] One-book-per-member rule enforced during auto-issue
- [ ] COMPLETED and CANCELLED reservations are immutable
- [ ] Auto-expiry scheduled task cancels PENDING reservations after 3 days
- [ ] Frontend table displays all reservation details and status
- [ ] Only STAFF and ADMIN roles can manage reservation statuses

---

### 3.10 Notification CRUD

**Description**
The Notification Management page (`/staff/notifications`) allows Staff to create, update, and delete broadcast notifications sent to all library members. Notifications have a title, message, and type. Members see their own notifications at `/member/notifications` (filtered by user ID or broadcast). Staff/Admin see all notifications. Endpoints: `GET /api/notifications` (search supported), `POST /api/notifications`, `PUT /api/notifications/{id}`, `DELETE /api/notifications/{id}`. Members can mark notifications as read (`PUT /api/notifications/{id}/read`) or mark all as read (`PUT /api/notifications/read-all`). Notification read status is tracked per-user for members.

**User Story**
- As a **Staff member**, I want to broadcast announcements (e.g., holiday closures, new arrivals) to all members so that they stay informed.
- As a **Member**, I want to see notifications addressed to me and mark them as read so that I can track what I've seen.

**Definition**
- Frontend `NotificationManagement.jsx` (Staff) provides CRUD interface for broadcast notifications.
- Frontend `MemberNotifications.jsx` (Member) displays received notifications with read/unread indicators.
- Backend `NotificationController` at `/api/notifications` with role-based access:
  - CREATE/UPDATE/DELETE: STAFF or ADMIN only
  - READ: All authenticated roles
  - Mark as read: MEMBER (per-user), STAFF/ADMIN (global)
- `NotificationDTO` includes: id, title, message, type, createdAt, isRead, userId.
- Search supported via `?search=` query parameter.

**Business Value**
- Direct communication channel from library staff to members without external tools.
- Broadcast capability enables efficient mass announcements.
- Read tracking ensures members can manage notification overload.

**Acceptance Criteria**

| # | Scenario | Given | When | Then |
|---|----------|-------|------|------|
| 1 | Create broadcast notification | Staff fills title + message | Clicks create | Notification created; visible to all members |
| 2 | Member views notifications | 3 notifications exist (1 targeted to member) | Member navigates to `/member/notifications` | Sees broadcast + targeted notifications |
| 3 | Mark as read | Unread notification exists | Member clicks "Mark as Read" | `isRead` updated for that user; badge disappears |
| 4 | Mark all as read | 5 unread notifications | Member clicks "Mark All as Read" | All user's notifications marked as read |
| 5 | Update notification | Notification exists | Staff edits title/message | Changes persisted |
| 6 | Delete notification | Notification exists | Staff deletes | Notification removed from all views |
| 7 | Search notifications | Multiple notifications exist | Staff types in search box | Filtered results displayed |

**Definition of Done**
- [ ] `POST /api/notifications` creates broadcast notification (STAFF/ADMIN only)
- [ ] `PUT /api/notifications/{id}` updates notification (STAFF/ADMIN only)
- [ ] `DELETE /api/notifications/{id}` deletes notification (STAFF/ADMIN only)
- [ ] `GET /api/notifications` returns notifications (role-aware: all for staff, user-specific for members)
- [ ] `PUT /api/notifications/{id}/read` marks notification as read per-user
- [ ] `PUT /api/notifications/read-all` marks all as read (role-aware)
- [ ] Search functionality works with `?search=` parameter
- [ ] Frontend CRUD interface for Staff; read-only + mark-read for Members
- [ ] Notification types and read/unread indicators render correctly
