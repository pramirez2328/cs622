package usecases;

/**
 * ✅ CS622 Release 2 – New File
 * This JUnit test verifies that appointments are saved asynchronously by the background worker.
 * It clears the appointments file before each run, books an appointment, waits briefly,
 * and checks that the correct data was written to the file.
 */

import model.Doctor;
import model.Patient;
import org.junit.jupiter.api.*;
import service.AppointmentManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AsyncAppointmentSavingTest {

    private static final String FILE_PATH = "data/appointments.txt";

    @BeforeEach
    public void clearFile() throws IOException {
        // ✅ Ensures a clean file before each test run
        Files.write(Path.of(FILE_PATH), new byte[0]);
    }

    @Test
    public void testAppointmentIsSavedAsynchronously() throws InterruptedException, IOException {
        AppointmentManager manager = new AppointmentManager();

        Doctor doctor = new Doctor("D1234", "Dr. Async", "Dermatology");
        Patient patient = new Patient("P9999", "Async Tester", "TestCare");

        // ✅ Booking triggers background save
        manager.bookAppointment(patient, doctor, "2025-12-01", "08:30");

        // ✅ Allow time for background worker to write
        Thread.sleep(1000);

        manager.shutdown(); // ✅ Ensure thread is stopped after save

        List<String> lines = Files.readAllLines(Path.of(FILE_PATH));
        boolean found = lines.stream().anyMatch(line ->
                line.contains("P9999") &&
                        line.contains("Dr. Async") &&
                        line.contains("2025-12-01") &&
                        line.contains("08:30")
        );

        if (found) {
            System.out.println("✅ Appointment was successfully saved by the background thread.");
        } else {
            System.err.println("❌ Appointment was NOT saved by the background thread.");
        }

        // ✅ Verifies that async save logic worked as intended
        assertTrue(found, "Appointment should be saved to file by the background worker.");
    }
}
