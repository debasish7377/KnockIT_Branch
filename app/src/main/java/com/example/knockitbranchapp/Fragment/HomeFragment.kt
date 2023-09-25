package com.example.knockitbranchapp.Fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.knockitbranchapp.Activity.AddProductActivity
import com.example.knockitbranchapp.Adapter.RiderAdapter
import com.example.knockitbranchapp.Database.ProductDatabase
import com.example.knockitbranchapp.Model.BranchModel
import com.example.knockitbranchapp.R
import com.example.knockitbranchapp.databinding.FragmentHomeBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false)

        FirebaseFirestore.getInstance().collection("BRANCHES")
            .document(FirebaseAuth.getInstance().uid.toString())
            .get()
            .addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
                val model: BranchModel? = documentSnapshot.toObject(BranchModel::class.java)

                try {
                    if (model?.profile.equals("")) {
                        Glide.with(context!!).load(R.drawable.avatara).into(binding.profileImage)
                    } else {
                        Glide.with(context!!).load(model?.profile.toString())
                            .into(binding.profileImage)
                    }
                }catch (e: Exception){
                    e.printStackTrace()
                }
                binding.name.text = model?.name
                binding.email.text = model?.email
                binding.totalEarning.text = "₹"+model?.totalEarning

            })

        ProductDatabase.loadProduct(context!!, binding.productRecyclerView)

        binding.addProductBtn.setOnClickListener {
            startActivity(Intent(context, AddProductActivity::class.java))
        }

        FirebaseFirestore.getInstance()
            .collection("BRANCHES")
            .document(FirebaseAuth.getInstance().uid.toString())
            .addSnapshotListener { value, error ->
                var connectWithRider = value?.getString("connectWithRider").toString()

                if (!connectWithRider.toString().equals("")) {
                    binding.storeConnectionBg.visibility = View.VISIBLE
                    binding.storeConnection.cancelBtn.visibility = View.GONE
                    binding.storeConnection.OkBtn.text = "Rider Connected"
                    FirebaseFirestore.getInstance()
                        .collection("RIDERS")
                        .document(connectWithRider)
                        .addSnapshotListener { value, error ->
                            var storeOwnerName = value?.getString("name").toString()
                            var storeOwnerProfile = value?.getString("profile").toString()
                            var number = value?.getString("number").toString()
                            binding.storeConnectionBg.visibility = View.VISIBLE
                            binding.storeConnection.storeName.text = number
                            binding.storeConnection.storeOwnerName.text = storeOwnerName
                            binding.storeConnection.storeNumber.text = number
                            binding.storeConnection.storeNumber.visibility = View.INVISIBLE
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