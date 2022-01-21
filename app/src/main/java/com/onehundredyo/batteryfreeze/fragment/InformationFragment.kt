package com.onehundredyo.batteryfreeze.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.onehundredyo.batteryfreeze.DO.News
import com.onehundredyo.batteryfreeze.R
import com.onehundredyo.batteryfreeze.adapter.CustomListViewAdapter
import android.view.MotionEvent

import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener


class InformationFragment : Fragment() {
    private lateinit var customListviewAdapter: CustomListViewAdapter
    private lateinit var news_list : MutableList<News>
    private lateinit var RecycleViewCustom: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_information, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        RecycleViewCustom = view.findViewById(R.id.list_view)

        initRecyler()

        customListviewAdapter.setItemClickListener(object: CustomListViewAdapter.OnItemClickListener{
            override fun onClick(view: View, position: Int){
                val intent = Intent(Intent.ACTION_VIEW,Uri.parse(news_list[position].body))
                startActivity(intent)
            }
        })
    }

    private fun initRecyler(){
        customListviewAdapter = context?.let { CustomListViewAdapter(it) }!!

        news_list = mutableListOf()
        news_list.apply {
            news_list.add(
                News(
                    "알아두면 쓸모 있는 가족, 친구 간 ‘기후변화’ 대화 TIP",
                    "https://www.greenpeace.org/korea/update/21156/blog-ce-talk-about-climate-change/",
                    "https://www.greenpeace.org/static/planet4-korea-stateless/2022/01/9467b2fb-gp0sttns2_web_size.jpg"
                )
            )
            news_list.add(
                News(
                    "기후변화, 얼만큼 다가왔을까요?",
                    "https://www.greenpeace.org/korea/update/20976/blog-ce-the-reality-of-the-climate-crisis/",
                    "https://www.greenpeace.org/static/planet4-korea-stateless/2022/01/62430466-gp1suvq1_web_size_with_credit_line.jpg"
                )
            )
            news_list.add(
                News(
                    "불타는 돈: 해외 석탄 투자 캠페인은 이렇게 만들어졌습니다",
                    "https://www.greenpeace.org/korea/update/21058/blog-ce-coal_finance_interview/",
                    "https://www.greenpeace.org/static/planet4-korea-stateless/2022/01/47167480-screenshot-2022-01-06-at-11.02.08-am.jpg"
                )
            )

            customListviewAdapter.newsList = news_list
            customListviewAdapter.notifyDataSetChanged()
        }

        RecycleViewCustom.adapter = customListviewAdapter
    }
}

