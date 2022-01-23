package com.onehundredyo.batteryfreeze.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.onehundredyo.batteryfreeze.DO.News
import com.onehundredyo.batteryfreeze.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL

class CustomListViewAdapter(private val context: Context) :
RecyclerView.Adapter<CustomListViewAdapter.ViewHolder>(){
    private lateinit var itemClickListener : OnItemClickListener
    var newsList = mutableListOf<News>()
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        private val listTitle: TextView = view.findViewById(R.id.list_view_title)
        private val listImage: ImageView = view.findViewById(R.id.list_view_image)

        fun bind(item: News) {
            listTitle.text = item.title
            CoroutineScope(Dispatchers.Main).launch{
                var bitmap = withContext(Dispatchers.IO){
                    ImageLoader.loadImage(item.imageUrl)
                }
                listImage.setImageBitmap(bitmap)
            }

        }
    }

    override fun getItemCount(): Int = newsList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_view_custom, parent,false)
        return ViewHolder(view)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(newsList[position])

        holder.itemView.setOnClickListener{
            itemClickListener.onClick(it, position)
        }
    }

    interface OnItemClickListener{
        fun onClick(view: View, position: Int)
    }

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