package com.example.knockitbranchapp.Activity

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.knockitbranchapp.Database.CategoryDatabase
import com.example.knockitbranchapp.Model.BranchModel
import com.example.knockitbranchapp.R
import com.example.knockitbranchapp.databinding.ActivityMainBinding
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


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var filePath: Uri
    lateinit var storeImagePath: Uri
    lateinit var compressedImage: Bitmap
    var updatePhoto = false
    var updateStoreImage = false

    companion object{
        lateinit var categoryDialog: Dialog
        lateinit var selectCategory: TextView
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view: View = binding.getRoot()
        setContentView(view)
        selectCategory = findViewById(R.id.selectCategory)

        ////////////////loading dialog
        categoryDialog = Dialog(this)
        categoryDialog.setContentView(R.layout.dialog_category)
        categoryDialog.setCancelable(false)
        categoryDialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        var categoryRecyclerView: RecyclerView = categoryDialog.findViewById(R.id.categoryRecyclerView)!!
        CategoryDatabase.loadSelectCategoryByCreateStore(this, categoryRecyclerView)
        ////////////////loading dialog

        FirebaseFirestore.getInstance().collection("BRANCHES")
            .document(FirebaseAuth.getInstance().uid.toString())
            .get()
            .addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
                val model: BranchModel? = documentSnapshot.toObject(BranchModel::class.java)

                binding.edName.text= Editable.Factory.getInstance().newEditable(model?.name.toString())
                binding.storeCreate.storeAddress.text= Editable.Factory.getInstance().newEditable(model?.address.toString())
                binding.email.text = model?.email.toString()
                binding.phone.text = model?.number.toString()
                binding.storeCreate.pincode.text = model?.pincode.toString()
                binding.storeCreate.city.text = model?.city.toString()
                binding.storeCreate.county.text = model?.country.toString()
            })

        binding.storeCreate.OkBtn.setOnClickListener {
            binding.storeCreate.OkBtn.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
            if (updatePhoto) {
                if (updateStoreImage) {
                    if (!binding.storeName.text.isEmpty()) {
                        if (!binding.edName.text.toString().isEmpty()) {
                            if (!binding.storeDescription.text.toString().isEmpty()) {
                                if (!binding.storeCreate.storeAddress.text.toString().isEmpty()) {
                                    if (!binding.storeCreate.selectCategory.text.toString()
                                            .isEmpty()
                                    ) {
                                        if (!binding.storeCreate.deliveryTiming.text.toString()
                                                .isEmpty()
                                        ) {


                                            var reference: StorageReference =
                                                FirebaseStorage.getInstance().getReference()
                                                    .child("profiles").child(
                                                        FirebaseAuth.getInstance().getUid()
                                                            .toString()
                                                    );
                                            reference.putFile(filePath).addOnCompleteListener {
                                                reference.downloadUrl.addOnSuccessListener { profileImage ->

                                                    var uploadStoreImage: StorageReference =
                                                        FirebaseStorage.getInstance().getReference()
                                                            .child("StoreImage")
                                                            .child(
                                                                FirebaseAuth.getInstance().getUid()
                                                                    .toString()
                                                            );
                                                    uploadStoreImage.putFile(storeImagePath)
                                                        .addOnCompleteListener {
                                                            uploadStoreImage.downloadUrl.addOnSuccessListener { storeImage ->

                                                                val userData: MutableMap<String, Any?> =
                                                                    HashMap()
                                                                userData["name"] =
                                                                    binding.edName.text.toString()
                                                                userData["profile"] = profileImage
                                                                userData["storeName"] =
                                                                    binding.storeName.text.toString()
                                                                userData["storeDescription"] =
                                                                    binding.storeDescription.text.toString()
                                                                userData["storeCategory"] =
                                                                    binding.storeCreate.selectCategory.text.toString()
                                                                userData["storeImage"] = storeImage
                                                                userData["storeVerification"] =
                                                                    "Pending"
                                                                userData["oderCompletionSize"] = "0"
                                                                userData["deliveryTiming"] =
                                                                    binding.storeCreate.deliveryTiming.text.toString()
                                                                userData["address"] =
                                                                    binding.storeCreate.storeAddress.text.toString()
                                                                userData["timeStamp"] =
                                                                    System.currentTimeMillis()

                                                                FirebaseFirestore.getInstance()
                                                                    .collection("BRANCHES")
                                                                    .document(FirebaseAuth.getInstance().uid.toString())
                                                                    .update(userData)
                                                                    .addOnCompleteListener {
                                                                        binding.progressBar.visibility =
                                                                            View.GONE
                                                                        Toast.makeText(
                                                                            this,
                                                                            "Your Profile Under Review",
                                                                            Toast.LENGTH_SHORT
                                                                        ).show()
                                                                        startActivity(
                                                                            Intent(
                                                                                this,
                                                                                DashboardActivity::class.java
                                                                            )
                                                                        )
                                                                        finish()
                                                                        binding.storeCreate.OkBtn.visibility =
                                                                            View.VISIBLE
                                                                    }

                                                            }
                                                        }

                                                }
                                            }


                                        } else {
                                            binding.storeCreate.deliveryTiming.error =
                                                "Enter Delivery Timing"
                                            binding.progressBar.visibility = View.GONE
                                            binding.storeCreate.deliveryTiming.setText("")
                                            binding.storeCreate.OkBtn.visibility = View.VISIBLE
                                        }
                                    } else {
                                        binding.storeCreate.selectCategory.error = "Select Category"
                                        binding.progressBar.visibility = View.GONE
                                        binding.storeCreate.selectCategory.setText("")
                                        binding.storeCreate.OkBtn.visibility = View.VISIBLE
                                    }
                                } else {
                                    binding.storeCreate.storeAddress.error = "Address"
                                    binding.progressBar.visibility = View.GONE
                                    binding.storeCreate.storeAddress.setText("")
                                    binding.storeCreate.OkBtn.visibility = View.VISIBLE
                                }
                            } else {
                                binding.storeDescription.error = "Enter Store Description"
                                binding.progressBar.visibility = View.GONE
                                binding.storeDescription.setText("")
                                binding.storeCreate.OkBtn.visibility = View.VISIBLE
                            }
                        } else {
                            binding.edName.error = "Enter Name"
                            binding.progressBar.visibility = View.GONE
                            binding.edName.setText("")
                            binding.storeCreate.OkBtn.visibility = View.VISIBLE
                        }
                    } else {
                        binding.storeName.error = "Enter Store Name"
                        binding.progressBar.visibility = View.GONE
                        binding.storeName.setText("")
                        binding.storeCreate.OkBtn.visibility = View.VISIBLE
                    }
                } else {
                    Toast.makeText(this, "Upload your Store Image", Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                    binding.storeCreate.OkBtn.visibility = View.VISIBLE
                }

            } else {
                Toast.makeText(this, "Upload your photo", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
                binding.storeCreate.OkBtn.visibility = View.VISIBLE
            }
        }

        binding.storeCreate.selectCategory.setOnClickListener {
            categoryDialog.show()
        }

        binding.profileImage.setOnClickListener {
            Dexter.withContext(this@MainActivity)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(permissionGrantedResponse: PermissionGrantedResponse) {
                        val intent = Intent(Intent.ACTION_PICK)
                        intent.type = "image/*"
                        startActivityForResult(
                            Intent.createChooser(intent, "Select Picture"),
                            1
                        )
                    }

                    override fun onPermissionDenied(permissionDeniedResponse: PermissionDeniedResponse) {}
                    override fun onPermissionRationaleShouldBeShown(
                        permissionRequest: PermissionRequest?,
                        permissionToken: PermissionToken
                    ) {
                        permissionToken.continuePermissionRequest()
                    }
                })
                .check()
        }

        binding.addStoreImage.setOnClickListener {
            Dexter.withContext(this@MainActivity)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(permissionGrantedResponse: PermissionGrantedResponse) {
                        val intent = Intent(Intent.ACTION_PICK)
                        intent.type = "image/*"
                        startActivityForResult(
                            Intent.createChooser(intent, "Select Picture"),
                            2
                        )
                    }

                    override fun onPermissionDenied(permissionDeniedResponse: PermissionDeniedResponse) {}
                    override fun onPermissionRationaleShouldBeShown(
                        permissionRequest: PermissionRequest?,
                        permissionToken: PermissionToken
                    ) {
                        permissionToken.continuePermissionRequest()
                    }
                })
                .check()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            //var bitmapImage: Bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data?.data!!)
            filePath = data?.data!!
            updatePhoto = true
//
//            var byteArrOutputStream = ByteArrayOutputStream()
//            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 50 , byteArrOutputStream)
//            var bytesArray: ByteArray = byteArrOutputStream.toByteArray()
//            compressedImage = BitmapFactory.decodeByteArray(bytesArray, 0 , bytesArray.size)
//
//            filePath = MediaStore.Images.Media.insertImage(this.contentResolver, compressedImage,"erg","reg").toUri()
            Glide.with(this).load(filePath).into(binding.profileImage)
        }

        if (requestCode == 2 && resultCode == RESULT_OK) {
            storeImagePath = data?.data!!
            var bitmapImage: Bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data?.data!!)
            updateStoreImage = true
            var byteArrOutputStream = ByteArrayOutputStream()
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 50 , byteArrOutputStream)
            var bytesArray = byteArrOutputStream.toByteArray()
            compressedImage = BitmapFactory.decodeByteArray(bytesArray, 0 , bytesArray.size)

            storeImagePath = MediaStore.Images.Media.insertImage(this.contentResolver, compressedImage,"erg","reg").toUri()
            Glide.with(this).load(storeImagePath).into(binding.storeImage)
        }
    }
}