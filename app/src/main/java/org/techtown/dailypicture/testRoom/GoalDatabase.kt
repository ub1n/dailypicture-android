package org.techtown.dailypicture.testRoom

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Goal::class], version = 1)
abstract class GoalDatabase : RoomDatabase() {

    abstract val goalDao: GoalDao

    companion object {
        private var instance: GoalDatabase? = null

        @Synchronized
        fun getInstance(context: Context): GoalDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(context.applicationContext, GoalDatabase::class.java, "goal.db")
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return instance as GoalDatabase
        }
    }
}