package com.bullitt.sampleapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import com.bullitt.sampleapp.ui.navigation.SampleAppNavigation
import com.bullitt.sdk.platform.BullittApis
import com.bullitt.sdk.platform.data.BullittConfig
import com.bullitt.sdk.platform.data.Response
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
  @Inject lateinit var bullittApis: BullittApis

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent { MaterialTheme { SampleAppNavigation() } }
  }

  override fun onStart() {
    super.onStart()

    setupBullittConfig()
  }

  fun setupBullittConfig() {
    val bullittConfig =
      BullittConfig(
        // Modify this to change sender's user ID
        userId = 14045551002,
        buildMode = BullittConfig.BuildMode.PROD,
      )

    when (val response = bullittApis.setConfig(bullittConfig)) {
      is Response.Failure -> {
        Log.e("MainActivity", "Failed to set config: ${response.exception}")
      }
      is Response.Success -> {
        Log.d("MainActivity", "Config set successfully")
      }
    }
  }
}
