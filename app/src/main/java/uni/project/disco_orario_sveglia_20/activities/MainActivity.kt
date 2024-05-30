package uni.project.disco_orario_sveglia_20.activities

import android.app.Activity
import android.app.TimePickerDialog
import android.content.Context
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
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var mainViewModel: MainViewModel
    private lateinit var manualEditText: EditText
    private lateinit var durationEditText: EditText
    private lateinit var confirmButton: Button
    private lateinit var switchCompat: SwitchCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val parkingRepository = ParkingRepository(ParkingDatabase(this))
        setUpViewModel(parkingRepository)
        setContentView(R.layout.activity_main)
        setUpViews()


        val sharedPref = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        sharedPref.edit().putBoolean("hasAlreadyRun", false).apply()

        mainViewModel.setFusedLocationProvider(this)
        mainViewModel.manageLocationPermission(this)

        confirmButton.setOnClickListener {
            if (mainViewModel.isLocationInitialized()) {
                handleConfirmClick()
            } else {
                showToast(R.string.current_location_not_initialized)
                mainViewModel.getLocationUpdate(this)
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
        manualEditText = findViewById<EditText?>(R.id.manualEditText).apply {
            hint = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")).toString()
            setTimePicker(this)
        }
        durationEditText = findViewById<EditText?>(R.id.durationEditText).apply {
            setTimePicker(this)
        }
        confirmButton = findViewById(R.id.manualTimeBt)
        switchCompat = findViewById<SwitchCompat>(R.id.switch1).apply {
            setThumbResource(R.drawable.switch_thumb_custom)
            setTrackResource(R.drawable.switch_track_custom)
            setOnCheckedChangeListener { button, _ ->
                if (button.isChecked) enableManualInsertion(this@MainActivity)
                else backToAutomaticInsertion(this@MainActivity)
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
        this.theme.resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true)
        manualEditText.apply {
            isFocusable = true
            isFocusableInTouchMode = true
            setHintTextColor(getColor(typedValue.resourceId))
            background = AppCompatResources.getDrawable(activity, R.drawable.input_background)
        }
    }

    private fun setUpViewModel(parkingRepository: ParkingRepository) {
        val viewModelProviderFactory = ViewModelFactory(application, parkingRepository)
        mainViewModel = ViewModelProvider(this, viewModelProviderFactory)[MainViewModel::class.java]
    }

    private fun setTimePicker(text: EditText){
        text.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus) {
                val c = Calendar.getInstance()
                val hour = c.get(Calendar.HOUR_OF_DAY)
                val minute = c.get(Calendar.MINUTE)
                val timePickerDialog = TimePickerDialog(
                    this,
                    { _, hourOfDay, minuteOfHour ->
                        text.setText(String.format("%02d:%02d", hourOfDay, minuteOfHour))
                    },
                    hour,
                    minute,
                    true
                )
                timePickerDialog.setOnDismissListener {
                    text.clearFocus()
                }
                timePickerDialog.show()
            }
        }
    }

    private fun handleConfirmClick() {
        val manualTime = manualEditText.text.toString()
        val hintTime = manualEditText.hint.toString()
        val durationTime = durationEditText.text.toString()

        if ((TimeRepository.isValidTime(manualTime) || TimeRepository.isValidTime(hintTime)) &&
            TimeRepository.isValidTime(durationTime)) {
            if (switchCompat.isChecked && TimeRepository.isValidTime(manualTime)) {
                mainViewModel.setArrivalTime(manualTime)
            } else {
                mainViewModel.setArrivalTime(hintTime)
            }
            mainViewModel.setParkingDuration(durationTime)
            mainViewModel.upsertParking()

            startActivity(Intent(this, ParkingDataActivity::class.java))
            finish()
        } else {
            showToast(R.string.wrong_format)
        }
    }

    private fun showToast(messageResId: Int) {
        Toast.makeText(this, messageResId, Toast.LENGTH_LONG).show()
    }
}