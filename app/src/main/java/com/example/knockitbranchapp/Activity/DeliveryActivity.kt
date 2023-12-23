package com.example.knockitbranchapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.knockitbranchapp.Adapter.DeliveryListAdapter
import com.example.knockitbranchapp.Adapter.SelectCategoryAdapterByAddProduct
import com.example.knockitbranchapp.Database.MyOderDatabase
import com.example.knockitbranchapp.Model.CategoryModel
import com.example.knockitbranchapp.Model.DeliveryListModel
import com.example.knockitbranchapp.R
import com.example.knockitbranchapp.databinding.ActivityAddProductQuantityBinding
import com.example.knockitbranchapp.databinding.ActivityDeliveryBinding

class DeliveryActivity : AppCompatActivity() {

    lateinit var binding: ActivityDeliveryBinding

    companion object {
        lateinit var deliveryText: TextView
        lateinit var deliveryRecyclerView: RecyclerView
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeliveryBinding.inflate(layoutInflater)
        val view: View = binding.getRoot()
        setContentView(view)
        deliveryText = findViewById(R.id.deliveryText)
        deliveryRecyclerView = findViewById(R.id.deliveryRecyclerView)

        ////// Delivery List
        var deliveryListModel: ArrayList<DeliveryListModel> = ArrayList<DeliveryListModel>()
        val layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL)
        binding.deliveryListRecyclerView.layoutManager = layoutManager

        deliveryListModel.add(DeliveryListModel("Pending"))
        deliveryListModel.add(DeliveryListModel("Order confirmed"))
        deliveryListModel.add(DeliveryListModel("Shipped"))
        deliveryListModel.add(DeliveryListModel("Out for delivery"))
        deliveryListModel.add(DeliveryListModel("Delivered"))
        deliveryListModel.add(DeliveryListModel("Canceled"))

        var deliveryListAdapter = DeliveryListAdapter(this, deliveryListModel)
        binding.deliveryListRecyclerView.adapter = deliveryListAdapter
        ////// Delivery List

        MyOderDatabase.loadMyOder(this, binding.deliveryRecyclerView, deliveryText.text.toString())

    }
}