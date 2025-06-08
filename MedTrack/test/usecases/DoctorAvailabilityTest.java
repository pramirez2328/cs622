package usecases;

import model.Doctor;
import model.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.FacadeService;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class DoctorAvailabilityTest {

    private final FacadeService facade = FacadeService.getInstance();

    @BeforeEach
    public void setUp() throws Exception {
        facade.enableTestMode(); // prevent disk writes
    }


    @Test
    public void testDoctorAvailabilityChangesAfterBooking() {
        Doctor doctor = new Doctor("D4001", "Dr. Rachel Kim", "Oncology");
        Patient patient = new Patient("P4001", "Ethan Clark", "Cigna");

        facade.registerUser(doctor);
        facade.registerUser(patient);

        String date = "2025-12-18";
        String time = "11:00";

        // Doctor should be available before booking
        assertTrue(doctor.isAvailable(date, time), "Doctor should initially be available");

        // Book appointment
        facade.bookAppointment(patient, doctor, date, time);

        // Doctor should no longer be available for the same slot
        assertFalse(doctor.isAvailable(date, time), "Doctor should no longer be available after booking");
    }
}

//🟥 Value Added:
//
//Validates dynamic availability logic on the Doctor object
//
//Captures before-and-after state transition via realistic booking
//
//Serves as a good baseline for future calendar or UI view logic