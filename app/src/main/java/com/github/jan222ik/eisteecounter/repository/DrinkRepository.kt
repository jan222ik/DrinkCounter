package com.github.jan222ik.eisteecounter.repository

import androidx.lifecycle.LiveData
import com.github.jan222ik.eisteecounter.data.dao.ConsumptionDao
import com.github.jan222ik.eisteecounter.data.dao.DrinkDao
import com.github.jan222ik.eisteecounter.data.entity.Consumption
import com.github.jan222ik.eisteecounter.data.entity.Drink

class DrinkRepository(private val drinkDao: DrinkDao, private val consumptionDao: ConsumptionDao) {

    val allDrinks: LiveData<List<Drink>> = drinkDao.getAlphabetizedDrinks()
    val allConsumption: LiveData<List<Consumption>> = consumptionDao.getAll()

    suspend fun insert(drink: Drink) = drinkDao.insert(drink)
    suspend fun updateAmount(consumption: Consumption, toInt: Int) {
        consumption.amount += toInt
        consumptionDao.update(consumption)
    }

    suspend fun insertConsumption(consumption: Consumption) {
        consumptionDao.insert(consumption)
    }
}