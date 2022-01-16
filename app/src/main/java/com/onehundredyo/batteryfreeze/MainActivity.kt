package com.onehundredyo.batteryfreeze

import android.app.AppOpsManager
import android.app.AppOpsManager.MODE_ALLOWED
import android.app.AppOpsManager.OPSTR_GET_USAGE_STATS
import android.app.Application
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
import java.util.*

val TAG = "Test"
val INTERVAL_DAY: Long = 60 * 60 * 24 * 1000L
val INTERVAL_WEEK: Long = INTERVAL_DAY * 7L
val INTERVAL_MONTH: Long = INTERVAL_WEEK * 4L
val INTERVAL_YEAR: Long = INTERVAL_DAY * 365

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    lateinit var listPackageInfo: MutableList<PackageInfo>

    lateinit var networkStatsManager: NetworkStatsManager
    val uidCarbon = mutableMapOf<Int, Long>()   // uid 와 데이터 클래스로 이뤄진 맵

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

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
        }

        configureBottomNavigation()

        // data usage
        networkStatsManager =
            applicationContext.getSystemService(NETWORK_STATS_SERVICE) as NetworkStatsManager
        val packageManager = applicationContext.packageManager
        listPackageInfo = mutableListOf()
        findPackageInfo(packageManager)

        getDailyDataUsage()
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
//        private fun getDataUsageWithPeriod(list: List<PackageInfo>, time: Int, type: Int) {
//            val INTERVAL_DAY: Long = 60 * 60 * 24 * 1000L
//            val INTERVAL_WEEK: Long = INTERVAL_DAY * 7L
//            val INTERVAL_MONTH: Long = INTERVAL_WEEK * 4L
//            val INTERVAL_YEAR: Long = INTERVAL_DAY * 365
//            val INTERVAL_MAP =
//                mapOf(
//                    2 to INTERVAL_DAY,
//                    3 to INTERVAL_WEEK,
//                    4 to INTERVAL_MONTH,
//                    5 to INTERVAL_YEAR
//                )
//            // returns 1등/데이터사용량 , 2등/데이터 사용량, 3위/데이터사용량, 기타/데이터사용량
//            // Time 1은 오늘 00시부터 현시각까지
//            // Time 2는 이번주 월요일 0~24, 화요일 0~24, ..., 일요일 0~24
//            // Time 3은 1월 1일 00시부터~1월말, 2월, 3월,...,12월
//            // Time 4는 올해 1월 1일 00시부터 12월 31일까지
//
//            // type 0은 데이터
//            // type 1는 와이파이
//
//            var start_time = 0L
//            val start_time_list = mutableListOf<Long>()
//            if (time == 1) {
//                // 일간
//                start_time = Calendar.getInstance().apply {
//                    set(Calendar.HOUR_OF_DAY, 0)
//                    set(Calendar.MINUTE, 0)
//                    set(Calendar.SECOND, 0)
//                    set(Calendar.MILLISECOND, 0)
//                }.time.time
//            } else if (time == 2) {
//                // 주간 사용량
//                // 일 == 1 / 월 == 2 / ... / 토 == 7
//                for (i: Int in 1..7) {
//                    start_time_list.add(Calendar.getInstance().apply {
//                        set(Calendar.DAY_OF_WEEK, i)
//                        set(Calendar.HOUR_OF_DAY, 0)
//                        set(Calendar.MINUTE, 0)
//                        set(Calendar.SECOND, 0)
//                        set(Calendar.MILLISECOND, 0)
//                    }.time.time)
//                }
//            } else if (time == 3) {
//                //  월간 1주 ~ 4주  사용량
//                val this_week_sunday = Calendar.getInstance().apply {
//                    set(Calendar.DAY_OF_WEEK, 1)
//                    set(Calendar.HOUR_OF_DAY, 0)
//                    set(Calendar.MINUTE, 0)
//                    set(Calendar.SECOND, 0)
//                    set(Calendar.MILLISECOND, 0)
//                }.time.time
//                // this_week_sunday 에 이번주 일요일 0시0분0초 저장
//                // 저번주: 이번주 일요일 - 1주일 시간
//                // 저저번주: 이번주 일요일 - 2주일 시간
//                // 저저저번주: 이번주 일요일 - 3주일 시간
//                for (i: Int in 3..0) {
//                    start_time_list.add(this_week_sunday - i * INTERVAL_WEEK)
//                }
//            } else if (time == 4) {
//                //  연간 (1월~12월) 사용량
//                for (i: Int in 0..11) {
//                    start_time_list.add(Calendar.getInstance().apply {
//                        set(Calendar.MONTH, i)
//                        set(Calendar.DATE, 1)
//                        set(Calendar.HOUR_OF_DAY, 0)
//                        set(Calendar.MINUTE, 0)
//                        set(Calendar.SECOND, 0)
//                        set(Calendar.MILLISECOND, 0)
//                    }.time.time)
//                }
//            } else if (time == 5) {
//                // 연도별 사용량, 올해부터 3개년
//                val year = Calendar.getInstance().get(Calendar.YEAR)
//                for (i: Int in 2..0) {
//                    start_time_list.add(
//                        Calendar.getInstance().apply {
//                            set(Calendar.YEAR, year - i)
//                            set(Calendar.DAY_OF_YEAR, 1)
//                            set(Calendar.HOUR_OF_DAY, 0)
//                            set(Calendar.MINUTE, 0)
//                            set(Calendar.SECOND, 0)
//                            set(Calendar.MILLISECOND, 0)
//                        }.time.time
//                    )
//                }
//            } else {
//                Log.d(TAG, "getDataUsageWithPeriod TIME 설정이 잘못되었습니다")
//            }
//            if (time != 1) {
//                // 시작 시간 기준으로
//                for (j in start_time_list.indices) {
//                    for (i in list.indices) {
//                        var packageName = list.get(i).packageName
//                        if (!packageName.contains("com.samsung") and !packageName.contains("com.android")) {
//                            var info = packageManager.getApplicationInfo(packageName, 0)
//                            var uid = info.uid
//                            val nwStatsWifi = networkStatsManager.queryDetailsForUid(
//                                type,
//                                null,
//                                start_time_list[j],
//                                start_time_list[j] + INTERVAL_MAP[time]!!,
//                                uid
//                            )
//                            val bucketWifi = NetworkStats.Bucket()
//                            var receivedWifi = 0L
//                            var sentWifi = 0L
//                            while (nwStatsWifi.hasNextBucket()) {
//                                nwStatsWifi.getNextBucket(bucketWifi)
//                                receivedWifi += bucketWifi.rxBytes
//                                sentWifi += bucketWifi.txBytes
//                            }
//                            // UID 별로 MAP에 저장
//                            Log.d(
//                                TAG + "결과",
//                                "패키지명: ${packageName} uid: ${uid}  ##usage: ${receivedWifi + sentWifi}"
//                            )
//                        }
//                    }
//                }
//            } else {
//                // 일일사용량을 원하는 경우
//                for (i in list.indices) {
//                    var packageName = list.get(i).packageName
//                    if (!packageName.contains("com.samsung") and !packageName.contains("com.android") and !packageName.contains(
//                            "com.qualcomm"
//                        ) and !packageName.contains("com.sec.android")
//                    ) {
//                        var info = packageManager.getApplicationInfo(packageName, 0)
//                        var uid = info.uid
//                        val nwStatsWifi = networkStatsManager.queryDetailsForUid(
//                            type,
//                            null,
//                            start_time,
//                            start_time + INTERVAL_DAY,
//                            uid
//                        )
//                        val bucketWifi = NetworkStats.Bucket()
//                        var receivedWifi = 0L
//                        var sentWifi = 0L
//                        while (nwStatsWifi.hasNextBucket()) {
//                            nwStatsWifi.getNextBucket(bucketWifi)
//                            receivedWifi += bucketWifi.rxBytes
//                            sentWifi += bucketWifi.txBytes
//                        }
//                        Log.d(
//                            TAG + "결과",
//                            "패키지명: ${packageName} uid: ${uid}  ##usage: ${receivedWifi + sentWifi}"
//                        )
//                    }
//                }
//            }
//        }
}