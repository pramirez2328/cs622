package usecases;

/**
 * ✅ CS622 Release – New File
 * This JUnit test validates concurrent booking behavior using multiple threads.
 * It ensures that only one patient is able to book a specific appointment slot,
 * confirming that the ReentrantLock in AppointmentManager is working as intended.
 */

import model.Doctor;
import model.Patient;
import org.junit.jupiter.api.Test;
import service.AppointmentManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AppointmentManagerConcurrencyTest {

    @Test
    public void testOnlyOneAppointmentIsBookedConcurrently() throws InterruptedException {
        AppointmentManager manager = new AppointmentManager();
        Doctor doctor = new Doctor("D123", "Dr. Threads", "Concurrent Medicine");

        List<Patient> patients = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            patients.add(new Patient("P040" + i, "Patient " + i, "BlueCross" + i % 2 + (i % 3 == 0 ? " Plus" : "")));
        }

        ExecutorService executor = Executors.newFixedThreadPool(5);
        String date = "2025-06-11";
        String time = "09:00";

        List<Future<Boolean>> results = new ArrayList<>();

        for (Patient patient : patients) {
            Callable<Boolean> task = () -> {
                try {
                    manager.bookAppointment(patient, doctor, date, time);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            };
            results.add(executor.submit(task));
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        // Count how many patients actually succeeded
        long successfulBookings = results.stream().filter(future -> {
            try {
                return future.get();
            } catch (Exception e) {
                return false;
            }
        }).count();

        // Assert only one booking succeeded
        assertEquals(1, successfulBookings, "Only one appointment should be booked.");
    }
}
