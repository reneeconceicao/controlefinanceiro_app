package com.firebaseapp.controlefinanceiro.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.firebaseapp.controlefinanceiro.data.entities.Budget
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface BudgetDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(budget: Budget)

    @Update
    suspend fun update(budget: Budget)

    @Delete
    suspend fun delete(budget: Budget)

    @Query("SELECT * from budgets WHERE id = :id")
    fun getBudget(id: Int): Flow<Budget?>

    @Query("SELECT * from budgets ORDER BY position ASC")
    fun getAllBudgets() : Flow<List<Budget>>


}