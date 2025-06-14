package usecases;

import model.Appointment;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class UpcomingAppointmentFlowTest {

    @Test // 🟥 new test
    public void testUpcomingAppointmentsFilteredAndSorted() {
        Appointment past = new Appointment("P1", "D1", LocalDate.now().minusDays(2).toString(), "10:00");
        Appointment today = new Appointment("P1", "D2", LocalDate.now().toString(), "12:00");
        Appointment future = new Appointment("P1", "D3", LocalDate.now().plusDays(5).toString(), "09:00");

        List<Appointment> all = Arrays.asList(past, today, future);

        List<Appointment> upcoming = all.stream()
                .filter(a -> {
                    LocalDate apptDate = LocalDate.parse(a.getDate());
                    return !apptDate.isBefore(LocalDate.now());
                })
                .sorted(Comparator.comparing(Appointment::getDate).thenComparing(Appointment::getTime))
                .collect(Collectors.toList());

        // ✅ Visual confirmation
        System.out.println("✅ Upcoming appointments:");
        upcoming.forEach(a -> System.out.println(
                a.getDate() + " " + a.getTime() + " → " + a.getConfirmationCode()
        ));

        // Assertions
        assertEquals(2, upcoming.size());
        assertEquals(today.getConfirmationCode(), upcoming.get(0).getConfirmationCode());
        assertEquals(future.getConfirmationCode(), upcoming.get(1).getConfirmationCode());
    }
}
