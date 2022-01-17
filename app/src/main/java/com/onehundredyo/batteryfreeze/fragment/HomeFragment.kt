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


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
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
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
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
        val animation = AnimationUtils.loadAnimation(activity, R.anim.moving)


        //퍼센트에 따른 변화
        var remainPercentage = 27
        when (remainPercentage) {
            in 76..100 -> {}
            in 51..75 -> {
//                glacierImage.setImageResource(R.drawable.glacier0)
//                polarBearImage.setImageResource(R.drawable.polar_bear2)
//                glacierImage.startAnimation(animation)
//                polarBearImage.startAnimation(animation)
            }
            in 26..50 -> {
                glacierImage.setImageResource(R.drawable.glacier0)
                polarBearImage.setImageResource(R.drawable.polar_bear1)
                glacierImage.startAnimation(animation)
                polarBearImage.startAnimation(animation)
            }
            in 0..25 -> {
//                glacierImage.setImageResource(0)
//                polarBearImage.setImageResource(0)
            }
            else -> {
            }
        }
        remainText.setText("남은 목표량: $remainPercentage%")


        //오늘의 문구
        val todayGoalText: TextView = view.findViewById(R.id.todayGoalText)
        if (compareDate()) {
            todayGoalText.setText(App.prefs.getSavedText("savedDate", "error"))
        } else {
            val range = (0..6)
            val randomNumber = range.random()
            val textArray: Array<String> = resources.getStringArray(R.array.dailyMission)
            val newText: String = textArray[randomNumber]
            todayGoalText.setText(newText)
            App.prefs.setSavedText("savedDate", newText)
        }
    }
}