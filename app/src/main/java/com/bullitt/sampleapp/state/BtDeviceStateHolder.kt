package com.bullitt.sampleapp.state

import com.bullitt.sampleapp.data.BtDeviceState
import com.bullitt.sampleapp.data.DeviceConnectionState
import com.bullitt.sdk.platform.data.device.SatConnectionStatus
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Making a singleton to easily access it by injecting it and keep state coherent across various
 * classes
 */
@Singleton
class BtDeviceStateHolder {
  private val _state = MutableStateFlow<BtDeviceState>(BtDeviceState())
  val state: StateFlow<BtDeviceState> = _state.asStateFlow()

  fun updateState(update: (BtDeviceState) -> BtDeviceState) {
    val currentState = _state.value
    _state.value = update(currentState)
  }

  fun isSatelliteConnected() =
    state.value.satConnectionState == SatConnectionStatus.CONNECTED ||
      state.value.satConnectionState == SatConnectionStatus.SEND_RECEIVE

  fun isDeviceConnected() = state.value.connectionState is DeviceConnectionState.Connected
}
