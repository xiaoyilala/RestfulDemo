package com.ice.good.lib.lib.cache

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ice.good.lib.common.AppGlobals

@Database(entities = [Cache::class], version = 1, exportSchema = true)
abstract class CacheDatabase: RoomDatabase() {

    abstract val cacheDao:CacheDao

    companion object{
        private var database:CacheDatabase

        init {
            val applicationContext = AppGlobals.get()!!.applicationContext
            database = Room.databaseBuilder(applicationContext, CacheDatabase::class.java, "good_cache").build()
        }

        fun get():CacheDatabase {
            return database
        }
    }
}