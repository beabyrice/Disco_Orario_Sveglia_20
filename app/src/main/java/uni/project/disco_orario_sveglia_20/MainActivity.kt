package uni.project.disco_orario_sveglia_20

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.CompoundButton
import android.widget.EditText
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import java.time.LocalDateTime
import java.time.LocalTime

class MainActivity : AppCompatActivity() {

    private lateinit var manualEditText : EditText
    private lateinit var autoBtn : Button
    private lateinit var switchCompat : SwitchCompat
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        manualEditText = findViewById(R.id.manualEditText)
        autoBtn = findViewById(R.id.autoTimeBt)
        switchCompat = findViewById(R.id.switch1)
        switchCompat.setThumbResource(R.drawable.switch_thumb_custom)
        switchCompat.setTrackResource(R.drawable.switch_track_custom)
        switchCompat.setOnCheckedChangeListener { button, isChecked ->
            if(button.isChecked) enableManualInsertion()
            else backToAutomaticInsertion()
        }
        // Set the background color programmatically

        autoBtn.setOnClickListener {
            val currentTime = LocalTime.now()
            val currentHour = currentTime.hour
            val currentMinutes = currentTime.minute
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
}