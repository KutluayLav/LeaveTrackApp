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
│   │       ├── service/        # Business Logic
│   │       ├── repository/     # Data Access
│   │       ├── model/          # Entities
│   │       ├── dto/            # Data Transfer Objects
│   │       ├── security/       # JWT & Security
│   │       └── config/         # Configuration
│   └── src/main/resources/
│       └── application.properties
└── leavetrack-frontend/        # Next.js Frontend
    ├── src/
    │   ├── app/               # App Router pages
    │   ├── components/        # React components
    │   ├── services/          # API services
    │   ├── contexts/          # React contexts
    │   └── types/             # TypeScript types
    └── package.json
```

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Node.js 18+
- MS SQL Server
- Maven

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
