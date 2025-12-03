public class Manuals {

    public static void displayManual() {
        System.out.println("USER MANUAL FOR ADB CONNECTION AND USB DEBUGGING:");
        System.out.println();
        System.out.println("1. How to Connect ADB:");
        System.out.println("   - Make sure you have ADB installed on your system.");
        System.out.println("   - Connect your Android device to your computer using a USB cable.");
        System.out.println("   - Run the following command in your terminal to ensure your device is connected:");
        System.out.println("     adb devices");
        System.out.println("   - If your device appears in the list of connected devices, it means ADB is working correctly.");
        System.out.println();
        System.out.println("2. How to Enable USB Debugging on Your Android Device:");
        System.out.println("   - Open your Android device's 'Settings'.");
        System.out.println("   - Scroll down and tap 'About phone'.");
        System.out.println("   - Find 'Build number' and tap it 7 times to enable Developer Options.");
        System.out.println("   - Go back to the 'Settings' screen and tap 'Developer options'.");
        System.out.println("   - Toggle 'USB debugging' to enable it.");
        System.out.println();
        System.out.println("Once USB debugging is enabled and ADB is working, you can proceed with other options.");
    }
}
