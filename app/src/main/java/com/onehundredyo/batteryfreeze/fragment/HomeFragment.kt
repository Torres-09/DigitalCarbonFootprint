package com.onehundredyo.batteryfreeze.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.onehundredyo.batteryfreeze.R
import android.widget.TextView
import com.onehundredyo.batteryfreeze.App
import java.time.LocalDate
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

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
        val chatBubbleText: TextView = view.findViewById(R.id.chatBubbleText)
        var randomNumber = (0..2).random()
        when (level) {
            3 -> chatBubbleText.setText(resources.getStringArray(R.array.chat_list3)[randomNumber])
            2 -> chatBubbleText.setText(resources.getStringArray(R.array.chat_list2)[randomNumber])
            1 -> chatBubbleText.setText(resources.getStringArray(R.array.chat_list1)[randomNumber])
            0 -> chatBubbleText.setText(resources.getStringArray(R.array.chat_list0)[randomNumber])
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    companion object {
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val glacierImage: ImageView = view.findViewById(R.id.glacierImage)
        val polarBearImage: ImageView = view.findViewById(R.id.polarBearImage)
        val remainText: TextView = view.findViewById(R.id.remainText)
        val chatBubbleText: TextView = view.findViewById(R.id.chatBubbleText)
        val chatBubble: ImageView = view.findViewById(R.id.chatBubble)
        val animation = AnimationUtils.loadAnimation(activity, R.anim.moving)
        val todayGoalText: TextView = view.findViewById(R.id.todayGoalText)

        //퍼센트에 따른 변화
        var randomNumber = (0..2).random()
        lateinit var textArray: Array<String>
        var remainPercentage = 27
        when (remainPercentage) {
            in 76..100 -> {
                polarBearImage.setOnClickListener { setChatText(view, 3) }
            }
            in 51..75 -> {
                polarBearImage.setOnClickListener { setChatText(view, 2) }

//                glacierImage.setImageResource(R.drawable.glacier0)
//                polarBearImage.setImageResource(R.drawable.polar_bear2)
//                glacierImage.startAnimation(animation)
//                polarBearImage.startAnimation(animation)
            }
            in 26..50 -> {
                polarBearImage.setOnClickListener { setChatText(view, 1) }
                textArray = resources.getStringArray(R.array.chat_list1)
                glacierImage.setImageResource(R.drawable.glacier0)
                polarBearImage.setImageResource(R.drawable.polar_bear1)
            }
            in 0..25 -> {
                polarBearImage.setOnClickListener { setChatText(view, 0) }
                textArray = resources.getStringArray(R.array.chat_list0)
//                glacierImage.setImageResource(0)
//                polarBearImage.setImageResource(0)
            }
            else -> {
            }
        }
        remainText.setText("남은 목표량: $remainPercentage%")
        glacierImage.startAnimation(animation)
        polarBearImage.startAnimation(animation)
        chatBubble.startAnimation(animation)
        chatBubbleText.startAnimation(animation)

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