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
import com.example.knockitbranchapp.Model.MyOderModel
import com.example.knockitbranchapp.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot


class MyOderAdapter(var context: Context, var model: ArrayList<MyOderModel>) :
    RecyclerView.Adapter<MyOderAdapter.viewHolder>() {
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
        holder.productTitle.text = model[position].productTitle
        holder.productPrice.text = model[position].productPrice.toString()
        holder.productCuttedPrice.text = model[position].productCuttedPrice.toString()
        holder.yourPrice.text = "Your Price ₹" + model[position].price.toString()
        holder.qty_text.text = model[position].qty
        var youSaved: String =
            (model[position].productCuttedPrice.toInt() - model[position].productPrice.toInt()).toString()
        holder.discountedPrice.text = "₹" + youSaved + " Saved"

        holder.canceledBtn.setOnClickListener {

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Oder")
            builder.setMessage("Are you sure to confirm this oder ?")

            builder.setPositiveButton("Yes") { dialog, which ->

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
        }

        if (model[position].delivery.equals("Pending")) {
            holder.deliveryBtn.text = "Oder confirmed"

            holder.deliveryBtn.setOnClickListener {

                val builder = AlertDialog.Builder(context)
                builder.setTitle("Oder")
                builder.setMessage("Are you sure to confirm this oder ?")

                builder.setPositiveButton("Yes") { dialog, which ->

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

            holder.deliveryBtn.setOnClickListener {

                val builder = AlertDialog.Builder(context)
                builder.setTitle("Oder")
                builder.setMessage("Oder delivered ?")

                builder.setPositiveButton("Yes") { dialog, which ->

                    val userData: MutableMap<String, Any?> =
                        HashMap()
                    userData["delivery"] = "Delivered"
                    userData["deliveredDate"] = System.currentTimeMillis().toString()
                    FirebaseFirestore.getInstance()
                        .collection("ODER")
                        .document(model[position].id)
                        .update(userData)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                notifyDataSetChanged()
                                Toast.makeText(context, "Oder Delivered", Toast.LENGTH_SHORT).show()
                            } else {

                            }
                        }
                }

                builder.setNegativeButton("No") { dialog, which ->
                }

                builder.show()
            }
        } else if (model[position].delivery.equals("Delivered")) {
            holder.deliveryBtn.text = "Oder Successfully Completed"
            holder.canceledBtn.visibility = View.GONE
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

        var canceledBtn: AppCompatButton = itemView.findViewById<AppCompatButton?>(R.id.canceledBtn)
        var deliveryBtn: AppCompatButton = itemView.findViewById<AppCompatButton?>(R.id.deliveryBtn)

    }
}
