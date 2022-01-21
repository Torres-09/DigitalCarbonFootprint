package com.onehundredyo.batteryfreeze.fragment

import android.Manifest
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.PixelCopy
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import com.onehundredyo.batteryfreeze.R
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginBottom
import com.onehundredyo.batteryfreeze.App
import com.onehundredyo.batteryfreeze.MainActivity
import com.onehundredyo.batteryfreeze.databinding.FragmentHomeBinding
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.time.LocalDate
import java.lang.IllegalArgumentException
import android.os.Environment


val TAG = "Home"

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

    fun dpToPx(context: Context, dp: Float): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        )
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
        val instabtn: ImageButton = view.findViewById(R.id.insta_share_button)
        val activity = context as Activity
        val topOne = (activity as MainActivity?)?.getTopFiveApp()!![0].name


        //퍼센트에 따른 변화
        var randomNumber = (0..2).random()
        lateinit var textArray: Array<String>
        var dailyCarbonDouble = (remainPercentage)?.toDouble()?.div(1000)
        var dailyCarbonInt = (remainPercentage)?.toDouble()?.div(1000)?.toInt()
        // 배출된 이산화탄소 양
        when (dailyCarbonInt) {
            in 0..2 -> {
                glacier.setImageResource(R.drawable.glacier_4)
                polarbear.setImageResource(R.drawable.polarbear_4)

                setChatText(view, 4)

                chatButton.setOnClickListener { setChatText(view, 4) }
                ObjectAnimator.ofFloat(chatBubble, "translationY", -dpToPx(activity, 110f))
                    .apply {
                        duration = 0
                        start()
                    }
                ObjectAnimator.ofFloat(chatBubble, "translationX", -dpToPx(activity, 20f))
                    .apply {
                        duration = 0
                        start()
                    }
                ObjectAnimator.ofFloat(chatBubbleText, "translationY", -dpToPx(activity, 110f))
                    .apply {
                        duration = 0
                        start()
                    }
                ObjectAnimator.ofFloat(chatBubbleText, "translationX", -dpToPx(activity, 20f))
                    .apply {
                        duration = 0
                        start()
                    }


            }
            in 3..4 -> {
                glacier.setImageResource(R.drawable.glacier_3)
                polarbear.setImageResource(R.drawable.polarbear_3)

                setChatText(view, 3)

                chatButton.setOnClickListener { setChatText(view, 3) }
            }
            in 5..6 -> {
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

                chatBubbleText.startAnimation(
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

                chatButton.setOnClickListener { setChatText(view, 2) }
            }
            in 7..8 -> {
                val glacier_1_1: ImageView = view.findViewById(R.id.glacier_1)
                glacier.setImageResource(R.drawable.glacier_1_0)
                glacier_1_1.setImageResource(R.drawable.glacier_1_1)
                polarbear.setImageResource(R.drawable.polarbear_1)

                setChatText(view, 1)

                ObjectAnimator.ofFloat(chatBubble, "translationY", -dpToPx(activity, 70f))
                    .apply {
                        duration = 0
                        start()
                    }
                ObjectAnimator.ofFloat(chatBubbleText, "translationY", -dpToPx(activity, 70f))
                    .apply {
                        duration = 0
                        start()
                    }

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
                chatBubbleText.startAnimation(
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
        randomNumber = (0..6).random()
        textArray = resources.getStringArray(R.array.dailyMission)

        if (randomNumber == 0)
            todayGoalText.setText(resources.getStringArray(R.array.dailyMission)[0].format(topOne))
        else
            todayGoalText.setText(textArray[randomNumber])


        instabtn.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                screenTwo()
            } else {
                // Q 버전 이하일 경우. 저장소 권한을 얻어온다.
                val writePermission = ActivityCompat.checkSelfPermission(
                    mContext!!,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                if (writePermission == PackageManager.PERMISSION_GRANTED) {
//                    getBitmapFromView(binding!!.root) { bitmap -> screenShot(bitmap) }
                    screenTwo()
                } else {
                    val requestExternalStorageCode = 1

                    val permissionStorage = arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )

                    ActivityCompat.requestPermissions(
                        activity as MainActivity,
                        permissionStorage,
                        requestExternalStorageCode
                    )
                }
            }

        }
    }

    // https://goni95.tistory.com/123
    // https://kimyunseok.tistory.com/139 를 참고하여 작성함
    private fun screenTwo() {
        requireView().isDrawingCacheEnabled = true

        val screenBitmap = requireView().drawingCache

        try {
            val cachePath = File(mContext?.cacheDir, "images")
            cachePath.mkdirs()
            val stream =
                FileOutputStream("/data/user/0/com.onehundredyo.batteryfreeze/cache/images/image.png")
            screenBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.close()

            val newFile = File(cachePath, "image.png")
            val contentUri: Uri = FileProvider.getUriForFile(
                mContext!!, "com.onehundredyo.batteryfreeze.fileprovider", newFile
            )
            // URI 생성
            Log.d(TAG, contentUri.toString())
            val sourceApplication = "com.onehundredyo.batteryfreeze"


            val sharing_intent = Intent("com.instagram.share.ADD_TO_STORY")

            sharing_intent.putExtra("source_application", sourceApplication)

            sharing_intent.type = "image/png"
            sharing_intent.setDataAndType(contentUri, "image/png");

            mContext?.grantUriPermission(
                "com.instagram.android", contentUri, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

            try {
                this.startActivity(sharing_intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(
                    mContext!!.applicationContext,
                    "인스타그램 앱이 존재하지 않습니다.",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
            try {
                //저장해놓고 삭제한다.
                Thread.sleep(1000)
                contentUri?.let { uri -> mContext?.contentResolver?.delete(uri, null, null) }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

        } catch (e: IOException) {
            Log.d(TAG, "사진 공유 실패")
            e.printStackTrace()
        }
    }

}