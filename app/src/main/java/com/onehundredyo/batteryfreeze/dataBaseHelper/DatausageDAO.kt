package com.onehundredyo.batteryfreeze.dataBaseHelper

import androidx.room.*
import com.onehundredyo.batteryfreeze.DO.AppUsageData
import com.onehundredyo.batteryfreeze.DO.MonthlyInfo
import com.onehundredyo.batteryfreeze.DO.WeeklyInfo
import com.onehundredyo.batteryfreeze.DO.YearlyInfo

// (사용전 StandardLee 에게 문의하기)
@Dao
interface DatausageDAO {
    
    //INSERT
    // 이미 존재하는 데이터면 새로운 데이터로 업데이트
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertYearData(data:YearlyInfo)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMonthData(data:MonthlyInfo)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWeekData(data: WeeklyInfo)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTopFiveAppData(data:AppUsageData)

    // UPDATE
    @Update
    fun updateYearData(data: YearlyInfo)
    @Update
    fun updateMonthData(data: MonthlyInfo)
    @Update
    fun updateWeekData(data: WeeklyInfo)
    @Update
    fun updateTopFiveAppData(data: AppUsageData)

    // DELETE
    @Delete
    fun deleteYearData(data: YearlyInfo)
    @Delete
    fun deleteMonthData(data: MonthlyInfo)
    @Delete
    fun deleteWeekData(data: WeeklyInfo)
    @Delete
    fun deleteTopFiveAppData(data:AppUsageData)

    // QUERY
    @Query("SELECT * FROM yearlyinfo")
    fun getAllYearlyData(): MutableList<YearlyInfo>
    @Query("SELECT * FROM monthlyinfo")
    fun getAllMonthlyData(): MutableList<MonthlyInfo>
    @Query("SELECT * FROM weeklyinfo")
    fun getAllWeeklyData(): MutableList<WeeklyInfo>
    @Query("SELECT * FROM appusagedata ORDER BY(DataUsage)")
    fun getAllTopFiveAppData(): MutableList<AppUsageData>

    // Table DELETE QUERY 
    @Query("DELETE FROM yearlyinfo")
    fun deleteAllYearlyData()
    @Query("DELETE FROM monthlyinfo")
    fun deleteAllMonthlyData()
    @Query("DELETE FROM weeklyinfo")
    fun deleteAllWeeklyData()
    @Query("DELETE FROM appusagedata")
    fun deleteAllTopFiveAppData()
}