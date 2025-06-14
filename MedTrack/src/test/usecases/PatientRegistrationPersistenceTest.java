package usecases;

import model.Patient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.DatabaseInitializer;
import service.FacadeService;
import service.UserRegistry;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ✅ CS622 Final Release – Test for ensuring newly registered patients
 * are persisted into SQLite and loaded back correctly
 */
public class PatientRegistrationPersistenceTest {

    private final FacadeService facade = FacadeService.getInstance();

    @BeforeEach
    public void setup() {
        DatabaseInitializer.initialize(); // Clean DB and preload from CSV/TXT
        facade.loadUsersFromDatabase();   // Load initial data into registry
    }

    @AfterEach
    public void teardown() {
        // Optional: can truncate patients table here if needed
    }

    @Test
    public void testPatientSavedToDatabase() throws SQLException {
        String testId = "PTEST";
        String testName = "Test User";
        String insurance = "TestInsurance";

        Patient newPatient = new Patient(testId, testName, insurance);
        facade.registerUser(newPatient);

        // Now query the DB directly to check
        try (Connection conn = service.DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT name, insurance FROM patients WHERE id = ?")) {

            stmt.setString(1, testId);

            try (ResultSet rs = stmt.executeQuery()) {
                assertTrue(rs.next(), "Patient should be inserted into DB");
                assertEquals(testName, rs.getString("name"));
                assertEquals(insurance, rs.getString("insurance"));
            }
        }
    }

    @Test
    public void testPatientReloadedAfterRestart() {
        String testId = "PLOAD";
        String testName = "Reload Me";
        String insurance = "ReloadCare";

        // Register and persist patient
        facade.registerUser(new Patient(testId, testName, insurance));

        // Simulate application restart (reload from DB)
        UserRegistry newRegistry = new UserRegistry();
        newRegistry.loadUsersFromDatabase();

        assertTrue(
                newRegistry.findUserById(testId).isPresent(),
                "Patient should be reloaded from SQLite"
        );
    }
}
