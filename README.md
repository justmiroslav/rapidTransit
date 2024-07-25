# RapidTransit

RapidTransit is a console-based Java application that represents robust and user-friendly ticketing and trip management system for both users and administrators.

## Table of Contents
1. [Prerequisites](#prerequisites)
2. [Project Structure](#project-structure)
3. [Installation](#installation)
4. [Configuration](#configuration)
5. [Building the Project](#building-the-project)
6. [Running the Application](#running-the-application)
7. [Usage](#usage)
8. [Contributing](#contributing)

## Prerequisites

- Java 22
- Maven 3.6.0 or higher
- PostgreSQL 13 or higher

## Project Structure

```bash
.
├── data/
│   ├── users.txt
│   ├── routes.txt
│   ├── buses.txt
│   ├── admins.txt
│   └── trips.txt
├── src/
│   └── main/
│       └── java/
│           └── org/
│               ├── dbCreation/
│               │   └── ProgramStart.java
│               └── rapidTransit/
│                   ├── dao/
│                   ├── db/
│                   ├── model/
│                   ├── service/
│                   ├── ui/
│                   ├── util/
│                   └── Main.java
├── test/
│   └── java/
│       └── org/
│           └── rapidTransit/
│               ├── dao/
│               └── service/
├── .gitignore
├── pom.xml
└── README.md
```

## Installation
1. Clone the repository:

```bash
git clone https://github.com/justmiroslav/rapidTransit.git
```
```bash
cd rapidTransit
```

2. Ensure you have Java 22 and Maven installed:

```bash
java --version
```
```bash
mvn --version
```

## Configuration

Set up the following environment variables with your PostgreSQL database credentials:

For Windows PowerShell:
```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5432/your_database"
$env:DB_USER = "your_username"
$env:DB_PASS = "your_password"
```

For Unix-based systems:
```bash
export DB_URL="jdbc:postgresql://localhost:5432/your_database"
export DB_USER="your_username"
export DB_PASS="your_password"
```

## Building the Project
1. Build the the database initialization:
```bash
mvn clean package -P db-init
```

2. Build the main application:
```bash
mvn package -P main-app
```
or simply
```bash
mvn package
```

## Running the Application

1. Initialize the database (this step creates tables and loads initial data):
```bash
java --enable-preview -jar target/rapidTransit-db-init-1.0-SNAPSHOT.jar
```

2. Run the main application:
```bash
java --enable-preview -jar target/rapidTransit-main-1.0-SNAPSHOT.jar
```

## Usage

After running the main application, you will be presented with a console interface. Follow the on-screen prompts to:

+ Register as a new user or log in as an existing user/admin
+ Browse available routes and trips/Purchase tickets/Manage your account (for users)
+ Manage routes, users, and view reports (for admins)

## Contributing
Contributions are welcome! Please feel free to submit a Pull Request.
