# Thalahena Public Library - Postman Testing Guide

## 📋 Table of Contents
1. [Backend Setup](#backend-setup)
2. [Authentication Endpoints](#authentication-endpoints)
3. [Admin Endpoints](#admin-endpoints)
4. [Staff Endpoints](#staff-endpoints)
5. [Common Issues & Solutions](#common-issues--solutions)

---

## 🔧 Backend Setup

### 1. Start the Backend Server
```bash
cd ThalahenaPublicLibrarydemo
mvn spring-boot:run
```

The server will start on **http://localhost:8081** (check `application.properties` for the exact port)

### 2. Database Initialization
On first run, the `DataInitializer` automatically creates:
- **Admin**: username=`admin1`, password=`admin123`
- **Staff**: username=`staff1`, password=`staff123`
- **Member**: username=`user1`, password=`user123`

---

## 🔐 Authentication Endpoints

### 1. Register New Member (Public)
**Endpoint:** `POST http://localhost:8081/api/auth/register`

**Request Body:**
```json
{
    "username": "newmember",
    "email": "newmember@gmail.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe"
}
```

**Expected Response (200 OK):**
```json
{
    "message": "User registered successfully!"
}
```

---

### 2. Login (Public)
**Endpoint:** `POST http://localhost:8081/api/auth/login`

**Request Body:**
```json
{
    "username": "user1",
    "password": "user123"
}
```

**Expected Response (200 OK):**
```json
{
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "id": 3,
    "username": "user1",
    "email": "user1@gmail.com",
    "role": "ROLE_MEMBER"
}
```

**⚠️ Important:** Copy the `token` value - you'll need it for authenticated requests!

---

### 3. Login as Admin
**Request Body:**
```json
{
    "username": "admin1",
    "password": "admin123"
}
```

**Expected Role:** `ROLE_ADMIN`

---

### 4. Login as Staff
**Request Body:**
```json
{
    "username": "staff1",
    "password": "staff123"
}
```

**Expected Role:** `ROLE_STAFF`

---

### 5. Request OTP (Passwordless Login)
**Endpoint:** `POST http://localhost:8081/api/auth/request-otp`

**Request Body:**
```json
{
    "email": "user1@gmail.com"
}
```

**Expected Response (200 OK):**
```json
{
    "message": "OTP sent to your email."
}
```

---

### 6. Verify OTP
**Endpoint:** `POST http://localhost:8081/api/auth/verify-otp`

**Request Body:**
```json
{
    "email": "user1@gmail.com",
    "otp": "123456"
}
```

**Expected Response (200 OK):** Returns JWT token

---

## 👨‍💼 Admin Endpoints

**⚠️ Required:** Add header to all requests:
```
Authorization: Bearer <YOUR_ADMIN_TOKEN>
Content-Type: application/json
```

### 1. Get Admin Dashboard Stats
**Endpoint:** `GET http://localhost:8081/api/admin/stats`

**Expected Response (200 OK):**
```json
{
    "totalUsers": 150,
    "staffCount": 5,
    "adminCount": 1,
    "activeMembers": 140,
    "inactiveMembers": 10,
    "genderDistribution": {
        "MALE": 80,
        "FEMALE": 70
    },
    "ageDistribution": {
        "≤18": 45,
        ">18": 105
    },
    "recentFeedback": [...]
}
```

---

### 2. Get All Members
**Endpoint:** `GET http://localhost:8081/api/admin/members`

**Expected Response:** Array of member objects

---

### 3. Search Members
**Endpoint:** `GET http://localhost:8081/api/admin/members/search?query=user1`

---

### 4. Create Member
**Endpoint:** `POST http://localhost:8081/api/admin/members`

**Request Body:**
```json
{
    "firstName": "Jane",
    "lastName": "Smith",
    "username": "janesmith",
    "email": "jane@gmail.com",
    "password": "password123",
    "birthDate": "1990-05-15",
    "gender": "FEMALE",
    "phone": "+94771234567",
    "whatsapp": "+94771234567"
}
```

---

### 5. Update Member
**Endpoint:** `PUT http://localhost:8081/api/admin/members/{id}`

**Example:** `PUT http://localhost:8081/api/admin/members/3`

**Request Body:** (same as create, with updated fields)

---

### 6. Delete Member
**Endpoint:** `DELETE http://localhost:8081/api/admin/members/{id}`

**Example:** `DELETE http://localhost:8081/api/admin/members/3`

---

### 7. Create Staff/Admin User
**Endpoint:** `POST http://localhost:8081/api/admin/registrations`

**Request Body:**
```json
{
    "firstName": "Staff",
    "lastName": "User",
    "username": "newstaff",
    "email": "newstaff@gmail.com",
    "password": "staffpass123",
    "role": "STAFF"
}
```

**Role Options:** `STAFF` or `ADMIN`

---

### 8. Get All Staff/Admin Users
**Endpoint:** `GET http://localhost:8081/api/admin/registrations`

---

### 9. Manage About Statements

**Get All:** `GET http://localhost:8081/api/admin/about`

**Create:** `POST http://localhost:8081/api/admin/about`
```json
{
    "content": "Library hours: 9 AM - 5 PM, Monday to Saturday"
}
```

**Update:** `PUT http://localhost:8081/api/admin/about/{id}`

**Delete:** `DELETE http://localhost:8081/api/admin/about/{id}`

---

## 👨‍💻 Staff Endpoints

**⚠️ Required:** Add header to all requests:
```
Authorization: Bearer <YOUR_STAFF_TOKEN>
Content-Type: application/json
```

### 1. Get Staff Dashboard Stats
**Endpoint:** `GET http://localhost:8081/api/staff/dashboard/stats`

**Expected Response:**
```json
{
    "totalBooks": 500,
    "borrowedBooks": 45,
    "activeReservations": 12,
    "totalFines": 150.00,
    "categoryDistribution": {
        "600 - Technology": 150,
        "800 - Literature": 120
    },
    "topBooks": [
        {
            "bookId": 1,
            "title": "Book Title",
            "borrowCount": 25
        }
    ]
}
```

---

### 2. View Members (Read-Only)
**Endpoint:** `GET http://localhost:8081/api/staff/members`

---

### 3. Get Member Borrow History
**Endpoint:** `GET http://localhost:8081/api/staff/members/{id}/borrow-history`

**Example:** `GET http://localhost:8081/api/staff/members/3/borrow-history`

---

### 4. Manage Books

**Get All:** `GET http://localhost:8081/api/books`

**Get by ID:** `GET http://localhost:8081/api/books/{id}`

**Create:** `POST http://localhost:8081/api/books`
```json
{
    "title": "The Great Gatsby",
    "author": "F. Scott Fitzgerald",
    "isbn": "978-0743273565",
    "category": "800 - Literature",
    "totalCopies": 5,
    "availableCopies": 5
}
```

**Update:** `PUT http://localhost:8081/api/books/{id}`

**Delete:** `DELETE http://localhost:8081/api/books/{id}`

---

### 5. Issue Book to Member
**Endpoint:** `POST http://localhost:8081/api/transactions/issue`

**Request Body:**
```json
{
    "userId": 3,
    "bookId": 1
}
```

**Auto-calculated:**
- Issue Date: Today
- Due Date: Today + 14 days
- Status: ISSUED

---

### 6. Return Book
**Endpoint:** `POST http://localhost:8081/api/transactions/return/{id}`

**Example:** `POST http://localhost:8081/api/transactions/return/1`

**Optional Request Body:**
```json
{
    "bookCondition": "GOOD",
    "conditionNotes": "Book in excellent condition"
}
```

**Book Condition Options:** `GOOD`, `DAMAGED`, `LOST`

**Auto-calculated:**
- Return Date: Today
- Fine Amount: Rs. 5 per day if overdue
- Status: RETURNED

---

### 7. Get All Transactions
**Endpoint:** `GET http://localhost:8081/api/transactions`

**Filter Options:**
- `?userId=3`
- `?bookId=1`
- `?status=ISSUED`
- `?dateFrom=2024-01-01&dateTo=2024-12-31`

---

### 8. Manage Fines

**Get All Fines:** `GET http://localhost:8081/api/fines`

**Filter by Status:** `GET http://localhost:8081/api/fines?status=UNPAID`

**Get Fine Stats:** `GET http://localhost:8081/api/fines/stats`

**Mark as Paid:** `PUT http://localhost:8081/api/fines/{id}/pay`

**Example:** `PUT http://localhost:8081/api/fines/1/pay`

---

### 9. Manage Reservations

**Get All:** `GET http://localhost:8081/api/reservations`

**Filter by Status:** `GET http://localhost:8081/api/reservations?status=PENDING`

**Update Status:** `PUT http://localhost:8081/api/reservations/{id}/status?status=FULFILLED`

**Status Options:** `PENDING`, `FULFILLED`, `CANCELLED`

---

### 10. Manage Notifications

**Get All:** `GET http://localhost:8081/api/notifications`

**Get Unread Count:** `GET http://localhost:8081/api/notifications/unread`

**Mark as Read:** `PUT http://localhost:8081/api/notifications/{id}/read`

**Mark All Read:** `PUT http://localhost:8081/api/notifications/read-all`

**Send Notification:** `POST http://localhost:8081/api/notifications`
```json
{
    "userId": 3,
    "message": "Your reserved book is now available for pickup!",
    "type": "RESERVATION"
}
```

**Notification Types:** `GENERAL`, `DUE_DATE`, `OVERDUE`, `RESERVATION`

**Delete:** `DELETE http://localhost:8081/api/notifications/{id}`

---

## 🚨 Common Issues & Solutions

### Issue 1: 500 Internal Server Error on Login (JWT Secret Key Too Short)
**Problem:** Login returns 500 error with message about "key byte array is 240 bits which is not secure enough"

**Root Cause:**
- JWT HMAC-SHA256 requires a secret key of at least 256 bits (32 bytes)
- Old secret `ThalahenaLibrarySecretKey2026!` was only 30 characters = 240 bits

**Solution:**
✅ **Already Fixed!** The secret key has been updated to:
```
ThalahenaLibrarySecretKey2026SuperSecureKey123!
```
This is 43 characters = 344 bits, which exceeds the 256-bit minimum requirement.

**What to do:**
1. **Restart the backend server** (required for the new secret to take effect)
2. **All old tokens are now invalid** (signed with the old secret)
3. **Login again** to get a new token with the new secret

```bash
# Stop the running backend (Ctrl+C)
# Then restart:
cd ThalahenaPublicLibrarydemo
mvn spring-boot:run
```

---

### Issue 2: 401 Unauthorized on Login
**Problem:** `POST /api/auth/login` returns 401

**Solutions:**
1. **Check if backend is running:** Open browser and visit `http://localhost:8081/api/auth/test`
2. **Check database:** Ensure MySQL is running and database exists
3. **Check port:** Verify the port in `application.properties` (default: 8081)
4. **Check credentials:** Use the pre-seeded users:
   - admin1 / admin123
   - staff1 / staff123
   - user1 / user123

---

### Issue 3: 403 Forbidden on API Calls
**Problem:** Authenticated requests return 403

**Solutions:**
1. **Check token:** Ensure you're sending the JWT token in the header:
   ```
   Authorization: Bearer eyJhbGci...
   ```
2. **Check role:** Ensure the token's role matches the endpoint requirements
   - `/api/admin/**` requires `ROLE_ADMIN`
   - `/api/staff/**` requires `ROLE_STAFF` or `ROLE_ADMIN`

---

### Issue 4: CORS Error
**Problem:** Browser blocks requests due to CORS

**Solution:** The backend is configured to allow `http://localhost:5173` (Vite dev server). If using a different frontend URL, update `WebSecurityConfig.java`:
```java
configuration.setAllowedOrigins(List.of("http://localhost:5173", "YOUR_URL"));
```

---

### Issue 5: User Not Found
**Problem:** Login fails with "Bad credentials"

**Solutions:**
1. **Restart backend:** The `DataInitializer` runs on startup and creates test users
2. **Check database:** Verify users exist in the `users` table
3. **Register new user:** Use the `/api/auth/register` endpoint

---

### Issue 6: Password Encoding Error
**Problem:** Can't login with correct credentials

**Solution:** Passwords are BCrypt encoded. The `DataInitializer` handles this automatically. If manually creating users:
```java
user.setPassword(encoder.encode("password123"));
```

---

## 📝 Postman Collection Setup

### 1. Create Environment Variables
In Postman, create a new environment with these variables:
- `base_url`: `http://localhost:8081`
- `admin_token`: (will be set after admin login)
- `staff_token`: (will be set after staff login)
- `member_token`: (will be set after member login)

### 2. Auto-Set Token After Login
In the login request's **Tests** tab, add:
```javascript
const response = pm.response.json();
if (response.token) {
    pm.environment.set("member_token", response.token);
}
```

### 3. Use Variables in Requests
In authenticated requests, set header:
```
Authorization: Bearer {{admin_token}}
```

---

## ✅ Testing Checklist

### Authentication
- [ ] Register new member
- [ ] Login as admin
- [ ] Login as staff
- [ ] Login as member
- [ ] Request OTP
- [ ] Verify OTP

### Admin Features
- [ ] Get dashboard stats
- [ ] Create member
- [ ] Update member
- [ ] Delete member
- [ ] Search members
- [ ] Create staff user
- [ ] Manage about statements

### Staff Features
- [ ] Get dashboard stats
- [ ] View members
- [ ] View borrow history
- [ ] Create book
- [ ] Update book
- [ ] Issue book
- [ ] Return book
- [ ] View fines
- [ ] Mark fine as paid
- [ ] View reservations
- [ ] Update reservation status
- [ ] Send notification
- [ ] Mark notification as read

---

## 🎯 Quick Start Commands

### 1. Login and Get Token
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin1","password":"admin123"}'
```

### 2. Get Admin Stats
```bash
curl -X GET http://localhost:8081/api/admin/stats \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### 3. Register New Member
```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username":"newuser",
    "email":"newuser@gmail.com",
    "password":"password123",
    "firstName":"New",
    "lastName":"User"
  }'
```

---

## 📞 Support

If you encounter any issues:
1. Check backend logs for errors
2. Verify MySQL is running
3. Check `application.properties` for correct database configuration
4. Ensure all dependencies are installed: `mvn clean install`

---

**Happy Testing! 🚀**
