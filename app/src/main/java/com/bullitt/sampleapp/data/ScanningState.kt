package com.bullitt.sampleapp.data

import com.bullitt.sdk.platform.device.DeviceScanResult

data class ScanningState(
  val devicesList: List<DeviceScanResult.Ble> = emptyList(),
  val isScanning: Boolean = false,
)
