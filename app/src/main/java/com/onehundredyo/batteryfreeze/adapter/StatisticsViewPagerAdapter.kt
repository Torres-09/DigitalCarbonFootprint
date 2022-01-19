package com.onehundredyo.batteryfreeze.adapter

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.viewpager.widget.PagerAdapter
import com.google.android.material.button.MaterialButtonToggleGroup
import com.onehundredyo.batteryfreeze.MainActivity
import com.onehundredyo.batteryfreeze.R


class StatisticsViewPagerAdapter : PagerAdapter() {
//    private var mContext: Context? = null
//    private var mainActivity: MainActivity? = null
//    private var weekButton: Button?= null
//    private var monthButton: Button?= null
//    private var yearButton: Button?= null

    fun StatisticsViewPagerAdapter(
//        context: Context
    ) {
//        mContext = context
//        mainActivity = context as MainActivity
//        weekButton = mainActivity!!.findViewById<Button>(R.id.weekbutton)
//        monthButton = mainActivity!!.findViewById<Button>(R.id.monthbutton)
//        yearButton = mainActivity!!.findViewById<Button>(R.id.yearbutton)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var resId = 0
        when (position) {
            0 -> {
                resId = R.id.weeklybarchart
//                weekButton?.isSelected = true
//                monthButton?.isSelected = false
//                yearButton?.isSelected = false
            }
            1 -> {
                resId = R.id.monthlybarchart
//                weekButton?.isSelected = false
//                monthButton?.isSelected = true
//                yearButton?.isSelected = false
            }
            2 -> {
                resId = R.id.yearlybarchart
//                weekButton?.isSelected = false
//                monthButton?.isSelected = false
//                yearButton?.isSelected = true
            }
        }
        return container.findViewById(resId)
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View?)
    }

    override fun getCount(): Int {
        return 3
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return (view == `object`)
    }



}