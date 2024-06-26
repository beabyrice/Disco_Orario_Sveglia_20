package uni.project.disco_orario_sveglia_20.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import uni.project.disco_orario_sveglia_20.R
import uni.project.disco_orario_sveglia_20.activities.MainActivity
import uni.project.disco_orario_sveglia_20.activities.ParkingDataActivity
import uni.project.disco_orario_sveglia_20.alarm.CountDownTimerService
import uni.project.disco_orario_sveglia_20.databinding.FragmentCountDownBinding
import uni.project.disco_orario_sveglia_20.repository.TimeRepository
import uni.project.disco_orario_sveglia_20.viewModel.ParkingViewModel

class CountDownFragment : Fragment(R.layout.fragment_count_down) {

    private lateinit var viewModel: ParkingViewModel
    private lateinit var binding: FragmentCountDownBinding

    private var duration: Long = 0
    private var progressTime : Float = 0f

    private lateinit var broadcastReceiver : BroadcastReceiver

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val parkingDataActivity = (activity as ParkingDataActivity)

        binding = FragmentCountDownBinding.bind(view)
        viewModel = parkingDataActivity.parkingViewModel

        val sharedPref = parkingDataActivity.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val hasTimerRun = sharedPref.getBoolean("hasAlreadyRun", false)

        viewModel.getParkingDuration()?.let {
            sharedPref.edit().putLong("durationInMillis", it).apply()
            duration = it
            progressTime = (duration/1000).toFloat()
        }
        binding.arrivalTime.text = viewModel.getArrivalTime()?.let { TimeRepository.timerFormat(it) }
        binding.progressBar.max = progressTime.toInt()
        binding.progressBar.progress = progressTime.toInt()

        broadcastReceiver = object : BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent != null) {
                    updateUI(intent,binding.textClock)
                }
            }
        }
        parkingDataActivity.registerReceiver(broadcastReceiver, IntentFilter(
            CountDownTimerService.COUNTDOWN_BR)
        )

        if (!hasTimerRun) {
            parkingDataActivity.startForegroundService()
        } else {
            binding.textClock.text = TimeRepository.timerFormat(0)
            binding.progressBar.progress = 0
            binding.textView4.text = getText(R.string.countdown_finished_text)
        }

        binding.button.setOnClickListener {
            viewModel.deleteParking()
            sharedPref.edit().putBoolean("hasAlreadyRun", false).apply()
            val homeIntent = Intent(parkingDataActivity, MainActivity::class.java)
            startActivity(homeIntent)
            parkingDataActivity
                .stopService(Intent(parkingDataActivity, CountDownTimerService::class.java))
            parkingDataActivity.finish()

        }
    }

    private fun updateUI(intent: Intent, textView: TextView) {
        if(intent.extras != null){
            val millisUntilFinished = intent.getLongExtra("countdown", 0)
            binding.progressBar.progress = (millisUntilFinished/1000).toFloat().toInt()
            textView.text = TimeRepository.timerFormat(millisUntilFinished)
            if(millisUntilFinished == 0L){
                binding.textView4.text = getText(R.string.countdown_finished_text)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as ParkingDataActivity).unregisterReceiver(broadcastReceiver)

    }
}