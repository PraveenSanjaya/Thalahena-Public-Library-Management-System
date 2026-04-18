# Thalahena Public Library Management System

A complete Library Management System with Spring Boot backend and React frontend.

## Features
- **Authentication**: JWT Based with Email OTP for password reset.
- **Role Based Access**: Admin, Staff, and Member dashboards.
- **Book Management**: CRUD with cover image upload.
- **Member Management**: Admin can manage all users.
- **Circulation**: Issue and Return books with automatic late fine calculation (Rs. 5/day).
- **Dashboards**: KPI summaries and charts (Gender/User status distribution).
- **Responsive Design**: Premium dark-mode UI with glassmorphism.

## Tech Stack
- **Backend**: Spring Boot 3.2, Spring Security, JWT, JPA, MySQL, JavaMailSender.
- **Frontend**: React 18, Vite, React Router, Axios, Chart.js, Lucide Icons, Framer Motion.
- **Database**: MySQL.

## Getting Started

### 1. Database Setup
1. Create a database named `library_db` in MySQL.
2. Run the `init.sql` script located in the root directory to seed initial data.
   - Alternatively, the application will automatically create tables and seed users on first run via `CommandLineRunner`.

### 2. Backend Setup (`/backend`)
1. Ensure Java 17+ and Maven are installed.
2. Open `src/main/resources/application.properties` and update:
   - `spring.datasource.username` and `spring.datasource.password` for your MySQL.
   - `spring.mail.username` and `spring.mail.password` (use a Gmail App Password) for OTP functionality.
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```
   The backend will start at `http://localhost:8080`.

### 3. Frontend Setup (`/frontend`)
1. Ensure Node.js is installed.
2. Navigate to the frontend directory:
   ```bash
   cd frontend
   npm install
   npm run dev
   ```
   The frontend will start at `http://localhost:5173`.

## Default Credentials
- **Admin**: `admin1` / `admin123`
- **Staff**: `staff1` / `staff123`
- **Member**: `user1` / `user123`

## Directory Structure
- `src/main/java/.../entity`: Database models.
- `src/main/java/.../security`: JWT and Security configs.
- `src/main/java/.../controller`: API endpoints.
- `frontend/src/pages`: UI pages.
- `frontend/src/components`: Reusable UI components.
- `uploads/`: Directory where book covers and profile pictures are saved.
