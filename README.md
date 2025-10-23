# 🎯 FocusFlow Study Companion: Capstone Project (IT 306 - Mobile Application Development)

This repository hosts the source code and documentation for the **FocusFlow Study Companion**, the final project developed to fulfill the completion requirements for the **IT 306W (Mobile Application Development 1)** subject.

This project is a strategic implementation of core Android development concepts, focusing heavily on **Activity lifecycle management** and **transient data handling** without the use of persistent databases.

---

## Project Objectives & Core Requirements

The application successfully integrates the following non-negotiable requirements, demonstrating proficiency in fundamental Android Java development:

1.  **Login Activity (ACT #3):** Secure entry point for the application.
2.  **Registration Activity (ACT #4 and ACT #5):** User onboarding with necessary input validation.
3.  **Feature Implementation (Unit 3 - 7 Mastery):** The core application feature—setting a single focus goal—must strictly rely on passing data between Activities using **`Intent.putExtra`** and the **`startActivityForResult`** callback mechanism. **No database or persistent storage is utilized.**
4.  **Creative UI/UX:** The application features a creative, appealing, and user-centric interface.
5.  **Video Advertisement:** A minimum 45-second promotional video showcasing the app's functionality and value proposition.

---

## Project Scoring Matrix

This project is evaluated based on the following weighted criteria:

| Component | Weight |
| :--- | :--- |
| Login Activity | 5 % |
| Registration Activity | 5 % |
| Number of Activities* | 5 % |
| App Features / Functions (Core Logic) | **35 %** |
| UI/UX (Design & Usability) | 25 % |
| Video Advertisement | 15% |
| Documentation | 10% |
| **TOTAL** | **100%** |

*\*Activity Scoring Note: Achieving 6 or more activities yields the full 5 points; 3 activities yields 3 points.*

---

## Required Documentation Deliverables

The final submission must include the following documentation assets:

### Team & Contributions:
| Team Member             | Primary Focus Area          | Key Tasks & Deliverables                                                                                                                                                                                                                                                                                                                                                                           | Project Requirements Hit            |
|--------------------------|-----------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------|
| **Jerome Avecilla** | (Dev Lead) Core Logic & Design         | 1. Implement `GoalSetupActivity.java` (Feature Logic).<br>2. Implement `MainActivity.java` to receive data using `registerForActivityResult`.<br>3. Design **all XML layouts** and handle styling/theming to meet **R4 (Creative UI)**.<br>4. Ensure seamless data passing via Intents.                                                                                                            | R3 (Feature Logic), R4 (UI/UX)     |
| **TBA**        | (Dev) Authentication Activities   | 1. Implement `LoginActivity.java` logic.<br>2. Implement `RegistrationActivity.java` logic and input validation.<br>3. Ensure Registration passes the Username back to Login using `Intent.putExtra`.                                                                                                                                                                                                 | R1 (Login), R2 (Registration)      |
| **Francis Palma**        | Documentation Lead          | 1. Write the “Brief description of your app; its features and functions.”<br>2. Compile and label all activity screenshots.<br>3. Finalize and review the `README.md`.                                                                                                                                                                                                                                | Documentation (10%)                |
| **TBA**     | Media and Presentation      | 1. Produce the **Video Advertisement** (must be ≥45 seconds).<br>2. Collect and manage **proof of collaboration** (online meeting screenshots/photos).                                                                                                                                                                                                                                               | R5 (Video Ad)                      |
| **Ralp Andre Giga**     | Quality Assurance (QA) & Setup | 1. Thoroughly test all activity navigation and data passing.<br>2. Verify Android Manifest and necessary Gradle dependencies (like Material Design) are correct.<br>3. Ensure the final app build is stable.                                                                                                                                                                                        | Activities Count, App Stability    |


### TODO
* Brief description of the application, its features, and functions.
* Screenshot of the app for **every single Activity** developed.
* Proof of collaboration (e.g., screenshots of online meetings or group work photos).

---

## Final Presentation

The live demonstration and final submission are scheduled for the **LAST WEEK OF THE FIRST SEMESTER** during the designated F2F (Face-to-Face) class session.

---

## Final Presentation
