# Student Management System

A comprehensive Student Management System built with Spring Boot, Spring Security, MySQL, and HTML/CSS/JavaScript. The system implements role-based access control with three distinct user roles: ADMIN, TEACHER, and STUDENT.

## 🚀 Features

### Authentication & Authorization
- **Session-based authentication** with Spring Security
- **BCrypt password encryption** for secure password storage
- **Role-based access control** (ADMIN, TEACHER, STUDENT)
- Account enable/disable functionality

### User Roles & Permissions

#### 🔴 ADMIN
- Full system access
- Create, update, delete user accounts
- Assign roles (ADMIN, TEACHER, STUDENT)
- Enable/disable user accounts
- View all students, teachers, modules, and marks
- View own profile information

#### 🔵 TEACHER
- View assigned module
- View students enrolled in their module
- Add, update, delete marks for students in their module
- View marks report for their module
- View own profile information

#### 🟢 STUDENT
- View own profile (name, email, username, enrollment date)
- View own marks across all modules
- View academic report with statistics (average, highest, lowest)

## 📋 Prerequisites

- **Java 11** or higher
- **Maven** 3.6+
- **MySQL** 8.0+
- **MySQL Workbench** (recommended)
- Modern web browser

## ⚙️ Installation & Setup

### 1. Clone the Repository

```bash
cd d:\S5-GL\DAAW\projet\spring-security-tp\spring-security-tp
```

### 2. Database Setup

1. Open MySQL Workbench
2. Run the following scripts in order:

**First, create the database and tables:**
```bash
src/main/resources/database/schema.sql
```

**Then, populate with test data:**
```bash
src/main/resources/database/seed-data.sql
```

### 3. Configure Database Connection

The database configuration is already set in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/student_management_db
spring.datasource.username=root
spring.datasource.password=2005
```

If your MySQL credentials are different, update these values.

### 4. Build the Project

```bash
mvnw clean install
```

### 5. Run the Application

```bash
mvnw spring-boot:run
```

The application will start on **http://localhost:8080**

## 👤 Test User Credentials

After running the seed data script, you can login with these accounts:

| Role | Username | Password | Description |
|------|----------|----------|-------------|
| ADMIN | `admin` | `password123` | Full system access |
| TEACHER | `teacher_java` | `password123` | Java Programming teacher |
| TEACHER | `teacher_web` | `password123` | Web Development teacher |
| TEACHER | `teacher_database` | `password123` | Database Systems teacher |
| STUDENT | `student_hanine` | `password123` | Student with marks |
| STUDENT | `student_sara` | `password123` | Student with marks |
| STUDENT | `student_youssef` | `password123` | Student with marks |
| STUDENT | `student_amina` | `password123` | Student with marks |
| STUDENT | `student_omar` | `password123` | Student with marks |

## 📊 Database Schema

### Tables

- **`users`** - Base authentication (id, username, email, password, role, enabled)
- **`students`** - Student profiles (id, user_id, full_name, enrollment_date)
- **`teachers`** - Teacher profiles (id, user_id, full_name, hire_date)
- **`modules`** - Course modules (id, name, code, teacher_id)
- **`marks`** - Student marks (id, student_id, module_id, teacher_id, score, date)

### Relationships

- One-to-One: User ↔ Student, User ↔ Teacher
- One-to-Many: Teacher → Module, Student → Mark, Module → Mark
- Many-to-One: Mark → Teacher

## 🌐 API Endpoints

### Authentication
- `POST /api/auth/login` - Login
- `GET /api/profile` - Get current user profile

### Admin Endpoints (`/api/admin/*`)
- `GET /users` - Get all users
- `POST /users` - Create user
- `PUT /users/{id}` - Update user
- `DELETE /users/{id}` - Delete user
- `PUT /users/{id}/enable` - Enable/disable user
- `GET /students` - Get all students
- `GET /teachers` - Get all teachers
- `GET /modules` - Get all modules
- `GET /marks` - Get all marks

### Teacher Endpoints (`/api/teacher/*`)
- `GET /module` - Get assigned module
- `GET /students` - Get students in module
- `GET /marks` - Get marks for module
- `POST /marks` - Add mark
- `PUT /marks/{id}` - Update mark
- `DELETE /marks/{id}` - Delete mark

### Student Endpoints (`/api/student/*`)
- `GET /profile` - Get own profile
- `GET /marks` - Get own marks
- `GET /report` - Get academic report

## 🛡️ Security Features

- **Method-level security** with `@PreAuthorize`
- **Ownership validation** - users can only access their own data
- **Role hierarchy** - prevents unauthorized access
- **Session management** - 30 minute timeout
- **CORS configuration** - for frontend integration

## 📁 Project Structure

```
src/main/java/com/example/spring_security_tp/
├── config/
│   ├── WebSecurityConfig.java       # Security configuration
│   └── DataInitializer.java         # Startup validator
├── controller/
│   ├── AdminController.java         # Admin REST API
│   ├── TeacherController.java       # Teacher REST API
│   ├── StudentController.java       # Student REST API
│   └── ProfileController.java       # Profile REST API
├── dto/
│   ├── LoginRequest.java            # Login DTO
│   └── UserDTO.java                 # User DTO
├── model/
│   ├── User.java                    # User entity
│   ├── Student.java                 # Student entity
│   ├── Teacher.java                 # Teacher entity
│   ├── Module.java                  # Module entity
│   └── Mark.java                    # Mark entity
├── repository/
│   ├── UserRepository.java
│   ├── StudentRepository.java
│   ├── TeacherRepository.java
│   ├── ModuleRepository.java
│   └── MarkRepository.java
└── service/
    ├── CustomUserDetailsService.java
    ├── UserService.java
    ├── StudentService.java
    ├── TeacherService.java
    ├── ModuleService.java
    └── MarkService.java

src/main/resources/
├── application.properties            # Application configuration
├── database/
│   ├── schema.sql                   # Database schema
│   └── seed-data.sql                # Test data
└── templates/                       # HTML templates (to be added)
```

## 🔧 Configuration

### Application Properties

Key configurations in `application.properties`:

- **Server Port:** 8080
- **Database:** student_management_db
- **Session Timeout:** 30 minutes
- **JPA DDL Auto:** none (manual schema)
- **SQL Logging:** enabled (DEBUG level)

## 🧪 Testing

### Manual Testing

1. **Login Flow:**
   - Navigate to http://localhost:8080/login
   - Login with different roles
   - Verify redirects to appropriate dashboard

2. **Admin Functions:**
   - Create new users
   - Assign roles
   - View all data

3. **Teacher Functions:**
   - View assigned module and students
   - Add/update/delete marks
   - Verify ownership validation

4. **Student Functions:**
   - View profile and marks
   - Check academic report calculations

### API Testing

Use Postman or cURL to test REST endpoints:

```bash
# Get current user profile (after login)
curl -X GET http://localhost:8080/api/profile --cookie "STUDENT_MANAGEMENT_SESSION=..."

# Admin: Get all users
curl -X GET http://localhost:8080/api/admin/users --cookie "STUDENT_MANAGEMENT_SESSION=..."
```

## 🎨 Frontend (Coming Soon)

The frontend will include:
- Login page
- Admin dashboard
- Teacher dashboard
- Student dashboard
- Modern, responsive design with HTML/CSS/JavaScript

## 📝 Notes

- Database schema is managed manually via SQL scripts
- All passwords are encrypted with BCrypt
- Session-based authentication (not JWT)
- One teacher teaches one module
- Each student receives maximum one mark per module
- Teachers can only modify marks for their own modules

## 🐛 Troubleshooting

### Database Connection Issues

If you see "No users found in database" warning:
1. Verify MySQL is running
2. Check database credentials in `application.properties`
3. Run `schema.sql` and `seed-data.sql` in MySQL Workbench

### Build Errors

```bash
mvnw clean
mvnw install -U
```

### Port Already in Use

Change the port in `application.properties`:
```properties
server.port=8081
```

## 👥 Contributors

- Developed as a university project for Spring Boot and Spring Security

## 📄 License

This project is for educational purposes.
