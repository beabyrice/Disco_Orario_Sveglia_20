package uni.project.disco_orario_sveglia_20.alarm

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import uni.project.disco_orario_sveglia_20.ParkingDataActivity
import uni.project.disco_orario_sveglia_20.R
import uni.project.disco_orario_sveglia_20.databinding.FragmentCountDownBinding
import uni.project.disco_orario_sveglia_20.viewModel.ParkingViewModel
import java.text.DecimalFormat
import kotlin.math.roundToInt

class CountDownFragment : Fragment(R.layout.fragment_count_down) {

    private lateinit var viewModel: ParkingViewModel
    private lateinit var binding: FragmentCountDownBinding


    private val testTime = 60
    private val clockTime = (testTime * 1000).toLong()
    private val progressTime = (clockTime/1000).toFloat()



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding =FragmentCountDownBinding.bind(view)

        viewModel = (activity as ParkingDataActivity).viewModel

        val tickInterval : Long = 1000
        val timer = CountDownTimer(clockTime,tickInterval)
        var secondsLeft = 0
        timer.onTick = { millisUntilFinished ->
            val currentSec = (millisUntilFinished/1000.0f).roundToInt()
            if(currentSec != secondsLeft){
                secondsLeft = currentSec

                timerFormat(secondsLeft,binding.textClock)

            }
        }
        timer.onFinish = {
            //TODO: alarm intent here ! or notification.. ask professor
            binding.textClock.text = "00:00:00"
        }

        binding.progressBar.max = progressTime.toInt()
        binding.progressBar.progress = progressTime.toInt()
        timer.startTimer()

        binding.button.setOnClickListener {
            viewModel.deleteParking()
            (activity as ParkingDataActivity).finish()
        }


    }

    private fun timerFormat(secondsLeft: Int, textView: TextView) {

        binding.progressBar.progress = secondsLeft
        val decimalFormat = DecimalFormat("00")
        val hours = secondsLeft/3600
        val minutes = (secondsLeft%3600)/60
        val seconds = secondsLeft%60

        val timeFormat = decimalFormat.format(hours) + ":" + decimalFormat.format(minutes) + ":" + decimalFormat.format(seconds)
        textView.text = timeFormat
    }

}