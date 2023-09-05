package com.example.knockitbranchapp.Adapter

import android.R.attr.name
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
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
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.knockitbranchapp.Model.SelectQtyModel
import com.example.knockitbranchapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class SelectQtyAdapter(var context: Context, var model: List<SelectQtyModel>) :
    RecyclerView.Adapter<SelectQtyAdapter.viewHolder>() {

    lateinit var loadingDialog: Dialog
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        var view: View = LayoutInflater.from(context).inflate(R.layout.item_select_qty, parent, false)
        return viewHolder(view)
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {

        holder.price.text = model[position].price
        holder.cuttedPrice.text = model[position].cuttedPrice
        holder.qty_text.text = model[position].qty
        if (model[position].availableQty == 0){
            holder.avlQtyText.text = model[position].availableQty.toString()
        }else{
            holder.avlQtyText.text = model[position].availableQty.toString()+" left"
        }

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
                    .collection("productSize")
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

        var price: TextView = itemView.findViewById<TextView?>(R.id.price_text)
        var cuttedPrice: TextView = itemView.findViewById<TextView?>(R.id.cutted_price_text)
        var qty_text: TextView = itemView.findViewById<TextView?>(R.id.qty_text)
        var avlQtyText: TextView = itemView.findViewById<TextView?>(R.id.avl_qty_text)
    }
}