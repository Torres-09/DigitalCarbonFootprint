package com.onehundredyo.batteryfreeze.DO

import com.onehundredyo.batteryfreeze.Constants.*
import java.util.*

class TimeData {
    var list_week: MutableList<Long> = mutableListOf(0, 0, 0, 0, 0, 0, 0)
    var list_month: MutableList<Long> = mutableListOf(0,0,0,0)
    var list_year: MutableList<Long> = mutableListOf(0,0,0,0,0,0,0,0,0,0,0,0)


    fun getStartTimeList(type: Int): MutableList<Long>{
        // type == 0 week
        // 1 month
        when (type) {
            0 -> {
                for (i: Int in 0..6) {
                    list_week[i] = (Calendar.getInstance().apply {
                        set(Calendar.DAY_OF_WEEK, i + 1)
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }.time.time)
                }
                return list_week
            }
            1 -> {
                val this_week_sunday = Calendar.getInstance().apply {
                    set(Calendar.DAY_OF_WEEK, 1)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.time.time
                // this_week_sunday 에 이번주 일요일 0시0분0초 저장
                // 저번주: 이번주 일요일 - 1주일 시간
                // 저저번주: 이번주 일요일 - 2주일 시간
                // 저저저번주: 이번주 일요일 - 3주일 시간
                for (i: Int in 0..3) {
                    list_month[i] = this_week_sunday - ((3 - i) * INTERVAL_WEEK)
                }
                return list_month
            }
            2 -> {
                // TODO: 년도 확인해서 1월 이하로 내려가면 이전년도로 넘어가기
                for (i: Int in 11..0) {
                    list_year[i] = (Calendar.getInstance().apply {
                        set(Calendar.MONTH, -i)
                        set(Calendar.DATE, 1)
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }.time.time)
                }
                return list_year
            }
            else -> print("default")
        }
        return list_week    //바꿔야됨 임시...
    }
}