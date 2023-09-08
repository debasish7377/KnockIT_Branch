package com.example.knockitbranchapp.Service

import android.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.res.ResourcesCompat
import com.example.knockitbranchapp.Activity.DeliveryActivity
import com.example.knockitbranchapp.Activity.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Timer
import javax.annotation.Nullable


internal class MyServices : Service() {
    var alarmHour: Long? = null
    var alarmMinute: Long? = null
    private var ringtone: Ringtone? = null
    private val t = Timer()
    var timeStamp: Long? = null

    companion object {
        private const val CHANNEL_ID = "MyNotificationChannelID"
        private const val CHANNEL_ID_2 = "MyNotification"
        private const val NOTIFICATION_ID = 100
    }

    @Nullable
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

//        alarmHour = intent.getLongExtra("alarmHour", 0);
//        alarmMinute = intent.getLongExtra("alarmMinute", 0);
        FirebaseFirestore.getInstance()
            .collection("NOTIFICATION")
            .document(FirebaseAuth.getInstance().uid.toString())
            .addSnapshotListener { value, error ->
                timeStamp = value?.getLong("timeStamp")

                val time: Thread = object : Thread() {
                    override fun run() {
                        try {
                            sleep(3000)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        } finally {

                            try {
                                val notificationIntent =
                                    Intent(applicationContext, MainActivity::class.java)
                                val pendingIntent = PendingIntent.getActivity(
                                    applicationContext,
                                    0,
                                    notificationIntent,
                                    PendingIntent.FLAG_IMMUTABLE
                                )
                                val notification: Notification =
                                    NotificationCompat.Builder(applicationContext, CHANNEL_ID)
//                                        .setContentTitle("My Alarm clock")
//                                        .setContentText("Alarm time – ")
//                                        .setSmallIcon(R.drawable.sym_def_app_icon)
//                                        .setContentIntent(pendingIntent)
                                        .build()
                                startForeground(1, notification)
                                var notificationChannel: NotificationChannel? = null
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    notificationChannel = NotificationChannel(
                                        CHANNEL_ID,
                                        "My Alarm clock Service",
                                        NotificationManager.IMPORTANCE_DEFAULT
                                    )
                                }
                                val notificationManager = getSystemService(
                                    NotificationManager::class.java
                                )
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    notificationManager.createNotificationChannel(
                                        notificationChannel!!
                                    )
                                }
                            } catch (e: java.lang.Exception) {
                                e.printStackTrace()
                            }

                            ringtone = RingtoneManager.getRingtone(
                                getApplicationContext(),
                                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                            )
                            try {
                                if (System.currentTimeMillis() >= timeStamp!!
                                ) {
                                    ringtone?.play()
                                    val drawable = ResourcesCompat.getDrawable(
                                        getResources(),
                                        R.drawable.star_on,
                                        null
                                    )
                                    val bitmapDrawable = drawable as BitmapDrawable?
                                    val largeIcon = bitmapDrawable!!.bitmap
                                    val notificationManager = getSystemService(
                                        NOTIFICATION_SERVICE
                                    ) as NotificationManager?

                                    val notificationIntent =
                                        Intent(applicationContext, DeliveryActivity::class.java)
                                    val pendingIntent = PendingIntent.getActivity(
                                        applicationContext,
                                        0,
                                        notificationIntent,
                                        PendingIntent.FLAG_IMMUTABLE
                                    )

                                    val notification: Notification =
                                        NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                                            .setLargeIcon(largeIcon)
                                            .setSmallIcon(R.drawable.star_on)
                                            .setContentTitle("Jay Sri Ram")
                                            .setSubText("New SMS From Debasish")
                                            .setContentText("Alarm time – " + alarmHour.toString() + " : " + alarmMinute.toString())
                                            .setChannelId(CHANNEL_ID_2)
                                            .setContentIntent(pendingIntent)
                                            .build()
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        notificationManager!!.createNotificationChannel(
                                            NotificationChannel(
                                                CHANNEL_ID_2,
                                                "New Notification",
                                                NotificationManager.IMPORTANCE_HIGH
                                            )
                                        )
                                    }
                                    notificationManager!!.notify(
                                        NOTIFICATION_ID,
                                        notification
                                    )
                                } else {
                                    ringtone?.stop()
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
                time.start()
            }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        ringtone!!.stop()
        t.cancel()
        super.onDestroy()
    }
}