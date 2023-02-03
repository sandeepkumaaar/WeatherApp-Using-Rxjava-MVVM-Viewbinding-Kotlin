package com.codingwithsandeep.weatherapp.network

import com.codingwithsandeep.weatherapp.model.Weather
import com.codingwithsandeep.weatherapp.utils.Constants
import io.reactivex.rxjava3.core.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("weather/")
    fun getCityData(
        @Query("q") q:String,
        @Query("appid") appId:String
    ): Observable<Weather>

    companion object {

        var weatherService: WeatherService? = null

        fun getInstance(): WeatherService {

            if (weatherService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .build()
                weatherService = retrofit.create(WeatherService::class.java)
            }
            return weatherService!!
        }
    }

}