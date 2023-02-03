package com.codingwithsandeep.weatherapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codingwithsandeep.weatherapp.R
import com.codingwithsandeep.weatherapp.model.Times

class TimeAdapter(private val timeList: List<Times>) :
    RecyclerView.Adapter<TimeAdapter.TimesViewHolder>() {

    inner class TimesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        val tvDegree: TextView = itemView.findViewById(R.id.tvDegree)
        val ivWeatherIcon: ImageView = itemView.findViewById(R.id.ivWeatherIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.rv_time_items,
                parent,
                false
            )
        return TimesViewHolder(view)
    }

    override fun getItemCount(): Int = timeList.size

    override fun onBindViewHolder(holder: TimesViewHolder, position: Int) {
        holder.tvTime.text = timeList[position].times
        holder.tvDegree.text = timeList[position].temp
        holder.ivWeatherIcon.setImageResource(timeList[position].icons)
    }
}