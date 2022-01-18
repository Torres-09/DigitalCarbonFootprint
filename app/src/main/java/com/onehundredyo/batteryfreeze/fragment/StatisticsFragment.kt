package com.onehundredyo.batteryfreeze.fragment

import android.content.ContentValues
import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapplication.DataUsage
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import com.google.android.material.shadow.ShadowRenderer
import com.onehundredyo.batteryfreeze.DO.MonthlyInfo
import com.onehundredyo.batteryfreeze.DO.WeeklyInfo
import com.onehundredyo.batteryfreeze.DO.YearlyInfo
import com.onehundredyo.batteryfreeze.MainActivity
import com.onehundredyo.batteryfreeze.R
import com.onehundredyo.batteryfreeze.dataBaseHelper.DataBaseManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

//weeklybarchart - weekly
//monthlybarchart - monthly
//yearlybarchart - yearly

class StatisticsFragment : Fragment() {
    private var WeekDataList = ArrayList<DataUsage>()
    private var MonthDataList = ArrayList<DataUsage>()
    private var YearDataList = ArrayList<DataUsage>()

    private lateinit var weeklybarChart: BarChart
    private lateinit var monthlybarChart: BarChart
    private lateinit var yearlybarChart: BarChart

    lateinit var mainActivity: MainActivity     // CONTEXT
    var yearlyData: MutableList<YearlyInfo> = mutableListOf(
        YearlyInfo(0, 0L),
        YearlyInfo(1, 0L),
        YearlyInfo(2, 0L),
        YearlyInfo(3, 0L),
        YearlyInfo(4, 0L),
        YearlyInfo(5, 0L),
        YearlyInfo(6, 0L),
        YearlyInfo(7, 0L),
        YearlyInfo(8, 0L),
        YearlyInfo(9, 0L),
        YearlyInfo(10, 0L),
        YearlyInfo(11, 0L)
    )
    var monthlyData: MutableList<MonthlyInfo> = mutableListOf(
        MonthlyInfo(0, 0L),
        MonthlyInfo(1, 0L),
        MonthlyInfo(2, 0L),
        MonthlyInfo(3, 0L)
    )
    var weeklyData: MutableList<WeeklyInfo> = mutableListOf(
        WeeklyInfo(0, 0L),
        WeeklyInfo(1, 0L),
        WeeklyInfo(2, 0L),
        WeeklyInfo(3, 0L),
        WeeklyInfo(4, 0L),
        WeeklyInfo(5, 0L),
        WeeklyInfo(6, 0L)
    )

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

        weeklybarChart = view.findViewById(R.id.weeklybarchart)
        monthlybarChart = view.findViewById(R.id.monthlychart)
        yearlybarChart = view.findViewById(R.id.yearlychart)

        WeekDataList = getWeeklyBarDataUsage()
        MonthDataList = getMonthlyBarDataUsage()
        YearDataList = getYearlyBarDataUsage()

        initWeeklyBarChart()
        initMonthlyBarChart()
        initYearlyBarChart()

        val Weeklyentries: ArrayList<BarEntry> = ArrayList()
        val Monthlyentries: ArrayList<BarEntry> = ArrayList()
        val Yearlyentries: ArrayList<BarEntry> = ArrayList()

        for (i in WeekDataList.indices) {
            val dataUsage = WeekDataList[i]
            Weeklyentries.add(BarEntry(i.toFloat(), dataUsage.datausage.toFloat()))
        }

        for (i in MonthDataList.indices) {
            val dataUsage = MonthDataList[i]
            Monthlyentries.add(BarEntry(i.toFloat(), dataUsage.datausage.toFloat()))
        }

        for (i in YearDataList.indices) {
            val dataUsage = YearDataList[i]
            Yearlyentries.add(BarEntry(i.toFloat(), dataUsage.datausage.toFloat()))
        }


        // label 이름이랑 color 설정 - weekly
        val weeklybarDataSet = BarDataSet(Weeklyentries, "")
        weeklybarDataSet.color = ColorTemplate.rgb("#1A73E9")
        weeklybarDataSet.barShadowColor = ColorTemplate.rgb("#F0F0F0")
        val weeklydata = BarData(weeklybarDataSet)
        weeklydata.barWidth = 0.35f
        weeklybarChart.data = weeklydata

        // label 이름이랑 color 설정 - monthly
        val monthlybarDataSet = BarDataSet(Monthlyentries, "")
        monthlybarDataSet.color = ColorTemplate.rgb("#1A73E9")
        monthlybarDataSet.barShadowColor = ColorTemplate.rgb("#F0F0F0")
        val monthlydata = BarData(monthlybarDataSet)
        monthlydata.barWidth = 0.35f
        monthlybarChart.data = monthlydata

        // label 이름이랑 color 설정 - weekly
        val yearlybarDataSet = BarDataSet(Weeklyentries, "")
        yearlybarDataSet.color = ColorTemplate.rgb("#1A73E9")
        yearlybarDataSet.barShadowColor = ColorTemplate.rgb("#F0F0F0")
        val yearlydata = BarData(yearlybarDataSet)
        yearlydata.barWidth = 0.35f
        weeklybarChart.data = yearlydata

        // shape 둥글게 변경 - barchart
        val myradius = 40


        val weeklybarChartRender =
            CustomBarChartRender(weeklybarChart, weeklybarChart.animator, weeklybarChart.viewPortHandler)
        weeklybarChartRender.setRadius(myradius)
        weeklybarChart.renderer = weeklybarChartRender

        val monthlybarChartRender =
            CustomBarChartRender(monthlybarChart, monthlybarChart.animator, monthlybarChart.viewPortHandler)
        monthlybarChartRender.setRadius(myradius)
        monthlybarChart.renderer = monthlybarChartRender

        val yearlybarChartRender =
            CustomBarChartRender(yearlybarChart, yearlybarChart.animator, yearlybarChart.viewPortHandler)
        yearlybarChartRender.setRadius(myradius)
        yearlybarChart.renderer = yearlybarChartRender

        weeklybarChart.invalidate()
        monthlybarChart.invalidate()
        yearlybarChart.invalidate()
    }

    private fun getWeeklyBarDataUsage(): ArrayList<DataUsage> {

        WeekDataList.add(DataUsage("일요일", weeklyData[0].DataUsage))
        WeekDataList.add(DataUsage("월요일", weeklyData[1].DataUsage))
        WeekDataList.add(DataUsage("화요일", weeklyData[2].DataUsage))
        WeekDataList.add(DataUsage("수요일", weeklyData[3].DataUsage))
        WeekDataList.add(DataUsage("목요일", weeklyData[4].DataUsage))
        WeekDataList.add(DataUsage("금요일", weeklyData[5].DataUsage))
        WeekDataList.add(DataUsage("토요일", weeklyData[6].DataUsage))

        return WeekDataList
    }

    fun getMonthlyBarDataUsage(): ArrayList<DataUsage>
    {
        MonthDataList.add(DataUsage("3주전", monthlyData[0].DataUsage))
        MonthDataList.add(DataUsage("2주전", monthlyData[1].DataUsage))
        MonthDataList.add(DataUsage("1주전", monthlyData[2].DataUsage))
        MonthDataList.add(DataUsage("이번주", monthlyData[3].DataUsage))

        return MonthDataList
    }

    fun getYearlyBarDataUsage(): ArrayList<DataUsage>
    {
        YearDataList.add(DataUsage("0달", yearlyData[0].DataUsage))
        YearDataList.add(DataUsage("1달", yearlyData[0].DataUsage))
        YearDataList.add(DataUsage("2달", yearlyData[0].DataUsage))
        YearDataList.add(DataUsage("3달", yearlyData[0].DataUsage))
        YearDataList.add(DataUsage("4달", yearlyData[0].DataUsage))
        YearDataList.add(DataUsage("5달", yearlyData[0].DataUsage))
        YearDataList.add(DataUsage("6달", yearlyData[0].DataUsage))
        YearDataList.add(DataUsage("7달", yearlyData[0].DataUsage))
        YearDataList.add(DataUsage("8달", yearlyData[0].DataUsage))
        YearDataList.add(DataUsage("9달", yearlyData[0].DataUsage))
        YearDataList.add(DataUsage("10달", yearlyData[0].DataUsage))
        YearDataList.add(DataUsage("11달", yearlyData[0].DataUsage))

        return YearDataList
    }

    // week
    private fun initWeeklyBarChart() {

        weeklybarChart.run {

            // 막대 그래프 그림자 on
            setDrawBarShadow(true)

            // 차트 터치 X
            setTouchEnabled(false)
            // 줌 금지
            setPinchZoom(false)

            // 막대 그래프 올라가는 애니메이션 추가
            animateXY(0, 800)

            extraBottomOffset = 10f

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
                textColor = ColorTemplate.rgb("#F0F0F0")
//                valueFormatter = MyAxisFormatter()
                granularity = 1f
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

    //month
    private fun initMonthlyBarChart() {

        monthlybarChart.run {

            // 막대 그래프 그림자 on
            setDrawBarShadow(true)

            // 차트 터치 X
            setTouchEnabled(false)
            // 줌 금지
            setPinchZoom(false)

            // 막대 그래프 올라가는 애니메이션 추가
            animateXY(0, 800)

            extraBottomOffset = 10f

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
                textColor = ColorTemplate.rgb("#F0F0F0")
//                valueFormatter = MyAxisFormatter()
                granularity = 1f
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

    //year
    private fun initYearlyBarChart() {

        yearlybarChart.run {

            // 막대 그래프 그림자 on
            setDrawBarShadow(true)

            // 차트 터치 X
            setTouchEnabled(false)
            // 줌 금지
            setPinchZoom(false)

            // 막대 그래프 올라가는 애니메이션 추가
            animateXY(0, 800)

            extraBottomOffset = 10f

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
                textColor = ColorTemplate.rgb("#F0F0F0")
//                valueFormatter = MyAxisFormatter()
                granularity = 1f
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

    // 막대 그래프 내부에 항목이름을 각각 적기 - barchart
//    inner class MyAxisFormatter : IndexAxisValueFormatter() {
//        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
//            val index = value.toInt()
//            Log.d(ContentValues.TAG, "getAxisLabel: index $index")
//            return if (index < DataList.size) {
//                DataList[index].title
//            } else {
//                ""
//            }
//        }
//    }

    private fun getDatabase() {
        val db = DataBaseManager.getInstance(mainActivity)!!
        CoroutineScope(Dispatchers.IO).launch {
            val tmpYearlyInfo: MutableList<YearlyInfo> = db!!.DatausageDAO().getAllYearlyData()
            val tmpMonthlyInfo: MutableList<MonthlyInfo> = db!!.DatausageDAO().getAllMonthlyData()
            val tmpWeeklyInfo: MutableList<WeeklyInfo> = db!!.DatausageDAO().getAllWeeklyData()
            for (i in tmpYearlyInfo.indices) {
                yearlyData[i] = tmpYearlyInfo[i]
            }
            for (i in tmpMonthlyInfo.indices) {
                monthlyData[i] = tmpMonthlyInfo[i]
            }
            for (i in tmpWeeklyInfo.indices) {
                weeklyData[i] = tmpWeeklyInfo[i]
            }
        }
    }

    // change barchart shape circular - barchart

    class CustomBarChartRender(
        chart: BarDataProvider?,
        animator: ChartAnimator?,
        viewPortHandler: ViewPortHandler?
    ) :
        BarChartRenderer(chart, animator, viewPortHandler) {
        private val mBarShadowRectBuffer = RectF()
        private var mRadius = 0
        fun setRadius(mRadius: Int) {
            this.mRadius = mRadius
        }

        override fun drawDataSet(c: Canvas, dataSet: IBarDataSet, index: Int) {
            val trans = mChart.getTransformer(dataSet.axisDependency)
            mBarBorderPaint.color = dataSet.barBorderColor
            mBarBorderPaint.strokeWidth = Utils.convertDpToPixel(dataSet.barBorderWidth)
            mShadowPaint.color = dataSet.barShadowColor
            val drawBorder = dataSet.barBorderWidth > 0f
            val phaseX = mAnimator.phaseX
            val phaseY = mAnimator.phaseY
            if (mChart.isDrawBarShadowEnabled) {
                mShadowPaint.color = dataSet.barShadowColor
                val barData = mChart.barData
                val barWidth = barData.barWidth
                val barWidthHalf = barWidth / 2.0f
                var x: Float
                var i = 0
                val count = Math.min(
                    Math.ceil(
                        (dataSet.entryCount
                            .toFloat() * phaseX).toDouble()
                    ),
                    dataSet.entryCount.toDouble()
                )
                while (i < count) {
                    val e = dataSet.getEntryForIndex(i)
                    x = e.x
                    mBarShadowRectBuffer.left = x - barWidthHalf
                    mBarShadowRectBuffer.right = x + barWidthHalf
                    trans.rectValueToPixel(mBarShadowRectBuffer)
                    if (!mViewPortHandler.isInBoundsLeft(mBarShadowRectBuffer.right)) {
                        i++
                        continue
                    }
                    if (!mViewPortHandler.isInBoundsRight(mBarShadowRectBuffer.left)) break
                    mBarShadowRectBuffer.top = mViewPortHandler.contentTop()
                    mBarShadowRectBuffer.bottom = mViewPortHandler.contentBottom()
                    c.drawRoundRect(mBarRect, mRadius.toFloat(), mRadius.toFloat(), mShadowPaint)
                    i++
                }
            }

            // initialize the buffer
            val buffer = mBarBuffers[index]
            buffer.setPhases(phaseX, phaseY)
            buffer.setDataSet(index)
            buffer.setInverted(mChart.isInverted(dataSet.axisDependency))
            buffer.setBarWidth(mChart.barData.barWidth)
            buffer.feed(dataSet)
            trans.pointValuesToPixel(buffer.buffer)
            val isSingleColor = dataSet.colors.size == 1
            if (isSingleColor) {
                mRenderPaint.color = dataSet.color
            }
            var j = 0
            while (j < buffer.size()) {
                if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])) {
                    j += 4
                    continue
                }
                if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j])) break
                if (!isSingleColor) {
                    // Set the color for the currently drawn value. If the index
                    // is out of bounds, reuse colors.
                    mRenderPaint.color = dataSet.getColor(j / 4)
                }
                if (dataSet.gradientColor != null) {
                    val gradientColor = dataSet.gradientColor
                    mRenderPaint.shader = LinearGradient(
                        buffer.buffer[j],
                        buffer.buffer[j + 3],
                        buffer.buffer[j],
                        buffer.buffer[j + 1],
                        gradientColor.startColor,
                        gradientColor.endColor,
                        Shader.TileMode.MIRROR
                    )
                }
                if (dataSet.gradientColors != null) {
                    mRenderPaint.shader = LinearGradient(
                        buffer.buffer[j],
                        buffer.buffer[j + 3],
                        buffer.buffer[j],
                        buffer.buffer[j + 1],
                        dataSet.getGradientColor(j / 4).startColor,
                        dataSet.getGradientColor(j / 4).endColor,
                        Shader.TileMode.MIRROR
                    )
                }
                val path2: Path = roundRect(
                    RectF(
                        buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                        buffer.buffer[j + 3]
                    ), mRadius.toFloat(), mRadius.toFloat(), true, true, false, false
                )
                c.drawPath(path2, mRenderPaint)
                if (drawBorder) {
                    val path: Path = roundRect(
                        RectF(
                            buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                            buffer.buffer[j + 3]
                        ), mRadius.toFloat(), mRadius.toFloat(), true, true, false, false
                    )
                    c.drawPath(path, mBarBorderPaint)
                }
                j += 4
            }
        }

        private fun roundRect(
            rect: RectF,
            rx: Float,
            ry: Float,
            tl: Boolean,
            tr: Boolean,
            br: Boolean,
            bl: Boolean
        ): Path {
            var rx = rx
            var ry = ry
            val top = rect.top
            val left = rect.left
            val right = rect.right
            val bottom = rect.bottom
            val path = Path()
            if (rx < 0) rx = 0f
            if (ry < 0) ry = 0f
            val width = right - left
            val height = bottom - top
            if (rx > width / 2) rx = width / 2
            if (ry > height / 2) ry = height / 2
            val widthMinusCorners = width - 2 * rx
            val heightMinusCorners = height - 2 * ry
            path.moveTo(right, top + ry)
            if (tr) path.rQuadTo(0f, -ry, -rx, -ry) //top-right corner
            else {
                path.rLineTo(0f, -ry)
                path.rLineTo(-rx, 0f)
            }
            path.rLineTo(-widthMinusCorners, 0f)
            if (tl) path.rQuadTo(-rx, 0f, -rx, ry) //top-left corner
            else {
                path.rLineTo(-rx, 0f)
                path.rLineTo(0f, ry)
            }
            path.rLineTo(0f, heightMinusCorners)
            if (bl) path.rQuadTo(0f, ry, rx, ry) //bottom-left corner
            else {
                path.rLineTo(0f, ry)
                path.rLineTo(rx, 0f)
            }
            path.rLineTo(widthMinusCorners, 0f)
            if (br) path.rQuadTo(rx, 0f, rx, -ry) //bottom-right corner
            else {
                path.rLineTo(rx, 0f)
                path.rLineTo(0f, -ry)
            }
            path.rLineTo(0f, -heightMinusCorners)
            path.close() //Given close, last lineto can be removed.
            return path
        }
    }
}