package com.example.knockitbranchapp.Adapter

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.knockitbranchapp.Activity.DeliveryActivity
import com.example.knockitbranchapp.Database.MyOderDatabase
import com.example.knockitbranchapp.Model.DeliveryListModel
import com.example.knockitbranchapp.R

class DeliveryListAdapter(var context: Context, var model: ArrayList<DeliveryListModel>) : RecyclerView.Adapter<DeliveryListAdapter.viewHolder>() {

    lateinit var loadingDialog: Dialog
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        var view: View = LayoutInflater.from(context).inflate(R.layout.item_delivery_list,parent,false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {

        holder.deliveryListTitle.text = model[position].deliveryList

        holder.itemView.setOnClickListener {
            DeliveryActivity.deliveryText.text = model[position].deliveryList
            MyOderDatabase.loadMyOder(context, DeliveryActivity.deliveryRecyclerView, model[position].deliveryList)
        }
        }

    override fun getItemCount(): Int {
        return model.size
    }

    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var deliveryListTitle: TextView = itemView.findViewById<TextView?>(R.id.delivery_list_text)

    }
}