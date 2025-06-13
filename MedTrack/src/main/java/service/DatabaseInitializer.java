package service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;

public class DatabaseInitializer {
    private static final String PATIENTS_CSV = "data/patients.csv";
    private static final String DOCTORS_CSV = "data/doctors.csv";

    public static void initialize() {
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {

            // ✅ Step 1: Create tables
            stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS patients (
                            id TEXT PRIMARY KEY,
                            name TEXT NOT NULL
                        );
                    """);

            stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS doctors (
                            id TEXT PRIMARY KEY,
                            name TEXT NOT NULL,
                            specialty TEXT NOT NULL
                        );
                    """);

            stmt.executeUpdate("""
                        CREATE TABLE IF NOT EXISTS appointments (
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

            // ✅ Step 2: Seed CSVs only if tables are empty
            if (!hasAnyRows(conn, "patients")) {
                loadPatients(conn);
            } else {
                System.out.println("ℹ️ Patients already in DB, skipping CSV load.");
            }

            if (!hasAnyRows(conn, "doctors")) {
                loadDoctors(conn);
            } else {
                System.out.println("ℹ️ Doctors already in DB, skipping CSV load.");
            }

        } catch (Exception e) {
            System.err.println("❌ DB initialization error: " + e.getMessage());
        }
    }

    private static boolean hasAnyRows(Connection conn, String table) throws SQLException {
        String query = "SELECT COUNT(*) FROM " + table;
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    private static void loadPatients(Connection conn) {
        try (BufferedReader reader = new BufferedReader(new FileReader(PATIENTS_CSV))) {
            String line;
            PreparedStatement pstmt = conn.prepareStatement("INSERT OR IGNORE INTO patients (id, name) VALUES (?, ?)");
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 2) {
                    pstmt.setString(1, parts[0].trim());
                    pstmt.setString(2, parts[1].trim());
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
            PreparedStatement pstmt = conn.prepareStatement("INSERT OR IGNORE INTO doctors (id, name, specialty) VALUES (?, ?, ?)");
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
