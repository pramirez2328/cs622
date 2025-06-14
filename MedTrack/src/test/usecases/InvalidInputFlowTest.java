package usecases;

import model.Doctor;
import model.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.FacadeService;
import service.InvalidInputException;

import java.io.File;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

public class InvalidInputFlowTest {

    private final FacadeService facade = FacadeService.getInstance();

    @BeforeEach
    public void cleanFiles() throws Exception {
        facade.enableTestMode();
        Files.deleteIfExists(new File("appointments.txt").toPath());
        Files.deleteIfExists(new File("patients.csv").toPath());
        Files.deleteIfExists(new File("doctors.csv").toPath());
    }

    @Test
    public void testNullPatientThrowsException() {
        Doctor doctor = new Doctor("D901", "Dr. Strange", "Neurology");
        facade.registerUser(doctor);

        InvalidInputException e = assertThrows(
                InvalidInputException.class,
                () -> facade.bookAppointment(null, doctor, "2025-12-20", "08:00")
        );
        assertEquals("Patient is null", e.getMessage());
    }

    @Test
    public void testMalformedDateThrowsException() {
        Patient patient = new Patient("P901", "Bruce Wayne", "WayneHealth");
        Doctor doctor = new Doctor("D902", "Dr. Thomas Wayne", "Pediatrics");

        facade.registerUser(patient);
        facade.registerUser(doctor);

        InvalidInputException e = assertThrows(
                InvalidInputException.class,
                () -> facade.bookAppointment(patient, doctor, "12/25/2025", "08:00")
        );
        assertTrue(e.getMessage().contains("Invalid date format"));
    }

    @Test
    public void testMalformedTimeThrowsException() {
        Patient patient = new Patient("P902", "Clark Kent", "DailyHealth");
        Doctor doctor = new Doctor("D903", "Dr. Jor-El", "Orthopedics");

        facade.registerUser(patient);
        facade.registerUser(doctor);

        InvalidInputException e = assertThrows(
                InvalidInputException.class,
                () -> facade.bookAppointment(patient, doctor, "2025-12-25", "800")
        );
        assertTrue(e.getMessage().contains("Invalid time format"));
    }
}

//🟥 Value Added:
//
//Tests validation logic for null, malformed date/time inputs
//
//Confirms system fails gracefully and predictably
//
//Strengthens your app’s robustness and input safety