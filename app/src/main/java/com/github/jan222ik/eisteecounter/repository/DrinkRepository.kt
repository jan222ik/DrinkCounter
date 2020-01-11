package com.github.jan222ik.eisteecounter.repository

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.github.jan222ik.eisteecounter.data.dao.ConsumptionDao
import com.github.jan222ik.eisteecounter.data.dao.DrinkDao
import com.github.jan222ik.eisteecounter.data.entity.Consumption
import com.github.jan222ik.eisteecounter.data.entity.DaysConsumptions
import com.github.jan222ik.eisteecounter.data.entity.Drink
import java.time.LocalDate

class DrinkRepository(private val drinkDao: DrinkDao, private val consumptionDao: ConsumptionDao) {

    val allDrinks: LiveData<List<Drink>> = drinkDao.getAlphabetizedDrinks()
    private val daysConsumptions =
        DaysConsumptions(MutableLiveData(LocalDate.now()), consumptionDao.getAll(LocalDate.now()))
    val allConsumption: MutableLiveData<DaysConsumptions> =
        MutableLiveData<DaysConsumptions>(daysConsumptions)

    suspend fun insert(drink: Drink) = drinkDao.insert(drink)

    suspend fun updateAmount(consumption: Consumption, toInt: Int) {
        consumption.amount += toInt
        consumptionDao.update(consumption)
    }

    suspend fun insertConsumption(consumption: Consumption) {
        consumptionDao.insert(consumption)
    }

    suspend fun updateAllConsumption(lifecycleOwner: LifecycleOwner, date: LocalDate): Boolean {
        return if (date.isAfter(LocalDate.now())) {
            false
        } else {
            Log.d("TAG", "updateAllConsumption: Update to date $date")
            allConsumption.value!!.date.value = date
            (allConsumption.value!!.consumptions.value as MutableList).apply {
                consumptionDao.getAll(date).observe(lifecycleOwner, Observer {
                    clear()
                    addAll(it)
                })
                allConsumption.postValue(daysConsumptions)
            }
            true
        }
    }
}