package com.onehundredyo.batteryfreeze.fragment


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.RecyclerView
import com.onehundredyo.batteryfreeze.DO.News
import com.onehundredyo.batteryfreeze.R
import com.onehundredyo.batteryfreeze.adapter.CustomListViewAdapter

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

}

