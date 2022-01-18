package com.onehundredyo.batteryfreeze.DO

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AppUsageData(
    @PrimaryKey var name: String,
    var DataUsage: Long
)