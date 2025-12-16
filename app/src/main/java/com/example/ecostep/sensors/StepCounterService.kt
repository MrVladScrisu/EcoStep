package com.example.ecostep.sensors

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Serviciu pentru monitorizarea pașilor și distanței parcurse
 */
class StepCounterService(private val context: Context) : SensorEventListener, LocationListener {
    
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    
    private val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    
    private val _steps = MutableStateFlow(0)
    val steps: StateFlow<Int> = _steps.asStateFlow()
    
    private val _distanceKm = MutableStateFlow(0.0)
    val distanceKm: StateFlow<Double> = _distanceKm.asStateFlow()
    
    private var initialSteps = 0
    private var isInitialized = false
    
    private var lastLocation: Location? = null
    private var totalDistance = 0.0
    
    /**
     * Pornește monitorizarea pașilor
     */
    fun startTracking() {
        // Verifică permisiuni pentru pași
        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            stepSensor?.let {
                sensorManager.registerListener(
                    this,
                    it,
                    SensorManager.SENSOR_DELAY_UI
                )
            }
        }
        
        // Verifică permisiuni pentru locație
        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            try {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    10000, // 10 secunde
                    10f,   // 10 metri
                    this
                )
            } catch (e: SecurityException) {
                // Ignorăm dacă nu avem permisiuni
            }
        }
    }
    
    /**
     * Oprește monitorizarea
     */
    fun stopTracking() {
        sensorManager.unregisterListener(this)
        try {
            locationManager.removeUpdates(this)
        } catch (e: SecurityException) {
            // Ignorăm
        }
    }
    
    /**
     * Resetează contorul de pași pentru ziua curentă
     */
    fun resetDailySteps() {
        isInitialized = false
        initialSteps = 0
        _steps.value = 0
        totalDistance = 0.0
        _distanceKm.value = 0.0
        lastLocation = null
    }
    
    // SensorEventListener
    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                val totalSteps = it.values[0].toInt()
                
                if (!isInitialized) {
                    initialSteps = totalSteps
                    isInitialized = true
                }
                
                _steps.value = totalSteps - initialSteps
            }
        }
    }
    
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Nu facem nimic
    }
    
    // LocationListener
    override fun onLocationChanged(location: Location) {
        lastLocation?.let { last ->
            val distance = last.distanceTo(location)
            totalDistance += distance
            _distanceKm.value = totalDistance / 1000.0 // Convertim în km
        }
        lastLocation = location
    }
    
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        // Deprecated, nu facem nimic
    }
    
    override fun onProviderEnabled(provider: String) {
        // Nu facem nimic
    }
    
    override fun onProviderDisabled(provider: String) {
        // Nu facem nimic
    }
    
    /**
     * Verifică dacă dispozitivul are senzor de pași
     */
    fun hasStepCounter(): Boolean {
        return stepSensor != null
    }
    
    /**
     * Verifică dacă avem permisiuni pentru pași
     */
    fun hasStepPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACTIVITY_RECOGNITION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * Verifică dacă avem permisiuni pentru locație
     */
    fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}

