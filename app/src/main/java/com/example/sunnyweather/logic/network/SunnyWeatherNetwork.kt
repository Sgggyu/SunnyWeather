package com.example.sunnyweather.logic.network

import android.util.Log
import com.example.sunnyweather.logic.model.DailyResponse
import com.example.sunnyweather.logic.model.PlaceResponse
import com.example.sunnyweather.logic.model.RealtimeResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


object SunnyWeatherNetwork {
    private val placeService = ServiceCreator.create<PlaceService>()
    suspend fun searchPlaces(query: String): PlaceResponse{
        return placeService.getData(query).await() //这里使用了刚刚的扩展函数
    }
    private val weatherService = ServiceCreator.create<WeatherService>()
    suspend fun getRealtimeWeather(lng: String,lat: String): RealtimeResponse{
        return weatherService.getRealtimeWeather(lng,lat).await()
    }
    suspend fun getDailyWeather(lng: String,lat: String): DailyResponse{
        return weatherService.getDailyWeather(lng,lat).await()
    }
    /**
     * 注意 这是一个扩展函数，扩展Call类，函数名为await()
     * 统一使用了await作为接口回调函数
     */
    private suspend fun <T> Call<T>.await():T{
        return suspendCoroutine {
            enqueue(object : Callback<T>{
                override fun onResponse(
                    call: Call<T?>,
                    response: Response<T?>
                ) {
                    Log.v("SunnyWeatherNetwork",response.toString())
                    val body = response.body()
                    if (body!=null){
                        it.resume(body)
                    }else{
                        val e = Exception("response body is null")
                        it.resumeWithException(e)
                    }

                }

                override fun onFailure(call: Call<T?>, t: Throwable) {
                    it.resumeWithException(t)
                }

            })
        }
    }
}