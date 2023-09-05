package com.example.knockitbranchapp.Adapter

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.knockitbranchapp.Model.ProductImagesModel
import com.example.knockitbranchapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class ProductImagesAdapter(var context: Context, var model: ArrayList<ProductImagesModel>) : RecyclerView.Adapter<ProductImagesAdapter.viewHolder>() {

    lateinit var loadingDialog: Dialog
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        var view: View = LayoutInflater.from(context).inflate(R.layout.item_product_images,parent,false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {

        Glide.with(context).load(model[position].image).into(holder.productImage)

//        for (i in model){
//            val userData: MutableMap<Any, String?> = HashMap()
//            userData["title"] = i.categoryTitle
//            FirebaseFirestore.getInstance().collection("Hii")
//                .document(i.categoryTitle)
//                .set(userData).addOnCompleteListener {
//                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
//                }
//        }

        ////////////////loading dialog
        loadingDialog = Dialog(context)
        loadingDialog.setContentView(R.layout.dialog_loading)
        loadingDialog.setCancelable(false)
        loadingDialog.window?.setBackgroundDrawable(context.getDrawable(R.drawable.login_btn_bg))
        loadingDialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        ////////////////loading dialog

        holder.deleteImage.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Delete")
            builder.setMessage("Are you sure to delete this item ?")

            builder.setPositiveButton("Yes") { dialog, which ->

                loadingDialog.show()
                FirebaseFirestore.getInstance()
                    .collection("PRODUCTS")
                    .document(model[position].productId)
                    .collection("productImages")
                    .document(model[position].id)
                    .delete()
                    .addOnCompleteListener {
                        notifyDataSetChanged()
                        Toast.makeText(context, "Image Deleted Successfully", Toast.LENGTH_SHORT).show()
                        loadingDialog.dismiss()
                    }
                loadingDialog.dismiss()

            }

            builder.setNegativeButton("No") { dialog, which ->
            }

            builder.show()

        }

        }

    override fun getItemCount(): Int {
        return model.size
    }

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var productImage: ImageView = itemView.findViewById<ImageView?>(R.id.product_images)
        var deleteImage: AppCompatButton = itemView.findViewById<AppCompatButton?>(R.id.deleteImage)

    }
}