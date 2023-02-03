package com.codingwithsandeep.weatherapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.codingwithsandeep.weatherapp.model.Weather
import com.codingwithsandeep.weatherapp.repository.WeatherRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers

class WeatherViewModel(private val weatherRepository: WeatherRepository) : ViewModel() {

    private val weatherResponse: MutableLiveData<Weather?> = MutableLiveData()

    val _weatherData : LiveData<Weather?>
    get() = weatherResponse

    val errorMessage: MutableLiveData<String?> = MutableLiveData()
    lateinit var disposable: Disposable

    fun getCityData(city: String, appId: String){
        val response = weatherRepository.getCityData(city, appId)
        response.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(getCityDataObserver())
    }

    private fun getCityDataObserver(): Observer<Weather> {
        return object : Observer<Weather> {
            override fun onComplete() {
                //hide progress indicator .
            }

            override fun onError(e: Throwable) {
                errorMessage.postValue(e.message)
            }

            override fun onNext(weather: Weather) {
                weatherResponse.postValue(weather)
            }

            override fun onSubscribe(d: Disposable) {
                disposable = d
                //start showing progress indicator.
            }
        }
    }
}