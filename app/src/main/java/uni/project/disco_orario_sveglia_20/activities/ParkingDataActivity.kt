package uni.project.disco_orario_sveglia_20.activities

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.os.VibratorManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import uni.project.disco_orario_sveglia_20.R
import uni.project.disco_orario_sveglia_20.alarm.CountDownTimerService
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
        if(intent.getBooleanExtra("stop_vibration", false)){
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                (getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
            } else {
                getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
            vibrator.cancel()
        }
        setUpViewModel()
        parkingViewModel.getParking()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(!parkingViewModel.isCameraPermissionOk(this)) {
            parkingViewModel.getCameraPermission(this)
        }

        val sharedPref = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val hasTimerRun = sharedPref.getBoolean("hasAlreadyRun", false)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.homeNavHostFragment) as NavHostFragment
        val navController = navHostFragment.findNavController()
        binding.bottomNavigationView.setupWithNavController(navController)

        if(!hasTimerRun){
            val serviceIntent = Intent(this, CountDownTimerService::class.java)
            startForegroundService(serviceIntent)
        }

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
        val sharedPref = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        sharedPref.edit().putBoolean("hasAlreadyRun", false).apply()
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