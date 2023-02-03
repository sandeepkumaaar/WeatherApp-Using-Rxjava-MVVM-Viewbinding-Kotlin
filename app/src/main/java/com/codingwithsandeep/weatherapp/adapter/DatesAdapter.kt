package com.codingwithsandeep.weatherapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.codingwithsandeep.weatherapp.R
import com.codingwithsandeep.weatherapp.model.Months

class DatesAdapter(private val datesNweekList: List<Months>) :
    RecyclerView.Adapter<DatesAdapter.DatesViewHolder>() {

    inner class DatesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvWeek: TextView = itemView.findViewById(R.id.tvWeek)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val ivWeatherIcon: ImageView = itemView.findViewById(R.id.ivWeatherIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DatesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.rv_date_n_week_item,
                parent,
                false
            )
        return DatesViewHolder(view)
    }

    override fun getItemCount(): Int = datesNweekList.size

    override fun onBindViewHolder(holder: DatesViewHolder, position: Int) {
        holder.tvWeek.text = datesNweekList[position].weeks
        holder.tvDate.text = datesNweekList[position].dates.toString()
        holder.ivWeatherIcon.setImageResource(datesNweekList[position].icons)
    }
}