package com.firebaseapp.controlefinanceiro.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.firebaseapp.controlefinanceiro.data.dao.WordDao
import com.firebaseapp.controlefinanceiro.data.dao.CategoryDao
import com.firebaseapp.controlefinanceiro.data.entities.Word
import com.firebaseapp.controlefinanceiro.data.entities.Category

/**
 * Database class with a singleton Instance object.
 */
@Database(entities = [Word::class, Category::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class MoneyManagerDatabase : RoomDatabase() {

    abstract fun wordDao(): WordDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var Instance: MoneyManagerDatabase? = null

        fun getDatabase(context: Context): MoneyManagerDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, MoneyManagerDatabase::class.java, "budget_database")
                    .fallbackToDestructiveMigration(true)
                    .build()

                    .also { Instance = it }
            }
        }
    }
}