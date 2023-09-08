package com.example.knockitbranchapp.Activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.example.knockitbranchapp.Database.CategoryDatabase
import com.example.knockitbranchapp.Fragment.CategoryFragment
import com.example.knockitbranchapp.Fragment.HomeFragment
import com.example.knockitbranchapp.Fragment.MyOderFragment
import com.example.knockitbranchapp.Fragment.ProfileFragment
import com.example.knockitbranchapp.Fragment.WalletFragment
import com.example.knockitbranchapp.Model.BranchModel
import com.example.knockitbranchapp.R
import com.example.knockitbranchapp.Service.MyServices
import com.example.knockitbranchapp.databinding.ActivityDashbordBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore

class DashboardActivity : AppCompatActivity() {

    val HOME_FRAGMENT = 0
    val CATEGORY_FRAGMENT = 1
    val MY_ODER_FRAGMENT = 2
    val WALLET_FRAGMENT = 3
    val PROFILE_FRAGMENT = 4
    var CurrentFragment = -1

    lateinit var binding: ActivityDashbordBinding
    lateinit var reviewDialog: Dialog
    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashbordBinding.inflate(layoutInflater)
        val view: View = binding.getRoot()
        setContentView(view)
        setFragment(HomeFragment(), HOME_FRAGMENT)
        window.setStatusBarColor(ContextCompat.getColor(this@DashboardActivity,R.color.primary));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)

        ////////////////loading dialog
        reviewDialog = Dialog(this)
        reviewDialog.setContentView(R.layout.dialog_under_review)
        reviewDialog.setCancelable(false)
        reviewDialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        var okBtn: AppCompatButton = reviewDialog.findViewById(R.id.okBtn)!!
        okBtn.setOnClickListener {
            finish()
        }
        ////////////////loading dialog

        FirebaseFirestore.getInstance().collection("BRANCHES")
            .document(FirebaseAuth.getInstance().uid.toString())
            .get()
            .addOnSuccessListener(OnSuccessListener<DocumentSnapshot> { documentSnapshot ->
                val model: BranchModel? = documentSnapshot.toObject(BranchModel::class.java)

                if (model?.storeVerification.equals("Pending")){
                    reviewDialog.show()
                }else{
                    reviewDialog.dismiss()
                }
            })

        binding.bottomNavigationView!!.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home -> {
                    setCheckedChancel()
                    invalidateOptionsMenu()
                    setFragment(HomeFragment(), HOME_FRAGMENT)
                    window.setStatusBarColor(ContextCompat.getColor(this@DashboardActivity,R.color.primary));
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
                }

                R.id.category -> {
                    setCheckedChancel()
                    invalidateOptionsMenu()
                    setFragment(CategoryFragment(), CATEGORY_FRAGMENT)
                    item.isChecked = true
                    window.setStatusBarColor(ContextCompat.getColor(this@DashboardActivity,R.color.white));
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                }

                R.id.my_oder -> {
                    setCheckedChancel()
                    invalidateOptionsMenu()
                    startActivity(Intent(this, DeliveryActivity::class.java))
                    item.isChecked = true
                    window.setStatusBarColor(ContextCompat.getColor(this@DashboardActivity,R.color.primary));
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
                }

                R.id.wallet -> {
                    setCheckedChancel()
                    invalidateOptionsMenu()
                    setFragment(WalletFragment(), WALLET_FRAGMENT)
                    item.isChecked = true
                    window.setStatusBarColor(ContextCompat.getColor(this@DashboardActivity,R.color.white));
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                }

                R.id.profile -> {
                    setCheckedChancel()
                    invalidateOptionsMenu()
                    setFragment(ProfileFragment(), PROFILE_FRAGMENT)
                    item.isChecked = true
                    window.setStatusBarColor(ContextCompat.getColor(this@DashboardActivity,R.color.white));
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                }
            }
            true
        }
    }

    private fun setFragment(fragment: Fragment, fragmentNo: Int) {
        if (fragmentNo != CurrentFragment) {
            CurrentFragment = fragmentNo
            val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(
                R.anim.slide_from_right,
                R.anim.slideout_from_left
            )
            fragmentTransaction.replace(binding.frameLayout!!.id, fragment)
            fragmentTransaction.commit()
        }
    }

    private fun setCheckedChancel() {
        binding.bottomNavigationView.getMenu().getItem(0).setChecked(false)
        binding.bottomNavigationView.getMenu().getItem(1).setChecked(false)
        binding.bottomNavigationView.getMenu().getItem(2).setChecked(false)
        binding.bottomNavigationView.getMenu().getItem(3).setChecked(false)
        binding.bottomNavigationView.getMenu().getItem(4).setChecked(false)
    }
}