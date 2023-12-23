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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.knockitbranchapp.Model.BranchModel
import com.example.knockitbranchapp.Model.MyOderModel
import com.example.knockitbranchapp.Model.UserModel
import com.example.knockitbranchapp.R
import com.example.knockitbranchapp.Service.MyServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONObject
import java.util.UUID
import javax.xml.transform.ErrorListener
import javax.xml.transform.TransformerException


class MyOderAdapter(var context: Context, var model: ArrayList<MyOderModel>) :
    RecyclerView.Adapter<MyOderAdapter.viewHolder>() {

    lateinit var myOrderDialog: Dialog
    lateinit var canceledDialog: Dialog
    lateinit var canceledText: EditText
    lateinit var okBtn: AppCompatButton
    lateinit var orderOkBtn: AppCompatButton

    lateinit var userName: TextView
    lateinit var userCity: TextView
    lateinit var userAddress: TextView
    lateinit var userPhone: TextView
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

        ////////////////canceled dialog
        myOrderDialog = Dialog(context)
        myOrderDialog.setContentView(R.layout.dialog_my_order)
        myOrderDialog.setCancelable(false)
        myOrderDialog.window?.setBackgroundDrawable(context.getDrawable(R.drawable.btn_buy_now))
        myOrderDialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        orderOkBtn = myOrderDialog.findViewById(R.id.okBtn)
        userName = myOrderDialog.findViewById(R.id.userName)
        userAddress = myOrderDialog.findViewById(R.id.userAddress)
        userPhone = myOrderDialog.findViewById(R.id.userPhone)
        userCity = myOrderDialog.findViewById(R.id.userCity)

        holder.itemView.setOnClickListener {
            myOrderDialog.show()
            userName.text = "User Name - "+model[position].name.toString()
            userAddress.text = "User Address - "+model[position].address.toString()
            userCity.text = "User City - "+model[position].city.toString()
            userPhone.text = "User Phone - "+model[position].number.toString()
        }
        orderOkBtn.setOnClickListener {
            myOrderDialog.dismiss()
        }
        ////////////////canceled dialog

        holder.productTitle.text = model[position].productTitle
        holder.productPrice.text = model[position].productPrice.toString()
        holder.productCuttedPrice.text = model[position].productCuttedPrice.toString()
        holder.yourPrice.text = model[position].price.toString()
        holder.qty_text.text = model[position].qty
        holder.qty_no.text = "qty : " + model[position].qtyNo.toString()
        holder.userName.text = "User Name : " + model[position].name.toString()
        holder.userUid.text = model[position].uid
        holder.deliveryPrice.text = "Delivery Price : "+ model[position].deliveryPrice
        holder.orderPayment.text = "Payment : "+ model[position].payment
        var youSaved: String =
            (model[position].productCuttedPrice.toInt() - model[position].productPrice.toInt()).toString()
        holder.discountedPrice.text = "₹" + youSaved + " Saved"

        if (!model[position].riderId.toString().equals("")) {

            holder.riderBg.visibility = View.VISIBLE
            FirebaseFirestore.getInstance()
                .collection("RIDERS")
                .document(model[position].riderId.toString())
                .addSnapshotListener { value, error ->
                    var storeOwnerName = value?.getString("name").toString()
                    var storeOwnerProfile = value?.getString("profile").toString()
                    var number = value?.getString("number").toString()
                    holder.riderNumber.text = number
                    holder.riderName.text = storeOwnerName
                    try {
                        Glide.with(context!!).load(storeOwnerProfile)
                            .placeholder(R.drawable.avatara)
                            .into(holder.riderImage)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
        } else {
            holder.riderBg.visibility = View.GONE
        }

        holder.canceledBtn.setOnClickListener {
            MyServices.ringtone.stop()
            if (model[position].delivery.equals("Canceled")) {
                Toast.makeText(context, "Order already Canceled", Toast.LENGTH_SHORT).show()
            } else if (model[position].delivery.equals("Pending")) {
                canceledDialog.show()
                FirebaseFirestore.getInstance()
                    .collection("OrderNotification")
                    .document(FirebaseAuth.getInstance().uid.toString())
                    .delete()
                val randomString = UUID.randomUUID().toString().substring(0, 18)
                val userData1: MutableMap<String, Any?> =
                    HashMap()
                userData1["id"] = randomString
                userData1["title"] = "Order Canceled"
                userData1["description"] =
                    "Your Order " + holder.productTitle.text.toString() + " and price ₹" + holder.yourPrice.text.toString() + " Canceled by you"
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
            } else {

                FirebaseFirestore.getInstance().collection("BRANCHES")
                    .document(model[position].storeId)
                    .get()
                    .addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
                        val model: BranchModel? =
                            documentSnapshot.toObject(BranchModel::class.java)

                        val userData: MutableMap<String, Any?> =
                            HashMap()
                        userData["pendingPayment"] = (model?.pendingPayment.toString()
                            .toInt() - holder.yourPrice.text.toString().toInt()).toInt()
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
                userData1["title"] = "Order Canceled"
                userData1["description"] =
                    "Your Order " + holder.productTitle.text.toString() + " and price ₹" + holder.yourPrice.text.toString() + " Canceled by you"
                userData1["payment"] = holder.yourPrice.text.toString() + " Payment Canceled"
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
                builder.setTitle("Order")
                builder.setMessage("Are you sure to cancel this order ?")

                builder.setPositiveButton("Yes") { dialog, which ->

                    canceledDialog.dismiss()
                    FirebaseFirestore.getInstance()
                        .collection("OrderNotification")
                        .document(FirebaseAuth.getInstance().uid.toString())
                        .delete()
                    val randomString = UUID.randomUUID().toString().substring(0, 18)
                    val userData1: MutableMap<String, Any?> =
                        HashMap()
                    userData1["id"] = randomString
                    userData1["title"] = "Order Canceled"
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

                    sendNotification(
                        "Order Canceled", canceledText.text.toString(),
                        model[position].userToken.toString()
                    )

                    FirebaseFirestore.getInstance().collection("USERS")
                        .document(model[position].uid)
                        .get()
                        .addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
                            val model: UserModel? =
                                documentSnapshot.toObject(UserModel::class.java)

                            val userData2: MutableMap<String, Any?> = HashMap()
                            userData2["notificationSize"] =
                                (model?.notificationSize.toString().toInt() + 1).toString()

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
                        .collection("ORDER")
                        .document(model[position].id)
                        .update(userData)
                        .addOnCompleteListener {
                            notifyDataSetChanged()
                            Toast.makeText(context, "Order Canceled", Toast.LENGTH_SHORT).show()
                        }

                }

                builder.setNegativeButton("No") { dialog, which ->
                }

                builder.show()
            } else {
                Toast.makeText(context, "Enter Details", Toast.LENGTH_SHORT).show()
            }
        }

        if (model[position].delivery.equals("Pending")) {
            holder.deliveryBtn.text = "Order confirmed"

            /////Order Confirmed Notification
            holder.deliveryBtn.setOnClickListener {
                MyServices.ringtone.stop()
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Order")
                builder.setMessage("Are you sure to confirm this order ?")

                builder.setPositiveButton("Yes") { dialog, which ->

                    FirebaseFirestore.getInstance()
                        .collection("OrderNotification")
                        .document(FirebaseAuth.getInstance().uid.toString())
                        .delete()

                    //////Order pending payment
                    FirebaseFirestore.getInstance().collection("BRANCHES")
                        .document(model[position].storeId)
                        .get()
                        .addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
                            val model: BranchModel? =
                                documentSnapshot.toObject(BranchModel::class.java)

                            val userData: MutableMap<String, Any?> =
                                HashMap()
                            userData["pendingPayment"] = (model?.pendingPayment.toString()
                                .toInt() + holder.yourPrice.text.toString().toInt()).toInt()
                            FirebaseFirestore.getInstance()
                                .collection("BRANCHES")
                                .document(FirebaseAuth.getInstance().uid.toString())
                                .update(userData)
                                .addOnCompleteListener {

                                }

                        })
                    //////Order pending payment

                    val randomString1 = UUID.randomUUID().toString().substring(0, 18)
                    val userData2: MutableMap<String, Any?> =
                        HashMap()
                    userData2["id"] = randomString1
                    userData2["title"] = "Order Confirmed"
                    userData2["description"] =
                        "Your Order " + holder.productTitle.text.toString() + " and price ₹" + holder.yourPrice.text.toString() + " Confirmed"
                    userData2["payment"] = holder.yourPrice.text.toString() + " Payment Pending"
                    userData2["timeStamp"] = System.currentTimeMillis()
                    userData2["read"] = "true"

                    sendNotification(
                        "Order Confirmed", "Your Order Confirmed",
                        model[position].userToken.toString()
                    )

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
                    userData1["title"] = "Order Confirmed"
                    userData1["description"] =
                        "Your Order " + holder.productTitle.text.toString() + " and price ₹" + holder.yourPrice.text.toString() + " Confirmed"
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
                            userData2["notificationSize"] =
                                (model?.notificationSize.toString().toInt() + 1).toString()

                            FirebaseFirestore.getInstance()
                                .collection("USERS")
                                .document(holder.userUid.text.toString())
                                .update(userData2)
                                .addOnCompleteListener {

                                }

                        })
                    /////Order Confirmed Notification


                    val userData: MutableMap<String, Any?> =
                        HashMap()
                    userData["delivery"] = "Order confirmed"
                    userData["orderConfirmedDate"] = System.currentTimeMillis().toString()
                    FirebaseFirestore.getInstance()
                        .collection("ORDER")
                        .document(model[position].id)
                        .update(userData)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                notifyDataSetChanged()
                                Toast.makeText(context, "Order confirmed", Toast.LENGTH_SHORT).show()
                            } else {

                            }
                        }

                }

                builder.setNegativeButton("No") { dialog, which ->
                }

                builder.show()
            }
        } else if (model[position].delivery.equals("Order confirmed")) {
            holder.deliveryBtn.text = "Shipped"

            holder.deliveryBtn.setOnClickListener {

                val builder = AlertDialog.Builder(context)
                builder.setTitle("Order")
                builder.setMessage("Order shipped ?")

                builder.setPositiveButton("Yes") { dialog, which ->

                    val userData: MutableMap<String, Any?> =
                        HashMap()
                    userData["delivery"] = "Shipped"
                    userData["shippedDate"] = System.currentTimeMillis().toString()
                    FirebaseFirestore.getInstance()
                        .collection("ORDER")
                        .document(model[position].id)
                        .update(userData)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                notifyDataSetChanged()
                                Toast.makeText(context, "Order Shipped", Toast.LENGTH_SHORT).show()
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
                builder.setTitle("Order")
                builder.setMessage("Out for delivery ?")

                builder.setPositiveButton("Yes") { dialog, which ->

                    val userData: MutableMap<String, Any?> =
                        HashMap()
                    userData["delivery"] = "Out for delivery"
                    userData["outForDeliveryDate"] = System.currentTimeMillis().toString()
                    FirebaseFirestore.getInstance()
                        .collection("ORDER")
                        .document(model[position].id)
                        .update(userData)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                notifyDataSetChanged()
                                Toast.makeText(context, "Order Out for delivery", Toast.LENGTH_SHORT)
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
            if (model[position].riderId.equals("")) {
                holder.deliveryBtn.text = "Add Rider"

                holder.deliveryBtn.setOnClickListener {

                    FirebaseFirestore.getInstance()
                        .collection("BRANCHES")
                        .document(FirebaseAuth.getInstance().uid.toString())
                        .addSnapshotListener { querySnapshot: DocumentSnapshot?, e: FirebaseFirestoreException? ->
                            querySnapshot?.let {
                                val userModel = it.toObject(BranchModel::class.java)

                                if (!userModel?.connectWithRider.equals("")) {
                                    val userData: MutableMap<String, Any?> = HashMap()
                                    userData["riderId"] = userModel?.connectWithRider
                                    FirebaseFirestore.getInstance()
                                        .collection("ORDER")
                                        .document(model[position].id)
                                        .update(userData)
                                        .addOnCompleteListener {

                                        }

                                } else {
                                    Toast.makeText(context, "First add rider", Toast.LENGTH_SHORT)
                                        .show()
                                }

                            }
                        }
                }
            } else {
                holder.deliveryBtn.text = "Rider Added"
                holder.canceledBtn.visibility = View.GONE
                holder.deliveryBtn.setOnClickListener {
                    Toast.makeText(context, "Rider already added", Toast.LENGTH_SHORT)
                        .show()
                }
            }

        } else if (model[position].delivery.equals("Delivered")) {
            holder.deliveryBtn.text = "Order Successfully Completed"
            holder.canceledBtn.visibility = View.GONE
            holder.deliveryBtn.setOnClickListener {
                Toast.makeText(context, "Order already Delivered", Toast.LENGTH_SHORT).show()
            }
        } else if (model[position].delivery.equals("Canceled")) {
            holder.canceledBtn.text = "Order Canceled"
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
        var qty_no: TextView = itemView.findViewById<TextView?>(R.id.qty_no)
        var yourPrice: TextView = itemView.findViewById<TextView?>(R.id.yourPrice)

        var userUid: TextView = itemView.findViewById<TextView?>(R.id.userUid)
        var userName: TextView = itemView.findViewById<TextView?>(R.id.userName)
        var deliveryPrice: TextView = itemView.findViewById<TextView?>(R.id.deliveryPrice)
        var orderPayment: TextView = itemView.findViewById<TextView?>(R.id.orderPayment)

        var canceledBtn: AppCompatButton = itemView.findViewById<AppCompatButton?>(R.id.canceledBtn)
        var deliveryBtn: AppCompatButton = itemView.findViewById<AppCompatButton?>(R.id.deliveryBtn)

        var riderBg: ConstraintLayout = itemView.findViewById<ConstraintLayout?>(R.id.riderBg)
        var riderName: TextView = itemView.findViewById<TextView?>(R.id.riderName)
        var riderNumber: TextView = itemView.findViewById<TextView?>(R.id.riderNumber)
        var riderImage: CircleImageView = itemView.findViewById<CircleImageView?>(R.id.riderImage)

    }

    fun sendNotification(name: String?, message: String?, token: String?) {
        val key =
            "Key=AAAA1GKyPQY:APA91bHHqpGYjpQWwlHkB1SKY1HU_MbJHgll3RvthoX6C3CHDl3o86eb54u0ytDkvPtf4Zjr_acmVUKRVjtMwzND3bGg6XGQrzSxQFazinkADaAS4VJYFEOuIE0XtyhD0Cy02DjfPknL"
        var headers = HashMap<String, String>()
        headers["Content-Type"] = "application/json"
        headers["Authorization"] = key

        try {
            val queue: RequestQueue = Volley.newRequestQueue(context)
            val url = "https://fcm.googleapis.com/fcm/send"
            val data = JSONObject()
            data.put("title", name)
            data.put("body", message)
            val notificationData = JSONObject()
            notificationData.put("notification", data)
            notificationData.put("to", token)
            val request: JsonObjectRequest =
                object : JsonObjectRequest(url, notificationData,
                    Response.Listener<JSONObject> {
                        fun onResponse(response: JSONObject?) {}
                    }, object : ErrorListener, Response.ErrorListener {
                        override fun warning(p0: TransformerException?) {
                            TODO("Not yet implemented")
                        }

                        override fun error(p0: TransformerException?) {
                            TODO("Not yet implemented")
                        }

                        override fun fatalError(p0: TransformerException?) {
                            TODO("Not yet implemented")
                        }

                        override fun onErrorResponse(error: VolleyError?) {
                            TODO("Not yet implemented")
                        }

                    }) {
                    // Override getHeaders() to set custom headers
                    @Throws(AuthFailureError::class)
                    override fun getHeaders(): Map<String, String> {
                        return headers
                    }
                }
            queue.add(request)
        } catch (ex: java.lang.Exception) {
        }
    }
}


//            holder.deliveryBtn.setOnClickListener {
//
//                val builder = AlertDialog.Builder(context)
//                builder.setTitle("Order")
//                builder.setMessage("Order delivered ?")
//
//                builder.setPositiveButton("Yes") { dialog, which ->
//
//                    //////Order pending payment
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
//                    //////Order pending payment
//
//                    val userData: MutableMap<String, Any?> =
//                        HashMap()
//                    userData["delivery"] = "Delivered"
//                    userData["deliveredDate"] = System.currentTimeMillis().toString()
//                    FirebaseFirestore.getInstance()
//                        .collection("ORDER")
//                        .document(model[position].id)
//                        .update(userData)
//                        .addOnCompleteListener {
//                            if (it.isSuccessful) {
//                                notifyDataSetChanged()
//                                Toast.makeText(context, "Order Delivered", Toast.LENGTH_SHORT).show()
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
