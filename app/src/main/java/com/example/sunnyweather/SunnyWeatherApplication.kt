package com.example.sunnyweather

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

/**
 * 自定义Application,其静态属性context可用于全局获取
 * TOKEN属性是彩云接口申请下来的令牌，易于调用api
 * 注意:需要在manifest文件中更改原来的application指定
 *
 */
class SunnyWeatherApplication: Application() {
    companion object{
        const val TOKEN = "【输入自己申请的api】"
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}