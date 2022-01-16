package com.onehundredyo.batteryfreeze.DO

import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import com.onehundredyo.batteryfreeze.Constants.*

class CarbonData {
    var dailyCabon: MutableMap<String, Long>
    var weeklyCabon: MutableList<Long>
    var monthlyCabon: MutableList<Long>
    var yearlyCabon: MutableList<Long>
    val listPackageInfo: MutableList<PackageInfo>
    val packageManager: PackageManager
    val networkStatsManager: NetworkStatsManager
    var timeData: TimeData

    constructor(
        listPackageInfo: MutableList<PackageInfo>,
        packageManager: PackageManager,
        networkStatsManager: NetworkStatsManager
    ) {
        this.listPackageInfo = listPackageInfo
        this.packageManager = packageManager
        this.networkStatsManager = networkStatsManager
        dailyCabon = mutableMapOf()
        weeklyCabon = mutableListOf(0, 0, 0, 0, 0, 0, 0)
        monthlyCabon = mutableListOf(0, 0, 0, 0)
        yearlyCabon = mutableListOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        timeData = TimeData()
    }

    fun setDailyCarbon() {
        for (i in listPackageInfo.indices) {
            var packageName = listPackageInfo.get(i).packageName
            var info = packageManager.getApplicationInfo(packageName, 0)
            var uid = info.uid
            val nwStatsWifi = networkStatsManager.queryDetailsForUid(
                ConnectivityManager.TYPE_WIFI,     //TYPE_MOBILE 시 데이터 사용량
                null,
                System.currentTimeMillis() - INTERVAL_DAY,     // EPOCH TIME 으로 시작시간 지정(15일이전부터)
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

            if (!dailyCabon.containsKey(packageName)) {
                dailyCabon.put(packageName, rxtxWifi)
            } else {
                dailyCabon.set(packageName, dailyCabon.getValue(packageName) + rxtxWifi)
            }
        }
        for (i in listPackageInfo.indices) {
            var packageName = listPackageInfo.get(i).packageName
            var info = packageManager.getApplicationInfo(packageName, 0)
            var uid = info.uid
            val nwStatsWifi = networkStatsManager.queryDetailsForUid(
                ConnectivityManager.TYPE_MOBILE,     //TYPE_MOBILE 시 데이터 사용량
                null,
                System.currentTimeMillis() - INTERVAL_DAY,     // EPOCH TIME 으로 시작시간 지정(15일이전부터)
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

            if (!dailyCabon.containsKey(packageName)) {
                dailyCabon.put(packageName, rxtxMobile)
            } else {
                dailyCabon.set(packageName, dailyCabon.getValue(packageName) + rxtxMobile)
            }
        }
    }

    fun getDailyCarbon(): MutableMap<String, Long> {
        return this.dailyCabon
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
                weeklyCabon[time] += rxtxWifi
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
                weeklyCabon[time] += rxtxMobile
            }
        }
    }

    fun getWeeklyCarbon(): MutableList<Long> {
        return this.weeklyCabon
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
                monthlyCabon[time] += rxtxWifi
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

                monthlyCabon[time] += rxtxMobile
            }
        }
    }

    fun getMonthlyCarbon(): MutableList<Long> {
        return this.monthlyCabon
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
                yearlyCabon[time] += rxtxWifi
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
                yearlyCabon[time] += rxtxMobile
            }
        }
    }

    fun getYearlyCarbon(): MutableList<Long> {
        return this.yearlyCabon
    }
}