package com.onehundredyo.batteryfreeze.fragment

import android.content.Context
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
import com.onehundredyo.batteryfreeze.DO.MonthlyInfo
import com.onehundredyo.batteryfreeze.DO.WeeklyInfo
import com.onehundredyo.batteryfreeze.DO.YearlyInfo
import com.onehundredyo.batteryfreeze.MainActivity
import com.onehundredyo.batteryfreeze.R
import com.onehundredyo.batteryfreeze.dataBaseHelper.DataBaseManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class StatisticsFragment : Fragment() {
    private var DataList = ArrayList<DataUsage>()
    private lateinit var barChart: BarChart
    lateinit var mainActivity: MainActivity     // CONTEXT
    var yearlyData: MutableList<YearlyInfo> = mutableListOf()
    var monthlyData: MutableList<MonthlyInfo> = mutableListOf()
    var weeklyData: MutableList<WeeklyInfo> = mutableListOf()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
        // DB를 사용하기 위해 CONTEXT 를 얻어옴
        getDatabase()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }


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

        DataList.add(DataUsage("3주전", monthlyData[0].DataUsage.toInt()))
        DataList.add(DataUsage("2주전", monthlyData[1].DataUsage.toInt()))
        DataList.add(DataUsage("1주전", monthlyData[2].DataUsage.toInt()))
        DataList.add(DataUsage("이번주", monthlyData[3].DataUsage.toInt()))
        DataList.add(DataUsage("이번주", monthlyData[3].DataUsage.toInt()))

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
            animateXY(0, 800)

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

    private fun getDatabase() {
        val db = DataBaseManager.getInstance(mainActivity)!!
        CoroutineScope(Dispatchers.IO).launch {
            for (i in (db!!.DatausageDAO().getAllYearlyData())) {
                yearlyData.add(i)
            }
            for (i in db!!.DatausageDAO().getAllMonthlyData()) {
                monthlyData.add(i)
            }
            for (i in db!!.DatausageDAO().getAllWeeklyData()) {
                weeklyData.add(i)
            }
        }
    }
}