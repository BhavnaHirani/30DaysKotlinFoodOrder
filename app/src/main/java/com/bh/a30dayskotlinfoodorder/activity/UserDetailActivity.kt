package com.bh.a30dayskotlinfoodorder.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.databinding.DataBindingUtil
import com.bh.a30dayskotlinfoodorder.R
import com.bh.a30dayskotlinfoodorder.databinding.ActivityUserDetailBinding
import com.bh.a30dayskotlinfoodorder.model.CartModel
import com.bh.a30dayskotlinfoodorder.utils.InternetConnection
import com.bh.a30dayskotlinfoodorder.utils.MyApplication
import com.bh.a30dayskotlinfoodorder.utils.PreferenceHelper
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder
import java.sql.Timestamp
import kotlin.collections.HashMap

class UserDetailActivity : BaseActivity() {

    private lateinit var binding: ActivityUserDetailBinding
    override var mContext: Context? = null
    private val db = Firebase.firestore
    private val _msg = "UserDetailActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_detail)
        mContext = this

        setActionBarLayout()
        if (!InternetConnection.isInternetAvailable(mContext)) {
            MyApplication.instance.showToast(getString(R.string.error_internet))
        }else{
            loadData()
        }

        initClickListeners()
    }

    private fun setActionBarLayout() {
        title=resources.getString(R.string.address)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun loadData() {

        if (!TextUtils.isEmpty(PreferenceHelper.getString(PreferenceHelper.CUSTOMER_ID, ""))){

            binding.edtNane.isEnabled = false
            binding.edtNumber.isEnabled = false
            binding.edtAddress.isEnabled = false
            binding.edtPinCode.isEnabled = true
            binding.edtEmail.isEnabled = true
            binding.btnSave.text = mContext!!.resources.getString(R.string.Continue)

            binding.progressBar.visibility= View.VISIBLE

            val docRef = db.collection("customers").document(PreferenceHelper.getString(PreferenceHelper.CUSTOMER_ID, ""))

            docRef.get().addOnCompleteListener { task ->
                binding.progressBar.visibility=View.GONE

                if (task.isSuccessful) {
                    val document = task.result

                    if (document != null) {
                        Log.d(_msg, "DocumentSnapshot data: " + task.result)
                        if(!TextUtils.isEmpty(document.getString("name"))){
                            binding.edtNane.text = Editable.Factory.getInstance().newEditable(document.getString("name"))
                        }
                        if(!TextUtils.isEmpty(document.getString("phone"))){
                            binding.edtNumber.text = Editable.Factory.getInstance().newEditable(document.getString("phone"))
                        }

                        if(!TextUtils.isEmpty(document.getString("address"))){
                            binding.edtAddress.text = Editable.Factory.getInstance().newEditable(document.getString("address"))
                        }

                        if(!TextUtils.isEmpty(document.getString("pincode"))){
                            binding.edtPinCode.text = Editable.Factory.getInstance().newEditable(document.getString("pincode"))
                        }

                        if(!TextUtils.isEmpty(document.getString("email"))){
                            binding.edtEmail.text = Editable.Factory.getInstance().newEditable(document.getString("email"))
                        }
                    } else {
                        Log.d(_msg, "No such document")
                    }
                } else {
                    Log.d(_msg, "get failed with ", task.exception)
                }
            }
            //Fetch data from db
        }else{
            binding.edtNane.isEnabled = true
            binding.edtNumber.isEnabled = true
            binding.edtAddress.isEnabled = true
            binding.edtPinCode.isEnabled = true
            binding.edtEmail.isEnabled = true

            binding.btnSave.text = mContext!!.resources.getString(R.string.Save)
        }
    }

    private fun initClickListeners() {

        binding.btnSave.setOnClickListener {

            if(TextUtils.isEmpty(binding.edtNane.text.toString())){
                MyApplication.instance.showError(binding.linRoot, "Please enter Name")
            } else if(TextUtils.isEmpty(binding.edtNumber.text.trim().toString())){
                MyApplication.instance.showError(binding.linRoot, "Please enter Phone Number")
            } else if(binding.edtNumber.text.trim().toString().length < 10){
                MyApplication.instance.showError(binding.linRoot, "Mobile Number must be 10 digit")
            } else if(TextUtils.isEmpty(binding.edtAddress.text.toString())){
                MyApplication.instance.showError(binding.linRoot, "Please enter Address")
            } else{
                binding.progressBar.visibility=View.VISIBLE

                val hashMap:HashMap<String,String> = HashMap()
                hashMap["address"] = binding.edtAddress.text.toString()
                hashMap["name"] = binding.edtNane.text.toString()
                hashMap["phone"] = binding.edtNumber.text.toString()
                hashMap["pincode"] = binding.edtPinCode.text.toString()
                hashMap["email"] = binding.edtEmail.text.toString()

                if (!TextUtils.isEmpty(PreferenceHelper.getString(PreferenceHelper.CUSTOMER_ID, ""))){

                    db.collection("customers")
                        .document(PreferenceHelper.getString(PreferenceHelper.CUSTOMER_ID, ""))
                        .update(hashMap as Map<String, Any>)

                        .addOnSuccessListener {

                            binding.progressBar.visibility=View.GONE
                            placeOrder()

                        }.addOnFailureListener { e -> Log.w(_msg, "Error adding Order", e)
                        }

                } else{
                    db.collection("customers")
                        .add(hashMap)
                        .addOnSuccessListener { documentReference ->

                            Log.d(_msg, "DocumentSnapshot written with ID: " + documentReference.id)
                            PreferenceHelper.putValue(PreferenceHelper.CUSTOMER_ID, documentReference.id)

                            createStatus()
                        }
                        .addOnFailureListener { e -> Log.w(_msg, "Error adding document", e)
                        }
                }
            }
        }
    }

    private fun createStatus(){

        val hashMap:HashMap<String,HashMap<String,Boolean>> = HashMap()
        val innerHashMap: HashMap<String, Boolean> = HashMap()

        innerHashMap["accepted"] = false
        innerHashMap["delivered"] = false
        innerHashMap["delivering"] = false
        innerHashMap["placed"] = true
        innerHashMap["recived"] = false
        hashMap["order_status"] = innerHashMap

        db.collection("status")
            .add(hashMap)
            .addOnSuccessListener { documentReference ->
                Log.d(_msg, "DocumentSnapshot written with status ID: " + documentReference.id)
                binding.progressBar.visibility=View.GONE
                PreferenceHelper.putValue(PreferenceHelper.STATUS_ID, documentReference?.id)

                placeOrder()
            }
            .addOnFailureListener { e -> Log.w(_msg, "Error adding document", e)
            }
    }

    private fun placeOrder(){

        var orderString = ""
        var menuCategoryList = mutableListOf<CartModel>()

        if(!TextUtils.isEmpty(PreferenceHelper.getString(PreferenceHelper.CART_MODEL, ""))){
            val gson = GsonBuilder().create()
            orderString = PreferenceHelper.getString(PreferenceHelper.CART_MODEL, "")

            menuCategoryList = gson.fromJson(PreferenceHelper.getString(PreferenceHelper.CART_MODEL, ""),Array<CartModel>::class.java).toList() as MutableList<CartModel>
        }

        var price = 0
        for(cart in menuCategoryList){
            price += cart.menuPrice!!.toInt() * cart.qty
        }

        val customerDocumentRef = db.collection("customers").document(PreferenceHelper.getString(PreferenceHelper.CUSTOMER_ID, ""))
        val statusDocumentRef = db.collection("status").document(PreferenceHelper.getString(PreferenceHelper.STATUS_ID, ""))

        val hashMap = mutableMapOf("customer" to customerDocumentRef, "date" to Timestamp(System.currentTimeMillis()),
            "order" to orderString, "order_number" to System.currentTimeMillis(), "order_status" to statusDocumentRef,
            "total_amount" to price)

        db.collection("orders")
            .add(hashMap)
            .addOnSuccessListener { documentReference ->
                Log.d(_msg, "DocumentSnapshot written with ORDER ID: " + documentReference.id)
                binding.progressBar.visibility=View.GONE
                MyApplication.instance.showError(binding.linRoot, "Item is placed successfully")
                PreferenceHelper.putValue(PreferenceHelper.CART_MODEL, "")

                Handler().postDelayed({
                    /* Create an Intent that will start the Menu-Activity. */
                    val intent= Intent()
                    setResult(Activity.RESULT_OK ,intent)
                    finish()
                }, 500)
            }

            .addOnFailureListener { e -> Log.w(_msg, "Error adding document", e) }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }

    override fun onBackPressed() {
        finish()
    }
}
