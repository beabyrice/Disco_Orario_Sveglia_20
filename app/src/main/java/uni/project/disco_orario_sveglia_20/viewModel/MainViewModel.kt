package uni.project.disco_orario_sveglia_20.viewModel

import android.app.Application
import android.media.tv.interactive.AppLinkInfo
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import uni.project.disco_orario_sveglia_20.model.Parking
import uni.project.disco_orario_sveglia_20.db.ParkingDao
import uni.project.disco_orario_sveglia_20.repository.ParkingRepository

class MainViewModel(
    app: Application,
    private val parkingRepository: ParkingRepository
) : AndroidViewModel(app){

    fun upsertParking(parking: Parking){
        viewModelScope.launch {
            parkingRepository.upsertParking(parking)
        }
    }

    fun deleteParking(parking: Parking){
        viewModelScope.launch {
            parkingRepository.deleteParking(parking)
        }
    }
}