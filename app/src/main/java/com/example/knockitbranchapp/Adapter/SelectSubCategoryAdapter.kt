package com.example.knockitbranchapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.knockitbranchapp.Activity.AddProductActivity
import com.example.knockitbranchapp.Activity.MainActivity
import com.example.knockitbranchapp.Model.SubCategoryModel
import com.example.knockitbranchapp.R

class SelectSubCategoryAdapter(var context: Context, var model: List<SubCategoryModel>) : RecyclerView.Adapter<SelectSubCategoryAdapter.viewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        var view: View = LayoutInflater.from(context).inflate(R.layout.item_select_sub_category,parent,false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {

        holder.subCategoryTitle.text = model[position].subCategoryTitle

        holder.itemView.setOnClickListener {
            AddProductActivity.selectSubCategory.text = model[position].subCategoryTitle.toString()
            AddProductActivity.subCategoryDialog.dismiss()
        }

        }

    override fun getItemCount(): Int {
        return model.size
    }

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var subCategoryTitle: TextView = itemView.findViewById<TextView?>(R.id.sub_category_title)
    }
}