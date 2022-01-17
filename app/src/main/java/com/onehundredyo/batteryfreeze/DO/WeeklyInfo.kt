package com.onehundredyo.batteryfreeze.DO

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WeeklyInfo(
    @PrimaryKey var NMonthAgo: Int,
    var DataUsage: Long
)


