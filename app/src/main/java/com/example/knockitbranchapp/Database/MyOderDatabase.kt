package com.example.knockitbranchapp.Database

import android.app.Dialog
import android.content.Context
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.knockitbranchapp.Adapter.MyOderAdapter
import com.example.knockitbranchapp.Model.MyOderModel
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import java.util.UUID

class MyOderDatabase {

    companion object {

        fun loadMyOder(context: Context, myOderRecyclerView: RecyclerView, deliveryText: String) {
            var oderModel: ArrayList<MyOderModel> = ArrayList<MyOderModel>()
            var CartAdapter = MyOderAdapter(context!!, oderModel)
            val layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            myOderRecyclerView.layoutManager = layoutManager
            myOderRecyclerView.adapter = CartAdapter
            var oderItems: ArrayList<MyOderModel> = ArrayList<MyOderModel>()

            FirebaseFirestore.getInstance()
                .collection("ODER")
                .orderBy("timeStamp", Query.Direction.ASCENDING)
                .addSnapshotListener { querySnapshot: QuerySnapshot?, e: FirebaseFirestoreException? ->
                    querySnapshot?.let {
                        oderItems.clear()
                        for (document in it) {
                            val model = document.toObject(MyOderModel::class.java)
                            oderItems.add(model)

                            oderModel.clear()
                            for (p in oderItems){
                                if (p.storeId.equals(FirebaseAuth.getInstance().uid)){
                                    if (p.delivery.equals(deliveryText)) {
                                        oderModel.add(p)
                                    }
                                }
                            }
                            CartAdapter.notifyDataSetChanged()
                        }
                    }
                }

//            FirebaseFirestore.getInstance()
//                .collection("PRODUCTS")
//                .document(productId)
//                .collection("productSize")
//                .orderBy("timeStamp", Query.Direction.DESCENDING)
//                .get().addOnSuccessListener(OnSuccessListener<QuerySnapshot> { queryDocumentSnapshots ->
//                    for (snapshot in queryDocumentSnapshots) {
//                        val model: SelectQtyModel = snapshot.toObject(SelectQtyModel::class.java)
//                        qtyModel.add(model)
//                    }
//                    qtyAdapter.notifyDataSetChanged()
//                })
        }
    }
}