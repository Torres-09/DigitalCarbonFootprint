package com.onehundredyo.batteryfreeze.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapplication.DataUsage
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.onehundredyo.batteryfreeze.R
import com.onehundredyo.batteryfreeze.databinding.FragmentStaticBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [StaticFragment.newInstance] factory method to
 * create an instance of this fragment.
 */


class StaticFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


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
        return inflater.inflate(R.layout.fragment_static, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment StaticFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            StaticFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private var DataList = ArrayList<DataUsage>()
    private lateinit var barChart: BarChart

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        barChart = view.findViewById(R.id.barchart)
        DataList = getBarDataUsage()

        initBarChart()

        val entries: ArrayList<BarEntry> = ArrayList()

        for (i in DataList.indices) {
            val dataUsage = DataList[i]
            entries.add(BarEntry(i.toFloat(), dataUsage.datausage.toFloat()))
        }

        val barDataSet = BarDataSet(entries, "")
        barDataSet.color = ColorTemplate.rgb("#5C98AF")
        val data = BarData(barDataSet)
        data.barWidth = 0.35f
        barChart.data = data

        barChart.invalidate()
    }

    fun getBarDataUsage(): ArrayList<DataUsage> {
        DataList.add(DataUsage("newflix", 56))
        DataList.add(DataUsage("youtube", 75))
        DataList.add(DataUsage("naver", 85))
        DataList.add(DataUsage("github", 45))
        DataList.add(DataUsage("bye", 63))

        return DataList
    }

    private fun initBarChart() {

        barChart.run {
            // 막대 그래프 그림자 on
            setDrawBarShadow(true)
            // 차트 터치 X
            setTouchEnabled(false)
            // 줌 금지
            setPinchZoom(false)

            // 막대 그래프 올라가는 애니메이션 추가
            animateXY(0,800)

            axisLeft.run {
                // 좌측 y축 제거
                isEnabled = false


            }
            axisRight.run {
                //우측 y축 제거
                isEnabled = false
            }

            xAxis.run {
                // 막대 그래프 바 grid 제거
                setDrawGridLines(false)
                setDrawAxisLine(false)

                // 막대 그래프 설정
                position = XAxis.XAxisPosition.BOTTOM
                textColor = ColorTemplate.rgb("#5C98AF")

                granularity = 1f
                labelRotationAngle = -25f
            }

            legend.run {
                // 하단 항목 이름 제거
                isEnabled = false
            }

            description.run {
                // 설명 제거
                isEnabled = false

            }
        }
    }
}