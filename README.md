# 📚 Library Management System

A **Java Spring Boot** based application for managing library operations.  
It provides **RESTful APIs** to handle books, users, and admin activities efficiently.  

---

## 🚀 Tech Stack

- **Backend:** Java 11+, Spring Boot  
- **Database:** MySQL / H2 (for testing)  
- **Build Tool:** Maven  
- **API Style:** RESTful Services  

---

## 🧩 Project Overview

This project aims to simplify library operations by providing:
- Book and user management  
- Admin control panel for managing data  
- Borrow/return tracking  
- Configurable persistence layer (H2 for testing, MySQL for production)



## ⚙️ Prerequisites

Make sure you have installed:
- [Java 11+](https://adoptium.net/)
- [Maven 3.6+](https://maven.apache.org/)
- [MySQL](https://www.mysql.com/) *(optional if using H2)*
- [Git](https://git-scm.com/)

---

## 🏁 Quick Start (Maven)


# 1️ Clone the repository
git clone https://github.com/OmmPrakash-tech/Library-Management-System.git

# 2️ Navigate into the project
cd Library-Management-System

# 3️ Build the project
mvn clean package

# 4️ Run the application
mvn spring-boot:run
