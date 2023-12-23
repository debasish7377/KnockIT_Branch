package com.example.knockitbranchapp.Database

import android.app.Dialog
import android.content.Context
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.knockitbranchapp.Adapter.RiderAdapter
import com.example.knockitbranchapp.Model.BranchModel
import com.example.knockitbranchapp.Model.RiderModel
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import java.util.UUID

class RiderDatabase {

    companion object {

        fun loadRider(context: Context, riderRecyclerView: RecyclerView) {
            var riderModel: ArrayList<RiderModel> = ArrayList<RiderModel>()
            var riderAdapter = RiderAdapter(context!!, riderModel)
            val layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            riderRecyclerView.layoutManager = layoutManager
            riderRecyclerView.adapter = riderAdapter
            var riderItems: ArrayList<RiderModel> = ArrayList<RiderModel>()

            FirebaseFirestore.getInstance()
                .collection("RIDERS")
                .orderBy("timeStamp", Query.Direction.ASCENDING)
                .addSnapshotListener { querySnapshot: QuerySnapshot?, e: FirebaseFirestoreException? ->
                    querySnapshot?.let {
                        riderItems.clear()
                        for (document in it) {
                            val model = document.toObject(RiderModel::class.java)
                            riderItems.add(model)

                            ////// Location wise rider Display
                            FirebaseFirestore.getInstance().collection("BRANCHES")
                                .document(FirebaseAuth.getInstance().uid.toString())
                                .get()
                                .addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
                                    val model: BranchModel? =
                                        documentSnapshot.toObject(BranchModel::class.java)

                                    riderModel.clear()
                                    for (p in riderItems) {
                                        if (p.city.equals(model?.city)) {
                                            if (p.connectWithStore.equals("")) {
                                                riderModel.add(p)
                                            }
                                        }
                                    }
                                    riderAdapter.notifyDataSetChanged()
                                })
                            ////// Location wise rider Display
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