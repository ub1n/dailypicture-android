package org.techtown.dailypicture.testRoom

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface GoalDao {
    @Query("SELECT * FROM goal")
    fun getGoal():List<Goal>

    @Query("DELETE FROM goal WHERE id=(:id)")
    fun delete(id:Int)

    @Insert(onConflict=OnConflictStrategy.REPLACE)
    fun insert(goal:Goal)
}