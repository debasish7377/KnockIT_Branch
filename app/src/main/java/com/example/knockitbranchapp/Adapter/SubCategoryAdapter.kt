package com.example.knockit.Adapter

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
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.knockitbranchapp.Model.SubCategoryModel
import com.example.knockitbranchapp.R

class SubCategoryAdapter(var context: Context, var model: List<SubCategoryModel>) : RecyclerView.Adapter<SubCategoryAdapter.viewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        var view: View = LayoutInflater.from(context).inflate(R.layout.item_sub_category,parent,false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {

        holder.subCategoryTitle.text = model[position].subCategoryTitle
        Glide.with(context).load(model[position].subCategoryImage).into(holder.subCategoryImage)
        holder.categoryImageBg.backgroundTintList = ColorStateList.valueOf(Color.parseColor(model[position].subCategoryBackground))

        holder.itemView.setOnClickListener {

        }

        }

    override fun getItemCount(): Int {
        return model.size
    }

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var subCategoryTitle: TextView = itemView.findViewById<TextView?>(R.id.sub_CategoryTitle)
        var subCategoryImage: ImageView = itemView.findViewById<ImageView?>(R.id.sub_CategoryImage)
        var categoryImageBg: LinearLayout  = itemView.findViewById<LinearLayout?>(R.id.circle_image_bg)
    }
}