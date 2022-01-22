package com.onehundredyo.batteryfreeze.fragment


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView

import com.onehundredyo.batteryfreeze.R

class InformationFragment : Fragment() {
    private lateinit var fundimage: ImageView
    private lateinit var fundimagedetail: ImageView
    private lateinit var fadeInAnim: Animation
    private lateinit var fadeOutAnim: Animation
    private lateinit var mContext: Context
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext=context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_information, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fundimagedetail = view.findViewById(R.id.fund_detail)
        fundimage = view.findViewById(R.id.fund2)

        fundimagedetail.visibility = View.INVISIBLE
        fundimagedetail.visibility = View.GONE

        fundimage.visibility = View.VISIBLE

        fadeInAnim = AnimationUtils.loadAnimation(mContext, R.anim.fade_in)
        fadeOutAnim = AnimationUtils.loadAnimation(mContext, R.anim.fade_out)

        fundimage.setOnClickListener {
            fundimage.visibility = View.GONE
            fundimage.visibility = View.INVISIBLE
            fundimage.startAnimation(fadeOutAnim)
            fundimagedetail.startAnimation(fadeInAnim)
            fundimagedetail.visibility = View.VISIBLE
        }
        fundimagedetail.setOnClickListener {
            fundimagedetail.visibility = View.GONE
            fundimagedetail.visibility = View.INVISIBLE
            fundimagedetail.startAnimation(fadeOutAnim)
            fundimage.startAnimation(fadeInAnim)
            fundimage.visibility = View.VISIBLE
        }
    }


}

