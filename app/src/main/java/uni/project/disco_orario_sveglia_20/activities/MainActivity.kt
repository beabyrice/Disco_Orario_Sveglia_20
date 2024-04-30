package uni.project.disco_orario_sveglia_20.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.ViewModelProvider
import uni.project.disco_orario_sveglia_20.R
import uni.project.disco_orario_sveglia_20.db.ParkingDatabase
import uni.project.disco_orario_sveglia_20.repository.ParkingRepository
import uni.project.disco_orario_sveglia_20.repository.TimeRepository
import uni.project.disco_orario_sveglia_20.viewModel.MainViewModel
import uni.project.disco_orario_sveglia_20.viewModel.ViewModelFactory
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var manualEditText: EditText
    private lateinit var durationEditText: EditText
    private lateinit var confirm: Button
    private lateinit var switchCompat: SwitchCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpViews()
        setUpViewModel()
        mainViewModel.setFusedLocationProvider(this)

        confirm.setOnClickListener {
            if (
                (TimeRepository.isValidTime(manualEditText.hint.toString()) || TimeRepository.isValidTime(manualEditText.text.toString()))
                && TimeRepository.isValidTime(durationEditText.text.toString())
            ) {
                if (switchCompat.isChecked && TimeRepository.isValidTime(manualEditText.text.toString())) {
                    mainViewModel.setTime(manualEditText.text.toString())
                } else {
                    mainViewModel.setTime(manualEditText.hint.toString())
                }
                mainViewModel.setParkingDuration(durationEditText.text.toString())
                mainViewModel.upsertParking()

                val intent = Intent(this, ParkingDataActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Wrong format, use 00:00", Toast.LENGTH_LONG).show()
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        mainViewModel.handlePermissionsResult(requestCode, grantResults, this)
    }

    private fun setUpViews() {
        val activity = this
        manualEditText = findViewById(R.id.manualEditText)
        manualEditText.hint =
            LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")).toString()
        durationEditText = findViewById(R.id.durationEditText)
        confirm = findViewById(R.id.manualTimeBt)
        switchCompat = findViewById(R.id.switch1)
        switchCompat.apply {
            setThumbResource(R.drawable.switch_thumb_custom)
            setTrackResource(R.drawable.switch_track_custom)
            setOnCheckedChangeListener { button, _ ->
                if (button.isChecked) enableManualInsertion(activity)
                else backToAutomaticInsertion(activity)
            }
        }

    }

    private fun backToAutomaticInsertion(activity: Activity) {
        manualEditText.apply {
            isFocusable = false
            isFocusableInTouchMode = false
            setHintTextColor(getColor(R.color.disabled))
            background = AppCompatResources.getDrawable(activity, R.drawable.input_bg_disabled)
        }
    }

    private fun enableManualInsertion(activity: Activity) {
        val typedValue = TypedValue()
        this.theme.resolveAttribute(androidx.appcompat.R.attr.colorAccent, typedValue, true)
        val color = typedValue.resourceId
        manualEditText.apply {
            isFocusable = true
            isFocusableInTouchMode = true
            setHintTextColor(getColor(color))
            background = AppCompatResources.getDrawable(activity, R.drawable.input_background)
        }
    }

    private fun setUpViewModel() {
        val parkingRepository = ParkingRepository(ParkingDatabase(this))
        val viewModelProviderFactory = ViewModelFactory(application, parkingRepository)
        mainViewModel = ViewModelProvider(this, viewModelProviderFactory)[MainViewModel::class.java]
    }

}