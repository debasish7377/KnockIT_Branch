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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.knockitbranchapp.Model.ProductSpecificationModel
import com.example.knockitbranchapp.R
import com.google.firebase.firestore.FirebaseFirestore

class ProductSpecificationAdapter(var context: Context, var model: ArrayList<ProductSpecificationModel>) : RecyclerView.Adapter<ProductSpecificationAdapter.viewHolder>() {

    lateinit var loadingDialog: Dialog
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        var view: View = LayoutInflater.from(context).inflate(R.layout.item_product_specification,parent,false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {

        holder.brand.text = model[position].brand
        holder.value.text = model[position].value

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

        holder.itemView.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Delete")
            builder.setMessage("Are you sure to delete this item ?")

            builder.setPositiveButton("Yes") { dialog, which ->

                loadingDialog.show()
                FirebaseFirestore.getInstance()
                    .collection("PRODUCTS")
                    .document(model[position].productId)
                    .collection("productSpecification")
                    .document(model[position].id)
                    .delete()
                    .addOnCompleteListener {
                        notifyDataSetChanged()
                        Toast.makeText(context, "item Deleted Successfully", Toast.LENGTH_SHORT).show()

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

        var brand: TextView = itemView.findViewById<TextView?>(R.id.brand_text)
        var value: TextView = itemView.findViewById<TextView?>(R.id.value_text)

    }
}