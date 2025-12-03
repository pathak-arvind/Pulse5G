import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;

public class MainMenu {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        boolean adbChecked = false;  // Track whether ADB check has been performed

        while (!exit) {
            // Show the main menu to the user
            System.out.println("\n**********************************");
            System.out.println("          MAIN MENU");
            System.out.println("**********************************");
            System.out.println("0. View User Manual");
            System.out.println("1. Execute ADB Command to Get SIM State");
            System.out.println("2. Check Network Type and Signal Strength");
            System.out.println("3. Display Detailed Signal Information");
            System.out.println("4. Exit");
            System.out.print("\nPlease enter your choice: ");
            
            int choice = scanner.nextInt();

            switch (choice) {
                case 0:
                    // Show the manual
                    Manuals.displayManual();
                    break;
                case 1:
                case 2:
                case 3:
                    // Before executing options 1, 2, 3, check if ADB device is connected
                    if (!adbChecked) {
                        boolean adbDeviceConnected = checkAdbDevice();
                        if (!adbDeviceConnected) {
                            System.out.println("\n*** No ADB device connected. Please connect your device and try again. ***");
                            continue;  // Skip to the next iteration of the menu if no device is connected
                        }
                        adbChecked = true;  // ADB check is done, proceed with the menu
                    }

                    // Execute the corresponding method based on the user's choice
                    if (choice == 1) {
                        System.out.println("\nExecuting ADB command to get SIM state...");
                        AdbCommandExecutor.main(new String[0]); // Pass an empty array as args
                    } else if (choice == 2) {
                        System.out.println("\nChecking network type and signal strength...");
                        NetworkTypeChecker.main(new String[0]);
                    } else if (choice == 3) {
                        System.out.println("\nDisplaying detailed signal information...");
                        SignalInfoDetailedParser.main(new String[0]);
                    }
                    break;
                case 4:
                    System.out.println("\nExiting the program. Goodbye!");
                    exit = true;
                    break;
                default:
                    System.out.println("\n*** Invalid choice. Please enter a valid option. ***");
            }
        }

        scanner.close();
    }

    // Method to check if any ADB device is connected
    public static boolean checkAdbDevice() {
        try {
            // Run the adb devices command
            Process process = Runtime.getRuntime().exec("adb devices");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            boolean deviceFound = false;

            // Read the output and check for connected devices
            while ((line = reader.readLine()) != null) {
                if (line.contains("\tdevice")) {
                    deviceFound = true;  // A device is found
                    break;
                }
            }

            // Wait for the process to complete
            process.waitFor();
            return deviceFound;  // Return whether a device is found
        } catch (Exception e) {
            e.printStackTrace();
            return false;  // If an exception occurs, assume no device is connected
        }
    }
}
