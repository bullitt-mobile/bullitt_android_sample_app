package com.bullitt.sampleapp.data

import com.bullitt.sdk.platform.data.device.BullittDeviceStatus
import com.bullitt.sdk.platform.device.DeviceScanResult

sealed interface DeviceConnectionState {
  data object Disconnected : DeviceConnectionState

  data class Connecting(val device: DeviceScanResult) : DeviceConnectionState

  data class Connected(
    val device: BullittDeviceStatus.Ble,
    val batteryLevel: Int?,
    val firmwareVersion: String?,
    val serialNumber: String?,
    val imsi: String,
  ) : DeviceConnectionState
}
