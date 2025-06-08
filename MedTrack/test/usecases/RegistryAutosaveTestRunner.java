package usecases;

import model.Patient;
import service.FacadeService;
import service.RegistryAutosaveService;

/**
 * ✅ CS622 Part 2.3 – Test runner for periodic autosave service.
 * Demonstrates autosave by registering a patient and waiting for the save cycle.
 */
public class RegistryAutosaveTestRunner {
    public static void main(String[] args) throws InterruptedException {
        FacadeService facade = FacadeService.getInstance();

        System.out.println("📂 Loading patients...");
        facade.loadUsersFromFiles();  // loads patients and doctors (but we only care about patients for autosave)

        RegistryAutosaveService autosave = new RegistryAutosaveService(facade.getUserRegistry(), 5); // every 5 seconds
        autosave.start();
        System.out.println("🟢 Autosave started (interval = 5s)");

        // Simulate patient registration
        Patient p1 = new Patient("P9999", "Test User", "TestCare");
        facade.registerUser(p1);
        System.out.println("👤 Registered new patient: " + p1.getName());

        // Wait long enough for autosave to trigger
        Thread.sleep(8000);
        System.out.println("⏱️ Autosave should have occurred.");

        autosave.stop();
    }
}

