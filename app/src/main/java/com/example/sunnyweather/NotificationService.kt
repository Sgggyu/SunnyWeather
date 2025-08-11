package com.example.sunnyweather

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Observer
import com.example.sunnyweather.logic.Repository
import com.example.sunnyweather.logic.dao.PlaceDao
import com.example.sunnyweather.logic.model.Weather

class NotificationService : Service() {
    lateinit var manager : NotificationManager
    val chanel1 = NotificationChannel("front_service","FRONT_SERVICE", NotificationManager.IMPORTANCE_HIGH)
    val chanel2 = NotificationChannel("warning","WARNING", NotificationManager.IMPORTANCE_HIGH)
    //方便后续添加通知
    //给全局源进行一个监听
    private val weatherObserver = Observer<Result<Weather>> { result ->
        result.onSuccess { weather ->
            // 更新通知或前台服务UI
            refreshFrontService(weather)
        }.onFailure {
            Log.e("WeatherService", "Weather update failed", it)
        }
    }

    companion object {
        const val NOTIFICATION_ID_FOREGROUND = 1
    }

    private val refreshHandler = Handler(Looper.getMainLooper())

    private val refreshRunnable = object : Runnable {
        override fun run() {
            Repository.refreshGlobalWeather(PlaceDao.getSavedPlace().location)
            refreshHandler.postDelayed(this, 30*60*1000) // 30分钟
        }
    }

    override fun onCreate() {
        super.onCreate()
        manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(chanel1)
        manager.createNotificationChannel(chanel2)
        Repository.globalWeatherData.observeForever(weatherObserver)
        startFrontService()
    }
    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        refreshHandler.post(refreshRunnable)
        return START_STICKY
    }

    override fun onDestroy() {
        refreshHandler.removeCallbacks(refreshRunnable)
        Repository.globalWeatherData.removeObserver(weatherObserver)
        super.onDestroy()
    }

    fun startFrontService(){
        val intent = Intent(this, MainActivity::class.java)
        val pi = PendingIntent.getActivity(this,0,intent, PendingIntent.FLAG_IMMUTABLE )
        val notification = NotificationCompat.Builder(this, "front_service").apply {
            setContentTitle("")
            setContentText("")
            setSmallIcon(R.drawable.ic_light_haze)
            setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_cloudy))
            setContentIntent(pi) // 使用onCreate中创建的PendingIntent
            setAutoCancel(false)
            setOngoing(true)
            priority = NotificationCompat.PRIORITY_HIGH
        }.build()
        startForeground(NOTIFICATION_ID_FOREGROUND, notification)
    }

    fun refreshFrontService(weather: Weather){
        val name = PlaceDao.getSavedPlace().name
        val realtime = weather.realtime
        val daily = weather.daily
        val temperature = daily.temperature[0]
        val intent = Intent(this, MainActivity::class.java)
        val pi = PendingIntent.getActivity(this,0,intent, PendingIntent.FLAG_IMMUTABLE )
        val newNotification = NotificationCompat.Builder(this, "front_service").apply {
            setContentTitle("${name} ")
            setContentText("${temperature.min.toInt()}~${temperature.max.toInt()}℃"+
            " 当前${realtime.temperature.toInt()}℃|空气指数${realtime.airQuality.aqi.chn.toInt()}")
            setSmallIcon(R.mipmap.ic_launcher)
            setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_cloudy))
            setContentIntent(pi) // 使用onCreate中创建的PendingIntent
            setAutoCancel(false)
            setOngoing(true)
            priority = NotificationCompat.PRIORITY_HIGH
        }.build()

        manager.notify(NOTIFICATION_ID_FOREGROUND, newNotification)

    }


}