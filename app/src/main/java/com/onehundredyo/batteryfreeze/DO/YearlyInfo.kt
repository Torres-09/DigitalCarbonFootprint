package com.onehundredyo.batteryfreeze.DO

import androidx.room.Entity
import androidx.room.PrimaryKey
// 테이블은 대소문자 구별하지 않음 (yearlyinfo 로 지정됨)
@Entity
data class YearlyInfo(
    @PrimaryKey var NMonthAgo:Int,
    var DataUsage: Long
)