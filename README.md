# LeaveTrackApp

A comprehensive employee leave tracking system with role-based access control, built with Spring Boot backend and Next.js frontend.

## 🚀 Features

- **Employee Leave Management**: Create, update, and track leave requests
- **Role-Based Access Control**: Admin and User roles with different permissions
- **Workday Calculation**: Automatic calculation excluding weekends
- **Leave Limit Enforcement**: Configurable yearly leave limits
- **Real-time Dashboard**: Responsive admin and user dashboards
- **JWT Authentication**: Secure authentication with refresh tokens
- **Asynchronous Processing**: Non-blocking operations for better performance

## 🛠️ Tech Stack

### Backend
- **Spring Boot : Main framework
- **Spring Security**: Authentication and authorization
- **JPA/Hibernate**: Database ORM
- **JWT**: Token-based authentication
- **SQL Server**: Database
- **Maven**: Dependency management

### Frontend
- **Next.js 14**: React framework with App Router
- **TypeScript**: Type safety
- **Tailwind CSS**: Styling and responsive design
- **React Hook Form**: Form management
- **Axios**: HTTP client
- **React Context**: State management

## 📊 Key Features

### Admin Features
- ✅ User management (CRUD operations)
- ✅ Department management
- ✅ Leave approval/rejection
- ✅ Advanced filtering and search
- ✅ System configuration
- ✅ Leave statistics and reports

### User Features
- ✅ Leave request creation
- ✅ Leave history tracking
- ✅ Leave summary and limits
- ✅ Real-time status updates

## 🏗️ Project Structure

```
leavetrackapp/
├── leavetrack/                 # Spring Boot Backend
│   ├── src/main/java/
│   │   └── com/kutluayulutas/leavetrack/
│   │       ├── controller/     # REST Controllers
│   │       │   ├── AuthController.java
│   │       │   ├── LeaveController.java
│   │       │   ├── LeaveAdminController.java
│   │       │   ├── UserController.java
│   │       │   └── DepartmentController.java
│   │       ├── service/        # Business Logic
│   │       │   ├── AuthService.java
│   │       │   ├── LeaveService.java
│   │       │   ├── UserService.java
│   │       │   ├── DepartmentService.java
│   │       │   └── RefreshTokenService.java
│   │       ├── repository/     # Data Access
│   │       │   ├── UserRepository.java
│   │       │   ├── LeaveRepository.java
│   │       │   ├── DepartmentRepository.java
│   │       │   └── RefreshTokenRepository.java
│   │       ├── model/          # Entities
│   │       │   ├── User.java
│   │       │   ├── Leave.java
│   │       │   ├── Department.java
│   │       │   ├── RefreshToken.java
│   │       │   ├── Role.java
│   │       │   ├── LeaveType.java
│   │       │   └── LeaveStatus.java
│   │       ├── dto/            # Data Transfer Objects
│   │       │   ├── request/    # Request DTOs
│   │       │   │   ├── LoginRequest.java
│   │       │   │   ├── RegisterRequest.java
│   │       │   │   ├── LeaveRequestDTO.java
│   │       │   │   └── TokenRefreshRequest.java
│   │       │   └── response/   # Response DTOs
│   │       │       ├── AuthResponse.java
│   │       │       ├── UserDTO.java
│   │       │       ├── LeaveDTO.java
│   │       │       ├── DepartmentDTO.java
│   │       │       └── SuccessResponse.java
│   │       ├── mapper/         # Entity to DTO Mappers
│   │       │   ├── UserMapper.java
│   │       │   ├── LeaveMapper.java
│   │       │   └── DepartmentMapper.java
│   │       ├── exception/      # Custom Exceptions
│   │       │   ├── GlobalExceptionHandler.java
│   │       │   ├── CustomAuthenticationException.java
│   │       │   ├── LeaveLimitExceededException.java
│   │       │   ├── InvalidDateRangeException.java
│   │       │   ├── LeaveNotFoundException.java
│   │       │   ├── EmailAlreadyUsedException.java
│   │       │   └── NotFoundException.java
│   │       ├── security/       # JWT & Security
│   │       │   ├── JwtTokenProvider.java
│   │       │   ├── JwtAuthenticationFilter.java
│   │       │   ├── SecurityConfig.java
│   │       │   └── UserDetailsServiceImpl.java
│   │       ├── config/         # Configuration
│   │       │   ├── LeaveConfig.java
│   │       │   └── AsyncConfig.java
│   │       └── LeavetrackApplication.java
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   └── data.sql
│   └── pom.xml
└── leavetrack-frontend/        # Next.js Frontend
    ├── src/
    │   ├── app/               # App Router pages
    │   │   ├── page.tsx       # Home page
    │   │   ├── login/         # Login page
    │   │   ├── dashboard/     # User dashboard
    │   │   ├── admin/         # Admin dashboard
    │   │   └── leaves/        # Leave management
    │   ├── contexts/          # React contexts
    │   │   └── AuthContext.tsx
    │   ├── services/          # API services
    │   │   └── api.ts
    │   └── types/             # TypeScript types
    │       └── index.ts
    ├── public/                # Static assets
    ├── package.json
    └── README.md
```

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Node.js 18+
- MS SQL Server
- Maven

### 📁 Backend Architecture

#### **Controller Layer** (`controller/`)
- **AuthController**: Login, logout, token refresh
- **LeaveController**: Leave CRUD operations, filtering, summary
- **LeaveAdminController**: Admin-specific leave configuration
- **UserController**: User management (Admin only)
- **DepartmentController**: Department management

#### **Service Layer** (`service/`)
- **AuthService**: Authentication and user management
- **LeaveService**: Business logic for leave operations
- **UserService**: User CRUD operations
- **DepartmentService**: Department operations
- **RefreshTokenService**: JWT refresh token management

#### **Repository Layer** (`repository/`)
- **UserRepository**: User data access
- **LeaveRepository**: Leave data access with custom queries
- **DepartmentRepository**: Department data access
- **RefreshTokenRepository**: Refresh token data access

#### **Model Layer** (`model/`)
- **User**: User entity with roles and department
- **Leave**: Leave request entity with status tracking
- **Department**: Department entity
- **RefreshToken**: JWT refresh token entity
- **Role**: User role enumeration
- **LeaveType**: Leave type enumeration
- **LeaveStatus**: Leave status enumeration

#### **DTO Layer** (`dto/`)
- **Request DTOs**: Input validation and data transfer
- **Response DTOs**: Structured API responses
- **SuccessResponse**: Standardized response wrapper

#### **Mapper Layer** (`mapper/`)
- **UserMapper**: User entity ↔ UserDTO conversion
- **LeaveMapper**: Leave entity ↔ LeaveDTO conversion
- **DepartmentMapper**: Department entity ↔ DepartmentDTO conversion

#### **Exception Layer** (`exception/`)
- **GlobalExceptionHandler**: Centralized error handling
- **Custom Exceptions**: Domain-specific error types
- **Validation Exceptions**: Input validation errors

#### **Security Layer** (`security/`)
- **JwtTokenProvider**: JWT token generation and validation
- **JwtAuthenticationFilter**: JWT authentication filter
- **SecurityConfig**: Spring Security configuration
- **UserDetailsServiceImpl**: Custom user details service

#### **Configuration Layer** (`config/`)
- **LeaveConfig**: Leave system configuration
- **AsyncConfig**: Asynchronous processing configuration

### Backend Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/KutluayLav/LeaveTrackApp.git
   cd LeaveTrackApp/leavetrack
   ```

2. **Configure database**
   - Update `application.properties` with your SQL Server credentials
   - Create database `trackdb`

3. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

### Frontend Setup

1. **Navigate to frontend directory**
   ```bash
   cd ../leavetrack-frontend
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Run development server**
   ```bash
   npm run dev
   ```

### 📱 Frontend Architecture

#### **App Router** (`app/`)
- **page.tsx**: Home page with routing logic
- **login/page.tsx**: Authentication page
- **dashboard/page.tsx**: User dashboard with leave summary
- **admin/page.tsx**: Admin dashboard with management tools
- **leaves/page.tsx**: Leave list and management
- **leaves/new/page.tsx**: New leave request form

#### **Context Layer** (`contexts/`)
- **AuthContext**: Global authentication state management
- **User state**: Current user information
- **Token management**: JWT token handling

#### **Service Layer** (`services/`)
- **api.ts**: Axios HTTP client with interceptors
- **Token refresh**: Automatic token renewal
- **Error handling**: Centralized error management

#### **Type System** (`types/`)
- **index.ts**: TypeScript interfaces and types
- **API types**: Request/response type definitions
- **Component props**: React component type definitions

## 🔐 Authentication

### Default Admin User
- **Email**: admin@leavetrack.com
- **Password**: admin123
- **Role**: ROLE_ADMIN

### User Registration
- Only admins can create new users
- Users cannot self-register

## 📡 API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/refresh-token` - Refresh JWT token
- `POST /api/auth/logout` - User logout

### Leave Management
- `GET /api/leaves` - Get all leaves (Admin)
- `POST /api/leaves` - Create leave request
- `PUT /api/leaves/{id}` - Update leave request
- `DELETE /api/leaves/{id}` - Delete leave request
- `GET /api/leaves/summary` - Get leave summary
- `GET /api/leaves/filter` - Filter leaves

### User Management
- `GET /api/users` - Get all users (Admin)
- `POST /api/users` - Create user (Admin)
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user (Admin)

### Department Management
- `GET /api/departments` - Get all departments
- `POST /api/departments` - Create department (Admin)
- `PUT /api/departments/{id}` - Update department (Admin)
- `DELETE /api/departments/{id}` - Delete department (Admin)

## 🔧 Configuration

### Leave Settings
- `leave.max-yearly-leave-days`: Maximum yearly leave days (default: 20)
- `leave.enable-work-day-calculation`: Enable weekend exclusion (default: true)
- `leave.enable-leave-limit-check`: Enable leave limit enforcement (default: true)

### JWT Settings
- `jwt.secret`: JWT secret key
- `jwt.expiration`: Access token expiration (30 seconds for testing)
- `jwt.expirationms`: Refresh token expiration (24 hours)

## 🚀 Deployment

### Backend Deployment
```bash
mvn clean package
java -jar target/leavetrack-0.0.1-SNAPSHOT.jar
```

### Frontend Deployment
```bash
npm run build
npm start
```

## 👨‍💻 Author

**Kutluay Ulutaş**
- GitHub: [@KutluayLav](https://github.com/KutluayLav)

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
