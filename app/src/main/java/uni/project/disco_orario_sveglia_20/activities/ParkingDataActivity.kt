package uni.project.disco_orario_sveglia_20.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import uni.project.disco_orario_sveglia_20.R
import uni.project.disco_orario_sveglia_20.databinding.ActivityHomeBinding
import uni.project.disco_orario_sveglia_20.db.ParkingDatabase
import uni.project.disco_orario_sveglia_20.repository.ParkingRepository
import uni.project.disco_orario_sveglia_20.viewModel.ParkingViewModel
import uni.project.disco_orario_sveglia_20.viewModel.ViewModelFactory

class ParkingDataActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    lateinit var parkingViewModel: ParkingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()
        parkingViewModel.getParking()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        parkingViewModel.getCameraPermission(this)


        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.homeNavHostFragment) as NavHostFragment
        val navController = navHostFragment.findNavController()
        binding.bottomNavigationView.setupWithNavController(navController)
    }

    private fun setUpViewModel() {
        val parkingRepository = ParkingRepository(ParkingDatabase(this))
        val viewModelProviderFactory = ViewModelFactory(application, parkingRepository)
        parkingViewModel =
            ViewModelProvider(this, viewModelProviderFactory)[ParkingViewModel::class.java]
    }

    override fun onDestroy() {
        super.onDestroy()
        parkingViewModel.deleteImageFile(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        parkingViewModel.handlePermissionsResult(requestCode, grantResults, this)
    }

}