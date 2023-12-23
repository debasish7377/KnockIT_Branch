package com.example.knockitbranchapp.Database

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.knockitbranchapp.Adapter.ProductImagesAdapter
import com.example.knockitbranchapp.Adapter.ProductSpecificationAdapter
import com.example.knockitbranchapp.Adapter.ProductsAdapter
import com.example.knockitbranchapp.Adapter.SelectQtyAdapter
import com.example.knockitbranchapp.Model.BranchModel
import com.example.knockitbranchapp.Model.ProductImagesModel
import com.example.knockitbranchapp.Model.ProductModel
import com.example.knockitbranchapp.Model.ProductSpecificationModel
import com.example.knockitbranchapp.Model.SelectQtyModel
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import java.util.Timer
import java.util.TimerTask

class ProductDatabase {

    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    var firebasefirestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    companion object {
        fun loadProduct(
            context: Context,
            productRecyclerView: RecyclerView
        ) {
            var productModel: ArrayList<ProductModel> = ArrayList<ProductModel>()
            var productAdapter = ProductsAdapter(context!!, productModel)
            val layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            productRecyclerView.layoutManager = layoutManager
            productRecyclerView.adapter = productAdapter
            val products = ArrayList<ProductModel>()

            FirebaseFirestore.getInstance()
                .collection("PRODUCTS")
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .addSnapshotListener { querySnapshot: QuerySnapshot?, e: FirebaseFirestoreException? ->
                    querySnapshot?.let {
                        products.clear()
                        for (document in it) {
                            val model = document.toObject(ProductModel::class.java)
                            products.add(model)

                            ////// Location wise product Display
                            FirebaseFirestore.getInstance().collection("BRANCHES")
                                .document(FirebaseAuth.getInstance().uid.toString())
                                .get()
                                .addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
                                    val model: BranchModel? =
                                        documentSnapshot.toObject(BranchModel::class.java)

                                    productModel.clear()
                                    for (p in products) {
                                        if (p.storeId.equals(model?.storeId)){
                                            if (p.productVerification.equals("Public")) {
                                                productModel.add(p)
                                            }
                                        }

                                    }
                                    productAdapter.notifyDataSetChanged()
                                })
                            ////// Location wise product Display


                        }
                    }
                }
        }

        fun insertProductImages(
            context: Context,
            productRecyclerView: RecyclerView,
            productId: String
        ) {
            var productModel: ArrayList<ProductModel> = ArrayList<ProductModel>()
            var productAdapter = ProductsAdapter(context!!, productModel)
            val layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            productRecyclerView.layoutManager = layoutManager
            productRecyclerView.adapter = productAdapter
            val products = ArrayList<ProductModel>()

            FirebaseFirestore.getInstance()
                .collection("PRODUCTS")
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .addSnapshotListener { querySnapshot: QuerySnapshot?, e: FirebaseFirestoreException? ->
                    querySnapshot?.let {
                        products.clear()
                        for (document in it) {
                            val model = document.toObject(ProductModel::class.java)
                            products.add(model)

                            ////// Location wise product Display
                            FirebaseFirestore.getInstance().collection("BRANCHES")
                                .document(FirebaseAuth.getInstance().uid.toString())
                                .get()
                                .addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
                                    val model: BranchModel? =
                                        documentSnapshot.toObject(BranchModel::class.java)

                                    productModel.clear()
                                    for (p in products) {
                                        if (p.storeId.equals(model?.storeId)){
                                            productModel.add(p)
                                        }

                                    }
                                    productAdapter.notifyDataSetChanged()
                                })
                            ////// Location wise product Display


                        }
                    }
                }
        }

        fun loadImages(context: Context ,productId: String, imagesRecyclerView: RecyclerView){
            var productImagesModel: ArrayList<ProductImagesModel> = ArrayList<ProductImagesModel>()
            val bannerLayout = LinearLayoutManager(context)
            bannerLayout.orientation = RecyclerView.VERTICAL
            imagesRecyclerView.layoutManager = bannerLayout

            var productImagesAdapter = ProductImagesAdapter(context!!, productImagesModel)
            imagesRecyclerView.adapter = productImagesAdapter

            FirebaseFirestore.getInstance()
                .collection("PRODUCTS")
                .document(productId)
                .collection("productImages")
                .orderBy("timeStamp", Query.Direction.ASCENDING)
                .addSnapshotListener { querySnapshot: QuerySnapshot?, e: FirebaseFirestoreException? ->
                    querySnapshot?.let {
                        productImagesModel.clear()
                        for (document in it) {
                            val model = document.toObject(ProductImagesModel::class.java)
                            productImagesModel.add(model)
                        }
                        productImagesAdapter.notifyDataSetChanged()
                    }
                }
        }

        fun loadSelectSize(context: Context ,productId: String, productSelectRecyclerView: RecyclerView){
            var selectQtyModel: ArrayList<SelectQtyModel> = ArrayList<SelectQtyModel>()
            val bannerLayout = LinearLayoutManager(context)
            bannerLayout.orientation = RecyclerView.VERTICAL
            productSelectRecyclerView.layoutManager = bannerLayout

            var productSelectAdapter = SelectQtyAdapter(context!!, selectQtyModel)
            productSelectRecyclerView.adapter = productSelectAdapter

            FirebaseFirestore.getInstance()
                .collection("PRODUCTS")
                .document(productId)
                .collection("productSize")
                .orderBy("timeStamp", Query.Direction.ASCENDING)
                .addSnapshotListener { querySnapshot: QuerySnapshot?, e: FirebaseFirestoreException? ->
                    querySnapshot?.let {
                        selectQtyModel.clear()
                        for (document in it) {
                            val model = document.toObject(SelectQtyModel::class.java)
                            selectQtyModel.add(model)
                        }
                        productSelectAdapter.notifyDataSetChanged()
                    }
                }

//            FirebaseFirestore.getInstance()
//                .collection("PRODUCTS")
//                .document(productId)
//                .collection("productSize")
//                .orderBy("timeStamp", Query.Direction.ASCENDING)
//                .get().addOnSuccessListener(OnSuccessListener<QuerySnapshot> { queryDocumentSnapshots ->
//                    for (snapshot in queryDocumentSnapshots) {
//                        val model: SelectQtyModel = snapshot.toObject(SelectQtyModel::class.java)
//                        selectQtyModel.add(model)
//                    }
//                    productSelectAdapter.notifyDataSetChanged()
//                })
        }

        fun loadSpecification(context: Context ,productId: String, specificationRecyclerView: RecyclerView){
            var specificationModel: ArrayList<ProductSpecificationModel> = ArrayList<ProductSpecificationModel>()
            val bannerLayout = LinearLayoutManager(context)
            bannerLayout.orientation = RecyclerView.VERTICAL
            specificationRecyclerView.layoutManager = bannerLayout

            var specificationAdapter = ProductSpecificationAdapter(context!!, specificationModel)
            specificationRecyclerView.adapter = specificationAdapter

            FirebaseFirestore.getInstance()
                .collection("PRODUCTS")
                .document(productId)
                .collection("productSpecification")
                .orderBy("timeStamp", Query.Direction.ASCENDING)
                .addSnapshotListener { querySnapshot: QuerySnapshot?, e: FirebaseFirestoreException? ->
                    querySnapshot?.let {
                        specificationModel.clear()
                        for (document in it) {
                            val model = document.toObject(ProductSpecificationModel::class.java)
                            specificationModel.add(model)
                        }
                        specificationAdapter.notifyDataSetChanged()
                    }
                }
        }

    }
}