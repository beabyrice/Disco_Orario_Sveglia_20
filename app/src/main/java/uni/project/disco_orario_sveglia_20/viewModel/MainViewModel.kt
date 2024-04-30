package uni.project.disco_orario_sveglia_20.viewModel

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uni.project.disco_orario_sveglia_20.model.Parking
import uni.project.disco_orario_sveglia_20.repository.ParkingRepository
import uni.project.disco_orario_sveglia_20.repository.TimeRepository

class MainViewModel(
    app: Application,
    private val parkingRepository: ParkingRepository
) : AndroidViewModel(app){
    companion object{
        const val TAG = "HomeViewModel"
    }

    private val FINE_PERMISSION_CODE = 1
    private lateinit var currentLocation : LatLng
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var currentTime = 0L
    private var parkingDuration = 0L

    fun setFusedLocationProvider(activity: Activity){
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)
        getLastLocation(activity)
    }

    //TODO: apk doesnt work!
    private fun getLastLocation(activity: Activity) {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), FINE_PERMISSION_CODE)
            return
        }
        val task = fusedLocationProviderClient.lastLocation
        task.addOnSuccessListener {location ->
            location?.let {
                currentLocation = LatLng(location.latitude,location.longitude)
            }?: run{
                Log.d(TAG, "entrato")
        }
        }
    }

    private fun getParking(): Parking{
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

    fun setTime(time: String){
        currentTime = TimeRepository.getLongSecondsFromString(time)
    }

    fun upsertParking(){
        viewModelScope.launch(Dispatchers.IO) {
            val parking = getParking()
            parkingRepository.upsertParking(parking)
        }
    }

    fun handlePermissionsResult(requestCode: Int, grantResults: IntArray, activity: Activity) {
        if(requestCode == FINE_PERMISSION_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation(activity)
            }else{
                Toast.makeText(activity, "location not permitted",Toast.LENGTH_SHORT).show()
            }
        }
    }
}