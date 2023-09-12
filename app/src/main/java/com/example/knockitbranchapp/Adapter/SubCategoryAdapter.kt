package com.example.knockit.Adapter

import android.app.AlertDialog
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
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.knockitbranchapp.Model.SubCategoryModel
import com.example.knockitbranchapp.R
import com.google.firebase.firestore.FirebaseFirestore

class SubCategoryAdapter(var context: Context, var model: List<SubCategoryModel>) :
    RecyclerView.Adapter<SubCategoryAdapter.viewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        var view: View =
            LayoutInflater.from(context).inflate(R.layout.item_sub_category, parent, false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {

        holder.subCategoryTitle.text = model[position].subCategoryTitle
        Glide.with(context).load(model[position].subCategoryImage).into(holder.subCategoryImage)
        holder.categoryImageBg.backgroundTintList =
            ColorStateList.valueOf(Color.parseColor(model[position].subCategoryBackground))

        holder.itemView.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Delete")
            builder.setMessage("Are you sure to delete sub category ?")

            builder.setPositiveButton("Yes") { dialog, which ->

                FirebaseFirestore.getInstance()
                    .collection("SubCategory")
                    .document(model[position].id)
                    .delete()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            notifyDataSetChanged()
                            Toast.makeText(
                                context,
                                "Sub Category Deleted Successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {

                        }
                    }

            }

            builder.setNegativeButton("No") { dialog, which ->
            }
            builder.show()
            true
        }

    }

    override fun getItemCount(): Int {
        return model.size
    }

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var subCategoryTitle: TextView = itemView.findViewById<TextView?>(R.id.sub_CategoryTitle)
        var subCategoryImage: ImageView = itemView.findViewById<ImageView?>(R.id.sub_CategoryImage)
        var categoryImageBg: LinearLayout =
            itemView.findViewById<LinearLayout?>(R.id.circle_image_bg)
    }
}