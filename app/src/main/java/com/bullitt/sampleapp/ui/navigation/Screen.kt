package com.bullitt.sampleapp.ui.navigation

sealed class Screen(val route: String) {
  data object Main : Screen("main")

  data object Devices : Screen("devices")
}
