package com.example.knockitbranchapp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.knockitbranchapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

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
    }
}