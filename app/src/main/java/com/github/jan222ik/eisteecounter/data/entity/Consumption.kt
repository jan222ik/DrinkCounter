package com.github.jan222ik.eisteecounter.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity
data class Consumption(
    @ForeignKey(entity = Drink::class, parentColumns = ["drinkId"], childColumns = ["drinkId"])
    val drinkId: Int,
    @ColumnInfo(name = "amount")
    var amount: Int
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "consumptionId")
    var consumptionId: Int? = null
}