package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Doctor extends User implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String specialty;
    private final List<Appointment> appointments;

    public Doctor(String id, String name, String specialty) {
        super(id, name);
        this.specialty = specialty;
        this.appointments = new ArrayList<>();
    }

    public String getSpecialty() {
        return specialty;
    }

    public boolean isAvailable(String date, String time) {
        for (Appointment a : appointments) {
            if (a.getDate().equals(date) && a.getTime().equals(time)) {
                return false;
            }
        }
        return true;
    }

    public void addAppointment(Appointment appointment) {
        appointments.add(appointment);
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    @Override
    public String getRoleInfo() {
        return "Doctor: " + name + ", Specialty: " + specialty;
    }

    @Override
    public String toString() {
        return name + " (" + specialty + ")";
    }
}
