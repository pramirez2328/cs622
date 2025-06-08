package app;

/**
 * ✅ CS622 Release – New File
 * This demo simulates 10 patients attempting to book the same appointment slot concurrently.
 * It showcases the real-world effect of the ReentrantLock added in AppointmentManager
 * by printing which patients succeeded and which failed. This complements the test class
 * by offering visual proof of thread-safe booking behavior in action.
 */

import model.Doctor;
import model.Patient;
import service.AppointmentManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ConcurrentBookingDemo {

    public static void main(String[] args) {
        AppointmentManager manager = new AppointmentManager();

        // Fallback shutdown hook in case app is interrupted (e.g. Ctrl+C)
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            manager.shutdown();
            System.out.println("✅ [Shutdown Hook] AppointmentManager stopped.");
        }));

        Doctor sharedDoctor = new Doctor("D0001", "Dr. Concur", "General Medicine");

        List<Patient> patients = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            patients.add(new Patient(
                    "P000" + i,
                    "Patient " + i,
                    "BlueCross" + (i % 2) + (i % 3 == 0 ? " Plus" : "")
            ));
        }

        ExecutorService executor = Executors.newFixedThreadPool(5);
        String date = "2025-06-10";
        String time = "10:00";

        List<Future<String>> results = new ArrayList<>();

        for (Patient patient : patients) {
            Callable<String> task = () -> {
                try {
                    manager.bookAppointment(patient, sharedDoctor, date, time);
                    return "✅ " + patient.getName() + " successfully booked.";
                } catch (Exception e) {
                    return "❌ " + patient.getName() + " failed: " + e.getMessage();
                }
            };
            results.add(executor.submit(task));
        }

        executor.shutdown();

        System.out.println("\n--- Booking Results ---");
        for (Future<String> result : results) {
            try {
                System.out.println(result.get());
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("Error retrieving result: " + e.getMessage());
            }
        }

        // Allow time for background thread to flush the queue
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }

        // Explicit manual shutdown for demo visibility
        manager.shutdown();
        System.out.println("✅ Manager shutdown completed.");
    }
}
