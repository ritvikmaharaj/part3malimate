package com.it.roomdb.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/*
    Expense entity represents a single expense record in the database.

    It includes:
    - Basic expense details
    - Time tracking (required by assignment)
    - Category reference
    - Optional image path for storing photos
*/

@Entity(tableName = "expenses")
data class Expense(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val description: String,

    val amount: Double,

    val date: String,

    val startTime: String,

    val endTime: String,

    val categoryId: Int,

    // Stores the photo file path or URI (can be null)
    val imagePath: String? = null
)