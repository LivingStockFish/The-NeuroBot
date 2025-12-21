# NeuroBot - AI Assistant

## Overview
NeuroBot is a JavaFX-based AI Assistant application that provides an interactive chat interface with AI capabilities. The application features a modern UI, user authentication, and various AI model integrations.

## Key Features

### 1. Authentication System
- User registration and login
- Secure password hashing
- Session management
- Admin and user roles

### 2. User Interface
- Modern, responsive design
- Dark/Light theme support
- Intuitive chat interface
- Navigation between different views

### 3. Core Functionality
- AI-powered chat with support for multiple models
- Chat history persistence
- Feedback system
- Model selection

### 4. Admin Panel
- User management
- Feedback review system
- API key management
- System monitoring

## Review 2 Implementation Criteria

### 1. Servlet Implementation
- Implemented `ProjectServlet` for handling HTTP requests
- Configured servlet mappings in `ServletConfig`
- Added request/response handling for API endpoints
- Integrated with the existing JavaFX application

### 2. Code Quality
- Followed Java coding standards and best practices
- Implemented proper error handling and logging
- Used design patterns (MVC) for better code organization
- Added input validation and sanitization
- Comprehensive code documentation

### 3. Innovation
- Implemented a unique model switching system
- Added a feedback mechanism for continuous improvement
- Developed a user-friendly admin interface
- Integrated secure authentication with role-based access control
- Added chat history persistence

## Technical Stack
- Java 17
- JavaFX
- SQLite Database
- Gradle Build Tool
- CSS for styling

## Setup and Running the Application

### Prerequisites

- Java 21 or later (Eclipse Adoptium recommended)
- Gradle 7.0 or later
- Internet connection (for dependencies and AI model access)

### Running the Application

1. **Clone the repository**:
   git clone https://github.com/LivingStockFish/The-NeuroBot.git
   cd The-NeuroBot

2. **Build the project**:
   cd javafx-ai-assistant
   ./gradlew build

3. **Run the application**:
   ./gradlew run
Or directly using Java:
   java -jar build/libs/neurobot.jar

4. **Access the Application**:
    The application will start and show a message in the terminal:
        🚀 Application is running!
    🔗 Access the web interface at: http://localhost:8080/project
    Open the provided URL in your web browser to access the servlet interface
    The desktop application will also launch automatically

5. **Default Access**:
    Use the desktop application for the main interface
    The web interface at http://localhost:8080/project provides additional servlet-based functionality

**Configuration**
Update database settings in src/main/resources/application.properties
Configure API keys and other settings as needed

**Troubleshooting**
Ensure port 8080 is available
Verify Java 21 is installed and set as the default Java version
Check the terminal for any error messages if the application fails to start

## Dependencies
- JavaFX 17
- SQLite JDBC
- Gson
- JavaFX WebView

## Contributors
- [Your Name/Team Name]
- [Other Contributors]

## License
[Your License Here]