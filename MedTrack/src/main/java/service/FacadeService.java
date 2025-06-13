package service;

import model.Doctor;
import model.Patient;
import model.User;
import model.Appointment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class FacadeService {

    private static final FacadeService instance = new FacadeService();
    private final UserRegistry userRegistry;
    private final AppointmentManager appointmentManager;
    private boolean isTestMode = false;

    private FacadeService() {
        this.userRegistry = new UserRegistry();
        this.appointmentManager = new AppointmentManager();
    }

    public static FacadeService getInstance() {
        return instance;
    }

    public void enableTestMode() {
        this.isTestMode = true;
    }

    public void registerUser(User user) {
        if (user instanceof Patient patient) {
            userRegistry.registerPatient(patient);
            if (!isTestMode) {
                insertPatientToDatabase(patient);
            }
        } else if (user instanceof Doctor doctor) {
            userRegistry.registerDoctor(doctor);
            if (!isTestMode) {
                insertDoctorToDatabase(doctor);
            }
        }
    }

    private void insertPatientToDatabase(Patient patient) {
        String sql = "INSERT OR IGNORE INTO patients (id, name) VALUES (?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, patient.getId());
            pstmt.setString(2, patient.getName());
            pstmt.executeUpdate();
            System.out.println("✅ Patient saved to database.");
        } catch (SQLException e) {
            System.err.println("❌ Failed to save patient to DB: " + e.getMessage());
        }
    }

    private void insertDoctorToDatabase(Doctor doctor) {
        String sql = "INSERT OR IGNORE INTO doctors (id, name, specialty) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, doctor.getId());
            pstmt.setString(2, doctor.getName());
            pstmt.setString(3, doctor.getSpecialty());
            pstmt.executeUpdate();
            System.out.println("✅ Doctor saved to database.");
        } catch (SQLException e) {
            System.err.println("❌ Failed to save doctor to DB: " + e.getMessage());
        }
    }

    public User findUserById(String id) {
        return userRegistry.findUserById(id).orElse(null);
    }

    public List<User> getAllUsers() {
        return userRegistry.getAllUsers();
    }

    public Appointment bookAppointment(Patient patient, Doctor doctor, String date, String time) {
        return appointmentManager.bookAppointment(patient, doctor, date, time);
    }

    public void loadAppointmentsForPatient(Patient patient) {
        appointmentManager.loadAppointmentsFromDatabase(patient);
    }

    public void loadUsersFromDatabase() {
        userRegistry.loadUsersFromDatabase();
    }

    public UserRegistry getUserRegistry() {
        return this.userRegistry;
    }
}
