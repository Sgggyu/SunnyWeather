package com.example.sunnyweather

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.sunnyweather.databinding.ActivityWeatherBinding
import com.example.sunnyweather.logic.model.Weather
import com.example.sunnyweather.ui.weather.WeatherViewModel
import com.sunnyweather.android.logic.model.getSky
import java.text.SimpleDateFormat
import java.util.Locale

class WeatherActivity : AppCompatActivity() {
    lateinit var binding: ActivityWeatherBinding
    val viewModel by lazy {
        ViewModelProvider(this).get(WeatherViewModel::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //用enableEdgeToEdge来代替原来的已被弃用的状态栏透明写法
        enableEdgeToEdge()
        binding = ActivityWeatherBinding.inflate(layoutInflater)

        setContentView(binding.root)

        if (viewModel.locationLng.isEmpty()){
            viewModel.locationLng = intent.getStringExtra("location_lng")?:""
        }
        if (viewModel.locationLat.isEmpty()){
            viewModel.locationLat = intent.getStringExtra("location_lat")?:""
        }
        if (viewModel.placeName.isEmpty()){
            viewModel.placeName = intent.getStringExtra("place_name")?:""
        }
        viewModel.weatherLiveData.observe(this, Observer{
            val weather = it.getOrNull()
            if (weather != null){
                //在前端显示数据
                showWeatherInfo(weather)
            }else{
                Toast.makeText(this,"无法获取天气信息，见擦汗是否连接网络", Toast.LENGTH_LONG).show()
                it.exceptionOrNull()?.printStackTrace()
            }
            binding.swipeWeather.isRefreshing = false
        })
        binding.swipeWeather.setColorSchemeResources(R.color.purple_200)
        viewModel.refreshWeather(viewModel.locationLng,viewModel.locationLat)
        binding.swipeWeather.setOnRefreshListener {
            refreshWeather()
        }
        binding.layoutNow.btnNav.setOnClickListener {
            binding.drawerWeather.openDrawer(GravityCompat.START)
        }
        binding.drawerWeather.addDrawerListener(object : DrawerLayout.DrawerListener{
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

            }

            override fun onDrawerOpened(drawerView: View) {

            }

            override fun onDrawerClosed(drawerView: View) {
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE)
                        as InputMethodManager
                manager.hideSoftInputFromWindow(drawerView.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS)
            }

            override fun onDrawerStateChanged(newState: Int) {

            }

        })
    }

    private fun showWeatherInfo(weather:Weather){
        binding.layoutNow.tvPlaceName.text = viewModel.placeName
        val realtime = weather.realtime
        val daily = weather.daily
        val currentTempText = "${realtime.temperature.toInt()}℃"
        binding.layoutNow.tvCurrentTemp.text = currentTempText
        binding.layoutNow.tvCurrentSky.text = getSky(realtime.skycon).info
        val currentPM25Text = "空气指数${realtime.airQuality.aqi.chn.toInt()}"
        binding.layoutNow.tvCurrentAQI.text = currentPM25Text
        binding.layoutNow.relativeNow.setBackgroundResource(getSky(realtime.skycon).bg)
        //填充foreCAST布局
        binding.layoutForcast.LinearForecastLayout.removeAllViews()
        val days = daily.skycon.size
        for (i in 0 until days){
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            val view = LayoutInflater.from(this).inflate(R.layout.forecast_item,binding.layoutForcast.LinearForecastLayout,false)
            val dateInfo: TextView = view.findViewById(R.id.tv_dateInfo)
            val skyIcon: ImageView = view.findViewById(R.id.img_skyIcon)
            val skyInfo: TextView = view.findViewById(R.id.tv_sky_info)
            val temperatureInfo: TextView = view.findViewById(R.id.tv_temperature_info)
            val simpleDataFormat = SimpleDateFormat("yyyy-MM-dd",Locale.getDefault())
            dateInfo.text = simpleDataFormat.format(skycon.date)
            val sky = getSky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text = sky.info
            val tempText = "${temperature.min.toInt()}~${temperature.max.toInt()}℃"
            temperatureInfo.text = tempText
            binding.layoutForcast.LinearForecastLayout.addView(view)
        }
        val lifeIndex = daily.lifeIndex
        binding.layoutLifeIndex.tvColdRisk.text = lifeIndex.coldRisk[0].desc
        binding.layoutLifeIndex.tvDressing.text = lifeIndex.dressing[0].desc
        binding.layoutLifeIndex.tvUltraviolet.text = lifeIndex.ultraviolet[0].desc
        binding.layoutLifeIndex.tvCarWashing.text = lifeIndex.carWashing[0].desc
        binding.layoutWeather.visibility = View.VISIBLE
    }

    fun refreshWeather(){
        viewModel.refreshWeather(viewModel.locationLng,viewModel.locationLat)
        binding.swipeWeather.isRefreshing = true
    }
}

