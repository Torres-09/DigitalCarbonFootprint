package com.onehundredyo.batteryfreeze.DO

import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.util.Log
import com.onehundredyo.batteryfreeze.Constants.*


val TAG = "CARBON"


class CarbonData {
    var daily_cabon: MutableMap<String, Long>
    var weekly_cabon: MutableList<Long>
    var monthly_cabon: MutableList<Long>
    var yearly_cabon: MutableList<Long>
    val listPackageInfo: MutableList<PackageInfo>
    val packageManager: PackageManager
    val networkStatsManager: NetworkStatsManager
    var time_data: TimeData

    constructor(
        listPackageInfo: MutableList<PackageInfo>,
        packageManager: PackageManager,
        networkStatsManager: NetworkStatsManager
    ) {
        this.listPackageInfo = listPackageInfo
        this.packageManager = packageManager
        this.networkStatsManager = networkStatsManager
        daily_cabon = mutableMapOf()
        weekly_cabon = mutableListOf(0,0,0,0,0,0,0)
        monthly_cabon = mutableListOf(0,0,0,0)
        yearly_cabon = mutableListOf(0,0,0,0,0,0,0,0,0,0,0,0)
        time_data = TimeData()
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

            if (!daily_cabon.containsKey(packageName)) {
                daily_cabon.put(packageName, rxtxWifi)
            } else {
                daily_cabon.set(packageName, daily_cabon.getValue(packageName) + rxtxWifi)
            }
            Log.d(
                TAG + "결과",
                "패키지명: ${packageName} uid: ${uid}  ##wifi: ${daily_cabon.getValue(packageName)}"
            )
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

            if (!daily_cabon.containsKey(packageName)) {
                daily_cabon.put(packageName, rxtxMobile)
            } else {
                daily_cabon.set(packageName, daily_cabon.getValue(packageName) + rxtxMobile)
            }
            Log.d(
                TAG + "결과",
                "패키지명: ${packageName} uid: ${uid}  ##sum: ${daily_cabon.getValue(packageName)}"
            )
        }
    }
    fun getDailyCarbon(): MutableMap<String,Long>{
        return this.daily_cabon
    }
    fun setWeeklyCarbon(){
        var start_time_list = time_data.getStartTimeList(0)
        var index = 0
        for (time in start_time_list){
            for (i in listPackageInfo.indices) {
                var packageName = listPackageInfo.get(i).packageName
                var info = packageManager.getApplicationInfo(packageName, 0)
                var uid = info.uid
                val nwStatsWifi = networkStatsManager.queryDetailsForUid(
                    ConnectivityManager.TYPE_WIFI,     //TYPE_MOBILE 시 데이터 사용량
                    null,
                    time,                                                   // EPOCH TIME 으로 시작시간 지정(15일이전부터)
                    time + INTERVAL_DAY,                            // EPOCH TIME 으로 종료시간 지정(지금 시간)
                    uid
                )

                val bucketWifi = NetworkStats.Bucket()
                var rxtxWifi = 0L

                while (nwStatsWifi.hasNextBucket()) {
                    nwStatsWifi.getNextBucket(bucketWifi)
                    rxtxWifi += bucketWifi.rxBytes
                    rxtxWifi += bucketWifi.txBytes
                }
                weekly_cabon[index] += rxtxWifi
            }

            Log.d(
                TAG + "결과",
                "패키지명: ${index}  ##[time]: ${time} ##wifi: ${weekly_cabon[index]}"
            )
            index += 1
        }
        index = 0
        for (time in start_time_list){
            for (i in listPackageInfo.indices) {
                var packageName = listPackageInfo.get(i).packageName
                var info = packageManager.getApplicationInfo(packageName, 0)
                var uid = info.uid
                val nwStatsMobile = networkStatsManager.queryDetailsForUid(
                    ConnectivityManager.TYPE_MOBILE,     //TYPE_MOBILE 시 데이터 사용량
                    null,
                    time,                                                   // EPOCH TIME 으로 시작시간 지정(15일이전부터)
                    time + INTERVAL_DAY,                            // EPOCH TIME 으로 종료시간 지정(지금 시간)
                    uid
                )

                val bucketMobile = NetworkStats.Bucket()
                var rxtxMobile = 0L

                while (nwStatsMobile.hasNextBucket()) {
                    nwStatsMobile.getNextBucket(bucketMobile)
                    rxtxMobile += bucketMobile.rxBytes
                    rxtxMobile += bucketMobile.txBytes
                }
                weekly_cabon[index] += rxtxMobile
            }

            Log.d(
                TAG + "결과",
                "패키지명: ${index}  ##[time]: ${time} ##SUM: ${weekly_cabon[index]}"
            )
            index += 1
        }
    }
    fun getWeeklyCarbon(): MutableList<Long>{
        return this.weekly_cabon
    }
    fun setMonthlyCarbon() {
        val start_time_list = time_data.getStartTimeList(1)
//        Log.d(TAG + "리스트", start_time_list.toString())
        // 와이파이 사용량
        for (time in start_time_list.indices) {
            for (i in listPackageInfo.indices) {
                var packageName = listPackageInfo.get(i).packageName
                var info = packageManager.getApplicationInfo(packageName, 0)
                var uid = info.uid

                val nwStatsWifi = networkStatsManager.queryDetailsForUid(
                    ConnectivityManager.TYPE_WIFI,     //TYPE_MOBILE 시 데이터 사용량
                    null,
                    start_time_list[time],                  // EPOCH TIME 으로 시작시간 지정
                    start_time_list[time] + INTERVAL_WEEK,                            // EPOCH TIME 으로 종료시간 지정(지금 시간)
                    uid
                )

                val bucketWifi = NetworkStats.Bucket()
                var rxtxWifi = 0L

                while (nwStatsWifi.hasNextBucket()) {
                    nwStatsWifi.getNextBucket(bucketWifi)
                    rxtxWifi += bucketWifi.rxBytes
                    rxtxWifi += bucketWifi.txBytes
                }
                monthly_cabon[time] += rxtxWifi
            }
            Log.d(
                TAG + "결과",
                "패키지명: ${time}  ##[time]: ${start_time_list[time]} ##wifi: ${monthly_cabon[time]}"
            )
        }
        // 데이터 사용량
        for (time in start_time_list.indices) {
            for (i in listPackageInfo.indices) {
                var packageName = listPackageInfo.get(i).packageName
                var info = packageManager.getApplicationInfo(packageName, 0)
                var uid = info.uid

                val nwStatsMobile = networkStatsManager.queryDetailsForUid(
                    ConnectivityManager.TYPE_MOBILE,     //TYPE_MOBILE 시 데이터 사용량
                    null,
                    start_time_list[time],                  // EPOCH TIME 으로 시작시간 지정
                    start_time_list[time] + INTERVAL_WEEK,                            // EPOCH TIME 으로 종료시간 지정(지금 시간)
                    uid
                )

                val bucketMobile = NetworkStats.Bucket()
                var rxtxMobile = 0L

                while (nwStatsMobile.hasNextBucket()) {
                    nwStatsMobile.getNextBucket(bucketMobile)
                    rxtxMobile += bucketMobile.rxBytes
                    rxtxMobile += bucketMobile.txBytes
                }

                monthly_cabon[time] += rxtxMobile
            }
            Log.d(
                TAG + "결과",
                "패키지명: ${time}  ##[time]: ${start_time_list[time]} ##SUM: ${monthly_cabon[time]}"
            )
        }
    }
    fun getMonthlyCarbon(): MutableList<Long>{
        return this.monthly_cabon
    }
    fun setYearlyCarbon() {
        var start_time_list = time_data.getStartTimeList(2)
        var index = 0
        for (time in start_time_list){
            for (i in listPackageInfo.indices) {
                var packageName = listPackageInfo.get(i).packageName
                var info = packageManager.getApplicationInfo(packageName, 0)
                var uid = info.uid
                val nwStatsWifi = networkStatsManager.queryDetailsForUid(
                    ConnectivityManager.TYPE_WIFI,     //TYPE_MOBILE 시 데이터 사용량
                    null,
                    time,                                                   // EPOCH TIME 으로 시작시간 지정(15일이전부터)
                    time + INTERVAL_MONTH,                            // EPOCH TIME 으로 종료시간 지정(지금 시간)
                    uid
                )

                val bucketWifi = NetworkStats.Bucket()
                var rxtxWifi = 0L

                while (nwStatsWifi.hasNextBucket()) {
                    nwStatsWifi.getNextBucket(bucketWifi)
                    rxtxWifi += bucketWifi.rxBytes
                    rxtxWifi += bucketWifi.txBytes
                }
                yearly_cabon[index] += rxtxWifi
            }

            Log.d(
                TAG + "결과",
                "패키지명: ${index}  ##[time]: ${time} ##wifi: ${yearly_cabon[index]}"
            )
            index += 1
        }
        index = 0
        for (time in start_time_list){
            for (i in listPackageInfo.indices) {
                var packageName = listPackageInfo.get(i).packageName
                var info = packageManager.getApplicationInfo(packageName, 0)
                var uid = info.uid
                val nwStatsMobile = networkStatsManager.queryDetailsForUid(
                    ConnectivityManager.TYPE_MOBILE,     //TYPE_MOBILE 시 데이터 사용량
                    null,
                    time,                                                   // EPOCH TIME 으로 시작시간 지정(15일이전부터)
                    time + INTERVAL_MONTH,                            // EPOCH TIME 으로 종료시간 지정(지금 시간)
                    uid
                )

                val bucketMobile = NetworkStats.Bucket()
                var rxtxMobile = 0L

                while (nwStatsMobile.hasNextBucket()) {
                    nwStatsMobile.getNextBucket(bucketMobile)
                    rxtxMobile += bucketMobile.rxBytes
                    rxtxMobile += bucketMobile.txBytes
                }
                yearly_cabon[index] += rxtxMobile
            }

            Log.d(
                TAG + "결과",
                "패키지명: ${index}  ##[time]: ${time} ##SUM: ${yearly_cabon[index]}"
            )
            index += 1
        }
    }
    fun getYearlyCarbon(): MutableList<Long>{
        return this.yearly_cabon
    }
}