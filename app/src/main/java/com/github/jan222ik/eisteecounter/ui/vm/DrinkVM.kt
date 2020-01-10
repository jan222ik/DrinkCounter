package com.github.jan222ik.eisteecounter.ui.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.github.jan222ik.eisteecounter.data.db.DrinkDatabase
import com.github.jan222ik.eisteecounter.data.entity.Consumption
import com.github.jan222ik.eisteecounter.data.entity.Drink
import com.github.jan222ik.eisteecounter.repository.DrinkRepository
import kotlinx.coroutines.launch

class DrinkVM(application: Application) : AndroidViewModel(application) {

    private val repository: DrinkRepository

    val allDrinks: LiveData<List<Drink>>
    val allConsumption: LiveData<List<Consumption>>

    init {
        val (drinkDao, consumptionDao) = DrinkDatabase.getDatabase(application, viewModelScope).daos()
        repository = DrinkRepository(drinkDao, consumptionDao)
        allDrinks = repository.allDrinks
        allConsumption = repository.allConsumption
    }

    fun insert(drink: Drink) = viewModelScope.launch {
        repository.insert(drink)
    }

    fun updateAmount(consumption: Consumption, amount: Double) = viewModelScope.launch {
        repository.updateAmount(consumption, (amount*10).toInt())
    }

    fun createConsumption(drink: Drink, amount: Double) = viewModelScope.launch {
        val consumption = Consumption(drinkId = drink.drinkId!!, amount = (amount * 10).toInt())
        repository.insertConsumption(consumption)
    }
}