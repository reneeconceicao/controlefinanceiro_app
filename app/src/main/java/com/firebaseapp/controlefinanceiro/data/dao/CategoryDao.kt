package com.firebaseapp.controlefinanceiro.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.firebaseapp.controlefinanceiro.data.entities.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(category: Category)

    @Update
    suspend fun update(person: Category)

    @Delete
    suspend fun delete(person: Category)

    @Query("SELECT * from categories WHERE id = :id")
    fun getCategory(id: Int): Flow<Category?>

    @Query("SELECT * from categories WHERE categoryName LIKE :query ORDER BY categoryName ASC")
    fun getAllCategories(query: String) : Flow<List<Category>>
}