// ✅ This class is responsible for initializing and resetting the SQLite database.
// It creates the schema for patients, doctors, and appointments,
// and supports seeding data from CSV and TXT files for initial boot or reset.

package service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.List;

public class DatabaseInitializer {
    private static final String PATIENTS_CSV = "data/patients.csv";
    private static final String DOCTORS_CSV = "data/doctors.csv";
    private static final String APPOINTMENTS_TXT = "data/appointments.txt";

    public static void initialize() {
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {

            // Drop tables for a clean slate
            stmt.executeUpdate("DROP TABLE IF EXISTS appointments");
            stmt.executeUpdate("DROP TABLE IF EXISTS doctors");
            stmt.executeUpdate("DROP TABLE IF EXISTS patients");

            // Create tables
            stmt.executeUpdate("""
                        CREATE TABLE patients (
                            id TEXT PRIMARY KEY,
                            name TEXT NOT NULL,
                            insurance TEXT NOT NULL
                        );
                    """);

            stmt.executeUpdate("""
                        CREATE TABLE doctors (
                            id TEXT PRIMARY KEY,
                            name TEXT NOT NULL,
                            specialty TEXT NOT NULL
                        );
                    """);

            stmt.executeUpdate("""
                        CREATE TABLE appointments (
                            id TEXT PRIMARY KEY,
                            patient_id TEXT,
                            doctor_id TEXT,
                            date TEXT,
                            time TEXT,
                            FOREIGN KEY (patient_id) REFERENCES patients(id),
                            FOREIGN KEY (doctor_id) REFERENCES doctors(id)
                        );
                    """);

            System.out.println("✅ Tables ensured.");

            // Seed patients and doctors
            loadPatients(conn);
            loadDoctors(conn);

            // ✅ Always seed appointments when creating a new DB
            seedAppointmentsFromTxt(APPOINTMENTS_TXT);

        } catch (Exception e) {
            System.err.println("❌ DB initialization error: " + e.getMessage());
        }
    }

    private static void loadPatients(Connection conn) {
        try (BufferedReader reader = new BufferedReader(new FileReader(PATIENTS_CSV))) {
            String line;
            PreparedStatement pstmt = conn.prepareStatement(
                    "INSERT OR IGNORE INTO patients (id, name, insurance) VALUES (?, ?, ?)"
            );
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 3) {
                    pstmt.setString(1, parts[0].trim());
                    pstmt.setString(2, parts[1].trim());
                    pstmt.setString(3, parts[2].trim());
                    pstmt.executeUpdate();
                }
            }
            System.out.println("✅ Patients seeded from CSV.");
        } catch (Exception e) {
            System.err.println("❌ Failed to load patients from CSV: " + e.getMessage());
        }
    }

    private static void loadDoctors(Connection conn) {
        try (BufferedReader reader = new BufferedReader(new FileReader(DOCTORS_CSV))) {
            String line;
            PreparedStatement pstmt = conn.prepareStatement(
                    "INSERT OR IGNORE INTO doctors (id, name, specialty) VALUES (?, ?, ?)"
            );
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 3) {
                    pstmt.setString(1, parts[0].trim());
                    pstmt.setString(2, parts[1].trim());
                    pstmt.setString(3, parts[2].trim());
                    pstmt.executeUpdate();
                }
            }
            System.out.println("✅ Doctors seeded from CSV.");
        } catch (Exception e) {
            System.err.println("❌ Failed to load doctors from CSV: " + e.getMessage());
        }
    }

    public static void seedAppointmentsFromTxt(String filePath) {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("DELETE FROM appointments");
            }

            List<String> lines = Files.readAllLines(Paths.get(filePath));
            for (String line : lines) {
                String[] tokens = line.split("\\|");
                if (tokens.length < 5) {
                    System.err.println("⚠️ Skipping malformed line: " + line);
                    continue;
                }

                String id = tokens[0].trim();                   // Full confirmation code (APT-...)
                String patientName = tokens[1].trim();          // e.g., Maria Gomez
                String doctorName = tokens[2].trim();           // e.g., Dr. Lee
                String date = tokens[3].trim();                 // e.g., 2025-12-12
                String time = tokens[4].trim();                 // e.g., 12:00

                // Extract patientId and doctorId from confirmation code
                String[] parts = id.split("-");
                if (parts.length < 5) {
                    System.err.println("⚠️ Skipping invalid appointment code: " + id);
                    continue;
                }

                String patientId = parts[1];
                String doctorId = parts[2];

                try (PreparedStatement stmt = conn.prepareStatement("""
                            INSERT INTO appointments (id, patient_id, doctor_id, date, time)
                            VALUES (?, ?, ?, ?, ?)
                        """)) {
                    stmt.setString(1, id);
                    stmt.setString(2, patientId);
                    stmt.setString(3, doctorId);
                    stmt.setString(4, date);
                    stmt.setString(5, time);
                    stmt.executeUpdate();
                }
            }
            System.out.println("✅ Appointments seeded from TXT.");
        } catch (Exception e) {
            System.err.println("❌ Failed to seed appointments: " + e.getMessage());
        }
    }

    public static void seedFromCsvForce() {
        try (Connection conn = DatabaseManager.getConnection()) {
            loadPatients(conn);
            loadDoctors(conn);
            System.out.println("✅ Database reseeded from CSV (force mode).");
        } catch (Exception e) {
            System.err.println("❌ Failed to reseed DB: " + e.getMessage());
        }
    }
}
