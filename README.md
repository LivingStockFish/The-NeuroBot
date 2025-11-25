# NeuroBot - AI-ChatBot

A sophisticated JavaFX-based desktop application that provides AI-powered assistance with both online and offline capabilities. Built with Spring Boot and JavaFX, this application offers a seamless user experience with features like user authentication, chat functionality, and admin controls.

## 🌟 Features

### User Authentication
- Secure login and registration system
- Role-based access control (Admin/User)
- Session management with preferences

### AI Chat Interface
- Real-time chat with AI assistant
- Support for text-based interactions
- Responsive UI with message history

### Admin Dashboard
- User management
- Feedback monitoring
- System configuration

### Additional Features
- Clean, modern UI with JavaFX
- Responsive design for various screen sizes
- Persistent data storage
- Export chat history

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Internet connection (for initial setup)

### 🧠 AI Model Setup (⚠ Required for Chatbot to Work)

NeuroBot uses local offline AI models powered by Ollama.
To enable AI chat features, you must install and configure Ollama.

## 📥 1. Install Ollama

Download and install Ollama from:
👉 https://ollama.com/download
Ollama supports Windows, macOS, and Linux.

## 🤖 2. Pull the Required Model

After installing Ollama, open Command Prompt / Terminal and run:

ollama run gemma3:4b

This will:
Download the Gemma 3 — 4B model
Start the model locally
Allow NeuroBot to connect automatically
You only need to run this once for setup.

## 🔗 3. Ensure Ollama is Running

Before starting NeuroBot, make sure Ollama is active:
On Windows → you can manually start it:
ollama serve

### Installation
1. Clone the repository
2. Build the project:
   mvn clean install
Run the application:
mvn spring-boot:run

### 🛠️ Configuration

### Database

NeuroBot uses:
SQLite database (users.db)
Stored locally inside your project folder
You can modify DB settings in:

DatabaseConfig.java

### AI Integration

Configure your AI service in:

ChatConfiguration.java
application.properties

### 📦 Dependencies

Spring Boot - Application framework
JavaFX - Desktop application UI
H2 Database - Embedded database
Lombok - Reduced boilerplate code
Spring AI - AI integration
