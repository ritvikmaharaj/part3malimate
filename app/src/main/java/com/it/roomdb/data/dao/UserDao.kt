package com.it.roomdb.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.it.roomdb.data.entity.User

@Dao
interface UserDao {

    @Insert
    fun insertUser(user: User): Long

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    fun getUser(username: String): User?

    @Query("""
        SELECT * FROM users 
        WHERE username = :username AND password = :password 
        LIMIT 1
    """)
    fun login(username: String, password: String): User?
}