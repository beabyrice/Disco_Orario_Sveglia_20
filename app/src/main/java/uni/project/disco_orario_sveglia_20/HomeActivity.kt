package uni.project.disco_orario_sveglia_20

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.ViewModelProvider
import uni.project.disco_orario_sveglia_20.db.ParkingDatabase
import uni.project.disco_orario_sveglia_20.repository.ParkingRepository
import uni.project.disco_orario_sveglia_20.viewModel.HomeViewModel
import uni.project.disco_orario_sveglia_20.viewModel.ViewModelFactory

class HomeActivity : AppCompatActivity() {

    private lateinit var viewModel: HomeViewModel
    private lateinit var manualEditText : EditText
    private lateinit var durationEditText : EditText
    private lateinit var autoBtn : Button
    private lateinit var confirm : Button
    private lateinit var switchCompat : SwitchCompat


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpViews()
        setUpViewModel()
        viewModel.setFusedLocationProvider(this)

        autoBtn.setOnClickListener {
            viewModel.setCurrentTime()
        }

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        confirm.setOnClickListener {
            viewModel.completeSetting(durationEditText.text.toString())
            viewModel.upsertParking()
            val intent = Intent(this, ParkingDataActivity::class.java)
            startActivity(intent)
        }

    }

    private fun setUpViews(){
        manualEditText = findViewById(R.id.manualEditText)
        durationEditText = findViewById(R.id.durationEditText)
        autoBtn = findViewById(R.id.autoTimeBt)
        confirm = findViewById(R.id.manualTimeBt)
        switchCompat = findViewById(R.id.switch1)
        switchCompat.setThumbResource(R.drawable.switch_thumb_custom)
        switchCompat.setTrackResource(R.drawable.switch_track_custom)
        switchCompat.setOnCheckedChangeListener { button, isChecked ->
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
            inputType = InputType.TYPE_NULL
            isFocusable = false
            isFocusableInTouchMode = false
            setHintTextColor(getColor(R.color.disabled))
            background = AppCompatResources.getDrawable(activity,R.drawable.input_bg_disabled)
        }

        autoBtn.isClickable = true
        autoBtn.background.setTint(getColor(R.color.colorAccent))
    }

    private fun enableManualInsertion(activity: Activity) {
        manualEditText.apply {
            inputType = InputType.TYPE_CLASS_DATETIME
            isFocusable = true
            isFocusableInTouchMode = true
            setHintTextColor(getColor(R.color.colorAccent))
            background = AppCompatResources.getDrawable(activity,R.drawable.input_barckground)
        }

        autoBtn.isClickable = false
        autoBtn.background.setTint(getColor(R.color.disabled))
    }

    private fun setUpViewModel(){
        val parkingRepository = ParkingRepository(ParkingDatabase(this))
        val viewModelProviderFactory = ViewModelFactory(application,parkingRepository)
        viewModel = ViewModelProvider(this,viewModelProviderFactory)[HomeViewModel::class.java]
    }

}