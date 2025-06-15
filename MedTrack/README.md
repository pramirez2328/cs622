# MEDTRACK: A Medical Appointment and Records Management System

**Author**: Pedro Ramirez  
**Course**: CS622 – Summer 2025

---

## 📦 ABOUT

MEDTRACK is a modular Java application simulating core workflows in a medical clinic:

- Patient registration
- Doctor scheduling
- Appointment booking and lookup
- Background autosaving and asynchronous persistence

It is designed with maintainability in mind, leveraging design patterns like **Facade**, and technologies like **SQLite
**, **multithreading**, and **JUnit 5** for testing.

---

## 🚀 HOW TO RUN THE APPLICATION

The CLI application requires **interactive input via `Scanner`**, so **do not use Gradle’s `run` task**.

### ✅ Recommended: Run from IntelliJ IDEA

1. Open the project in **IntelliJ IDEA**
2. Ensure your JDK is set to **Java 17 or later** (Java 23 used during development)
3. Open the file:

```

src/main/java/app/Main.java

```

4. Right-click the `Main` class → **Run 'Main'**

### 🧭 On First Launch

You will be prompted:

```

Reinitialize the database from scratch? (yes/no):

```

- Enter `yes` to drop and recreate the SQLite schema
    - Seeds patients from `data/patients.csv`
    - Seeds doctors from `data/doctors.csv`
    - Imports appointments from `data/appointments.txt`
- Enter `no` to load the current database state from `data/medtrack.db`

After startup, you’ll see the menu:

```

0. Clean db and reload from CSV files
1. Register as new patient
2. Book an appointment
3. View my appointments
4. Exit

````

---

### ❌ Not Supported: `./gradlew run`

Due to `Scanner.nextLine()` being incompatible with Gradle’s non-interactive run environment, avoid:

```bash
./gradlew run
````

Instead, use the IntelliJ run button or the terminal instructions below.

---

### 🔹 Alternative: Run from Terminal (if needed)

#### 1. Build the project:

```bash
./gradlew build
```

#### 2. Run with Java CLI:

```bash
java -cp build/classes/java/main:build/resources/main app.Main
```

> On Windows, replace `:` with `;`:

```cmd
java -cp build\classes\java\main;build\resources\main app.Main
```

---

## 🧪 RUNNING TESTS

Tests are written using **JUnit 5** and cover concurrency, DB persistence, and functional flows.

1. Navigate to `src/test/java/` in IntelliJ
2. Right-click any test (e.g., `AppointmentManagerTest`) → **Run**
3. Or use:

```bash
./gradlew test
```

✅ Tests include:

* Patient/Doctor registration and lookup
* Appointment creation and conflict checks
* Autosave and concurrent booking validations
* Database seeding and persistence checks

---

## 📁 PROJECT STRUCTURE

```
src/
├── main/
│   └── java/
│       ├── app/         → Main CLI class
│       ├── model/       → Domain entities (Patient, Doctor, Appointment)
│       └── service/     → DB layer, concurrency, autosave, facades

├── test/
│   └── java/
│       ├── model/       → Unit tests for components
│       └── usecases/    → Integration and workflow tests

data/
├── patients.csv         → Patient data (used for seeding)
├── doctors.csv          → Doctor data (used for seeding)
├── appointments.txt     → Legacy appointment entries
├── medtrack.db          → Persistent SQLite database
```

---

## 🔧 FEATURES

* ✅ SQLite persistence (self-contained, no external DB required)
* ✅ Thread-safe booking via `ReentrantLock`
* ✅ Background appointment saving with blocking queue
* ✅ Autosave service for patient data every 40 seconds
* ✅ CSV and TXT file seeding on first run
* ✅ Graceful shutdown and background thread handling
* ✅ Facade design pattern for simplified access
* ✅ Robust error and input validation

---

## 📌 NOTES

* Appointments are saved in `data/medtrack.db`
* CSV and TXT files are only used when resetting the database
* You can reseed the system at any time using Option 0 in the CLI menu
* Doctors are preloaded and fixed; only patients can register dynamically
* The app exits cleanly and flushes any unsaved data before shutdown

---

**Created by Pedro Ramirez – Summer 2025**


