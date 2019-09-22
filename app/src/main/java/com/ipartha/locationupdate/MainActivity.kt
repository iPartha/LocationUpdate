package com.ipartha.locationupdate

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import java.util.*
import android.util.Log
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private lateinit var locationViewModel : LocationViewModel
    private lateinit var textView : TextView
    private lateinit var geocoder : Geocoder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textView = findViewById(R.id.latLong)
        locationViewModel = ViewModelProviders.of(this).get(LocationViewModel::class.java)
        geocoder = Geocoder(this, Locale.getDefault())
    }

    private fun startLocationUpdates() {
        locationViewModel.getLocationData().observe(this, Observer {

            try {
                val addressList = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                if (addressList != null && addressList.size > 0) {
                    val address = addressList[0]
                    val sb = StringBuilder()
                    for (i in 0 until address.maxAddressLineIndex) {
                        sb.append(address.getAddressLine(i)).append("\n")
                    }
                    sb.append(address.subAdminArea).append("\n")
                    sb.append(address.subLocality).append("\n")
                    sb.append(address.locality).append("\n")
                    sb.append(address.postalCode).append("\n")
                    sb.append(address.countryName)
                    textView.text = sb.toString()
                }
            } catch (e: IOException) {
                Log.e("MainActivity", "Unable connect to Geocoder", e)
                textView.text = ""
            }
        })
    }

    private fun isPermissionsGranted() : Boolean {
        return (
                (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) &&
                        (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED))
    }

    override fun onStart() {
        super.onStart()

        if (isPermissionsGranted()){
            startLocationUpdates()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), 100)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            100 -> {
                startLocationUpdates()
            }
        }
    }

}
