package uni.project.disco_orario_sveglia_20.alarm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import androidx.core.app.NotificationCompat
import uni.project.disco_orario_sveglia_20.R
import uni.project.disco_orario_sveglia_20.activities.ParkingDataActivity

class CountDownTimerService : Service() {

    companion object {
        val COUNTDOWN_BR = "uni.project.disco_orario_sveglia_20.alarm"
        const val CHANNEL_ID = "countdownServiceChannel"

    }

    val intent = Intent(COUNTDOWN_BR)
    private lateinit var countDownTimer: CountDownTimer

    override fun onCreate() {
        super.onCreate()

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Countdown Timer Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

        startForeground(1, createNotification())

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
                stopSelf()
            }

        }
        countDownTimer.start()
    }

    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, ParkingDataActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Countdown Timer")
            .setContentText("The timer is running...")
            .setSmallIcon(R.mipmap.ic_launcher_prova_round)
            .setContentIntent(pendingIntent)
            .build()
    }

    override fun onDestroy() {
        countDownTimer.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


}