package model;

import org.junit.jupiter.api.Test;
import service.AppointmentManager;
import service.InvalidInputException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AppointmentManagerTest {

    @Test
    public void testBookAppointmentSuccessfully() {
        Patient patient = new Patient("P1001", "John Doe", "BlueCross");
        Doctor doctor = new Doctor("D2002", "Dr. Daniel Lee", "Pediatrics");
        AppointmentManager manager = new AppointmentManager();

        // Book the appointment
        Appointment apt = manager.bookAppointment(patient, doctor, "2025-05-10", "14:00");

        assertNotNull(apt, "Appointment should be booked");
        assertEquals("P1001", apt.getPatientId());
        assertEquals("D2002", apt.getDoctorId());
    }

    @Test
    public void testBookFailsIfDoctorUnavailable() {
        Patient patient = new Patient("P1002", "Jane Smith", "MediCare");

        // Create doctor and simulate already booked time
        Doctor doctor = new Doctor("D2003", "Busy Doctor", "Oncology");
        AppointmentManager manager = new AppointmentManager();

        // First appointment (fills the time slot)
        manager.bookAppointment(patient, doctor, "2025-06-01", "10:00");

        // Second appointment at same time — should fail
        InvalidInputException exception = assertThrows(
                InvalidInputException.class,
                () -> manager.bookAppointment(patient, doctor, "2025-06-01", "10:00")
        );

        assertTrue(exception.getMessage().contains("already booked"));
    }

    @Test
    public void testSaveAndLoadAppointments() {
        AppointmentManager manager = new AppointmentManager();

        // Create sample data
        Appointment a1 = new Appointment("P100", "D200", "2025-12-01", "10:00");
        Appointment a2 = new Appointment("P101", "D201", "2025-12-02", "14:00");
        List<Appointment> originalList = new ArrayList<>();
        originalList.add(a1);
        originalList.add(a2);

        // Save to binary file
        manager.saveAppointmentsToBinaryFile(originalList);

        // Load from binary file
        List<Appointment> loadedList = manager.loadAppointmentsFromBinaryFile();

        // Assertions
        assertNotNull(loadedList);
        assertEquals(2, loadedList.size());
        assertTrue(loadedList.contains(a1));
        assertTrue(loadedList.contains(a2));

        // Cleanup (optional)
        File file = new File("data/appointments.ser");
        if (file.exists()) file.delete();
    }
}
