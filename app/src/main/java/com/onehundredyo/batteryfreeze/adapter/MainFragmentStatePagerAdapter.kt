package com.onehundredyo.batteryfreeze.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.onehundredyo.batteryfreeze.fragment.HomeFragment
import com.onehundredyo.batteryfreeze.fragment.StatisitcsFragment

class MainFragmentStatePagerAdapter(fm : FragmentManager, val fragmentCount : Int) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        when(position){
            0 -> return HomeFragment()
            1 -> return StatisitcsFragment()
            else -> return HomeFragment()
        }
    }

    override fun getCount(): Int = fragmentCount

}