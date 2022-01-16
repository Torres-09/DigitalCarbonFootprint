package com.onehundredyo.batteryfreeze.DO

import com.onehundredyo.batteryfreeze.Constants.*
import java.util.*

class TimeData {
    var listWeek: MutableList<Long> = mutableListOf(0, 0, 0, 0, 0, 0, 0)
    var listMonth: MutableList<Long> = mutableListOf(0, 0, 0, 0)
    var listYear: MutableList<Long> = mutableListOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)

    fun getStartTimeList(type: Int): MutableList<Long> {
        when (type) {
            // type == 0 week
            0 -> {
                // 0 : 일, 1 : 월 ... 6 : 토
                for (i: Int in 0..6) {
                    listWeek[i] = (Calendar.getInstance().apply {
                        set(Calendar.DAY_OF_WEEK, i + 1)
                        set(Calendar.HOUR_OF_DAY, 0)
                        set(Calendar.MINUTE, 0)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }.time.time)
                }
                return listWeek
            }
            // type == 1 month
            1 -> {
                val this_week_sunday = Calendar.getInstance().apply {
                    set(Calendar.DAY_OF_WEEK, 1)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.time.time
                // list_month[3]: this_week_sunday 에 이번주 일요일 0시0분0초 저장
                // list_month[2]: 저번주 일요일 - 1주일 시간
                // list_month[1]: 저저번주 일요일 - 2주일 시간
                // list_month[0]: 저저저저번주 일요일 - 3주일 시간
                for (i: Int in 0..3) {
                    listMonth[i] = this_week_sunday - ((3 - i) * INTERVAL_WEEK)
                }
                return listMonth
            }
            // type == 2 year
            2 -> {
                var this_month = (Calendar.getInstance().apply {
                    set(Calendar.DATE, 1)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.time.time)
                for (i: Int in 0..10) {
                    listYear[i] = this_month - INTERVAL_MONTH * (11 - i)
                }
                listYear[11] = this_month
                return listYear
            }
        }
        return mutableListOf()
    }
}