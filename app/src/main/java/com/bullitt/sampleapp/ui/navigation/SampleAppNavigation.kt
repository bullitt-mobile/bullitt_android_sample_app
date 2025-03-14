package com.bullitt.sampleapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.bullitt.sampleapp.state.BtViewModel
import com.bullitt.sampleapp.state.ChatViewModel
import com.bullitt.sampleapp.ui.screen.BtDevicesScreen
import com.bullitt.sampleapp.ui.screen.MainScreen

@Composable
fun SampleAppNavigation() {
  val navController = rememberNavController()

  val chatViewModel = hiltViewModel<ChatViewModel>()
  val btViewModel = hiltViewModel<BtViewModel>()

  NavHost(navController = navController, startDestination = Screen.Main.route) {
    composable(Screen.Main.route) {
      MainScreen(
        navController = navController,
        chatViewModel = chatViewModel,
        btViewModel = btViewModel,
      )
    }
    composable(Screen.Devices.route) {
      BtDevicesScreen(navController = navController, btViewModel = btViewModel)
    }
  }
}
