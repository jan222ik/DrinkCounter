package com.github.jan222ik.eisteecounter.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.jan222ik.eisteecounter.data.entity.Drink

@Dao
interface DrinkDao {
    @Query("SELECT * from drink_table ORDER BY drinkName ASC")
    fun getAlphabetizedDrinks(): LiveData<List<Drink>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(drink: Drink)

    @Query("DELETE FROM drink_table")
    suspend fun deleteAll()
}
