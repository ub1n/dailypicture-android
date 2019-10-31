package org.techtown.dailypicture.testRoom

import android.content.Context

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Picture::class], version = 1)
abstract class PictureDatabase : RoomDatabase() {

    abstract val pictureDao: PictureDao

    companion object {
        private var instance: PictureDatabase? = null

        @Synchronized
        fun getInstance(context: Context): PictureDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(context.applicationContext, PictureDatabase::class.java, "picture.db")
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return instance as PictureDatabase
        }
    }
}