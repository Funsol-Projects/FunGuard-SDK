# FunGuard SDK [![](https://jitpack.io/v/Funsol-Projects/Funsol-Billing-Helper.svg)](https://jitpack.io/#Funsol-Projects/Funsol-Billing-Helper)

<div align="center">


![FunGuard SDK](https://img.shields.io/badge/FunGuard-SDK-blue?style=for-the-badge)
![Android](https://img.shields.io/badge/Android-24%2B-green?style=for-the-badge&logo=android)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9+-purple?style=for-the-badge&logo=kotlin)
![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)

**A comprehensive Android security SDK for detecting root access, tampering, debuggers, emulators, and dynamic analysis tools.**

</div>

---

## Overview

**FunGuard SDK** is a powerful Android security library designed to protect your applications from various security threats. It provides comprehensive detection capabilities for root access, app tampering, debuggers, emulators, and dynamic analysis tools like Frida.

The SDK runs all security checks asynchronously on background threads to prevent ANR (Application Not Responding) issues, ensuring smooth user experience while maintaining robust security.

---

## Features

- 🔒 **Comprehensive Security Checks**: Multiple detection mechanisms for various security threats
- 🚀 **Non-Blocking**: All checks run asynchronously to prevent ANR
- 🎨 **Customizable UI**: Fully customizable warning dialog with Jetpack Compose
- 📱 **Responsive Design**: Dialog adapts to different screen sizes
- 🔄 **Re-check Capability**: Built-in "Check Again" functionality with loading states
- 📧 **Support Integration**: Optional support email link in dialog
- 📊 **Flexible Configuration**: Enable/disable individual security checks
- 🎯 **Simple API**: Single function call with optional parameters
- 📝 **Logging Support**: Optional debug logging for troubleshooting

---

## Installation

### Add Dependency

Add the FunGuard SDK AAR to your project's `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.funsol.securitysdk:funguardsdk:1.0.1")
}
```

### Required Dependencies

The SDK uses Jetpack Compose for the warning dialog UI. If your project doesn't already use Compose, you need to add the following dependencies:

```kotlin
dependencies {
    // Compose dependencies (required if your project doesn't use Compose)
    val composeBom = platform("androidx.compose:compose-bom:2024.02.00")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.runtime:runtime")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    
    // Coroutines for async operations (required)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
}
```

**Note:** If your project already uses Jetpack Compose and Coroutines, you can skip adding these dependencies as they're already included in your project.

### Sync Gradle

Sync your project to download the dependencies.

---

## Quick Start

### Basic Usage

```kotlin
import com.funsol.securitysdk.FunGuardSDK
import com.funsol.securitysdk.SecurityListener
import com.funsol.securitysdk.models.SecurityResult
import com.funsol.securitysdk.models.SecurityIssue

class MainActivity : ComponentActivity() {

    private val securityListener = object : SecurityListener {
        override fun onSecurityCheckComplete(result: SecurityResult) {
            if (result.isSecure) {
                // Device is secure, proceed with app
                Log.d("Security", "All checks passed")
            } else {
                // Security issue detected
                // Note: If showWarningDialog = true, SDK handles the dialog automatically
                // You don't need to do anything here unless you want custom handling
                Log.w("Security", "Issue detected: ${result.issueType}")
            }
        }

        override fun onCancel(issueType: SecurityIssue) {
            // IMPORTANT: When user clicks Cancel, you MUST close the app yourself
            // SDK will NOT automatically close the app
            // Save any important data or complete any pending work before closing
            finish() // Close the activity/app
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FunGuardSDK.checkSecurity(
            context = this,
            showWarningDialog = true,
            listener = securityListener
        )
    }
}
```

### Recommended

**For continuous monitoring, it's highly recommended to call `checkSecurity()` in `onResume()` instead of `onCreate()`.** This ensures security checks run every time the activity comes to foreground.

```kotlin
override fun onResume() {
    super.onResume()

    FunGuardSDK.checkSecurity(
        context = this,
        showWarningDialog = true,
        listener = securityListener
    )
}
```

**Why `onResume()`?**
- Ensures security checks on every app resume
- Detects security issues that may occur while app is in background
- Provides continuous protection throughout app lifecycle

### Dialog Customization

The SDK provides a **default dialog** that appears when a security issue is detected. However, you can fully customize the dialog appearance and behavior using `DialogConfig`.

**Note:** If `showWarningDialog = true`, the SDK automatically handles the dialog display. You don't need to implement any custom UI in the callback - the SDK manages everything.

```kotlin
import com.funsol.securitysdk.models.DialogConfig

FunGuardSDK.checkSecurity(
    context = this,
    showWarningDialog = true,
    loggingEnabled = true,
    dialogConfig = DialogConfig(
        title = "Security Alert",                    // Custom title
        description = "A security issue detected.", // Custom description
        ctaButtonText = "Retry",                     // Custom "Check Again" button text
        cancelButtonText = "Exit",                    // Custom "Cancel" button text
        showCheckAgainButton = true,                 // Show/hide "Check Again" button
        supportEmail = "support@example.com"         // Support email (shown as clickable link)
    ),
    listener = listener
)
```

**Partial Customization:** You can customize only specific fields - others will use defaults:

```kotlin
DialogConfig(
    title = "Custom Title",
    supportEmail = "support@example.com"
    // Other fields (description, buttons) will use SDK defaults
)
```

### Selective Security Checks

```kotlin
FunGuardSDK.checkSecurity(
    context = this,
    rootCheck = true,
    tamperingCheck = true,
    fridaCheck = true,
    debuggerCheck = false, // Skip debugger check in debug builds
    emulatorCheck = false, // Allow emulator for testing
    showWarningDialog = true,
    listener = listener
)
```

---

## API Reference

### FunGuardSDK.checkSecurity()

Main function to perform security checks.

```kotlin
fun checkSecurity(
    context: Context,
    rootCheck: Boolean = true,
    tamperingCheck: Boolean = true,
    fridaCheck: Boolean = true,
    debuggerCheck: Boolean = true,
    emulatorCheck: Boolean = true,
    showWarningDialog: Boolean = false,
    loggingEnabled: Boolean = false,
    dialogConfig: DialogConfig? = null,
    listener: SecurityListener? = null
): SecurityResult
```

#### Parameters

- `context`: Application or Activity context (required)
- `rootCheck`: Enable/disable root detection (default: `true`)
- `tamperingCheck`: Enable/disable tampering detection (default: `true`)
- `fridaCheck`: Enable/disable Frida detection (default: `true`)
- `debuggerCheck`: Enable/disable debugger detection (default: `true`)
- `emulatorCheck`: Enable/disable emulator detection (default: `true`)
- `showWarningDialog`: Show warning dialog on security failure (default: `false`)
- `loggingEnabled`: Enable debug logging (default: `false`)
- `dialogConfig`: Custom dialog configuration (optional)
- `listener`: Callback for security check results (optional)

#### Return Value

Returns `SecurityResult` immediately with `isSecure = true` (default). The actual result is delivered asynchronously via the `SecurityListener.onSecurityCheckComplete()` callback.

---

### SecurityListener

Interface for receiving security check results and dialog events.

```kotlin
interface SecurityListener {
    fun onSecurityCheckComplete(result: SecurityResult)
    fun onCancel(issueType: SecurityIssue)
}
```

**Methods:**
- `onSecurityCheckComplete(result: SecurityResult)`: Called when security check completes
- `onCancel(issueType: SecurityIssue)`: Called when user clicks "Cancel" button in dialog

---

### SecurityResult

Data class representing the result of a security check.

```kotlin
data class SecurityResult(
    val isSecure: Boolean,
    val issueType: SecurityIssue? = null
)
```

**Properties:**
- `isSecure`: `true` if device is secure, `false` if security issue detected
- `issueType`: Type of security issue (null if secure)

---

### SecurityIssue

Enum representing different types of security issues.

```kotlin
enum class SecurityIssue {
    ROOT_DETECTED,
    TAMPERING_DETECTED,
    EMULATOR_DETECTED,
    FRIDA_DETECTED,
    DEBUGGER_DETECTED,
    UNKNOWN
}
```

---

### DialogConfig

Data class for customizing the warning dialog.

```kotlin
data class DialogConfig(
    val title: String? = null,
    val description: String? = null,
    val ctaButtonText: String? = null,
    val cancelButtonText: String? = null,
    val showCheckAgainButton: Boolean? = null,
    val supportEmail: String? = null
)
```

**Properties:**
- `title`: Dialog title (uses default if null)
- `description`: Dialog description (issue type automatically appended if not present)
- `ctaButtonText`: "Check Again" button text (default: "Check Again")
- `cancelButtonText`: Cancel button text (default: "Cancel")
- `showCheckAgainButton`: Show/hide "Check Again" button (default: `true`)
- `supportEmail`: Support email to display as clickable link (only shown if provided)

**Note:** All parameters are optional. If a parameter is `null`, the default value will be used.

---

## Examples

### Example 1: Basic Security Check (Recommended)

```kotlin
class MainActivity : ComponentActivity() {

    private val securityListener = object : SecurityListener {
        override fun onSecurityCheckComplete(result: SecurityResult) {
            if (result.isSecure) {
                // Device is secure - proceed normally
                // Note: If showWarningDialog = true, SDK handles dialog automatically
                // No need to implement custom UI here
            } else {
                // Security issue detected
                // SDK dialog is already shown (if showWarningDialog = true)
                // You can add additional logging or analytics here if needed
            }
        }

        override fun onCancel(issueType: SecurityIssue) {
            // IMPORTANT: User clicked Cancel - close app yourself
            // Save any important data before closing
            saveImportantData()

            // Close the app
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        // Recommended: Call in onResume for continuous monitoring
        FunGuardSDK.checkSecurity(
            context = this,
            showWarningDialog = true,
            listener = securityListener
        )
    }

    private fun saveImportantData() {
        // Save user data, preferences, etc.
    }
}
```

### Example 2: Debug vs Release Configuration

```kotlin
val isDebug = BuildConfig.DEBUG

FunGuardSDK.checkSecurity(
    context = this,
    rootCheck = true,
    tamperingCheck = true,
    fridaCheck = true,
    debuggerCheck = !isDebug, // Skip in debug mode
    emulatorCheck = !isDebug,  // Allow emulator in debug mode
    showWarningDialog = true,
    loggingEnabled = isDebug, // Enable logging only in debug
    listener = securityListener
)
```

### Example 3: Custom Dialog with Support Email

```kotlin
FunGuardSDK.checkSecurity(
    context = this,
    showWarningDialog = true,
    dialogConfig = DialogConfig(
        title = "Security Alert",
        description = "Your device has been flagged for potential security risks.",
        ctaButtonText = "Retry Check",
        cancelButtonText = "Close App",
        showCheckAgainButton = true,
        supportEmail = "security@yourapp.com"
    ),
    listener = securityListener
)
```

---

## Security Checks

The SDK performs the following security checks:

### Root Detection
Detects if the device is rooted by checking for common root binary paths, root management apps, and `su` command availability.

**Issue Type**: `SecurityIssue.ROOT_DETECTED`

### Tampering Detection
Detects app tampering by checking app signature integrity, system properties, and package manager verification.

**Issue Type**: `SecurityIssue.TAMPERING_DETECTED`

### Debugger Detection
Detects active debuggers by checking `Debug.isDebuggerConnected()`, TracerPid, JDWP status, and debug flags.

**Issue Type**: `SecurityIssue.DEBUGGER_DETECTED`

### Frida Detection
Detects Frida dynamic analysis framework by checking Frida server ports, processes, and libraries.

**Issue Type**: `SecurityIssue.FRIDA_DETECTED`

### Emulator Detection
Detects if running on an emulator by checking QEMU-related files, build properties, and emulator-specific characteristics.

**Issue Type**: `SecurityIssue.EMULATOR_DETECTED`

---

## Best Practices

### 1. Dialog Handling

**When `showWarningDialog = true`:**
- SDK automatically displays the dialog when security issue is detected
- You **don't need to implement any custom UI** in `onSecurityCheckComplete()` callback
- SDK handles all dialog interactions (Check Again, Cancel) automatically

**When `showWarningDialog = false`:**
- You must handle security failures yourself in `onSecurityCheckComplete()` callback
- Implement your own custom UI or logic for security issues

### 3. Handle Cancel Callback Properly

**⚠️ IMPORTANT:** When user clicks "Cancel" button in the dialog, the `onCancel()` callback is triggered. **You MUST close the app yourself** - the SDK will NOT automatically close the app.

```kotlin
override fun onCancel(issueType: SecurityIssue) {
    // CRITICAL: Save any important data or complete pending work
    saveUserData()
    completePendingOperations()

    // Then close the app
    finish() // For Activity
    // or System.exit(0) // For complete app termination
}
```

**Why manual close?**
- SDK doesn't know your app's data persistence requirements
- You may need to save user data, clear caches, or complete transactions
- Gives you full control over app termination process

### 4. Different Configuration for Debug and Release

```kotlin
val isDebug = BuildConfig.DEBUG

FunGuardSDK.checkSecurity(
    context = this,
    debuggerCheck = !isDebug,  // Skip in debug builds
    emulatorCheck = !isDebug,  // Allow emulator for testing
    loggingEnabled = isDebug,   // Enable logging only in debug
    showWarningDialog = true,
    listener = listener
)
```

### 5. Provide Support Email for Better UX

Always provide a support email in production builds:

```kotlin
dialogConfig = DialogConfig(
    supportEmail = "security-support@yourapp.com"
)
```

### 6. Disable Logging in Production

```kotlin
loggingEnabled = !BuildConfig.DEBUG
```

---

## Troubleshooting

### Dialog Not Showing
- Ensure `showWarningDialog = true`
- Verify that `listener` is not `null`
- Check that you're using an `Activity` context
- Ensure the activity is not finishing or destroyed

### False Positives
- Review which checks are enabled
- Consider disabling `emulatorCheck` if you need to support emulators
- Disable `debuggerCheck` in debug builds
- Use `loggingEnabled = true` to debug what's being detected

---

## License

Copyright (c) 2024 Funsol Technologies Pvt Ltd

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

---
