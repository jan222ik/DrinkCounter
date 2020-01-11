package com.github.jan222ik.eisteecounter.data.entity

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.time.LocalDate

data class DaysConsumptions(
    var date: MutableLiveData<LocalDate>,
    var consumptions: LiveData<MutableList<Consumption>>
)