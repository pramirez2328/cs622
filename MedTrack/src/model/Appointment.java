package model;

import java.io.Serializable;
import java.util.Objects;

public class Appointment implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String patientId;
    private final String doctorId;
    private final String date;
    private final String time;
    private final String confirmationCode;

    public Appointment(String patientId, String doctorId, String date, String time) {
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.date = date;
        this.time = time;
        this.confirmationCode = generateConfirmationCode();
    }

    private String generateConfirmationCode() {
        return "APT-" + patientId + "-" + doctorId + "-" + date.replace("-", "")
                + "-" + time.replace(":", "");
    }

    public String getConfirmationCode() {
        return confirmationCode;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Appointment)) return false;
        Appointment that = (Appointment) o;
        return patientId.equals(that.patientId) &&
                doctorId.equals(that.doctorId) &&
                date.equals(that.date) &&
                time.equals(that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(patientId, doctorId, date, time);
    }
}

