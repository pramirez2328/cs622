package service;

import model.Appointment;
import model.Doctor;
import model.Patient;

import java.sql.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class AppointmentManager {

    // ✅ Lock to prevent race conditions in concurrent bookings
    private final ReentrantLock lock = new ReentrantLock();

    // ✅ Background thread for async DB saving
    private final AppointmentSaverWorker saverWorker = new AppointmentSaverWorker();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public AppointmentManager() {
        executor.submit(saverWorker); // ✅ Submit the saver worker to run in background
    }

    public boolean checkAvailability(Doctor doctor, String date, String time) {
        return doctor.isAvailable(date, time);
    }

    public Appointment bookAppointment(Patient patient, Doctor doctor, String date, String time) {
        lock.lock(); // ✅ Synchronize access to prevent concurrent conflicts
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

            // ✅ Save the appointment using a background worker thread
            saverWorker.saveLater(appointment, patient.getName(), doctor.getName());

            return appointment;
        } finally {
            lock.unlock(); // ✅ Always release the lock
        }
    }

    public void shutdown() {
        saverWorker.flushAndStop(); // ✅ Send poison pill and stop saver thread
        executor.shutdown();

        try {
            // ✅ Wait for saver thread to flush queue
            if (!executor.awaitTermination(4, TimeUnit.SECONDS)) {
                System.err.println("⚠️ Saver thread did not terminate in time.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("❌ Shutdown interrupted");
        }
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
                        patient.addAppointment(a); // ✅ Add to in-memory list if not already present
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Failed to load appointments from DB: " + e.getMessage());
        }
    }
}
