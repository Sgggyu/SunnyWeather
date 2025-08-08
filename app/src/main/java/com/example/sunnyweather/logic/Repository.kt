package com.example.sunnyweather.logic

import androidx.lifecycle.liveData
import com.example.sunnyweather.logic.dao.PlaceDao
import com.example.sunnyweather.logic.model.Place
import com.example.sunnyweather.logic.model.Weather
import com.example.sunnyweather.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

object Repository{
    fun searchPlaces(query: String) = liveData(Dispatchers.IO) {
        val result = try{
            val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
            if (placeResponse.status == "ok"){
                val places = placeResponse.places
                Result.success(places)
            }else{
                Result.failure(RuntimeException("response status is ${placeResponse.status}"))
            }
        }catch (e: Exception){
            Result.failure<List<Place>>(e)
        }
        emit(result)
    }

    fun refreshWeather(lng: String,lat: String) = liveData(Dispatchers.IO){
        val result = try{
            coroutineScope {
                val deferredRealtime = async { SunnyWeatherNetwork.getRealtimeWeather(lng,lat)}
                val deferredDaily = async { SunnyWeatherNetwork.getDailyWeather(lng,lat)}
                val realtimeResponse = deferredRealtime.await()
                val dailyResponse = deferredDaily.await()
                if (realtimeResponse.status == "ok" && dailyResponse.status == "ok"){
                    val weather = Weather(realtimeResponse.result.realtime ,dailyResponse.result.daily)
                    Result.success(weather)
                }else{
                    Result.failure(
                        RuntimeException("realtime response status is ${realtimeResponse.status}" +
                                "daily response status is ${dailyResponse.status}")
                    )
                }
            }
        }catch (e: Exception){
            Result.failure<Weather>(e)
        }
        emit(result)
    }

    fun savePlace(place: Place) = PlaceDao.savePlace(place)
    fun getSavedPlace() = PlaceDao.getSavedPlace()
    fun isPlaceSaved() = PlaceDao.isPlaceSaved()

}