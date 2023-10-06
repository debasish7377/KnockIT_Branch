package com.example.knockitbranchapp.Fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.example.knockitbranchapp.Activity.AddProductActivity
import com.example.knockitbranchapp.Adapter.RiderAdapter
import com.example.knockitbranchapp.Database.ProductDatabase
import com.example.knockitbranchapp.Model.BranchModel
import com.example.knockitbranchapp.R
import com.example.knockitbranchapp.databinding.FragmentHomeBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.io.IOException
import java.util.Locale

class HomeFragment : Fragment() {

    lateinit var loadingDialog: Dialog
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context!!)

        ////////////////loading dialog
        loadingDialog = Dialog(context!!)
        loadingDialog.setContentView(R.layout.dialog_loading)
        loadingDialog.setCancelable(false)
        loadingDialog.window?.setBackgroundDrawable(context!!.getDrawable(R.drawable.login_btn_bg))
        loadingDialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        ////////////////loading dialog

        binding.updateLocation.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Location")
            builder.setMessage("Are you sure to update your location ?")

            builder.setPositiveButton("Yes") { dialog, which ->

                loadingDialog.show()
                //////// Location Update
                if (ActivityCompat.checkSelfPermission(
                        context!!,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        context!!,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.

                }
                fusedLocationProviderClient.lastLocation
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            try {
                                val geocoder =
                                    Geocoder(context!!, Locale.getDefault())
                                val addresses = geocoder.getFromLocation(
                                    location.latitude,
                                    location.longitude,
                                    1
                                )

                                //////// Update Address
                                val userData: MutableMap<String, Any?> = HashMap()
                                userData["city"] = addresses!![0].locality
                                userData["country"] = addresses!![0].countryName
                                userData["state"] = addresses!![0].adminArea
                                userData["pincode"] = addresses!![0].postalCode
                                userData["address"] = addresses!![0].getAddressLine(0)
                                userData["latitude"] = addresses!![0].latitude
                                userData["longitude"] = addresses!![0].longitude
                                userData["timeStamp"] = System.currentTimeMillis()

                                var sharedPreferences: SharedPreferences =
                                    context!!.getSharedPreferences("Address", AppCompatActivity.MODE_PRIVATE)
                                val myEdit = sharedPreferences.edit()
                                myEdit.putString(
                                    "address",
                                    addresses!![0].getAddressLine(0)
                                )
                                myEdit?.commit()

                                FirebaseFirestore.getInstance()
                                    .collection("BRANCHES")
                                    .document(FirebaseAuth.getInstance().uid.toString())
                                    .update(userData)
                                    .addOnCompleteListener() { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(context, "Location Updated Successfully", Toast.LENGTH_SHORT).show()
                                            loadingDialog.dismiss()
                                        }
                                        //////// Update Address
                                    }

                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }
                    }
                //////// Location Update
            }

            builder.setNegativeButton("No") { dialog, which ->
            }

            builder.show()
        }

        FirebaseFirestore.getInstance().collection("BRANCHES")
            .document(FirebaseAuth.getInstance().uid.toString())
            .get()
            .addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
                val model: BranchModel? =
                    documentSnapshot.toObject(BranchModel::class.java)

                try {
                    if (model?.profile.equals("")) {
                        Glide.with(context!!).load(R.drawable.avatara)
                            .into(binding.profileImage)
                    } else {
                        Glide.with(context!!).load(model?.profile.toString())
                            .into(binding.profileImage)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                binding.name.text = model?.name
                binding.email.text = model?.email
                binding.totalEarning.text = "₹" + model?.totalEarning

            })

        ProductDatabase.loadProduct(context!!, binding.productRecyclerView)

        binding.addProductBtn.setOnClickListener {
            startActivity(Intent(context, AddProductActivity::class.java))
        }

        FirebaseFirestore.getInstance()
            .collection("BRANCHES")
            .document(FirebaseAuth.getInstance().uid.toString())
            .addSnapshotListener { value, error ->
                var connectWithRider =
                    value?.getString("connectWithRider").toString()

                if (!connectWithRider.toString().equals("")) {
                    binding.storeConnectionBg.visibility = View.VISIBLE
                    binding.storeConnection.cancelBtn.visibility = View.GONE
                    binding.storeConnection.OkBtn.text = "Rider Connected"
                    FirebaseFirestore.getInstance()
                        .collection("RIDERS")
                        .document(connectWithRider)
                        .addSnapshotListener { value, error ->
                            var storeOwnerName =
                                value?.getString("name").toString()
                            var storeOwnerProfile =
                                value?.getString("profile").toString()
                            var number = value?.getString("number").toString()
                            binding.storeConnectionBg.visibility = View.VISIBLE
                            binding.storeConnection.storeName.text = number
                            binding.storeConnection.storeOwnerName.text =
                                storeOwnerName
                            binding.storeConnection.storeNumber.text = number
                            binding.storeConnection.storeNumber.visibility =
                                View.INVISIBLE
                            try {
                                Glide.with(context!!).load(storeOwnerProfile)
                                    .placeholder(R.drawable.avatara)
                                    .into(binding.storeConnection.storeOwnerProfile)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                } else {
                    binding.storeConnectionBg.visibility = View.GONE
                }
            }

        return binding.root
    }
}