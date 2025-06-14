package usecases;

import model.Appointment;
import model.Doctor;
import model.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.FacadeService;
import service.InvalidInputException;

import java.io.File;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

public class DoubleBookingTest {

    private final FacadeService facade = FacadeService.getInstance();

    @BeforeEach
    public void resetFiles() throws Exception {
        facade.enableTestMode();
        Files.deleteIfExists(new File("appointments.txt").toPath());
        Files.deleteIfExists(new File("patients.csv").toPath());
        Files.deleteIfExists(new File("doctors.csv").toPath());
    }

    @Test
    public void testDoctorCannotBeDoubleBooked() {
        Patient p1 = new Patient("P2001", "Carlos Rivera", "Aetna");
        Patient p2 = new Patient("P2002", "Linda Tran", "UnitedHealth");
        Doctor d1 = new Doctor("D3001", "Dr. Sophia Park", "Dermatology");

        facade.registerUser(p1);
        facade.registerUser(p2);
        facade.registerUser(d1);

        // First booking should succeed
        Appointment first = facade.bookAppointment(p1, d1, "2025-12-15", "09:00");
        assertNotNull(first);

        // Second booking with different patient but same doctor/date/time should fail
        InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> facade.bookAppointment(p2, d1, "2025-12-15", "09:00")
        );

        assertTrue(exception.getMessage().contains("already booked"));
        System.out.println("✅ Double booking correctly prevented: " + exception.getMessage());
    }
}

//🟥 Value Added:
//
//Validates real-world scheduling logic (conflict detection)
//
//Tests InvalidInputException for overlapping appointments
//
//Strengthens business rule enforcement and scheduling integrity