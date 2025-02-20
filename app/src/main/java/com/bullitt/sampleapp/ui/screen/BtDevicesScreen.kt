package com.bullitt.sampleapp.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.bullitt.sampleapp.data.DeviceConnectionState
import com.bullitt.sampleapp.state.BtViewModel
import com.bullitt.sampleapp.ui.component.BtDevicesList

@Composable
fun BtDevicesScreen(navController: NavHostController, btViewModel: BtViewModel) {
  val btDeviceState by btViewModel.btDeviceState.collectAsState()

  LaunchedEffect(btDeviceState.connectionState) {
    if (btDeviceState.connectionState is DeviceConnectionState.Connected) {
      navController.navigateUp()
    }
  }

  Column(modifier = Modifier.systemBarsPadding()) {
    BtDevicesList(onDeviceClick = { btViewModel.selectDevice(it) })

    if (btDeviceState.connectionState is DeviceConnectionState.Connecting) {
      Dialog(onDismissRequest = {}) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
      }
    }
  }
}
