package com.bullitt.sampleapp.ui.component

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bullitt.sampleapp.state.BtViewModel
import com.bullitt.sdk.platform.device.DeviceScanResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BtDevicesList(
  onDeviceClick: (DeviceScanResult.Ble) -> Unit,
  viewModel: BtViewModel = hiltViewModel(),
) {
  LaunchedEffect(Unit) { viewModel.startScan() }

  val scanningState by viewModel.scanningState.collectAsState()
  val devicesList = scanningState.devicesList
  val isScanning = scanningState.isScanning

  // Using Material pull-refresh until Material3 version is stable
  val pullRefreshState = rememberPullToRefreshState()

  PullToRefreshBox(
    isRefreshing = isScanning,
    state = pullRefreshState,
    onRefresh = { viewModel.startScan() },
    modifier = Modifier.fillMaxSize(),
  ) {
    if (devicesList.isEmpty() && !isScanning) {
      Box(
        modifier = Modifier.fillMaxSize().padding(top = 16.dp),
        contentAlignment = Alignment.Center,
      ) {
        Text(text = "No devices found", style = MaterialTheme.typography.bodyLarge)
      }
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize().padding(top = 16.dp),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        items(devicesList) { device ->
          DeviceItem(device = device.device.device, onClick = { onDeviceClick(device) })
        }
      }
    }
  }
}

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeviceItem(device: BluetoothDevice, onClick: () -> Unit) {
  Card(
    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    onClick = onClick,
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Text(
        text = device.name ?: "Unknown Device",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Medium,
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(text = "MAC: ${device.address}", style = MaterialTheme.typography.bodyMedium)
    }
  }
}
