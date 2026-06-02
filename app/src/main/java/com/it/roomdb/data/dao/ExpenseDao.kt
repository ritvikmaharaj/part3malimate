package com.it.roomdb.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.it.roomdb.data.entity.Expense

/*
    Data Access Object (DAO) for Expense.

    This handles:
    - Inserting expenses
    - Retrieving all expenses
    - Filtering by date
    - Calculating totals per category
*/

@Dao
interface ExpenseDao {

    // Insert a new expense into the database
    @Insert
    suspend fun insert(expense: Expense)

    // Get all expenses
    @Query("SELECT * FROM expenses ORDER BY date DESC")
    suspend fun getAllExpenses(): List<Expense>

    // Get expenses between selected dates
    @Query("SELECT * FROM expenses WHERE date BETWEEN :startDate AND :endDate")
    suspend fun getExpensesByDate(startDate: String, endDate: String): List<Expense>

    // Get total spent per category (important for assignment requirement)
    @Query("""
        SELECT categoryId as cat, SUM(amount) as total 
        FROM expenses 
        WHERE date BETWEEN :startDate AND :endDate 
        GROUP BY categoryId
    """)
    suspend fun getTotalPerCategory(cat: String, startDate: String, endDate: String): List<Expense>

}