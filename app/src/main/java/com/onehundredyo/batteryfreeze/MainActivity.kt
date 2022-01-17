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
import android.content.pm.PackageInfo
import com.onehundredyo.batteryfreeze.DO.CarbonData


class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    lateinit var listPackageInfo: MutableList<PackageInfo>
    lateinit var networkStatsManager: NetworkStatsManager
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

        findPackageInfo()

//        carbonData = CarbonData(listPackageInfo,packageManager,networkStatsManager)
//        carbonData.setYearlyCarbon()
//
//        var yearlyData: MutableList<Long> = carbonData.getYearlyCarbon()
    }
    private fun configureBottomNavigation(){
        binding.mainFragPager.adapter = MainFragmentStatePagerAdapter(supportFragmentManager, 2)

        binding.bottomNavigation.setupWithViewPager(binding.mainFragPager)

        val bottomNaviLayout: View = this.layoutInflater.inflate(R.layout.bottom_navigation_tab, null, false)

        binding.bottomNavigation.getTabAt(0)!!.customView = bottomNaviLayout.findViewById(R.id.btn_bottom_navi_home_tab) as RelativeLayout
        binding.bottomNavigation.getTabAt(1)!!.customView = bottomNaviLayout.findViewById(R.id.btn_bottom_navi_static_tab) as RelativeLayout
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
}