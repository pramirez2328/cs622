package service;

import model.Doctor;
import model.Patient;
import model.User;
import model.Appointment;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class FacadeService {

    private static final FacadeService instance = new FacadeService();
    private final UserRegistry userRegistry;
    private final AppointmentManager appointmentManager;
    private static final String DATA_DIR = "data/";
    private boolean isTestMode = false;

    public void enableTestMode() {
        this.isTestMode = true;
    }

    private FacadeService() {
        this.userRegistry = new UserRegistry();
        this.appointmentManager = new AppointmentManager();
    }

    public static FacadeService getInstance() {
        return instance;
    }

    public void registerUser(User user) {
        if (user instanceof Patient patient) {
            userRegistry.registerPatient(patient);
            if (!isTestMode) {
                try (FileWriter writer = new FileWriter(DATA_DIR + "patients.csv", true)) {
                    writer.write(patient.getId() + " | " + patient.getName() + " | " + patient.getInsuranceProvider() + "\n");
                } catch (IOException e) {
                    System.err.println("❌ Could not save patient to file: " + e.getMessage());
                }
            }
        } else if (user instanceof Doctor doctor) {
            userRegistry.registerDoctor(doctor);
            if (!isTestMode) {
                try (FileWriter writer = new FileWriter(DATA_DIR + "doctors.csv", true)) {
                    writer.write(doctor.getId() + " | " + doctor.getName() + " | " + doctor.getSpecialty() + "\n");
                } catch (IOException e) {
                    System.err.println("❌ Could not save doctor to file: " + e.getMessage());
                }
            }
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

    public void loadAppointmentsFromFile(Patient patient) {
        appointmentManager.loadAppointmentsFromFile(patient);
    }

    public void loadUsersFromFiles() {
        userRegistry.loadUsersFromCsv(DATA_DIR + "patients.csv", UserRegistry.UserType.PATIENT);
        userRegistry.loadUsersFromCsv(DATA_DIR + "doctors.csv", UserRegistry.UserType.DOCTOR);
    }

    // ✅ CS622 Part 2.3 – Exposed access to UserRegistry for autosave
    public UserRegistry getUserRegistry() {
        return this.userRegistry;
    }
}
