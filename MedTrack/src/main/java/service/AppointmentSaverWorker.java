package service;

/**
 * ✅ CS622 Release 2 – New File
 * This background worker class handles appointment saving asynchronously using a thread-safe queue.
 * Appointments are added via `saveLater(...)`, and the worker runs in a separate thread,
 * taking items from the queue and appending them to `appointments.txt`.
 * This improves performance by decoupling booking from disk I/O.
 */

import model.Appointment;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class AppointmentSaverWorker implements Runnable {
    private static final String FILE_PATH = "data/appointments.txt";

    private final BlockingQueue<AppointmentSaveRequest> queue = new LinkedBlockingQueue<>();
    private volatile boolean running = true;

    // ✅ Adds a new save request to the queue to be handled by the background thread
    public void saveLater(Appointment appointment, String patientName, String doctorName) {
        queue.offer(new AppointmentSaveRequest(appointment, patientName, doctorName));
    }

    // ✅ Gracefully stops the worker when the application shuts down
    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        System.out.println("🟢 Appointment saver started...");
        while (running || !queue.isEmpty()) {
            try {
                AppointmentSaveRequest request = queue.take(); // Waits if queue is empty
                Appointment a = request.appointment();

                try (FileWriter writer = new FileWriter(FILE_PATH, true)) {
                    writer.write(a.getConfirmationCode() + " | " +
                            request.patientName() + " | " +
                            request.doctorName() + " | " +
                            a.getDate() + " | " +
                            a.getTime() + "\n");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("⚠️ Saver thread interrupted");
            } catch (IOException e) {
                System.err.println("❌ Error saving appointment: " + e.getMessage());
            }
        }
        System.out.println("🛑 Appointment saver stopped.");
    }

    // ✅ Carries appointment + metadata for clean writing
    public record AppointmentSaveRequest(Appointment appointment, String patientName, String doctorName) {
    }
}
