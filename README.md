# TeleportFix

A client-side Fabric mod for Minecraft Java 1.21.11. The first release detects server position/teleport packets and measures client tick stalls for eight seconds after each teleport.

## Why this first version is diagnostic

A client mod cannot remove server TPS lag, network latency, or slow chunk delivery. Blindly changing chunk loading or render distance can make the freeze worse. TeleportFix 0.1.0 therefore measures the client-side stall safely and reports the worst tick duration in chat and the log. The measurements help identify whether a later optimization should target client tick work, rendering, memory/GC, or server/network delay.

## Requirements

- Java 21
- Minecraft 1.21.11
- Fabric Loader 0.18.1 or newer
- Fabric API for 1.21.11

## Build

The project uses GitHub Actions to build without requiring a Gradle Wrapper JAR. Push the project to GitHub, open the **Actions** tab, select **Build TeleportFix**, and download the `TeleportFix-1.21.11` artifact after the workflow succeeds.

If Gradle 9.6.1 is installed locally, run:

```powershell
gradle build --no-daemon
```

The mod JAR will be in `build\libs\`.

## Install

Copy `build/libs/teleportfix-0.1.0.jar` into your Minecraft instance's `mods` folder. Fabric API must also be installed.

## GitHub Actions

The included workflow builds the mod automatically when pushed to GitHub. Download the JAR from the workflow's Artifacts section.

## License

MIT
