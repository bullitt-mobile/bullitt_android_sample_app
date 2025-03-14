package com.bullitt.sampleapp.ui.screen

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.bullitt.sampleapp.data.DeviceConnectionState
import com.bullitt.sampleapp.state.BtViewModel
import com.bullitt.sampleapp.state.ChatViewModel
import com.bullitt.sampleapp.ui.component.BtDeviceInfoBar
import com.bullitt.sampleapp.ui.component.ChatList
import com.bullitt.sampleapp.ui.component.MessageComposeBar
import com.bullitt.sampleapp.ui.navigation.Screen
import com.bullitt.sdk.platform.PermissionUtil
import com.bullitt.sdk.platform.data.Response
import com.bullitt.sdk.platform.data.exception.MissingPermissionException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
  navController: NavHostController,
  chatViewModel: ChatViewModel = hiltViewModel(),
  btViewModel: BtViewModel = hiltViewModel(),
) {
  val context = LocalContext.current

  val permissionsLauncher =
    rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
      permissionsResult ->
      val allGranted = permissionsResult.all { it.value }
      if (allGranted) {
        navController.navigate(Screen.Devices.route)
      } else {
        Toast.makeText(context, "Permissions not granted", Toast.LENGTH_SHORT).show()
      }
    }

  val btDeviceState by btViewModel.btDeviceState.collectAsState()

  // Please modify this to the required number to send messages to
  var chattingWith by remember { mutableLongStateOf(14045551001) }
  val (showDialog, setShowDialog) = remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      Column {
        TopAppBar(
          title = { Text(chattingWith.toString()) },
          actions = {
            IconButton(onClick = { setShowDialog(true) }) {
              Icon(imageVector = Icons.Default.Edit, contentDescription = "Chat Number")
            }

            if (btDeviceState.connectionState is DeviceConnectionState.Connected) {
              IconButton(onClick = { btViewModel.forgetDevice() }) {
                Icon(
                  imageVector = Icons.Default.BluetoothDisabled,
                  contentDescription = "Bluetooth",
                )
              }
            } else {
              IconButton(
                onClick = {
                  checkPermissionsAndNavigateToDevicesList(
                    context,
                    navController,
                    permissionsLauncher,
                  )
                }
              ) {
                Icon(imageVector = Icons.Default.Bluetooth, contentDescription = "Bluetooth")
              }
            }
          },
          colors =
            TopAppBarDefaults.topAppBarColors()
              .copy(containerColor = MaterialTheme.colorScheme.primaryContainer),
        )

        BtDeviceInfoBar(btDeviceState)
      }
    },
    bottomBar = { MessageComposeBar(chattingWith = chattingWith, viewModel = chatViewModel) },
  ) { padding ->
    Column(modifier = Modifier.padding(padding)) {
      ChatList(chatWith = chattingWith, viewModel = chatViewModel)
    }
  }

  if (showDialog) {
    NumberUpdateDialog(
      initialNumber = chattingWith.toString(),
      onDismiss = { setShowDialog(false) },
      onConfirm = { newNumber ->
        try {
          val newLongValue = newNumber.toLong()
          chattingWith = newLongValue
          Toast.makeText(context, "Number updated to $newLongValue", Toast.LENGTH_SHORT).show()
          setShowDialog(false)
        } catch (e: NumberFormatException) {
          Toast.makeText(context, "Invalid number format", Toast.LENGTH_SHORT).show()
        }
      },
    )
  }
}

fun checkPermissionsAndNavigateToDevicesList(
  context: Context,
  navController: NavHostController,
  permissionsLauncher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>,
) {
  val requiresPermissions = PermissionUtil.verifyRequiredPermissions(context)
  return when (requiresPermissions) {
    is Response.Failure -> {
      val permissionsToRequest =
        (requiresPermissions.exception as MissingPermissionException).permissions
      permissionsLauncher.launch(permissionsToRequest)
    }
    is Response.Success -> {
      navController.navigate(Screen.Devices.route)
    }
  }
}

@Composable
fun NumberUpdateDialog(initialNumber: String, onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
  var number by remember { mutableStateOf(initialNumber) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text("Update Phone Number") },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.Center,
      ) {
        OutlinedTextField(
          value = number,
          onValueChange = { number = it },
          label = { Text("Phone Number") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
          modifier = Modifier.fillMaxWidth(),
        )
      }
    },
    confirmButton = { Button(onClick = { onConfirm(number) }) { Text("Update") } },
    dismissButton = { Button(onClick = onDismiss) { Text("Cancel") } },
  )
}
