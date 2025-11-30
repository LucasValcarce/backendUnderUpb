# UnderUpb Backend API Tester - Frontend Structure

## 🎮 Main Entry Points

### Home Page
**URL:** `http://localhost:8081/index.html`
- Splash screen with welcome message
- Links to Dashboard and Swagger documentation

### API Dashboard (Intermediary)
**URL:** `http://localhost:8081/dashboard.html`
- Service selection page with 10 service cards
- Each card links to its dedicated service tester

---

## 📋 Service Testers (Individual Pages)

### 1. Users Management
**URL:** `http://localhost:8081/services/users.html`
- Create User (POST)
- List Users (GET with pagination)
- Get User by ID (GET)
- Update User (PUT)

### 2. Levels Management
**URL:** `http://localhost:8081/services/levels.html`
- Create Level (POST)
- List Levels (GET with pagination)
- Get Level by ID (GET)
- Update Level (PUT)

### 3. Questions Management
**URL:** `http://localhost:8081/services/questions.html`
- Create Question (POST)
- List Questions (GET with pagination)
- Questions by Level with Random Selection (GET with optional count parameter)
- Get Question by ID (GET)
- Delete Question (DELETE)

### 4. Characters Management
**URL:** `http://localhost:8081/services/characters.html`
- Create Character (POST)
- List Characters (GET with pagination)
- Get Character by ID (GET)
- Update Character (PUT)

### 5. Enemies Management
**URL:** `http://localhost:8081/services/enemies.html`
- Create Enemy (POST)
- Enemies by Level (GET)
- Get Enemy by ID (GET)
- List All Enemies (GET with pagination)

### 6. Save Games Management
**URL:** `http://localhost:8081/services/saves.html`
- Save Game (POST)
- Get Latest Save (GET)
- Get All Saves for User (GET with pagination)
- Validate Save (POST)
- Delete Save (DELETE)

### 7. Leaderboard Management
**URL:** `http://localhost:8081/services/leaderboard.html`
- Top Players (GET)
- Top Players Paginated (GET)
- User Scores (GET)
- Record Score (POST)

### 8. Match Management
**URL:** `http://localhost:8081/services/match.html`
- Calculate Final Score (GET)
- End Match (POST)

### 9. Decisions Management
**URL:** `http://localhost:8081/services/decisions.html`
- Create Decision (POST)
- Get Decisions by Question (GET)
- Get Decision by ID (GET)
- List All Decisions (GET with pagination)
- Delete Decision (DELETE)

### 10. Purchases Management
**URL:** `http://localhost:8081/services/purchases.html`
- Create Purchase Order (POST)
- Get Purchase Order (GET)
- Complete Purchase Order (PUT)
- Fail Purchase Order (PUT)
- Payment Webhook (POST)

---

## 🗂️ File Structure

```
templates/
├── index.html              # Home/splash screen
├── dashboard.html          # Service selection page
├── styles.css              # Shared CSS styles
├── app.js                  # Shared JavaScript functions
└── services/
    ├── users.html
    ├── levels.html
    ├── questions.html
    ├── characters.html
    ├── enemies.html
    ├── saves.html
    ├── leaderboard.html
    ├── match.html
    ├── decisions.html
    └── purchases.html
```

---

## 🚀 How to Use

1. **Start the Backend:**
   ```
   mvn spring-boot:run
   ```

2. **Navigate to Home:**
   Open `http://localhost:8081/index.html` in your browser

3. **Choose a Service:**
   Click "Open API Dashboard" to see all 10 service testers

4. **Test Endpoints:**
   - Select a service to access its dedicated tester page
   - Fill in the form fields
   - Click the action button to send the request
   - View the response in real-time

---

## ✨ Features

- **Modular Design**: Each service has its own dedicated page
- **No External Dependencies**: Pure HTML5, CSS3, and vanilla JavaScript
- **Real-time Feedback**: Immediate response display with syntax highlighting
- **Form Validation**: Input fields with proper types and placeholders
- **Responsive Design**: Works on desktop and mobile devices
- **Easy Navigation**: Back buttons to return to dashboard
- **Visual Feedback**: Status indicators (success/error) for each request

---

## 🔗 Quick Links

| Service | URL |
|---------|-----|
| **Home** | http://localhost:8081/index.html |
| **Dashboard** | http://localhost:8081/dashboard.html |
| **Users** | http://localhost:8081/services/users.html |
| **Levels** | http://localhost:8081/services/levels.html |
| **Questions** | http://localhost:8081/services/questions.html |
| **Characters** | http://localhost:8081/services/characters.html |
| **Enemies** | http://localhost:8081/services/enemies.html |
| **Saves** | http://localhost:8081/services/saves.html |
| **Leaderboard** | http://localhost:8081/services/leaderboard.html |
| **Match** | http://localhost:8081/services/match.html |
| **Decisions** | http://localhost:8081/services/decisions.html |
| **Purchases** | http://localhost:8081/services/purchases.html |
| **Swagger API Docs** | https://localhost:8081/swagger-ui.html |

---

## 💡 Notes

- Backend must be running on `http://localhost:8081`
- All API responses are displayed with syntax highlighting
- Error responses show status code and error message
- Pagination support included where applicable
- UUIDs needed for most CRUD operations

