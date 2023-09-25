package com.example.knockitbranchapp.Fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.knockitbranchapp.Database.RiderDatabase
import com.example.knockitbranchapp.R
import com.example.knockitbranchapp.databinding.FragmentOtpBinding
import com.example.knockitbranchapp.databinding.FragmentRiderBinding

class RiderFragment : Fragment() {

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentRiderBinding = FragmentRiderBinding.inflate(inflater, container, false)

        RiderDatabase.loadRider(context!!, binding.riderRecyclerView)
        return binding.root
    }
}