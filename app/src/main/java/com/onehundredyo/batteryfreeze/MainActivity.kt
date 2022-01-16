package com.onehundredyo.batteryfreeze

import android.app.AppOpsManager
import android.app.AppOpsManager.MODE_ALLOWED
import android.app.AppOpsManager.OPSTR_GET_USAGE_STATS
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import com.onehundredyo.batteryfreeze.adapter.MainFragmentStatePagerAdapter
import com.onehundredyo.batteryfreeze.databinding.ActivityMainBinding
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
import android.content.pm.PackageManager
import com.onehundredyo.batteryfreeze.Constants.INTERVAL_DAY
import com.onehundredyo.batteryfreeze.DO.CarbonData
import java.util.*

val TAG = "Test"

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    lateinit var listPackageInfo: MutableList<PackageInfo>
    lateinit var networkStatsManager: NetworkStatsManager
    val uidCarbon = mutableMapOf<Int, Long>()   // uid 와 데이터 클래스로 이뤄진 맵
    lateinit var carbonData : CarbonData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!checkForPermission()) {
            Toast.makeText(
                this,
                "Battery Freeze 앱 권한에 동의해 주십시오.",
                Toast.LENGTH_LONG
            ).show()
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }

        _binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        configureBottomNavigation()

        //data usage
        networkStatsManager =
            applicationContext.getSystemService(NETWORK_STATS_SERVICE) as NetworkStatsManager
        val packageManager = applicationContext.packageManager
        listPackageInfo = mutableListOf()
        findPackageInfo(packageManager)
        carbonData = CarbonData(listPackageInfo,packageManager,networkStatsManager)
        carbonData.setYearlyCarbon()
        var yearlyData: MutableList<Long> = carbonData.getYearlyCarbon()
        Log.d(TAG +"연간데이터 getter 테스트", yearlyData.toString())
    }
    private fun configureBottomNavigation(){
        binding.mainFragPager.adapter = MainFragmentStatePagerAdapter(supportFragmentManager, 2)

        binding.bottomNavigation.setupWithViewPager(binding.mainFragPager)

        val bottomNaviLayout: View = this.layoutInflater.inflate(R.layout.bottom_navigation_tab, null, false)

        binding.bottomNavigation.getTabAt(0)!!.customView = bottomNaviLayout.findViewById(R.id.btn_bottom_navi_home_tab) as RelativeLayout
        binding.bottomNavigation.getTabAt(1)!!.customView = bottomNaviLayout.findViewById(R.id.btn_bottom_navi_static_tab) as RelativeLayout
    }

    private fun findPackageInfo(packMangater: PackageManager?) {
        // 설치된 어플 목록
        var list: List<PackageInfo> = packageManager.getInstalledPackages(0)
        for (i in list.indices) {
            var packageName = list.get(i).packageName
            if (!packageName.contains("com.samsung") and !packageName.contains("com.android") and !packageName.contains(
                    "com.qualcomm"
                ) and !packageName.contains("com.sec.android")
            ) {
                listPackageInfo.add(list[i])
            }
        }
    }

    private fun checkForPermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(OPSTR_GET_USAGE_STATS, Process.myUid(), packageName)
        return mode == MODE_ALLOWED
    }

    private fun getDailyDataUsage() {
        for (i in listPackageInfo.indices) {
            var packageName = listPackageInfo.get(i).packageName
//                Log.d(TAG +"리스트", list.get(i).packageName)
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

            if (!uidCarbon.containsKey(uid)) {
                uidCarbon.put(uid, rxtxWifi)
            } else {
                uidCarbon.set(uid, uidCarbon.getValue(uid) + rxtxWifi)
            }
            Log.d(
                TAG + "결과",
                "패키지명: ${packageName} uid: ${uid}  ##wifi: ${uidCarbon.getValue(uid)}"
            )
        }
    }
}