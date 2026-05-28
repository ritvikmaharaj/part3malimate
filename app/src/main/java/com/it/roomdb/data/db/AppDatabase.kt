package com.it.roomdb.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.it.roomdb.data.dao.UserDao
import com.it.roomdb.data.dao.CategoryDao

import com.it.roomdb.data.dao.ExpenseDao
import com.it.roomdb.data.entity.User
import com.it.roomdb.data.entity.Category
import com.it.roomdb.data.entity.Expense

/*
    Main Room database class.

    This database stores:
    - User data (login)
    - Categories (expense grouping)
    - Expenses (including image paths for photos)
*/

@Database(
    entities = [User::class, Category::class, Expense::class],
    version = 3
)
abstract class AppDatabase : RoomDatabase() {

    // Data Access Objects
    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun expenseDao(): ExpenseDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        /*
            Returns a single instance of the database (Singleton pattern).
            Prevents multiple database instances running at the same time.
        */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {


                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )

                    /*
                        Clears and rebuilds database if schema changes.
                        Useful during development to avoid crashes.
                    */
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}