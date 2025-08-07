package com.example.sunnyweather.ui.place

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.sunnyweather.R
import com.example.sunnyweather.WeatherActivity
import com.example.sunnyweather.logic.model.Place
import java.util.zip.Inflater

class PlaceAdapter(private val fragment: Fragment, private val placeList: List<Place>) : RecyclerView.Adapter<PlaceAdapter.ViewHolder>() {
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val placeName : TextView = view.findViewById(R.id.tv_place_name)
        val placeAddress : TextView = view.findViewById(R.id.tv_place_address)
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaceAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_place,parent,false)
        val holder =  ViewHolder(view)
        holder.itemView.setOnClickListener {
            val position = holder.bindingAdapterPosition
            val place = placeList[position]
            val intent = Intent(parent.context, WeatherActivity::class.java).apply {
                putExtra("location_lng",place.location.lng)
                putExtra("location_lat",place.location.lat)
                putExtra("place_name",place.name)
            }
            fragment.startActivity(intent)
            fragment.activity?.finish()
        }

        return holder
    }

    override fun onBindViewHolder(holder: PlaceAdapter.ViewHolder, position: Int) {
        val place = placeList[position]
        holder.placeName.text = place.name
        holder.placeAddress.text = place.addresses
    }

    override fun getItemCount() = placeList.size

}