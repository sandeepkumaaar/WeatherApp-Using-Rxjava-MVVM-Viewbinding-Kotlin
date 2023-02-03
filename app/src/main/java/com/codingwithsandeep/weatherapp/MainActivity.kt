package com.codingwithsandeep.weatherapp

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.codingwithsandeep.weatherapp.adapter.DatesAdapter
import com.codingwithsandeep.weatherapp.adapter.TimeAdapter
import com.codingwithsandeep.weatherapp.databinding.ActivityMainBinding
import com.codingwithsandeep.weatherapp.model.Months
import com.codingwithsandeep.weatherapp.model.Times
import com.codingwithsandeep.weatherapp.network.WeatherService
import com.codingwithsandeep.weatherapp.repository.WeatherRepository
import com.codingwithsandeep.weatherapp.viewModel.WeatherViewModel
import com.codingwithsandeep.weatherapp.viewModel.WeatherViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.math.RoundingMode
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private val permissionId = 2
    private lateinit var binding: ActivityMainBinding
    private lateinit var weatherViewModelFactory: WeatherViewModelFactory
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var weatherRepository: WeatherRepository
    private val weatherService = WeatherService.getInstance()

    private var monthsList: ArrayList<Months>? = null
    private var timeList: ArrayList<Times>? = null
    private lateinit var datesAdapter: DatesAdapter
    private lateinit var timesAdapter: TimeAdapter
    private var cityName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLocation()

        weatherRepository = WeatherRepository(weatherService)
        weatherViewModelFactory = WeatherViewModelFactory(weatherRepository)

        weatherViewModel = ViewModelProvider(
            this,
            WeatherViewModelFactory(weatherRepository)
        )[WeatherViewModel::class.java]


        weatherViewModel._weatherData.observe(this) { weatherData ->
            Log.d("WeatherResponse_TAG", "onCreate: $weatherData")
            if (weatherData != null) {
                binding.apply {
                    tvCityTemp.text = "" + k2c(weatherData.main.temp!!) + "°C"
                    tvTempDescription.text = weatherData.weather[0].description
                    tvWind.text = weatherData.wind.speed.toString()
                    tvHum.text = weatherData.main.humidity.toString()
                    tvVisiblity.text = weatherData.visibility.toString()
                }
            }
        }

        weatherViewModel.errorMessage.observe(this) {
            //binding.tv.text = it.toString()
        }

        setDates()
        setTimes()

    }

    private fun setDates() {
        monthsList = ArrayList()
        monthsList!!.add(Months("SUN", "05/02", R.drawable.ic_sun_behind_loud))
        monthsList!!.add(Months("SAT", "04/02", R.drawable.ic_sun_black))
        monthsList!!.add(Months("FRI", "03/02", R.drawable.ic_sun))
        monthsList!!.add(Months("THRU", "02/02", R.drawable.ic_sun))
        monthsList!!.add(Months("WED", "01/02", R.drawable.ic_sun))
        monthsList!!.add(Months("TUE", "31/01", R.drawable.ic_sun))
        monthsList!!.add(Months("MON", "30/01", R.drawable.ic_sun_behind_rain_cloud))
        datesAdapter = DatesAdapter(monthsList!!)

        binding.rvDateNweekList.setHasFixedSize(true)
        binding.rvDateNweekList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true)
        binding.rvDateNweekList.adapter = datesAdapter
    }

    private fun setTimes() {
        timeList = ArrayList()
        timeList!!.add(Times("11:00", "14.3", R.drawable.ic_sun_black))
        timeList!!.add(Times("12:00", "16.8", R.drawable.ic_sun_black))
        timeList!!.add(Times("13:00", "15.9", R.drawable.ic_sun_black))
        timeList!!.add(Times("14:00", "18.3", R.drawable.ic_sun_behind_loud))
        timeList!!.add(Times("15:00", "13.8", R.drawable.ic_sun))
        timeList!!.add(Times("16:00", "12.6", R.drawable.ic_sun))
        timeList!!.add(Times("17:00", "12.4", R.drawable.ic_sun))
        timesAdapter = TimeAdapter(timeList!!)

        binding.rvTimeList.setHasFixedSize(true)
        binding.rvTimeList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true)
        binding.rvTimeList.adapter = timesAdapter

    }

    private fun k2c(double: Double): Double {
        var temp = double
        temp = temp.minus(273)
        return temp.toBigDecimal().setScale(1, RoundingMode.UP).toDouble()
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            permissionId
        )
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLocation()
            }
        }
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location != null) {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val list = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                        cityName = list!![0].locality
                        weatherViewModel.getCityData(cityName!!, "a5eeb9e8d254e764d67b7cedc6fe5f5e")

                        Log.d("TAG", "getLocation: $list")
                        binding.apply {
//                            var place = list!![0].getAddressLine(0).split(",")
//                            cityName = place[3]
//                            Log.d("PLACE_TAG", "getLocation: ${place[3]}")
                            tvCityName.text = cityName
                        }

                    }
                }
            } else {
                Toast.makeText(this, "Please turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }
}