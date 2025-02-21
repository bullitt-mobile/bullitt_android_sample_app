package com.bullitt.sampleapp.di

import com.bullitt.sampleapp.state.BtDeviceStateHolder
import com.bullitt.sdk.platform.BullittSDK
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class SampleAppModule {
  @Provides fun provideBullittApis() = BullittSDK.bullittApis

  @Provides fun provideBtDeviceStateHolder() = BtDeviceStateHolder()
}
