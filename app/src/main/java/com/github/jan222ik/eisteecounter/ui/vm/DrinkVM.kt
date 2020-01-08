package com.github.jan222ik.eisteecounter.ui.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.github.jan222ik.eisteecounter.data.db.DrinkDatabase
import com.github.jan222ik.eisteecounter.data.entity.Drink
import com.github.jan222ik.eisteecounter.repository.DrinkRepository
import kotlinx.coroutines.launch

class DrinkVM(application: Application) : AndroidViewModel(application) {

    // The ViewModel maintains a reference to the repository to get data.
    private val repository: DrinkRepository
    // LiveData gives us updated words when they change.
    val allWords: LiveData<List<Drink>>

    init {
        // Gets reference to WordDao from WordRoomDatabase to construct
        // the correct WordRepository.
        val wordsDao = DrinkDatabase.getDatabase(application, viewModelScope).drinkDao()
        repository = DrinkRepository(wordsDao)
        allWords = repository.allDrinks
    }

    /**
     * The implementation of insert() in the database is completely hidden from the UI.
     * Room ensures that you're not doing any long running operations on the mainthread, blocking
     * the UI, so we don't need to handle changing Dispatchers.
     * ViewModels have a coroutine scope based on their lifecycle called viewModelScope which we
     * can use here.
     */
    fun insert(drink: Drink) = viewModelScope.launch {
        repository.insert(drink)
    }
}