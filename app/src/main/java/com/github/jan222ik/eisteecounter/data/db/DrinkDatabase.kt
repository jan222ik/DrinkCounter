package com.github.jan222ik.eisteecounter.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.github.jan222ik.eisteecounter.data.dao.DrinkDao
import com.github.jan222ik.eisteecounter.data.entity.Drink
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Drink::class], version = 1, exportSchema = false)
abstract class DrinkDatabase : RoomDatabase() {

    abstract fun drinkDao(): DrinkDao

    companion object {
        @Volatile
        private var INSTANCE: DrinkDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): DrinkDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DrinkDatabase::class.java,
                    "drink_database"
                )
                    .addCallback(DrinkDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }

        private class DrinkDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            /**
             * Override the onOpen method to populate the database.
             * For this sample, we clear the database every time it is created or opened.
             */
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                // If you want to keep the data through app restarts,
                // comment out the following line.
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.drinkDao())
                    }
                }
            }
        }

        /**
         * Populate the database in a new coroutine.
         * If you want to start with more words, just add them.
         */
        suspend fun populateDatabase(drinkDao: DrinkDao) {
            // Start the app with a clean database every time.
            // Not needed if you only populate on creation.
            drinkDao.deleteAll()

            var drink = Drink("Hello")
            drinkDao.insert(drink)
            drink = Drink("World!")
            drinkDao.insert(drink)
        }
    }

}