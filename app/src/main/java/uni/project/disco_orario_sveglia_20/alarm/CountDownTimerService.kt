package uni.project.disco_orario_sveglia_20.alarm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.os.PowerManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import uni.project.disco_orario_sveglia_20.R
import uni.project.disco_orario_sveglia_20.activities.ParkingDataActivity

class CountDownTimerService : Service() {

    companion object {
        val COUNTDOWN_BR = "uni.project.disco_orario_sveglia_20.alarm"
        const val CHANNEL_ID = "countdownServiceChannel"
        const val ALARM_CHANNEL_ID = "alarmChannel"

    }

    val intent = Intent(COUNTDOWN_BR)
    private lateinit var countDownTimer: CountDownTimer

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()
        startForeground(1, createNotification())

        val sharedPref = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val durationInMillis = sharedPref.getLong("durationInMillis", 20000) // Default duration if not foun
        countDownTimer = object : CountDownTimer(durationInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                intent.putExtra("countdown", millisUntilFinished)
                sendBroadcast(intent)
            }

            override fun onFinish() {
                intent.putExtra("countdown", 0)
                sendBroadcast(intent)
                triggerAlarm()
                sharedPref.edit().putBoolean("hasAlreadyRunned", true).apply()
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

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            "Countdown Timer Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val alarmChannel = NotificationChannel(
            ALARM_CHANNEL_ID,
            "Alarm Channel",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Channel for timer alarm notifications"
            enableVibration(true)
            setSound(null, null) // Default sound
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(serviceChannel)
        manager.createNotificationChannel(alarmChannel)
    }

    private fun triggerAlarm() {

        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE,
            "uni.project.disco_orario_sveglia_20.alarm:Wakelock"
        )
        wakeLock.acquire(5000)

        // Vibrate the phone
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = this.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            val vibrator = vibratorManager.defaultVibrator
            vibrator.vibrate(VibrationEffect.createOneShot(4000,100))
        } else {
            val vibrator = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(VibrationEffect.createOneShot(4000,100))
        }

        val notificationIntent = Intent(this, ParkingDataActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmNotification = NotificationCompat.Builder(this, ALARM_CHANNEL_ID)
            .setContentTitle("Timer Finished")
            .setContentText("The countdown timer has finished.")
            .setSmallIcon(R.mipmap.ic_launcher_prova_round)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL) // Including sound, vibration, etc.
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(2, alarmNotification)
    }

    override fun onDestroy() {
        countDownTimer.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


}