package uni.project.disco_orario_sveglia_20.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import uni.project.disco_orario_sveglia_20.R
import uni.project.disco_orario_sveglia_20.alarm.CountDownTimerService
import uni.project.disco_orario_sveglia_20.databinding.ActivityHomeBinding
import uni.project.disco_orario_sveglia_20.viewModel.ParkingViewModel

class ParkingDataActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    val parkingViewModel: ParkingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        parkingViewModel.getParking()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.homeNavHostFragment) as NavHostFragment
        val navController = navHostFragment.findNavController()
        binding.bottomNavigationView.setupWithNavController(navController)

        if(!parkingViewModel.isCameraPermissionOk(this)) {
            parkingViewModel.getCameraPermission(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        parkingViewModel.deleteImageFile(this)
        stopService(Intent(this, CountDownTimerService::class.java))
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

    fun startForegroundService(){
        val serviceIntent = Intent(this, CountDownTimerService::class.java)
        startForegroundService(serviceIntent)
        val sharedPref = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        sharedPref.getBoolean("hasAlreadyRun", true)
    }

}