package service;

import model.Doctor;
import model.Patient;
import model.User;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class UserRegistry {
    public enum UserType {
        PATIENT, DOCTOR
    }

    // ✅ Added: Tracks whether patient data has changed, for autosave purposes
    private boolean dirty = false;

    private static final String PATIENTS_FILE = "data/patients.ser";
    private static final String DOCTORS_FILE = "data/doctors.ser";

    private final UserRepository<Patient> patients = new UserRepository<>();
    private final UserRepository<Doctor> doctors = new UserRepository<>();

    public void registerPatient(Patient patient) {
        patients.addUser(patient);
        dirty = true;  // ✅ Mark dirty when a new patient is added
    }

    public void registerDoctor(Doctor doctor) {
        doctors.addUser(doctor);
    }

    public Optional<User> findUserById(String id) {
        return Stream.concat(patients.getAllUsers().stream(), doctors.getAllUsers().stream())
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    public List<User> getAllUsers() {
        List<User> allUsers = new ArrayList<>();
        allUsers.addAll(patients.getAllUsers());
        allUsers.addAll(doctors.getAllUsers());
        return allUsers;
    }

    // ✅ Added: Load all users from SQLite database
    public void loadUsersFromDatabase() {
        loadPatientsFromDatabase(); // ✅ Added: pulls patients from SQLite
        loadDoctorsFromDatabase();  // ✅ Added: pulls doctors from SQLite
    }

    // ✅ Added: Loads patients from 'patients' SQLite table
    private void loadPatientsFromDatabase() {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT id, name, insurance FROM patients");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String insurance = rs.getString("insurance");
                registerPatient(new Patient(id, name, insurance)); // ✅ auto-sets dirty flag
            }

            System.out.println("✅ Loaded patients from SQLite");

        } catch (SQLException e) {
            System.err.println("❌ Failed to load patients from DB: " + e.getMessage());
        }
    }

    // ✅ Added: Loads doctors from 'doctors' SQLite table
    private void loadDoctorsFromDatabase() {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT id, name, specialty FROM doctors");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String specialty = rs.getString("specialty");
                registerDoctor(new Doctor(id, name, specialty));
            }

            System.out.println("✅ Loaded doctors from SQLite");

        } catch (SQLException e) {
            System.err.println("❌ Failed to load doctors from DB: " + e.getMessage());
        }
    }

    // 🔁 Legacy file-loading logic (CSV) – unchanged
    public <T extends User> void loadUsersFromFile(
            String filename,
            CsvParser<T> parser,
            Consumer<T> registerFunction
    ) {
        CsvLoader<T> loader = new CsvLoader<>();
        try {
            List<T> loaded = loader.load(filename, parser);
            loaded.forEach(registerFunction);
        } catch (IOException e) {
            System.err.println("❌ Could not load users from file: " + e.getMessage());
        } catch (IllegalArgumentException ex) {
            System.err.println("⚠️ Skipped malformed line: " + ex.getMessage());
        }
    }

    public void savePatientsToBinaryFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PATIENTS_FILE))) {
            oos.writeObject(patients.getAllUsers());
        } catch (IOException e) {
            System.err.println("❌ Failed to save patients: " + e.getMessage());
        }
    }

    public void saveDoctorsToBinaryFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DOCTORS_FILE))) {
            oos.writeObject(doctors.getAllUsers());
        } catch (IOException e) {
            System.err.println("❌ Failed to save doctors: " + e.getMessage());
        }
    }

    public void loadPatientsFromBinaryFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(PATIENTS_FILE))) {
            Object obj = ois.readObject();
            if (obj instanceof List<?> list) {
                for (Object o : list) {
                    if (o instanceof Patient p) {
                        registerPatient(p);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Failed to load patients: " + e.getMessage());
        }
    }

    public void loadDoctorsFromBinaryFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DOCTORS_FILE))) {
            Object obj = ois.readObject();
            if (obj instanceof List<?> list) {
                for (Object o : list) {
                    if (o instanceof Doctor d) {
                        registerDoctor(d);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Failed to load doctors: " + e.getMessage());
        }
    }

    public void saveAllUsersToBinaryFile() {
        savePatientsToBinaryFile();
    }

    // ✅ Added: Used by autosave service to detect when persistence is needed
    public boolean isDirty() {
        return dirty;
    }

    // ✅ Added: Called after a successful save to reset the flag
    public void resetDirtyFlag() {
        dirty = false;
    }
}
