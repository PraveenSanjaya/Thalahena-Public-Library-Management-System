# 📊 How to Populate Database with Sample Data

## 🎯 Overview

This guide will help you populate your MySQL database with realistic test data for the Thalahena Public Library system.

---

## 📋 What's Included

The `sample_data.sql` file contains:

| Entity | Count | Description |
|--------|-------|-------------|
| **Users** | 10 | 2 Admins, 3 Staff, 5 Members |
| **Authors** | 10 | Famous authors with biographies |
| **Books** | 10 | Classic literature with ISBNs |
| **Transactions** | 10 | Book borrowings (issued & returned) |
| **Fines** | 3 | Paid and unpaid fines |
| **Reservations** | 5 | Different reservation statuses |
| **Feedback** | 5 | Member feedback messages |
| **About Statements** | 5 | Library information |
| **Notifications** | 5 | Various notification types |

---

## 🔧 Method 1: Using MySQL Command Line (Recommended)

### **Step 1: Open MySQL Command Line**
```bash
mysql -u root -p
```
Enter your password: `pass`

### **Step 2: Run the SQL Script**
```sql
USE library_db;
source C:/Users/Praveen/OneDrive/Documents/ThalahenaPublicLibrarydemo/sample_data.sql
```

### **Step 3: Verify Data**
```sql
-- Check counts
SELECT 'Users' as table_name, COUNT(*) as count FROM users
UNION ALL
SELECT 'Authors', COUNT(*) FROM authors
UNION ALL
SELECT 'Books', COUNT(*) FROM books;
```

---

## 🔧 Method 2: Using MySQL Workbench

### **Step 1: Open MySQL Workbench**
1. Launch MySQL Workbench
2. Connect to your local MySQL server (root/pass)

### **Step 2: Open the SQL File**
1. Click **File** → **Open SQL Script**
2. Navigate to: `C:\Users\Praveen\OneDrive\Documents\ThalahenaPublicLibrarydemo\sample_data.sql`
3. Click **Open**

### **Step 3: Execute the Script**
1. Make sure `library_db` is selected as the default schema
2. Click the **⚡ Execute** button (or press Ctrl+Shift+Enter)
3. Check the **Output** panel for success messages

---

## 🔧 Method 3: Using PowerShell/Command Prompt

### **Windows PowerShell:**
```powershell
mysql -u root -ppass library_db < "C:\Users\Praveen\OneDrive\Documents\ThalahenaPublicLibrarydemo\sample_data.sql"
```

### **Command Prompt:**
```cmd
mysql -u root -ppass library_db < "C:\Users\Praveen\OneDrive\Documents\ThalahenaPublicLibrarydemo\sample_data.sql"
```

---

## ✅ Verification After Import

### **Check User Data:**
```sql
SELECT id, username, email, role, first_name, last_name, is_active 
FROM users 
ORDER BY role, id;
```

**Expected Output:**
```
| id | username   | email               | role   | first_name | last_name | is_active |
|----|------------|---------------------|--------|------------|-----------|-----------|
|  1 | admin1     | admin1@gmail.com    | ADMIN  | Admin      | User      |         1 |
|  2 | admin2     | admin2@gmail.com    | ADMIN  | Sarah      | Johnson   |         1 |
|  3 | staff1     | staff1@gmail.com    | STAFF  | Staff      | User      |         1 |
|  4 | staff2     | staff2@gmail.com    | STAFF  | Emily      | Davis     |         1 |
|  5 | staff3     | staff3@gmail.com    | STAFF  | Michael    | Brown     |         1 |
|  6 | user1      | user1@gmail.com     | MEMBER | Member     | User      |         1 |
|  7 | john_doe   | john.doe@gmail.com  | MEMBER | John       | Doe       |         1 |
|  8 | jane_smith | jane.smith@gmail.com| MEMBER | Jane       | Smith     |         1 |
|  9 | alex_wilson| alex.wilson@gmail.com|MEMBER | Alex       | Wilson    |         1 |
| 10 | maria_garcia| maria.garcia@gmail.com|MEMBER| Maria      | Garcia    |         1 |
```

### **Check Books:**
```sql
SELECT id, title, author, category, total_copies, available_copies 
FROM books 
ORDER BY id;
```

### **Check Transactions:**
```sql
SELECT t.id, u.username, b.title, t.issue_date, t.due_date, t.return_date, t.status, t.fine_amount
FROM transactions t
JOIN users u ON t.user_id = u.id
JOIN books b ON t.book_id = b.id
ORDER BY t.id;
```

---

## 🔐 Sample Login Credentials

After importing the data, you can login with:

| Username | Password | Role | Full Name |
|----------|----------|------|-----------|
| admin1 | admin123 | ADMIN | Admin User |
| admin2 | admin123 | ADMIN | Sarah Johnson |
| staff1 | staff123 | STAFF | Staff User |
| staff2 | staff123 | STAFF | Emily Davis |
| staff3 | staff123 | STAFF | Michael Brown |
| user1 | user123 | MEMBER | Member User |
| john_doe | user123 | MEMBER | John Doe |
| jane_smith | user123 | MEMBER | Jane Smith |
| alex_wilson | user123 | MEMBER | Alex Wilson |
| maria_garcia | user123 | MEMBER | Maria Garcia |

---

## 📊 Sample Data Highlights

### **Books Available for Testing:**
1. The Great Gatsby (3 available)
2. To Kill a Mockingbird (2 available)
3. 1984 (4 available)
4. Harry Potter (5 available)
5. War and Peace (0 available - all borrowed!)

### **Active Transactions:**
- user1 borrowed "The Great Gatsby" (Due: April 15)
- john_doe borrowed "1984" (Due: April 19)
- jane_smith borrowed "To Kill a Mockingbird" (Due: April 22)
- alex_wilson borrowed "Harry Potter" (Due: April 24)

### **Unpaid Fines:**
- john_doe: Rs. 30.00 (6 days late)
- user1: Rs. 35.00 (7 days late)

### **Pending Reservations:**
- user1 waiting for "War and Peace"
- john_doe waiting for "The Great Gatsby"
- alex_wilson waiting for "To Kill a Mockingbird"

---

## ⚠️ Important Notes

### **Duplicate Data Warning:**
If you run this script multiple times, you'll get duplicate entries. To avoid this:

**Option 1: Clear existing data first**
```sql
-- Stop the backend server first!
DELETE FROM notifications;
DELETE FROM feedback;
DELETE FROM fines;
DELETE FROM reservations;
DELETE FROM transactions;
DELETE FROM books;
DELETE FROM authors;
DELETE FROM about;
DELETE FROM users WHERE username != 'admin1' AND username != 'staff1' AND username != 'user1';
```

**Option 2: Use INSERT IGNORE**
Change `INSERT INTO` to `INSERT IGNORE INTO` in the SQL file to skip duplicates.

### **Password Hashes:**
All passwords are BCrypt hashed:
- `admin123` → `$2a$10$zT7D85KocL1zped7B4HA2euXCbwUaIZ99v.E5fFffrsT4tmoTlNJm`
- `staff123` → `$2a$10$6aPSvN5wM9rzT9vAgz0kBeAfALNWJ5wNPT/.oHdOiQETbrmxptNRG`
- `user123` → `$2a$10$qMpWd/cnAdjMstr08NppMOMAWhOFNF.ecReOFmfDf3gr4Az.2UdAG`

---

## 🎯 Test Scenarios After Import

### **Scenario 1: Admin Dashboard**
1. Login as `admin1`
2. View dashboard stats (should show 10 users, 10 books, etc.)
3. Navigate to Members page (should see 5 members)
4. Search for "john" (should find John Doe)

### **Scenario 2: Staff Operations**
1. Login as `staff1`
2. View dashboard stats
3. Check member list
4. View John Doe's borrow history (should see 2 transactions)
5. Issue a new book to Maria Garcia

### **Scenario 3: Member Features**
1. Login as `user1`
2. View book history (should see 3 transactions)
3. Check reservations (should see 1 pending)
4. View notifications (should see 2 unread)

### **Scenario 4: Fine Management**
1. Login as `staff1`
2. Go to Fines page
3. See unpaid fines (Rs. 65 total)
4. Mark john_doe's fine as paid
5. Check updated stats

---

## 🐛 Troubleshooting

### **Error: "Table doesn't exist"**
**Solution:** Start the backend server first to let it create the tables, then run the SQL script.

### **Error: "Duplicate entry for key 'username'"**
**Solution:** The data already exists. Either:
- Delete existing data first
- Skip this step (data is already there)

### **Error: "Access denied for user 'root'"**
**Solution:** Check your MySQL password in `application.properties`:
```properties
spring.datasource.username=root
spring.datasource.password=pass
```

### **Error: "Unknown database 'library_db'"**
**Solution:** The database will be created automatically when you start the backend. Start the server first.

---

## 📝 Next Steps After Import

1. ✅ Restart your backend server
2. ✅ Test login with different users
3. ✅ View dashboard statistics
4. ✅ Try creating new records
5. ✅ Test role-based access control
6. ✅ Verify fine calculations

---

## 🎉 Success Indicators

After successful import, you should see:
- ✅ 10 users in the database
- ✅ 10 authors with biographies
- ✅ 10 books across different categories
- ✅ 10 transactions (4 issued, 6 returned)
- ✅ 3 fines (2 unpaid, 1 paid)
- ✅ 5 reservations (3 pending, 1 fulfilled, 1 cancelled)
- ✅ 5 feedback entries
- ✅ 5 about statements
- ✅ 5 notifications

---

**Ready to populate your database! Run the SQL script and start testing! 🚀**
