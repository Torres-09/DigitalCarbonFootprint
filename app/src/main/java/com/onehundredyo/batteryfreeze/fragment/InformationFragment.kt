package com.onehundredyo.batteryfreeze.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.onehundredyo.batteryfreeze.DO.News
import com.onehundredyo.batteryfreeze.R
import com.onehundredyo.batteryfreeze.adapter.CustomListViewAdapter

class InformationFragment : Fragment() {
    private lateinit var news_list : MutableList<News>
    private lateinit var listViewCustiom: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        news_list = mutableListOf()
        news_list.add(News("알아두면 쓸모 있는 가족, 친구 간 ‘기후변화’ 대화 TIP",
            "1. 타이밍이 생명! 분위기를 살펴보세요 대부분의 경우, 우리는 마음의 여유가 있을 때 효과적인 대화를 나눌 수 있습니다..",
            "https://www.greenpeace.org/static/planet4-korea-stateless/2022/01/9467b2fb-gp0sttns2_web_size.jpg"))
        news_list.add(News("기후변화, 얼만큼 다가왔을까요?",
            "지구가 점점 뜨거워지고 있습니다. 불과 10년 전까지만 해도 ‘지구온난화’ 또는 ‘기후변화’라는 단어가 주로 사용되었지만, 최근에는 그 상황이 심각해져 ‘기후위기’ 혹은 ‘기후재앙’이란 단어가 더 자주 등장하고 있습니다.",
            "https://www.greenpeace.org/static/planet4-korea-stateless/2022/01/62430466-gp1suvq1_web_size_with_credit_line.jpg"))
        news_list.add(News("알아두면 쓸모 있는 가족, 친구 간 ‘기후변화’ 대화 TIP",
            "1. 타이밍이 생명! 분위기를 살펴보세요 대부분의 경우, 우리는 마음의 여유가 있을 때 효과적인 대화를 나눌 수 있습니다..",
            "https://www.greenpeace.org/static/planet4-korea-stateless/2022/01/9467b2fb-gp0sttns2_web_size.jpg"))



        return inflater.inflate(R.layout.fragment_information, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listViewCustiom = view.findViewById(R.id.list_view)
        var customListviewAdapter = CustomListViewAdapter(context, news_list)
        listViewCustiom.adapter = customListviewAdapter

    }
}

