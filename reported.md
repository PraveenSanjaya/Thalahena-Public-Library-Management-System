# Library Management System (LMS) — Project Report

## Talahena Public Library, Negombo

**OOAD Module Assignment — SLIIT**

---

## 1. Introduction

The **Talahena Public Library Management System (LMS)** is a full-stack web application developed as part of the Object-Oriented Analysis and Design (OOAD) module assignment at SLIIT. The system was designed and built for the **Talahena Public Library**, located in Thalahena, Negombo, operating under the **Negombo Municipal Council**.

This project applies object-oriented principles, agile development practices, and modern software engineering patterns to deliver a production-ready system that replaces the library's existing manual, paper-based operations with a centralized digital platform.

> **Screenshot Placeholder** — *Insert a full-page screenshot of the application landing page / login screen here.*

---

### Client Details

| Field | Details |
|---|---|
| **Client Name** | Talahena Public Library |
| **Location** | Thalahena, Negombo |
| **Governing Body** | Negombo Municipal Council |
| **Contact Person** | Mrs. Sheromi Perera (Head Librarian) |
| **Contact Phone** | 076-5272541 |
| **Municipal Commissioner** | Ms. Nuwani Sudusinghe (S.L.A.S. 01) |
| **Project Supervisor** | Mr. Eranda (appointed by Municipal Commissioner, Contact: 077-5834583) |
| **Lecturer-in-Charge (OOAD)** | Mr. Jeewaka |

### Client Background

The Talahena Public Library serves as an essential educational and recreational resource for the local community. Established to provide accessible reading materials and learning resources, the library currently maintains a collection of several thousand books across various genres and subjects. The library serves diverse demographics including students, working professionals, researchers, and general readers.

### Current Operational Challenges

The library currently operates using manual, paper-based systems for all its core functions. This traditional approach has led to numerous operational inefficiencies including:

- Time-consuming book searches through physical card catalogues
- Difficulty in tracking borrowed materials and overdue returns
- Manual fine calculations prone to human error
- Challenges in generating timely summary reports for management decisions
- Staff spending considerable time on administrative tasks that could be automated
- Reduced time available for member services and library development activities

### Evidence of Client Engagement

Initial meetings with the client were conducted to understand their requirements, operational workflows, and pain points. Documentation of these meetings, including email correspondence, meeting minutes, and requirement validation documents, is provided in the **Appendix** section of this report.

> **Screenshot Placeholder** — *Insert screenshot of email correspondence with Mr. Jeewaka (Lecturer-in-Charge) regarding project extension request. Subject: "Request for Official Support Letter – Library Management System | IT24101848"*

**Email Summary — Project Extension Context:**

A personal letter was submitted to the Municipal Commissioner on January 25, 2026, requesting an extension until July 10, 2026, to complete and deliver the final system. This request was rejected. As the system was developed as part of the OOAD module assignment, the Municipal Council requested that an official letter be submitted through the SLIIT campus. The email to Mr. Jeewaka requests an official letter from SLIIT to Ms. Nuwani Sudusinghe (Municipal Commissioner) explaining why the project could not be completed on time due to academic commitments, and proposing a new confirmed deadline for delivery.

---

## 2. Background & Problem Statement — User Research Summary

### 2.1 Research Approach

User research was conducted through direct engagement with library staff and observation of daily operations at the Talahena Public Library. The following methods were employed:

- **On-site observation** of front-desk operations, book issuance, and return workflows
- **Interviews** with Mrs. Sheromi Perera (Head Librarian) and library support staff
- **Document analysis** of existing paper-based registers, fine receipt books, and member ledgers
- **Requirement validation sessions** with the project supervisor Mr. Eranda

### 2.2 Key Findings

| Problem Area | Current State | Impact |
|---|---|---|
| **Book Cataloguing** | Manual card catalogue; no digital index | Staff spend 10–15 minutes per book search |
| **Member Registration** | Paper ledger with handwritten entries | Difficult to verify membership status or search records |
| **Book Issuance & Returns** | Logbook entries with manual date tracking | Frequent errors in due dates; no automated overdue alerts |
| **Fine Management** | Handwritten fine receipts; cash-only | Calculation errors; no audit trail; disputes unresolved |
| **Reporting** | No management reports generated | Library performance invisible to Municipal Council |
| **Reservations** | Not supported | Members cannot reserve books in advance |
| **Notifications** | None (no digital communication channel) | Members unaware of overdue books or new arrivals |
| **Access Control** | Single staff key to physical records | No role separation; no accountability |

### 2.3 Problem Statement

> The Talahena Public Library lacks a unified digital system to manage its core operations — cataloguing, membership, circulation, fines, and reporting. The continued reliance on manual processes results in operational inefficiencies, data inconsistencies, poor member experience, and an inability to generate actionable insights for library management and the Negombo Municipal Council.

---

## 3. Proposed System Scope

### 3.1 Project Scope

The Library Management System (LMS) is a role-based, full-stack web application that digitizes and automates the following core library functions:

| Module | Scope |
|---|---|
| **Authentication & Authorization** | Secure login (username/password + OTP-based passwordless login), JWT-based session management, role-based access control (Admin, Staff, Member) |
| **Admin Dashboard** | High-level KPIs, user demographics, member CRUD, staff/admin registration management, about page content management |
| **Staff Dashboard** | Operational KPIs, book catalogue CRUD (with cover images), author CRUD, book issuance and returns, fine management, reservation tracking, notification broadcasting |
| **Member Portal** | Self-registration, book browsing, reservation requests, personal borrow history, fine status, notifications, profile management, feedback submission |
| **Circulation Management** | Book issue/return workflows, automatic fine calculation on overdue returns, book condition tracking (Good, Fair, Poor, Damaged) |
| **Reporting & Analytics** | Admin and staff dashboards with category distribution (Dewey Decimal Classification), top borrowed books, fine statistics, member demographics |

### 3.2 Objectives

1. **Eliminate manual record-keeping** by providing a centralized digital catalogue for all library books with metadata including pages, Dewey Decimal codes, municipal references, and library reference numbers.
2. **Automate circulation workflows** including book issuance, return processing, overdue fine calculation, and book condition assessment.
3. **Implement role-based access control** ensuring Admins manage users and configuration, Staff handle daily operations, and Members access their own records securely.
4. **Improve member experience** through self-service registration, online book reservations, real-time notifications, and personal dashboards.
5. **Provide actionable analytics** to library management and the Negombo Municipal Council through visual dashboards with charts and KPI widgets.
6. **Ensure data integrity and security** using JWT authentication, IDOR protection, input validation, and secure password storage (BCrypt hashing).

---

## 4. System Users

The system defines three distinct user roles, each with specific permissions and access levels:

### 4.1 Admin

| Attribute | Description |
|---|---|
| **Role** | `ADMIN` |
| **Primary Responsibilities** | System administration, user management, content management |
| **Key Permissions** | Full CRUD on members, staff/admin registrations, about statements; access to admin analytics dashboard; all staff-level permissions |
| **Typical User** | Head Librarian (Mrs. Sheromi Perera) or designated administrator |

### 4.2 Staff

| Attribute | Description |
|---|---|
| **Role** | `STAFF` |
| **Primary Responsibilities** | Daily library operations — catalogue management, book circulation, fine processing |
| **Key Permissions** | Book CRUD, author CRUD, transaction issue/return, fine management, reservation acknowledgement, notification broadcasting, view members and borrow history |
| **Typical User** | Library front-desk staff |

### 4.3 Member

| Attribute | Description |
|---|---|
| **Role** | `MEMBER` |
| **Primary Responsibilities** | Browsing catalogue, reserving books, managing own profile |
| **Key Permissions** | Self-registration, view books, create reservations, view own borrow history and fines, submit feedback, manage profile, receive notifications |
| **Typical User** | Students, working professionals, researchers, general readers from the local community |

> **Screenshot Placeholder** — *Insert screenshot showing the role-based navigation sidebar differences between Admin, Staff, and Member accounts.*

---

## 5. Identified Requirements

### 5.1 Functional Requirements

| ID | Requirement | Module | Priority |
|---|---|---|---|
| FR-01 | System shall support username/password login and OTP-based passwordless login | Authentication | High |
| FR-02 | System shall allow members to self-register via public registration form | Authentication | High |
| FR-03 | System shall enforce role-based access control for Admin, Staff, and Member roles | Authorization | High |
| FR-04 | Admin shall be able to create, read, update, and delete member records | Admin Dashboard | High |
| FR-05 | Admin shall be able to activate or deactivate member accounts | Admin Dashboard | High |
| FR-06 | Admin shall be able to search members by name, email, or username | Admin Dashboard | Medium |
| FR-07 | Admin shall be able to create, update, and delete staff and admin user accounts | Admin Dashboard | High |
| FR-08 | Admin shall be able to manage about page statements (CRUD) | Admin Dashboard | Low |
| FR-09 | Staff shall be able to create, read, update, and delete books with cover images | Staff Dashboard | High |
| FR-10 | Staff shall be able to create, read, update, and delete authors | Staff Dashboard | High |
| FR-11 | Staff shall be able to issue books to members and process returns | Circulation | High |
| FR-12 | System shall automatically calculate fines on overdue book returns | Circulation | High |
| FR-13 | Staff shall be able to record book condition (Good, Fair, Poor, Damaged) on return | Circulation | Medium |
| FR-14 | Staff shall be able to view, filter, and manage all transactions | Circulation | High |
| FR-15 | Staff shall be able to view and manage fines (mark as paid, view statistics) | Circulation | High |
| FR-16 | Members shall be able to create book reservations | Reservations | Medium |
| FR-17 | Staff shall be able to acknowledge and approve/reject reservations | Reservations | Medium |
| FR-18 | Approved reservations shall automatically trigger book issuance | Reservations | Medium |
| FR-19 | Staff/Admin shall be able to create and broadcast notifications | Notifications | Medium |
| FR-20 | Members shall receive and read their notifications | Notifications | Medium |
| FR-21 | Members shall be able to submit, update, and delete feedback | Feedback | Low |
| FR-22 | All authenticated users shall be able to view and update their profile | Profile | Medium |
| FR-23 | All authenticated users shall be able to change their password | Profile | Medium |
| FR-24 | All authenticated users shall be able to upload a profile picture | Profile | Low |

### 5.2 Non-Functional Requirements

| ID | Requirement | Category |
|---|---|---|
| NFR-01 | All API endpoints shall be secured using JWT bearer token authentication | Security |
| NFR-02 | Passwords shall be hashed using BCrypt before storage | Security |
| NFR-03 | Member self-access endpoints shall include IDOR protection (SpEL-based principal validation) | Security |
| NFR-04 | System shall respond to all API requests within 2 seconds under normal load | Performance |
| NFR-05 | Frontend shall be responsive and usable on desktop, tablet, and mobile devices | Usability |
| NFR-06 | System shall use Dewey Decimal Classification for book categorization | Domain Compliance |
| NFR-07 | Database schema shall support soft deletes for fines and related entities | Data Integrity |
| NFR-08 | File uploads (book covers, profile pictures) shall be stored with UUID-based filenames to prevent collisions | Reliability |
| NFR-09 | System shall follow SOLID principles in backend architecture (SRP, OCP, DIP documented in controllers) | Maintainability |
| NFR-10 | System shall provide meaningful error messages and validation feedback to users | Usability |

---

## 6. User Stories

---

### Epic 1: Authentication & Access Control

> **Epic Description:** This epic covers all authentication mechanisms and authorization controls in the LMS. It ensures that users can securely log in using traditional credentials or a passwordless OTP flow, and that the system enforces strict role-based permissions across all modules.

---

#### US 1.1 — Login Page (UI + API)

**Description:**
The login page is the primary entry point for all users. It provides a clean, accessible interface for username/password authentication. Upon successful login, the backend issues a JWT token that the frontend stores and attaches to all subsequent API requests.

**User Story:**
> *As a* registered user (Admin, Staff, or Member),
> *I want to* log in using my username and password,
> *So that* I can access the system securely with my assigned role.

**Business Value:**
Secure authentication is the foundation of the system. It protects sensitive member data, prevents unauthorized access to administrative functions, and ensures accountability for all operations performed within the system.

**Acceptance Criteria:**

| # | Scenario | Given | When | Then |
|---|---|---|---|---|
| AC-1.1.1 | Successful login | A registered user with valid credentials exists | The user submits correct username and password | A JWT token is returned; user is redirected to their role-appropriate dashboard |
| AC-1.1.2 | Invalid credentials | A user exists in the system | The user submits an incorrect password | The system returns a 401 Unauthorized response with an error message |
| AC-1.1.3 | Non-existent user | No user with the given username exists | The user submits a username that does not exist | The system returns a 401 Unauthorized response |
| AC-1.1.4 | Empty fields | The login page is displayed | The user submits without entering username or password | Frontend validation displays "Username and password are required" |

**Definition of Done:**
- [ ] POST `/api/auth/login` endpoint returns JWT token on valid credentials
- [ ] Frontend login form validates inputs before submission
- [ ] Invalid login displays appropriate error message without revealing which field is incorrect
- [ ] JWT token is stored in localStorage and attached to subsequent requests via Axios interceptor
- [ ] Login page is accessible and responsive on desktop and mobile
- [ ] Unit tests pass for AuthService login logic
- [ ] Postman collection includes good path and negative path test cases

> **Screenshot Placeholder** — *Insert screenshot of the login page with username/password fields.*

---

#### US 1.2 — Passwordless Login via OTP

**Description:**
The OTP-based login flow provides an alternative authentication method where users receive a one-time password via email. This is particularly useful for members who may forget their passwords or prefer not to maintain one.

**User Story:**
> *As a* registered user,
> *I want to* log in using a one-time password sent to my email,
> *So that* I can access the system without remembering a password.

**Business Value:**
Passwordless login reduces friction for members, decreases password reset requests to library staff, and provides an additional layer of security since OTPs are time-limited and single-use.

**Acceptance Criteria:**

| # | Scenario | Given | When | Then |
|---|---|---|---|---|
| AC-1.2.1 | OTP request | A registered user with a valid email exists | The user requests an OTP via their email | A 6-digit OTP is generated, stored, and sent to the user's email |
| AC-1.2.2 | Successful OTP verification | An OTP has been sent to the user | The user submits the correct OTP | A JWT token is returned; user is redirected to dashboard |
| AC-1.2.3 | Invalid OTP | An OTP has been sent | The user submits an incorrect OTP | The system returns a 401 error with "Invalid OTP" message |
| AC-1.2.4 | Expired OTP | An OTP was sent more than 5 minutes ago | The user submits the OTP (even if correct) | The system returns a 401 error with "OTP has expired" message |

**Definition of Done:**
- [ ] POST `/api/auth/request-otp` generates and emails OTP
- [ ] POST `/api/auth/verify-otp` validates OTP and returns JWT
- [ ] OTP expires after 5 minutes
- [ ] OTP is single-use (deleted after successful verification)
- [ ] Frontend OTP input form handles all error states
- [ ] Postman collection includes OTP flow test cases

---

#### US 1.3 — Role-Based Authorization

**Description:**
The system enforces strict role-based access control using Spring Security's `@PreAuthorize` annotations. Each API endpoint is guarded to ensure only users with the appropriate role can access it. The frontend also conditionally renders navigation and UI elements based on the user's role.

**User Story:**
> *As a* system administrator,
> *I want* the system to restrict access to features based on user roles (Admin, Staff, Member),
> *So that* sensitive operations are only performed by authorized personnel.

**Business Value:**
Role-based authorization prevents unauthorized access to administrative functions (e.g., member deletion, staff management), protects member privacy (IDOR prevention), and ensures clear separation of duties between admin, staff, and member roles.

**Acceptance Criteria:**

| # | Scenario | Given | When | Then |
|---|---|---|---|---|
| AC-1.3.1 | Admin access | User has ADMIN role | Admin accesses `/api/admin/members` | Returns 200 with member list |
| AC-1.3.2 | Staff blocked from admin | User has STAFF role | Staff accesses `/api/admin/members` | Returns 403 Forbidden |
| AC-1.3.3 | Member blocked from staff | User has MEMBER role | Member accesses `/api/staff/books` (POST) | Returns 403 Forbidden |
| AC-1.3.4 | IDOR protection | Member A is logged in | Member A accesses `/api/transactions/user/{memberB_id}` | Returns 403 Forbidden (SpEL: `#userId == principal.id`) |
| AC-1.3.5 | Unauthenticated access | No JWT token provided | Any user accesses a protected endpoint | Returns 401 Unauthorized |

**Definition of Done:**
- [ ] All admin endpoints annotated with `@PreAuthorize("hasRole('ADMIN')")`
- [ ] All staff endpoints annotated with appropriate role checks
- [ ] Member self-access endpoints include SpEL-based IDOR protection
- [ ] Frontend conditionally renders navigation based on role from JWT
- [ ] Access Denied page displayed for unauthorized navigation attempts
- [ ] Postman collection includes role-based access test cases for all three roles

> **Screenshot Placeholder** — *Insert screenshot of Access Denied page shown when a Member tries to access Admin dashboard.*

---

### Epic 2: Admin Dashboard

> **Epic Description:** This epic covers the administrative interface that provides high-level oversight of the library system. The Admin Dashboard includes analytical widgetslands, member management, staff/admin user registration, and content management for the public about page.

---

#### US 2.1 — Admin Dashboard Layout & Widgets

**Description:**
The Admin Dashboard provides the administrator with a visual overview of the library system through KPI cards and charts. It displays user counts, demographic distributions, and recent feedback to support data-driven decision-making.

**User Story:**
> *As an* Admin,
> *I want to* see a dashboard with key statistics and visual widgets when I log in,
> *So that* I can quickly assess the library's operational status and member demographics.

**Business Value:**
Visual dashboards replace manual report preparation, provide instant visibility into library operations, and enable the administrator to present data-driven reports to the Negombo Municipal Council.

**Acceptance Criteria:**

| # | Scenario | Given | When | Then |
|---|---|---|---|---|
| AC-2.1.1 | Dashboard loads | Admin is logged in | Admin navigates to dashboard | KPI cards show total members, total staff, total books, and active members |
| AC-2.1.2 | Gender distribution | Member data exists | Admin views dashboard | Pie chart displays male/female/other distribution |
| AC-2.1.3 | Age distribution | Members with DOB exist | Admin views dashboard | Bar chart shows age group distribution |
| AC-2.1.4 | Recent feedback | Feedback submissions exist | Admin views dashboard | Latest 5 feedback entries are displayed |

**Definition of Done:**
- [ ] GET `/api/admin/stats` returns all required statistics
- [ ] Dashboard renders KPI cards, pie chart, bar chart, and feedback list
- [ ] Charts are responsive and use consistent colour scheme
- [ ] Dashboard loads within 2 seconds
- [ ] Postman test for `/api/admin/stats` endpoint included

> **Screenshot Placeholder** — *Insert screenshot of the Admin Dashboard showing KPI cards, gender pie chart, and age bar chart.*

---

#### US 2.2 — Member CRUD (Admin)

**Description:**
The admin can create, view, update, and delete library member records. This includes managing personal details, contact information, home address, membership status, and profile images. The admin can also search members and toggle account activation.

**User Story:**
> *As an* Admin,
> *I want to* manage all library member records (create, read, update, delete, activate/deactivate),
> *So that* I can maintain an accurate and up-to-date membership database.

**Business Value:**
Digital member management replaces paper ledgers, enables quick member lookup, prevents duplicate registrations through username/email validation, and allows the admin to control access by activating or deactivating accounts.

**Acceptance Criteria:**

| # | Scenario | Given | When | Then |
|---|---|---|---|---|
| AC-2.2.1 | Create member | Admin is on member creation form | Admin fills all required fields and submits | Member is created; success message displayed |
| AC-2.2.2 | Duplicate username | A member with username "john" exists | Admin tries to create member with username "john" | Error: "Username already exists" |
| AC-2.2.3 | Duplicate email | A member with email "a@b.com" exists | Admin tries to create member with same email | Error: "Email already exists" |
| AC-2.2.4 | Update member | Member record exists | Admin edits fields and saves | Changes persisted; success message displayed |
| AC-2.2.5 | Toggle status | An active member exists | Admin clicks toggle status | Member status changed; confirmation shown |
| AC-2.2.6 | Delete member | A member exists | Admin clicks delete and confirms | Member soft-deleted; removed from active list |
| AC-2.2.7 | Search members | Multiple members exist | Admin types in search box | Results filtered by name, email, or username |

**Definition of Done:**
- [ ] GET `/api/admin/members` returns paginated member list
- [ ] POST `/api/admin/members` creates member with validation
- [ ] PUT `/api/admin/members/{id}` updates member details
- [ ] DELETE `/api/admin/members/{id}` soft-deletes member
- [ ] PUT `/api/admin/members/{id}/status` toggles active/inactive
- [ ] GET `/api/admin/members/search?query=` returns filtered results
- [ ] Frontend member management page handles all CRUD operations
- [ ] Postman collection includes all member CRUD test cases

> **Screenshot Placeholder** — *Insert screenshot of the Admin Member Management page showing the member table and action buttons.*

---

#### US 2.3 — Registration CRUD (Staff/Admin User Management)

**Description:**
The admin can create and manage staff and admin user accounts. This is separate from member management — it controls who has operational or administrative access to the system. Passwords must meet minimum length requirements.

**User Story:**
> *As an* Admin,
> *I want to* create and manage staff and admin accounts,
> *So that* I can control who has operational and administrative access to the system.

**Business Value:**
Centralized user account management ensures that only authorized personnel can access staff and admin functions, supports staff onboarding and offboarding, and maintains an audit trail of administrative users.

**Acceptance Criteria:**

| # | Scenario | Given | When | Then |
|---|---|---|---|---|
| AC-2.3.1 | Create staff account | Admin is logged in | Admin fills registration form with STAFF role | Staff account created; staff can log in |
| AC-2.3.2 | Create admin account | Admin is logged in | Admin fills registration form with ADMIN role | Admin account created with admin privileges |
| AC-2.3.3 | Short password | Admin is creating a user | Admin enters password with fewer than 8 characters | Error: "Password must be at least 8 characters" |
| AC-2.3.4 | Update registration | A staff account exists | Admin updates the account details | Changes persisted successfully |
| AC-2.3.5 | Delete registration | A staff account exists | Admin deletes the account | Account removed; user can no longer log in |

**Definition of Done:**
- [ ] GET `/api/admin/registrations` lists all staff and admin accounts
- [ ] POST `/api/admin/registrations` creates user with role assignment
- [ ] PUT `/api/admin/registrations/{id}` updates user details
- [ ] DELETE `/api/admin/registrations/{id}` removes user account
- [ ] Password minimum 8 characters enforced at API level
- [ ] Frontend registration form supports role selection (STAFF/ADMIN)
- [ ] Postman collection includes registration CRUD test cases

---

#### US 2.4 — About Statements CRUD

**Description:**
The admin can manage the content displayed on the public "About" page of the library website. This allows the library to keep its public-facing information up to date without developer intervention.

**User Story:**
> *As an* Admin,
> *I want to* create, edit, and delete about page statements,
> *So that* I can keep the library's public information current and accurate.

**Business Value:**
Content management capability empowers the library to independently maintain its public-facing website, reducing dependency on technical staff for content updates.

**Acceptance Criteria:**

| # | Scenario | Given | When | Then |
|---|---|---|---|---|
| AC-2.4.1 | View about page | About statements exist | Any user visits the About page | Statements displayed correctly |
| AC-2.4.2 | Create statement | Admin is logged in | Admin creates a new about statement | Statement saved and visible on About page |
| AC-2.4.3 | Update statement | An about statement exists | Admin edits the statement | Changes reflected on About page |
| AC-2.4.4 | Delete statement | An about statement exists | Admin deletes the statement | Statement removed from About page |

**Definition of Done:**
- [ ] GET `/api/about` is publicly accessible (no auth required)
- [ ] POST `/api/admin/about` creates statement (ADMIN only)
- [ ] PUT `/api/admin/about/{id}` updates statement (ADMIN only)
- [ ] DELETE `/api/admin/about/{id}` deletes statement (ADMIN only)
- [ ] Frontend About page renders statements dynamically
- [ ] Postman test cases for public GET and admin CRUD included

---

### Epic 3: Staff Dashboard

> **Epic Description:** This epic covers all operational features used by library staff for day-to-day activities. It includes catalogue management, circulation (borrow/return), fine processing, reservation handling, and member communication through notifications.

---

#### US 3.1 — Staff Dashboard Layout & KPIs

**Description:**
The Staff Dashboard provides operational staff with real-time KPIs and visual analytics to monitor daily library activities including book circulation, fines, and reservations.

**User Story:**
> *As a* Staff member,
> *I want to* see a dashboard with operational KPIs and charts when I log in,
> *So that* I can monitor daily library activities at a glance.

**Business Value:**
Operational dashboards reduce manual counting and reporting, help staff prioritize tasks (e.g., pending reservations, unpaid fines), and provide data for the Municipal Council's periodic reviews.

**Acceptance Criteria:**

| # | Scenario | Given | When | Then |
|---|---|---|---|---|
| AC-3.1.1 | KPI cards | Staff is logged in | Staff navigates to dashboard | Cards show total books, currently borrowed, active reservations, unpaid fines |
| AC-3.1.2 | Category distribution | Books with Dewey codes exist | Staff views dashboard | Pie chart shows book distribution by Dewey Decimal category |
| AC-3.1.3 | Top borrowed books | Transaction history exists | Staff views dashboard | Top 5 most borrowed books displayed |
| AC-3.1.4 | Fine statistics | Fines exist in system | Staff views dashboard | Fine statistics panel shows total, paid, and unpaid amounts |

**Definition of Done:**
- [ ] GET `/api/staff/dashboard/stats` returns all KPI data
- [ ] Dashboard renders KPI cards, pie chart with Dewey categories, and top borrowed list
- [ ] Charts update dynamically based on current data
- [ ] Postman test for dashboard stats endpoint included

> **Screenshot Placeholder** — *Insert screenshot of the Staff Dashboard showing KPI cards and category distribution chart.*

---

#### US 3.2 — Manage Member (View-only + Borrow History)

**Description:**
Staff members can view member details and their complete borrow history but cannot modify member records. This allows staff to verify membership status and review a member's borrowing patterns before processing transactions.

**User Story:**
> *As a* Staff member,
> *I want to* view member details and their borrow history,
> *So that* I can verify membership and assess borrowing patterns before issuing books.

**Business Value:**
View access to member history helps staff make informed lending decisions, identify members with outstanding fines or overdue books, and provide better service at the front desk.

**Acceptance Criteria:**

| # | Scenario | Given | When | Then |
|---|---|---|---|---|
| AC-3.2.1 | View members | Staff is logged in | Staff navigates to member list | Member list displayed (read-only) |
| AC-3.2.2 | View borrow history | A member with transactions exists | Staff clicks on member's borrow history | Complete transaction history displayed |
| AC-3.2.3 | Cannot edit member | Staff is viewing a member | Staff attempts to modify member data | No edit option available; fields are read-only |

**Definition of Done:**
- [ ] GET `/api/staff/members` returns member list for staff
- [ ] GET `/api/staff/members/{id}/borrow-history` returns transaction history
- [ ] Frontend displays member data in read-only mode for staff
- [ ] Postman test cases for staff member endpoints included

---

#### US 3.3 — Book CRUD (with Cover Picture)

**Description:**
Staff can manage the complete book catalogue including creating new entries with cover images, updating metadata (title, author, ISBN, category, pages, Dewey Decimal code, municipal reference, library reference), and managing available copies.

**User Story:**
> *As a* Staff member,
> *I want to* create, read, update, and delete books with cover images and full metadata,
> *So that* the library catalogue is complete, searchable, and visually rich.

**Business Value:**
A comprehensive digital catalogue replaces the manual card system, enables quick book searches by title/author/ISBN/category, supports the Dewey Decimal Classification standard, and provides visual identification through cover images.

**Acceptance Criteria:**

| # | Scenario | Given | When | Then |
|---|---|---|---|---|
| AC-3.3.1 | Create book | Staff is logged in | Staff fills book form with cover image and submits | Book created with all metadata; cover image uploaded |
| AC-3.3.2 | Upload cover image | Staff is creating/editing a book | Staff uploads a JPEG/PNG image file | Image stored with UUID filename; displayed on book card |
| AC-3.3.3 | Update book | A book exists | Staff updates metadata and saves | Changes persisted; updated data shown |
| AC-3.3.4 | Delete book | A book exists with no active transactions | Staff deletes the book | Book removed from catalogue |
| AC-3.3.5 | View book details | Books exist in catalogue | Staff/Member browses book inventory | Cards show title, author, category, pages, Dewey code, available copies, cover image |

**Definition of Done:**
- [ ] POST `/api/staff/books` (multipart/form-data) creates book with cover
- [ ] PUT `/api/staff/books/{id}` updates book metadata
- [ ] DELETE `/api/staff/books/{id}` removes book
- [ ] POST `/api/staff/books/{id}/cover` uploads/replaces cover image
- [ ] GET `/api/books` returns public book list
- [ ] Book fields include: title, author, ISBN, category, pages, deweyCode, municipalRef, libraryRef, availableCopies, totalCopies
- [ ] Cover images stored in `uploads/books/` with UUID prefix
- [ ] Postman collection includes multipart book creation test case

> **Screenshot Placeholder** — *Insert screenshot of the Book Inventory page showing book cards with cover images and metadata.*

---

#### US 3.4 — Author CRUD

**Description:**
Staff can manage the author database independently from books. Authors can be created, updated, and deleted. Deletion is prevented if the author has associated books in the catalogue.

**User Story:**
> *As a* Staff member,
> *I want to* manage the author database (create, read, update, delete),
> *So that* the library maintains an accurate and organized author directory.

**Business Value:**
Separate author management enables consistent author naming across the catalogue, supports author-based book searches, and maintains data integrity through referential constraints.

**Acceptance Criteria:**

| # | Scenario | Given | When | Then |
|---|---|---|---|---|
| AC-3.4.1 | Create author | Staff is logged in | Staff fills author form and submits | Author created successfully |
| AC-3.4.2 | Update author | An author exists | Staff updates author details | Changes saved |
| AC-3.4.3 | Delete author (no books) | Author has no associated books | Staff deletes author | Author removed |
| AC-3.4.4 | Delete author (has books) | Author has associated books | Staff attempts to delete | Error: "Cannot delete author with existing books" |

**Definition of Done:**
- [ ] GET `/api/staff/authors` returns author list
- [ ] POST `/api/staff/authors` creates author
- [ ] PUT `/api/staff/authors/{id}` updates author
- [ ] DELETE `/api/staff/authors/{id}` deletes author (only if no books linked)
- [ ] Postman test cases for author CRUD included

---

#### US 3.5 — Borrow & Return CRUD with Filtering

**Description:**
Staff manage the complete circulation workflow — issuing books to members and processing returns. Transactions can be filtered by status (borrowed, returned, overdue) for efficient management.

**User Story:**
> *As a* Staff member,
> *I want to* issue books to members and process returns with status filtering,
> *So that* I can efficiently manage the library's circulation operations.

**Business Value:**
Digital circulation management eliminates manual logbooks, provides real-time visibility into borrowed books, enables quick identification of overdue items, and creates an audit trail for all transactions.

**Acceptance Criteria:**

| # | Scenario | Given | When | Then |
|---|---|---|---|---|
| AC-3.5.1 | Issue book | Member and available book exist | Staff issues book to member (userId + bookId) | Transaction created; available copies decremented |
| AC-3.5.2 | Return book | An active transaction exists | Staff processes return with date and condition | Transaction updated; available copies incremented |
| AC-3.5.3 | Filter transactions | Multiple transactions exist | Staff filters by status | Only matching transactions displayed |
| AC-3.5.4 | View counters | Transactions exist | Staff views transaction page | Counters show total, borrowed, returned, overdue counts |
| AC-3.5.5 | Book unavailable | Book has 0 available copies | Staff tries to issue book | Error: "Book is not available" |

**Definition of Done:**
- [ ] POST `/api/staff/transactions/issue?userId=X&bookId=Y` issues book
- [ ] PUT `/api/staff/transactions/return/{issueId}` processes return
- [ ] GET `/api/staff/transactions?status=` returns filtered list
- [ ] GET `/api/staff/transactions/counters` returns status counts
- [ ] Available copies automatically managed on issue/return
- [ ] Postman collection includes issue and return test cases

> **Screenshot Placeholder** — *Insert screenshot of the Borrow & Return management page with filter dropdown.*

---

#### US 3.6 — Automatic Fine Calculation

**Description:**
When a book is returned after its due date, the system automatically calculates a fine based on the number of overdue days. The fine is linked to the specific transaction and appears in both staff and member views.

**User Story:**
> *As a* Staff member,
> *I want* the system to automatically calculate fines when books are returned late,
> *So that* fine processing is consistent, accurate, and requires no manual calculation.

**Business Value:**
Automated fine calculation eliminates human error, ensures consistent application of the library's fine policy, creates an auditable record of all fines, and reduces disputes with members.

**Acceptance Criteria:**

| # | Scenario | Given | When | Then |
|---|---|---|---|---|
| AC-3.6.1 | On-time return | Book returned on or before due date | Staff processes return | No fine generated |
| AC-3.6.2 | Late return | Book returned after due date | Staff processes return | Fine automatically created with calculated amount |
| AC-3.6.3 | Fine visibility | A fine exists | Staff/Member views fines | Fine amount, transaction details, and status visible |

**Definition of Done:**
- [ ] TransactionService calculates fine on return processing
- [ ] Fine entity created automatically with correct amount
- [ ] Fine linked to transaction via OneToOne relationship
- [ ] Fine visible in staff fine management and member fine view
- [ ] Postman test verifies fine creation on late return

---

#### US 3.7 — Fines CRUD

**Description:**
Staff can view, filter, and manage all fines in the system. Fines can be filtered by status (all, unpaid, paid, none) and marked as paid when the member settles their dues.

**User Story:**
> *As a* Staff member,
> *I want to* view, filter, and manage fines (mark as paid),
> *So that* I can track outstanding fines and record payments accurately.

**Business Value:**
Digital fine management replaces paper receipt books, provides real-time visibility into outstanding amounts, supports financial reporting to the Municipal Council, and creates an audit trail for all fine transactions.

**Acceptance Criteria:**

| # | Scenario | Given | When | Then |
|---|---|---|---|---|
| AC-3.7.1 | View all fines | Fines exist | Staff navigates to fines page | All fines displayed with details |
| AC-3.7.2 | Filter by status | Fines of different statuses exist | Staff selects filter (UNPAID/PAID/ALL) | Filtered list displayed |
| AC-3.7.3 | Mark as paid | An unpaid fine exists | Staff marks fine as paid | Fine status updated to PAID; paid date recorded |
| AC-3.7.4 | Fine statistics | Fines exist | Staff views fine stats | Total, paid, and unpaid amounts displayed |

**Definition of Done:**
- [ ] GET `/api/staff/fines?status=` returns filtered fines
- [ ] PUT `/api/staff/fines/{fineId}/pay` marks fine as paid
- [ ] GET `/api/staff/fines/stats` returns fine statistics
- [ ] PUT `/api/staff/fines` supports fine updates
- [ ] DELETE `/api/staff/fines` supports soft delete
- [ ] Postman test cases for fine management included

---

#### US 3.8 — Return with Book Condition

**Description:**
When processing a book return, staff must record the physical condition of the returned book using a standardized scale (Good, Fair, Poor, Damaged). Optional condition notes can be added for documentation.

**User Story:**
> *As a* Staff member,
> *I want to* record the book's condition (Good, Fair, Poor, Damaged) when processing a return,
> *So that* the library tracks the physical state of its collection over time.

**Business Value:**
Book condition tracking enables the library to identify deteriorating books, plan for replacements, hold members accountable for damage, and maintain collection quality reports for the Municipal Council.

**Acceptance Criteria:**

| # | Scenario | Given | When | Then |
|---|---|---|---|---|
| AC-3.8.1 | Return in good condition | An active transaction exists | Staff returns book with condition "GOOD" | Return processed; condition recorded |
| AC-3.8.2 | Return damaged | An active transaction exists | Staff returns book with condition "DAMAGED" + notes | Return processed; condition and notes recorded |
| AC-3.8.3 | Condition required | Staff is processing a return | Staff does not select a condition | Validation error: "Book condition is required" |

**Definition of Done:**
- [ ] Return endpoint accepts `bookCondition` (GOOD, FAIR, POOR, DAMAGED) and optional `conditionNotes`
- [ ] Condition stored on Transaction entity
- [ ] Condition visible in transaction history and book details
- [ ] Frontend return form includes condition dropdown and notes field
- [ ] Postman test includes return with condition data

---

#### US 3.9 — Reservation (View-only for Staff)

**Description:**
Staff can view all member reservations, acknowledge new requests, and approve or reject them. When a reservation is approved, the system automatically issues the book to the member.

**User Story:**
> *As a* Staff member,
> *I want to* view, acknowledge, and approve/reject member reservations,
> *So that* I can manage reservation requests efficiently and issue approved books.

**Business Value:**
Digital reservation management replaces a manual waitlist, ensures fair allocation of popular books, and automates the issuance workflow when reservations are approved.

**Acceptance Criteria:**

| # | Scenario | Given | When | Then |
|---|---|---|---|---|
| AC-3.9.1 | View reservations | Reservations exist | Staff navigates to reservations page | All reservations displayed with status |
| AC-3.9.2 | Acknowledge reservation | A new reservation exists | Staff clicks acknowledge | Reservation status updated to ACKNOWLEDGED |
| AC-3.9.3 | Approve reservation | An acknowledged reservation exists | Staff approves reservation | Status set to APPROVED; book automatically issued to member |
| AC-3.9.4 | Reject reservation | An acknowledged reservation exists | Staff rejects reservation | Status set to REJECTED; reservation marked as processed |

**Definition of Done:**
- [ ] GET `/api/staff/reservations` returns all reservations
- [ ] PATCH `/api/staff/reservations/{id}/acknowledge` acknowledges reservation
- [ ] PATCH `/api/staff/reservations/{id}/status` changes status (APPROVED/REJECTED)
- [ ] APPROVED status triggers automatic book issuance via TransactionService
- [ ] Postman test cases for reservation workflow included

---

#### US 3.10 — Notification CRUD

**Description:**
Staff and Admin can create notifications that are broadcast to members. Members can view their notifications and mark them as read individually or in bulk.

**User Story:**
> *As a* Staff member,
> *I want to* create and broadcast notifications to members,
> *So that* I can communicate important updates (new arrivals, overdue reminders, events) to library members.

**Business Value:**
Digital notifications replace the absence of any communication channel, enabling the library to proactively inform members about overdue books, new arrivals, events, and policy changes.

**Acceptance Criteria:**

| # | Scenario | Given | When | Then |
|---|---|---|---|---|
| AC-3.10.1 | Create notification | Staff is logged in | Staff creates a notification | Notification created and sent to targeted members |
| AC-3.10.2 | View notifications | Notifications exist for a member | Member logs in | Unread notifications displayed with badge count |
| AC-3.10.3 | Mark as read | A notification exists | Member clicks mark as read | Notification marked as read |
| AC-3.10.4 | Mark all as read | Multiple unread notifications exist | Member clicks "Mark All Read" | All notifications for that member marked as read |
| AC-3.10.5 | Member isolation | Notifications exist for Member A | Member B views notifications | Member B sees only their own notifications |

**Definition of Done:**
- [ ] POST `/api/notifications` creates notification (STAFF/ADMIN only)
- [ ] GET `/api/notifications` returns user's own notifications
- [ ] PUT `/api/notifications/{id}/read` marks single notification as read
- [ ] PUT `/api/notifications/read-all` marks all as read for current user
- [ ] DELETE `/api/notifications/{id}` deletes notification
- [ ] Members can only see their own notifications (member validation enforced)
- [ ] Postman test cases for notification CRUD included

> **Screenshot Placeholder** — *Insert screenshot of the notification bell/dropdown showing unread notifications for a member.*

---

## 7. Risk Assessment & Mitigation

| # | Risk | Likelihood | Impact | Mitigation Strategy |
|---|---|---|---|---|
| R-01 | **Client scope creep** — Client requests additional features beyond agreed scope | High | Medium | Maintain a clear product backlog; defer new features to future iterations; communicate scope boundaries in writing to Municipal Commissioner and Project Supervisor |
| R-02 | **Project deadline extension rejected** — Municipal Council rejects extension request | Medium | High | Submitted official request through SLIIT campus (email to Mr. Jeewaka, LIC); documented all development progress to demonstrate commitment |
| R-03 | **Data loss during migration** — Loss of existing paper records during digitization | Medium | High | Incremental data entry with verification; maintain paper records as backup; implement database backup strategy |
| R-04 | **Low user adoption** — Library staff resistant to new digital system | Medium | High | Conduct training sessions; provide user-friendly UI; offer parallel running period (manual + digital) |
| R-05 | **Security vulnerabilities** — JWT token theft, IDOR attacks, SQL injection | Low | Critical | JWT with short expiry; SpEL-based IDOR protection on member endpoints; parameterized queries via JPA; BCrypt password hashing; input validation on all endpoints |
| R-06 | **Performance degradation** — Slow response times as data grows | Low | Medium | Pagination on list endpoints; indexed database columns; optimized JPA queries; lazy loading for entity relationships |
| R-07 | **File storage exhaustion** — Unrestricted image uploads filling disk space | Low | Medium | UUID-based filenames prevent duplication; file type validation (JPEG/PNG only); consider cloud storage migration in future |
| R-08 | **Single point of failure** — Application depends on single server | Medium | High | Document deployment requirements; recommend production deployment with load balancer and database replication |
| R-09 | **Integration failure** — Email service (OTP) unavailable or unreliable | Medium | Medium | Username/password login remains as primary method; OTP is an alternative; graceful error handling when email service fails |
| R-10 | **Hibernate schema conflicts** — `ddl-auto=update` causing column issues on existing databases | Medium | Medium | Verified with production data; tested migration scenarios; documented column defaults and NOT NULL constraints |

---

## 8. Use Case Scenarios

### UC-01: Member Self-Registration and Login

| Field | Description |
|---|---|
| **Actor** | New library visitor |
| **Precondition** | User has a valid email address |
| **Main Flow** | 1. User navigates to registration page → 2. Fills in name, email, username, password, address → 3. Submits form → 4. System creates MEMBER account → 5. User logs in with credentials → 6. Redirected to Member dashboard |
| **Alternative Flow** | 2a. Username already exists → System shows error → User changes username → 2b. Email already exists → System shows error → User uses different email |
| **Postcondition** | User has an active MEMBER account and is logged in |

### UC-02: Staff Issues a Book to a Member

| Field | Description |
|---|---|
| **Actor** | Staff member |
| **Precondition** | Member has active status; Book has available copies > 0 |
| **Main Flow** | 1. Staff navigates to Transactions page → 2. Selects member and book → 3. Clicks "Issue Book" → 4. System creates transaction with issue date, due date → 5. Book's available copies decremented → 6. Transaction appears in borrowed list |
| **Alternative Flow** | 2a. Book has 0 available copies → System shows "Book not available" error → 2b. Member has deactivated account → System prevents issuance |
| **Postcondition** | Active transaction created; book's available copies reduced by 1 |

### UC-03: Staff Processes a Book Return with Fine

| Field | Description |
|---|---|
| **Actor** | Staff member |
| **Precondition** | An active borrowed transaction exists; book is overdue |
| **Main Flow** | 1. Staff navigates to Transactions → 2. Filters by "BORROWED" status → 3. Selects the overdue transaction → 4. Records return date, book condition (e.g., FAIR), and condition notes → 5. System calculates fine (days overdue × daily rate) → 6. Transaction status updated to RETURNED → 7. Fine record created → 8. Book's available copies incremented |
| **Alternative Flow** | 4a. Book returned on time → No fine generated → 4b. Book condition is DAMAGED → Staff adds detailed condition notes |
| **Postcondition** | Transaction marked as returned; fine created and linked to transaction; book available copies restored |

### UC-04: Member Reserves a Book

| Field | Description |
|---|---|
| **Actor** | Library Member |
| **Precondition** | Member is logged in; Book exists in catalogue |
| **Main Flow** | 1. Member browses book catalogue → 2. Clicks "Reserve" on desired book → 3. System creates reservation with PENDING status → 4. Staff acknowledges reservation → 5. Staff approves reservation → 6. System automatically issues book to member → 7. Member receives notification |
| **Alternative Flow** | 5a. Staff rejects reservation → Status set to REJECTED → Member notified → 3a. Member already has active reservation for same book → System prevents duplicate |
| **Postcondition** | Book issued to member via automated reservation approval workflow |

### UC-05: Admin Manages Staff Accounts

| Field | Description |
|---|---|
| **Actor** | Admin |
| **Precondition** | Admin is logged in with ADMIN role |
| **Main Flow** | 1. Admin navigates to Registrations page → 2. Clicks "Create New User" → 3. Fills in details, selects role (STAFF or ADMIN), sets password (min 8 chars) → 4. Submits form → 5. New user account created → 6. New user can log in with assigned credentials |
| **Alternative Flow** | 3a. Password less than 8 characters → Validation error → 3b. Username already exists → Error displayed |
| **Postcondition** | New staff/admin user account created and ready for use |

---

## 9. Appendix

### 9.1 Client Communication Documentation

#### 9.1.1 Initial Client Engagement

Initial meetings with Mrs. Sheromi Perera (Head Librarian) were conducted at the Talahena Public Library premises in Thalahena, Negombo. The meetings focused on understanding:

- Current operational workflows (book cataloguing, member registration, issuance, returns, fines)
- Pain points with the existing manual system
- Desired features for the new digital system
- Reporting requirements for the Negombo Municipal Council

#### 9.1.2 Email Correspondence — Project Extension Request

**Subject:** Request for Official Support Letter – Library Management System | IT24101848

**From:** Student Developer

**To:** Mr. Jeewaka (Lecturer-in-Charge, OOAD Module, SLIIT)

**Summary:**

The student developer submitted a personal letter to the Municipal Commissioner (Ms. Nuwani Sudusinghe) on **January 25, 2026**, requesting an extension until **July 10, 2026** to complete and deliver the final system. This request was **rejected** by the Municipal Council.

As the system was developed as part of the OOAD module assignment, the Municipal Council requested that an **official letter be submitted through the SLIIT campus**. The email requests Mr. Jeewaka's assistance in writing an official letter from SLIIT to the Municipal Commissioner explaining:

1. Why the project could not be completed on time due to academic commitments at SLIIT
2. A new confirmed deadline for the delivery of the completed system

**Key Contacts Referenced:**

| Person | Role | Contact |
|---|---|---|
| Ms. Nuwani Sudusinghe | Municipal Commissioner (S.L.A.S. 01), Municipal Council, Negombo | — |
| Mr. Eranda | Project Supervisor (appointed by Municipal Commissioner) | 077-5834583 |
| Ms. Sheromi Perera | Head Librarian, Talahena Public Library | 076-5272541 |
| Mr. Jeewaka | Lecturer-in-Charge, OOAD Module, SLIIT | — |

> **Screenshot Placeholder** — *Insert full screenshot of the email sent to Mr. Jeewaka regarding the project extension request.*

#### 9.1.3 Requirement Validation

Requirements gathered from client meetings were documented and validated with the project supervisor Mr. Eranda. The product backlog was maintained and prioritized throughout the development lifecycle.

### 9.2 Team GitHub Repository

| Item | Detail |
|---|---|
| **Repository Name** | ThalahenaPublicLibrarydemo |
| **Version Control** | Git with GitHub |
| **Branching Strategy** | Dev branch for active development; main branch for stable releases |
| **Commit Convention** | Descriptive commit messages indicating feature/fix type |

> **Screenshot Placeholder** — *Insert screenshot of the GitHub repository page showing commit history and branch structure.*

### 9.3 Technology Stack Summary

| Layer | Technology |
|---|---|
| **Frontend** | React 18 (Vite), React Router, Axios, Recharts (charts), Tailwind CSS |
| **Backend** | Spring Boot 3.2.4, Spring Security, Spring Data JPA, Hibernate |
| **Database** | MySQL 8.x |
| **Authentication** | JWT (JSON Web Tokens) via `jjwt` library |
| **Password Hashing** | BCrypt (Spring Security) |
| **File Storage** | Local filesystem (`uploads/books/`, `uploads/profiles/`) with UUID naming |
| **Build Tool** | Maven (backend), npm (frontend) |
| **Java Version** | Java 17+ |

---

## 10. QA Responsibilities

### 10.1 Workflow: Dev Branch → GitHub → QA

```
Developer (Local)                 GitHub                         QA Environment
┌─────────────┐    git push    ┌──────────────┐   deploy    ┌──────────────────┐
│  Dev Branch │──────────────→ │   Remote Dev │───────────→ │  QA Testing Env  │
│  (Feature)  │                │    Branch    │             │  (localhost:8081)│
└─────────────┘                └──────────────┘             └──────────────────┘
```

**Workflow Steps:**
1. Developer implements feature/fix on local dev branch
2. Code is committed with descriptive commit messages
3. Branch is pushed to GitHub repository
4. QA pulls latest code and deploys to testing environment
5. QA executes test plan (manual + Postman collections)
6. Bugs logged as GitHub Issues with reproduction steps
7. Fixes merged back to dev branch; cycle repeats

---

### 10.2 Unit Testing

**What to Test:**
- Service layer business logic (fine calculation, book availability checks, OTP expiry validation)
- Utility functions (JWT token generation, password hashing, date calculations)
- Entity validation constraints (email format, password length, required fields)

**How to Run:**
```bash
cd ThalahenaPublicLibrarydemo
mvn test
```

**Key Test Classes:**
- `ThalahenaPublicLibrarydemoApplicationTests.java` — Spring context load test
- Service-level unit tests using JUnit 5 with Mockito for dependency mocking

**What NOT to Unit Test:**
- HTTP request/response handling (covered by integration testing)
- Frontend UI rendering (covered by functional/usability testing)
- Database connectivity (covered by integration testing)

---

### 10.3 Integration Testing

**What to Test:**
- Controller-to-Service-to-Repository data flow
- JPA entity relationships (e.g., Transaction → Book, Transaction → User, Fine → Transaction)
- Spring Security filter chain (JWT validation, role-based access)
- File upload and storage integration (book covers, profile pictures)

**What NOT to Test Here:**
- UI rendering and user interactions (functional testing)
- External email delivery (mocked or tested separately)
- Performance under load (performance testing)

**How to Run:**
```bash
mvn verify -Dspring.profiles.active=test
```

---

### 10.4 Functional Testing

#### Happy Path Testing

| Test ID | Scenario | Steps | Expected Result |
|---|---|---|---|
| FT-HP-01 | Admin login | Enter admin/admin123 → Submit | Dashboard loads with KPI widgets |
| FT-HP-02 | Create member | Admin fills member form → Submit | Member appears in list |
| FT-HP-03 | Create book with cover | Staff uploads book with image → Submit | Book card shows cover and metadata |
| FT-HP-04 | Issue book | Staff selects member + available book → Issue | Transaction created; copies decremented |
| FT-HP-05 | Return book on time | Staff processes return before due date | No fine generated |
| FT-HP-06 | Member self-registration | Visitor fills registration form → Submit | MEMBER account created |
| FT-HP-07 | Reserve a book | Member clicks Reserve on a book | Reservation created with PENDING status |
| FT-HP-08 | Create notification | Staff creates notification | Members receive notification |

#### Negative / Edge Case Path Testing

| Test ID | Scenario | Steps | Expected Result |
|---|---|---|---|
| FT-NP-01 | Login with wrong password | Enter valid username, wrong password | 401 Unauthorized; error message shown |
| FT-NP-02 | Issue unavailable book | Try to issue book with 0 available copies | Error: "Book not available" |
| FT-NP-03 | Duplicate username | Create member with existing username | Error: "Username already exists" |
| FT-NP-04 | Staff accesses admin endpoint | Staff tries to access `/api/admin/members` | 403 Forbidden |
| FT-NP-05 | Delete author with books | Try to delete author who has linked books | Error: "Cannot delete author with existing books" |
| FT-NP-06 | Short password registration | Create user with password < 8 characters | Validation error displayed |
| FT-NP-07 | IDOR attack | Member A accesses Member B's transactions | 403 Forbidden (SpEL protection) |
| FT-NP-08 | Return without condition | Process return without selecting book condition | Validation: "Book condition is required" |
| FT-NP-09 | Expired OTP | Submit OTP after 5-minute expiry | Error: "OTP has expired" |
| FT-NP-10 | Access without token | Call protected API without JWT | 401 Unauthorized |

---

### 10.5 API & Endpoint Testing (Postman / cURL)

#### Postman Workflow

1. **Environment Setup:** Create Postman environment with `base_url = http://localhost:8081`
2. **Authentication:** Login via POST `/api/auth/login`; store JWT token in environment variable `{{token}}`
3. **Authorization Header:** All authenticated requests include `Authorization: Bearer {{token}}`
4. **Collections:** Organized by Epic (Auth, Admin, Staff) with pre-configured requests
5. **Tests:** Each request includes pre-request scripts and test assertions

#### Legend

| Symbol | Meaning |
|---|---|
| ✅ **Good Path** | Valid request with expected successful response |
| ❌ **Negative Path** | Invalid/unauthorized request demonstrating error handling |

---

#### Epic 1: Authentication & Access Control — Postman Tests

##### 1.1 Login Page (UI + API)

**✅ Good Path — Successful Login**

```
POST http://localhost:8081/api/auth/login
Content-Type: application/json
```

**Request Body:**
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**Expected Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsInJvbGUiOiJBRE1JTiIsImlkIjoxLCJpYXQiOjE3MTg2NjQwMDAsImV4cCI6MTcxODY2NzYwMH0...",
  "username": "admin",
  "role": "ADMIN",
  "id": 1
}
```

**❌ Negative Path — Invalid Credentials**

```
POST http://localhost:8081/api/auth/login
Content-Type: application/json
```

**Request Body:**
```json
{
  "username": "admin",
  "password": "wrongpassword"
}
```

**Expected Response (401 Unauthorized):**
```json
{
  "error": "Invalid username or password"
}
```

> **Screenshot Placeholder** — *Insert Postman screenshot showing successful login response (200 OK with JWT token).*

> **Screenshot Placeholder** — *Insert Postman screenshot showing failed login response (401 Unauthorized).*

---

##### 1.2 Passwordless Login via OTP

**✅ Good Path — Request OTP**

```
POST http://localhost:8081/api/auth/request-otp
Content-Type: application/json
```

**Request Body:**
```json
{
  "email": "member@example.com"
}
```

**Expected Response (200 OK):**
```json
{
  "message": "OTP sent to your email"
}
```

**✅ Good Path — Verify OTP**

```
POST http://localhost:8081/api/auth/verify-otp
Content-Type: application/json
```

**Request Body:**
```json
{
  "email": "member@example.com",
  "otp": "123456"
}
```

**Expected Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "member1",
  "role": "MEMBER",
  "id": 5
}
```

**❌ Negative Path — Invalid OTP**

```
POST http://localhost:8081/api/auth/verify-otp
Content-Type: application/json
```

**Request Body:**
```json
{
  "email": "member@example.com",
  "otp": "000000"
}
```

**Expected Response (401 Unauthorized):**
```json
{
  "error": "Invalid OTP"
}
```

> **Screenshot Placeholder** — *Insert Postman screenshot of OTP request and verification flow.*

---

##### 1.3 Role-Based Authorization

**❌ Negative Path — Staff Accessing Admin Endpoint**

```
GET http://localhost:8081/api/admin/stats
Authorization: Bearer {{staff_token}}
```

**Expected Response (403 Forbidden):**
```json
{
  "error": "Access Denied",
  "message": "You do not have permission to access this resource"
}
```

**❌ Negative Path — Member Accessing Staff Endpoint**

```
POST http://localhost:8081/api/staff/books
Authorization: Bearer {{member_token}}
Content-Type: multipart/form-data
```

**Expected Response (403 Forbidden)**

**❌ Negative Path — IDOR Protection (Member accessing another member's data)**

```
GET http://localhost:8081/api/transactions/user/99
Authorization: Bearer {{member_token_id_5}}
```

**Expected Response (403 Forbidden)** — SpEL expression `#userId == principal.id` blocks access.

> **Screenshot Placeholder** — *Insert Postman screenshot showing 403 Forbidden for role-based access denial.*

---

#### Epic 2: Admin Dashboard — Postman Tests

##### 2.1 Admin Dashboard Stats

**✅ Good Path — Get Admin Statistics**

```
GET http://localhost:8081/api/admin/stats
Authorization: Bearer {{admin_token}}
```

**Expected Response (200 OK):**
```json
{
  "totalMembers": 45,
  "totalStaff": 3,
  "totalBooks": 60,
  "activeMembers": 42,
  "genderDistribution": {
    "male": 25,
    "female": 20
  },
  "ageDistribution": {
    "18-25": 15,
    "26-35": 18,
    "36-50": 8,
    "50+": 4
  },
  "recentFeedback": [
    {
      "id": 1,
      "message": "Great collection of books!",
      "rating": 5,
      "user": "member1"
    }
  ]
}
```

> **Screenshot Placeholder** — *Insert Postman screenshot of admin stats response.*

---

##### 2.2 Member CRUD (Admin)

**✅ Good Path — Create Member**

```
POST http://localhost:8081/api/admin/members
Authorization: Bearer {{admin_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "firstName": "Kamal",
  "lastName": "Perera",
  "email": "kamal.perera@example.com",
  "username": "kamalp",
  "password": "secure123",
  "phone": "0771234567",
  "address": "45, Main Street, Negombo",
  "dateOfBirth": "1995-03-15",
  "gender": "MALE"
}
```

**Expected Response (201 Created):**
```json
{
  "id": 46,
  "firstName": "Kamal",
  "lastName": "Perera",
  "email": "kamal.perera@example.com",
  "username": "kamalp",
  "phone": "0771234567",
  "address": "45, Main Street, Negombo",
  "dateOfBirth": "1995-03-15",
  "gender": "MALE",
  "active": true,
  "role": "MEMBER"
}
```

**✅ Good Path — Update Member**

```
PUT http://localhost:8081/api/admin/members/46
Authorization: Bearer {{admin_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "firstName": "Kamal",
  "lastName": "Perera",
  "email": "kamal.updated@example.com",
  "phone": "0779876543",
  "address": "12, Temple Road, Negombo",
  "dateOfBirth": "1995-03-15",
  "gender": "MALE"
}
```

**Expected Response (200 OK):** Updated member object returned.

**✅ Good Path — Toggle Member Status**

```
PUT http://localhost:8081/api/admin/members/46/status
Authorization: Bearer {{admin_token}}
```

**Expected Response (200 OK):** Member's `active` field toggled.

**✅ Good Path — Search Members**

```
GET http://localhost:8081/api/admin/members/search?query=kamal
Authorization: Bearer {{admin_token}}
```

**Expected Response (200 OK):** Array of matching member objects.

**❌ Negative Path — Duplicate Username**

```
POST http://localhost:8081/api/admin/members
Authorization: Bearer {{admin_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "firstName": "Test",
  "lastName": "User",
  "email": "test@example.com",
  "username": "admin",
  "password": "test1234",
  "phone": "0770000000",
  "gender": "MALE"
}
```

**Expected Response (400 Bad Request):**
```json
{
  "error": "Username already exists"
}
```

**❌ Negative Path — Duplicate Email**

```
POST http://localhost:8081/api/admin/members
Authorization: Bearer {{admin_token}}
Content-Type: application/json
```

**Request Body (using existing email):**
```json
{
  "firstName": "Test",
  "lastName": "User",
  "email": "kamal.perera@example.com",
  "username": "uniqueuser",
  "password": "test1234",
  "phone": "0770000000",
  "gender": "MALE"
}
```

**Expected Response (400 Bad Request):**
```json
{
  "error": "Email already exists"
}
```

> **Screenshot Placeholder** — *Insert Postman screenshots of member create (201), update (200), and duplicate error (400).*

---

##### 2.3 Registration CRUD

**✅ Good Path — Create Staff Account**

```
POST http://localhost:8081/api/admin/registrations
Authorization: Bearer {{admin_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "firstName": "Nimal",
  "lastName": "Silva",
  "email": "nimal@library.com",
  "username": "nimal_staff",
  "password": "staff1234",
  "role": "STAFF"
}
```

**Expected Response (201 Created):** New staff user object.

**❌ Negative Path — Password Too Short**

```
POST http://localhost:8081/api/admin/registrations
Authorization: Bearer {{admin_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "firstName": "Test",
  "lastName": "User",
  "email": "test@library.com",
  "username": "teststaff",
  "password": "short",
  "role": "STAFF"
}
```

**Expected Response (400 Bad Request):**
```json
{
  "error": "Password must be at least 8 characters"
}
```

> **Screenshot Placeholder** — *Insert Postman screenshot of registration create and password validation.*

---

##### 2.4 About Statements CRUD

**✅ Good Path — Create About Statement (Admin)**

```
POST http://localhost:8081/api/admin/about
Authorization: Bearer {{admin_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "title": "Our Mission",
  "content": "To provide accessible reading materials and learning resources to the Talahena community."
}
```

**Expected Response (201 Created):** About statement object.

**✅ Good Path — Public Read (No Auth)**

```
GET http://localhost:8081/api/about
```

**Expected Response (200 OK):** Array of all about statements.

**❌ Negative Path — Non-Admin Create Attempt**

```
POST http://localhost:8081/api/admin/about
Authorization: Bearer {{staff_token}}
Content-Type: application/json
```

**Expected Response (403 Forbidden)**

> **Screenshot Placeholder** — *Insert Postman screenshot of about statement CRUD operations.*

---

#### Epic 3: Staff Dashboard — Postman Tests

##### 3.1 Staff Dashboard Stats

**✅ Good Path — Get Staff Dashboard Statistics**

```
GET http://localhost:8081/api/staff/dashboard/stats
Authorization: Bearer {{staff_token}}
```

**Expected Response (200 OK):**
```json
{
  "totalBooks": 60,
  "totalBorrowed": 12,
  "activeReservations": 5,
  "unpaidFines": 3,
  "totalFinesAmount": 450.00,
  "categoryDistribution": [
    { "category": "Fiction", "deweyCode": "800", "count": 15 },
    { "category": "Science", "deweyCode": "500", "count": 10 },
    { "category": "History", "deweyCode": "900", "count": 8 }
  ],
  "topBorrowed": [
    { "title": "Harry Potter and the Sorcerer's Stone", "borrowCount": 8 },
    { "title": "Sapiens", "borrowCount": 6 }
  ]
}
```

> **Screenshot Placeholder** — *Insert Postman screenshot of staff dashboard stats.*

---

##### 3.2 Manage Member (View-only + Borrow History)

**✅ Good Path — View Members (Staff)**

```
GET http://localhost:8081/api/staff/members
Authorization: Bearer {{staff_token}}
```

**Expected Response (200 OK):** Array of member objects.

**✅ Good Path — View Member Borrow History**

```
GET http://localhost:8081/api/staff/members/5/borrow-history
Authorization: Bearer {{staff_token}}
```

**Expected Response (200 OK):**
```json
[
  {
    "id": 10,
    "bookTitle": "Sapiens",
    "issueDate": "2026-05-01",
    "dueDate": "2026-05-15",
    "returnDate": "2026-05-14",
    "status": "RETURNED",
    "fineAmount": 0,
    "bookCondition": "GOOD"
  }
]
```

---

##### 3.3 Book CRUD (with Cover Picture)

**✅ Good Path — Create Book**

```
POST http://localhost:8081/api/staff/books
Authorization: Bearer {{staff_token}}
Content-Type: multipart/form-data
```

**Form Data:**
| Key | Value |
|---|---|
| title | The Art of War |
| authorId | 3 |
| isbn | 978-1590302255 |
| category | Philosophy |
| totalCopies | 3 |
| availableCopies | 3 |
| pages | 273 |
| deweyCode | 355.4 |
| municipalRef | 90061 |
| libraryRef | 1061 |
| coverImage | *(file upload: art_of_war.jpg)* |

**Expected Response (201 Created):**
```json
{
  "id": 61,
  "title": "The Art of War",
  "author": { "id": 3, "name": "Sun Tzu" },
  "isbn": "978-1590302255",
  "category": "Philosophy",
  "totalCopies": 3,
  "availableCopies": 3,
  "pages": 273,
  "deweyCode": "355.4",
  "municipalRef": "90061",
  "libraryRef": "1061",
  "coverImage": "/uploads/books/uuid-filename.jpg"
}
```

**❌ Negative Path — Member Trying to Create Book**

```
POST http://localhost:8081/api/staff/books
Authorization: Bearer {{member_token}}
Content-Type: multipart/form-data
```

**Expected Response (403 Forbidden)**

> **Screenshot Placeholder** — *Insert Postman screenshot of book creation with multipart form data.*

---

##### 3.4 Author CRUD

**✅ Good Path — Create Author**

```
POST http://localhost:8081/api/staff/authors
Authorization: Bearer {{staff_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "name": "Sun Tzu",
  "biography": "Ancient Chinese military strategist and philosopher."
}
```

**Expected Response (201 Created):** Author object.

**❌ Negative Path — Delete Author with Books**

```
DELETE http://localhost:8081/api/staff/authors/1
Authorization: Bearer {{staff_token}}
```

**Expected Response (400 Bad Request):**
```json
{
  "error": "Cannot delete author with existing books"
}
```

---

##### 3.5 Borrow & Return

**✅ Good Path — Issue Book**

```
POST http://localhost:8081/api/staff/transactions/issue?userId=5&bookId=10
Authorization: Bearer {{staff_token}}
```

**Expected Response (201 Created):**
```json
{
  "id": 25,
  "user": { "id": 5, "username": "member1" },
  "book": { "id": 10, "title": "Sapiens" },
  "issueDate": "2026-06-18",
  "dueDate": "2026-07-02",
  "status": "BORROWED",
  "availableCopiesRemaining": 2
}
```

**✅ Good Path — Return Book**

```
PUT http://localhost:8081/api/staff/transactions/return/25
Authorization: Bearer {{staff_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "returnDate": "2026-07-01",
  "bookCondition": "GOOD",
  "conditionNotes": "Returned in good condition"
}
```

**Expected Response (200 OK):** Transaction updated with return details; no fine (on-time return).

**✅ Good Path — Return Book Late (Auto Fine)**

```
PUT http://localhost:8081/api/staff/transactions/return/26
Authorization: Bearer {{staff_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "returnDate": "2026-07-10",
  "bookCondition": "FAIR",
  "conditionNotes": "Minor wear on cover"
}
```

**Expected Response (200 OK):**
```json
{
  "id": 26,
  "returnDate": "2026-07-10",
  "status": "RETURNED",
  "bookCondition": "FAIR",
  "conditionNotes": "Minor wear on cover",
  "fineAmount": 80.00,
  "fine": {
    "id": 15,
    "amount": 80.00,
    "status": "UNPAID"
  }
}
```

**❌ Negative Path — Issue Unavailable Book**

```
POST http://localhost:8081/api/staff/transactions/issue?userId=5&bookId=99
Authorization: Bearer {{staff_token}}
```

*(where bookId=99 has 0 available copies)*

**Expected Response (400 Bad Request):**
```json
{
  "error": "Book is not available for borrowing"
}
```

**❌ Negative Path — Return Without Condition**

```
PUT http://localhost:8081/api/staff/transactions/return/27
Authorization: Bearer {{staff_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "returnDate": "2026-07-01"
}
```

**Expected Response (400 Bad Request):**
```json
{
  "error": "Book condition is required"
}
```

> **Screenshot Placeholder** — *Insert Postman screenshots of issue, return (on-time), return (late with fine), and error cases.*

---

##### 3.6 & 3.7 Fines Management

**✅ Good Path — View Fines (Filtered)**

```
GET http://localhost:8081/api/staff/fines?status=UNPAID
Authorization: Bearer {{staff_token}}
```

**Expected Response (200 OK):**
```json
[
  {
    "id": 15,
    "transactionId": 26,
    "memberName": "member1",
    "bookTitle": "Sapiens",
    "amount": 80.00,
    "returnDate": "2026-07-10",
    "status": "UNPAID"
  }
]
```

**✅ Good Path — Mark Fine as Paid**

```
PUT http://localhost:8081/api/staff/fines/15/pay
Authorization: Bearer {{staff_token}}
```

**Expected Response (200 OK):**
```json
{
  "id": 15,
  "amount": 80.00,
  "paidDate": "2026-07-12",
  "status": "PAID"
}
```

**✅ Good Path — Fine Statistics**

```
GET http://localhost:8081/api/staff/fines/stats
Authorization: Bearer {{staff_token}}
```

**Expected Response (200 OK):**
```json
{
  "totalFines": 15,
  "totalAmount": 1200.00,
  "paidAmount": 800.00,
  "unpaidAmount": 400.00,
  "paidCount": 10,
  "unpaidCount": 5
}
```

> **Screenshot Placeholder** — *Insert Postman screenshots of fines list, mark as paid, and statistics.*

---

##### 3.9 Reservations

**✅ Good Path — View All Reservations (Staff)**

```
GET http://localhost:8081/api/staff/reservations
Authorization: Bearer {{staff_token}}
```

**Expected Response (200 OK):** Array of reservation objects with member and book details.

**✅ Good Path — Acknowledge Reservation**

```
PATCH http://localhost:8081/api/staff/reservations/5/acknowledge
Authorization: Bearer {{staff_token}}
```

**Expected Response (200 OK):** Reservation status updated to `ACKNOWLEDGED`.

**✅ Good Path — Approve Reservation (Auto-Issues Book)**

```
PATCH http://localhost:8081/api/staff/reservations/5/status
Authorization: Bearer {{staff_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "status": "APPROVED"
}
```

**Expected Response (200 OK):** Reservation approved; book automatically issued to member.

**❌ Negative Path — Member Creating Reservation for Non-existent Book**

```
POST http://localhost:8081/api/reservations
Authorization: Bearer {{member_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "bookId": 9999
}
```

**Expected Response (404 Not Found):**
```json
{
  "error": "Book not found"
}
```

> **Screenshot Placeholder** — *Insert Postman screenshots of reservation workflow (view, acknowledge, approve).*

---

##### 3.10 Notifications

**✅ Good Path — Create Notification (Staff)**

```
POST http://localhost:8081/api/notifications
Authorization: Bearer {{staff_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "title": "New Arrivals",
  "message": "Check out our latest collection of science fiction books!",
  "userId": 5
}
```

**Expected Response (201 Created):** Notification object.

**✅ Good Path — Member Views Own Notifications**

```
GET http://localhost:8081/api/notifications
Authorization: Bearer {{member_token}}
```

**Expected Response (200 OK):** Only notifications belonging to the authenticated member.

**✅ Good Path — Mark Notification as Read**

```
PUT http://localhost:8081/api/notifications/10/read
Authorization: Bearer {{member_token}}
```

**Expected Response (200 OK):** Notification `read` field set to `true`.

**✅ Good Path — Mark All as Read**

```
PUT http://localhost:8081/api/notifications/read-all
Authorization: Bearer {{member_token}}
```

**Expected Response (200 OK):** All member's notifications marked as read.

**❌ Negative Path — Member Accessing Another Member's Notification**

```
PUT http://localhost:8081/api/notifications/99/read
Authorization: Bearer {{member_token_id_5}}
```

*(notification ID 99 belongs to a different member)*

**Expected Response (403 Forbidden):** Member validation prevents cross-member access.

> **Screenshot Placeholder** — *Insert Postman screenshots of notification creation and member notification view.*

---

#### Quick cURL Commands

```bash
# Login and get token
curl -X POST http://localhost:8081/api/auth/login -H "Content-Type: application/json" -d @login.json

# Get admin stats (replace TOKEN with actual JWT)
curl -X GET http://localhost:8081/api/admin/stats -H "Authorization: Bearer TOKEN"

# Get staff dashboard stats
curl -X GET http://localhost:8081/api/staff/dashboard/stats -H "Authorization: Bearer TOKEN"

# Create a book (multipart)
curl -X POST http://localhost:8081/api/staff/books -H "Authorization: Bearer TOKEN" -F "title=New Book" -F "authorId=1" -F "isbn=978-1234" -F "category=Fiction" -F "totalCopies=3" -F "coverImage=@book.jpg"

# Issue a book
curl -X POST "http://localhost:8081/api/staff/transactions/issue?userId=5&bookId=10" -H "Authorization: Bearer TOKEN"

# Return a book
curl -X PUT http://localhost:8081/api/staff/transactions/return/25 -H "Authorization: Bearer TOKEN" -H "Content-Type: application/json" -d '{"returnDate":"2026-07-01","bookCondition":"GOOD","conditionNotes":"Good condition"}'

# Get fines
curl -X GET "http://localhost:8081/api/staff/fines?status=UNPAID" -H "Authorization: Bearer TOKEN"

# Mark fine as paid
curl -X PUT http://localhost:8081/api/staff/fines/15/pay -H "Authorization: Bearer TOKEN"
```

---

### 10.6 Usability Testing — Core HCI Principles

| HCI Principle | How It's Checked in LMS | Status |
|---|---|---|
| **Visibility** | All navigation items visible in sidebar; current page highlighted; breadcrumbs on nested pages | ✅ Implemented |
| **Feedback** | Success/error toast messages on all CRUD operations; loading spinners during API calls | ✅ Implemented |
| **Consistency** | Uniform colour scheme, button styles, form layouts, and card designs across all pages | ✅ Implemented |
| **Affordance** | Buttons look clickable; input fields have clear labels and placeholders; icons indicate actions (edit, delete, view) | ✅ Implemented |
| **Error Prevention** | Frontend validation prevents empty submissions; confirmation dialogs before delete operations; duplicate checks on username/email | ✅ Implemented |
| **Error Recovery** | Clear error messages guide users to fix issues; "Cancel" buttons on all forms; status toggle allows re-activation of deactivated accounts | ✅ Implemented |
| **Flexibility & Efficiency** | Search/filter on member and transaction lists; role-based dashboards show relevant KPIs; keyboard-accessible forms | ✅ Implemented |
| **Aesthetic & Minimalist Design** | Clean layout with adequate whitespace; no unnecessary UI elements; charts and cards for data visualization | ✅ Implemented |
| **Help & Documentation** | Form field labels are self-explanatory; tooltips on complex fields; about page provides library information | ✅ Implemented |
| **User Control & Freedom** | Users can navigate freely within their role; undo actions (e.g., un-mark notification as read); logout available from any page | ✅ Implemented |

> **Screenshot Placeholder** — *Insert screenshots demonstrating HCI principles: consistent button styles, confirmation dialogs, error messages, and loading states.*

---

## 11. Screenshots

> **Note:** This section contains placeholders for project screenshots to be inserted when converting to a Word document.

### 11.1 Login & Authentication

> **Screenshot Placeholder** — *Login page with username/password fields*

> **Screenshot Placeholder** — *OTP request and verification screens*

> **Screenshot Placeholder** — *Access Denied page for unauthorized access*

### 11.2 Admin Dashboard

> **Screenshot Placeholder** — *Admin Dashboard with KPI cards, gender pie chart, age bar chart*

> **Screenshot Placeholder** — *Member Management page — table view with action buttons*

> **Screenshot Placeholder** — *Member creation/edit form*

> **Screenshot Placeholder** — *Registration Management page — staff/admin account list*

> **Screenshot Placeholder** — *About Statements management page*

### 11.3 Staff Dashboard

> **Screenshot Placeholder** — *Staff Dashboard with KPI cards and category distribution chart*

> **Screenshot Placeholder** — *Book Inventory page — cards with cover images and full metadata*

> **Screenshot Placeholder** — *Book creation/edit form with cover image upload*

> **Screenshot Placeholder** — *Author Management page*

> **Screenshot Placeholder** — *Transaction Management page — borrow/return with filters*

> **Screenshot Placeholder** — *Book return form with condition dropdown and notes field*

> **Screenshot Placeholder** — *Fines Management page — list with filter and pay button*

> **Screenshot Placeholder** — *Reservations page — list with acknowledge/approve/reject actions*

> **Screenshot Placeholder** — *Notification creation form and member notification dropdown*

### 11.4 Member Portal

> **Screenshot Placeholder** — *Member dashboard / book catalogue browse page*

> **Screenshot Placeholder** — *Book reservation confirmation*

> **Screenshot Placeholder** — *Member profile page with edit and change password options*

> **Screenshot Placeholder** — *Member feedback submission page*

### 11.5 Postman Testing

> **Screenshot Placeholder** — *Postman workspace overview showing all collections*

> **Screenshot Placeholder** — *Login endpoint — good path (200 OK with token)*

> **Screenshot Placeholder** — *Login endpoint — negative path (401 Unauthorized)*

> **Screenshot Placeholder** — *Admin stats endpoint with authorization header*

> **Screenshot Placeholder** — *Book creation with multipart form data*

> **Screenshot Placeholder** — *Transaction issue and return flow*

> **Screenshot Placeholder** — *Fine management — mark as paid*

> **Screenshot Placeholder** — *Role-based access denial (403 Forbidden)*

---

## 12. Conclusion

### 12.1 Key Achievements

1. **Complete Digital Transformation:** Successfully replaced the Talahena Public Library's manual, paper-based operations with a comprehensive digital system covering authentication, catalogue management, circulation, fines, reservations, notifications, and reporting.

2. **Full-Stack Implementation:** Delivered a production-ready application using Spring Boot 3.2.4 (backend), React 18 with Vite (frontend), and MySQL (database) — demonstrating proficiency in modern full-stack development.

3. **Security-First Design:** Implemented JWT authentication, BCrypt password hashing, role-based authorization (`@PreAuthorize`), and SpEL-based IDOR protection — ensuring data security across all user roles.

4. **Automated Business Logic:** Automatic fine calculation on overdue returns, automatic book issuance on reservation approval, and automatic available copy management on issue/return — reducing manual errors and staff workload.

5. **Rich Metadata Cataloguing:** 60 books catalogued with complete metadata including pages, Dewey Decimal Classification codes, municipal reference numbers, and library reference numbers — meeting the Negombo Municipal Council's cataloguing standards.

6. **Analytical Dashboards:** Visual dashboards with KPI cards, pie charts (gender distribution, category distribution with Dewey codes), and bar charts (age distribution) — enabling data-driven reporting to the Municipal Council.

7. **Object-Oriented Design:** Backend architecture follows SOLID principles with clear separation of concerns (Controller → Service → Repository), documented in code comments.

8. **Comprehensive API:** 50+ RESTful API endpoints with consistent response formats, proper HTTP status codes, and comprehensive error handling — tested via Postman with both good path and negative path scenarios.

### 12.2 System Limitations and Future Enhancements

| Limitation | Future Enhancement |
|---|---|
| Single-server deployment | Containerized deployment with Docker and load balancing |
| Local file storage for images | Cloud storage integration (AWS S3, Google Cloud Storage) |
| No email queue for OTP | Integration with reliable email service (SendGrid, AWS SES) |
| No multi-language support | Sinhala and Tamil language support for local community |
| No barcode/RFID integration | Barcode scanner integration for faster book check-in/check-out |
| No mobile application | React Native mobile app for members |
| Basic fine calculation (flat rate) | Configurable fine policies (per-day, per-hour, grace periods) |
| No inter-branch support | Multi-branch library network support for Negombo Municipal Council |
| No automated reminders | Scheduled email/SMS reminders for overdue books |
| Limited reporting | Advanced reporting with exportable PDF/Excel reports |

### 12.3 Expected Organizational Impact

- **Operational Efficiency:** Reduction in time spent on book searches (from 10-15 minutes to seconds), member lookups, and fine calculations
- **Data Accuracy:** Elimination of manual calculation errors in fines and record-keeping
- **Member Experience:** Improved satisfaction through self-service registration, online reservations, and digital notifications
- **Management Visibility:** Real-time dashboards provide the Head Librarian and Municipal Council with instant visibility into library operations
- **Accountability:** Role-based access control and transaction audit trails ensure clear accountability for all operations
- **Scalability:** The digital system can accommodate growth in membership, catalogue size, and transaction volume without proportional increase in staff effort
- **Community Impact:** By modernizing library services, the Talahena Public Library can better serve its diverse community of students, professionals, researchers, and general readers

---

*Report prepared for the OOAD Module Assignment — SLIIT*

*Talahena Public Library Management System © 2026*
