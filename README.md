# 📚 Library Management System

![Spring Boot](https://img.shields.io/badge/Backend-SpringBoot-green)
![Angular](https://img.shields.io/badge/Frontend-Angular-red)
![MySQL](https://img.shields.io/badge/Database-MySQL-blue)
![JWT](https://img.shields.io/badge/Auth-JWT-orange)
![Razorpay](https://img.shields.io/badge/Payment-Razorpay-purple)

A full-stack **Library Management System** built using **Spring Boot, Angular, and MySQL**, designed to manage books, users, borrowing, reservations, and subscriptions efficiently.

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
