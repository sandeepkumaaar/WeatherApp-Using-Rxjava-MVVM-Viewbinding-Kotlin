package com.codingwithsandeep.weatherapp.repository

import com.codingwithsandeep.weatherapp.network.WeatherService

class WeatherRepository(private val weatherService: WeatherService) {

    fun getCityData(city: String, appId: String) = weatherService.getCityData(city, appId)
}