package com.github.jan222ik.eisteecounter.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "drink_table")
data class Drink(
    @ColumnInfo(name = "drinkName") val drinkName: String
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "drinkId")
    var drinkId: Int? = null
}