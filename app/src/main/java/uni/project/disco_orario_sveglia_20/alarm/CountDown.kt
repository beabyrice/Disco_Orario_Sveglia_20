package uni.project.disco_orario_sveglia_20.alarm

import android.os.Bundle
import uni.project.disco_orario_sveglia_20.alarm.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import uni.project.disco_orario_sveglia_20.R
import java.text.DecimalFormat
import java.util.Locale
import kotlin.math.roundToInt

/**
 * A simple [Fragment] subclass.
 * Use the [CountDown.newInstance] factory method to
 * create an instance of this fragment.
 */
class CountDown : Fragment() {

    private val testTime = 60
    private val clockTime = (testTime * 1000).toLong()
    private val progressTime = (clockTime/1000).toFloat()
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_count_down, container, false)
        // Inflate the layout for this fragment
        val timeDuration : Long = 20000
        val tickInterval : Long = 1000
        val button = view.findViewById<Button>(R.id.button)
        val timer = CountDownTimer(clockTime,tickInterval)
        progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        val textView = view.findViewById<TextView>(R.id.textClock)

        var secondsLeft = 0
        timer.onTick = { millisUntilFinished ->
            val currentSec = (millisUntilFinished/1000.0f).roundToInt()
            if(currentSec != secondsLeft){
                secondsLeft = currentSec

                timerFormat(secondsLeft,textView)

            }
        }
        timer.onFinish = {
            //TODO: alarm intent here ! or notification.. ask professor
            textView.text = "00:00:00"
        }
        progressBar.max = progressTime.toInt()
        progressBar.progress = progressTime.toInt()
        timer.startTimer()


        return view
    }

    private fun timerFormat(secondsLeft: Int, textView: TextView) {

        progressBar.progress = secondsLeft
        val decimalFormat = DecimalFormat("00")
        val hours = secondsLeft/3600
        val minutes = (secondsLeft%3600)/60
        val seconds = secondsLeft%60

        val timeFormat = decimalFormat.format(hours) + ":" + decimalFormat.format(minutes) + ":" + decimalFormat.format(seconds)
        textView.text = timeFormat
    }

}