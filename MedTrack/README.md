Absolutely! Here's the ready-to-copy **`README.md`** content for your SQLite-powered MEDTRACK project:

---

````markdown
# MEDTRACK: A Medical Appointment and Records Management System

**Author**: Pedro Ramirez  
**Course**: CS622 – Summer 2025

---

## 🟢 HOW TO RUN THE APPLICATION

1. Open the project in **IntelliJ IDEA** (or any Java IDE that supports Gradle).

2. Ensure you have **JDK 17+** installed (Java 23 used during development).

3. Run the `Main` class located at:  
   `src/main/java/app/Main.java`

Or, from the terminal:

```bash
./gradlew run
````

This will:

* Create required SQLite tables (if not already present)
* Load patient and doctor data from `.csv` (only on first run)
* Start a **background autosave** thread for patient records
* Allow patient registration, appointment booking, and appointment lookup via CLI
* Save appointments asynchronously into the SQLite database

---

## 🧪 RUNNING TESTS

1. Test files are located under:
   `src/test/java/`

2. Right-click any test class (e.g., `AppointmentManagerTest.java`) and select:
   `"Run <testName>"`

3. Tests are written with **JUnit 5** and cover:

* ✅ Registration, booking, data loading logic
* ✅ Concurrency: thread-safe booking, async queue, autosave
* ✅ Data integrity: duplicate detection, malformed line handling

---

## 🗂 PROJECT STRUCTURE

```
src/
├── main/
│   └── java/
│       ├── app/       → CLI entry point (Main.java)
│       ├── model/     → Domain models (Patient, Doctor, Appointment)
│       └── service/   → Business logic, DB access, autosave, facades

├── test/
│   └── java/          → Unit + integration tests

data/
├── medtrack.db        → SQLite database file (created automatically)
├── patients.csv       → Initial data (used only for first DB load)
├── doctors.csv        → Initial data (used only for first DB load)
```

---

## 🔧 FEATURES

* ✅ **SQLite persistence** for all appointments and user records
* ✅ **Thread-safe booking** using `ReentrantLock`
* ✅ **Asynchronous appointment saving** with a background queue
* ✅ **Periodic autosave** of patient data via `ScheduledExecutorService`
* ✅ **CSV parsing** for initial DB seeding (only if DB is empty)
* ✅ **Robust error handling** for malformed inputs and SQL failures
* ✅ **Testable architecture** with single-point `FacadeService` access

---

## 📌 NOTES

* All data is now stored in **SQLite** (`data/medtrack.db`)
* CSVs are only used for the **first-time database population**
* Legacy `.ser` and `.txt` files have been archived for reference but are no longer used
* The autosave thread runs silently and shuts down cleanly on application exit
* Doctors are fixed and do not change once loaded

---

**Created by Pedro Ramirez – Summer 2025**



