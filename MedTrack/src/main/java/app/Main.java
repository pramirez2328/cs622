package app;

import model.*;
import service.DatabaseInitializer;
import service.FacadeService;
import service.InvalidInputException;
import service.RegistryAutosaveService;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        DatabaseInitializer.initialize();

        FacadeService facade = FacadeService.getInstance();
        Scanner scanner = new Scanner(System.in);

        // ✅ Load users from SQLite instead of file
        facade.loadUsersFromDatabase();

        // ✅ CS622 Part 2.3 – Start autosave service on boot
        RegistryAutosaveService autosaveService = new RegistryAutosaveService(facade.getUserRegistry(), 40); // every 40 seconds
        autosaveService.start();
        System.out.println("💡 Autosave is enabled. Your data will be saved automatically in the background.");

        // ✅ CS622 Part 2.3 – Ensure autosave stops on app exit
        Runtime.getRuntime().addShutdownHook(new Thread(autosaveService::stop));

        boolean running = true;
        while (running) {
            System.out.println("\n==== MEDTRACK MENU ====");
            System.out.println("0. Clean db and reload from CSV files (WARNING: this will delete all existing data!");
            System.out.println("1. Register as new patient");
            System.out.println("2. Book an appointment");
            System.out.println("3. View my appointments");
            System.out.println("4. Exit");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "0":
                    System.out.println("⚠️ This will reload patients and doctors from CSV files.");
                    System.out.print("Are you sure? (yes/no): ");
                    String confirm = scanner.nextLine().trim().toLowerCase();
                    if (confirm.equals("yes")) {
                        DatabaseInitializer.seedFromCsvForce();
                    } else {
                        System.out.println("❌ Operation cancelled.");
                    }
                    break;

                case "1":
                    System.out.print("Enter your name: ");
                    String name = scanner.nextLine().trim();
                    System.out.print("Enter your insurance provider: ");
                    String insurance = scanner.nextLine().trim();
                    String newId = String.format("P%04d", System.currentTimeMillis() % 10000);
                    Patient newPatient = new Patient(newId, name, insurance);
                    facade.registerUser(newPatient);
                    System.out.println("✅ Registered successfully! Your patient ID: " + newId);
                    break;

                case "2":
                    System.out.print("Enter your patient ID: ");
                    String pid = scanner.nextLine().trim();
                    User foundUser = facade.findUserById(pid);
                    if (!(foundUser instanceof Patient)) {
                        System.out.println("❌ Invalid patient ID.");
                        break;
                    }

                    System.out.println("\nAvailable Doctors:");
                    for (User u : facade.getAllUsers()) {
                        if (u instanceof Doctor) {
                            System.out.println("- ID: " + u.getId() + " | " + u);
                        }
                    }

                    System.out.print("Enter doctor ID to book with: ");
                    String did = scanner.nextLine().trim();
                    User dUser = facade.findUserById(did);
                    if (!(dUser instanceof Doctor)) {
                        System.out.println("❌ Invalid doctor ID.");
                        break;
                    }

                    System.out.print("Enter date (YYYY-MM-DD): ");
                    String date = scanner.nextLine().trim();
                    System.out.print("Enter time (HH:MM): ");
                    String time = scanner.nextLine().trim();

                    try {
                        Appointment appt = facade.bookAppointment((Patient) foundUser, (Doctor) dUser, date, time);
                        System.out.println("✅ Appointment booked! Confirmation code: " + appt.getConfirmationCode());
                    } catch (InvalidInputException e) {
                        System.err.println("❌ " + e.getMessage());
                        System.err.println("ℹ️  Details: " + e.getInvalidInput());
                    }
                    break;

                case "3":
                    System.out.print("Enter your patient ID: ");
                    String viewId = scanner.nextLine().trim();
                    User viewUser = facade.findUserById(viewId);

                    if (viewUser == null || !(viewUser instanceof Patient)) {
                        System.err.println("⚠️ User with ID '" + viewId + "' not found.");
                        System.err.println("❌ Invalid patient ID.");
                        break;
                    }

                    Patient viewPatient = (Patient) viewUser;
                    facade.loadAppointmentsFromFile(viewPatient); // ❗ Still using file-based appointments
                    List<Appointment> appointments = viewPatient.getAppointments();

                    if (appointments.isEmpty()) {
                        System.out.println("📭 No appointments found.");
                    } else {
                        System.out.println("Enter (A) to see All your appointments or");
                        System.out.println("Enter (U) to see only Upcoming appointments");
                        String viewChoice = scanner.nextLine().trim().toLowerCase();

                        Stream<Appointment> stream = appointments.stream();

                        if (viewChoice.equals("u")) {
                            stream = stream
                                    .filter(a -> {
                                        LocalDate today = LocalDate.now();
                                        LocalDate apptDate = LocalDate.parse(a.getDate());
                                        return apptDate.isEqual(today) || apptDate.isAfter(today);
                                    })
                                    .sorted(Comparator.comparing(Appointment::getDate)
                                            .thenComparing(Appointment::getTime));
                        }

                        List<Appointment> filtered = stream.toList();

                        if (filtered.isEmpty()) {
                            System.out.println("📭 No upcoming appointments.");
                        } else {
                            System.out.println("📅 Your Appointments:");
                            for (Appointment a : filtered) {
                                String doctorId = a.getDoctorId();
                                User docUser = facade.findUserById(doctorId);
                                String doctorName = (docUser instanceof Doctor) ? docUser.getName() : "Unknown";

                                System.out.println("- " + a.getDate() + " " + a.getTime()
                                        + " with Dr. " + doctorName + " (ID: " + doctorId + ")"
                                        + " (Code: " + a.getConfirmationCode() + ")");
                            }
                        }
                    }
                    break;

                case "4":
                    System.out.println("💾 Final autosave and shutdown in progress...");
                    System.out.println("👋 Exiting MEDTRACK. Goodbye!");
                    System.exit(0);
                    break;

                default:
                    System.out.println("❌ Invalid option. Try again.");
            }
        }

        scanner.close();
    }
}
