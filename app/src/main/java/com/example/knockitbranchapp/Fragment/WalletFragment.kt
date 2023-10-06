package com.example.knockitbranchapp.Fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.bumptech.glide.Glide
import com.example.knockitbranchapp.Database.NotificationDatabase
import com.example.knockitbranchapp.Model.BranchModel
import com.example.knockitbranchapp.R
import com.example.knockitbranchapp.databinding.FragmentProfileBinding
import com.example.knockitbranchapp.databinding.FragmentWalletBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import java.util.UUID

class WalletFragment : Fragment() {

    lateinit var loadingDialog: Dialog
    lateinit var redeemDialog: Dialog
    lateinit var paymentDetailsDialog: Dialog

    lateinit var accountHolderName: EditText
    lateinit var accountNumber: EditText
    lateinit var accountIfscCode: EditText
    lateinit var bankAccountName: EditText

    lateinit var bankName: TextView
    lateinit var bankNumber: TextView
    lateinit var ifscCode: TextView
    lateinit var bankHolderName: TextView
    lateinit var totalEarning: TextView
    lateinit var redeemBtn: AppCompatButton
    lateinit var submitBtn: AppCompatButton
    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentWalletBinding = FragmentWalletBinding.inflate(inflater, container, false)

        ////////////////loading dialog
        loadingDialog = Dialog(context!!)
        loadingDialog.setContentView(R.layout.dialog_loading)
        loadingDialog.setCancelable(false)
        loadingDialog.window?.setBackgroundDrawable(context!!.getDrawable(R.drawable.btn_buy_now))
        loadingDialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        ////////////////loading dialog

        ////////////////Redeem dialog
        paymentDetailsDialog = Dialog(context!!)
        paymentDetailsDialog.setContentView(R.layout.dialog_enter_payment_details)
        paymentDetailsDialog.setCancelable(true)
        paymentDetailsDialog.window?.setBackgroundDrawable(context!!.getDrawable(R.drawable.btn_buy_now))
        paymentDetailsDialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        accountHolderName = paymentDetailsDialog.findViewById(R.id.accountHolderName)
        accountNumber = paymentDetailsDialog.findViewById(R.id.accountNumber)
        accountIfscCode = paymentDetailsDialog.findViewById(R.id.ifscCode)
        bankAccountName = paymentDetailsDialog.findViewById(R.id.bankName)
        submitBtn = paymentDetailsDialog.findViewById(R.id.submit_btn)
        ////////////////Redeem dialog

        ////////////////Redeem dialog
        redeemDialog = Dialog(context!!)
        redeemDialog.setContentView(R.layout.dialog_redeem)
        redeemDialog.setCancelable(true)
        redeemDialog.window?.setBackgroundDrawable(context!!.getDrawable(R.drawable.btn_buy_now))
        redeemDialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        bankName = redeemDialog.findViewById(R.id.bankName)
        bankNumber = redeemDialog.findViewById(R.id.accountNumber)
        ifscCode = redeemDialog.findViewById(R.id.ifscCode)
        bankHolderName = redeemDialog.findViewById(R.id.bankHolderName)
        totalEarning = redeemDialog.findViewById(R.id.totalAmount)
        redeemBtn = redeemDialog.findViewById(R.id.redeemBtn)
        ////////////////Redeem dialog

        FirebaseFirestore.getInstance()
            .collection("BRANCHES")
            .document(FirebaseAuth.getInstance().uid.toString())
            .addSnapshotListener { querySnapshot: DocumentSnapshot?, e: FirebaseFirestoreException? ->
                querySnapshot?.let {
                    val userModel = it.toObject(BranchModel::class.java)

                    binding.pendingPayment.text = "Delivery Pending Payment ₹"+userModel?.pendingPayment.toString()
                    binding.totalPayment.text = "Total Payment ₹"+userModel?.totalEarning.toString()
                    binding.accountNumber.text = userModel?.bankAccountNumber.toString()

                    if (userModel?.totalEarning.toString().toInt() <= 1000){
                        binding.redeemBtn.visibility = View.VISIBLE
                    }else{
                        binding.redeemBtn.visibility = View.GONE
                    }

                    bankName.text = userModel?.bankName
                    bankNumber.text = userModel?.bankAccountNumber
                    bankHolderName.text = userModel?.bankHolderName
                    ifscCode.text = userModel?.bankIFSCCode

                }
            }

        binding.redeemBtn.setOnClickListener {
            if (binding.accountNumber.text.toString().equals("")){
                paymentDetailsDialog.show()
            }else{
                redeemDialog.show()
            }
        }

        redeemBtn.setOnClickListener {
                loadingDialog.show()
                redeemDialog.dismiss()

                val randomString = UUID.randomUUID().toString().substring(0, 18)
                val userData3: MutableMap<String, Any?> =
                    HashMap()
                userData3["id"] = randomString
                userData3["title"] = "Redeem"
                userData3["description"] =
                    "Your Redeem Successful. Payment credited within 24hr"
                userData3["payment"] = totalEarning.text.toString() + " Payment Redeem"
                userData3["timeStamp"] = System.currentTimeMillis()
                userData3["read"] = "true"
                FirebaseFirestore.getInstance()
                    .collection("BRANCHES")
                    .document(FirebaseAuth.getInstance().uid.toString())
                    .collection("MY_NOTIFICATION")
                    .document(randomString)
                    .set(userData3)
                    .addOnCompleteListener {
                        val userData2: MutableMap<String, Any?> = HashMap()
                        userData2["riderId"] = FirebaseAuth.getInstance().uid.toString()
                        userData2["totalAmount"] = totalEarning.text.toString().toInt()
                        userData2["bankName"] = bankName.text.toString()
                        userData2["bankHolderName"] = bankHolderName.text.toString()
                        userData2["ifscCode"] = ifscCode.text.toString()
                        userData2["bankAccountNumber"] = bankNumber.text.toString()
                        userData2["timeStamp"] = System.currentTimeMillis()
                        userData2["payment"] = "false"
                        FirebaseFirestore.getInstance()
                            .collection("BRANCH_PAYMENT")
                            .document(randomString)
                            .set(userData2)
                            .addOnCompleteListener {
                                val userData: MutableMap<String, Any?> =
                                    HashMap()
                                userData["totalEarning"] = 0.toInt()
                                FirebaseFirestore.getInstance()
                                    .collection("RIDERS")
                                    .document(FirebaseAuth.getInstance().uid.toString())
                                    .update(userData)
                                    .addOnCompleteListener {

                                        FirebaseFirestore.getInstance()
                                            .collection("RIDERS")
                                            .document(FirebaseAuth.getInstance().uid.toString())
                                            .collection("PAYMENT")
                                            .document(randomString)
                                            .set(userData2)
                                            .addOnCompleteListener {
                                                loadingDialog.dismiss()
                                                Toast.makeText(
                                                    context,
                                                    "Redeem Successful",
                                                    Toast.LENGTH_SHORT
                                                ).show()

                                            }

                                    }

                            }
                    }
        }

        submitBtn.setOnClickListener {
            if (!accountHolderName.text.toString().equals("")){
                if (!accountNumber.text.toString().equals("")){
                    if (!accountIfscCode.text.toString().equals("")){
                        if (!bankAccountName.text.toString().equals("")){
                            loadingDialog.show()
                            paymentDetailsDialog.dismiss()
                            val userData: MutableMap<String, Any?> = HashMap()
                            userData["bankAccountNumber"] = accountNumber.text.toString()
                            userData["bankName"] = bankAccountName.text.toString()
                            userData["bankHolderName"] = accountHolderName.text.toString()
                            userData["bankIFSCCode"] = accountIfscCode.text.toString()
                            FirebaseFirestore.getInstance()
                                .collection("BRANCHES")
                                .document(FirebaseAuth.getInstance().uid.toString())
                                .update(userData)
                                .addOnCompleteListener() { task ->
                                    if (task.isSuccessful){
                                        loadingDialog.dismiss()
                                        Toast.makeText(context, "Account added successfully", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }else{
                            Toast.makeText(context, "Enter Bank Name", Toast.LENGTH_SHORT).show()
                        }
                    }else{
                        Toast.makeText(context, "Enter ifsc code", Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(context, "Enter Account Number", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(context, "Enter Account Holder Name", Toast.LENGTH_SHORT).show()
            }
        }

        NotificationDatabase.loadNotification(context!!, binding.notificationRecyclerView)

        return binding.root
    }
}