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
import android.widget.Toast
import android.app.usage.NetworkStatsManager
import android.content.ContentValues.TAG
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Log
import com.onehundredyo.batteryfreeze.DO.*
import com.onehundredyo.batteryfreeze.dataBaseHelper.DataBaseManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.fragment.app.Fragment
import com.onehundredyo.batteryfreeze.DO.CarbonData
import com.onehundredyo.batteryfreeze.fragment.*


class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    lateinit var listPackageInfo: MutableList<PackageInfo>
    lateinit var networkStatsManager: NetworkStatsManager
    lateinit var carbonData: CarbonData
    lateinit var db: DataBaseManager

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
        initNavigationBar()

        //data usage
        networkStatsManager =
            applicationContext.getSystemService(NETWORK_STATS_SERVICE) as NetworkStatsManager
        val packageManager = applicationContext.packageManager
        listPackageInfo = mutableListOf()

        findPackageInfo()

        // 아래 메소드(initiateDatabase)는 DB에 주간,월간,연간데이터를 저장하게 함
        // 실행시간 30초 예상되는 메소드임,
        // DB에 주간,월간,연간데이터를 저장하게 함
//        initiateDatabase(listPackageInfo,packageManager,networkStatsManager)

    }


    fun initNavigationBar() {
        binding.bottomNavigation.run {
            setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.page_1 -> {
                        changeFragment(HomeFragment())
                    }
                    R.id.page_2 -> {
                        changeFragment(StaticFragment())
                    }
                }
                true
            }
            selectedItemId = R.id.page_1
        }
    }

    fun changeFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.mainFragPager.id, fragment).commit()

    }

    private fun findPackageInfo() {
        // 설치된 어플 목록
        var list: List<PackageInfo> = packageManager.getInstalledPackages(0)
        for (i in list.indices) {
            var packageName = list.get(i).packageName
            if (!packageName.contains("com.samsung") and !packageName.contains("com.android")
                and !packageName.contains("com.qualcomm") and !packageName.contains("com.sec.android")
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


    // initiateDatabase 는 실행할 때 마다 기존데이터 덮어쓰게됨
    private fun initiateDatabase(
        listPackageInfo: MutableList<PackageInfo>,
        packageManager: PackageManager,
        networkStatsManager: NetworkStatsManager
    ) {
        carbonData = CarbonData(listPackageInfo, packageManager, networkStatsManager)
        carbonData.setWeeklyCarbon()
        carbonData.setMonthlyCarbon()
        carbonData.setYearlyCarbon()

        // 리스트 형태로 주,월,연간 데이터 받아온다
        val weeklyCarbon: MutableList<Long> = carbonData.getWeeklyCarbon()
        val monthlyCarbon: MutableList<Long> = carbonData.getMonthlyCarbon()
        val yearlyCarbon: MutableList<Long> = carbonData.getYearlyCarbon()

        // 메인쓰레드 사용(권장하지 않음)
//        val db = Room.databaseBuilder(
//            applicationContext,
//            DataBaseManager::class.java,
//            "databasemanager"
//        ).allowMainThreadQueries() 
//            .build()

        //코루틴 사용(IO 쓰레드 사용)
        db = DataBaseManager.getInstance(applicationContext)!!

        CoroutineScope(Dispatchers.IO).launch {
            for (weekData in weeklyCarbon.indices) {
                Log.d(TAG + "INSERT: ", "${weekData} 번째 요일 / 사용량:${weeklyCarbon[weekData]}")
                db!!.DatausageDAO().insertWeekData(WeeklyInfo(weekData, weeklyCarbon[weekData]))
            }
            for (monthData in monthlyCarbon.indices) {
                Log.d(TAG + "INSERT: ", "${monthData} 주 / 사용량:${monthlyCarbon[monthData]}")
                db!!.DatausageDAO()
                    .insertMonthData(MonthlyInfo(monthData, monthlyCarbon[monthData]))
            }
            for (yearData in yearlyCarbon.indices) {
                Log.d(TAG + "INSERT: ", "${yearData} 달 / 사용량:${yearlyCarbon[yearData]}")
                db!!.DatausageDAO().insertYearData(YearlyInfo(yearData, yearlyCarbon[yearData]))
            }
        }
    }

}