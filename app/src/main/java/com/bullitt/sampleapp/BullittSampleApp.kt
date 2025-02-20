package com.bullitt.sampleapp

import android.app.Application
import com.bullitt.sampleapp.logger.SDKLogger
import com.bullitt.sdk.platform.BullittSDK
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BullittSampleApp : Application() {
  override fun onCreate() {
    super.onCreate()
    BullittSDK.initialize(this, SDKLogger(), {})
  }
}
