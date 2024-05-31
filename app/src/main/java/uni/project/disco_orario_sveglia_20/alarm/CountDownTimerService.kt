package uni.project.disco_orario_sveglia_20.alarm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
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
import androidx.core.content.ContextCompat
import uni.project.disco_orario_sveglia_20.R
import uni.project.disco_orario_sveglia_20.activities.ParkingDataActivity

//TODO: stop vibration
class CountDownTimerService : Service() {

    companion object {
        val COUNTDOWN_BR = "uni.project.disco_orario_sveglia_20.alarm"
        const val CHANNEL_ID = "countdownServiceChannel"
        const val ALARM_CHANNEL_ID = "alarmChannel"
        const val WARNING_CHANNEL_ID = "warningChannel"
    }

    val intent = Intent(COUNTDOWN_BR)
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var vibrator: Vibrator

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()
        startForeground(1, countdownNotification())

        val sharedPref = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        sharedPref.edit().putBoolean("warningSent", false).apply()
        val durationInMillis = sharedPref.getLong("durationInMillis", 20000)
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

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            "Countdown Timer Service Channel",
            NotificationManager.IMPORTANCE_LOW
        )
        val warningChannel = NotificationChannel(
            WARNING_CHANNEL_ID,
            "Warning Channel",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Channel for timer warning notifications"
            enableVibration(true)
            setSound(null, null)
        }
        val alarmChannel = NotificationChannel(
            ALARM_CHANNEL_ID,
            "Alarm Channel",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Channel for timer alarm notifications"
            enableVibration(true)
            setSound(null, null)
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(serviceChannel)
        manager.createNotificationChannel(alarmChannel)
        manager.createNotificationChannel(warningChannel)
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

        return NotificationCompat.Builder(this, WARNING_CHANNEL_ID)
            .setContentTitle(getString(R.string.timer_warning_title))
            .setContentText(getString(R.string.timer_warning_message))
            .setSmallIcon(R.mipmap.ic_launcher_prova_round)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
    }

    private fun triggerAlarm() {
        wakeApp()
        val vibrationWaveFormDurationPattern = longArrayOf(0, 10, 200, 500, 50, 10)
        initialiseVibrator()
        vibrator.vibrate(VibrationEffect.createWaveform(vibrationWaveFormDurationPattern, 0))

        val stopVibrationIntent = Intent(this, StopVibrationReceiver::class.java)
        val stopPendingIntent = PendingIntent.getBroadcast(this, 0, stopVibrationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val notificationIntent = Intent(this, ParkingDataActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmNotification = NotificationCompat.Builder(this, ALARM_CHANNEL_ID)
            .setContentTitle(getString(R.string.timer_ended_title))
            .setContentText(getString(R.string.timer_ended))
            .setSmallIcon(R.mipmap.ic_launcher_prova_round)
            .setContentIntent(pendingIntent)
            .addAction(R.mipmap.ic_launcher_prova_round,"Stop Vibration", stopPendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, alarmNotification)
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

    private fun initialiseVibrator() {
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = ContextCompat.getSystemService(
                this,
                VibratorManager::class.java
            ) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

}

class StopVibrationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = ContextCompat.getSystemService(
                context,
                VibratorManager::class.java
            ) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        vibrator.cancel()
    }
}
