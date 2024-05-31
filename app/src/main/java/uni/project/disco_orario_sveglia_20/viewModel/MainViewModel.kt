package uni.project.disco_orario_sveglia_20.viewModel

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.pm.PackageManager
import android.os.Looper
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uni.project.disco_orario_sveglia_20.R
import uni.project.disco_orario_sveglia_20.model.Parking
import uni.project.disco_orario_sveglia_20.repository.ParkingRepository
import uni.project.disco_orario_sveglia_20.repository.TimeRepository
import java.util.concurrent.TimeUnit

class MainViewModel(
    app: Application,
) : AndroidViewModel(app){

    private val repository = ParkingRepository(app)

    private val FINE_PERMISSION_CODE = 1
    lateinit var currentLocation : LatLng

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private var currentTime = 0L
    private var parkingDuration = 0L

    fun setFusedLocationProvider(activity: Activity){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, TimeUnit.SECONDS.toMillis(30)).setWaitForAccurateLocation(false).build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                result.lastLocation?.let {
                    currentLocation = LatLng(it.latitude, it.longitude)
                }
            }
        }
    }

    private fun isLocationPermitted(activity: Activity): Boolean {
        return !(ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED)
    }

    private fun getLocationPermission(activity: Activity){
        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), FINE_PERMISSION_CODE)
        return
    }

    fun getLocationUpdate(activity: Activity) {
        if(ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED){
            getLocationPermission(activity)
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
    }

    fun isLocationInitialized(): Boolean {
        return this@MainViewModel::currentLocation.isInitialized
    }

    fun manageLocationPermission(activity: Activity){
        if (isLocationPermitted(activity)){
            getLocationUpdate(activity)
        } else {
            getLocationPermission(activity)
        }
    }

    private fun setParking(): Parking{
        return Parking(
            latitude = currentLocation.latitude,
            longitude = currentLocation.longitude,
            arrivalTime = currentTime,
            parkingDuration = parkingDuration
        )
    }

    fun setParkingDuration(duration: String){
        parkingDuration = TimeRepository.getLongSecondsFromString(duration)
    }

    fun setArrivalTime(time: String){
        currentTime = TimeRepository.getLongSecondsFromString(time)
    }

    fun upsertParking(){
        viewModelScope.launch(Dispatchers.IO) {
            val parking = setParking()
            repository.upsertParking(parking)
        }
    }

    fun handlePermissionsResult(requestCode: Int, grantResults: IntArray, activity: Activity) {
        if(requestCode == FINE_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLocationUpdate(activity)
            }else{
                Toast.makeText(activity, R.string.location_permission,Toast.LENGTH_SHORT).show()
            }
        }
    }
}