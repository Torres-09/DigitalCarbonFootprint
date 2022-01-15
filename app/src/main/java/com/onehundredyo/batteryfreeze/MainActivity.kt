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


val TAG = "Test"

class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.M)
    lateinit var networkStatsManager:NetworkStatsManager
    val datas = mutableListOf<ApplicationUsageData>()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        networkStatsManager =
            applicationContext.getSystemService(NETWORK_STATS_SERVICE) as NetworkStatsManager
        val packageManager = applicationContext.packageManager
        // 설치된 어플 목록
        var list:List<PackageInfo> = packageManager.getInstalledPackages(0)
        for(i in list.indices){
            var packageName = list.get(i).packageName
//                Log.d(TAG +"리스트", list.get(i).packageName)
            if(!packageName.contains("com.samsung") and !packageName.contains("com.android"))
            {
                var info = packageManager.getApplicationInfo(packageName, 0)
                var uid = info.uid
                val nwStatsWifi = networkStatsManager.queryDetailsForUid(
                    ConnectivityManager.TYPE_WIFI,
                    null,
                    System.currentTimeMillis() - 1296000000,
                    System.currentTimeMillis(),
                    uid
                )

                val bucketWifi = NetworkStats.Bucket()
                var receivedWifi = 0L
                var sentWifi = 0L

                while (nwStatsWifi.hasNextBucket()) {
                    nwStatsWifi.getNextBucket(bucketWifi)
                    receivedWifi = receivedWifi + bucketWifi.rxBytes
                    sentWifi = sentWifi + bucketWifi.txBytes
                }
                datas.add(ApplicationUsageData(uid, packageName,receivedWifi + sentWifi))
                Log.d(TAG +"결과","패키지명: ${packageName} uid: ${uid}  ##wifi: ${receivedWifi + sentWifi}" )
            }
        }
        datas.sortByDescending { it.rxtx }

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

}