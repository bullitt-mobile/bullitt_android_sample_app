package com.bullitt.sampleapp.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bullitt.sampleapp.data.BtDeviceState
import com.bullitt.sampleapp.data.DeviceConnectionState
import com.bullitt.sdk.platform.data.device.SatConnectionStatus
import com.bullitt.sdk.platform.data.smp.device.response.SatNetwork

@Composable
fun BtDeviceInfoBar(btDeviceState: BtDeviceState) {
  when (val connectionState = btDeviceState.connectionState) {
    is DeviceConnectionState.Connected -> {
      val isSatConnected =
        btDeviceState.satConnectionState == SatConnectionStatus.CONNECTED ||
          btDeviceState.satConnectionState == SatConnectionStatus.SEND_RECEIVE

      Row(
        modifier =
          Modifier.fillMaxWidth()
            .background(if (isSatConnected) Color(0xFFCAE8CA) else Color(0xFFFFCACA))
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Icon(imageVector = Icons.Default.Bluetooth, contentDescription = "Bluetooth")
        Text(
          text = connectionState.device.name,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
          text = btDeviceState.satConnectionState.name,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurface,
        )
        when (btDeviceState.satNetwork) {
          is SatNetwork.SignalStrength -> {
            Text(
              text = "sinr: ${btDeviceState.satNetwork.sinr}",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurface,
            )
          }
          SatNetwork.Unavailable -> {
            Text(
              text = "sinr: --",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurface,
            )
          }
        }
      }
    }
    is DeviceConnectionState.Connecting -> Unit
    DeviceConnectionState.Disconnected -> Unit
  }
}
