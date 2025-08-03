package com.example.sunnyweather.logic.network

import com.example.sunnyweather.logic.model.PlaceResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import retrofit2.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


object SunnyWeatherNetwork {
    private val placeService = ServiceCreator.create<PlaceService>()
    suspend fun searchPlaces(query: String): PlaceResponse{
        return placeService.getData(query).await() //这里使用了刚刚的扩展函数
    }

    /**
     * 注意 这是一个扩展函数，扩展Call类，函数名为await()
     */
    private suspend fun <T> Call<T>.await():T{
        return suspendCoroutine {
            enqueue(object : Callback<T>{
                override fun onResponse(
                    call: Call<T?>,
                    response: Response<T?>
                ) {
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