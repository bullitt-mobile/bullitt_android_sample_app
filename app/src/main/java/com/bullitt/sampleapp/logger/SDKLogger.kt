package com.bullitt.sampleapp.logger

import android.util.Log
import com.bullitt.sdk.platform.SdkLogger

class SDKLogger : SdkLogger {
  companion object {
    private const val TAG = "BullittSDKLog"
  }

  override fun logD(tag: String, message: String) {
    Log.d(TAG, message)
  }

  override fun logE(tag: String, message: String) {
    Log.e(TAG, message)
  }

  override fun logW(tag: String, message: String) {
    Log.w(TAG, message)
  }

  override fun logI(tag: String, message: String) {
    Log.i(TAG, message)
  }

  override fun logV(tag: String, message: String) {
    Log.v(TAG, message)
  }

  override fun logWtf(tag: String, message: String) {
    Log.v(TAG, message)
  }
}
