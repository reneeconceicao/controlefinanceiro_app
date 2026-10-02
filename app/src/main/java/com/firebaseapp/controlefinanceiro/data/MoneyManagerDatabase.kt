package com.firebaseapp.controlefinanceiro.data

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.db.SupportSQLiteDatabase
import com.firebaseapp.controlefinanceiro.data.dao.BudgetDao
import com.firebaseapp.controlefinanceiro.data.dao.WordDao
import com.firebaseapp.controlefinanceiro.data.dao.CategoryDao
import com.firebaseapp.controlefinanceiro.data.entities.Budget
import com.firebaseapp.controlefinanceiro.data.entities.Word
import com.firebaseapp.controlefinanceiro.data.entities.Category
import com.firebaseapp.controlefinanceiro.data.entities.CategoryType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.math.BigDecimal

/**
 * Database class with a singleton Instance object.
 */
@Database(entities = [Word::class, Category::class, Budget::class], version = 5, exportSchema = false)
@TypeConverters(Converters::class)
abstract class MoneyManagerDatabase : RoomDatabase() {

    abstract fun wordDao(): WordDao
    abstract fun categoryDao(): CategoryDao

    abstract fun budgetDao(): BudgetDao

    companion object {
        @Volatile
        private var Instance: MoneyManagerDatabase? = null

        fun getDatabase(context: Context): MoneyManagerDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, MoneyManagerDatabase::class.java, "budget_database")
                    .fallbackToDestructiveMigration(true)
                    .addCallback(DatabaseCallback())
                    .build()

                    .also { Instance = it }
            }
        }

        class DatabaseCallback : Callback() {
            override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                super.onDestructiveMigration(db)
                Log.d("TAG_", "onCreate db")

                val dao = Instance?.categoryDao()

                val daoBudget = Instance?.budgetDao()

                val category1 = Category(categoryName = "Food", categoryType = CategoryType.Expense, position = 0)
                val category2 = Category(categoryName = "Car", categoryType = CategoryType.Expense, position = 1)
                val category3 = Category(categoryName = "Funny", categoryType = CategoryType.Expense, position = 2)

                val category4 = Category(categoryName = "Salary", categoryType = CategoryType.Income, position = 0)
                val category5 = Category(categoryName = "Investing", categoryType = CategoryType.Income, position = 1)

                CoroutineScope(Dispatchers.IO).launch {

                    val iq = dao?.insert(category1)

                    iq?.let {
                        val budget = Budget(budgetName = "Primeiro", value = BigDecimal("10.00"), categoryId = iq.toInt(), position = 0)
                        daoBudget?.insert(budget)
                    }

                    dao?.insert(category2)
                    dao?.insert(category3)
                    dao?.insert(category4)
                    dao?.insert(category5)

                    val i = dao?.getCategoriesByType(CategoryType.Expense)
                    i?.collect {
                        Log.d("TAG_", "onCreate categories: $i ")
                    }
                }
            }
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                Log.d("TAG_", "onCreate db")

                val dao = Instance?.categoryDao()

                val daoBudget = Instance?.budgetDao()

                val category1 = Category(categoryName = "Food", categoryType = CategoryType.Expense, position = 0)
                val category2 = Category(categoryName = "Car", categoryType = CategoryType.Expense, position = 1)
                val category3 = Category(categoryName = "Funny", categoryType = CategoryType.Expense, position = 2)

                val category4 = Category(categoryName = "Salary", categoryType = CategoryType.Income, position = 0)
                val category5 = Category(categoryName = "Investing", categoryType = CategoryType.Income, position = 1)



                CoroutineScope(Dispatchers.IO).launch {
                    val iq = dao?.insert(category1)

                    iq?.let {
                        val budget = Budget(budgetName = "Primeiro", value = BigDecimal("10"), categoryId = iq.toInt(), position = 0)
                        daoBudget?.insert(budget)
                    }


                    dao?.insert(category2)
                    dao?.insert(category3)
                    dao?.insert(category4)
                    dao?.insert(category5)

                    val i = dao?.getCategoriesByType(CategoryType.Expense)
                    i?.collect {
                        Log.d("TAG_", "onCreate categories: $i ")
                    }
                }
            }

        }
    }


}