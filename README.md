# FocusFlow Study Companion

**Final Project for IT 306W - Mobile Application Development 1**

---

## About the Application

**FocusFlow** is a minimalist, single-session study companion built for students. The app is designed to help you lock in your focus by setting one active goal at a time, using proven productivity techniques to maximize study efficiency and maintain momentum.

### Key Features

- **Multiple Productivity Techniques**: Choose from five built-in techniques or create custom goals:
  - **Pomodoro Technique**: 25-minute work sessions with 5-minute breaks (4 cycles, then long break)
  - **52/17 Method**: 52-minute intensive work sessions with 17-minute breaks
  - **90-Minute Ultradian**: Aligns with natural ultradian rhythm for optimal productivity
  - **Sprint Sessions**: Quick 12-minute focused bursts with 1.5-minute breaks
  - **Deadline Simulation**: Custom work duration with no scheduled breaks
  - **Custom Goal**: Set your own work and break durations

- **Session Management**:
  - Real-time countdown timer with visual progress indicators
  - Pause and resume functionality
  - Break management with skip/extend options
  - Cycle tracking for multi-cycle techniques (e.g., Pomodoro)

- **Activity Tracking**:
  - View completed study sessions in "My Activity" tab
  - Detailed session information including technique used, time spent, and completion details
  - Session history organized by most recent first

- **User-Friendly Interface**:
  - Dark theme with purple and teal accent colors
  - Tab-based navigation (PlayGround & My Activity)
  - Sidebar navigation menu
  - Responsive layouts for both portrait and landscape orientations

- **Transient Data Management**:
  - All data handled in-memory (no persistent storage)
  - Data passed between activities using `Intent.putExtra()` and `startActivityForResult()`
  - Session data lost on app close (as per project requirements)

---

## Application Architecture

### Activities (Total: 5)

1. **LoginActivity** - Entry point for user authentication
2. **RegistrationActivity** - New user registration with input validation
3. **MainActivity** - Core application interface with technique selection and session management
4. **AboutProjectActivity** - Information about the FocusFlow project
5. **AboutTeamActivity** - Meet the development team

### Core Components

- **Fragments**: 
  - `PlaygroundFragment` - Technique selection interface
  - `MyActivityFragment` - Completed sessions display

- **Data Models**:
  - `StudySession` - Active session data structure
  - `CompletedSession` - Completed session history
  - `SessionState` - Session state enumeration
  - `User` - User account information

- **Key Features**:
  - TabLayout with ViewPager2 for tab navigation
  - Bottom sheet dialogs for session creation and management
  - Sidebar navigation menu
  - RecyclerView for displaying completed sessions
  - CountDownTimer for session countdown

---

## Project Requirements & Compliance

### Core Requirements Met

**Login Activity (5%)**: Secure authentication with email and password validation  
**Registration Activity (5%)**: User onboarding with comprehensive input validation  
**Feature Implementation (35%)**: Core focus goal setting with Intent-based data passing  
**Creative UI/UX (25%)**: Modern dark theme with Material Design components  
**Video Advertisement (15%)**: Promotional video showcasing app functionality  
**Documentation (10%)**: Comprehensive README and project documentation  

### Activity Count

**Total Activities: 5**
- LoginActivity
- RegistrationActivity  
- MainActivity
- AboutProjectActivity
- AboutTeamActivity

*Note: Achieving 6+ activities yields full 5 points. Current implementation has 5 activities.*

---

## Technical Implementation

### Data Passing Strategy

The application strictly adheres to the requirement of **no persistent storage**. All data is passed between activities using:

- **`Intent.putExtra()`**: For passing data from one activity to another
- **`startActivityForResult()` / `registerForActivityResult()`**: For receiving data back from activities
- **Static Collections**: `HashMap` for temporary user storage (e.g., `RegistrationActivity.userMap`)
- **Transient Variables**: In-memory session data that is lost when the app closes

### Key Technical Features

- **Activity Lifecycle Management**: Proper handling of `onPause()` and `onResume()` for timer state
- **Fragment Management**: ViewPager2 with FragmentStateAdapter for tab navigation
- **Material Design Components**: MaterialButton, MaterialCardView, TextInputLayout, Chip, etc.
- **Responsive Layouts**: Separate layouts for portrait (`layout/`) and landscape (`layout-land/`)
- **Dark Theme**: Consistent dark color scheme throughout the application

---

## Team & Contributions

| Team Member          | Primary Focus Area              | Key Tasks & Deliverables                                                                                                                                    | Project Requirements Hit        |
| -------------------- | ------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------ | ------------------------------- |
| **Jerome Avecilla**  | (Dev Lead) Core Logic & Design  | 1. Implement `GoalSetupActivity.java` (Feature Logic).<br>2. Implement `MainActivity.java` to receive data using `registerForActivityResult`.<br>3. Design **all XML layouts** and handle styling/theming to meet **R4 (Creative UI)**.<br>4. Ensure seamless data passing via Intents. | R3 (Feature Logic), R4 (UI/UX)  |
| **Mico Oleriana**    | (Dev) Authentication Activities | 1. Implement `LoginActivity.java` logic.<br>2. Implement `RegistrationActivity.java` logic and input validation.<br>3. Ensure Registration passes the Username back to Login using `Intent.putExtra`.                   | R1 (Login), R2 (Registration)   |
| **Rensen Dela Cruz** | Documentation Lead              | 1. Write the "Brief description of your app; its features and functions."<br>2. Compile and label all activity screenshots.<br>3. Finalize and review the `README.md`.                                                  | Documentation (10%)             |
| **Francis Palma**    | Media and Presentation          | 1. Produce the **Video Advertisement** (must be ≥45 seconds).<br>2. Collect and manage **proof of collaboration** (online meeting screenshots/photos).                                                                  | R5 (Video Ad)                   |
| **Ralp Andre Giga**  | Quality Assurance (QA) & Setup  | 1. Thoroughly test all activity navigation and data passing.<br>2. Verify Android Manifest and necessary Gradle dependencies (like Material Design) are correct.<br>3. Ensure the final app build is stable.            | Activities Count, App Stability |

---

## Project Scoring Matrix

| Component                             | Weight   |
| :------------------------------------ | :------- |
| Login Activity                        | 5 %      |
| Registration Activity                 | 5 %      |
| Number of Activities\*                | 5 %      |
| App Features / Functions (Core Logic) | **35 %** |
| UI/UX (Design & Usability)            | 25 %     |
| Video Advertisement                   | 15%      |
| Documentation                         | 10%      |
| **TOTAL**                             | **100%** |

_\*Activity Scoring Note: Achieving 6 or more activities yields the full 5 points; 3 activities yields 3 points._

---

## Documentation Deliverables

### Required Documentation

- Brief description of the application, its features, and functions (see above)
- Screenshot of the app for **every single Activity** developed
- Proof of collaboration (e.g., screenshots of online meetings or group work photos)

### Activities Requiring Screenshots

1. LoginActivity
2. RegistrationActivity
3. MainActivity (PlayGround tab)
4. MainActivity (My Activity tab)
5. AboutProjectActivity
6. AboutTeamActivity

---

## Project Objectives

This project demonstrates proficiency in fundamental Android Java development through:

1. **Activity Lifecycle Management**: Proper handling of activity states, data persistence during lifecycle events, and timer management
2. **Transient Data Handling**: All data managed in-memory without persistent storage (no databases, SharedPreferences, or file storage)
3. **Intent-Based Communication**: Seamless data passing between activities using Android's Intent system
4. **Material Design Implementation**: Modern, user-friendly interface following Material Design guidelines
5. **Fragment Management**: Efficient use of fragments for modular UI components
6. **Responsive Design**: Support for both portrait and landscape orientations

---

## Final Presentation

The live demonstration and final submission are scheduled for the **LAST WEEK OF THE FIRST SEMESTER** during the designated F2F (Face-to-Face) class session.

---

## Development Notes

### Important Constraints

- **No Persistent Storage**: All data must be transient (lost when app closes)
- **Intent-Based Data Passing**: Must use `Intent.putExtra()` and `startActivityForResult()` only
- **No External APIs**: All functionality must be self-contained
- **Java Only**: Implementation must be in Java (not Kotlin)

### Build Requirements

- **Minimum SDK**: As specified in project requirements
- **Target SDK**: Latest stable Android version
- **Material Design**: Material Components for Android library
- **ViewPager2**: For tab navigation
- **RecyclerView**: For displaying completed sessions list

---

## License & Credits

This project is developed as part of the IT 306W (Mobile Application Development 1) course requirements.

**Development Team:**
- Jerome Avecilla (Dev Lead)
- Mico Oleriana
- Rensen Dela Cruz
- Francis Palma
- Ralp Andre Giga

---

## Project Resources

This sections contains our project resources used to aid development and design.

- **[Wireframe Designs](https://excalidraw.com/#room=78e6eae75fa363c48594,iw5jl-1xBPeYpWHIAOrzEw)**: Link to the Excalidraw file containing low-fidelity wireframe designs.
- **[Teams Documentation](https://drive.google.com/drive/folders/1uSIWyRkSVmJmf4j9u83AnWYCwxaOhdMM?usp=sharing)**: Google Drive folder containing all project-related documents, design files, photo documentation, app video presentation, and meeting notes.
