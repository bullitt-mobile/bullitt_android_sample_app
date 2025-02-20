package com.bullitt.sampleapp.store

import androidx.room.TypeConverter

class Converters {

  @TypeConverter fun toHealth(value: Int) = Message.Status.fromInt(value)

  @TypeConverter fun fromHealth(value: Message.Status) = value.value
}
