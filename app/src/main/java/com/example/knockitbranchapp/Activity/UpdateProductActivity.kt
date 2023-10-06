package com.example.knockitbranchapp.Activity

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.knockitbranchapp.Database.CategoryDatabase
import com.example.knockitbranchapp.Model.BranchModel
import com.example.knockitbranchapp.Model.ProductModel
import com.example.knockitbranchapp.R
import com.example.knockitbranchapp.databinding.ActivityAddProductActivtyBinding
import com.example.knockitbranchapp.databinding.ActivityUpdateProductBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import java.io.ByteArrayOutputStream
import java.util.UUID

class UpdateProductActivity : AppCompatActivity() {

    lateinit var binding: ActivityUpdateProductBinding
    lateinit var productImagePath: Uri
    var updateProductImage: Boolean = false
    lateinit var compressedImage: Bitmap

    lateinit var loadingDialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateProductBinding.inflate(layoutInflater)
        val view: View = binding.getRoot()
        setContentView(view)
        var productId = intent.getStringExtra("productId")

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

        FirebaseFirestore.getInstance().collection("PRODUCTS")
            .document(productId.toString())
            .get()
            .addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
                val model: ProductModel? = documentSnapshot.toObject(ProductModel::class.java)

                binding.productTitle.text= Editable.Factory.getInstance().newEditable(model?.productTitle.toString())
                binding.productDescription.text= Editable.Factory.getInstance().newEditable(model?.productDescription.toString())
                binding.addProducts.brandName.text= Editable.Factory.getInstance().newEditable(model?.productBrandName.toString())
                binding.addProducts.selectCategory.text= model?.productCategory.toString()
                binding.addProducts.subCategory.text= model?.productSubCategory.toString()
                binding.addProducts.OriginalPrice.text= Editable.Factory.getInstance().newEditable(model?.productCuttedPrice.toString())
                binding.addProducts.discountedPrice.text= Editable.Factory.getInstance().newEditable(model?.productPrice.toString())
                Glide.with(this).load(model?.productImage).into(binding.productImage)

                binding.addProducts.city1.text= Editable.Factory.getInstance().newEditable(model?.city_1.toString())
                binding.addProducts.city2.text= Editable.Factory.getInstance().newEditable(model?.city_2.toString())
                binding.addProducts.city3.text= Editable.Factory.getInstance().newEditable(model?.city_3.toString())
                binding.addProducts.city4.text= Editable.Factory.getInstance().newEditable(model?.city_4.toString())
                binding.addProducts.city5.text= Editable.Factory.getInstance().newEditable(model?.city_5.toString())

            })

        binding.addImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                1
            )
        }

        FirebaseFirestore.getInstance().collection("BRANCHES")
            .document(FirebaseAuth.getInstance().uid.toString())
            .get()
            .addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
                val model: BranchModel? = documentSnapshot.toObject(BranchModel::class.java)

                binding.addProducts.city1.text = model?.city.toString()
            })

        binding.addProducts.OkBtn.setOnClickListener {
            binding.addProducts.OkBtn.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
            if (updateProductImage) {
                if (!binding.productTitle.text.isEmpty()) {
                    if (!binding.productDescription.text.toString().isEmpty()) {
                        if (!binding.addProducts.brandName.text.toString().isEmpty()) {
                            if (!binding.addProducts.selectCategory.text.toString().isEmpty()) {
                                if (!binding.addProducts.subCategory.text.toString().isEmpty()) {
                                    if (!binding.addProducts.OriginalPrice.text.toString().isEmpty()) {
                                        if (!binding.addProducts.discountedPrice.text.toString().isEmpty()) {
                                            if (!binding.addProducts.city1.text.toString().isEmpty()) {
                                                if (!binding.addProducts.productSearch.text.toString().isEmpty()) {

                                                    loadingDialog.show()
                                                    var tagArray: List<String> =
                                                        binding.addProducts.productSearch.text.toString()
                                                            .split(",")
                                                    var tags: List<String> = tagArray

                                                    var reference: StorageReference =
                                                        FirebaseStorage.getInstance().getReference()
                                                            .child("productImages").child(
                                                                System.currentTimeMillis()
                                                                    .toString()
                                                            );
                                                    reference.putFile(productImagePath)
                                                        .addOnCompleteListener {
                                                            reference.downloadUrl.addOnSuccessListener { productImage ->

                                                                val userData: MutableMap<String, Any?> =
                                                                    HashMap()
                                                                userData["city_1"] =
                                                                    binding.addProducts.city1.text.toString()
                                                                userData["city_2"] =
                                                                    binding.addProducts.city2.text.toString()
                                                                userData["city_3"] =
                                                                    binding.addProducts.city3.text.toString()
                                                                userData["city_4"] =
                                                                    binding.addProducts.city4.text.toString()
                                                                userData["city_5"] =
                                                                    binding.addProducts.city5.text.toString()

                                                                userData["productBrandName"] =
                                                                    binding.addProducts.brandName.text.toString()
                                                                userData["productTitle"] =
                                                                    binding.productTitle.text.toString()
                                                                userData["productCategory"] =
                                                                    binding.addProducts.selectCategory.text.toString()
                                                                userData["productCuttedPrice"] =
                                                                    binding.addProducts.OriginalPrice.text.toString()
                                                                        .toLong()
                                                                userData["productDescription"] =
                                                                    binding.productDescription.text.toString()
                                                                userData["productImage"] =
                                                                    productImage.toString()
                                                                userData["productPrice"] =
                                                                    binding.addProducts.discountedPrice.text.toString()
                                                                        .toLong()
                                                                userData["productSearch"] = tags
                                                                userData["productSubCategory"] =
                                                                    binding.addProducts.subCategory.text.toString()
                                                                userData["productVerification"] =
                                                                    "Private"
                                                                userData["storeId"] =
                                                                    FirebaseAuth.getInstance().uid.toString()
                                                                userData["timeStamp"] =
                                                                    System.currentTimeMillis()

                                                                FirebaseFirestore.getInstance()
                                                                    .collection("PRODUCTS")
                                                                    .document(productId.toString())
                                                                    .update(userData)
                                                                    .addOnCompleteListener {
                                                                        if (it.isSuccessful) {
                                                                            binding.progressBar.visibility =
                                                                                View.GONE
                                                                            Toast.makeText(
                                                                                this,
                                                                                "Add Product Images",
                                                                                Toast.LENGTH_SHORT
                                                                            ).show()
                                                                            var intent = Intent(this, AddProductImages::class.java)
                                                                            intent.putExtra("productId", productId)
                                                                            startActivity(intent)
                                                                            finish()
                                                                            binding.addProducts.OkBtn.visibility =
                                                                                View.VISIBLE
                                                                        } else {
                                                                            Toast.makeText(
                                                                                this,
                                                                                it.exception.toString(),
                                                                                Toast.LENGTH_SHORT
                                                                            ).show()
                                                                        }
                                                                        loadingDialog.dismiss()
                                                                    }

                                                            }
                                                        }
                                                } else {
                                                    binding.addProducts.productSearch.error =
                                                        "Enter Search tags"
                                                    binding.progressBar.visibility = View.GONE
                                                    binding.addProducts.productSearch.setText("")
                                                    binding.addProducts.OkBtn.visibility =
                                                        View.VISIBLE
                                                }
                                            } else {
                                                binding.addProducts.city1.error = "City"
                                                binding.progressBar.visibility = View.GONE
                                                binding.addProducts.city1.setText("")
                                                binding.addProducts.OkBtn.visibility = View.VISIBLE
                                            }
                                        } else {
                                            binding.addProducts.discountedPrice.error =
                                                "Enter Price"
                                            binding.progressBar.visibility = View.GONE
                                            binding.addProducts.discountedPrice.setText("")
                                            binding.addProducts.OkBtn.visibility = View.VISIBLE
                                        }
                                    } else {
                                        binding.addProducts.OriginalPrice.error =
                                            "Enter Original Price"
                                        binding.progressBar.visibility = View.GONE
                                        binding.addProducts.OriginalPrice.setText("")
                                        binding.addProducts.OkBtn.visibility = View.VISIBLE
                                    }
                                } else {
                                    binding.addProducts.subCategory.error = "Select Sub Category"
                                    binding.progressBar.visibility = View.GONE
                                    binding.addProducts.subCategory.setText("")
                                    binding.addProducts.OkBtn.visibility = View.VISIBLE
                                }
                            } else {
                                binding.addProducts.selectCategory.error = "Select Category"
                                binding.progressBar.visibility = View.GONE
                                binding.addProducts.selectCategory.setText("")
                                binding.addProducts.OkBtn.visibility = View.VISIBLE
                            }
                        } else {
                            binding.addProducts.brandName.error = "Enter Brand Name"
                            binding.progressBar.visibility = View.GONE
                            binding.addProducts.brandName.setText("")
                            binding.addProducts.OkBtn.visibility = View.VISIBLE
                        }
                    } else {
                        binding.productDescription.error = "Enter Description"
                        binding.progressBar.visibility = View.GONE
                        binding.productDescription.setText("")
                        binding.addProducts.OkBtn.visibility = View.VISIBLE
                    }
                } else {
                    binding.productTitle.error = "Enter Product Title"
                    binding.progressBar.visibility = View.GONE
                    binding.productTitle.setText("")
                    binding.addProducts.OkBtn.visibility = View.VISIBLE
                }
            } else {
                Toast.makeText(this, "Upload your Product Image", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
                binding.addProducts.OkBtn.visibility = View.VISIBLE
            }

        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            var bitmapImage: Bitmap =
                MediaStore.Images.Media.getBitmap(this.getContentResolver(), data?.data!!)
            updateProductImage = true
            var byteArrOutputStream = ByteArrayOutputStream()
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 50, byteArrOutputStream)
            var bytesArray = byteArrOutputStream.toByteArray()
            compressedImage = BitmapFactory.decodeByteArray(bytesArray, 0, bytesArray.size)

            productImagePath = MediaStore.Images.Media.insertImage(
                this.contentResolver,
                compressedImage,
                "erg",
                "reg"
            ).toUri()
            Glide.with(this).load(productImagePath).into(binding.productImage)
        }
    }
}