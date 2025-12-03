import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.*;

public class NetworkTypeChecker {
    private static final Pattern COMMON_PATTERN = Pattern.compile("(\\w+)\\s*=\\s*(\\[.*?\\]|\".*?\"|\\d+|true|false|\\w+)");

    public static String getSignalStrengthFromADB() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                "adb", "shell", "dumpsys", "telephony.registry"
            );
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            
            StringBuilder signalStrengthData = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("mSignalStrength")) {
                        signalStrengthData.append(cleanSignalStrengthData(line)).append("\n");
                    }
                }
            }
            process.destroy();
            return signalStrengthData.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error occurred while fetching signal strength";
        }
    }

    public static String cleanSignalStrengthData(String data) {
        if (data == null || data.isEmpty()) return "";
        return data.replaceAll("\\[|\\]", "")
                  .replaceAll("\\s+", " ")
                  .trim();
    }

    public static String extractValue(String data, String key) {
        try {
            if (data.contains("SignalStrength:")) {
                String[] sections = data.split("\\{|\\}");
                for (String section : sections) {
                    if (section.contains(key)) {
                        String[] pairs = section.split("\\s+");
                        for (String pair : pairs) {
                            if (pair.startsWith(key + "=")) {
                                String value = pair.substring(pair.indexOf("=") + 1);
                                return value.replaceAll("[^-?\\d.].*$", "");
                            }
                        }
                    }
                }
            }
            
            int index = data.indexOf(key);
            if (index != -1) {
                int start = data.indexOf("=", index) + 1;
                String value = data.substring(start).trim();
                if (value.contains(" ")) {
                    value = value.substring(0, value.indexOf(" "));
                }
                return value;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "N/A";
    }

    private static String formatSignalValue(String value) {
        if (value.equals("N/A")) return value;
        try {
            int intValue = Integer.parseInt(value);
            return String.format("%d dBm", intValue);
        } catch (NumberFormatException e) {
            return value;
        }
    }

    public static void checkNetworkType(String networkType, String signalStrength) {
        if (networkType == null || networkType.isEmpty()) {
            System.out.println("Failed to retrieve network type.");
            return;
        }

        System.out.println("Extracted Network Type: " + networkType);

        switch (networkType) {
            case "NR":
            case "NRNSA":
                System.out.println("The device is connected to 5G.");
                display5GDetails(signalStrength);
                break;

            case "LTE":
            case "LTE_CA":
            case "LTE_ADVANCED_PRO":
                System.out.println("The device is connected to 4G.");
                display4GDetails(signalStrength);
                break;

            case "UMTS":
            case "HSDPA":
            case "HSUPA":
            case "HSPA":
            case "HSPAP":
            case "EHRPD":
                System.out.println("The device is connected to 3G.");
                display3GDetails(signalStrength);
                break;

            case "EDGE":
            case "GPRS":
            case "CDMA":
            case "1xRTT":
                System.out.println("The device is connected to 2G.");
                display2GDetails(signalStrength);
                break;

            case "IWLAN":
                System.out.println("The device is connected to WiFi Calling.");
                break;

            case "UNKNOWN":
                System.out.println("Network type is unknown (but to be safe lets say it 4G)");
                System.out.println("The device is connected to 4G.");
                display4GDetails(signalStrength);
                break;

            default:
                System.out.println("Unrecognized network type: " + networkType);
                break;
        }
    }

    public static void display5GDetails(String signalData) {
        String ssRsrp = extractValue(signalData, "ssRsrp");
        String ssRsrq = extractValue(signalData, "ssRsrq");
        String ssSinr = extractValue(signalData, "ssSinr");

        System.out.println("5G Details:");
        System.out.println("  SS RSRP: " + formatSignalValue(ssRsrp));
        System.out.println("  SS RSRQ: " + formatSignalValue(ssRsrq));
        System.out.println("  SS SINR: " + formatSignalValue(ssSinr));
    }

    public static void display4GDetails(String signalData) {
        String rssi = extractValue(signalData, "CellSignalStrengthLte: rssi");
        String rsrp = extractValue(signalData, "rsrp");
        String rsrq = extractValue(signalData, "rsrq");
        String rssnr = extractValue(signalData, "rssnr");

        System.out.println("4G Details:");
        System.out.println("  RSSI: " + formatSignalValue(rssi));
        System.out.println("  RSRP: " + formatSignalValue(rsrp));
        System.out.println("  RSRQ: " + formatSignalValue(rsrq));
        System.out.println("  RSSNR: " + formatSignalValue(rssnr));
    }

    public static void display3GDetails(String signalData) {
        String rscp = extractValue(signalData, "rscp");
        String ecno = extractValue(signalData, "ecno");

        System.out.println("3G Details:");
        System.out.println("  RSCP: " + formatSignalValue(rscp));
        System.out.println("  ECNO: " + formatSignalValue(ecno));
    }

    public static void display2GDetails(String signalData) {
        String rssi = extractValue(signalData, "rssi");

        System.out.println("2G Details:");
        System.out.println("  RSSI: " + formatSignalValue(rssi));
    }

    public static void main(String[] args) {
        Process process = null;
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(
                "adb", "shell", "dumpsys", "telephony.registry"
            );
            processBuilder.redirectErrorStream(true);
            process = processBuilder.start();

            Map<String, String> extractedInfo = new LinkedHashMap<>();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    Matcher matcher = COMMON_PATTERN.matcher(line);
                    while (matcher.find()) {
                        String key = matcher.group(1);
                        String value = matcher.group(2).replaceAll("\\\\\"", "").trim();
                        extractedInfo.putIfAbsent(key, value);
                    }
                }
            }

            if (extractedInfo.containsKey("accessNetworkTechnology")) {
                String networkType = extractedInfo.get("accessNetworkTechnology");
                String signalStrength = getSignalStrengthFromADB();
                checkNetworkType(networkType, signalStrength);
            } else {
                System.out.println("Access Network Technology not found.");
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
}
