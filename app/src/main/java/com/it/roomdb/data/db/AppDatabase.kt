package com.it.roomdb.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.it.roomdb.data.dao.UserDao
import com.it.roomdb.data.dao.CategoryDao
import com.it.roomdb.data.dao.ExpenseDao
import com.it.roomdb.data.dao.SavingsGoalDao
import com.it.roomdb.data.entity.User
import com.it.roomdb.data.entity.Category
import com.it.roomdb.data.entity.Expense
import com.it.roomdb.data.entity.SavingsGoal

@Database(
    entities = [User::class, Category::class, Expense::class, SavingsGoal::class],
    version = 4
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun savingsGoalDao(): SavingsGoalDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}