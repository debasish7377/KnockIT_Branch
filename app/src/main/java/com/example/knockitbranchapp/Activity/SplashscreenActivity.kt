package com.example.knockitbranchapp.Activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.knockitbranchapp.R
import com.example.knockitbranchapp.Service.MyServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import org.checkerframework.checker.units.qual.min


class SplashscreenActivity : AppCompatActivity() {
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        firebaseAuth = FirebaseAuth.getInstance()
        val time: Thread = object : Thread() {
            override fun run() {
                try {
                    sleep(1000)
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    if (firebaseAuth.currentUser == null) {
                        startActivity(Intent(this@SplashscreenActivity, RegisterActivity::class.java))
                    } else {
                        FirebaseFirestore.getInstance().collection("BRANCHES")
                            .document(firebaseAuth.currentUser?.uid.toString())
                            .update("Last seen", FieldValue.serverTimestamp())
                            .addOnCompleteListener { }
                        startActivity(Intent(this@SplashscreenActivity, PermissionActivity::class.java))
                        finish()
                    }
                }
            }
        }
        time.start()

        var intent = Intent(this, MyServices::class.java)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            applicationContext.startForegroundService(intent)
//        }else{
//            this@SplashscreenActivity.startService(intent)
//        }
        ServiceCaller(intent);
    }

    private fun ServiceCaller(intent: Intent) {
        stopService(intent)

//        Integer alarmHour = timePicker.getCurrentHour();
//        Integer alarmMinute = timePicker.getCurrentMinute();

//                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                        if (task.isSuccessful()) {
//
//
//                        }
//                    }
//                });
        val time: Thread = object : Thread() {
            override fun run() {
                try {
                    sleep(3000)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                } finally {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        this@SplashscreenActivity.startForegroundService(intent)
                    }else{
                        startService(intent)
                    }
                    //startService(intent)
                }
            }
        }
        time.start()
    }
}