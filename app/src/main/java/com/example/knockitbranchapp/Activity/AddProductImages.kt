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
import android.widget.Toast
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.knockitbranchapp.Database.ProductDatabase
import com.example.knockitbranchapp.R
import com.example.knockitbranchapp.databinding.ActivityAddProductActivtyBinding
import com.example.knockitbranchapp.databinding.ActivityAddProductImagesBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.util.Arrays
import java.util.UUID

class AddProductImages : AppCompatActivity() {

    lateinit var binding: ActivityAddProductImagesBinding
    var updateProductImage: Boolean = false
    lateinit var productImagePath: Uri
    lateinit var loadingDialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductImagesBinding.inflate(layoutInflater)
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

        ProductDatabase.loadImages(this, productId!!, binding.recyclerView )

        binding.browseImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(
                Intent.createChooser(intent, "Select Picture"),
                1
            )
        }

        binding.addImage.setOnClickListener {
            if (updateProductImage) {
                loadingDialog.show()

                var reference: StorageReference =
                    FirebaseStorage.getInstance().getReference()
                        .child("productImages").child(
                            System.currentTimeMillis()
                                .toString()
                        );
                reference.putFile(productImagePath)
                    .addOnCompleteListener {
                        reference.downloadUrl.addOnSuccessListener { productImage ->

                            val randomString = UUID.randomUUID().toString().substring(0, 15)
                            val userData: MutableMap<Any, Any?> =
                                HashMap()
                            userData["id"] = randomString
                            userData["image"] = productImage.toString()
                            userData["timeStamp"] = System.currentTimeMillis()
                            userData["productId"] = productId

                            FirebaseFirestore.getInstance()
                                .collection("PRODUCTS")
                                .document(productId)
                                .collection("productImages")
                                .document(randomString)
                                .set(userData)
                                .addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        loadingDialog.dismiss()
                                        binding.next.visibility = View.VISIBLE
                                    }
                                }
                        }
                    }

            }else{
                Toast.makeText(this, "Browse Image First", Toast.LENGTH_SHORT).show()
            }
        }

        binding.next.setOnClickListener {
            var intent = Intent(this, AddProductQuantityActivity::class.java)
            intent.putExtra("productId", productId)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == RESULT_OK) {
            var bitmapImage: Bitmap =
                MediaStore.Images.Media.getBitmap(this.getContentResolver(), data?.data!!)
            updateProductImage = true
            var byteArrOutputStream = ByteArrayOutputStream()
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 40, byteArrOutputStream)
            var bytesArray = byteArrOutputStream.toByteArray()
            var compressedImage = BitmapFactory.decodeByteArray(bytesArray, 0, bytesArray.size)

            try {
                productImagePath = MediaStore.Images.Media.insertImage(
                    this.contentResolver,
                    compressedImage,
                    "erg",
                    "reg"
                ).toUri()
                Glide.with(this).load(productImagePath).into(binding.productImage)
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }
}