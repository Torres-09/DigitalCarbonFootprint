package com.onehundredyo.batteryfreeze.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.onehundredyo.batteryfreeze.DO.News
import com.onehundredyo.batteryfreeze.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL

class CustomListViewAdapter(val context: Context?, val newsList: MutableList<News>) : BaseAdapter(){
    private lateinit var newsTitle: TextView
    private lateinit var newsBody: TextView
    private lateinit var newsImageUrl: ImageView

    override fun getCount(): Int {
        return newsList.size
    }

    override fun getItem(position: Int): Any {
        return newsList[position]
    }

    override fun getItemId(p0: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = LayoutInflater.from(context).inflate(R.layout.list_view_custiom, null)

        newsTitle = view.findViewById(R.id.list_view_title)
        newsBody = view.findViewById(R.id.list_view_body)
        newsImageUrl = view.findViewById(R.id.list_view_image)

        val news = newsList[position]
        var bitmap: Bitmap? = null
        newsTitle.setText(news.title)
        newsBody.setText(news.body)
        CoroutineScope(Dispatchers.Main).launch{
            bitmap = withContext(Dispatchers.IO){
                ImageLoader.loadImage(news.imageUrl)
            }
            newsImageUrl.setImageBitmap(bitmap)
        }
        return view
    }

    object ImageLoader{
        suspend fun loadImage(imageUrl: String): Bitmap?{
            val bmp: Bitmap? = null
            try{
                val url = URL(imageUrl)
                val stream = url.openStream()

                return BitmapFactory.decodeStream(stream)
            }catch (e: MalformedURLException){
                e.printStackTrace()
            }catch (e: IOException){
                e.printStackTrace()
            }
            return bmp
        }
    }
}