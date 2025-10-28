# ForgeGrid Test Cases

## Application Overview
ForgeGrid is a task management application for coding challenges with authentication, onboarding, and dashboard features.

---

## Test Cases for Welcome UI

| Test Case ID | Test Scenario | Test Steps / Inputs | Expected Result | Actual Result | Status (Pass/Fail) |
|-------------|---------------|---------------------|-----------------|---------------|-------------------|
| TC01 | Display Welcome Screen | 1. Launch ForgeGrid application | Welcome screen displays with logo, title "ForgeGrid", tagline, and "Start" button | | |
| TC02 | Verify Welcome Screen Elements | 1. Check all UI elements on welcome screen | Logo, title, tagline, and Start button are visible and properly styled | | |
| TC03 | Navigate to Login from Welcome | 1. Open application<br>2. Click "Start" button | Application navigates to Login screen | | |
| TC04 | Welcome Screen Responsiveness | 1. Resize the window<br>2. Check UI elements | All elements remain properly aligned and visible | | |

---

## Test Cases for Authentication UI

| Test Case ID | Test Scenario | Test Steps / Inputs | Expected Result | Actual Result | Status (Pass/Fail) |
|-------------|---------------|---------------------|-----------------|---------------|-------------------|
| TC05 | Display Login Screen | 1. Navigate from Welcome screen or directly to Login | Login screen displays with email/username field, password field, Remember Me checkbox, and login button | | |
| TC06 | Display Signup Screen | 1. Click "New User? Sign Up" button | Signup screen displays with username, email, and password fields | | |
| TC07 | Switch from Login to Signup | 1. On Login screen<br>2. Click "New User? Sign Up" button | Navigate to Signup screen | | |
| TC08 | Switch from Signup to Login | 1. On Signup screen<br>2. Click "Already have an account? Login" button | Navigate back to Login screen | | |
| TC09 | Back Button Visibility | 1. Navigate to Login/Signup screens | Back button is visible in header | | |
| TC10 | Back Button Navigation | 1. On Login/Signup screen<br>2. Click back arrow button | Navigate back to Welcome screen | | |
| TC11 | Logo Display on Login Screen | 1. Open Login screen | ForgeGrid logo displays at the top | | |
| TC12 | Logo Display on Signup Screen | 1. Open Signup screen | ForgeGrid logo and branding are visible | | |

---

## Test Cases for Login Functionality

| Test Case ID | Test Scenario | Test Steps / Inputs | Expected Result | Actual Result | Status (Pass/Fail) |
|-------------|---------------|---------------------|-----------------|---------------|-------------------|
| TC13 | Successful Login | 1. Enter valid username/email<br>2. Enter valid password<br>3. Click "Login" | User is authenticated and navigates to onboarding prompt or dashboard | | |
| TC14 | Login with Invalid Username | 1. Enter non-existent username<br>2. Enter any password<br>3. Click "Login" | Error message displays: "Invalid username or password." | | |
| TC15 | Login with Invalid Password | 1. Enter valid username<br>2. Enter incorrect password<br>3. Click "Login" | Error message displays: "Invalid username or password." | | |
| TC16 | Login with Empty Username | 1. Leave username field empty<br>2. Enter password<br>3. Click "Login" | Error message: "Please fill in all fields." | | |
| TC17 | Login with Empty Password | 1. Enter username<br>2. Leave password field empty<br>3. Click "Login" | Error message: "Please fill in all fields." | | |
| TC18 | Login with Empty Fields | 1. Leave both fields empty<br>2. Click "Login" | Error message: "Please fill in all fields." | | |
| TC19 | Login Button Loading State | 1. Enter credentials<br>2. Click "Login" button | Button text changes to "Authenticating..." and button is disabled during authentication | | |
| TC20 | Login Username Placeholder Behavior | 1. Click on username field<br>2. Start typing | Placeholder clears and input is accepted | | |
| TC21 | Login Password Placeholder Behavior | 1. Click on password field<br>2. Start typing | Placeholder clears and password is hidden (bullets) | | |
| TC22 | Password Visibility Toggle | 1. Enter password<br>2. Click "Show" button | Password characters are visible<br>Button changes to "Hide" | | |
| TC23 | Hide Password after Toggle | 1. With password visible<br>2. Click "Hide" button | Password is hidden with bullets<br>Button changes to "Show" | | |
| TC24 | Login with Remember Me | 1. Enter credentials<br>2. Check "Remember Me"<br>3. Click "Login"<br>4. Logout<br>5. Restart app | Credentials are auto-filled in login fields | | |
| TC25 | Login without Remember Me | 1. Enter credentials<br>2. Uncheck "Remember Me"<br>3. Click "Login"<br>4. Logout<br>5. Restart app | Credentials are NOT auto-filled | | |
| TC26 | Keyboard Navigation Login Form | 1. Press Tab key repeatedly on login form | Focus moves through fields in order: username → password → login button | | |
| TC27 | Enter Key Submission | 1. Enter credentials<br>2. Press Enter key while in password field | Login is submitted without clicking button | | |

---

## Test Cases for Signup Functionality

| Test Case ID | Test Scenario | Test Steps / Inputs | Expected Result | Actual Result | Status (Pass/Fail) |
|-------------|---------------|---------------------|-----------------|---------------|-------------------|
| TC28 | Successful Signup | 1. Enter unique username<br>2. Enter email<br>3. Enter password<br>4. Click "Sign Up" | Account created successfully<br>Success message displays<br>Navigates to Login screen | | |
| TC29 | Signup with Duplicate Username | 1. Enter existing username<br>2. Enter email<br>3. Enter password<br>4. Click "Sign Up" | Error message: "Username already exists. Please choose a different email." | | |
| TC30 | Signup with Duplicate Email | 1. Enter username<br>2. Enter existing email<br>3. Enter password<br>4. Click "Sign Up" | Error message: "Username already exists. Please choose a different email." | | |
| TC31 | Signup with Empty Username | 1. Leave username empty<br>2. Enter email<br>3. Enter password<br>4. Click "Sign Up" | Error message lists missing fields | | |
| TC32 | Signup with Empty Email | 1. Enter username<br>2. Leave email empty<br>3. Enter password<br>4. Click "Sign Up" | Error message lists missing fields | | |
| TC33 | Signup with Empty Password | 1. Enter username<br>2. Enter email<br>3. Leave password empty<br>4. Click "Sign Up" | Error message lists missing fields | | |
| TC34 | Signup with All Empty Fields | 1. Leave all fields empty<br>2. Click "Sign Up" | Error message: "Please fill in all fields: Username, Email, Password." | | |
| TC35 | Username Equals Email Validation | 1. Enter "test" as username<br>2. Enter "test" as email<br>3. Enter password<br>4. Click "Sign Up" | Error message: "Username and Email cannot be the same." | | |
| TC36 | Signup Button Loading State | 1. Enter valid data<br>2. Click "Sign Up" | Button text changes to "Creating Account..." and button is disabled | | |
| TC37 | Signup Field Placeholder Behavior | 1. Click on each field<br>2. Start typing | Placeholders clear and input is accepted | | |
| TC38 | Keyboard Navigation Signup Form | 1. Press Tab key repeatedly | Focus moves through: username → email → password → signup button | | |
| TC39 | Enter Key Submission in Signup | 1. Fill all fields<br>2. Press Enter in password field | Signup is submitted | | |
| TC40 | Reset Signup Fields after Success | 1. Complete successful signup<br>2. Navigate to Signup again | All fields reset to placeholder state | | |

---

## Test Cases for Password Reset

| Test Case ID | Test Scenario | Test Steps / Inputs | Expected Result | Actual Result | Status (Pass/Fail) |
|-------------|---------------|---------------------|-----------------|---------------|-------------------|
| TC41 | Access Password Reset | 1. On Login screen<br>2. Click "Forgot Password?" link | Dialog opens asking for username/email | | |
| TC42 | Reset Password with Valid Username | 1. Click "Forgot Password?"<br>2. Enter valid username<br>3. Enter new password (min 6 chars)<br>4. Confirm | Password is reset<br>Success message displays | | |
| TC43 | Reset Password with Invalid Username | 1. Click "Forgot Password?"<br>2. Enter non-existent username<br>3. Click OK | Error message: "Username not found." | | |
| TC44 | Reset Password with Short Password | 1. Click "Forgot Password?"<br>2. Enter valid username<br>3. Enter password < 6 characters | Error message: "Password must be at least 6 characters long." | | |
| TC45 | Cancel Password Reset | 1. Open password reset dialog<br>2. Click Cancel | Dialog closes without resetting password | | |
| TC46 | Login with Reset Password | 1. Reset password<br>2. Login with new password | Login successful with new password | | |

---

## Test Cases for Onboarding Flow

| Test Case ID | Test Scenario | Test Steps / Inputs | Expected Result | Actual Result | Status (Pass/Fail) |
|-------------|---------------|---------------------|-----------------|---------------|-------------------|
| TC47 | Onboarding Prompt for New User | 1. Login as new user | "Do onboarding now?" prompt is displayed with Yes/Skip options | | |
| TC48 | Skip Onboarding | 1. On onboarding prompt<br>2. Click "Skip for now" | Navigate to Dashboard<br>Onboarding marked as skipped in DB | | |
| TC49 | Start Onboarding - New User | 1. On onboarding prompt<br>2. Click "Yes, start onboarding" | Navigate to Question 1: "What is your primary goal?" | | |
| TC50 | Question 1 Options Display | 1. Reach Question 1 | All 4 goal options are displayed: Learn fundamentals, Interview prep, Competitive programming, Build projects | | |
| TC51 | Select Goal in Question 1 | 1. On Question 1<br>2. Select any option<br>3. Click "Continue" | Navigate to Question 2 | | |
| TC52 | Continue Button Disabled Initially | 1. On Question 1<br>2. Don't select anything | Continue button is disabled | | |
| TC53 | Back Navigation from Question 2 | 1. Reach Question 2<br>2. Click "Back" button | Return to Question 1 | | |
| TC54 | Question 2 Options Display | 1. Reach Question 2 | All 4 language options displayed: Java, Python, C, JavaScript | | |
| TC55 | Select Language in Question 2 | 1. On Question 2<br>2. Select any option<br>3. Click "Continue" | Navigate to Question 3 | | |
| TC56 | Question 3 Options Display | 1. Reach Question 3 | All 4 skill level options displayed | | |
| TC57 | Select Skill Level and Complete | 1. On Question 3<br>2. Select any option<br>3. Click "Continue" | Navigate to completion screen with "Continue to Dashboard" button | | |
| TC58 | Complete Onboarding Journey | 1. Answer all 3 questions<br>2. Click "Continue to Dashboard" on completion screen | Onboarding data saved to DB<br>Navigate to Dashboard | | |
| TC59 | Welcome Back for Returning User | 1. Login as user who completed onboarding | Welcome back message displays with "Continue to Dashboard" button | | |
| TC60 | Return to Dashboard from Welcome Back | 1. On welcome back screen<br>2. Click "Continue to Dashboard" | Navigate to Dashboard with previous onboarding data | | |

---

## Test Cases for Loading Screen

| Test Case ID | Test Scenario | Test Steps / Inputs | Expected Result | Actual Result | Status (Pass/Fail) |
|-------------|---------------|---------------------|-----------------|---------------|-------------------|
| TC61 | Loading Screen Display | 1. Trigger loading (e.g., after login) | Loading screen displays with "ForgeGrid", tagline, and "Loading..." status | | |
| TC62 | Loading Screen Branding | 1. View loading screen | Logo, brand name, and tagline are properly displayed | | |
| TC63 | Loading Screen Auto-Navigation | 1. After login<br>2. Wait for processing | Loading screen transitions to next screen automatically | | |

---

## Test Cases for Dashboard Functionality

| Test Case ID | Test Scenario | Test Steps / Inputs | Expected Result | Actual Result | Status (Pass/Fail) |
|-------------|---------------|---------------------|-----------------|---------------|-------------------|
| TC64 | Dashboard Initial Load | 1. Complete login/onboarding<br>2. Reach Dashboard | Dashboard displays with sidebar navigation and center content area | | |
| TC65 | Sidebar Navigation Display | 1. View Dashboard | Sidebar shows: Home, Tasks, Assigned Tasks, Completed Tasks, Missed Tasks, Goated Tasks, Profile, Settings, Help, Motivation, Achievements, Progress Tracker | | |
| TC66 | Navigate to Home View | 1. On Dashboard<br>2. Click "Home" in sidebar | Center area shows home/dashboard view with stats | | |
| TC67 | Navigate to Tasks View | 1. On Dashboard<br>2. Click "Tasks" in sidebar | Center area shows tasks list | | |
| TC68 | Navigate to Profile View | 1. On Dashboard<br>2. Click "Profile" in sidebar | Center area shows user profile information | | |
| TC69 | Navigate to Settings View | 1. On Dashboard<br>2. Click "Settings" in sidebar | Center area shows settings options | | |
| TC70 | User Info Card Display | 1. View Dashboard sidebar | User info card displays at bottom of sidebar with username | | |
| TC71 | Level Display on Dashboard | 1. View Dashboard top panel | Current level is displayed correctly | | |
| TC72 | XP Progress Bar Display | 1. View Dashboard | XP progress bar displays current XP and max XP | | |
| TC73 | Logout Functionality | 1. On Dashboard<br>2. Click logout (if available) | User is logged out and returns to Welcome/Login screen | | |
| TC74 | Dashboard Responsive Layout | 1. Resize Dashboard window | All UI elements remain properly aligned and functional | | |
| TC75 | Window Close Confirmation | 1. On Dashboard<br>2. Attempt to close window (X button) | Confirmation dialog appears: "Are you sure you want to exit ForgeGrid?" | | |
| TC76 | Confirm Exit Dialog | 1. Close dialog appears<br>2. Click "Yes" | Application closes completely | | |
| TC77 | Cancel Exit Dialog | 1. Close dialog appears<br>2. Click "No" | Dialog closes and application remains open | | |

---

## Test Cases for Database Connection

| Test Case ID | Test Scenario | Test Steps / Inputs | Expected Result | Actual Result | Status (Pass/Fail) |
|-------------|---------------|---------------------|-----------------|---------------|-------------------|
| TC78 | Database Connection Success | 1. Launch application with valid DB credentials | Application connects to database successfully | | |
| TC79 | Database Connection Failure | 1. Disconnect database or use invalid credentials<br>2. Launch application | Error message displays: "Unable to connect to database" or similar | | |
| TC80 | Login with Disconnected Database | 1. Disconnect database<br>2. Attempt login | Error message displays or application handles gracefully | | |
| TC81 | Data Persistence After Restart | 1. Complete some actions<br>2. Close application<br>3. Restart application<br>4. Login | All previous data (tasks, level, XP) is preserved | | |

---

## Test Cases for UI/UX Elements

| Test Case ID | Test Scenario | Test Steps / Inputs | Expected Result | Actual Result | Status (Pass/Fail) |
|-------------|---------------|---------------------|-----------------|---------------|-------------------|
| TC82 | Window Icon Display | 1. View any window | ForgeGrid logo displays in window title bar | | |
| TC83 | Application Title Display | 1. View any screen | Title bar displays "ForgeGrid" correctly | | |
| TC84 | Theme Colors Consistency | 1. Navigate through all screens | Pink brand color (Theme.BRAND_PINK) is consistently used | | |
| TC85 | Button Hover Effects | 1. Hover over any button | Visual feedback on hover (if implemented) | | |
| TC86 | Field Focus Indicators | 1. Click on any input field | Field receives visual focus indicator | | |
| TC87 | Error Message Display | 1. Trigger any error | Error message displays in dialog or inline, is clear and user-friendly | | |
| TC88 | Success Message Display | 1. Complete successful operation | Success message displays appropriately | | |
| TC89 | Dialog Modal Behavior | 1. Open any dialog | Dialog blocks interaction with background until dismissed | | |

---

## Test Cases for Edge Cases and Error Handling

| Test Case ID | Test Scenario | Test Steps / Inputs | Expected Result | Actual Result | Status (Pass/Fail) |
|-------------|---------------|---------------------|-----------------|---------------|-------------------|
| TC90 | Special Characters in Username | 1. Enter username with special characters (e.g., user@123)<br>2. Attempt signup | Signup is validated appropriately or error message for invalid characters | | |
| TC91 | Very Long Username | 1. Enter username > 50 characters<br>2. Attempt signup | Validation error or truncated appropriately | | |
| TC92 | SQL Injection Attempt | 1. Enter SQL code in username field (e.g., 'OR'1'='1)<br>2. Attempt login | Login fails safely without SQL injection | | |
| TC93 | XSS Attack Attempt | 1. Enter JavaScript in username field<br>2. Attempt signup | Script is sanitized and not executed | | |
| TC94 | Rapid Button Clicks | 1. Click "Login" button multiple times rapidly | Only one login attempt is processed | | |
| TC95 | Window Resize to Minimum | 1. Resize window to minimum size<br>2. Check UI | All elements remain visible and functional | | |
| TC96 | Large Window Size | 1. Maximize window<br>2. Check UI | Layout adjusts properly to large screen size | | |
| TC97 | Concurrent Login Attempts | 1. Open multiple instances<br>2. Login simultaneously | Application handles concurrent requests properly | | |

---

## Test Summary

**Total Test Cases:** 97  
**Coverage Areas:**
- Welcome UI (4 test cases)
- Authentication UI (8 test cases)
- Login Functionality (15 test cases)
- Signup Functionality (13 test cases)
- Password Reset (6 test cases)
- Onboarding Flow (14 test cases)
- Loading Screen (3 test cases)
- Dashboard Functionality (14 test cases)
- Database Connection (4 test cases)
- UI/UX Elements (8 test cases)
- Edge Cases and Error Handling (8 test cases)

**Notes:**
- All test cases should be executed with valid database connection
- Test cases can be automated or executed manually
- Actual Results and Status columns should be filled during execution
- Test cases are designed for both positive and negative testing scenarios

