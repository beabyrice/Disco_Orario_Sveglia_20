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

//TODO: doesnt work when timer ends and you open the app
class CountDownFragment : Fragment(R.layout.fragment_count_down) {

    private lateinit var viewModel: ParkingViewModel
    private lateinit var binding: FragmentCountDownBinding
    private var duration: Long = 0

    private var progressTime : Float = 0f

    private lateinit var broadcastReceiver : BroadcastReceiver

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding =FragmentCountDownBinding.bind(view)

        viewModel = (activity as ParkingDataActivity).parkingViewModel

        val sharedPref = (activity as ParkingDataActivity).getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        viewModel.getParkingDuration()?.let {
            sharedPref.edit().putLong("durationInMillis", it).apply()
            duration = it
            progressTime = (duration/1000).toFloat()
        }

        broadcastReceiver = object : BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent != null) {
                    updateUI(intent,duration,binding.textClock)
                    if(duration == 0L){
                        binding.textView4.text = getText(R.string.countdown_finished_text)
                    }
                }
            }

        }
        binding.arrivalTime.text = viewModel.getArrivalTime()?.let { TimeRepository.timerFormat(it) }
        binding.progressBar.max = progressTime.toInt()
        binding.progressBar.progress = progressTime.toInt()

        val hasTimerRunned = sharedPref.getBoolean("hasAlreadyRunned", false)

        if (!hasTimerRunned) {
            val intent =
                Intent((activity as ParkingDataActivity), CountDownTimerService::class.java)
            (activity as ParkingDataActivity).startService(intent)
        } else {
            binding.textClock.text = TimeRepository.timerFormat(0)
            binding.progressBar.progress = 0
            binding.textView4.text = getText(R.string.countdown_finished_text)
        }

        binding.button.setOnClickListener {
            viewModel.deleteParking()
            sharedPref.edit().putBoolean("hasAlreadyRunned", false).apply()
            val homeIntent = Intent((activity as ParkingDataActivity), MainActivity::class.java)
            startActivity(homeIntent)
            (activity as ParkingDataActivity).finish()

        }
    }

    private fun updateUI(intent: Intent, secondsLeft: Long, textView: TextView) {
        if(intent.extras != null){
            val millisUntilFinished = intent.getLongExtra("countdown", secondsLeft)
            binding.progressBar.progress = (millisUntilFinished/1000).toFloat().toInt()

            textView.text = TimeRepository.timerFormat(millisUntilFinished)
        }
    }

    override fun onStart() {
        super.onStart()
        (activity as ParkingDataActivity).registerReceiver(broadcastReceiver, IntentFilter(
            CountDownTimerService.COUNTDOWN_BR)
        )
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
        (activity as ParkingDataActivity).stopService(Intent((activity as ParkingDataActivity),
            CountDownTimerService::class.java))
        super.onDestroy()
    }

}