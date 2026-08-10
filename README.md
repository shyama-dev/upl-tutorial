    # 🎓 Online Course & Learning Management Web Service

A robust, secure Spring Boot RESTful Web Service built to support an Angular frontend for an online learning management platform. The service features **Role-Based Access Control (RBAC)** across three tier levels (*PUBLIC*, *INSTRUCTOR*, and *ADMIN*), stateless **JWT authentication**, and complete cross-cutting concerns including AOP logging, global exception handling, and interactive Swagger/OpenAPI documentation.

---

## 🚀 Key Features

### 👥 User Roles & Permissions Matrix
* **PUBLIC (Unauthenticated / General Users):**
  * Browse published courses and view course details/tutorials.
  * Register as an **Instructor**.
  * Authenticate (`/login`) to receive a signed JWT token.
* **INSTRUCTOR (Authenticated & Approved):**
  * Password secure hashing with **BCrypt**.
  * Full CRUD management for own **Courses** and **Tutorials**.
  * View instructor dashboard.
* **ADMIN (System Administrator):**
  * **Instructor Management:** Review pending instructor registrations and **Approve** or **Reject** accounts.
  * **Analytics & Metrics:** View system-wide course analytics, instructor statistics, and platform reporting.
  * **•	Monitor and moderate content:** Review the courses and mark it inactive if not appropriate.

---

## 🛠️ Technology Stack & Architecture

* **Backend:** Java 17/21, Spring Boot 4.x
* **Security:** Spring Security, JWT (JSON Web Token), BCrypt Password Encoder
* **Persistence & DB:** Spring Data JPA, Hibernate, MySQL
* **Aspect-Oriented Programming (AOP):** AspectJ & SLF4J 
* **API Documentation:** Springdoc OpenAPI 3.0 / Swagger UI
* **Testing:** JUnit 5, Mockito, Spring Security Test($$$)
* **Build & Management:** Maven

---

## 🏗️ System Architecture & Workflow

```text
+-------------------+            HTTP Requests            +-----------------------+
|  Angular Frontend |  ================================>  |  Spring Boot Service  |
|   (Client App)    |  <================================  |    (REST Controller)  |
+-------------------+       JSON / Bearer JWT Token       +-----------------------+
                                                                      |
                                                          +-----------+-----------+
                                                          |                       |
                                                   [Security Filter]      [Global Exception]
                                                   (JWT Validation)       (@ControllerAdvice)
                                                          |                       |
                                                          +-----------+-----------+
                                                                      |
                                                              [Service Layer]
                                                              (AOP SLF4J Log)
                                                                      |
                                                              [Data Access Layer]
                                                                 (Spring Data)