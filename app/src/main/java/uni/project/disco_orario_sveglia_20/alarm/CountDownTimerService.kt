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

//TODO: stop vibration
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
        startForeground(1, countdownNotification())

        val sharedPref = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        sharedPref.edit().putBoolean("warningSent", false).apply()
        val durationInMillis = sharedPref.getLong("durationInMillis", 20000) // Default duration if not foun
        countDownTimer = object : CountDownTimer(durationInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                intent.putExtra("countdown", millisUntilFinished)
                sendBroadcast(intent)

                if (millisUntilFinished <= 600000 && !sharedPref.getBoolean("warningSent", false)) {
                    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.notify(1, createWarningNotification())
                    sharedPref.edit().putBoolean("warningSent", true).apply()
                }
            }

            override fun onFinish() {
                intent.putExtra("countdown", 0)
                sendBroadcast(intent)
                triggerAlarm()
                sharedPref.edit().putBoolean("hasAlreadyRun", true).apply()
            }
        }
        countDownTimer.start()
    }

    private fun countdownNotification(): Notification {
        val notificationIntent = Intent(this, ParkingDataActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.timer_title))
            .setContentText(getString(R.string.timer_running))
            .setSmallIcon(R.mipmap.ic_launcher_prova_round)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun createWarningNotification(): Notification {
        val notificationIntent = Intent(this, ParkingDataActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.timer_warning_title))
            .setContentText(getString(R.string.timer_warning_message))
            .setSmallIcon(R.mipmap.ic_launcher_prova_round)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            "Countdown Timer Service Channel",
            NotificationManager.IMPORTANCE_LOW
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
        wakeApp()
        val vibrationWaveFormDurationPattern = longArrayOf(0, 10, 200, 500, 700, 200, 50, 10) // Looping pattern for vibration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = this.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            val vibrator = vibratorManager.defaultVibrator
            vibrator.vibrate(VibrationEffect.createWaveform(vibrationWaveFormDurationPattern, 0))
        } else {
            val vibrator = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(VibrationEffect.createWaveform(vibrationWaveFormDurationPattern, 0))
        }
        val notificationIntent = Intent(this, ParkingDataActivity::class.java).apply {
            putExtra("stop_vibration", true)
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmNotification = NotificationCompat.Builder(this, ALARM_CHANNEL_ID)
            .setContentTitle(getString(R.string.timer_ended_title))
            .setContentText(getString(R.string.timer_ended))
            .setSmallIcon(R.mipmap.ic_launcher_prova_round)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(2, alarmNotification)
        notificationManager.cancelAll()
    }

    override fun onDestroy() {
        countDownTimer.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun wakeApp() {
        val pm = applicationContext.getSystemService(POWER_SERVICE) as PowerManager
        val screenIsOn = pm.isInteractive
        if (!screenIsOn) {
            val wakeLockTag = packageName + "WAKELOCK"
            val wakeLock = pm.newWakeLock(
                PowerManager.FULL_WAKE_LOCK or
                        PowerManager.ACQUIRE_CAUSES_WAKEUP or
                        PowerManager.ON_AFTER_RELEASE, wakeLockTag
            )
            wakeLock.acquire(3000)
        }
    }

}