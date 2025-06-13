package service;

import model.Appointment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class AppointmentSaverWorker implements Runnable {

    private final BlockingQueue<AppointmentSaveRequest> queue = new LinkedBlockingQueue<>();
    private volatile boolean running = true;

    public void saveLater(Appointment appointment, String patientName, String doctorName) {
        queue.offer(new AppointmentSaveRequest(appointment, patientName, doctorName));
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        System.out.println("🟢 Appointment saver started...");
        while (running || !queue.isEmpty()) {
            try {
                AppointmentSaveRequest request = queue.take();
                Appointment a = request.appointment();

                try (Connection conn = DatabaseManager.getConnection();
                     PreparedStatement stmt = conn.prepareStatement("""
                                 INSERT INTO appointments (id, patient_id, doctor_id, date, time)
                                 VALUES (?, ?, ?, ?, ?)
                             """)) {

                    stmt.setString(1, a.getConfirmationCode());
                    stmt.setString(2, a.getPatientId());
                    stmt.setString(3, a.getDoctorId());
                    stmt.setString(4, a.getDate());
                    stmt.setString(5, a.getTime());

                    stmt.executeUpdate();
                    System.out.println("💾 Saved to SQLite: " + a.getConfirmationCode());

                } catch (SQLException e) {
                    System.err.println("❌ DB save error: " + e.getMessage());
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("⚠️ Saver thread interrupted");
            }
        }
        System.out.println("🛑 Appointment saver stopped.");
    }

    public record AppointmentSaveRequest(Appointment appointment, String patientName, String doctorName) {
    }
}
