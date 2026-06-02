package com.it.roomdb.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.it.roomdb.data.entity.Category

@Dao
interface CategoryDao {

    @Insert
    fun insert(categoryEntity: Category): Long

    @Query("SELECT * FROM categories")
    fun getAllCategories(): List<Category>
}