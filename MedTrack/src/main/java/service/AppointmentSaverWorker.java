package service;

import model.Appointment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class AppointmentSaverWorker implements Runnable {

    // ✅ Special marker to signal shutdown (poison pill)
    private static final AppointmentSaveRequest POISON_PILL =
            new AppointmentSaveRequest(null, null, null);

    // ✅ Queue used to buffer appointment save requests
    private final BlockingQueue<AppointmentSaveRequest> queue = new LinkedBlockingQueue<>();
    private volatile boolean running = true;

    // ✅ Enqueue appointment to be saved asynchronously
    public void saveLater(Appointment appointment, String patientName, String doctorName) {
        queue.offer(new AppointmentSaveRequest(appointment, patientName, doctorName));
    }

    // ✅ Triggers shutdown by enqueueing poison pill
    public void flushAndStop() {
        running = false;
        queue.offer(POISON_PILL);
    }

    @Override
    public void run() {
        System.out.println("🟢 Appointment saver started...");
        while (true) {
            try {
                AppointmentSaveRequest request = queue.take();

                if (request == POISON_PILL) {
                    break; // ✅ Exit loop on poison pill
                }

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

                    stmt.executeUpdate(); // ✅ Save appointment to SQLite DB
                    System.out.println("\n💾 Saved to SQLite: " + a.getConfirmationCode());

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

    // ✅ Record to hold appointment + metadata
    public record AppointmentSaveRequest(Appointment appointment, String patientName, String doctorName) {
    }
}
