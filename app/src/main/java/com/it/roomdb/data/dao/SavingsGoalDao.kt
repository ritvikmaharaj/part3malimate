package com.it.roomdb.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.it.roomdb.data.entity.SavingsGoal

@Dao
interface SavingsGoalDao {

    @Insert
    suspend fun insert(goal: SavingsGoal)

    @Update
    suspend fun update(goal: SavingsGoal)

    @Query("SELECT * FROM savings_goals")
    suspend fun getAllGoals(): List<SavingsGoal>
}