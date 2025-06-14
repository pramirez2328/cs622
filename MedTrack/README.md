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

## 🚀 RUNNING THE APPLICATION

The CLI requires interactive input via `Scanner`, so there are two supported ways to run the app.

### 🔹 Option 1: Run from IntelliJ (Recommended)

1. Open the project in **IntelliJ IDEA**
2. Navigate to: `src/main/java/app/Main.java`
3. Right-click on the `Main` class → **Run 'Main'**

✅ This is the **preferred way** to run the app interactively.

---

### 🔹 Option 2: Run via terminal (outside Gradle)

Gradle's `run` task does **not** support interactive input. Instead, do this:

1. **Build the project**:
   ```bash
   ./gradlew build

2. **Run using the Java CLI**:

   ```bash
   java -cp build/classes/java/main:build/resources/main app.Main
   ```

   > On Windows, use `;` instead of `:` in the classpath:

   ```cmd
   java -cp build\classes\java\main;build\resources\main app.Main
   ```

⚠️ Avoid running with:

```bash
./gradlew run
```

It will fail with a `NoSuchElementException` due to `Scanner.nextLine()` not being supported by Gradle's run
environment.

---

## 🧪 RUNNING TESTS

Tests are written using **JUnit 5** and cover core features including concurrency, persistence, and error handling.

1. Open `src/test/java/` in IntelliJ
2. Right-click any test class (e.g., `AppointmentManagerTest`) → **Run**
3. Or run all tests with:

   ```bash
   ./gradlew test
   ```

Tests include:

* ✅ Patient/Doctor registration and lookup
* ✅ Appointment creation and conflict checks
* ✅ Autosave and concurrent booking validations
* ✅ Database persistence and recovery

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
│       ├── model/       → Unit tests for individual components
│       └── usecases/    → Integration and flow tests

data/
├── patients.csv         → Initial patient data (CSV format)
├── doctors.csv          → Initial doctor data (CSV format)
├── appointments.txt     → Legacy data file (used for seeding)
├── medtrack.db          → Persistent SQLite database
```

---

## 🔧 FEATURES

* ✅ SQLite persistence (no need for external DB)
* ✅ Thread-safe booking using `ReentrantLock`
* ✅ Background queue for asynchronous appointment saving
* ✅ Autosave service for patient data every 40 seconds
* ✅ CSV seeding on first run
* ✅ Graceful error handling (invalid inputs, SQL failures)
* ✅ Facade pattern for unified access to system operations

---

## 📌 NOTES

* Patients and doctors are seeded from CSV only **once**
* All appointment data is stored in **`data/medtrack.db`**
* The autosave thread shuts down cleanly when the program exits
* You can reset the DB by choosing "yes" when prompted at startup
* Doctors are considered static; only patients can register dynamically

---

**Created by Pedro Ramirez – Summer 2025**

