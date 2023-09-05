package com.example.knockitbranchapp.Activity

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import com.example.knockitbranchapp.Database.ProductDatabase
import com.example.knockitbranchapp.R
import com.example.knockitbranchapp.databinding.ActivityAddProductImagesBinding
import com.example.knockitbranchapp.databinding.ActivityAddSpecificationBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class AddSpecificationActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddSpecificationBinding
    lateinit var loadingDialog: Dialog
    lateinit var specificationDialog: Dialog
    lateinit var brand: EditText
    lateinit var value: EditText
    lateinit var ok_btn: AppCompatButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddSpecificationBinding.inflate(layoutInflater)
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

        ////////////////quantity dialog
        specificationDialog = Dialog(this)
        specificationDialog.setContentView(R.layout.dialog_add_specification)
        specificationDialog.setCancelable(false)
        specificationDialog.window?.setBackgroundDrawable(getDrawable(R.drawable.login_btn_bg))
        specificationDialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        brand = specificationDialog.findViewById(R.id.brand)
        value = specificationDialog.findViewById(R.id.value)
        ok_btn = specificationDialog.findViewById(R.id.Ok_btn)
        ////////////////quantity dialog

        ProductDatabase.loadSpecification(this, productId!!, binding.specificationRecyclerView)

        binding.addSpecification.setOnClickListener {
            specificationDialog.show()
        }

        ok_btn.setOnClickListener {
            if (!brand.text.toString().equals("")) {
                if (!value.text.toString().equals("")) {
                    specificationDialog.dismiss()
                    loadingDialog.show()

                    val randomString = UUID.randomUUID().toString().substring(0, 15)
                    val userData: MutableMap<Any, Any?> =
                        HashMap()
                    userData["id"] = randomString
                    userData["brand"] = brand.text.toString()
                    userData["value"] = value.text.toString()
                    userData["timeStamp"] = System.currentTimeMillis()
                    userData["productId"] = productId

                    FirebaseFirestore.getInstance()
                        .collection("PRODUCTS")
                        .document(productId)
                        .collection("productSpecification")
                        .document(randomString)
                        .set(userData)
                        .addOnCompleteListener {
                            loadingDialog.dismiss()
                            binding.publishBtn.visibility = View.VISIBLE
                            value.setText("")
                            brand.setText("")
                        }

                }else{
                    value.setText("")
                    value.error = "Enter value"
                }
            }else{
                brand.setText("")
                brand.error = "Enter Brand"
            }

        }

        binding.publishBtn.setOnClickListener {
            loadingDialog.show()
            val userData: MutableMap<String, Any?> =
                HashMap()
            userData["productVerification"] = "Public"

            FirebaseFirestore.getInstance()
                .collection("PRODUCTS")
                .document(productId)
                .update(userData)
                .addOnCompleteListener {
                    loadingDialog.dismiss()
                    startActivity(Intent(this, DashboardActivity::class.java))
                    finish()
                }
        }
    }
}