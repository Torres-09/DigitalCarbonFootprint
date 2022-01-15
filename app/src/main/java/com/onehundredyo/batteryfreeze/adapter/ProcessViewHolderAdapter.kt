package com.onehundredyo.batteryfreeze.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.onehundredyo.batteryfreeze.ProcessData
import com.onehundredyo.batteryfreeze.R

class ProcessViewHolderAdapter(private val context: Context) :
    RecyclerView.Adapter<ProcessViewHolderAdapter.ViewHolder>() {
    var datas = mutableListOf<ProcessData>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycler_process_row, parent,false)
        return ViewHolder(view)
    }
    override fun getItemCount(): Int = datas.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(datas[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val processName: TextView = itemView.findViewById(R.id.process_name)
        private val processRx: TextView = itemView.findViewById(R.id.process_rx_textview)
        private val processTx: TextView = itemView.findViewById(R.id.process_tx_textview)

        fun bind(item: ProcessData) {
            processName.text = item.name
            processRx.text = item.rx.toString()
            processTx.text = item.tx.toString()
        }
    }
}