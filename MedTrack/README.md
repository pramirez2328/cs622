# MEDTRACK: A Medical Appointment and Records Management System

**Author**: Pedro Ramirez
**Course**: CS622

---

## 🟢 HOW TO RUN THE APPLICATION

1. Open the project in **IntelliJ IDEA** (or any Java IDE).

2. Ensure you have **JDK 17+** installed (Java 23 used during development).

3. Run the `Main` class located at:
   `src/app/Main.java`

This will:

- Load users from `.csv` or `.ser` files (whichever is available)
- Start a **background autosave** thread for patient data
- Allow registering patients and booking appointments via the CLI
- Save appointments asynchronously in the background
- Persist patient records using `.ser` binary files

---

## 🧪 RUNNING TESTS

1. Test files are located under:
   `test/`

2. Right-click any test file (e.g., `AppointmentManagerTest.java`, `AsyncAppointmentSavingTest.java`) and select:
   `"Run <testName>"`

3. Tests are built with **JUnit 5** and cover:

- 🧪 Registration, booking, serialization logic
- 🧪 Concurrency: thread-safe booking, async saving, autosave validation
- 🧪 Edge cases: double-booking, malformed CSVs, concurrent flows

---

## 🗂 PROJECT STRUCTURE

```

src/
├── app/ → CLI entry point (Main.java)
├── model/ → Domain models (User, Patient, Doctor, Appointment)
├── service/ → Core logic (AppointmentManager, FacadeService, etc.)

test/
├── model/ → Unit tests (model validation, serialization)
└── usecases/ → Functional scenarios (concurrency, autosave, booking flow)

data/
├── patients.csv → CSV fallback for patients
├── doctors.csv → CSV fallback for doctors
├── appointments.txt → Log of saved appointments (async writes)
├── patients.ser → Binary data (autosaved patient records)

```

---

## 🔧 FEATURES

- ✅ **Thread-safe appointment booking** using `ReentrantLock`
- ✅ **Asynchronous appointment saving** via background thread and queue
- ✅ **Periodic autosave** of patient data using `ScheduledExecutorService`
- ✅ **Object persistence** with `.ser` binary files across runs
- ✅ **Lambdas and streams** for filtering/sorting appointment data
- ✅ **Generic utilities** (e.g., `UserRepository<T>`, `CsvLoader<T>`)
- ✅ **Robust error handling** for malformed files and IO failures
- ✅ **Extensive test coverage** for service logic and real-world scenarios

---

## 📌 NOTES

- The system automatically decides whether to load patients from `patients.ser` or fall back to `patients.csv`.
- Doctors are **pre-registered** and used for appointment selection, but are not autosaved.
- The autosave service runs silently every few seconds and is cleanly shut down on exit.

All concurrency techniques, architectural changes, and enhancements are fully documented in the assignment write-up.

---

**Created by Pedro Ramirez – Summer 2025**

```
