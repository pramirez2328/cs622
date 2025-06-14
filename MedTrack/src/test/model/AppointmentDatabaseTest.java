package model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import service.DatabaseInitializer;
import service.DatabaseManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

public class AppointmentDatabaseTest {

    @BeforeAll
    public static void setup() {
        // ⚠️ Ensures tables are created and appointments are seeded from TXT
        DatabaseInitializer.initialize();
    }

    @Test
    public void testDatabaseHasAppointments() {
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total FROM appointments")) {

            int count = rs.getInt("total");
            assertTrue(count > 0, "Database should have at least one appointment");

        } catch (Exception e) {
            fail("Exception while checking appointments: " + e.getMessage());
        }
    }

    @Test
    public void testAppointmentDateAndTimeFormat() {
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT date, time FROM appointments")) {

            boolean found = false;

            while (rs.next()) {
                found = true;
                String date = rs.getString("date");
                String time = rs.getString("time");

                assertTrue(date.matches("\\d{4}-\\d{2}-\\d{2}"), "Date should be in YYYY-MM-DD format");
                assertTrue(time.matches("\\d{2}:\\d{2}"), "Time should be in HH:MM format");
            }

            assertTrue(found, "There should be at least one appointment");

        } catch (Exception e) {
            fail("Exception while validating appointment format: " + e.getMessage());
        }
    }
}
