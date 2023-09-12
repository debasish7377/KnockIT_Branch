package com.example.knockitbranchapp.Adapter

import android.R.attr.name
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.example.knockitbranchapp.Model.BranchModel
import com.example.knockitbranchapp.Model.MyOderModel
import com.example.knockitbranchapp.Model.UserModel
import com.example.knockitbranchapp.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import java.util.UUID


class MyOderAdapter(var context: Context, var model: ArrayList<MyOderModel>) :
    RecyclerView.Adapter<MyOderAdapter.viewHolder>() {

    lateinit var canceledDialog: Dialog
    lateinit var canceledText: EditText
    lateinit var okBtn: AppCompatButton
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        var view: View = LayoutInflater.from(context).inflate(R.layout.item_my_oder, parent, false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {

        FirebaseFirestore.getInstance()
            .collection("PRODUCTS")
            .document(model[position].productId)
            .get()
            .addOnCompleteListener(OnCompleteListener<DocumentSnapshot> { task ->
                if (task.isSuccessful) {
                    Glide.with(context).load(task.result.getString("productImage").toString())
                        .into(holder.productImage)
                }
            })

        ////////////////canceled dialog
        canceledDialog = Dialog(context)
        canceledDialog.setContentView(R.layout.dialog_oder_canceled)
        canceledDialog.setCancelable(true)
        canceledDialog.window?.setBackgroundDrawable(context.getDrawable(R.drawable.btn_buy_now))
        canceledDialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        canceledText = canceledDialog.findViewById(R.id.canceledText)
        okBtn = canceledDialog.findViewById(R.id.okBtn)
        ////////////////canceled dialog

        holder.productTitle.text = model[position].productTitle
        holder.productPrice.text = model[position].productPrice.toString()
        holder.productCuttedPrice.text = model[position].productCuttedPrice.toString()
        holder.yourPrice.text = model[position].price.toString()
        holder.qty_text.text = model[position].qty
        holder.userUid.text = model[position].uid
        var youSaved: String =
            (model[position].productCuttedPrice.toInt() - model[position].productPrice.toInt()).toString()
        holder.discountedPrice.text = "₹" + youSaved + " Saved"

        holder.canceledBtn.setOnClickListener {
            if (model[position].delivery.equals("Canceled")) {
                Toast.makeText(context, "Oder already Canceled", Toast.LENGTH_SHORT).show()
            } else if (model[position].delivery.equals("Pending")){
                canceledDialog.show()
                FirebaseFirestore.getInstance()
                    .collection("OderNotification")
                    .document(FirebaseAuth.getInstance().uid.toString())
                    .delete()
                val randomString = UUID.randomUUID().toString().substring(0, 18)
                val userData1: MutableMap<String, Any?> =
                    HashMap()
                userData1["id"] = randomString
                userData1["title"] = "Oder Canceled"
                userData1["description"] = "Your Oder "+holder.productTitle.text.toString()+" and price ₹"+holder.yourPrice.text.toString()+ " Canceled by you"
                userData1["payment"] = ""
                userData1["timeStamp"] = System.currentTimeMillis()
                userData1["read"] = "true"

                FirebaseFirestore.getInstance()
                    .collection("BRANCHES")
                    .document(FirebaseAuth.getInstance().uid.toString())
                    .collection("MY_NOTIFICATION")
                    .document(randomString)
                    .set(userData1)
                    .addOnCompleteListener {

                    }
            }else{

                FirebaseFirestore.getInstance().collection("BRANCHES")
                    .document(model[position].storeId)
                    .get()
                    .addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
                        val model: BranchModel? =
                            documentSnapshot.toObject(BranchModel::class.java)

                        val userData: MutableMap<String, Any?> =
                            HashMap()
                        userData["pendingPayment"] = (model?.pendingPayment.toString().toInt() - holder.yourPrice.text.toString().toInt()).toInt()
                        FirebaseFirestore.getInstance()
                            .collection("BRANCHES")
                            .document(FirebaseAuth.getInstance().uid.toString())
                            .update(userData)
                            .addOnCompleteListener {

                            }

                    })

                val randomString = UUID.randomUUID().toString().substring(0, 18)
                val userData1: MutableMap<String, Any?> =
                    HashMap()
                userData1["id"] = randomString
                userData1["title"] = "Oder Canceled"
                userData1["description"] = "Your Oder "+holder.productTitle.text.toString()+" and price ₹"+holder.yourPrice.text.toString()+ " Canceled by you"
                userData1["payment"] = holder.yourPrice.text.toString()+" Payment Canceled"
                userData1["timeStamp"] = System.currentTimeMillis()
                userData1["read"] = "true"

                FirebaseFirestore.getInstance()
                    .collection("BRANCHES")
                    .document(FirebaseAuth.getInstance().uid.toString())
                    .collection("MY_NOTIFICATION")
                    .document(randomString)
                    .set(userData1)
                    .addOnCompleteListener {

                    }

                canceledDialog.show()
            }
        }

        okBtn.setOnClickListener {
            if (!canceledText.text.toString().equals("")) {
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Oder")
                builder.setMessage("Are you sure to cancel this oder ?")

                builder.setPositiveButton("Yes") { dialog, which ->

                    canceledDialog.dismiss()
                    FirebaseFirestore.getInstance()
                        .collection("OderNotification")
                        .document(FirebaseAuth.getInstance().uid.toString())
                        .delete()
                    val randomString = UUID.randomUUID().toString().substring(0, 18)
                    val userData1: MutableMap<String, Any?> =
                        HashMap()
                    userData1["id"] = randomString
                    userData1["title"] = "Oder Canceled"
                    userData1["description"] = canceledText.text.toString()
                    userData1["timeStamp"] = System.currentTimeMillis()
                    userData1["read"] = "true"

                    FirebaseFirestore.getInstance()
                        .collection("USERS")
                        .document(model[position].uid)
                        .collection("MY_NOTIFICATION")
                        .document(randomString)
                        .set(userData1)
                        .addOnCompleteListener {

                        }

                    FirebaseFirestore.getInstance().collection("USERS")
                        .document(model[position].uid)
                        .get()
                        .addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
                            val model: UserModel? =
                                documentSnapshot.toObject(UserModel::class.java)

                            val userData2: MutableMap<String, Any?> = HashMap()
                            userData2["notificationSize"] = (model?.notificationSize.toString().toInt() + 1).toString()

                            FirebaseFirestore.getInstance()
                                .collection("USERS")
                                .document(holder.userUid.text.toString())
                                .update(userData2)
                                .addOnCompleteListener {
                                    canceledDialog.dismiss()
                                }

                        })

                    val userData: MutableMap<String, Any?> =
                        HashMap()
                    userData["delivery"] = "Canceled"
                    FirebaseFirestore.getInstance()
                        .collection("ODER")
                        .document(model[position].id)
                        .update(userData)
                        .addOnCompleteListener {
                            notifyDataSetChanged()
                            Toast.makeText(context, "Oder Canceled", Toast.LENGTH_SHORT).show()
                        }

                }

                builder.setNegativeButton("No") { dialog, which ->
                }

                builder.show()
            }else{
                Toast.makeText(context, "Enter Details", Toast.LENGTH_SHORT).show()
            }
        }

        if (model[position].delivery.equals("Pending")) {
            holder.deliveryBtn.text = "Oder confirmed"

            /////Oder Confirmed Notification
            holder.deliveryBtn.setOnClickListener {

                val builder = AlertDialog.Builder(context)
                builder.setTitle("Oder")
                builder.setMessage("Are you sure to confirm this oder ?")

                builder.setPositiveButton("Yes") { dialog, which ->

                    FirebaseFirestore.getInstance()
                        .collection("OderNotification")
                        .document(FirebaseAuth.getInstance().uid.toString())
                        .delete()

                    //////Oder pending payment
                    FirebaseFirestore.getInstance().collection("BRANCHES")
                        .document(model[position].storeId)
                        .get()
                        .addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
                            val model: BranchModel? =
                                documentSnapshot.toObject(BranchModel::class.java)

                            val userData: MutableMap<String, Any?> =
                                HashMap()
                            userData["pendingPayment"] = (model?.pendingPayment.toString().toInt() + holder.yourPrice.text.toString().toInt()).toInt()
                            FirebaseFirestore.getInstance()
                                .collection("BRANCHES")
                                .document(FirebaseAuth.getInstance().uid.toString())
                                .update(userData)
                                .addOnCompleteListener {

                                }

                        })
                    //////Oder pending payment

                    val randomString1 = UUID.randomUUID().toString().substring(0, 18)
                    val userData2: MutableMap<String, Any?> =
                        HashMap()
                    userData2["id"] = randomString1
                    userData2["title"] = "Oder Confirmed"
                    userData2["description"] = "Your Oder "+holder.productTitle.text.toString()+" and price ₹"+holder.yourPrice.text.toString()+ " Confirmed"
                    userData2["payment"] = holder.yourPrice.text.toString()+" Payment Pending"
                    userData2["timeStamp"] = System.currentTimeMillis()
                    userData2["read"] = "true"

                    FirebaseFirestore.getInstance()
                        .collection("BRANCHES")
                        .document(FirebaseAuth.getInstance().uid.toString())
                        .collection("MY_NOTIFICATION")
                        .document(randomString1)
                        .set(userData2)
                        .addOnCompleteListener {

                        }

                    val randomString = UUID.randomUUID().toString().substring(0, 18)
                    val userData1: MutableMap<String, Any?> =
                        HashMap()
                    userData1["id"] = randomString
                    userData1["title"] = "Oder Confirmed"
                    userData1["description"] = "Your Oder "+holder.productTitle.text.toString()+" and price ₹"+holder.yourPrice.text.toString()+ " Confirmed"
                    userData1["timeStamp"] = System.currentTimeMillis()
                    userData1["read"] = "true"

                    FirebaseFirestore.getInstance()
                        .collection("USERS")
                        .document(model[position].uid)
                        .collection("MY_NOTIFICATION")
                        .document(randomString)
                        .set(userData1)
                        .addOnCompleteListener {

                        }

                    FirebaseFirestore.getInstance().collection("USERS")
                        .document(model[position].uid)
                        .get()
                        .addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
                            val model: UserModel? =
                                documentSnapshot.toObject(UserModel::class.java)

                            val userData2: MutableMap<String, Any?> = HashMap()
                            userData2["notificationSize"] = (model?.notificationSize.toString().toInt() + 1).toString()

                            FirebaseFirestore.getInstance()
                                .collection("USERS")
                                .document(holder.userUid.text.toString())
                                .update(userData2)
                                .addOnCompleteListener {

                                }

                        })
                    /////Oder Confirmed Notification


                    val userData: MutableMap<String, Any?> =
                        HashMap()
                    userData["delivery"] = "Oder confirmed"
                    userData["oderConfirmedDate"] = System.currentTimeMillis().toString()
                    FirebaseFirestore.getInstance()
                        .collection("ODER")
                        .document(model[position].id)
                        .update(userData)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                notifyDataSetChanged()
                                Toast.makeText(context, "Oder confirmed", Toast.LENGTH_SHORT).show()
                            } else {

                            }
                        }

                }

                builder.setNegativeButton("No") { dialog, which ->
                }

                builder.show()
            }
        } else if (model[position].delivery.equals("Oder confirmed")) {
            holder.deliveryBtn.text = "Shipped"

            holder.deliveryBtn.setOnClickListener {

                val builder = AlertDialog.Builder(context)
                builder.setTitle("Oder")
                builder.setMessage("Oder shipped ?")

                builder.setPositiveButton("Yes") { dialog, which ->

                    val userData: MutableMap<String, Any?> =
                        HashMap()
                    userData["delivery"] = "Shipped"
                    userData["shippedDate"] = System.currentTimeMillis().toString()
                    FirebaseFirestore.getInstance()
                        .collection("ODER")
                        .document(model[position].id)
                        .update(userData)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                notifyDataSetChanged()
                                Toast.makeText(context, "Oder Shipped", Toast.LENGTH_SHORT).show()
                            } else {

                            }
                        }

                }

                builder.setNegativeButton("No") { dialog, which ->
                }

                builder.show()

            }
        } else if (model[position].delivery.equals("Shipped")) {
            holder.deliveryBtn.text = "Out for delivery"

            holder.deliveryBtn.setOnClickListener {

                val builder = AlertDialog.Builder(context)
                builder.setTitle("Oder")
                builder.setMessage("Out for delivery ?")

                builder.setPositiveButton("Yes") { dialog, which ->

                    val userData: MutableMap<String, Any?> =
                        HashMap()
                    userData["delivery"] = "Out for delivery"
                    userData["outForDeliveryDate"] = System.currentTimeMillis().toString()
                    FirebaseFirestore.getInstance()
                        .collection("ODER")
                        .document(model[position].id)
                        .update(userData)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                notifyDataSetChanged()
                                Toast.makeText(context, "Oder Out for delivery", Toast.LENGTH_SHORT)
                                    .show()
                            } else {

                            }
                        }
                }

                builder.setNegativeButton("No") { dialog, which ->
                }

                builder.show()

            }
        } else if (model[position].delivery.equals("Out for delivery")) {
            holder.deliveryBtn.text = "Delivered"

        } else if (model[position].delivery.equals("Delivered")) {
            holder.deliveryBtn.text = "Oder Successfully Completed"
            holder.canceledBtn.visibility = View.GONE
            holder.deliveryBtn.setOnClickListener {
                Toast.makeText(context, "Oder already Delivered", Toast.LENGTH_SHORT).show()
            }
        }else if (model[position].delivery.equals("Canceled")){
            holder.canceledBtn.text = "Oder Canceled"
            holder.deliveryBtn.visibility = View.GONE
        }

    }

    override fun getItemCount(): Int {
        return model.size
    }

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var productPrice: TextView = itemView.findViewById<TextView?>(R.id.mini_product_price)
        var productCuttedPrice: TextView =
            itemView.findViewById<TextView?>(R.id.mini_product_cutted_price)
        var productTitle: TextView = itemView.findViewById<TextView?>(R.id.mini_product_title)
        var productImage: ImageView = itemView.findViewById<ImageView?>(R.id.mini_product_image)
        var discountedPrice: TextView = itemView.findViewById<TextView?>(R.id.discount_text)
        var qty_text: TextView = itemView.findViewById<TextView?>(R.id.qty_text)
        var yourPrice: TextView = itemView.findViewById<TextView?>(R.id.yourPrice)
        var userUid: TextView = itemView.findViewById<TextView?>(R.id.userUid)

        var canceledBtn: AppCompatButton = itemView.findViewById<AppCompatButton?>(R.id.canceledBtn)
        var deliveryBtn: AppCompatButton = itemView.findViewById<AppCompatButton?>(R.id.deliveryBtn)

    }
}





//            holder.deliveryBtn.setOnClickListener {
//
//                val builder = AlertDialog.Builder(context)
//                builder.setTitle("Oder")
//                builder.setMessage("Oder delivered ?")
//
//                builder.setPositiveButton("Yes") { dialog, which ->
//
//                    //////Oder pending payment
//                    FirebaseFirestore.getInstance().collection("BRANCHES")
//                        .document(model[position].storeId)
//                        .get()
//                        .addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
//                            val model: BranchModel? =
//                                documentSnapshot.toObject(BranchModel::class.java)
//
//                            val userData: MutableMap<String, Any?> =
//                                HashMap()
//                            userData["pendingPayment"] = (model?.pendingPayment.toString().toInt() + holder.yourPrice.text.toString().toInt()).toInt()
//                            FirebaseFirestore.getInstance()
//                                .collection("BRANCHES")
//                                .document(FirebaseAuth.getInstance().uid.toString())
//                                .update(userData)
//                                .addOnCompleteListener {
//
//                                }
//
//                        })
//                    //////Oder pending payment
//
//                    val userData: MutableMap<String, Any?> =
//                        HashMap()
//                    userData["delivery"] = "Delivered"
//                    userData["deliveredDate"] = System.currentTimeMillis().toString()
//                    FirebaseFirestore.getInstance()
//                        .collection("ODER")
//                        .document(model[position].id)
//                        .update(userData)
//                        .addOnCompleteListener {
//                            if (it.isSuccessful) {
//                                notifyDataSetChanged()
//                                Toast.makeText(context, "Oder Delivered", Toast.LENGTH_SHORT).show()
//                            } else {
//
//                            }
//                        }
//                }
//
//                builder.setNegativeButton("No") { dialog, which ->
//                }
//
//                builder.show()
//            }
