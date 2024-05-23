package uni.project.disco_orario_sveglia_20.viewModel

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uni.project.disco_orario_sveglia_20.activities.ParkingDataActivity
import uni.project.disco_orario_sveglia_20.model.Parking
import uni.project.disco_orario_sveglia_20.repository.ParkingRepository
import java.io.File

class ParkingViewModel(
    app: Application,
    private val parkingRepository: ParkingRepository
) : AndroidViewModel(app) {

    private val CAMERA_CODE = 2

    private var parkingFlow = MutableStateFlow<Parking?>(null)
    private val parking = parkingFlow.asStateFlow()

    fun getParking() {
        viewModelScope.launch(Dispatchers.IO) {
            parkingFlow.update { parkingRepository.getParking() }
        }
    }

    fun deleteParking() {
        viewModelScope.launch(Dispatchers.IO) {
            parking.value?.let { parkingRepository.deleteParking(it) }
        }
    }

    fun getMyCarLocation(): LatLng? {
        parking.value?.let { parking ->
            return LatLng(parking.latitude, parking.longitude)
        }
        return null
    }

    fun getParkingDuration(): Long? {
        parking.value?.let { parking ->
            return parking.parkingDuration
        }
        return null
    }

    fun getArrivalTime(): Long? {
        parking.value?.let { parking ->
            return parking.arrivalTime
        }
        return null
    }

    fun getCameraPermission(activity: Activity) {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_CODE
            )
            return
        }

    }

    fun isCameraPermissionOk(activity: Activity): Boolean {
        return ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

     fun createImageUri(activity : Activity): Uri {
        activity.filesDir.listFiles()
        val image = File((activity as ParkingDataActivity).filesDir, "camera_photos.png")
        return FileProvider.getUriForFile(
            activity,
            "uni.project.disco_orario_sveglia_20.FileProvider",
            image
        )
    }

    fun deleteImageFile(activity : Activity) {
        val imageFile = File(activity.filesDir, "camera_photos.png")
        if (imageFile.exists()) {
            imageFile.delete()
        }
    }

    fun getImageFile(activity: Activity) : File? {
        val imageFile = File(activity.filesDir, "camera_photos.png")
        return if (imageFile.exists()) {
            imageFile
        } else {
            null
        }
    }

    fun handlePermissionsResult(
        requestCode: Int,
        grantResults: IntArray,
        parkingDataActivity: Activity
    ) {
        if (requestCode == CAMERA_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCameraPermission(parkingDataActivity)
            } else {
                Toast.makeText(parkingDataActivity, "camera not permitted", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }

}