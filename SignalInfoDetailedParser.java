import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.*;

public class SignalInfoDetailedParser {
    private static final Pattern COMMON_PATTERN = Pattern.compile("(\\w+)\\s*=\\s*(\\[.*?\\]|\".*?\"|\\d+|true|false|\\w+)");

    public static void main(String[] args) {
        Process process = null;
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                "adb", "shell", "dumpsys", "telephony.registry"
            );
            processBuilder.redirectErrorStream(true);
            process = processBuilder.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                Map<String, String> extractedInfo = new LinkedHashMap<>();
                String line;
                while ((line = reader.readLine()) != null) {
                    Matcher matcher = COMMON_PATTERN.matcher(line);
                    while (matcher.find()) {
                        String key = matcher.group(1);
                        String value = matcher.group(2).replaceAll("\\\\\"", "").trim();
                        extractedInfo.putIfAbsent(key, value);
                    }
                }

                printFormattedOutput(extractedInfo);
            }

        } catch (Exception e) {
            System.err.println("Error fetching network details: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }

    private static void printFormattedOutput(Map<String, String> info) {
        System.out.println("Cell Identity");
        printLine("Physical Cell ID", info.get("mPci"));
        printLine("Tracking Area Code", info.get("mTac"));
        printLine("NR Absolute Radio Frequency Channel Number", info.get("mNrArfcn"));
        printLine("Band(s)", info.get("mBands"));
        printLine("Mobile Country Code", info.get("mMcc"));
        printLine("Mobile Network Code", info.get("mMnc"));
        printLine("NR Cell Identity", info.get("mNci"));
        printLine("Network name (long)", info.get("mAlphaLong"));
        printLine("Network name (short)", info.get("mAlphaShort"));

        System.out.println("\nVoice-Specific Info:");
        printLine("Circuit Switched Services Supported", info.get("mCssSupported"));
        printLine("Roaming indicator", info.get("mRoamingIndicator"));
        printLine("System is in PRL", info.get("mSystemIsInPrl"));
        printLine("Default roaming indicator", info.get("mDefaultRoamingIndicator"));

        System.out.println("\nNetwork Registration Information:");
        printLine("Registration Domain", info.get("domain"));
        printLine("Transport Type", info.get("transportType"));
        printLine("Registration State", info.get("registrationState"));
        printLine("Roaming Type", info.get("roamingType"));
        printLine("Access Network Technology", info.get("accessNetworkTechnology"));
        printLine("Reject Cause", info.get("rejectCause"));
        printLine("Emergency Services Enabled", info.get("emergencyEnabled"));
        printLine("Services Available", info.get("availableServices"));

        System.out.println("\nCarrier Aggregation:");
        printLine("Indicates if carrier aggregation is being used", info.get("isUsingCarrierAggregation"));
    }

    private static void printLine(String label, String value) {
        if (value == null || value.isEmpty()) {
            value = "Not available";
        }
        System.out.println(label + ": " + value);
    }
}
