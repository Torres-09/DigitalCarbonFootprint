package com.onehundredyo.batteryfreeze.customs

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent

import androidx.viewpager.widget.ViewPager


class CustomViewPager(context: Context, attrs: AttributeSet?) :
    ViewPager(context, attrs) {
    private var swip_enabled = true
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (swip_enabled) {
            super.onTouchEvent(event)
        } else false
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return if (swip_enabled) {
            super.onInterceptTouchEvent(event)
        } else false
    }

    fun setPagingEnabled(enabled: Boolean) {
        this.swip_enabled = enabled
    }
}