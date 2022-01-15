package com.onehundredyo.batteryfreeze

import android.app.AppOpsManager
import android.app.AppOpsManager.MODE_ALLOWED
import android.app.AppOpsManager.OPSTR_GET_USAGE_STATS
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import java.lang.Thread.sleep

import android.net.ConnectivityManager

import android.app.usage.NetworkStats
import android.app.usage.NetworkStatsManager
import android.os.Build
import androidx.annotation.RequiresApi
import android.content.pm.PackageInfo
import java.util.*


val TAG = "Test"

class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.M)
    lateinit var networkStatsManager: NetworkStatsManager
    val datas = mutableListOf<ApplicationUsageData>()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        networkStatsManager =
            applicationContext.getSystemService(NETWORK_STATS_SERVICE) as NetworkStatsManager
        val packageManager = applicationContext.packageManager
        // 설치된 어플 목록
        var list: List<PackageInfo> = packageManager.getInstalledPackages(0)
        getDataUsageWithPeriod(list, 1, 0)     // getDataUsageWithPeriod(list, time, type)
        datas.sortByDescending { it.rxtx }       // 데이터 사용량 기준 정렬
    }

    override fun onStart() {
        if (!checkForPermission()) {
            Log.i(TAG, "The user may not allow the access to apps usage. ")
            Toast.makeText(
                this,
                "Failed to retrieve app usage statistics. " +
                        "You may need to enable access for this app through " +
                        "Settings > Security > Apps with usage access",
                Toast.LENGTH_LONG
            ).show()
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        } else {

        }
        super.onStart()
    }

    private fun checkForPermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(OPSTR_GET_USAGE_STATS, Process.myUid(), packageName)
        return mode == MODE_ALLOWED
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getDataUsageWithPeriod(list: List<PackageInfo>, time: Int, type: Int) {
        // Time 1은 오늘 00시부터
        // Time 2는 이번주 월요일 00시부터
        // Time 3은 이번달 1일 00시부터
        // Time 4는 올해 1월 1일 00시부터

        // type 0은 데이터
        // type 1는 와이파이

        var start_time = 0L
        if (time == 1) {
            // 일간
            start_time = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time.time

        } else if (time == 2) {
            // 주간
            start_time = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time.time
        } else if (time == 3) {
            // 월간
            start_time = Calendar.getInstance().apply {
                set(Calendar.DATE, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time.time

        } else if (time == 4) {
            // 연간
            start_time = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_YEAR, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time.time
        } else {
            Log.d(TAG, "getDataUsageWithPeriod TIME 설정이 잘못되었습니다")
        }
        for (i in list.indices) {
            var packageName = list.get(i).packageName
            if (!packageName.contains("com.samsung") and !packageName.contains("com.android")) {
                var info = packageManager.getApplicationInfo(packageName, 0)
                var uid = info.uid
                val nwStatsWifi = networkStatsManager.queryDetailsForUid(
                    type,
                    null,
                    start_time,
                    System.currentTimeMillis(),
                    uid
                )

                val bucketWifi = NetworkStats.Bucket()
                var receivedWifi = 0L
                var sentWifi = 0L

                while (nwStatsWifi.hasNextBucket()) {
                    nwStatsWifi.getNextBucket(bucketWifi)
                    receivedWifi +=  bucketWifi.rxBytes
                    sentWifi += bucketWifi.txBytes
                }
                datas.add(ApplicationUsageData(uid, packageName, receivedWifi + sentWifi))
                Log.d(
                    TAG + "결과",
                    "패키지명: ${packageName} uid: ${uid}  ##usage: ${receivedWifi + sentWifi}"
                )
            }
        }
    }
}