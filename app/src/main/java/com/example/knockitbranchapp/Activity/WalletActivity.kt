package com.example.knockitbranchapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.knockitbranchapp.Database.NotificationDatabase
import com.example.knockitbranchapp.Model.BranchModel
import com.example.knockitbranchapp.R
import com.example.knockitbranchapp.databinding.ActivityDashbordBinding
import com.example.knockitbranchapp.databinding.ActivityWalletBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException

class WalletActivity : AppCompatActivity() {

    lateinit var binding: ActivityWalletBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalletBinding.inflate(layoutInflater)
        val view: View = binding.getRoot()
        setContentView(view)

        FirebaseFirestore.getInstance()
            .collection("BRANCHES")
            .document(FirebaseAuth.getInstance().uid.toString())
            .addSnapshotListener { querySnapshot: DocumentSnapshot?, e: FirebaseFirestoreException? ->
                querySnapshot?.let {
                    val userModel = it.toObject(BranchModel::class.java)

                    binding.pendingPayment.text = "Delivery Pending Payment ₹"+userModel?.pendingPayment.toString()
                    binding.totalPayment.text = "Total Payment ₹"+userModel?.totalEarning.toString()

                }
            }

        NotificationDatabase.loadNotification(this, binding.notificationRecyclerView)
    }
}