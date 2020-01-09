package com.github.jan222ik.eisteecounter.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.jan222ik.eisteecounter.data.entity.Consumption
import com.github.jan222ik.eisteecounter.data.entity.Drink

@Dao
interface ConsumptionDao {

    @Query("Select * From Consumption")
    fun getAll(): LiveData<List<Consumption>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(consumption: Consumption)

    @Query("DELETE FROM consumption")
    suspend fun deleteAll()
}
