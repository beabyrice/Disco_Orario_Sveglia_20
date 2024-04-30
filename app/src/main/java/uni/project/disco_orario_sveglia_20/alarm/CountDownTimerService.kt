package uni.project.disco_orario_sveglia_20.alarm

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder

class CountDownTimerService : Service() {

    companion object {
        const val COUNTDOWN_BR = "uni.project.disco_orario_sveglia_20.alarm"
    }

    val intent = Intent(COUNTDOWN_BR)
    private lateinit var countDownTimer: CountDownTimer

    override fun onCreate() {
        super.onCreate()

        val sharedPref = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val durationInMillis =
            sharedPref.getLong("durationInMillis", 20000) // Default duration if not found


        countDownTimer = object : CountDownTimer(durationInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                intent.putExtra("countdown", millisUntilFinished)
                sendBroadcast(intent)
            }

            override fun onFinish() {
                intent.putExtra("countdown", 0)
                val alarmIntent = Intent(applicationContext, AlarmReceiver::class.java)
                sendBroadcast(alarmIntent)
            }

        }
        countDownTimer.start()
    }

    override fun onDestroy() {
        countDownTimer.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


}