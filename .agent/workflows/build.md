---
description: Build the project WAR file for WildFly deployment
---

1. Open a terminal in the project root.
2. Run the Gradle build command:
   - Windows (PowerShell/CMD):
     ```powershell
     ./gradlew clean war
     ```
   - Linux/macOS:
     ```bash
     ./gradlew clean war
     ```
   *(If you don't have the Gradle wrapper `gradlew`, use `gradle clean war` if Gradle is installed globally)*

3. The compiled WAR file will be located at:
   `build/libs/WebLab3.war`

4. Copy this file to the `standalone/deployments` directory of your WildFly server.
