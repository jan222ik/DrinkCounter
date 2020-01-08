package com.github.jan222ik.eisteecounter.repository

import androidx.lifecycle.LiveData
import com.github.jan222ik.eisteecounter.data.dao.DrinkDao
import com.github.jan222ik.eisteecounter.data.entity.Drink

class DrinkRepository(private val drinkDao: DrinkDao) {

    val allDrinks: LiveData<List<Drink>> = drinkDao.getAlphabetizedDrinks()

    suspend fun insert(drink: Drink) = drinkDao.insert(drink)
}