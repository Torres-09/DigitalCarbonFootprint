package com.onehundredyo.batteryfreeze.dataBaseHelper

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.onehundredyo.batteryfreeze.DO.AppUsageData
import com.onehundredyo.batteryfreeze.DO.MonthlyInfo
import com.onehundredyo.batteryfreeze.DO.WeeklyInfo
import com.onehundredyo.batteryfreeze.DO.YearlyInfo

@Database(entities = [YearlyInfo::class, MonthlyInfo::class, WeeklyInfo::class, AppUsageData::class], version = 1)
abstract class DataBaseManager:RoomDatabase() {
    abstract fun DatausageDAO(): DatausageDAO

    companion object{
        private var instance: DataBaseManager? = null
        
        @Synchronized
        fun getInstance(context:Context): DataBaseManager?{
            if (instance == null){
                synchronized((DataBaseManager::class)){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        DataBaseManager::class.java,
                        "databasemanager"
                    ).build()
                }
            }
            return instance
        }
    }

}