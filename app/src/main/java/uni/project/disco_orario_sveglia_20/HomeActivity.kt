package uni.project.disco_orario_sveglia_20

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.ViewModelProvider
import uni.project.disco_orario_sveglia_20.db.ParkingDatabase
import uni.project.disco_orario_sveglia_20.repository.ParkingRepository
import uni.project.disco_orario_sveglia_20.repository.TimeRepository
import uni.project.disco_orario_sveglia_20.viewModel.HomeViewModel
import uni.project.disco_orario_sveglia_20.viewModel.ViewModelFactory
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class HomeActivity : AppCompatActivity() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var manualEditText : EditText
    private lateinit var durationEditText : EditText
    private lateinit var confirm : Button
    private lateinit var switchCompat : SwitchCompat


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpViews()
        setUpViewModel()
        viewModel.setFusedLocationProvider(this)
        
        confirm.setOnClickListener {
            if(
                (TimeRepository.isValidTime(manualEditText.hint.toString()) || TimeRepository.isValidTime(manualEditText.text.toString()))
                && TimeRepository.isValidTime(durationEditText.text.toString()))
            {
                if(switchCompat.isChecked){

                    viewModel.setTimeFromUser(manualEditText.text.toString())
                }else{
                    viewModel.setCurrentTime()
                }
                viewModel.completeSetting(durationEditText.text.toString())
                viewModel.upsertParking()
                val intent = Intent(this, ParkingDataActivity::class.java)
                startActivity(intent)
                //finish()
            } else {
                Toast.makeText(this, "wrong format input", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun setUpViews(){
        manualEditText = findViewById(R.id.manualEditText)
        manualEditText.hint = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")).toString()
        durationEditText = findViewById(R.id.durationEditText)
        confirm = findViewById(R.id.manualTimeBt)
        switchCompat = findViewById(R.id.switch1)
        switchCompat.setThumbResource(R.drawable.switch_thumb_custom)
        switchCompat.setTrackResource(R.drawable.switch_track_custom)
        switchCompat.setOnCheckedChangeListener { button, _ ->
            if(button.isChecked) enableManualInsertion(this)
            else backToAutomaticInsertion(this)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        viewModel.handlePermissionsResult(requestCode, grantResults, this)
    }

    private fun backToAutomaticInsertion(activity: Activity){
        manualEditText.apply {
            isFocusable = false
            isFocusableInTouchMode = false
            setHintTextColor(getColor(R.color.disabled))
            background = AppCompatResources.getDrawable(activity,R.drawable.input_bg_disabled)
        }
    }

    private fun enableManualInsertion(activity: Activity) {
        manualEditText.apply {
            isFocusable = true
            isFocusableInTouchMode = true
            setHintTextColor(getColor(R.color.colorAccent))
            background = AppCompatResources.getDrawable(activity,R.drawable.input_barckground)
        }
    }

    private fun setUpViewModel(){
        val parkingRepository = ParkingRepository(ParkingDatabase(this))
        val viewModelProviderFactory = ViewModelFactory(application,parkingRepository)
        viewModel = ViewModelProvider(this,viewModelProviderFactory)[HomeViewModel::class.java]
    }

}