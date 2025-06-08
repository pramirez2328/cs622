package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.FacadeService;
import service.InvalidInputException;

import static org.junit.jupiter.api.Assertions.*;

public class FacadeServiceTest {

    private FacadeService facade;
    private Patient patient;
    private Doctor doctor;


    @BeforeEach
    public void setUp() {
        facade = FacadeService.getInstance();
        facade.enableTestMode(); // 👈 prevents file I/O

        patient = new Patient("P3001", "Alice Baker", "UnitedHealth");
        doctor = new Doctor("D4001", "Dr. Raymond Cruz", "Neurology");

        facade.registerUser(patient);
        facade.registerUser(doctor);
        facade.loadAppointmentsFromFile(patient);
    }


    @Test
    public void testSuccessfulAppointmentBooking() {
        String date = "2025-06-01";
        String time = "10:30";

        Appointment appointment = facade.bookAppointment(patient, doctor, date, time);

        assertNotNull(appointment);
        assertEquals("P3001", appointment.getPatientId());
        assertEquals("D4001", appointment.getDoctorId());
        assertEquals(date, appointment.getDate());
        assertEquals(time, appointment.getTime());
        assertTrue(appointment.getConfirmationCode().startsWith("APT-"));
    }

    @Test
    public void testBookingFailsWithInvalidDate() {
        String invalidDate = "06-01-2025";
        String time = "10:30";

        InvalidInputException thrown = assertThrows(
                InvalidInputException.class,
                () -> facade.bookAppointment(patient, doctor, invalidDate, time)
        );

        assertTrue(thrown.getMessage().contains("Invalid date format"));
        assertEquals(invalidDate, thrown.getInvalidInput());
    }

    @Test
    public void testBookingFailsWithUnavailableTime() {
        // Book an initial appointment to make time unavailable
        facade.bookAppointment(patient, doctor, "2025-06-01", "11:00");

        // Try booking again at same time
        InvalidInputException thrown = assertThrows(
                InvalidInputException.class,
                () -> facade.bookAppointment(patient, doctor, "2025-06-01", "11:00")
        );

        assertTrue(thrown.getMessage().contains("already booked"));
    }

}
