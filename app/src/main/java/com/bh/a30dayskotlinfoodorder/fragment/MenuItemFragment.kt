package com.bh.a30dayskotlinfoodorder.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bh.a30dayskotlinfoodorder.R
import com.bh.a30dayskotlinfoodorder.adapter.MenuCategoryAdapter
import com.bh.a30dayskotlinfoodorder.databinding.FragmentMenuItemBinding
import com.bh.a30dayskotlinfoodorder.listener.OnRecyclerItemClick
import com.bh.a30dayskotlinfoodorder.model.CartModel
import com.bh.a30dayskotlinfoodorder.utils.Constant
import com.bh.a30dayskotlinfoodorder.utils.InternetConnection
import com.bh.a30dayskotlinfoodorder.utils.MyApplication
import com.bh.a30dayskotlinfoodorder.utils.PreferenceHelper
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder
import java.util.*
import kotlin.collections.ArrayList

class MenuItemFragment : Fragment() , OnRecyclerItemClick {

    private var categoryId: String? = null
    private var categoryName: String? = null
    private lateinit var binding: FragmentMenuItemBinding
    private var mContext: Context? = null

    private val menuCategoryList = mutableListOf<CartModel>()
    private var cartList  = mutableListOf<CartModel>()
    private var menuCategoryAdapter: MenuCategoryAdapter? = null
    private val db = Firebase.firestore
    private val _msg = "MenuItemFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_menu_item, container, false)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        mContext = activity

        getIntentData()
        setActionBarLayout()
        if (!InternetConnection.isInternetAvailable(mContext)) {
            MyApplication.instance.showToast(getString(R.string.error_internet))
        }else{
            loadData()
        }
        initClickListeners()

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.title = categoryName + " - " + resources.getString(R.string.title_Menu)
    }

    private fun setActionBarLayout() {
        Objects.requireNonNull((activity as AppCompatActivity).supportActionBar)!!.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.title = categoryName + " - " + resources.getString(R.string.title_Menu)
    }

    private fun loadData() {

        binding.progressBar.visibility= View.VISIBLE
        if(!TextUtils.isEmpty(PreferenceHelper.getString(PreferenceHelper.CART_MODEL, ""))){
            val gson = GsonBuilder().create()
            cartList= gson.fromJson(PreferenceHelper.getString(PreferenceHelper.CART_MODEL, ""),Array<CartModel>::class.java).toList() as MutableList<CartModel>
        }

        db.collection("menu_items")
            .get()
            .addOnCompleteListener { task ->

                binding.progressBar.visibility= View.GONE

                if (task.isSuccessful) {

                    menuCategoryList.clear()

                    for (document in task.result!!) {

                        val menuCategory = document.toObject(CartModel::class.java)

                        menuCategory.menuId = document.id
                        val documentReference = document.get("category") as DocumentReference
                        documentReference.path
                        menuCategory.categoryId = documentReference.id
                        menuCategory.menuName = document.get("name").toString()
                        menuCategory.menuPrice = document.get("price").toString()
                        menuCategory.categoryName = categoryName

                        if(menuCategory.categoryId.equals(categoryId)){
                            menuCategoryList.add(menuCategory)
                        }
                        MyApplication.instance.log(_msg, document.id + " => " + document.data)
                    }

                    compareList()
                    binding.rvMenuItemList.layoutManager = LinearLayoutManager(mContext, RecyclerView.VERTICAL, false)
                    menuCategoryAdapter = MenuCategoryAdapter(this)
                    binding.rvMenuItemList.itemAnimator = DefaultItemAnimator()
                    menuCategoryAdapter?.setData(menuCategoryList, cartList)
                    binding.rvMenuItemList.setHasFixedSize(false)
                    binding.rvMenuItemList.isNestedScrollingEnabled = true
                    binding.rvMenuItemList.adapter = menuCategoryAdapter
                } else {
                    Log.d(_msg, "Error getting documents: ", task.exception)
                }
            }
    }

    private fun compareList(){
        for(i in menuCategoryList.indices){
            for(j in cartList.indices){
                if(menuCategoryList[i].menuId.equals(cartList[j].menuId)){
                    menuCategoryList[i].qty = cartList[j].qty
                }
            }
        }
    }

    private fun getIntentData() {
        if (arguments != null) {
            if (!TextUtils.isEmpty(arguments?.getString(Constant.CATEGORY_ID))){
                categoryId = arguments?.getString(Constant.CATEGORY_ID)
                MyApplication.instance.log("@category", " CATEGORY_ID => $categoryId")
            }

            if (!TextUtils.isEmpty(arguments?.getString(Constant.CATEGORY_NAME))){
                categoryName = arguments?.getString(Constant.CATEGORY_NAME)
                MyApplication.instance.log("@category", "CATEGORY_NAME => $categoryId")
            }
        }
    }

    private fun initClickListeners() {

        binding.edtSearchView.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(newText: Editable) {}

            override fun beforeTextChanged(newText: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(newText: CharSequence, start: Int, before: Int, count: Int) {

                filter(newText.toString())
            }
        })
    }

    fun filter(text: String) {
        val temp = ArrayList<CartModel>()

        for (item in menuCategoryList) {

            if (item.menuName?.contains(text, ignoreCase = true)!! ) {

                temp.add(item)
            }
        }
        menuCategoryAdapter?.setData(temp, cartList)
        menuCategoryAdapter?.notifyDataSetChanged()
    }
}
