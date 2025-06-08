package service;

/**
 * ✅ CS622 Release 2 – Modified
 * This class was updated to support periodic autosaving of patient data.
 * Key changes:
 * - Introduced a 'dirty' flag to track when patients are added
 * - Added methods to expose that flag to the autosave service
 * - Simplified autosave to save patients only (doctors are admin-controlled and static)
 */

import model.Doctor;
import model.Patient;
import model.User;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class UserRegistry {
    public enum UserType {
        PATIENT, DOCTOR
    }

    // ✅ Added in Part 2.3 – Tracks when patient data has changed
    private boolean dirty = false;

    private static final String PATIENTS_FILE = "data/patients.ser";
    private static final String DOCTORS_FILE = "data/doctors.ser";

    private final UserRepository<Patient> patients = new UserRepository<>();
    private final UserRepository<Doctor> doctors = new UserRepository<>();

    public void registerPatient(Patient patient) {
        patients.addUser(patient);
        dirty = true; // ✅ Flag set when new patient is added
    }

    public void registerDoctor(Doctor doctor) {
        doctors.addUser(doctor);
        // ✅ Doctors are admin-only and do not trigger autosave
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

    public <T extends User> void loadUsersFromFile(
            String filename,
            CsvParser<T> parser,
            Consumer<T> registerFunction
    ) {
        CsvLoader<T> loader = new CsvLoader<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(filename));
            for (String line : lines) {
                try {
                    T user = parser.parse(line);
                    registerFunction.accept(user);
                } catch (IllegalArgumentException ex) {
                    System.err.println("⚠️ Skipped malformed line: " + line + " → " + ex.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("❌ Could not load users from file: " + e.getMessage());
        }
    }

    public void loadUsersFromCsv(String filename, UserType type) {
        switch (type) {
            case PATIENT -> loadUsersFromFile(
                    filename,
                    line -> {
                        String[] parts = line.split("\\|");
                        if (parts.length < 3) throw new IllegalArgumentException("Bad patient line: " + line);
                        return new Patient(parts[0].trim(), parts[1].trim(), parts[2].trim());
                    },
                    this::registerPatient
            );
            case DOCTOR -> loadUsersFromFile(
                    filename,
                    line -> {
                        String[] parts = line.split("\\|");
                        if (parts.length < 3) throw new IllegalArgumentException("Bad doctor line: " + line);
                        return new Doctor(parts[0].trim(), parts[1].trim(), parts[2].trim());
                    },
                    this::registerDoctor
            );
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
    
    // ✅ Part 2.3 – Method used by autosave service (only saves patients)
    public void saveAllUsersToBinaryFile() {
        savePatientsToBinaryFile();
    }

    // ✅ Part 2.3 – Used to determine if autosave should run
    public boolean isDirty() {
        return dirty;
    }

    public void resetDirtyFlag() {
        dirty = false;
    }
}
