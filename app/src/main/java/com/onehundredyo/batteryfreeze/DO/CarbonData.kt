package com.onehundredyo.batteryfreeze.DO

import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.util.Log
import com.onehundredyo.batteryfreeze.Constants.*
import java.lang.Math.pow
import java.util.*

class CarbonData {
    private var totalDailyCabon: Long
    private var dailyCarbon: MutableMap<String, Long>
    private var weeklyCarbon: MutableList<Long>
    private var monthlyCarbon: MutableList<Long>
    private var yearlyCarbon: MutableList<Long>
    private var topFiveApp: MutableList<Pair<String, Long>>
    val listPackageInfo: MutableList<PackageInfo>
    val packageManager: PackageManager
    val networkStatsManager: NetworkStatsManager
    var timeData: TimeData

    constructor(
        listPackageInfo: MutableList<PackageInfo>,
        packageManager: PackageManager,
        networkStatsManager: NetworkStatsManager
    ) {
        this.totalDailyCabon = 0L
        this.listPackageInfo = listPackageInfo
        this.packageManager = packageManager
        this.networkStatsManager = networkStatsManager
        dailyCarbon = mutableMapOf()
        weeklyCarbon = mutableListOf(0, 0, 0, 0, 0, 0, 0)
        monthlyCarbon = mutableListOf(0, 0, 0, 0)
        yearlyCarbon = mutableListOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        timeData = TimeData()
        topFiveApp = mutableListOf()
    }

    fun getTotalDailyCarbon(): Long {
        return this.totalDailyCabon
    }

    fun setDailyCarbon() {
        var todayStart = (Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time.time)
        Log.d("time", "time ${todayStart}")
        for (i in listPackageInfo.indices) {
            var packageName = listPackageInfo.get(i).packageName
            var info = packageManager.getApplicationInfo(packageName, 0)
            var uid = info.uid
            val nwStatsWifi = networkStatsManager.queryDetailsForUid(
                ConnectivityManager.TYPE_WIFI,     //TYPE_MOBILE 시 데이터 사용량
                null,
                todayStart,     // EPOCH TIME 으로 시작시간 지정(15일이전부터)
                System.currentTimeMillis(),                            // EPOCH TIME 으로 종료시간 지정(지금 시간)
                uid
            )

            val bucketWifi = NetworkStats.Bucket()
            var rxtxWifi = 0L

            while (nwStatsWifi.hasNextBucket()) {
                nwStatsWifi.getNextBucket(bucketWifi)
                rxtxWifi += bucketWifi.rxBytes
                rxtxWifi += bucketWifi.txBytes
            }

            rxtxWifi = transData(rxtxWifi)
            if (!dailyCarbon.containsKey(packageName)) {
                dailyCarbon.put(packageName, rxtxWifi)
            } else {
                dailyCarbon.set(packageName, dailyCarbon.getValue(packageName) + rxtxWifi)
            }
            totalDailyCabon += rxtxWifi
        }
        for (i in listPackageInfo.indices) {
            var packageName = listPackageInfo.get(i).packageName
            var info = packageManager.getApplicationInfo(packageName, 0)
            var uid = info.uid
            val nwStatsWifi = networkStatsManager.queryDetailsForUid(
                ConnectivityManager.TYPE_MOBILE,     //TYPE_MOBILE 시 데이터 사용량
                null,
                todayStart,     // EPOCH TIME 으로 시작시간 지정(15일이전부터)
                System.currentTimeMillis(),                            // EPOCH TIME 으로 종료시간 지정(지금 시간)
                uid
            )

            val bucketMobile = NetworkStats.Bucket()
            var rxtxMobile = 0L

            while (nwStatsWifi.hasNextBucket()) {
                nwStatsWifi.getNextBucket(bucketMobile)
                rxtxMobile += bucketMobile.rxBytes
                rxtxMobile += bucketMobile.txBytes
            }

            rxtxMobile = transData(rxtxMobile)
            if (!dailyCarbon.containsKey(packageName)) {
                dailyCarbon.put(packageName, rxtxMobile)
            } else {
                dailyCarbon.set(packageName, dailyCarbon.getValue(packageName) + rxtxMobile)
            }
            totalDailyCabon += rxtxMobile
        }

        // 맵을 리스트로 변경하여 소트한 후 저장
        var list: List<Pair<String, Long>> =
            dailyCarbon.toList().sortedWith(compareBy { it.second }).reversed().subList(0, 5)

        Log.d("CARBON DATA", list.toString())

//        for(i in 0..4){
//            // 패키지명을 어플명으로 변경하여 Pair 에 저장
//            topFiveApp[i] =
//                Pair(
//                    packageManager.getApplicationLabel(
//                        packageManager.getApplicationInfo(
//                            list[i].first,
//                            PackageManager.GET_META_DATA
//                        )
//                    ).toString(), list[i].second
//                )
//            Log.d("CARBONDATA", topFiveApp.toString())
//
//        }
        for (i in list.indices) {
            // 패키지명을 어플명으로 변경하여 Pair 에 저장
            topFiveApp.add(
                Pair(
                    packageManager.getApplicationLabel(
                        packageManager.getApplicationInfo(
                            list[i].first,
                            PackageManager.GET_META_DATA
                        )
                    ).toString(), list[i].second
                )
            )

            Log.d("CARBONDATA", topFiveApp.toString())

        }
    }

    fun getDailyCarbon(): MutableMap<String, Long> {
        return this.dailyCarbon
    }

    fun getTopFiveApp(): MutableList<Pair<String, Long>> {
        return this.topFiveApp
    }

    fun setWeeklyCarbon() {
        var startTimeList = timeData.getStartTimeList(0)
        for (time in startTimeList.indices) {
            for (i in listPackageInfo.indices) {
                var packageName = listPackageInfo.get(i).packageName
                var info = packageManager.getApplicationInfo(packageName, 0)
                var uid = info.uid
                val nwStatsWifi = networkStatsManager.queryDetailsForUid(
                    ConnectivityManager.TYPE_WIFI,
                    null,
                    startTimeList[time],
                    startTimeList[time] + INTERVAL_DAY,
                    uid
                )

                val bucketWifi = NetworkStats.Bucket()
                var rxtxWifi = 0L

                while (nwStatsWifi.hasNextBucket()) {
                    nwStatsWifi.getNextBucket(bucketWifi)
                    rxtxWifi += bucketWifi.rxBytes
                    rxtxWifi += bucketWifi.txBytes
                }
                weeklyCarbon[time] += rxtxWifi
            }
        }
        for (time in startTimeList.indices) {
            for (i in listPackageInfo.indices) {
                var packageName = listPackageInfo.get(i).packageName
                var info = packageManager.getApplicationInfo(packageName, 0)
                var uid = info.uid
                val nwStatsMobile = networkStatsManager.queryDetailsForUid(
                    ConnectivityManager.TYPE_MOBILE,
                    null,
                    startTimeList[time],
                    startTimeList[time] + INTERVAL_DAY,
                    uid
                )

                val bucketMobile = NetworkStats.Bucket()
                var rxtxMobile = 0L

                while (nwStatsMobile.hasNextBucket()) {
                    nwStatsMobile.getNextBucket(bucketMobile)
                    rxtxMobile += bucketMobile.rxBytes
                    rxtxMobile += bucketMobile.txBytes
                }
                weeklyCarbon[time] += rxtxMobile
            }
        }
    }

    fun getWeeklyCarbon(): MutableList<Long> {
        for (i in weeklyCarbon.indices) {
            weeklyCarbon[i] = transData(weeklyCarbon[i])
        }
        return this.weeklyCarbon
    }

    fun setMonthlyCarbon() {
        val startTimeList = timeData.getStartTimeList(1)
        // 와이파이 사용량
        for (time in startTimeList.indices) {
            for (i in listPackageInfo.indices) {
                var packageName = listPackageInfo.get(i).packageName
                var info = packageManager.getApplicationInfo(packageName, 0)
                var uid = info.uid

                val nwStatsWifi = networkStatsManager.queryDetailsForUid(
                    ConnectivityManager.TYPE_WIFI,
                    null,
                    startTimeList[time],
                    startTimeList[time] + INTERVAL_WEEK,
                    uid
                )

                val bucketWifi = NetworkStats.Bucket()
                var rxtxWifi = 0L

                while (nwStatsWifi.hasNextBucket()) {
                    nwStatsWifi.getNextBucket(bucketWifi)
                    rxtxWifi += bucketWifi.rxBytes
                    rxtxWifi += bucketWifi.txBytes
                }
                monthlyCarbon[time] += rxtxWifi
            }
        }
        // 데이터 사용량
        for (time in startTimeList.indices) {
            for (i in listPackageInfo.indices) {
                var packageName = listPackageInfo.get(i).packageName
                var info = packageManager.getApplicationInfo(packageName, 0)
                var uid = info.uid

                val nwStatsMobile = networkStatsManager.queryDetailsForUid(
                    ConnectivityManager.TYPE_MOBILE,
                    null,
                    startTimeList[time],
                    startTimeList[time] + INTERVAL_WEEK,
                    uid
                )

                val bucketMobile = NetworkStats.Bucket()
                var rxtxMobile = 0L

                while (nwStatsMobile.hasNextBucket()) {
                    nwStatsMobile.getNextBucket(bucketMobile)
                    rxtxMobile += bucketMobile.rxBytes
                    rxtxMobile += bucketMobile.txBytes
                }

                monthlyCarbon[time] += rxtxMobile
            }
        }
    }

    fun getMonthlyCarbon(): MutableList<Long> {
        for (i in monthlyCarbon.indices) {
            monthlyCarbon[i] = transData(monthlyCarbon[i])
        }
        return this.monthlyCarbon
    }

    fun setYearlyCarbon() {
        var startTimeList = timeData.getStartTimeList(2)
        for (time in startTimeList.indices) {
            for (i in listPackageInfo.indices) {
                var packageName = listPackageInfo.get(i).packageName
                var info = packageManager.getApplicationInfo(packageName, 0)
                var uid = info.uid
                val nwStatsWifi = networkStatsManager.queryDetailsForUid(
                    ConnectivityManager.TYPE_WIFI,
                    null,
                    startTimeList[time],
                    startTimeList[time] + INTERVAL_MONTH,
                    uid
                )

                val bucketWifi = NetworkStats.Bucket()
                var rxtxWifi = 0L

                while (nwStatsWifi.hasNextBucket()) {
                    nwStatsWifi.getNextBucket(bucketWifi)
                    rxtxWifi += bucketWifi.rxBytes
                    rxtxWifi += bucketWifi.txBytes
                }
                yearlyCarbon[time] += rxtxWifi
            }
        }
        for (time in startTimeList.indices) {
            for (i in listPackageInfo.indices) {
                var packageName = listPackageInfo.get(i).packageName
                var info = packageManager.getApplicationInfo(packageName, 0)
                var uid = info.uid
                val nwStatsMobile = networkStatsManager.queryDetailsForUid(
                    ConnectivityManager.TYPE_MOBILE,
                    null,
                    startTimeList[time],
                    startTimeList[time] + INTERVAL_MONTH,
                    uid
                )

                val bucketMobile = NetworkStats.Bucket()
                var rxtxMobile = 0L

                while (nwStatsMobile.hasNextBucket()) {
                    nwStatsMobile.getNextBucket(bucketMobile)
                    rxtxMobile += bucketMobile.rxBytes
                    rxtxMobile += bucketMobile.txBytes
                }
                yearlyCarbon[time] += rxtxMobile
            }
        }
    }

    fun getYearlyCarbon(): MutableList<Long> {
        for (i in yearlyCarbon.indices) {
            yearlyCarbon[i] = transData(yearlyCarbon[i])
        }
        return this.yearlyCarbon
    }

    fun transData(data: Long): Long {
        return ((data / pow(10.0, 6.0)) * 3.6).toLong()
    }
}