package com.example.knockitbranchapp.Activity

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.net.toUri
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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream
import java.util.UUID

class CategoryActivity : AppCompatActivity() {

    lateinit var binding: ActivityCategoryBinding

    companion object {
        lateinit var categoryMainTitle: TextView
        lateinit var subCategoryRecyclerView: RecyclerView
        lateinit var categoryImage: ImageView
        lateinit var productNotAvailable: TextView
        lateinit var categoryTitleSub: TextView
        lateinit var selecCcategoryDialog: Dialog
    }

    lateinit var categoryRecyclerView: RecyclerView
    lateinit var cartImage: ImageView
    lateinit var qtySizeText: TextView
    lateinit var qtyBg: LinearLayout
    lateinit var searchView: LinearLayout

    lateinit var categoryDialog: Dialog
    lateinit var loadingDialog: Dialog
    lateinit var greenColor: CircleImageView
    lateinit var redColor: CircleImageView
    lateinit var blueColor: CircleImageView
    lateinit var purpleColor: CircleImageView
    lateinit var yellowColor: CircleImageView
    lateinit var categoryTitle: EditText
    lateinit var selectedColor: TextView
    lateinit var selectCategoryImage: ImageView
    lateinit var okBtn: AppCompatButton
    var updateCategoryImage: Boolean = false
    lateinit var categoryImagePath: Uri

    lateinit var subCategoryDialog: Dialog
    lateinit var greenColorSub: CircleImageView
    lateinit var redColorSub: CircleImageView
    lateinit var blueColorSub: CircleImageView
    lateinit var purpleColorSub: CircleImageView
    lateinit var yellowColorSub: CircleImageView
    lateinit var subCategoryTitleSub: EditText
    lateinit var selectedColorSub: TextView
    lateinit var selectCategoryImageSub: ImageView
    lateinit var okBtnSub: AppCompatButton
    var updateSubCategoryImage: Boolean = false
    lateinit var subCategoryImagePath: Uri
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

        ////////////////category dialog
        categoryDialog = Dialog(this)
        categoryDialog.setContentView(R.layout.dialog_add_category)
        categoryDialog.setCancelable(true)
        categoryDialog.window?.setBackgroundDrawable(getDrawable(R.drawable.btn_buy_now))
        categoryDialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        okBtn = categoryDialog.findViewById(R.id.okBtn)
        greenColor = categoryDialog.findViewById(R.id.greenColor)
        redColor = categoryDialog.findViewById(R.id.redColor)
        yellowColor = categoryDialog.findViewById(R.id.yellowColor)
        blueColor = categoryDialog.findViewById(R.id.blueColor)
        purpleColor = categoryDialog.findViewById(R.id.purpleColor)
        categoryTitle = categoryDialog.findViewById(R.id.categoryText)
        selectedColor = categoryDialog.findViewById(R.id.selectedColor)
        selectCategoryImage = categoryDialog.findViewById(R.id.circleImageView)
        ////////////////category dialog

        ////////////////subCategory dialog
        subCategoryDialog = Dialog(this)
        subCategoryDialog.setContentView(R.layout.dialog_add_sub_category)
        subCategoryDialog.setCancelable(true)
        subCategoryDialog.window?.setBackgroundDrawable(getDrawable(R.drawable.btn_buy_now))
        subCategoryDialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        okBtnSub = subCategoryDialog.findViewById(R.id.okBtnSub)
        greenColorSub = subCategoryDialog.findViewById(R.id.greenColorSub)
        redColorSub = subCategoryDialog.findViewById(R.id.redColorSub)
        yellowColorSub = subCategoryDialog.findViewById(R.id.yellowColorSub)
        blueColorSub = subCategoryDialog.findViewById(R.id.blueColorSub)
        purpleColorSub = subCategoryDialog.findViewById(R.id.purpleColorSub)
        categoryTitleSub = subCategoryDialog.findViewById(R.id.categoryTextSub)
        subCategoryTitleSub = subCategoryDialog.findViewById(R.id.subCategoryTextSub)
        selectedColorSub = subCategoryDialog.findViewById(R.id.selectedColorSub)
        selectCategoryImageSub = subCategoryDialog.findViewById(R.id.circleImageViewSub)
        ////////////////subCategory dialog

        ////////////////loading dialog
        loadingDialog = Dialog(this)
        loadingDialog.setContentView(R.layout.dialog_loading)
        loadingDialog.setCancelable(false)
        loadingDialog.window?.setBackgroundDrawable(getDrawable(R.drawable.login_btn_bg))
        loadingDialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        ////////////////loading dialog

        ////////////////loading dialog
        selecCcategoryDialog = Dialog(this)
        selecCcategoryDialog.setContentView(R.layout.dialog_category)
        selecCcategoryDialog.setCancelable(false)
        selecCcategoryDialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        var categoryRecyclerView1: RecyclerView =
            selecCcategoryDialog.findViewById(R.id.categoryRecyclerView)!!
        CategoryDatabase.loadSelectCategoryByAddSubCategory(this, categoryRecyclerView1)
        ////////////////loading dialog

        addCategory()
        addSubCategory()

        CategoryDatabase.loadCategoryMini(this, categoryRecyclerView)
        CategoryDatabase.loadSubCategory(
            this,
            CategoryActivity.subCategoryRecyclerView,
            "Fruits and Vegetables",
            CategoryActivity.productNotAvailable
        )

    }

    fun addSubCategory() {
        binding.addSubCategory.setOnClickListener {
            subCategoryDialog.show()
        }

        categoryTitleSub.setOnClickListener {
            selecCcategoryDialog.show()
        }

        greenColorSub.setOnClickListener {
            selectedColorSub.text = "#EBFFE2"
        }
        redColorSub.setOnClickListener {
            selectedColorSub.text = "#FFDFDF"
        }
        yellowColorSub.setOnClickListener {
            selectedColorSub.text = "#FFFCE0"
        }
        blueColorSub.setOnClickListener {
            selectedColorSub.text = "#F0F4FF"
        }
        purpleColorSub.setOnClickListener {
            selectedColorSub.text = "#F8E6FF"
        }

        selectCategoryImageSub.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                2
            )
        }

        okBtnSub.setOnClickListener {
            if (!categoryTitleSub.text.toString().equals("")) {
                if (!selectedColorSub.text.toString().equals("")) {
                    if (!subCategoryTitleSub.text.toString().equals("")) {
                        if (updateSubCategoryImage) {

                            loadingDialog.show()
                            subCategoryDialog.dismiss()
                            var reference: StorageReference =
                                FirebaseStorage.getInstance().getReference()
                                    .child("subCategoryImages").child(
                                        System.currentTimeMillis()
                                            .toString()
                                    );
                            reference.putFile(subCategoryImagePath)
                                .addOnCompleteListener {
                                    reference.downloadUrl.addOnSuccessListener { subCategoryImage ->


                                        val randomString =
                                            UUID.randomUUID().toString().substring(0, 25)
                                        val userData: MutableMap<Any, Any?> =
                                            HashMap()
                                        userData["id"] = randomString
                                        userData["subCategoryImage"] = subCategoryImage.toString()
                                        userData["category"] = categoryTitleSub.text.toString()
                                        userData["subCategoryBackground"] =
                                            selectedColorSub.text.toString()
                                        userData["subCategoryTitle"] =
                                            subCategoryTitleSub.text.toString()
                                        userData["timeStamp"] =
                                            System.currentTimeMillis().toString()
                                        FirebaseFirestore.getInstance()
                                            .collection("SubCategory")
                                            .document(randomString)
                                            .set(userData)
                                            .addOnCompleteListener {
                                                Toast.makeText(
                                                    this,
                                                    "Sub Category Successfully added",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                loadingDialog.dismiss()
                                            }
                                    }
                                }
                        } else {
                            Toast.makeText(this, "Enter Sub Category", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, "Select Image", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Select Color", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Enter Category", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun addCategory() {
        binding.addCategory.setOnClickListener {
            categoryDialog.show()
        }

        greenColor.setOnClickListener {
            selectedColor.text = "#EBFFE2"
        }
        redColor.setOnClickListener {
            selectedColor.text = "#FFDFDF"
        }
        yellowColor.setOnClickListener {
            selectedColor.text = "#FFFCE0"
        }
        blueColor.setOnClickListener {
            selectedColor.text = "#F0F4FF"
        }
        purpleColor.setOnClickListener {
            selectedColor.text = "#F8E6FF"
        }

        selectCategoryImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                1
            )
        }

        okBtn.setOnClickListener {
            if (!categoryTitle.text.toString().equals("")) {
                if (!selectedColor.text.toString().equals("")) {
                    if (updateCategoryImage) {

                        loadingDialog.show()
                        categoryDialog.dismiss()
                        var reference: StorageReference =
                            FirebaseStorage.getInstance().getReference()
                                .child("categoryImages").child(
                                    System.currentTimeMillis()
                                        .toString()
                                );
                        reference.putFile(categoryImagePath)
                            .addOnCompleteListener {
                                reference.downloadUrl.addOnSuccessListener { categoryImage ->


                                    val randomString = UUID.randomUUID().toString().substring(0, 25)
                                    val userData: MutableMap<Any, Any?> =
                                        HashMap()
                                    userData["id"] = randomString
                                    userData["categoryImage"] = categoryImage.toString()
                                    userData["categoryTitle"] = categoryTitle.text.toString()
                                    userData["categoryBackground"] = selectedColor.text.toString()
                                    userData["timeStamp"] = System.currentTimeMillis().toString()
                                    FirebaseFirestore.getInstance()
                                        .collection("Category")
                                        .document(randomString)
                                        .set(userData)
                                        .addOnCompleteListener {
                                            Toast.makeText(
                                                this,
                                                "Category Successfully added",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            loadingDialog.dismiss()
                                        }
                                }
                            }
                    } else {
                        Toast.makeText(this, "Select Image", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Select Color", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Enter Category", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK) {
            updateCategoryImage = true
            categoryImagePath = data?.data!!
            Glide.with(this).load(data?.data!!).into(selectCategoryImage)

        }

        if (requestCode == 2 && resultCode == RESULT_OK) {
            updateSubCategoryImage = true
            subCategoryImagePath = data?.data!!
            Glide.with(this).load(data?.data!!).into(selectCategoryImageSub)

        }
    }
}