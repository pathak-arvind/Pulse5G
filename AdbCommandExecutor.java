import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdbCommandExecutor {

    public static void main(String[] args) {
        // Execute the adb command to get the SIM state
        String simState = executeAdbCommand("adb shell getprop gsm.sim.state");
        handleSimState(simState);

        // Fetch additional device details
        fetchOsVersion();
        fetchProductModel();
    }

    private static void handleSimState(String simState) {
        if (simState != null && !simState.isEmpty()) {
            String[] simStates = simState.split(",");
            if (simStates.length > 0) {
                System.out.println("SIM Slot 1 State: " + simStates[0]);
                if (simStates.length > 1) {
                    System.out.println("SIM Slot 2 State: " + simStates[1]);
                }
                evaluateSimSlots(simStates);
            } else {
                System.out.println("Unable to retrieve SIM states. Please check the device connection.");
            }
        } else {
            System.out.println("No response from the device. Please ensure your device is connected and adb is working.");
        }
    }

    private static void evaluateSimSlots(String[] simStates) {
        boolean sim1HasCard = "LOADED".equals(simStates[0]);
        boolean sim2HasCard = simStates.length > 1 && "LOADED".equals(simStates[1]);

        if (sim1HasCard && sim2HasCard) {
            System.out.println("Both SIM slots have SIM cards inserted.");
            fetchPhoneNumber(19); // Fetch phone number for SIM 1
            fetchPhoneNumber(20); // Fetch phone number for SIM 2
            fetchImeiNumber(1);   // Fetch IMEI number for SIM 1
            fetchImeiNumber(2);   // Fetch IMEI number for SIM 2
        } else if (sim1HasCard) {
            System.out.println("Only SIM slot 1 has a SIM card inserted.");
            fetchPhoneNumber(19); // Fetch phone number for SIM 1
            fetchImeiNumber(1);   // Fetch IMEI number for SIM 1
        } else if (sim2HasCard) {
            System.out.println("Only SIM slot 2 has a SIM card inserted.");
            fetchPhoneNumber(20); // Fetch phone number for SIM 2
            fetchImeiNumber(2);   // Fetch IMEI number for SIM 2
        } else {
            System.out.println("Neither SIM slot has a SIM card inserted.");
        }
    }

    private static void fetchPhoneNumber(int serviceCall) {
        String command = String.format("adb shell service call iphonesubinfo %d | cut -c 50-65 | tr -d '.[:space:]+'", serviceCall);
        String result = executeAdbCommand(command);
        String phoneNumber = extractPhoneNumber(result);

        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            System.out.println("Phone number: " + phoneNumber);
        } else {
            System.out.println("Phone number not available.");
        }
    }

    private static void fetchImeiNumber(int slot) {
        String command = String.format("adb shell service call iphonesubinfo %d | cut -c 50-65 | tr -d '.[:space:]+'", slot);
        String result = executeAdbCommand(command);
        String imeiNumber = extractImeiNumber(result);

        if (imeiNumber != null && !imeiNumber.isEmpty()) {
            System.out.println("IMEI number for SIM slot " + slot + ": " + imeiNumber);
        } else {
            System.out.println("IMEI number for SIM slot " + slot + " not available.");
        }
    }

    private static void fetchOsVersion() {
        String command = "adb shell getprop ro.build.version.release";
        String osVersion = executeAdbCommand(command);

        if (osVersion != null && !osVersion.isEmpty()) {
            System.out.println("OS Version: " + osVersion);
        } else {
            System.out.println("Unable to fetch OS version.");
        }
    }

    private static void fetchProductModel() {
        String command = "adb shell getprop ro.product.model";
        String productModel = executeAdbCommand(command);

        if (productModel != null && !productModel.isEmpty()) {
            System.out.println("Product Model: " + productModel);
        } else {
            System.out.println("Unable to fetch product model.");
        }
    }

    private static String extractPhoneNumber(String result) {
        if (result != null && !result.isEmpty()) {
            // Pattern to match phone number (numeric, excluding extra symbols and spaces)
            Pattern pattern = Pattern.compile("[+]?\\d{10,15}");
            Matcher matcher = pattern.matcher(result);
            if (matcher.find()) {
                return matcher.group(0); // Return the first matched phone number
            }
        }
        return ""; // Return an empty string if no phone number is found
    }

    private static String extractImeiNumber(String result) {
        if (result != null && !result.isEmpty()) {
            // Pattern to match IMEI number (15 digits)
            Pattern pattern = Pattern.compile("\\d{15}");
            Matcher matcher = pattern.matcher(result);
            if (matcher.find()) {
                return matcher.group(0); // Return the first matched IMEI number
            }
        }
        return ""; // Return an empty string if no IMEI number is found
    }

    public static String executeAdbCommand(String command) {
        StringBuilder output = new StringBuilder();
        Process process = null;

        try {
            // Execute the adb command
            process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            // Read the output of the command
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            // Wait for process to complete
            process.waitFor();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("An error occurred while executing the adb command.");
        } finally {
            if (process != null) {
                process.destroy();
            }
        }

        return output.toString().trim(); // Return the output
    }
}
