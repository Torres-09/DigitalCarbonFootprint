package com.onehundredyo.batteryfreeze

import android.Manifest
import android.app.ActivityManager
import android.app.AppOpsManager
import android.app.AppOpsManager.MODE_ALLOWED
import android.app.AppOpsManager.OPSTR_GET_USAGE_STATS
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.net.TrafficStats
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import com.onehundredyo.batteryfreeze.adapter.ProcessViewHolderAdapter
import java.lang.Thread.sleep
import java.util.*
import android.telephony.TelephonyManager

import android.net.ConnectivityManager

import android.app.usage.NetworkStats
import android.os.RemoteException
import android.app.usage.NetworkStatsManager
import android.content.pm.PackageManager
import android.nfc.Tag
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import java.security.AccessController.getContext
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo


val TAG = "Test"

class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.M)
    lateinit var networkStatsManager:NetworkStatsManager
    lateinit var processViewHolderAdapter: ProcessViewHolderAdapter
    val datas = mutableListOf<ProcessData>()

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
                Log.d(TAG +"결과","패키지명: ${packageName} uid: ${uid}  ##wifi: ${receivedWifi + sentWifi}" )
            }

        }

        initRecycler()
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

    private fun initRecycler() {
        sleep(5000)
        processViewHolderAdapter = ProcessViewHolderAdapter(this)
        datas.apply {
            add(ProcessData(name = "Test1", rx = 4545, tx = 45450))
            add(ProcessData(name = "Test2", rx = 4545, tx = 45450))
            add(ProcessData(name = "Test3", rx = 4545, tx = 45450))
        }
        processViewHolderAdapter.datas = datas
        processViewHolderAdapter.notifyDataSetChanged()
    }

    private fun checkForPermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(OPSTR_GET_USAGE_STATS, Process.myUid(), packageName)
        return mode == MODE_ALLOWED
    }




}