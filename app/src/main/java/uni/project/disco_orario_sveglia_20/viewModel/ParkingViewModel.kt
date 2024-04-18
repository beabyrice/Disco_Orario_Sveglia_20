package uni.project.disco_orario_sveglia_20.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import uni.project.disco_orario_sveglia_20.model.Parking
import uni.project.disco_orario_sveglia_20.repository.ParkingRepository

class ParkingViewModel(
    app: Application,
    private val parkingRepository: ParkingRepository
) : AndroidViewModel(app) {

    private var parkingFlow = MutableStateFlow<Parking?>(null)
    val parking = parkingFlow.asStateFlow()

    fun getParking(){
        viewModelScope.launch (Dispatchers.IO){
            parkingFlow.update { parkingRepository.getParking() }
        }
    }
    fun deleteParking(){
        viewModelScope.launch (Dispatchers.IO){
            parking.value?.let { parkingRepository.deleteParking(it) }
        }
    }

    fun getMyCarLocation(): LatLng? {
        parking.value?.let { parking ->
            return LatLng(parking.latitude, parking.longitude)
        }
        return null
    }

    fun getParkingDuration() : Long?{
        parking.value?.let { parking ->
            return parking.parkingDuration
        }
        return null
    }

}