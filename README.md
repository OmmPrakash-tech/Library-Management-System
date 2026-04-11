# 📚 Library Management System

![Spring Boot](https://img.shields.io/badge/Backend-SpringBoot-green)
![Angular](https://img.shields.io/badge/Frontend-Angular-red)
![MySQL](https://img.shields.io/badge/Database-MySQL-blue)
![JWT](https://img.shields.io/badge/Auth-JWT-orange)
![Razorpay](https://img.shields.io/badge/Payment-Razorpay-purple)

A full-stack **Library Management System** built using **Spring Boot, Angular, and MySQL**, designed to manage books, users, borrowing, reservations, and subscriptions efficiently.


Live Demo - https://library-management-system-3d9t.onrender.com

---

## 🚀 Features

### 👤 User Features
- 🔐 User registration & login (JWT authentication)
- 📚 Browse and search books
- 📖 View book details
- ❤️ Add books to wishlist
- 📦 Borrow and return books
- 📅 Reserve unavailable books
- 🕒 View borrowing history
- 💳 Subscription-based borrowing system
- 💰 Online payment integration (Razorpay)

### 🛠️ Admin Features
- 📊 Admin dashboard
- ➕ Add / ✏️ update / ❌ delete books
- 👥 Manage users and staff
- 📋 View all reservations and borrow records
- 💳 Manage subscriptions and payments
- 📈 Track book availability and usage

---

## 🧑‍💻 Tech Stack

### 🌐 Frontend
- Angular  
- TypeScript  
- HTML, CSS *(Glassy UI Design)*  

### ⚙️ Backend
- Spring Boot  
- Spring Security (JWT Authentication)  
- REST APIs  

### 🗄️ Database
- MySQL  

### 💳 Payment Integration
- Razorpay *(Test Mode)*  

---

## 📂 Project Structure

```bash
Library-Management-System/
│
├── backend/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── model/
│   └── config/
│
├── frontend/
│   ├── src/app/
│   │   ├── user/
│   │   ├── admin/
│   │   ├── services/
│   │   └── components/
│
└── database/
    └── schema.sql
```

## ⚙️ Installation & Setup

### 🔧 Backend Setup (Spring Boot)

Clone the repository:

```bash
git clone https://github.com/your-username/library-management.git
cd backend
```
Configure application.properties:
```
spring.datasource.url=jdbc:mysql://localhost:3306/library_db
spring.datasource.username=root
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

Run the backend server:
```
mvn spring-boot:run
```
💻 Frontend Setup (Angular)

Navigate to frontend:
```
cd library-frontend
```
Install dependencies:
```
npm install
```
Run Angular app:
```
ng serve
```
Open in browser:

http://localhost:4200
🔐 API Endpoints
📘 Book APIs
```
GET /api/books → Get all books
POST /api/books → Add book (Admin)
DELETE /api/books/{id} → Delete book
```
❤️ Wishlist APIs
```
POST /api/wishlist/add/{bookId}
GET /api/wishlist
DELETE /api/wishlist/{id}
```
📦 Borrow APIs
```
POST /api/borrow/{bookId}
GET /api/borrow/my
```
📅 Reservation APIs
```
POST /api/reservation/{bookId}
GET /api/reservation/my
```
💳 Payment APIs
```
POST /api/payment/create-order
POST /api/payment/verify
```
🔄 System Workflow<br>

User → Register/Login → Choose Subscription → Payment (Razorpay)
     → Subscription Activated → Borrow / Wishlist / Reserve Books
     <br>
     
📌 Key Concepts Implemented<br>

🔐 JWT Authentication & Authorization<br>
🧱 MVC Architecture<br>
🔗 RESTful API Design<br>
💳 Subscription-based access control<br>
✔️ Payment verification system<br>
⚡ Real-time UI updates with Angular<br>
❗ Exception handling & validation<br>
🧪 Testing<br>
✅ API tested using Postman<br>
✅ Frontend tested with real user flows<br>
✅ Payment tested in Razorpay Test Mode<br>
📈 Future Enhancements<br>
📧 Email notifications (borrow/reminder)<br>
💸 Fine calculation for late returns<br>
🤖 AI-based book recommendation system<br>
📱 Mobile app version<br>
📊 Admin analytics dashboard<br>
👨‍🎓 Author<br>

Omm Prakash Debata
💻 Software Engineering Student
🚀 Full Stack Developer

📄 License

This project is developed for educational purposes.
