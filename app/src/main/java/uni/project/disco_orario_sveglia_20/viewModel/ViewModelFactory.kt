package uni.project.disco_orario_sveglia_20.viewModel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import uni.project.disco_orario_sveglia_20.repository.ParkingRepository

class ViewModelFactory(
    val application: Application,
    private val parkingRepository: ParkingRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(application,parkingRepository) as T
    }
}