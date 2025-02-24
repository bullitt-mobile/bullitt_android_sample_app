# Bullitt Android SDK Integration Guide

## Overview

The Bullitt SDK Platform provides functionality for satellite device discovery, pairing, and communication. This guide demonstrates how to integrate the SDK into your Android application.

## Pre-requisites
- **Android Studio**: The latest stable version for development
- Physical Android Device: Running Android 8.0 (API level 26) or higher
- Motorola Defy Satellite Link: Or equivalent satellite-enabled device with: A provisioned satellite connection and an activated satellite service plan
- GitHub credentials from Bullitt team for SDK access

For satellite connection activation assistance, please contact the Bullitt support team at support@bullitt.com or through your provided account representative.

## Installation

Add the dependency to your app's `settings.gradle`:

```gradle
repositories {        
  // Add Bullitt SDK Repository
  maven {
    name = "GitHubPackages"
    url = uri("https://maven.pkg.github.com/bullitt-mobile/bullitt_android_sdk")
    credentials {
      username = githubUsername // from local.properties
      password = githubToken   // from local.properties
    }        
  }
}
```

Add the dependency to your app's `build.gradle`:

```gradle
dependencies {
    implementation(libs.bullitt.sdk)
    // Other dependencies
}
```

## Initialization

Initialize the SDK in your Application class:

```kotlin
@HiltAndroidApp
class YourApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        BullittSDK.initialize(
            application = this,
            logger = YourSDKLogger(), // Implement SdkLogger interface
            globalEventHandler = { /* Handle global events */ }
        )
    }
}
```

## Core Features

### 1. Configuration

Before using the SDK, configure it with user-specific settings:

```kotlin
bullittApis.setConfig(
    BullittConfig(
        userId = userId,         // Sender ID, 
        checkInNumber = number,  // Check-in phone number
    )
)
```

### 2. Device Discovery

Scan for available satellite devices:

```kotlin
// Start scanning with 10-second timeout
bullittApis.listDevices(timeoutInMillis = 10000)
    .collect { response ->
        when (response) {
            is StreamResponse.Data -> {
                // Handle discovered device
                when (response.data) {
                    is DeviceScanResult.Ble -> // Handle BLE device
                    is DeviceScanResult.D2d -> // Handle D2D device
                }
            }
            is StreamResponse.End -> // Scanning completed
            is StreamResponse.Failure -> // Handle error
        }
    }
```

### 3. Device Pairing

```kotlin
// Request pairing with discovered device
val pairingResponse = bullittApis.requestDevicePairing(deviceScanResult)
if (pairingResponse is Response.Success) {
    // Validate IMSI returned in pairingResponse.data.imsi. If valid, confirm device linking
    val linkingResponse = bullittApis.confirmDeviceLinking()
    if (linkingResponse is Response.Success) {
        // Device successfully paired
    }
}
```

### 4. Device Management

Get currently linked device:

```kotlin
when (val response = bullittApis.getLinkedDevice()) {
    is Response.Success -> {
        val device = response.data
        // Work with connected device
    }
    is Response.Failure -> {
        // Handle no linked device
    }
}
```

Remove linked device:

```kotlin
bullittApis.removeLinkedDevice()
```

### 5. Satellite

Send messages to connected device:

```kotlin
suspend fun sendMessage(content: SmpContent): Boolean {
        return linkedDevice?.let { connection ->
            val contentBundle = connection.createContentBundle(content)
            when (val response = connection.sendMessage(contentBundle).await()) {
                is Response.Success -> response.data.result
                is Response.Failure -> false
            }
        } ?: false
    }
```

### 6. Device Status Monitoring

Monitor device status through global events:

```kotlin
bullittApis.globalEvents.collect { event ->
    when (event) {
        is GlobalEvent.DeviceLinked -> // Device connected
        is GlobalEvent.DeviceUnlinked -> // Device disconnected
        is GlobalEvent.DeviceUpdate -> // Status update
        is GlobalEvent.Message -> // New message received
    }
}
```

## Permissions

Add required permissions to your AndroidManifest.xml:

```xml

<uses-permission android:name="android.permission.BLUETOOTH_SCAN" /><uses-permission
android:name="android.permission.BLUETOOTH_CONNECT" /><uses-permission
android:name="android.permission.BLUETOOTH" /><uses-permission
android:name="android.permission.BLUETOOTH_ADMIN" /><uses-permission
android:name="android.permission.ACCESS_FINE_LOCATION" /><uses-permission
android:name="android.permission.ACCESS_COARSE_LOCATION" /><uses-permission
android:name="android.permission.INTERNET" />
```

## Error Handling

The SDK uses a `Response` type for operation results:

- `Response.Success`: Operation completed successfully
- `Response.Failure`: Operation failed with an exception

For streaming operations, `StreamResponse` is used:

- `StreamResponse.Data`: New data available
- `StreamResponse.End`: Stream completed
- `StreamResponse.Failure`: Stream error occurred

## Best Practices

1. Always check if the SDK is initialized before accessing APIs
2. Handle permission requirements before scanning for devices
3. Implement proper error handling for all SDK operations
4. Monitor device status through global events for real-time updates

## Sample Implementation

See the demo app implementation for a complete example of SDK integration and usage patterns.
