package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Patient extends User implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String insuranceProvider;
    private final List<Appointment> appointments;

    public Patient(String id, String name, String insuranceProvider) {
        super(id, name);
        this.insuranceProvider = insuranceProvider;
        this.appointments = new ArrayList<>();
    }

    public String getInsuranceProvider() {
        return insuranceProvider;
    }

    public void addAppointment(Appointment appointment) {
        appointments.add(appointment);
    }

    public List<Appointment> getAppointments() {
        return appointments;
    }

    @Override
    public String getRoleInfo() {
        return "Patient: " + name + " (ID: " + id + ")";
    }
}
