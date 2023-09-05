package com.example.knockitbranchapp.Activity

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.example.knockitbranchapp.Database.ProductDatabase
import com.example.knockitbranchapp.R
import com.example.knockitbranchapp.databinding.ActivityAddProductImagesBinding
import com.example.knockitbranchapp.databinding.ActivityAddProductQuantityBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class AddProductQuantityActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddProductQuantityBinding
    lateinit var addQuantityDialog: Dialog
    lateinit var loadingDialog: Dialog
    lateinit var discountedPrice: EditText
    lateinit var originalPrice: EditText
    lateinit var qty: EditText
    lateinit var avl_qty: EditText
    lateinit var ok_btn: AppCompatButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductQuantityBinding.inflate(layoutInflater)
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
        addQuantityDialog = Dialog(this)
        addQuantityDialog.setContentView(R.layout.dialog_add_quantity)
        addQuantityDialog.setCancelable(false)
        addQuantityDialog.window?.setBackgroundDrawable(getDrawable(R.drawable.login_btn_bg))
        addQuantityDialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        discountedPrice = addQuantityDialog.findViewById(R.id.discountedPrice)
        originalPrice = addQuantityDialog.findViewById(R.id.OriginalPrice)
        qty = addQuantityDialog.findViewById(R.id.qty)
        avl_qty = addQuantityDialog.findViewById(R.id.avl_qty)
        ok_btn = addQuantityDialog.findViewById(R.id.Ok_btn)
        ////////////////quantity dialog

        ProductDatabase.loadSelectSize(this, productId!!, binding.addQuantityRecyclerView)

        binding.addQty.setOnClickListener {
            addQuantityDialog.show()
        }

        ok_btn.setOnClickListener {
            if (!discountedPrice.text.toString().equals("")){
                if (!originalPrice.text.toString().equals("")){
                    if (!qty.text.toString().equals("")){
                        if (!avl_qty.text.toString().equals("")){
                            addQuantityDialog.dismiss()
                            loadingDialog.show()

                            val randomString = UUID.randomUUID().toString().substring(0, 15)
                            val userData: MutableMap<Any, Any?> =
                                HashMap()
                            userData["id"] = randomString
                            userData["productId"] = productId
                            userData["price"] = discountedPrice.text.toString()
                            userData["cuttedPrice"] = originalPrice.text.toString()
                            userData["availableQty"] = avl_qty.text.toString().toLong()
                            userData["qty"] = qty.text.toString()
                            userData["timeStamp"] = System.currentTimeMillis()

                            FirebaseFirestore.getInstance()
                                .collection("PRODUCTS")
                                .document(productId)
                                .collection("productSize")
                                .document(randomString)
                                .set(userData)
                                .addOnCompleteListener {
                                    loadingDialog.dismiss()
                                    binding.next.visibility = View.VISIBLE
                                    avl_qty.setText("")
                                    qty.setText("")
                                    originalPrice.setText("")
                                    discountedPrice.setText("")
                                }

                        }else{
                            avl_qty.setText("")
                            avl_qty.error = "Enter Available Quantity"
                        }
                    }else{
                        qty.setText("")
                        qty.error = "Enter Qty"
                    }
                }else{
                    originalPrice.setText("")
                    originalPrice.error = "Enter Original Price"
                }
            }else{
                discountedPrice.setText("")
                discountedPrice.error = "Enter Discounted Price"
            }

        }

        binding.next.setOnClickListener {
            var intent = Intent(this, AddSpecificationActivity::class.java)
            intent.putExtra("productId", productId)
            startActivity(intent)
        }
    }
}