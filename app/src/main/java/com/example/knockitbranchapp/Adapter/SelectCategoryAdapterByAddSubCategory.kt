package com.example.knockitbranchapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.knockitbranchapp.Activity.AddProductActivity
import com.example.knockitbranchapp.Activity.CategoryActivity
import com.example.knockitbranchapp.Activity.MainActivity
import com.example.knockitbranchapp.Model.CategoryModel
import com.example.knockitbranchapp.R


class SelectCategoryAdapterByAddSubCategory(var context: Context, var model: List<CategoryModel>) :
    RecyclerView.Adapter<SelectCategoryAdapterByAddSubCategory.viewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        var view: View = LayoutInflater.from(context).inflate(R.layout.item_select_category, parent, false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {

        holder.categoryTitle.text = model[position].categoryTitle

        holder.itemView.setOnClickListener {
            CategoryActivity.categoryTitleSub.text = model[position].categoryTitle.toString()
            CategoryActivity.selecCcategoryDialog.dismiss()
        }
    }

    override fun getItemCount(): Int {
        return model.size
    }

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var categoryTitle: TextView = itemView.findViewById<TextView?>(R.id.category_title)

    }
}