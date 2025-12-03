# Pulse5G — Real-Time Network Intelligence & Device Health Platform (Demo)

Pulse5G is a prototype command-line tool (Java) that collects device and cellular network metrics from an Android device using **ADB (Android Debug Bridge)**, computes network details (including a Network Quality Score), and prints human-readable diagnostics. This repository contains the Java source files for the prototype used in a 5G hackathon.

> **Important privacy note:** This tool may read sensitive device identifiers (IMEI) and phone numbers when run on a real device. **Do not commit real device outputs or PII to this repository.** See **Privacy & Security** below.

---

## Contents

- `MainMenu.java` — entry point and menu UI (CLI).
- `AdbCommandExecutor.java` — executes ADB calls to fetch SIM state, phone number, IMEI, OS version, product model.
- `NetworkTypeChecker.java` — parses `dumpsys telephony.registry` to detect network type and extract signal metrics.
- `SignalInfoDetailedParser.java` — prints detailed cell identity and registration info.
- `Manuals.java` — simple user manual printed from the CLI.

---

## Quick start (demo mode)

### Prerequisites
- Java JDK 11+ installed.
- `adb` in your PATH (Android SDK platform-tools). On Linux/macOS, make sure `adb` is executable.
- USB debugging enabled on the Android device (or use an emulator that exposes required dumpsys outputs).

### Build & run (simple)
From the repo root (sources should be under `src/` or you can compile directly):

```bash
# compile (if .java files are in current directory)
javac *.java

# run
java MainMenu
```

If you keep sources under `src/main/java` follow standard `javac -d out $(find src -name "*.java")` then run `java -cp out MainMenu`.

---

## How it works (high level)

1. `MainMenu` shows a CLI that checks for ADB device presence.
2. Upon selection, the program runs ADB commands to fetch:
   - SIM state (`adb shell getprop gsm.sim.state`)
   - Telephony dumps (`adb shell dumpsys telephony.registry`)
   - Service-call outputs to attempt phone/IMEI retrieval
3. Output is parsed and formatted into human-readable network details (2G/3G/4G/5G specifics).
4. `SignalInfoDetailedParser` prints cell identity and other registration fields.

---

## Important security & correctness notes (read before running)

- **Shell pipelines:** Some code uses shell pipelines (cut/tr/tr) — those require executing via an actual shell. If running on Unix, the code must invoke `/bin/sh -c "<command>"` (see source comments). Without that, pipeline commands will fail.
- **Sensitive output:** The tool may print IMEI and phone numbers. **Mask sensitive output** before saving or sharing. Consider setting a flag to mask identifiers.
- **Device variability:** `dumpsys` outputs differ across Android versions and manufacturers; the parser is best-effort and may not work on all devices.
- **Permissions & ADB:** Ensure `adb devices` lists your device before using options that require device information.
- **Do NOT commit device outputs** (IMEI, phone numbers, call logs, screenshots with PII).

---

## Recommended safe workflow for demo / evaluation

1. Create a sanitized test device/emulator or use anonymized sample outputs (place under `docs/samples/`).
2. Add `--mask-sensitive` mode (or an environment variable) so IMEI/phone numbers are masked during demo.
3. If you must include sample outputs in repo, **sanitize** them first (e.g., replace digits with `X`).

---

## Known limitations (be honest in interviews)
- Parsing logic is brittle — depends on manufacturer & Android version.
- Service call numbers used to fetch phone/IMEI are device-specific (magic numbers used).
- No retry/backoff or advanced error handling for adb unavailability or permission prompts.
- The CLI blocks while waiting for user input and runs long-running commands synchronously.

---

## Suggested improvements (todo)
- Replace all `Runtime.exec` usages with `ProcessBuilder` and use `redirectErrorStream(true)` and proper stream gobblers.
- Add a configuration file / constants for service-call indices, and a flag to mask PII.
- Add `--help` and `--non-interactive` modes for automation.
- Add unit tests: store **sanitized** sample dumpsys outputs and write parsers against them.
- Use a logging library (SLF4J / JUL) instead of `System.out.println`.

---

## Privacy & License
- **Privacy:** Do not upload logs or outputs containing IMEIs, phone numbers, or other personal information. The repository intentionally contains no real device data.
- **License:** MIT (recommended). See `LICENSE` for terms.

---

## Contact
Arvind Pathak — arvindpathak017@gmail.com

