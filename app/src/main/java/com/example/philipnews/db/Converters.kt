package com.example.philipnews.db

import androidx.room.TypeConverter
import com.example.philipnews.models.Source

class Converters {

    @TypeConverter
    fun fromSource(source: Source): String? =source.name

    @TypeConverter
    fun fromString(name:String): Source = Source(name,name)

}