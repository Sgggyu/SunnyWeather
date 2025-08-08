package com.example.sunnyweather.ui.place


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sunnyweather.logic.model.Place
import androidx.lifecycle.switchMap
import com.example.sunnyweather.logic.Repository

class PlaceViewModel: ViewModel() {
    private val searchLiveData = MutableLiveData<String>()
    val placeList = ArrayList<Place>()
    val placeLiveData = searchLiveData.switchMap {
        Repository.searchPlaces(it)
    }
    fun searchPlaces(query: String){
        searchLiveData.value = query
    }
    fun savePlace(place: Place) = Repository.savePlace(place)
    fun getSavedPlace() = Repository.getSavedPlace()
    fun isPlacedSaved() = Repository.isPlaceSaved()
}