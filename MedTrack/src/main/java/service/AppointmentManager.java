package service;

import model.Appointment;
import model.Doctor;
import model.Patient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ✅ CS622 Final Release – Updated for SQLite
 * - Replaces all file-based appointment saving/loading with SQLite
 * - Supports thread-safe appointment booking with ReentrantLock
 * - Uses a background worker thread to save appointments asynchronously
 * - Appointments can be loaded from the database into a Patient’s record
 */
public class AppointmentManager {

    // ✅ Lock to prevent race conditions in concurrent bookings
    private final ReentrantLock lock = new ReentrantLock();

    // ✅ Background thread for async DB saving
    private final AppointmentSaverWorker saverWorker = new AppointmentSaverWorker();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public AppointmentManager() {
        executor.submit(saverWorker);
    }

    public boolean checkAvailability(Doctor doctor, String date, String time) {
        return doctor.isAvailable(date, time);
    }

    public Appointment bookAppointment(Patient patient, Doctor doctor, String date, String time) {
        lock.lock();
        try {
            if (patient == null) {
                throw new InvalidInputException("Patient is null", "null");
            }
            if (doctor == null) {
                throw new InvalidInputException("Doctor is null", "null");
            }
            if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
                throw new InvalidInputException("Invalid date format (expected YYYY-MM-DD)", date);
            }
            if (!time.matches("\\d{2}:\\d{2}")) {
                throw new InvalidInputException("Invalid time format (expected HH:MM)", time);
            }

            if (!doctor.isAvailable(date, time)) {
                throw new InvalidInputException("Doctor is already booked at " + date + " " + time,
                        doctor.getName() + " | " + date + " " + time);
            }

            Appointment appointment = new Appointment(
                    patient.getId(), doctor.getId(), date, time
            );

            doctor.addAppointment(appointment);
            patient.addAppointment(appointment);

            saverWorker.saveLater(appointment, patient.getName(), doctor.getName());

            return appointment;
        } finally {
            lock.unlock();
        }
    }

    public void shutdown() {
        saverWorker.stop();
        executor.shutdown();
    }

    // ✅ Loads all appointments for the given patient from the database
    public void loadAppointmentsFromDatabase(Patient patient) {
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("""
                         SELECT id, doctor_id, date, time
                         FROM appointments
                         WHERE patient_id = ?
                     """)) {

            stmt.setString(1, patient.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String doctorId = rs.getString("doctor_id");
                    String date = rs.getString("date");
                    String time = rs.getString("time");

                    Appointment a = new Appointment(patient.getId(), doctorId, date, time);
                    if (!patient.getAppointments().contains(a)) {
                        patient.addAppointment(a);
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Failed to load appointments from DB: " + e.getMessage());
        }
    }
}
