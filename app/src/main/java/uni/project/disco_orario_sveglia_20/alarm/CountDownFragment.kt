package uni.project.disco_orario_sveglia_20.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import uni.project.disco_orario_sveglia_20.ParkingDataActivity
import uni.project.disco_orario_sveglia_20.R
import uni.project.disco_orario_sveglia_20.databinding.FragmentCountDownBinding
import uni.project.disco_orario_sveglia_20.viewModel.ParkingViewModel
import java.lang.Exception
import java.text.DecimalFormat
import kotlin.time.Duration

class CountDownFragment : Fragment(R.layout.fragment_count_down) {

    private lateinit var viewModel: ParkingViewModel
    private lateinit var binding: FragmentCountDownBinding
    private var duration: Long = 0

    private var progressTime : Float = 0f

    private lateinit var broadcastReceiver : BroadcastReceiver

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding =FragmentCountDownBinding.bind(view)

        viewModel = (activity as ParkingDataActivity).viewModel

        val sharedPref = (activity as ParkingDataActivity).getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        viewModel.getParkingDuration()?.let {
            sharedPref.edit().putLong("durationInMillis", it).apply()
            duration = it
            progressTime = (duration/1000).toFloat()
        }

        broadcastReceiver = object : BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent != null) {
                    timerFormat(intent,duration,binding.textClock)
                }
            }

        }

        val intent = Intent((activity as ParkingDataActivity),CountDownTimerService::class.java)

        (activity as ParkingDataActivity).startService(intent)

        binding.progressBar.max = progressTime.toInt()
        binding.progressBar.progress = progressTime.toInt()

        binding.button.setOnClickListener {
            viewModel.deleteParking()
            (activity as ParkingDataActivity).finish()
        }


    }

    private fun timerFormat(intent: Intent, secondsLeft: Long, textView: TextView) {
        if(intent.extras != null){
            val millisUntilFinished = intent.getLongExtra("countdown", secondsLeft)
            binding.progressBar.progress = (millisUntilFinished/1000).toFloat().toInt()

            val hours = (millisUntilFinished/1000)/3600
            val minutes = ((millisUntilFinished/1000)%3600)/60
            val seconds = (millisUntilFinished/1000)%60

            val timeFormat = String.format("%02d:%02d:%02d", hours, minutes, seconds)
            textView.text = timeFormat
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as ParkingDataActivity).registerReceiver(broadcastReceiver, IntentFilter(CountDownTimerService.COUNTDOWN_BR))
    }

    override fun onPause() {
        super.onPause()
        (activity as ParkingDataActivity).unregisterReceiver(broadcastReceiver)
    }

    override fun onStop() {
        try {
            (activity as ParkingDataActivity).unregisterReceiver(broadcastReceiver)
        } catch (e: Exception) {
            //
        }
        super.onStop()
    }

    override fun onDestroy() {
        (activity as ParkingDataActivity).stopService(Intent((activity as ParkingDataActivity),CountDownTimerService::class.java))
        super.onDestroy()
    }

}