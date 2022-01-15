package com.onehundredyo.batteryfreeze

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import com.onehundredyo.batteryfreeze.adapter.MainFragmentStatePagerAdapter
import com.onehundredyo.batteryfreeze.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        configureBottomNavigation()
    }

    private fun configureBottomNavigation(){
        binding.mainFragPager.adapter = MainFragmentStatePagerAdapter(supportFragmentManager, 2)

        binding.bottomNavigation.setupWithViewPager(binding.mainFragPager)

        val bottomNaviLayout: View = this.layoutInflater.inflate(R.layout.bottom_navigation_tab, null, false)

        binding.bottomNavigation.getTabAt(0)!!.customView = bottomNaviLayout.findViewById(R.id.btn_bottom_navi_home_tab) as RelativeLayout
        binding.bottomNavigation.getTabAt(1)!!.customView = bottomNaviLayout.findViewById(R.id.btn_bottom_navi_static_tab) as RelativeLayout
    }
}