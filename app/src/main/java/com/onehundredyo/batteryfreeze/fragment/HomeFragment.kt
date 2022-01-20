package com.onehundredyo.batteryfreeze.fragment

import android.Manifest
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.onehundredyo.batteryfreeze.R
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.onehundredyo.batteryfreeze.App
import com.onehundredyo.batteryfreeze.MainActivity
import com.onehundredyo.batteryfreeze.databinding.FragmentHomeBinding
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDate

class HomeFragment : Fragment() {
    private var remainPercentage: Long? = 0L
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding
    private var mContext: Context? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
        if (activity != null && activity is MainActivity) {
            remainPercentage = (activity as MainActivity?)?.getTotalDailyCarbon()
        }
    }

    fun compareDate(): Boolean {
        var currentDate: String = LocalDate.now().toString()
        val savedDate: String = App.prefs.getSavedDate("savedDate", "")
        if (currentDate != savedDate) {
            App.prefs.setSavedDate("savedDate", currentDate)
            return false
        } else
            return true
    }

    fun setChatText(view: View, level: Int) {
        val chatBubbleText: TextView = view.findViewById(R.id.chat_bubble_text)
        var randomNumber = (0..2).random()
        when (level) {
            4 -> chatBubbleText.setText(resources.getStringArray(R.array.chat_list4)[randomNumber])
            3 -> chatBubbleText.setText(resources.getStringArray(R.array.chat_list3)[randomNumber])
            2 -> chatBubbleText.setText(resources.getStringArray(R.array.chat_list2)[randomNumber])
            1 -> chatBubbleText.setText(resources.getStringArray(R.array.chat_list1)[randomNumber])
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        Log.d("homefragment", "yeah${remainPercentage}")
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val glacier: ImageView = view.findViewById(R.id.glacier)
        val polarbear: ImageView = view.findViewById(R.id.polarbear)
        val remainText: TextView = view.findViewById(R.id.remainText)
        val chatBubbleText: TextView = view.findViewById(R.id.chat_bubble_text)
        val chatBubble: ImageView = view.findViewById(R.id.chat_bubble)
        val todayGoalText: TextView = view.findViewById(R.id.today_goal_text)
        val chatButton: ImageView = view.findViewById(R.id.chat_button)

        //퍼센트에 따른 변화
        var randomNumber = (0..2).random()
        lateinit var textArray: Array<String>
        var dailyCarbonDouble = (remainPercentage)?.toDouble()?.div(1000)
        var dailyCarbonInt = (remainPercentage)?.toDouble()?.div(1000)?.toInt()
        // 배출된 이산화탄소 양
        when (dailyCarbonInt) {
            in 0..4 -> {
                glacier.setImageResource(R.drawable.glacier_4)
                polarbear.setImageResource(R.drawable.polarbear_4)

                setChatText(view, 4)

                chatButton.setOnClickListener { setChatText(view, 4) }
                ObjectAnimator.ofFloat(chatBubble, "translationY", -300f).apply { start() }
                ObjectAnimator.ofFloat(chatBubble, "translationX", -50f).apply { start() }
                ObjectAnimator.ofFloat(chatBubbleText, "translationY", -300f).apply { start() }
                ObjectAnimator.ofFloat(chatBubbleText, "translationX", -50f).apply { start() }
            }
            in 5..9 -> {
                glacier.setImageResource(R.drawable.glacier_3)
                polarbear.setImageResource(R.drawable.polarbear_3)

                setChatText(view, 3)

                chatButton.setOnClickListener { setChatText(view, 3) }
                ObjectAnimator.ofFloat(chatBubble, "translationY", -50f).apply { start() }
                ObjectAnimator.ofFloat(chatBubbleText, "translationY", -50f).apply { start() }
            }
            in 10..14 -> {
                val glacier_2_1: ImageView = view.findViewById(R.id.glacier_1)
                val glacier_2_2: ImageView = view.findViewById(R.id.glacier_2)
                val glacier_2_3: ImageView = view.findViewById(R.id.glacier_3)

                setChatText(view, 2)

                glacier.setImageResource(R.drawable.glacier_2_0)
                glacier_2_1.setImageResource(R.drawable.glacier_2_1)
                glacier_2_2.setImageResource(R.drawable.glacier_2_2)
                glacier_2_3.setImageResource(R.drawable.glacier_2_3)
                polarbear.setImageResource(R.drawable.polarbear_2)

                glacier.startAnimation(
                    AnimationUtils.loadAnimation(
                        activity,
                        R.anim.glacier_2_0_moving
                    )
                )
                glacier_2_1.startAnimation(
                    AnimationUtils.loadAnimation(
                        activity,
                        R.anim.glacier_2_1_moving
                    )
                )
                glacier_2_2.startAnimation(
                    AnimationUtils.loadAnimation(
                        activity,
                        R.anim.glacier_2_2_moving
                    )
                )
                glacier_2_3.startAnimation(
                    AnimationUtils.loadAnimation(
                        activity,
                        R.anim.glacier_2_3_moving
                    )
                )
                polarbear.startAnimation(
                    AnimationUtils.loadAnimation(
                        activity,
                        R.anim.glacier_2_0_moving
                    )
                )

                chatBubble.startAnimation(
                    AnimationUtils.loadAnimation(
                        activity,
                        R.anim.glacier_2_0_moving
                    )
                )
                chatBubbleText.startAnimation(
                    AnimationUtils.loadAnimation(
                        activity,
                        R.anim.glacier_2_0_moving
                    )
                )

                chatButton.setOnClickListener { setChatText(view, 2) }

            }
            in 15..19 -> {
                val glacier_1_1: ImageView = view.findViewById(R.id.glacier_1)
                glacier.setImageResource(R.drawable.glacier_1_0)
                glacier_1_1.setImageResource(R.drawable.glacier_1_1)
                polarbear.setImageResource(R.drawable.polarbear_1)

                setChatText(view, 1)

                ObjectAnimator.ofFloat(chatBubble, "translationY", -200f).apply { start() }
                ObjectAnimator.ofFloat(chatBubbleText, "translationY", -200f).apply { start() }

                glacier.startAnimation(
                    AnimationUtils.loadAnimation(
                        activity,
                        R.anim.glacier_2_0_moving
                    )
                )
                glacier_1_1.startAnimation(
                    AnimationUtils.loadAnimation(
                        activity,
                        R.anim.glacier_2_2_moving
                    )
                )
                polarbear.startAnimation(
                    AnimationUtils.loadAnimation(
                        activity,
                        R.anim.glacier_2_0_moving
                    )
                )
                chatBubble.startAnimation(
                    AnimationUtils.loadAnimation(
                        activity,
                        R.anim.glacier_2_0_moving
                    )
                )
                chatBubbleText.startAnimation(
                    AnimationUtils.loadAnimation(
                        activity,
                        R.anim.glacier_2_0_moving
                    )
                )

                chatButton.setOnClickListener { setChatText(view, 1) }

            }
            else -> {
                glacier.setImageResource(R.drawable.glacier_0)
                polarbear.setImageResource(0)
                chatBubble.setImageResource(0)
                chatBubbleText.setText("")
                glacier.startAnimation(
                    AnimationUtils.loadAnimation(
                        activity,
                        R.anim.glacier_2_0_moving
                    )
                )
            }
        }
        remainText.setText("오늘의 탄소배출량 ${dailyCarbonDouble}kg")

        //문구
//        val todayGoalText: TextView = view.findViewById(R.id.todayGoalText)
//        if (compareDate()) {
//            todayGoalText.setText(App.prefs.getSavedText("savedDate", "error"))
//        } else {
//            val range = (0..6)
//            val randomNumber = range.random()
//            val textArray: Array<String> = resources.getStringArray(R.array.dailyMission)
//            val newText: String = textArray[randomNumber]
//            todayGoalText.setText(newText)
//            App.prefs.setSavedText("savedDate", newText)
//        }

        //문구
        randomNumber = (0..6).random()
        textArray = resources.getStringArray(R.array.dailyMission)
        todayGoalText.setText(textArray[randomNumber])
    }

}