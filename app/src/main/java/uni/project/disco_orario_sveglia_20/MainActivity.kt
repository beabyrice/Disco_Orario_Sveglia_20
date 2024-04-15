package uni.project.disco_orario_sveglia_20

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import uni.project.disco_orario_sveglia_20.db.ParkingDatabase
import uni.project.disco_orario_sveglia_20.model.Parking
import uni.project.disco_orario_sveglia_20.repository.ParkingRepository
import uni.project.disco_orario_sveglia_20.viewModel.MainViewModel
import uni.project.disco_orario_sveglia_20.viewModel.ViewModelFactory
import java.time.LocalTime

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private lateinit var manualEditText : EditText
    private lateinit var durationEditText : EditText
    private lateinit var autoBtn : Button
    private lateinit var confirm : Button
    private lateinit var switchCompat : SwitchCompat
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var currentLatLong : LatLng
    private var currentTime = 0L
    private var parkingDuration = 0L

    companion object{
        private const val LOCATION_CODE = 1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpViewModel()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)


        manualEditText = findViewById(R.id.manualEditText)
        durationEditText = findViewById(R.id.durationEditText)
        autoBtn = findViewById(R.id.autoTimeBt)
        confirm = findViewById(R.id.manualTimeBt)
        switchCompat = findViewById(R.id.switch1)
        switchCompat.setThumbResource(R.drawable.switch_thumb_custom)
        switchCompat.setTrackResource(R.drawable.switch_track_custom)
        switchCompat.setOnCheckedChangeListener { button, isChecked ->
            if(button.isChecked) enableManualInsertion()
            else backToAutomaticInsertion()
        }
        // Set the background color programmatically

        autoBtn.setOnClickListener {
            currentTime = LocalTime.now().toSecondOfDay() * 1000L
        }

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        confirm.setOnClickListener {

            parkingDuration = ((durationEditText.text.toString().toInt()) * 3600 * 1000L)
            setUpMap()
            viewModel.upsertParking(
                Parking(
                    latitude = currentLatLong.latitude,
                    longitude = currentLatLong.longitude,
                    arrivalTime = currentTime,
                    parkingDuration = parkingDuration
                )
            )
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
    }

    private fun backToAutomaticInsertion(){
        manualEditText.inputType = InputType.TYPE_NULL
        manualEditText.isFocusable = false
        manualEditText.isFocusableInTouchMode = false
        manualEditText.setHintTextColor(getColor(R.color.disabled))
        manualEditText.background = AppCompatResources.getDrawable(this,R.drawable.input_bg_disabled)

        autoBtn.isClickable = true
        autoBtn.background.setTint(getColor(R.color.colorAccent))
    }
    private fun enableManualInsertion() {
        manualEditText.inputType = InputType.TYPE_CLASS_DATETIME
        manualEditText.isFocusable = true
        manualEditText.isFocusableInTouchMode = true
        manualEditText.setHintTextColor(getColor(R.color.colorAccent))
        manualEditText.background = AppCompatResources.getDrawable(this,R.drawable.input_barckground)

        autoBtn.isClickable = false
        autoBtn.background.setTint(getColor(R.color.disabled))
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_CODE
            )
            return
        }
        fusedLocationProviderClient.lastLocation.addOnSuccessListener(this) { location ->
            if(location!= null){
                currentLatLong = LatLng(location.latitude, location.longitude)
            }
        }
    }

    private fun setUpViewModel(){
        val parkingRepository = ParkingRepository(ParkingDatabase(this))
        val viewModelProviderFactory = ViewModelFactory(application,parkingRepository)
        viewModel = ViewModelProvider(this,viewModelProviderFactory)[MainViewModel::class.java]
    }

}