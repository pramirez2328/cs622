package usecases;

import model.Appointment;
import model.Doctor;
import model.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.FacadeService;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AppointmentFlowTest {

    private final FacadeService facade = FacadeService.getInstance();

    @BeforeEach
    public void setup() throws Exception {
        facade.enableTestMode(); // prevent disk writes
    }

    @Test
    public void testFullAppointmentLifecycle() {
        Patient patient = new Patient("P8000", "Maria Gomez", "HealthPlus");
        Doctor doctor = new Doctor("D8000", "Dr. Lee", "Pediatrics");

        facade.registerUser(patient);
        facade.registerUser(doctor);

        Appointment appointment = facade.bookAppointment(patient, doctor, "2025-12-30", "14:30");

        assertNotNull(appointment);
        assertEquals("P8000", appointment.getPatientId());
        assertEquals("D8000", appointment.getDoctorId());

        // Reset state and reload from file
        patient.getAppointments().clear();
        facade.loadAppointmentsFromFile(patient);

        List<Appointment> reloaded = patient.getAppointments();
        assertEquals(1, reloaded.size());
        assertEquals("2025-12-30", reloaded.get(0).getDate());
        assertEquals("14:30", reloaded.get(0).getTime());
    }
}
