package model;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AppointmentFileTest {

    @Test
    public void testAppointmentFileOutput() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("data/appointments.txt"))
                .stream()
                .filter(line -> !line.trim().isEmpty())
                .toList();

        assertFalse(lines.isEmpty(), "File should not be empty");

        String lastLine = lines.get(lines.size() - 1).trim();
        assertTrue(lastLine.startsWith("APT-"), "Appointment entry should start with confirmation code");

        System.out.println("✅ Test passed: appointment entry starts with confirmation code.");
    }

    @Test
    public void testAppointmentFileHasExpectedFormat() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("data/appointments.txt"))
                .stream()
                .filter(line -> !line.trim().isEmpty())
                .toList();
        assertFalse(lines.isEmpty(), "appointments.txt should contain at least one appointment");

        String lastLine = lines.get(lines.size() - 1).trim();
        String[] tokens = lastLine.split("\\|");

        assertEquals(5, tokens.length, "Appointment entry should contain 5 fields separated by '|'");

        String date = tokens[3].trim();
        String time = tokens[4].trim();

        assertTrue(date.matches("\\d{4}-\\d{2}-\\d{2}"), "Date should be in YYYY-MM-DD format");
        assertTrue(time.matches("\\d{2}:\\d{2}"), "Time should be in HH:MM format");

        System.out.println("✅ Format test passed: 5 fields, correct date and time formats.");
    }

}
