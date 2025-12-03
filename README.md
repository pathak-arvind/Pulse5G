# Pulse5G — Real-Time Network Intelligence & Device Health Platform (Demo)

Pulse5G is a Java-based command-line prototype for analyzing mobile network conditions through Android Debug Bridge (ADB). It extracts network type, signal metrics, SIM state, device details, and prints structured diagnostics for 2G/3G/4G/5G connectivity.

---

## 📌 Features

- Detects SIM state and device readiness via ADB  
- Reads network type from `dumpsys telephony.registry`  
- Extracts 5G/4G/3G/2G signal metrics (RSRP, RSRQ, RSSI, SINR, etc.)  
- Fetches device OS version and product model  
- Provides detailed cell information  
- Simple command-line menu interface  
- Works on real devices connected over USB with USB debugging enabled

---

## 📁 Project Structure

```
Pulse5G/
├── MainMenu.java
├── AdbCommandExecutor.java
├── NetworkTypeChecker.java
├── SignalInfoDetailedParser.java
└── Manuals.java
```

---

## 🚀 Getting Started

### Requirements
- Java JDK 11+
- ADB installed and added to PATH  
  (`adb devices` must show your phone)
- USB Debugging enabled on the Android device

### Compile
If Java files are in the same folder:

```bash
javac *.java
```

### Run
```bash
java MainMenu
```

---

## ⚙️ How It Works

1. The program checks whether an ADB-connected device is available.
2. Based on menu selections, it executes commands such as:
   - `adb shell getprop gsm.sim.state`
   - `adb shell dumpsys telephony.registry`
   - `adb shell getprop ro.product.model`
3. The output is parsed to:
   - Determine network type (5G/4G/3G/2G/WiFi Calling)
   - Extract important signal metrics
   - Display device information
4. The CLI prints all results in a formatted, human-readable way.

---

## 🔒 Privacy Notice

Some ADB commands may reveal **sensitive device identifiers** such as:

- Phone numbers  
- IMEI numbers  

These values are **never stored** by the program and are only displayed locally.  
Do NOT commit logs containing real device data to GitHub.

---

## ⚡ Notes

- Works best on modern Android phones (Android 10+).  
- ADB access must be allowed on the device when prompted.  
- Dumpsys output may vary slightly across manufacturers.

---

## 📜 License

This project is licensed under the MIT License.

---

## 👤 Author

**Arvind Pathak**  
Email: arvindpathak017@gmail.com

