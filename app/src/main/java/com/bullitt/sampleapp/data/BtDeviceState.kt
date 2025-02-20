package com.bullitt.sampleapp.data

import com.bullitt.sdk.platform.data.device.SatConnectionStatus
import com.bullitt.sdk.platform.data.smp.device.response.SatNetwork

data class BtDeviceState(
  val connectionState: DeviceConnectionState = DeviceConnectionState.Disconnected,
  val satConnectionState: SatConnectionStatus = SatConnectionStatus.DISCONNECTED,
  val satNetwork: SatNetwork = SatNetwork.Unavailable,
  val batteryLevel: Int = 0,
)
