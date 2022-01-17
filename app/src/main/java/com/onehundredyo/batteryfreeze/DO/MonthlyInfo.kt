package com.onehundredyo.batteryfreeze.DO

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MonthlyInfo(
    @PrimaryKey var NMonthAgo: Int,
    var DataUsage: Long
)

