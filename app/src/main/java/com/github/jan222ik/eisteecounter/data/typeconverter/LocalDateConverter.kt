package com.github.jan222ik.eisteecounter.data.typeconverter

import androidx.room.TypeConverter
import java.time.LocalDate

class LocalDateConverter {

    @TypeConverter
    fun toDate(dateString: String?): LocalDate? =
        if (dateString != null) LocalDate.parse(dateString) else null

    @TypeConverter
    fun toDateString(date: LocalDate?): String? = date?.toString()

}