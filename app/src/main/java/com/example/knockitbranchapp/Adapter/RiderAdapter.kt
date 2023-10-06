package com.example.knockitbranchapp.Adapter

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.knockitbranchapp.Activity.DashboardActivity
import com.example.knockitbranchapp.Model.BranchModel
import com.example.knockitbranchapp.Model.NotificationModel
import com.example.knockitbranchapp.Model.RiderModel
import com.example.knockitbranchapp.R
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.Date

class RiderAdapter(var context: Context, var model: List<RiderModel>) :
    RecyclerView.Adapter<RiderAdapter.viewHolder>() {

    companion object {
        lateinit var riderConnectingDialog: Dialog
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        var view: View =
            LayoutInflater.from(context).inflate(R.layout.item_riders, parent, false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {

        holder.name.text = model[position].name
        holder.email.text = model[position].email
        if (!model[position].profile.equals("")) {
            Glide.with(context).load(model[position].profile.toString())
                .placeholder(R.drawable.avatara).into(holder.circleImageView)
        } else {
            Glide.with(context).load(R.drawable.avatara).into(holder.circleImageView)
        }

        ////////////////canceled dialog
        riderConnectingDialog = Dialog(context)
        riderConnectingDialog.setContentView(R.layout.dialog_rider_connecting)
        riderConnectingDialog.setCancelable(true)
        riderConnectingDialog.window?.setBackgroundDrawable(context.getDrawable(R.drawable.btn_buy_now))
        riderConnectingDialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        ////////////////canceled dialog


        holder.connectBtn.setOnClickListener {
            FirebaseFirestore.getInstance()
                .collection("BRANCHES")
                .document(FirebaseAuth.getInstance().uid.toString())
                .get()
                .addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
                    val userModel: BranchModel? =
                        documentSnapshot.toObject(BranchModel::class.java)

                        if (userModel?.connectWithRider.equals("")) {
                            riderConnectingDialog.show()
                            try {
                                val userData: MutableMap<String, Any?> = HashMap()
                                userData["storeOwnerName"] = userModel?.name
                                userData["storeOwnerProfile"] = userModel?.profile
                                userData["storeName"] = userModel?.storeName
                                userData["storeId"] = userModel?.storeId
                                userData["riderId"] = model[position].riderId
                                userData["timeStamp"] = System.currentTimeMillis()
                                FirebaseFirestore.getInstance()
                                    .collection("RiderNotification")
                                    .document(model[position].riderId)
                                    .set(userData)
                                    .addOnCompleteListener {

                                    }
                            }catch (e: Exception){
                                e.printStackTrace()
                            }
                        } else {
                            Toast.makeText(context, "Rider Already added", Toast.LENGTH_SHORT)
                                .show()
                        }

                    })
        }

        FirebaseFirestore.getInstance()
            .collection("BRANCHES")
            .document(FirebaseAuth.getInstance().uid.toString())
            .addSnapshotListener { value, error ->
                var connectWithRider = value?.getString("connectWithRider").toString()

                if (!connectWithRider.equals("")) {
                    Toast.makeText(context, "Rider Connected", Toast.LENGTH_SHORT)
                        .show()
                    riderConnectingDialog.dismiss()
                    context.startActivity(Intent(context, DashboardActivity::class.java))
                } else {

                }
            }

        FirebaseFirestore.getInstance()
            .collection("RiderNotification")
            .document(model[position].riderId)
            .addSnapshotListener { value, error ->
                var riderId = value?.getString("riderId").toString()

                try {
                    if (model[position].riderId.equals(riderId)) {
                        riderConnectingDialog.show()
                    } else {
                        riderConnectingDialog.dismiss()
                    }
                }catch (e: Exception){
                    e.printStackTrace()
                }
            }

    }

    override fun getItemCount(): Int {
        return model.size
    }

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var circleImageView: CircleImageView =
            itemView.findViewById<CircleImageView?>(R.id.imageView6)
        var name: TextView = itemView.findViewById<TextView?>(R.id.riderName)
        var email: TextView = itemView.findViewById<TextView?>(R.id.riderEmail)
        var connectBtn: AppCompatButton = itemView.findViewById<AppCompatButton?>(R.id.connectRider)

    }
}