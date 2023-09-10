package com.example.knockitbranchapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.knockitbranchapp.Database.CategoryDatabase
import com.example.knockitbranchapp.R
import com.example.knockitbranchapp.databinding.ActivityAddProductActivtyBinding
import com.example.knockitbranchapp.databinding.ActivityCategoryBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException

class CategoryActivity : AppCompatActivity() {

    lateinit var binding: ActivityCategoryBinding
    companion object{
        lateinit var categoryMainTitle: TextView
        lateinit var subCategoryRecyclerView: RecyclerView
        lateinit var categoryImage: ImageView
        lateinit var productNotAvailable: TextView
    }
    lateinit var categoryRecyclerView: RecyclerView
    lateinit var cartImage: ImageView
    lateinit var qtySizeText: TextView
    lateinit var qtyBg: LinearLayout
    lateinit var searchView: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        val view: View = binding.getRoot()
        setContentView(view)
        categoryMainTitle = findViewById(R.id.categoryMainTitle)!!
        categoryRecyclerView = findViewById(R.id.category_recyclerView)!!
        subCategoryRecyclerView = findViewById(R.id.sub_categoryRecyclerView)!!
        categoryImage = findViewById(R.id.category_image)!!
        productNotAvailable = findViewById(R.id.products_not_available_text)!!

        CategoryDatabase.loadCategoryMini(this,categoryRecyclerView)
        CategoryDatabase.loadSubCategory(
            this,
            CategoryActivity.subCategoryRecyclerView, "Fruits and Vegetables", CategoryActivity.productNotAvailable
        )

    }
}