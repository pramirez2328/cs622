package model;

import org.junit.jupiter.api.Test;
import service.UserRegistry;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserRegistryTest {

    @Test
    public void testRegisterAndFindUser() {
        UserRegistry registry = new UserRegistry();
        Patient p = new Patient("P1001", "John Doe", "BlueCross");

        registry.registerPatient(p);
        User result = registry.findUserById("P1001").orElse(null);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertInstanceOf(Patient.class, result);
    }

    @Test
    public void testFindUserNotFound() {
        UserRegistry registry = new UserRegistry();
        assertNull(registry.findUserById("UNKNOWN").orElse(null));
    }

    @Test
    public void testGenericPatientLoad() {
        UserRegistry registry = new UserRegistry();

        registry.loadUsersFromCsv("data/patients.csv", UserRegistry.UserType.PATIENT);

        List<User> all = registry.getAllUsers();
        assertTrue(all.size() >= 1);
        assertEquals("P8000", all.get(0).getId());
    }

    @Test
    public void testSaveAndLoadUsersBinaryFile() {
        UserRegistry registry = new UserRegistry();

        // Create and register mock users
        Patient p1 = new Patient("P123", "Alice Blue", "HealthNet");
        Doctor d1 = new Doctor("D456", "Dr. Smith", "Cardiology");

        registry.registerPatient(p1);
        registry.registerDoctor(d1);

        registry.savePatientsToBinaryFile();
        registry.saveDoctorsToBinaryFile();

        UserRegistry loadedRegistry = new UserRegistry();
        loadedRegistry.loadPatientsFromBinaryFile();
        loadedRegistry.loadDoctorsFromBinaryFile();
        
        User loadedPatient = loadedRegistry.findUserById("P123").orElse(null);
        User loadedDoctor = loadedRegistry.findUserById("D456").orElse(null);

        assertNotNull(loadedPatient);
        assertNotNull(loadedDoctor);

        assertEquals("Alice Blue", loadedPatient.getName());
        assertEquals("Dr. Smith", loadedDoctor.getName());

        assertInstanceOf(Patient.class, loadedPatient);
        assertInstanceOf(Doctor.class, loadedDoctor);
    }
}
