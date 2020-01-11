package com.github.jan222ik.eisteecounter.ui.vm

import android.app.Application
import androidx.lifecycle.*
import com.github.jan222ik.eisteecounter.data.db.DrinkDatabase
import com.github.jan222ik.eisteecounter.data.entity.Consumption
import com.github.jan222ik.eisteecounter.data.entity.DaysConsumptions
import com.github.jan222ik.eisteecounter.data.entity.Drink
import com.github.jan222ik.eisteecounter.repository.DrinkRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import java.lang.Exception
import java.time.LocalDate

class DrinkVM(application: Application) : AndroidViewModel(application) {

    private val repository: DrinkRepository

    val allDrinks: LiveData<List<Drink>>
    val allConsumption: MutableLiveData<DaysConsumptions>
    var currentDate: LocalDate

    init {
        val (drinkDao, consumptionDao) = DrinkDatabase.getDatabase(
            application,
            viewModelScope
        ).daos()
        repository = DrinkRepository(drinkDao, consumptionDao)
        allDrinks = repository.allDrinks
        allConsumption = repository.allConsumption
        currentDate = LocalDate.now()
    }

    fun insert(drink: Drink) = viewModelScope.launch {
        repository.insert(drink)
    }

    fun updateAmount(consumption: Consumption, amount: Double) = viewModelScope.launch {
        repository.updateAmount(consumption, (amount * 10).toInt())
    }

    fun createConsumption(drink: Drink, amount: Double) = viewModelScope.launch {
        val consumption = Consumption(
            drinkId = drink.drinkId!!,
            amount = (amount * 10).toInt(),
            date = currentDate
        )
        repository.insertConsumption(consumption)
    }


    fun nextDate(lifecycleOwner: LifecycleOwner, coroutineExceptionHandler: CoroutineExceptionHandler) = viewModelScope.launch(coroutineExceptionHandler) {
        val nextDay = currentDate.plusDays(1)
        val success = repository.updateAllConsumption(lifecycleOwner, nextDay)
        if (!success) {
            throw DateOutOfBoundsException()
        } else {
            currentDate = nextDay
        }
    }

    fun prevDate(lifecycleOwner: LifecycleOwner, coroutineExceptionHandler: CoroutineExceptionHandler) = viewModelScope.launch(coroutineExceptionHandler) {
        val prevDay = currentDate.minusDays(1)
        val success = repository.updateAllConsumption(lifecycleOwner, prevDay)
        if (!success) {
            throw DateOutOfBoundsException()
        } else {
            currentDate = prevDay
        }
    }
}

class DateOutOfBoundsException : Exception()