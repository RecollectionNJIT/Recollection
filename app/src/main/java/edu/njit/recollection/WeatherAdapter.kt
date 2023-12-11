package edu.njit.recollection

import WeatherValue
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database

class WeatherAdapter (private val context: Context, private val weatherList: List<WeatherValue>): RecyclerView.Adapter<WeatherAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val weatherNameView = itemView.findViewById<TextView>(R.id.rvWeatherName)
        val weatherTempView = itemView.findViewById<TextView>(R.id.rvWeatherTemperature)
        val weatherForecastView = itemView.findViewById<TextView>(R.id.rvWeatherForecast)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.rv_weather_item, parent, false)
        return WeatherAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeatherAdapter.ViewHolder, position: Int) {
        val item = weatherList[position]
        holder.weatherNameView.text = item.name
        holder.weatherTempView.text = item.temperature.toString()
        holder.weatherForecastView.text = item.shortForecast

    }

    override fun getItemCount() = weatherList.size

}