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
import android.widget.Toast
import android.app.usage.NetworkStatsManager
import android.content.pm.PackageInfo
import androidx.fragment.app.Fragment
import com.onehundredyo.batteryfreeze.DO.CarbonData
import com.onehundredyo.batteryfreeze.fragment.*

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    lateinit var listPackageInfo: MutableList<PackageInfo>
    lateinit var networkStatsManager: NetworkStatsManager
    lateinit var carbonData: CarbonData

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
}