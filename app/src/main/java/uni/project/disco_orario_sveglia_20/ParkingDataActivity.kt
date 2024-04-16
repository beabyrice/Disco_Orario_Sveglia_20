package uni.project.disco_orario_sveglia_20

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import uni.project.disco_orario_sveglia_20.databinding.ActivityHomeBinding
import uni.project.disco_orario_sveglia_20.db.ParkingDatabase
import uni.project.disco_orario_sveglia_20.repository.ParkingRepository
import uni.project.disco_orario_sveglia_20.viewModel.ParkingViewModel
import uni.project.disco_orario_sveglia_20.viewModel.ViewModelFactory

class ParkingDataActivity: AppCompatActivity(){

    private lateinit var binding: ActivityHomeBinding
    lateinit var viewModel: ParkingViewModel

    @SuppressLint("ResourceType", "UseRequireInsteadOfGet")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()
        viewModel.getParking()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val navHostFragment = supportFragmentManager.findFragmentById(R.id.homeNavHostFragment) as NavHostFragment
        val navController = navHostFragment.findNavController()
        binding.bottomNavigationView.setupWithNavController(navController)
    }

    private fun setUpViewModel(){
        val parkingRepository = ParkingRepository(ParkingDatabase(this))
        val viewModelProviderFactory = ViewModelFactory(application,parkingRepository)
        viewModel = ViewModelProvider(this,viewModelProviderFactory)[ParkingViewModel::class.java]
    }

}