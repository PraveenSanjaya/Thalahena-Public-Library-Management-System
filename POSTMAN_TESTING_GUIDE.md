# Thalahena Public Library — Postman Testing Guide

> **Base URL:** `http://localhost:8081`  
> **Version:** 3.0 — Complete Epic Coverage with Positive & Negative Paths, User Management, and Enhanced Samples

---

## Legend

| Symbol | Meaning | Description |
|--------|---------|-------------|
| ✅ | **Positive Path** | Valid request with correct data, auth, and permissions — expect success |
| ❌ | **Negative Path** | Invalid request (bad data, wrong role, missing auth) — expect error |
| 🔒 | **Auth Required** | Request requires `Authorization: Bearer <token>` header |
| 📎 | **Content-Type** | Specify `Content-Type: application/json` unless noted otherwise |

**HTTP Status Code Reference:**

| Code | Name | When |
|------|------|------|
| `200` | OK | Successful GET/PUT/DELETE |
| `201` | Created | Successful POST (resource created) |
| `400` | Bad Request | Validation error, duplicate, or malformed input |
| `401` | Unauthorized | Missing or invalid JWT token |
| `403` | Forbidden | Valid token but insufficient role/permissions |
| `404` | Not Found | Resource does not exist |
| `500` | Internal Server Error | Unexpected server failure |

---

## Postman Environment Variables

Create a Postman environment with these variables before testing:

```
base_url     = http://localhost:8081
admin_token  = (auto-set after admin login)
staff_token  = (auto-set after staff login)
member_token = (auto-set after member login)
```

**Auto-set token script** (paste in the **Tests** tab of login requests):

```javascript
const response = pm.response.json();
if (response.token) {
    const role = response.role;
    if (role === "ADMIN") pm.environment.set("admin_token", response.token);
    if (role === "STAFF") pm.environment.set("staff_token", response.token);
    if (role === "MEMBER") pm.environment.set("member_token", response.token);
}
```

---

## Seeded Test Accounts

On first startup, `DataInitializer` creates:

| Role | Username | Password | Expected Role in Response |
|------|----------|----------|---------------------------|
| Admin | `admin1` | `admin123` | `ADMIN` |
| Staff | `staff1` | `staff123` | `STAFF` |
| Member | `user1` | `user123` | `MEMBER` |

---

## Quick Reference — All Endpoints by Epic

### Epic 1: Authentication & Access Control

| # | Method | Endpoint | Auth | Path |
|---|--------|----------|------|------|
| 1 | POST | `/api/auth/login` | None | ✅❌ |
| 2 | POST | `/api/auth/request-otp` | None | ✅❌ |
| 3 | POST | `/api/auth/verify-otp` | None | ✅❌ |
| 4 | POST | `/api/auth/register` | None | ✅❌ |
| 5 | GET | `/api/transactions/user/{userId}` | MEMBER (own) | ✅❌ |

### Epic 2: Admin Dashboard

| # | Method | Endpoint | Auth | Path |
|---|--------|----------|------|------|
| 6 | GET | `/api/admin/stats` | ADMIN | ✅ |
| 7 | GET | `/api/admin/members` | ADMIN | ✅❌ |
| 8 | GET | `/api/admin/members/search?query=` | ADMIN | ✅ |
| 9 | GET | `/api/admin/members/{id}` | ADMIN | ✅❌ |
| 10 | POST | `/api/admin/members` | ADMIN | ✅❌ |
| 11 | PUT | `/api/admin/members/{id}` | ADMIN | ✅❌ |
| 12 | PUT | `/api/admin/members/{id}/status` | ADMIN | ✅❌ |
| 13 | DELETE | `/api/admin/members/{id}` | ADMIN | ✅❌ |
| 14 | GET | `/api/admin/registrations` | ADMIN | ✅ |
| 15 | POST | `/api/admin/registrations` | ADMIN | ✅❌ |
| 16 | PUT | `/api/admin/registrations/{id}` | ADMIN | ✅ |
| 17 | DELETE | `/api/admin/registrations/{id}` | ADMIN | ✅ |
| 18 | GET | `/api/about` | Any | ✅ |
| 19 | POST | `/api/admin/about` | ADMIN | ✅❌ |
| 20 | PUT | `/api/admin/about/{id}` | ADMIN | ✅❌ |
| 21 | DELETE | `/api/admin/about/{id}` | ADMIN | ✅❌ |
| 22 | GET | `/api/users` | ADMIN | ✅❌ |
| 23 | POST | `/api/users` | ADMIN | ✅❌ |
| 24 | PUT | `/api/users/{id}` | ADMIN | ✅❌ |
| 25 | DELETE | `/api/users/{id}` | ADMIN | ✅❌ |
| 26 | POST | `/api/users/{id}/profile-picture` | ADMIN | ✅ |

### Epic 3: Staff Dashboard

| # | Method | Endpoint | Auth | Path |
|---|--------|----------|------|------|
| 27 | GET | `/api/staff/dashboard/stats` | STAFF/ADMIN | ✅ |
| 28 | GET | `/api/staff/transactions/counters` | STAFF/ADMIN | ✅ |
| 29 | GET | `/api/staff/members` | STAFF/ADMIN | ✅❌ |
| 30 | GET | `/api/staff/members/search?query=` | STAFF/ADMIN | ✅ |
| 31 | GET | `/api/staff/members/{id}/borrow-history` | STAFF/ADMIN | ✅ |
| 32 | GET | `/api/staff/books` | STAFF/ADMIN | ✅ |
| 33 | GET | `/api/staff/books/{id}` | STAFF/ADMIN | ✅❌ |
| 34 | POST | `/api/staff/books` | STAFF/ADMIN | ✅❌ |
| 35 | PUT | `/api/staff/books/{id}` | STAFF/ADMIN | ✅ |
| 36 | POST | `/api/staff/books/{id}/cover` | STAFF/ADMIN | ✅ |
| 37 | DELETE | `/api/staff/books/{id}` | STAFF/ADMIN | ✅❌ |
| 38 | GET | `/api/staff/authors` | STAFF/ADMIN | ✅❌ |
| 39 | GET | `/api/staff/authors/{id}` | STAFF/ADMIN | ✅❌ |
| 40 | POST | `/api/staff/authors` | STAFF/ADMIN | ✅❌ |
| 41 | PUT | `/api/staff/authors/{id}` | STAFF/ADMIN | ✅❌ |
| 42 | DELETE | `/api/staff/authors/{id}` | STAFF/ADMIN | ✅❌ |
| 43 | GET | `/api/staff/transactions` | STAFF/ADMIN | ✅ |
| 44 | POST | `/api/staff/transactions/issue` | STAFF/ADMIN | ✅❌ |
| 45 | PUT | `/api/staff/transactions/return/{id}` | STAFF/ADMIN | ✅❌ |
| 46 | PUT | `/api/staff/transactions/{id}/update` | STAFF/ADMIN | ✅❌ |
| 47 | GET | `/api/staff/fines` | STAFF/ADMIN | ✅❌ |
| 48 | GET | `/api/staff/fines/stats` | STAFF/ADMIN | ✅ |
| 49 | GET | `/api/staff/fines/{id}` | STAFF/ADMIN | ✅❌ |
| 50 | PUT | `/api/staff/fines/{id}` | STAFF/ADMIN | ✅❌ |
| 51 | PUT | `/api/staff/fines/{id}/pay` | STAFF/ADMIN | ✅❌ |
| 52 | DELETE | `/api/staff/fines/{id}` | STAFF/ADMIN | ✅❌ |
| 53 | GET | `/api/staff/reservations` | STAFF/ADMIN | ✅ |
| 54 | GET | `/api/staff/reservations/{id}` | STAFF/ADMIN | ✅❌ |
| 55 | PATCH | `/api/staff/reservations/{id}/acknowledge` | STAFF/ADMIN | ✅❌ |
| 56 | PATCH | `/api/staff/reservations/{id}/status` | STAFF/ADMIN | ✅❌ |
| 57 | GET | `/api/notifications` | Any role | ✅❌ |
| 58 | GET | `/api/notifications/{id}` | Any role | ✅❌ |
| 59 | POST | `/api/notifications` | STAFF/ADMIN | ✅❌ |
| 60 | PUT | `/api/notifications/{id}` | STAFF/ADMIN | ✅❌ |
| 61 | PUT | `/api/notifications/{id}/read` | Any role | ✅ |
| 62 | PUT | `/api/notifications/read-all` | Any role | ✅ |
| 63 | DELETE | `/api/notifications/{id}` | STAFF/ADMIN | ✅❌ |

### Member Endpoints

| # | Method | Endpoint | Auth | Path |
|---|--------|----------|------|------|
| 64 | GET | `/api/feedback` | Any role | ✅ |
| 65 | POST | `/api/feedback` | MEMBER | ✅❌ |
| 66 | PUT | `/api/feedback/{id}` | MEMBER | ✅❌ |
| 67 | DELETE | `/api/feedback/{id}` | MEMBER | ✅❌ |
| 68 | POST | `/api/reservations` | Any auth | ✅❌ |
| 69 | GET | `/api/reservations/user/{userId}` | Any auth | ✅ |
| 70 | GET | `/api/transactions/user/{userId}` | MEMBER (own) | ✅❌ |

> **Total: 70 endpoints** with **100+ test scenarios** covering positive and negative paths

---

## Epic 1 — Authentication & Access Control

### 1.1 Login (Username + Password)

#### ✅ 1.1.1 Login as Admin

```
POST {{base_url}}/api/auth/login
Content-Type: application/json
```

**Request Body:**
```json
{
  "username": "admin1",
  "password": "admin123"
}
```

**Response — `200 OK`:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbjEiLCJpYXQiOjE3...",
  "id": 1,
  "username": "admin1",
  "email": "admin1@library.com",
  "role": "ADMIN"
}
```

---

#### ✅ 1.1.2 Login as Staff

```
POST {{base_url}}/api/auth/login
Content-Type: application/json
```

**Request Body:**
```json
{
  "username": "staff1",
  "password": "staff123"
}
```

**Response — `200 OK`:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "id": 2,
  "username": "staff1",
  "email": "staff1@library.com",
  "role": "STAFF"
}
```

---

#### ✅ 1.1.3 Login as Member

```
POST {{base_url}}/api/auth/login
Content-Type: application/json
```

**Request Body:**
```json
{
  "username": "user1",
  "password": "user123"
}
```

**Response — `200 OK`:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "id": 3,
  "username": "user1",
  "email": "user1@gmail.com",
  "role": "MEMBER"
}
```

---

#### ❌ 1.1.4 Login with Wrong Password

```
POST {{base_url}}/api/auth/login
Content-Type: application/json
```

**Request Body:**
```json
{
  "username": "admin1",
  "password": "wrongpassword"
}
```

**Response — `401 Unauthorized`:**
```json
{
  "message": "Bad credentials"
}
```

---

#### ❌ 1.1.5 Login with Non-Existent User

```
POST {{base_url}}/api/auth/login
Content-Type: application/json
```

**Request Body:**
```json
{
  "username": "ghost_user",
  "password": "anything"
}
```

**Response — `401 Unauthorized`:**
```json
{
  "message": "Bad credentials"
}
```

---

### 1.2 Passwordless Login via OTP

#### ✅ 1.2.1 Request OTP (Valid Email)

```
POST {{base_url}}/api/auth/request-otp
Content-Type: application/json
```

**Request Body:**
```json
{
  "email": "user1@gmail.com"
}
```

**Response — `200 OK`:**
```json
{
  "message": "OTP sent to your email."
}
```

> 📧 Check the configured email inbox for a 6-digit OTP code.

---

#### ❌ 1.2.2 Request OTP (Unregistered Email)

```
POST {{base_url}}/api/auth/request-otp
Content-Type: application/json
```

**Request Body:**
```json
{
  "email": "unknown@gmail.com"
}
```

**Response — `400 Bad Request`:**
```json
{
  "message": "Error: No account found with this email!"
}
```

---

#### ✅ 1.2.3 Verify OTP (Correct Code)

```
POST {{base_url}}/api/auth/verify-otp
Content-Type: application/json
```

**Request Body:**
```json
{
  "email": "user1@gmail.com",
  "otp": "123456"
}
```

**Response — `200 OK`:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "id": 3,
  "username": "user1",
  "email": "user1@gmail.com",
  "role": "MEMBER"
}
```

---

#### ❌ 1.2.4 Verify OTP (Wrong Code)

```
POST {{base_url}}/api/auth/verify-otp
Content-Type: application/json
```

**Request Body:**
```json
{
  "email": "user1@gmail.com",
  "otp": "000000"
}
```

**Response — `400 Bad Request`:**
```json
{
  "message": "Error: Invalid or expired OTP!"
}
```

---

### 1.3 User Registration (Self-Service — MEMBER only)

#### ✅ 1.3.1 Register New Member

```
POST {{base_url}}/api/auth/register
Content-Type: application/json
```

**Request Body:**
```json
{
  "username": "kamal_perera",
  "email": "kamal@gmail.com",
  "password": "password123",
  "firstName": "Kamal",
  "lastName": "Perera"
}
```

**Response — `200 OK`:**
```json
{
  "message": "User registered successfully!"
}
```

---

#### ❌ 1.3.2 Register with Duplicate Username

```
POST {{base_url}}/api/auth/register
Content-Type: application/json
```

**Request Body:**
```json
{
  "username": "user1",
  "email": "newemail@gmail.com",
  "password": "password123",
  "firstName": "Test",
  "lastName": "User"
}
```

**Response — `400 Bad Request`:**
```json
{
  "message": "Error: Username is already taken!"
}
```

---

#### ❌ 1.3.3 Register with Duplicate Email

```
POST {{base_url}}/api/auth/register
Content-Type: application/json
```

**Request Body:**
```json
{
  "username": "unique_user",
  "email": "user1@gmail.com",
  "password": "password123",
  "firstName": "Test",
  "lastName": "User"
}
```

**Response — `400 Bad Request`:**
```json
{
  "message": "Error: Email is already in use!"
}
```

---

### 1.4 Role-Based Authorization Tests

#### ❌ 1.4.1 Access Admin Endpoint with MEMBER Token

```
GET {{base_url}}/api/admin/stats
Authorization: Bearer {{member_token}}
```

**Response — `403 Forbidden`**

---

#### ❌ 1.4.2 Access Admin Endpoint with STAFF Token

```
GET {{base_url}}/api/admin/stats
Authorization: Bearer {{staff_token}}
```

**Response — `403 Forbidden`**

---

#### ❌ 1.4.3 Access Staff Endpoint with MEMBER Token

```
GET {{base_url}}/api/staff/transactions
Authorization: Bearer {{member_token}}
```

**Response — `403 Forbidden`**

---

#### ❌ 1.4.4 Access Any Protected Endpoint Without Token

```
GET {{base_url}}/api/admin/stats
```
*(No Authorization header)*

**Response — `401 Unauthorized`**

---

#### ✅ 1.4.5 MEMBER Access Own Borrowing History (IDOR Protected)

```
GET {{base_url}}/api/transactions/user/3
Authorization: Bearer {{member_token}}
```

**Response — `200 OK`:**
```json
[
  {
    "id": 1,
    "userId": 3,
    "memberName": "user1",
    "bookTitle": "Clean Code",
    "issueDate": "2026-06-01",
    "dueDate": "2026-06-15",
    "returnDate": null,
    "status": "ISSUED",
    "fineAmount": 0.0,
    "bookCondition": null
  }
]
```

---

#### ❌ 1.4.6 MEMBER Access Another User's History (IDOR Blocked)

```
GET {{base_url}}/api/transactions/user/5
Authorization: Bearer {{member_token}}
```

*(Assumes member's own ID is 3, not 5)*

**Response — `403 Forbidden`**

> SpEL rule: `hasRole('MEMBER') and #userId == principal.id` blocks this.

---

## Epic 2 — Admin Dashboard

> 🔒 **All endpoints below require:** `Authorization: Bearer {{admin_token}}`

### 2.1 Admin Dashboard Stats

#### ✅ 2.1.1 Get Dashboard Statistics

```
GET {{base_url}}/api/admin/stats
Authorization: Bearer {{admin_token}}
```

**Response — `200 OK`:**
```json
{
  "totalUsers": 16,
  "staffCount": 3,
  "adminCount": 1,
  "activeMembers": 10,
  "inactiveMembers": 2,
  "genderDistribution": {
    "MALE": 7,
    "FEMALE": 5
  },
  "ageDistribution": {
    "≤18": 4,
    ">18": 8
  },
  "recentFeedback": [
    {
      "id": 1,
      "message": "Great library service!",
      "username": "user1",
      "createdAt": "2026-06-10T14:30:00"
    }
  ]
}
```

---

### 2.2 Member CRUD (Admin)

#### ✅ 2.2.1 Get All Members

```
GET {{base_url}}/api/admin/members
Authorization: Bearer {{admin_token}}
```

**Response — `200 OK`:**
```json
[
  {
    "id": 3,
    "username": "user1",
    "email": "user1@gmail.com",
    "firstName": "Amara",
    "lastName": "Perera",
    "gender": "MALE",
    "birthDate": "1995-03-15",
    "age": 31,
    "membershipDate": "2026-01-01",
    "phone": "+94771234567",
    "whatsapp": "+94771234567",
    "role": "MEMBER",
    "isActive": true,
    "profilePicture": null
  }
]
```

---

#### ✅ 2.2.2 Search Members

```
GET {{base_url}}/api/admin/members?search=Amara
Authorization: Bearer {{admin_token}}
```

**Response — `200 OK`:** Array of matching `MemberDTO` objects.

---

#### ✅ 2.2.3 Search Members (Dedicated Search Endpoint)

```
GET {{base_url}}/api/admin/members/search?query=Perera
Authorization: Bearer {{admin_token}}
```

**Response — `200 OK`:**
```json
[
  {
    "id": 3,
    "username": "user1",
    "email": "user1@gmail.com",
    "firstName": "Amara",
    "lastName": "Perera",
    "gender": "MALE",
    "birthDate": "1995-03-15",
    "age": 31,
    "membershipDate": "2026-01-01",
    "phone": "+94771234567",
    "whatsapp": "+94771234567",
    "role": "MEMBER",
    "isActive": true,
    "profilePicture": null
  }
]
```

---

#### ✅ 2.2.4 Get Member by ID

```
GET {{base_url}}/api/admin/members/3
Authorization: Bearer {{admin_token}}
```

**Response — `200 OK`:** Single `MemberDTO` object.

---

#### ❌ 2.2.5 Get Non-Existent Member

```
GET {{base_url}}/api/admin/members/9999
Authorization: Bearer {{admin_token}}
```

**Response — `404 Not Found`**

---

#### ✅ 2.2.6 Create New Member

```
POST {{base_url}}/api/admin/members
Authorization: Bearer {{admin_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "firstName": "Nimal",
  "lastName": "Silva",
  "username": "nimal_silva",
  "email": "nimal@gmail.com",
  "password": "member123",
  "phone": "+94779876543",
  "whatsapp": "+94779876543",
  "gender": "MALE",
  "birthDate": "1998-07-20",
  "membershipDate": "2026-06-01",
  "active": true
}
```

**Response — `200 OK`:**
```json
{
  "id": 17,
  "username": "nimal_silva",
  "email": "nimal@gmail.com",
  "firstName": "Nimal",
  "lastName": "Silva",
  "gender": "MALE",
  "membershipDate": "2026-06-01",
  "isActive": true
}
```

---

#### ❌ 2.2.7 Create Member — Duplicate Username

```
POST {{base_url}}/api/admin/members
Authorization: Bearer {{admin_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "firstName": "Test",
  "lastName": "User",
  "username": "user1",
  "email": "test_new@gmail.com",
  "password": "password123"
}
```

**Response — `400 Bad Request`:**
```json
"Error: Username is already taken!"
```

---

#### ❌ 2.2.8 Create Member — Duplicate Email

```
POST {{base_url}}/api/admin/members
Authorization: Bearer {{admin_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "firstName": "Test",
  "lastName": "User",
  "username": "brand_new_user",
  "email": "user1@gmail.com",
  "password": "password123"
}
```

**Response — `400 Bad Request`:**
```json
"Error: Email is already in use!"
```

---

#### ✅ 2.2.9 Update Member

```
PUT {{base_url}}/api/admin/members/3
Authorization: Bearer {{admin_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "firstName": "Amara",
  "lastName": "Perera-Updated",
  "username": "amara_new",
  "email": "amara_new@gmail.com",
  "phone": "+94770000000",
  "gender": "MALE",
  "birthDate": "1995-03-15",
  "membershipDate": "2026-01-01",
  "active": true
}
```

**Response — `200 OK`:** Updated `MemberDTO`.

---

#### ❌ 2.2.10 Update Member — Username Already Taken

```
PUT {{base_url}}/api/admin/members/3
Authorization: Bearer {{admin_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "username": "staff1",
  "email": "user1@gmail.com",
  "firstName": "Amara",
  "lastName": "Perera"
}
```

**Response — `400 Bad Request`:**
```json
"Error: Username is already taken!"
```

---

#### ✅ 2.2.11 Toggle Member Status (Active → Inactive)

```
PUT {{base_url}}/api/admin/members/3/status
Authorization: Bearer {{admin_token}}
```

**Response — `200 OK`:**
```json
{
  "id": 3,
  "username": "user1",
  "isActive": false
}
```

> Call again to toggle back to Active.

---

#### ✅ 2.2.12 Delete Member

```
DELETE {{base_url}}/api/admin/members/17
Authorization: Bearer {{admin_token}}
```

**Response — `200 OK`:**
```json
"Member deleted successfully"
```

---

#### ❌ 2.2.13 Delete Non-Member User (Staff/Admin)

```
DELETE {{base_url}}/api/admin/members/1
Authorization: Bearer {{admin_token}}
```

*(Assuming ID 1 is admin1)*

**Response — `400 Bad Request`:**
```json
"Error: User is not a member!"
```

---

#### ❌ 2.2.14 Toggle Status for Non-Member User

```
PUT {{base_url}}/api/admin/members/1/status
Authorization: Bearer {{admin_token}}
```

*(Assuming ID 1 is admin1)*

**Response — `400 Bad Request`:**
```json
"Error: User is not a member!"
```

---

#### ❌ 2.2.15 Update Non-Existent Member

```
PUT {{base_url}}/api/admin/members/9999
Authorization: Bearer {{admin_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "firstName": "Ghost",
  "lastName": "User",
  "email": "ghost@library.com"
}
```

**Response — `404 Not Found`**

---

#### ❌ 2.2.16 Access Members with MEMBER Token (Forbidden)

```
GET {{base_url}}/api/admin/members
Authorization: Bearer {{member_token}}
```

**Response — `403 Forbidden`**

---

### 2.3 Registration CRUD (Staff/Admin Account Management)

#### ✅ 2.3.1 Get All Staff & Admin Users

```
GET {{base_url}}/api/admin/registrations
Authorization: Bearer {{admin_token}}
```

**Response — `200 OK`:** Array of User objects (only STAFF and ADMIN roles).

---

#### ✅ 2.3.2 Create Staff Account

```
POST {{base_url}}/api/admin/registrations
Authorization: Bearer {{admin_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "firstName": "Kumari",
  "lastName": "Fernando",
  "username": "kumari_staff",
  "email": "kumari@library.com",
  "password": "staffpass123",
  "phone": "+94775555555",
  "whatsapp": "+94775555555",
  "role": "STAFF",
  "isActive": true
}
```

**Response — `200 OK`:**
```json
{
  "message": "User registered successfully",
  "userId": 18,
  "username": "kumari_staff",
  "role": "STAFF"
}
```

---

#### ✅ 2.3.3 Create Admin Account

```
POST {{base_url}}/api/admin/registrations
Authorization: Bearer {{admin_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "firstName": "Super",
  "lastName": "Admin",
  "username": "admin2",
  "email": "admin2@library.com",
  "password": "adminpass123",
  "role": "ADMIN",
  "isActive": true
}
```

**Response — `200 OK`**

---

#### ❌ 2.3.4 Create Registration with MEMBER Role (Rejected)

```
POST {{base_url}}/api/admin/registrations
Authorization: Bearer {{admin_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "firstName": "Bad",
  "lastName": "Request",
  "username": "bad_member",
  "email": "bad@library.com",
  "password": "password123",
  "role": "MEMBER"
}
```

**Response — `400 Bad Request`:**
```json
"Error: Role must be STAFF or ADMIN!"
```

---

#### ❌ 2.3.5 Create Registration — Short Password

```
POST {{base_url}}/api/admin/registrations
Authorization: Bearer {{admin_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "firstName": "Short",
  "lastName": "Pass",
  "username": "shortpass_staff",
  "email": "shortpass@library.com",
  "password": "abc",
  "role": "STAFF"
}
```

**Response — `400 Bad Request`:**
```json
"Error: Password must be at least 8 characters!"
```

---

#### ✅ 2.3.6 Update Registration

```
PUT {{base_url}}/api/admin/registrations/18
Authorization: Bearer {{admin_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "firstName": "Kumari",
  "lastName": "Fernando-Updated",
  "email": "kumari_new@library.com",
  "phone": "+94770001111",
  "role": "ADMIN",
  "isActive": true
}
```

**Response — `200 OK`:** Updated User object.

---

#### ✅ 2.3.7 Delete Registration

```
DELETE {{base_url}}/api/admin/registrations/18
Authorization: Bearer {{admin_token}}
```

**Response — `200 OK`:**
```json
"User deleted successfully"
```

---

### 2.4 About Statements CRUD

#### ✅ 2.4.1 Get All About Statements

```
GET {{base_url}}/api/about
Authorization: Bearer {{admin_token}}
```

**Response — `200 OK`:**
```json
[
  {
    "id": 1,
    "content": "Welcome to Thalahena Public Library. Open 9 AM – 5 PM, Mon–Sat.",
    "updatedAt": "2026-06-01T10:00:00"
  }
]
```

---

#### ✅ 2.4.2 Create About Statement

```
POST {{base_url}}/api/admin/about
Authorization: Bearer {{admin_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "content": "Thalahena Public Library — Serving the community since 2020. Over 10,000 books across 10 Dewey Decimal categories."
}
```

**Response — `200 OK`:**
```json
{
  "id": 2,
  "content": "Thalahena Public Library — Serving the community since 2020...",
  "updatedAt": "2026-06-16T23:00:00"
}
```

---

#### ✅ 2.4.3 Update About Statement

```
PUT {{base_url}}/api/admin/about/1
Authorization: Bearer {{admin_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "content": "Updated: Holiday hours — Closed on Poya days. Regular hours resume next Monday."
}
```

**Response — `200 OK`:** Updated `AboutDTO`.

---

#### ✅ 2.4.4 Delete About Statement

```
DELETE {{base_url}}/api/admin/about/2
Authorization: Bearer {{admin_token}}
```

**Response — `200 OK`**

---

#### ❌ 2.4.5 Delete Non-Existent About Statement

```
DELETE {{base_url}}/api/admin/about/9999
Authorization: Bearer {{admin_token}}
```

**Response — `404 Not Found`**

---

#### ❌ 2.4.6 Create About with STAFF Token (Forbidden)

```
POST {{base_url}}/api/admin/about
Authorization: Bearer {{staff_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "content": "Staff trying to create about statement"
}
```

**Response — `403 Forbidden`**

---

#### ❌ 2.4.7 Update Non-Existent About Statement

```
PUT {{base_url}}/api/admin/about/9999
Authorization: Bearer {{admin_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "content": "This about statement does not exist"
}
```

**Response — `500 Internal Server Error`:**
```json
{
  "message": "About statement not found"
}
```

---

### 2.5 User Management (Admin Only)

> 🔒 **All endpoints below require:** `Authorization: Bearer {{admin_token}}`

#### ✅ 2.5.1 Get All Users

```
GET {{base_url}}/api/users
Authorization: Bearer {{admin_token}}
```

**Response — `200 OK`:**
```json
[
  {
    "id": 1,
    "username": "admin1",
    "email": "admin1@library.com",
    "firstName": "Admin",
    "lastName": "User",
    "role": "ADMIN",
    "isActive": true,
    "phone": "+94771234567",
    "gender": "MALE",
    "birthDate": "1990-01-01",
    "profilePicture": null,
    "membershipDate": "2026-01-01"
  },
  {
    "id": 2,
    "username": "staff1",
    "email": "staff1@library.com",
    "firstName": "Staff",
    "lastName": "User",
    "role": "STAFF",
    "isActive": true
  },
  {
    "id": 3,
    "username": "user1",
    "email": "user1@gmail.com",
    "firstName": "Amara",
    "lastName": "Perera",
    "role": "MEMBER",
    "isActive": true
  }
]
```

---

#### ❌ 2.5.2 Get All Users with STAFF Token (Forbidden)

```
GET {{base_url}}/api/users
Authorization: Bearer {{staff_token}}
```

**Response — `403 Forbidden`**

---

#### ✅ 2.5.3 Create User

```
POST {{base_url}}/api/users
Authorization: Bearer {{admin_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "username": "new_user_admin",
  "email": "newuser@library.com",
  "password": "securePass123",
  "firstName": "New",
  "lastName": "User",
  "role": "STAFF",
  "isActive": true
}
```

**Response — `200 OK`:** Created User object.

---

#### ❌ 2.5.4 Create User — Duplicate Username

```
POST {{base_url}}/api/users
Authorization: Bearer {{admin_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "username": "admin1",
  "email": "unique@library.com",
  "password": "password123",
  "firstName": "Duplicate",
  "lastName": "Username"
}
```

**Response — `400 Bad Request`:**
```json
"Error: Username is already taken!"
```

---

#### ❌ 2.5.5 Create User — Duplicate Email

```
POST {{base_url}}/api/users
Authorization: Bearer {{admin_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "username": "unique_admin_user",
  "email": "admin1@library.com",
  "password": "password123",
  "firstName": "Duplicate",
  "lastName": "Email"
}
```

**Response — `400 Bad Request`:**
```json
"Error: Email is already in use!"
```

---

#### ✅ 2.5.6 Update User

```
PUT {{base_url}}/api/users/2
Authorization: Bearer {{admin_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "email": "staff1_updated@library.com",
  "isActive": true
}
```

**Response — `200 OK`:** Updated User object.

---

#### ❌ 2.5.7 Update Non-Existent User

```
PUT {{base_url}}/api/users/9999
Authorization: Bearer {{admin_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "email": "ghost@library.com"
}
```

**Response — `404 Not Found`**

---

#### ✅ 2.5.8 Delete User

```
DELETE {{base_url}}/api/users/18
Authorization: Bearer {{admin_token}}
```

**Response — `200 OK`**

---

#### ❌ 2.5.9 Delete Non-Existent User

```
DELETE {{base_url}}/api/users/9999
Authorization: Bearer {{admin_token}}
```

**Response — `404 Not Found`**

---

#### ✅ 2.5.10 Upload Profile Picture

```
POST {{base_url}}/api/users/3/profile-picture
Authorization: Bearer {{admin_token}}
Content-Type: multipart/form-data
```

**Form Data:**

| Key | Value |
|-----|-------|
| `file` | *(select image file)* |

**Response — `200 OK`:** User object with `profilePicture` field updated.

---

## Epic 3 — Staff Dashboard

> 🔒 **All endpoints below require:** `Authorization: Bearer {{staff_token}}` (or `{{admin_token}}`)

### 3.1 Staff Dashboard Stats

#### ✅ 3.1.1 Get Staff Dashboard Stats

```
GET {{base_url}}/api/staff/dashboard/stats
Authorization: Bearer {{staff_token}}
```

**Response — `200 OK`:**
```json
{
  "totalBooks": 45,
  "booksBorrowed": 12,
  "activeReservations": 5,
  "totalFines": 250.0,
  "categoryCounts": [
    { "category": "Computer Science", "count": 15, "code": "000" },
    { "category": "Literature", "count": 10, "code": "800" }
  ],
  "top5Books": [
    { "bookId": 1, "title": "Clean Code", "borrowCount": 25 },
    { "bookId": 3, "title": "Design Patterns", "borrowCount": 18 }
  ]
}
```

---

#### ✅ 3.1.2 Get Transaction Counters

```
GET {{base_url}}/api/staff/transactions/counters
Authorization: Bearer {{staff_token}}
```

**Response — `200 OK`:**
```json
{
  "totalBorrows": 12,
  "totalOverdue": 3,
  "totalReturned": 45
}
```

---

### 3.2 Manage Members (View-Only + Borrow History)

#### ✅ 3.2.1 View All Members (Staff Read-Only)

```
GET {{base_url}}/api/staff/members
Authorization: Bearer {{staff_token}}
```

**Response — `200 OK`:** Array of `MemberDTO` (same structure as admin).

---

#### ✅ 3.2.2 Search Members (Staff)

```
GET {{base_url}}/api/staff/members/search?query=Amara
Authorization: Bearer {{staff_token}}
```

**Response — `200 OK`:**
```json
[
  {
    "id": 3,
    "username": "user1",
    "email": "user1@gmail.com",
    "firstName": "Amara",
    "lastName": "Perera",
    "gender": "MALE",
    "birthDate": "1995-03-15",
    "age": 31,
    "membershipDate": "2026-01-01",
    "phone": "+94771234567",
    "whatsapp": "+94771234567",
    "role": "MEMBER",
    "isActive": true,
    "profilePicture": null
  }
]
```

---

#### ✅ 3.2.3 View Member Borrow History

```
GET {{base_url}}/api/staff/members/3/borrow-history
Authorization: Bearer {{staff_token}}
```

**Response — `200 OK`:**
```json
[
  {
    "id": 1,
    "user": { "id": 3, "username": "user1" },
    "book": { "id": 1, "title": "Clean Code" },
    "issueDate": "2026-06-01",
    "dueDate": "2026-06-15",
    "returnDate": null,
    "status": "ISSUED",
    "fineAmount": 0.0,
    "bookCondition": null
  }
]
```

---

#### ❌ 3.2.4 Access Staff Members with MEMBER Token (Forbidden)

```
GET {{base_url}}/api/staff/members
Authorization: Bearer {{member_token}}
```

**Response — `403 Forbidden`**

---

### 3.3 Book CRUD (with Cover Picture)

#### ✅ 3.3.1 Get All Books

```
GET {{base_url}}/api/staff/books
Authorization: Bearer {{staff_token}}
```

**Response — `200 OK`:**
```json
[
  {
    "id": 1,
    "title": "Clean Code",
    "isbn": "978-0132350884",
    "category": "000 – Generalities",
    "description": "A handbook of agile software craftsmanship.",
    "publisher": "Prentice Hall",
    "dateReceived": "2026-01-15",
    "totalCopies": 5,
    "availableCopies": 3,
    "coverImage": "/uploads/books/abc123_cover.jpg",
    "author": {
      "id": 1,
      "name": "Robert C. Martin",
      "bio": "Software engineer and author."
    }
  }
]
```

---

#### ✅ 3.3.2 Search Books

```
GET {{base_url}}/api/staff/books?search=Clean
Authorization: Bearer {{staff_token}}
```

---

#### ✅ 3.3.3 Filter Books by Category

```
GET {{base_url}}/api/staff/books?category=800 – Literature
Authorization: Bearer {{staff_token}}
```

---

#### ✅ 3.3.4 Get Book by ID

```
GET {{base_url}}/api/staff/books/1
Authorization: Bearer {{staff_token}}
```

**Response — `200 OK`:** Single `BookDTO`.

---

#### ❌ 3.3.5 Get Non-Existent Book

```
GET {{base_url}}/api/staff/books/9999
Authorization: Bearer {{staff_token}}
```

**Response — `404 Not Found`**

---

#### ✅ 3.3.6 Create Book (multipart/form-data)

```
POST {{base_url}}/api/staff/books
Authorization: Bearer {{staff_token}}
Content-Type: multipart/form-data
```

**Form Data (not JSON):**

| Key | Value |
|-----|-------|
| `title` | The Pragmatic Programmer |
| `authorId` | 1 |
| `isbn` | 978-0135957059 |
| `category` | 000 – Generalities |
| `totalCopies` | 4 |
| `availableCopies` | 4 |
| `publisher` | Addison-Wesley |
| `dateReceived` | 2026-06-01 |
| `description` | Your journey to mastery. |
| `file` | *(select cover image file)* |

**Response — `200 OK`:**
```json
{
  "id": 46,
  "title": "The Pragmatic Programmer",
  "isbn": "978-0135957059",
  "category": "000 – Generalities",
  "totalCopies": 4,
  "availableCopies": 4,
  "coverImage": "/uploads/books/uuid_cover.jpg",
  "author": {
    "id": 1,
    "name": "Robert C. Martin",
    "bio": "..."
  }
}
```

---

#### ✅ 3.3.7 Update Book (multipart/form-data)

```
PUT {{base_url}}/api/staff/books/1
Authorization: Bearer {{staff_token}}
Content-Type: multipart/form-data
```

**Form Data:** Same keys as create, with updated values.

---

#### ✅ 3.3.8 Upload Cover Image Only

```
POST {{base_url}}/api/staff/books/1/cover
Authorization: Bearer {{staff_token}}
Content-Type: multipart/form-data
```

**Form Data:**

| Key | Value |
|-----|-------|
| `file` | *(select image file)* |

---

#### ✅ 3.3.9 Delete Book

```
DELETE {{base_url}}/api/staff/books/46
Authorization: Bearer {{staff_token}}
```

**Response — `200 OK`**

---

#### ❌ 3.3.10 Create Book with MEMBER Token (Forbidden)

```
POST {{base_url}}/api/staff/books
Authorization: Bearer {{member_token}}
Content-Type: multipart/form-data
```

**Response — `403 Forbidden`**

---

#### ❌ 3.3.11 Create Book — Non-Existent Author

```
POST {{base_url}}/api/staff/books
Authorization: Bearer {{staff_token}}
Content-Type: multipart/form-data
```

**Form Data:**

| Key | Value |
|-----|-------|
| `title` | Unknown Author Book |
| `authorId` | 9999 |
| `isbn` | 978-0000000000 |
| `category` | 000 – Generalities |
| `totalCopies` | 1 |

**Response — `500 Internal Server Error`:**
```json
{
  "message": "Author not found with ID: 9999"
}
```

---

#### ❌ 3.3.12 Delete Non-Existent Book

```
DELETE {{base_url}}/api/staff/books/9999
Authorization: Bearer {{staff_token}}
```

**Response — `404 Not Found`**

---

### 3.4 Author CRUD

#### ✅ 3.4.1 Get All Authors

```
GET {{base_url}}/api/staff/authors
Authorization: Bearer {{staff_token}}
```

**Response — `200 OK`:**
```json
[
  {
    "id": 1,
    "name": "Robert C. Martin",
    "bio": "Software engineer, author of Clean Code.",
    "books": [
      { "id": 1, "title": "Clean Code" }
    ]
  }
]
```

---

#### ✅ 3.4.2 Search Authors

```
GET {{base_url}}/api/staff/authors?search=Martin
Authorization: Bearer {{staff_token}}
```

---

#### ✅ 3.4.3 Get Author by ID

```
GET {{base_url}}/api/staff/authors/1
Authorization: Bearer {{staff_token}}
```

---

#### ✅ 3.4.4 Create Author

```
POST {{base_url}}/api/staff/authors
Authorization: Bearer {{staff_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "name": "Erich Gamma",
  "bio": "Co-author of Design Patterns, Swiss computer scientist."
}
```

**Response — `201 Created`:**
```json
{
  "id": 10,
  "name": "Erich Gamma",
  "bio": "Co-author of Design Patterns, Swiss computer scientist.",
  "books": []
}
```

---

#### ❌ 3.4.5 Create Author — Empty Name

```
POST {{base_url}}/api/staff/authors
Authorization: Bearer {{staff_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "name": "",
  "bio": "No name author"
}
```

**Response — `400 Bad Request`:**
```json
{
  "message": "Author name is required"
}
```

---

#### ✅ 3.4.6 Update Author

```
PUT {{base_url}}/api/staff/authors/10
Authorization: Bearer {{staff_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "name": "Erich Gamma",
  "bio": "Updated bio — co-author of GoF Design Patterns."
}
```

**Response — `200 OK`:** Updated `AuthorDTO`.

---

#### ✅ 3.4.7 Delete Author (No Linked Books)

```
DELETE {{base_url}}/api/staff/authors/10
Authorization: Bearer {{staff_token}}
```

**Response — `200 OK`:**
```json
{
  "message": "Author deleted successfully"
}
```

---

#### ❌ 3.4.8 Delete Author with Linked Books

```
DELETE {{base_url}}/api/staff/authors/1
Authorization: Bearer {{staff_token}}
```

*(Assuming author 1 has books linked)*

**Response — `400 Bad Request`:**
```json
{
  "message": "Cannot delete author with linked books"
}
```

---

#### ❌ 3.4.9 Get Non-Existent Author

```
GET {{base_url}}/api/staff/authors/9999
Authorization: Bearer {{staff_token}}
```

**Response — `404 Not Found`:**
```json
{
  "message": "Author not found with ID: 9999"
}
```

---

#### ❌ 3.4.10 Update Non-Existent Author

```
PUT {{base_url}}/api/staff/authors/9999
Authorization: Bearer {{staff_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "name": "Ghost Author",
  "bio": "Does not exist"
}
```

**Response — `404 Not Found`:**
```json
{
  "message": "Author not found with ID: 9999"
}
```

---

#### ❌ 3.4.11 Delete Non-Existent Author

```
DELETE {{base_url}}/api/staff/authors/9999
Authorization: Bearer {{staff_token}}
```

**Response — `404 Not Found`:**
```json
{
  "message": "Author not found with ID: 9999"
}
```

---

#### ❌ 3.4.12 Access Authors with MEMBER Token (Forbidden)

```
GET {{base_url}}/api/staff/authors
Authorization: Bearer {{member_token}}
```

**Response — `403 Forbidden`**

---

### 3.5 Borrow & Return (Transactions)

#### ✅ 3.5.1 Get All Transactions

```
GET {{base_url}}/api/staff/transactions
Authorization: Bearer {{staff_token}}
```

**Response — `200 OK`:**
```json
[
  {
    "id": 1,
    "userId": 3,
    "memberName": "user1",
    "memberEmail": "user1@gmail.com",
    "bookId": 1,
    "bookTitle": "Clean Code",
    "bookIsbn": "978-0132350884",
    "issueDate": "2026-06-01",
    "dueDate": "2026-06-15",
    "returnDate": null,
    "status": "ISSUED",
    "fineAmount": 0.0,
    "bookCondition": null,
    "conditionNotes": null
  }
]
```

---

#### ✅ 3.5.2 Filter Transactions by Status

```
GET {{base_url}}/api/staff/transactions?status=Overdue
Authorization: Bearer {{staff_token}}
```

> Filter values: `All`, `Issue`, `Overdue`, `Return`

---

#### ✅ 3.5.3 Issue Book to Member

```
POST {{base_url}}/api/staff/transactions/issue?userId=3&bookId=5
Authorization: Bearer {{staff_token}}
```

**Response — `201 Created`:**
```json
{
  "id": 50,
  "userId": 3,
  "memberName": "user1",
  "bookId": 5,
  "bookTitle": "Design Patterns",
  "issueDate": "2026-06-16",
  "dueDate": "2026-06-30",
  "returnDate": null,
  "status": "ISSUED",
  "fineAmount": 0.0
}
```

> Auto-calculated: issue date = today, due date = today + 14 days.

---

#### ❌ 3.5.4 Issue Book — Member Already Has Active Borrow

```
POST {{base_url}}/api/staff/transactions/issue?userId=3&bookId=6
Authorization: Bearer {{staff_token}}
```

*(Member 3 already has ISSUED book)*

**Response — `400 Bad Request`:**
```json
{
  "message": "According to library borrowing rules, a member can borrow only one book at a time. This member currently has 'Clean Code' (Status: ISSUED). Please return the currently borrowed book before borrowing another."
}
```

---

#### ❌ 3.5.5 Issue Book — No Available Copies

```
POST {{base_url}}/api/staff/transactions/issue?userId=4&bookId=1
Authorization: Bearer {{staff_token}}
```

*(Book 1 has 0 available copies)*

**Response — `400 Bad Request`:**
```json
{
  "message": "Book is currently unavailable."
}
```

---

#### ❌ 3.5.6 Issue Book — Member Not Found

```
POST {{base_url}}/api/staff/transactions/issue?userId=9999&bookId=1
Authorization: Bearer {{staff_token}}
```

**Response — `404 Not Found`:**
```json
{
  "message": "Member not found with ID: 9999"
}
```

---

#### ✅ 3.5.7 Return Book (with Condition)

```
PUT {{base_url}}/api/staff/transactions/return/50?returnDate=2026-06-28&bookCondition=GOOD&conditionNotes=Returned in excellent condition
Authorization: Bearer {{staff_token}}
```

**Response — `200 OK`:**
```json
{
  "id": 50,
  "userId": 3,
  "bookTitle": "Design Patterns",
  "issueDate": "2026-06-16",
  "dueDate": "2026-06-30",
  "returnDate": "2026-06-28",
  "status": "RETURNED",
  "fineAmount": 0.0,
  "bookCondition": "GOOD",
  "conditionNotes": "Returned in excellent condition"
}
```

---

#### ✅ 3.5.8 Return Book Late (Auto Fine Calculated)

```
PUT {{base_url}}/api/staff/transactions/return/1?returnDate=2026-06-18&bookCondition=FAIR&conditionNotes=Minor page wear
Authorization: Bearer {{staff_token}}
```

*(Due date was June 15; returned June 18 = 3 days late × Rs. 5 = Rs. 15)*

**Response — `200 OK`:**
```json
{
  "id": 1,
  "returnDate": "2026-06-18",
  "status": "RETURNED",
  "fineAmount": 15.0,
  "bookCondition": "FAIR",
  "conditionNotes": "Minor page wear"
}
```

---

#### ❌ 3.5.9 Return Book — Missing Condition

```
PUT {{base_url}}/api/staff/transactions/return/1?returnDate=2026-06-28
Authorization: Bearer {{staff_token}}
```

**Response — `400 Bad Request`:**
```json
{
  "message": "Book Condition is required"
}
```

---

#### ❌ 3.5.10 Return Book — Missing Return Date

```
PUT {{base_url}}/api/staff/transactions/return/1?bookCondition=GOOD
Authorization: Bearer {{staff_token}}
```

**Response — `400 Bad Request`:**
```json
{
  "message": "Return Date is required"
}
```

---

#### ✅ 3.5.11 Update Transaction (Edit Status/Condition)

```
PUT {{base_url}}/api/staff/transactions/1/update?status=ISSUED&bookCondition=GOOD
Authorization: Bearer {{staff_token}}
```

**Response — `200 OK`:** Updated `TransactionDTO`.

---

#### ❌ 3.5.12 Update Non-Existent Transaction

```
PUT {{base_url}}/api/staff/transactions/9999/update?status=ISSUED
Authorization: Bearer {{staff_token}}
```

**Response — `404 Not Found`:**
```json
{
  "message": "Transaction not found with ID: 9999"
}
```

---

#### ❌ 3.5.13 Return Non-Existent Transaction

```
PUT {{base_url}}/api/staff/transactions/return/9999?returnDate=2026-06-28&bookCondition=GOOD
Authorization: Bearer {{staff_token}}
```

**Response — `404 Not Found`:**
```json
{
  "message": "Transaction not found with ID: 9999"
}
```

---

#### ❌ 3.5.14 Return Book — Invalid Condition Value

```
PUT {{base_url}}/api/staff/transactions/return/1?returnDate=2026-06-28&bookCondition=BROKEN
Authorization: Bearer {{staff_token}}
```

**Response — `400 Bad Request`:**
```json
{
  "message": "Invalid book condition: BROKEN"
}
```

---

#### ❌ 3.5.15 Issue Book with MEMBER Token (Forbidden)

```
POST {{base_url}}/api/staff/transactions/issue?userId=3&bookId=5
Authorization: Bearer {{member_token}}
```

**Response — `403 Forbidden`**

---

#### ❌ 3.5.16 Return Already Returned Book

```
PUT {{base_url}}/api/staff/transactions/return/1?returnDate=2026-06-28&bookCondition=GOOD
Authorization: Bearer {{staff_token}}
```

*(Assuming transaction 1 is already returned)*

**Response — `400 Bad Request`:**
```json
{
  "message": "This book has already been returned"
}
```

---

### 3.6 Automatic Fine Calculation

> Fines are calculated automatically during book return. See **3.5.8** above for the live example.

**Fine Rate:** Rs. 5.00 per day overdue  
**Formula:** `days_overdue × 5.0`  
**Minimum:** Rs. 0.00 (never negative)

| Due Date | Return Date | Days Overdue | Fine |
|----------|-------------|--------------|------|
| Jun 15 | Jun 14 | 0 (early) | Rs. 0.00 |
| Jun 15 | Jun 15 | 0 (same day) | Rs. 0.00 |
| Jun 15 | Jun 18 | 3 days | Rs. 15.00 |
| Jun 15 | Jun 25 | 10 days | Rs. 50.00 |

---

### 3.7 Fines CRUD

#### ✅ 3.7.1 Get All Fines

```
GET {{base_url}}/api/staff/fines
Authorization: Bearer {{staff_token}}
```

**Response — `200 OK`:**
```json
[
  {
    "id": 1,
    "transactionId": 1,
    "memberName": "user1",
    "bookTitle": "Clean Code",
    "amount": 15.0,
    "returnDate": "2026-06-18",
    "paidDate": null,
    "status": "UNPAID"
  }
]
```

---

#### ✅ 3.7.2 Filter Fines by Status

```
GET {{base_url}}/api/staff/fines?status=UNPAID
Authorization: Bearer {{staff_token}}
```

> Filter values: `ALL`, `UNPAID`, `PAID`, `NONE`

---

#### ✅ 3.7.3 Get Fine Statistics

```
GET {{base_url}}/api/staff/fines/stats
Authorization: Bearer {{staff_token}}
```

**Response — `200 OK`:**
```json
{
  "totalFines": 5,
  "unpaidCount": 3,
  "paidCount": 2,
  "totalCollected": 45.0
}
```

---

#### ✅ 3.7.4 Get Fine by ID

```
GET {{base_url}}/api/staff/fines/1
Authorization: Bearer {{staff_token}}
```

---

#### ✅ 3.7.5 Mark Fine as Paid

```
PUT {{base_url}}/api/staff/fines/1/pay
Authorization: Bearer {{staff_token}}
```

**Response — `200 OK`:**
```json
{
  "id": 1,
  "status": "PAID",
  "paidDate": "2026-06-16"
}
```

---

#### ✅ 3.7.6 Mark Fine as Paid (Custom Payment Date)

```
PUT {{base_url}}/api/staff/fines/1/pay?paymentDate=2026-06-15
Authorization: Bearer {{staff_token}}
```

---

#### ✅ 3.7.7 Update Fine

```
PUT {{base_url}}/api/staff/fines/1?status=PAID&paymentDate=2026-06-16
Authorization: Bearer {{staff_token}}
```

---

#### ✅ 3.7.8 Delete Fine

```
DELETE {{base_url}}/api/staff/fines/1
Authorization: Bearer {{staff_token}}
```

**Response — `200 OK`:**
```json
{
  "message": "Fine deleted successfully"
}
```

---

#### ❌ 3.7.9 Get Non-Existent Fine

```
GET {{base_url}}/api/staff/fines/9999
Authorization: Bearer {{staff_token}}
```

**Response — `404 Not Found`**

---

#### ❌ 3.7.10 Mark Non-Existent Fine as Paid

```
PUT {{base_url}}/api/staff/fines/9999/pay
Authorization: Bearer {{staff_token}}
```

**Response — `404 Not Found`:**
```json
{
  "message": "Fine not found with ID: 9999"
}
```

---

#### ❌ 3.7.11 Update Fine — Invalid Status

```
PUT {{base_url}}/api/staff/fines/1?status=INVALID_STATUS
Authorization: Bearer {{staff_token}}
```

**Response — `400 Bad Request`:**
```json
{
  "message": "No enum constant com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.entity.FineStatus.INVALID_STATUS"
}
```

---

#### ❌ 3.7.12 Delete Non-Existent Fine

```
DELETE {{base_url}}/api/staff/fines/9999
Authorization: Bearer {{staff_token}}
```

**Response — `404 Not Found`:**
```json
{
  "message": "Fine not found with ID: 9999"
}
```

---

#### ❌ 3.7.13 Access Fines with MEMBER Token (Forbidden)

```
GET {{base_url}}/api/staff/fines
Authorization: Bearer {{member_token}}
```

**Response — `403 Forbidden`**

---

### 3.8 Return with Book Condition

> Book condition is **mandatory** on return. See **3.5.7** and **3.5.9** above.

**Valid Conditions:** `GOOD`, `FAIR`, `POOR`, `DAMAGED`

| Condition | Description |
|-----------|-------------|
| `GOOD` | No visible wear or damage |
| `FAIR` | Minor wear (bent corners, light marks) |
| `POOR` | Noticeable damage (torn pages, loose spine) |
| `DAMAGED` | Significant damage (water damage, missing pages) |

---

### 3.9 Reservations (View + Status Management for Staff)

#### ✅ 3.9.1 Get All Reservations

```
GET {{base_url}}/api/staff/reservations
Authorization: Bearer {{staff_token}}
```

**Response — `200 OK`:**
```json
[
  {
    "id": 1,
    "userId": 3,
    "memberName": "user1",
    "bookId": 5,
    "bookTitle": "Design Patterns",
    "reservationDate": "2026-06-14",
    "status": "PENDING"
  }
]
```

---

#### ✅ 3.9.2 Get Reservation by ID

```
GET {{base_url}}/api/staff/reservations/1
Authorization: Bearer {{staff_token}}
```

---

#### ✅ 3.9.3 Approve Reservation (Auto-Issues Book)

```
PATCH {{base_url}}/api/staff/reservations/1/status
Authorization: Bearer {{staff_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "status": "APPROVED"
}
```

**Response — `200 OK`:**
```json
{
  "id": 1,
  "status": "APPROVED",
  "message": "Book auto-issued to member"
}
```

---

#### ✅ 3.9.4 Reject Reservation

```
PATCH {{base_url}}/api/staff/reservations/1/status
Authorization: Bearer {{staff_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "status": "REJECTED"
}
```

**Response — `200 OK`**

---

#### ✅ 3.9.5 Acknowledge Reservation

```
PATCH {{base_url}}/api/staff/reservations/1/acknowledge
Authorization: Bearer {{staff_token}}
```

---

#### ❌ 3.9.6 Approve Reservation — Member Already Has Active Borrow

```
PATCH {{base_url}}/api/staff/reservations/2/status
Authorization: Bearer {{staff_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "status": "APPROVED"
}
```

**Response — `400 Bad Request`:**
```json
{
  "message": "Cannot issue book. Member already has an active borrow."
}
```

---

#### ❌ 3.9.7 Modify COMPLETED Reservation

```
PATCH {{base_url}}/api/staff/reservations/5/status
Authorization: Bearer {{staff_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "status": "REJECTED"
}
```

**Response — `400 Bad Request`:** Cannot modify completed/cancelled.

---

#### ❌ 3.9.8 Update Reservation — Invalid Status

```
PATCH {{base_url}}/api/staff/reservations/1/status
Authorization: Bearer {{staff_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "status": "INVALID_STATUS"
}
```

**Response — `400 Bad Request`:**
```json
{
  "message": "Invalid status: No enum constant ...INVALID_STATUS"
}
```

---

#### ❌ 3.9.9 Update Reservation — Missing Status Field

```
PATCH {{base_url}}/api/staff/reservations/1/status
Authorization: Bearer {{staff_token}}
Content-Type: application/json
```

**Request Body:**
```json
{}
```

**Response — `400 Bad Request`:**
```json
{
  "message": "Status field is required"
}
```

---

#### ❌ 3.9.10 Get Non-Existent Reservation

```
GET {{base_url}}/api/staff/reservations/9999
Authorization: Bearer {{staff_token}}
```

**Response — `404 Not Found`:**
```json
{
  "message": "Reservation not found with ID: 9999"
}
```

---

#### ❌ 3.9.11 Acknowledge Already Processed Reservation

```
PATCH {{base_url}}/api/staff/reservations/1/acknowledge
Authorization: Bearer {{staff_token}}
```

*(Assuming reservation 1 is already acknowledged/processed)*

**Response — `400 Bad Request`:**
```json
{
  "message": "Reservation has already been processed"
}
```

---

### 3.10 Notifications CRUD

#### ✅ 3.10.1 Get All Notifications (Staff/Admin sees all)

```
GET {{base_url}}/api/notifications
Authorization: Bearer {{staff_token}}
```

**Response — `200 OK`:**
```json
[
  {
    "id": 1,
    "title": "Holiday Closure",
    "message": "Library will be closed on June 20 for Poya day.",
    "type": "GENERAL",
    "createdAt": "2026-06-15T09:00:00",
    "isRead": false,
    "userId": null
  }
]
```

---

#### ✅ 3.10.2 Search Notifications

```
GET {{base_url}}/api/notifications?search=Holiday
Authorization: Bearer {{staff_token}}
```

---

#### ✅ 3.10.3 Get Notification by ID

```
GET {{base_url}}/api/notifications/1
Authorization: Bearer {{staff_token}}
```

---

#### ✅ 3.10.4 Create Notification (Broadcast)

```
POST {{base_url}}/api/notifications
Authorization: Bearer {{staff_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "title": "New Books Arrived!",
  "message": "50 new books added to the Technology section. Visit us this week!",
  "type": "GENERAL"
}
```

**Response — `201 Created`:**
```json
{
  "id": 5,
  "title": "New Books Arrived!",
  "message": "50 new books added to the Technology section...",
  "type": "GENERAL",
  "createdAt": "2026-06-16T23:00:00",
  "isRead": false
}
```

---

#### ✅ 3.10.5 Update Notification

```
PUT {{base_url}}/api/notifications/5
Authorization: Bearer {{staff_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "title": "New Books Arrived! (Updated)",
  "message": "100 new books added across all sections.",
  "type": "GENERAL"
}
```

**Response — `200 OK`**

---

#### ✅ 3.10.6 Delete Notification

```
DELETE {{base_url}}/api/notifications/5
Authorization: Bearer {{staff_token}}
```

**Response — `200 OK`:**
```json
{
  "message": "Notification deleted successfully"
}
```

---

#### ✅ 3.10.7 Mark Notification as Read (Member)

```
PUT {{base_url}}/api/notifications/1/read
Authorization: Bearer {{member_token}}
```

**Response — `200 OK`:**
```json
{
  "message": "Notification marked as read"
}
```

---

#### ✅ 3.10.8 Mark All Notifications as Read

```
PUT {{base_url}}/api/notifications/read-all
Authorization: Bearer {{member_token}}
```

**Response — `200 OK`:**
```json
{
  "message": "All notifications marked as read"
}
```

---

#### ❌ 3.10.9 Create Notification with MEMBER Token (Forbidden)

```
POST {{base_url}}/api/notifications
Authorization: Bearer {{member_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "title": "Spam",
  "message": "Members should not create notifications",
  "type": "GENERAL"
}
```

**Response — `403 Forbidden`**

---

#### ❌ 3.10.10 Delete Non-Existent Notification

```
DELETE {{base_url}}/api/notifications/9999
Authorization: Bearer {{staff_token}}
```

**Response — `404 Not Found`:**
```json
{
  "message": "Notification not found with ID: 9999"
}
```

---

#### ❌ 3.10.11 Get Non-Existent Notification

```
GET {{base_url}}/api/notifications/9999
Authorization: Bearer {{staff_token}}
```

**Response — `404 Not Found`:**
```json
{
  "message": "Notification not found with ID: 9999"
}
```

---

#### ❌ 3.10.12 Member Access Another User's Notification

```
GET {{base_url}}/api/notifications/1
Authorization: Bearer {{member_token}}
```

*(Assuming notification 1 is targeted to a different user)*

**Response — `403 Forbidden`:**
```json
{
  "message": "Access denied: This notification does not belong to you"
}
```

---

#### ✅ 3.10.13 Member Views Own Notifications

```
GET {{base_url}}/api/notifications
Authorization: Bearer {{member_token}}
```

**Response — `200 OK`:** Only broadcast + member-targeted notifications.

---

## Bonus: Member Endpoints

> 🔒 These require `Authorization: Bearer {{member_token}}`

### Feedback

#### ✅ Create Feedback

```
POST {{base_url}}/api/feedback
Authorization: Bearer {{member_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "message": "Excellent service! The new books are wonderful."
}
```

**Response — `201 Created`:**
```json
{
  "id": 5,
  "message": "Excellent service! The new books are wonderful.",
  "user": { "id": 3, "username": "user1" },
  "createdAt": "2026-06-16T23:00:00"
}
```

---

#### ✅ Update Feedback (Own Only)

```
PUT {{base_url}}/api/feedback/5
Authorization: Bearer {{member_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "message": "Updated: Even better than I thought!"
}
```

---

#### ❌ Update Another Member's Feedback

```
PUT {{base_url}}/api/feedback/1
Authorization: Bearer {{member_token}}
Content-Type: application/json
```

**Response — `403 Forbidden`:**
```json
{
  "message": "You are not authorized to update this feedback"
}
```

---

#### ✅ Delete Feedback (Own Only)

```
DELETE {{base_url}}/api/feedback/5
Authorization: Bearer {{member_token}}
```

**Response — `200 OK`:**
```json
{
  "message": "Feedback deleted successfully"
}
```

---

#### ❌ Delete Another Member's Feedback

```
DELETE {{base_url}}/api/feedback/1
Authorization: Bearer {{member_token}}
```

**Response — `403 Forbidden`:**
```json
{
  "message": "You are not authorized to delete this feedback"
}
```

---

#### ❌ Create Feedback with STAFF Token (Forbidden)

```
POST {{base_url}}/api/feedback
Authorization: Bearer {{staff_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "message": "Staff trying to create feedback"
}
```

**Response — `403 Forbidden`**

---

#### ❌ Update Non-Existent Feedback

```
PUT {{base_url}}/api/feedback/9999
Authorization: Bearer {{member_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "message": "Updating feedback that doesn't exist"
}
```

**Response — `404 Not Found`:**
```json
{
  "message": "Feedback not found"
}
```

---

#### ❌ Delete Non-Existent Feedback

```
DELETE {{base_url}}/api/feedback/9999
Authorization: Bearer {{member_token}}
```

**Response — `404 Not Found`:**
```json
{
  "message": "Feedback not found"
}
```

---

#### ✅ Get All Feedback (Staff/Admin sees all)

```
GET {{base_url}}/api/feedback
Authorization: Bearer {{staff_token}}
```

**Response — `200 OK`:** All feedback entries from all members.

---

#### ✅ Get My Feedback (Member sees own only)

```
GET {{base_url}}/api/feedback
Authorization: Bearer {{member_token}}
```

**Response — `200 OK`:** Only feedback entries belonging to the logged-in member.

---

### Member Reservations

#### ✅ Create Reservation

```
POST {{base_url}}/api/reservations
Authorization: Bearer {{member_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "userId": 3,
  "bookId": 5
}
```

**Response — `201 Created`:**
```json
{
  "id": 10,
  "userId": 3,
  "memberName": "user1",
  "bookId": 5,
  "bookTitle": "Design Patterns",
  "reservationDate": "2026-06-16",
  "status": "PENDING"
}
```

---

#### ❌ Create Reservation — Non-Existent Book

```
POST {{base_url}}/api/reservations
Authorization: Bearer {{member_token}}
Content-Type: application/json
```

**Request Body:**
```json
{
  "userId": 3,
  "bookId": 9999
}
```

**Response — `404 Not Found`:**
```json
{
  "message": "Book not found with ID: 9999"
}
```

---

#### ✅ View My Reservations

```
GET {{base_url}}/api/reservations/user/3
Authorization: Bearer {{member_token}}
```

**Response — `200 OK`:**
```json
[
  {
    "id": 10,
    "userId": 3,
    "memberName": "user1",
    "bookId": 5,
    "bookTitle": "Design Patterns",
    "reservationDate": "2026-06-16",
    "status": "PENDING"
  }
]
```

---

### Member Borrowing History

#### ✅ View My Borrowing History

```
GET {{base_url}}/api/transactions/user/3
Authorization: Bearer {{member_token}}
```

**Response — `200 OK`:** Array of `TransactionDTO` for this member only.

---

## Common Issues & Solutions

| Issue | Cause | Solution |
|-------|-------|----------|
| `401 Unauthorized` | Missing or expired JWT | Login again to get a fresh token |
| `403 Forbidden` | Token role doesn't match endpoint | Use correct role token (admin/staff/member) |
| `500 Internal Server Error` | JWT secret too short | Already fixed — secret is now 344 bits |
| CORS Error | Browser blocks cross-origin | Postman ignores CORS; only affects browser |
| `404 Not Found` | Wrong ID or endpoint | Verify the resource ID exists in DB |
| Stale tokens after restart | Server secret changed | All old tokens invalid — login again |
| `multipart/form-data` error | Using `application/json` for books | Switch to `form-data` in Postman Body tab |
| IDOR `403 Forbidden` | Member accessing another user's resource | Use correct user ID matching the logged-in member |
| `Book already returned` | Double-returning a transaction | Check transaction status before returning |
| `Member already has active borrow` | One-book policy enforced | Return the current book before issuing another |

---

## Testing Checklist

### Epic 1 — Authentication
- [ ] Login as Admin / Staff / Member
- [ ] Login with wrong password (401)
- [ ] Login with non-existent user (401)
- [ ] Request OTP (valid + invalid email)
- [ ] Verify OTP (correct + wrong code)
- [ ] Register new member
- [ ] Register duplicate username/email (400)
- [ ] Role-based access denial (403)
- [ ] IDOR: member own history vs other's history

### Epic 2 — Admin Dashboard
- [ ] Get dashboard stats
- [ ] CRUD members (list, create, update, delete, toggle status)
- [ ] Search members (query param + dedicated search endpoint)
- [ ] Duplicate username/email on create/update (400)
- [ ] Toggle status for non-member user (400)
- [ ] Update/delete non-existent member (404)
- [ ] Access members with wrong role token (403)
- [ ] CRUD registrations (list, create STAFF/ADMIN, update, delete)
- [ ] Reject MEMBER role registration (400)
- [ ] Short password validation (400)
- [ ] CRUD about statements (list, create, update, delete)
- [ ] Update non-existent about statement (500)
- [ ] Create about with STAFF token (403)
- [ ] User Management: CRUD users (list, create, update, delete)
- [ ] User Management: duplicate username/email (400)
- [ ] User Management: upload profile picture
- [ ] User Management: access with wrong role (403)

### Epic 3 — Staff Dashboard
- [ ] Get staff stats + transaction counters
- [ ] View members + search + borrow history
- [ ] Access staff members with MEMBER token (403)
- [ ] CRUD books (list, create multipart, update, delete, cover upload)
- [ ] Create book with MEMBER token (403)
- [ ] Create book with non-existent author (500)
- [ ] Delete non-existent book (404)
- [ ] CRUD authors (list, create, update, delete with/without books)
- [ ] Create author with empty name (400)
- [ ] Non-existent author operations (404)
- [ ] Access authors with MEMBER token (403)
- [ ] Issue book (success + one-book rule + no copies + not found)
- [ ] Return book (on time + late with fine + missing condition + missing date)
- [ ] Return book — invalid condition value (400)
- [ ] Return already returned book (400)
- [ ] Return/update non-existent transaction (404)
- [ ] Issue book with MEMBER token (403)
- [ ] Update transaction status/condition
- [ ] Access transactions with MEMBER token (403)
- [ ] Fines: list, filter, stats, get by ID, mark paid, update, delete
- [ ] Fines: non-existent fine operations (404)
- [ ] Fines: invalid status update (400)
- [ ] Fines: access with MEMBER token (403)
- [ ] Reservations: list, get by ID, approve (auto-issue), reject, acknowledge
- [ ] Reservations: invalid status string (400)
- [ ] Reservations: missing status field (400)
- [ ] Reservations: modify completed reservation (400)
- [ ] Reservations: non-existent reservation (404)
- [ ] Notifications: CRUD, search, mark read, mark all read
- [ ] Notifications: create with MEMBER token (403)
- [ ] Notifications: member sees only own notifications
- [ ] Feedback: create, update own, delete own
- [ ] Feedback: blocked on other's feedback (403)
- [ ] Feedback: create with STAFF token (403)
- [ ] Feedback: update/delete non-existent (404)
- [ ] Feedback: get all (staff sees all, member sees own)
- [ ] Member reservations: create, view own
- [ ] Member borrowing history: view own

---

**Happy Testing!** 🚀

---

## Summary

| Epic | Endpoints | ✅ Positive | ❌ Negative | Total Scenarios |
|------|-----------|-------------|-------------|----------------|
| Epic 1: Authentication | 5 | 8 | 8 | 16 |
| Epic 2: Admin Dashboard | 21 | 22 | 18 | 40 |
| Epic 3: Staff Dashboard | 37 | 35 | 30 | 65 |
| Member Endpoints | 7 | 10 | 8 | 18 |
| **Total** | **70** | **75** | **64** | **139** |

> **Last Updated:** Version 3.0 — All epics fully covered with positive and negative path test scenarios.
