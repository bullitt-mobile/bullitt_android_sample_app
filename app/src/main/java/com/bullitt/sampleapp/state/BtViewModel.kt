package com.bullitt.sampleapp.state

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bullitt.sampleapp.data.BtDeviceState
import com.bullitt.sampleapp.data.DeviceConnectionState
import com.bullitt.sampleapp.data.ScanningState
import com.bullitt.sdk.platform.BullittApis
import com.bullitt.sdk.platform.data.Response
import com.bullitt.sdk.platform.data.StreamResponse
import com.bullitt.sdk.platform.data.device.BullittDeviceStatus
import com.bullitt.sdk.platform.data.device.SatConnectionStatus
import com.bullitt.sdk.platform.data.events.GlobalEvent
import com.bullitt.sdk.platform.data.smp.device.response.SatNetwork
import com.bullitt.sdk.platform.device.DeviceScanResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class BtViewModel
@Inject
constructor(
  private val bullittApis: BullittApis,
  private val btDeviceStateHolder: BtDeviceStateHolder,
) : ViewModel() {
  private val _scanningState = MutableStateFlow<ScanningState>(ScanningState())
  val scanningState: StateFlow<ScanningState> = _scanningState.asStateFlow()

  val btDeviceState: StateFlow<BtDeviceState> = btDeviceStateHolder.state

  var scanJob: Job? = null

  companion object {
    private const val TAG = "BtViewModel"
  }

  init {
    viewModelScope.launch {
      when (val response = bullittApis.getLinkedDevice()) {
        is Response.Failure -> {
          setDeviceDisconnected()
        }

        is Response.Success -> {
          when (val status = response.data.getStatus()) {
            is BullittDeviceStatus.Ble -> refreshConnectedDeviceState(status)
            else -> setDeviceDisconnected()
          }
        }
      }
    }

    viewModelScope.launch { bullittApis.globalEvents.collect { handleGlobalEvent(it) } }
  }

  private suspend fun handleGlobalEvent(event: GlobalEvent) {
    when (event) {
      is GlobalEvent.DeviceLinked -> {
        when (val status = event.satDeviceConnection.getStatus()) {
          is BullittDeviceStatus.Ble -> refreshConnectedDeviceState(status)
          else -> setDeviceDisconnected()
        }
      }
      is GlobalEvent.DeviceUpdate -> {
        when (val status = event.deviceStatus) {
          is BullittDeviceStatus.Ble -> refreshConnectedDeviceState(status)
          else -> setDeviceDisconnected()
        }
      }
      GlobalEvent.DeviceUnlinked -> setDeviceDisconnected()

      // We don't handle messages here
      is GlobalEvent.Message -> Unit
    }
  }

  fun startScan() {
    _scanningState.update { ScanningState(isScanning = true, devicesList = emptyList()) }
    scanJob =
      viewModelScope.launch {
        bullittApis.listDevices(timeoutInMillis = 10000).collect {
          when (it) {
            is StreamResponse.Data -> {
              when (val device = it.data) {
                is DeviceScanResult.Ble -> {
                  val devicesList = _scanningState.value.devicesList.toMutableList()
                  devicesList.add(device)
                  _scanningState.update { it.copy(devicesList = devicesList) }
                }
                // D2D device is an android phone that can do satellite communication
                is DeviceScanResult.D2d -> Unit
              }
            }
            is StreamResponse.End -> stopScan()
            is StreamResponse.Failure -> {
              stopScan()
              Log.e(TAG, "Failed to fetch bluetooth devices: ${it.exception}")
            }
          }
        }
      }
  }

  fun stopScan() {
    scanJob?.cancel()
    _scanningState.update { it.copy(isScanning = false) }
  }

  fun selectDevice(device: DeviceScanResult.Ble) {
    stopScan()
    btDeviceStateHolder.updateState {
      it.copy(connectionState = DeviceConnectionState.Connecting(device))
    }
    viewModelScope.launch {
      when (val deviceConnection = bullittApis.requestDevicePairing(device)) {
        is Response.Failure -> {
          Log.e(TAG, "Failed to connect to device: ${deviceConnection.exception}")
          setDeviceDisconnected()
        }

        is Response.Success -> {
          // At this phase, you would ideally check for subscription using the imsi from
          // deviceConnection.data.imsi and confirm device linking. Just defaulting for sample app.
          when (bullittApis.confirmDeviceLinking()) {
            is Response.Failure -> {
              Log.e(TAG, "Failed to confirm device linking")
              setDeviceDisconnected()
            }
            is Response.Success -> Log.d(TAG, "Device linked successfully")
          }
        }
      }
    }
  }

  fun forgetDevice() {
    viewModelScope.launch { bullittApis.removeLinkedDevice() }
    setDeviceDisconnected()
  }

  fun setDeviceDisconnected() {
    btDeviceStateHolder.updateState { BtDeviceState() }
  }

  fun refreshConnectedDeviceState(deviceStatus: BullittDeviceStatus.Ble) {
    Log.d(
      TAG,
      "Bluetooth device state: \n" +
        "SatConnection: ${deviceStatus.satConnectionStatus} \n" +
        "SatNetwork: ${deviceStatus.satNetwork} \n" +
        "Battery level: ${deviceStatus.batteryLevel}",
    )

    btDeviceStateHolder.updateState {
      it.copy(
        connectionState =
          DeviceConnectionState.Connected(
            device = deviceStatus,
            batteryLevel = deviceStatus.batteryLevel,
            firmwareVersion = deviceStatus.deviceInfo?.osVersion ?: "",
            serialNumber = deviceStatus.deviceInfo?.serialNumber ?: "",
            imsi = deviceStatus.deviceImsi.imsi,
          ),
        satConnectionState = deviceStatus.satConnectionStatus ?: SatConnectionStatus.DISCONNECTED,
        satNetwork = deviceStatus.satNetwork ?: SatNetwork.Unavailable,
        batteryLevel = deviceStatus.batteryLevel ?: 0,
      )
    }
  }
}
