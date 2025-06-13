package service;

import model.Appointment;
import model.Doctor;
import model.Patient;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ✅ CS622 Release – Modified
 * This class was updated to:
 * - Support concurrent appointment booking using ReentrantLock (Part 2.1)
 * - Save appointments asynchronously using a background thread and queue (Part 2.2)
 */
public class AppointmentManager {
    private static final String DATA_DIR = "data/";
    private static final String APPOINTMENT_BINARY_FILE = DATA_DIR + "appointments.ser";

    // ✅ Part 2.1 – Lock to prevent race conditions in concurrent bookings
    private final ReentrantLock lock = new ReentrantLock();

    // ✅ Part 2.2 – Background thread and queue-based saver for async file writing
    private final AppointmentSaverWorker saverWorker = new AppointmentSaverWorker();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public AppointmentManager() {
        // ✅ Part 2.2 – Start the background saver thread
        executor.submit(saverWorker);
    }

    public boolean checkAvailability(Doctor doctor, String date, String time) {
        return doctor.isAvailable(date, time);
    }

    public Appointment bookAppointment(Patient patient, Doctor doctor, String date, String time) {
        // ✅ Part 2.1 – Lock acquired to ensure only one thread can book at a time
        lock.lock();
        try {
            if (patient == null) {
                throw new InvalidInputException("Patient is null", "null");
            }
            if (doctor == null) {
                throw new InvalidInputException("Doctor is null", "null");
            }
            if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
                throw new InvalidInputException("Invalid date format (expected YYYY-MM-DD)", date);
            }
            if (!time.matches("\\d{2}:\\d{2}")) {
                throw new InvalidInputException("Invalid time format (expected HH:MM)", time);
            }

            if (!doctor.isAvailable(date, time)) {
                throw new InvalidInputException("Doctor is already booked at " + date + " " + time,
                        doctor.getName() + " | " + date + " " + time);
            }

            Appointment appointment = new Appointment(
                    patient.getId(), doctor.getId(), date, time
            );

            doctor.addAppointment(appointment);
            patient.addAppointment(appointment);

            // ✅ Part 2.2 – Save appointment asynchronously through a background worker
            saverWorker.saveLater(appointment, patient.getName(), doctor.getName());

            return appointment;
        } finally {
            lock.unlock(); // ✅ Part 2.1 – Release lock
        }
    }

    public void shutdown() {
        // ✅ Part 2.2 – Stop background worker and shutdown executor service
        saverWorker.stop();
        executor.shutdown();
    }

    public void loadAppointmentsFromFile(Patient patient) {
        boolean warned = false;
        try {
            List<String> lines = Files.readAllLines(Paths.get(DATA_DIR + "appointments.txt"));
            for (String line : lines) {
                String[] tokens = line.split("\\|");
                if (tokens.length < 5) {
                    if (!warned) {
                        System.err.println("⚠️ Skipped malformed line:");
                        warned = true;
                    }
                    continue;
                }

                String fullCode = tokens[0].trim();
                String[] parts = fullCode.split("-");
                if (parts.length < 5) continue;

                String patientId = parts[1].trim();
                String doctorId = parts[2].trim();
                String dateRaw = parts[3].trim();
                String timeRaw = parts[4].trim();

                String formattedDate = dateRaw.substring(0, 4) + "-" + dateRaw.substring(4, 6) + "-" + dateRaw.substring(6);
                String formattedTime = timeRaw.substring(0, 2) + ":" + timeRaw.substring(2);

                if (patient.getId().equals(patientId)) {
                    Appointment a = new Appointment(patientId, doctorId, formattedDate, formattedTime);
                    if (!patient.getAppointments().contains(a)) {
                        patient.addAppointment(a);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading appointments: " + e.getMessage());
        }
    }

    public void saveAppointmentsToBinaryFile(List<Appointment> appointments) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(APPOINTMENT_BINARY_FILE))) {
            oos.writeObject(appointments);
            System.out.println("✅ Appointments saved to binary file.");
        } catch (IOException e) {
            System.err.println("❌ Failed to save appointments: " + e.getMessage());
        }
    }

    public List<Appointment> loadAppointmentsFromBinaryFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(APPOINTMENT_BINARY_FILE))) {
            Object obj = ois.readObject();
            if (obj instanceof List<?>) {
                return (List<Appointment>) obj;
            } else {
                System.err.println("❌ File does not contain a valid List<Appointment>");
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ Failed to load appointments: " + e.getMessage());
        }
        return new ArrayList<>();
    }
}
