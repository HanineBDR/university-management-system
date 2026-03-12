# Student Management System - Setup Guide

This document provides step-by-step instructions to set up and run the Student Management System.

## Prerequisites

Before you begin, ensure you have the following installed:

- ✅ **Java 11 or higher**
- ✅ **Maven 3.6+** (included with the project via Maven Wrapper)
- ✅ **MySQL 8.0 or higher**
- ✅ **MySQL Workbench** (recommended for running SQL scripts)

## Step-by-Step Setup

### Step 1: Verify MySQL Installation

1. Open MySQL Workbench or MySQL Command Line
2. Verify MySQL is running:
   ```sql
   SELECT VERSION();
   ```

### Step 2: Create the Database

1. Open MySQL Workbench
2. Create a new SQL tab
3. Navigate to: `d:\S5-GL\DAAW\projet\spring-security-tp\spring-security-tp\src\main\resources\database\schema.sql`
4. Copy the entire content and paste it into MySQL Workbench
5. Execute the script (click the lightning bolt ⚡ icon or press Ctrl+Shift+Enter)
6. Verify the database was created:
   ```sql
   SHOW DATABASES;
   USE student_management_db;
   SHOW TABLES;
   ```

You should see 5 tables:
- `users`
- `students`
- `teachers`
- `modules`
- `marks`

### Step 3: Populate with Test Data

1. In MySQL Workbench, open a new SQL tab
2. Navigate to: `d:\S5-GL\DAAW\projet\spring-security-tp\spring-security-tp\src\main\resources\database\seed-data.sql`
3. Copy the entire content and paste it into MySQL Workbench
4. Execute the script
5. Verify data was inserted:
   ```sql
   SELECT * FROM users;
   SELECT * FROM students;
   SELECT * FROM teachers;
   SELECT * FROM modules;
   SELECT * FROM marks;
   ```

### Step 4: Configure Database Credentials

The application is already configured to use:
- **Host:** localhost:3306
- **Database:** student_management_db
- **Username:** root
- **Password:** 2005

If your MySQL uses different credentials, edit `src/main/resources/application.properties`:

```properties
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

### Step 5: Build the Project

Open a terminal/command prompt in the project directory and run:

```bash
cd d:\S5-GL\DAAW\projet\spring-security-tp\spring-security-tp
.\mvnw.cmd clean install
```

**Expected output:**
```
[INFO] BUILD SUCCESS
[INFO] Total time:  ~20 s
```

### Step 6: Run the Application

```bash
.\mvnw.cmd spring-boot:run
```

**Look for these messages:**
```
============================================================
Student Management System - Database Status
============================================================
Total users in database: 9
✓ Database is populated and ready!

Test User Credentials:
- Admin: username=admin, password=password123
- Teacher: username=teacher_java, password=password123
- Student: username=student_hanine, password=password123
============================================================
Started SpringSecurityTpApplication in X.XXX seconds
```

### Step 7: Access the Application

Open your web browser and navigate to:
```
http://localhost:8080/login
```

## Test Login Credentials

### Administrator Account
- **Username:** `admin`
- **Password:** `password123`
- **Access:** Full system access

### Teacher Accounts
| Username | Password | Module |
|----------|----------|---------|
| `teacher_java` | `password123` | Advanced Java Programming |
| `teacher_web` | `password123` | Web Development |
| `teacher_database` | `password123` | Database Systems |

### Student Accounts
| Username | Password | Full Name |
|----------|----------|-----------|
| `student_hanine` | `password123` | Hanine El Mansouri |
| `student_sara` | `password123` | Sara Chahid |
| `student_youssef` | `password123` | Youssef Tazi |
| `student_amina` | `password123` | Amina El Fassi |
| `student_omar` | `password123` | Omar Zaki |

## Testing the System

### Test as ADMIN

1. Login with `admin` / `password123`
2. Test these endpoints (using browser or Postman):
   - `GET http://localhost:8080/api/admin/users` - View all users
   - `GET http://localhost:8080/api/admin/students` - View all students
   - `GET http://localhost:8080/api/admin/teachers` - View all teachers
   - `GET http://localhost:8080/api/admin/marks` - View all marks

### Test as TEACHER

1. Login with `teacher_java` / `password123`
2. Test these endpoints:
   - `GET http://localhost:8080/api/teacher/module` - View assigned module
   - `GET http://localhost:8080/api/teacher/students` - View students in module
   - `GET http://localhost:8080/api/teacher/marks` - View all marks for module

### Test as STUDENT

1. Login with `student_hanine` / `password123`
2. Test these endpoints:
   - `GET http://localhost:8080/api/student/profile` - View own profile
   - `GET http://localhost:8080/api/student/marks` - View own marks
   - `GET http://localhost:8080/api/student/report` - View academic report

## Troubleshooting

### Issue: "No users found in database!"

**Solution:**
1. Make sure you ran both SQL scripts (schema.sql and seed-data.sql)
2. Verify in MySQL Workbench:
   ```sql
   USE student_management_db;
   SELECT COUNT(*) FROM users;
   ```
   Should return 9

### Issue: "Access Denied for user 'root'@'localhost'"

**Solution:**
1. Check your MySQL credentials
2. Update `application.properties` with correct username/password

### Issue: "Communications link failure"

**Solution:**
1. Verify MySQL is running
2. Check if MySQL is listening on port 3306:
   ```sql
   SHOW VARIABLES LIKE 'port';
   ```

### Issue: Port 8080 already in use

**Solution:**
1. Change the port in `application.properties`:
   ```properties
   server.port=8081
   ```
2. Or stop the application using port 8080

### Issue: Build fails with compilation errors

**Solution:**
```bash
.\mvnw.cmd clean
.\mvnw.cmd compile
```

## Next Steps

After successfully running the backend:

1. **Frontend Development** - Create HTML/CSS/JavaScript pages for:
   - Login page
   - Admin dashboard
   - Teacher dashboard
   - Student dashboard

2. **API Integration** - Connect frontend with REST API endpoints

3. **Testing** - Comprehensive testing of all features

4. **Deployment** - Deploy to production server

## Project Structure Reference

```
spring-security-tp/
├── src/main/resources/database/
│   ├── schema.sql          ← Run this first
│   └── seed-data.sql       ← Then run this
├── src/main/resources/
│   └── application.properties   ← Database config here
└── README.md
```

## Support

If you encounter any issues:

1. Check the console output for error messages
2. Verify all SQL scripts were executed successfully
3. Ensure MySQL is running and accessible
4. Check the `application.properties` configuration

## Summary Checklist

- [ ] MySQL installed and running
- [ ] Database created (schema.sql executed)
- [ ] Test data loaded (seed-data.sql executed)
- [ ] Database credentials configured
- [ ] Project built successfully (`mvnw clean install`)
- [ ] Application running (`mvnw spring-boot:run`)
- [ ] Can login with test credentials
- [ ] API endpoints accessible

---

**Congratulations!** Your Student Management System backend is now ready! 🎉
