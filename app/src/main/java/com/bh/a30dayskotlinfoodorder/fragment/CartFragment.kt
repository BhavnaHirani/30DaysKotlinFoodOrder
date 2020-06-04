package com.bh.a30dayskotlinfoodorder.fragment

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
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
import com.bh.a30dayskotlinfoodorder.activity.MainActivity
import com.bh.a30dayskotlinfoodorder.adapter.CartAdapter
import com.bh.a30dayskotlinfoodorder.databinding.FragmentCartBinding
import com.bh.a30dayskotlinfoodorder.listener.OnRecyclerItemClick
import com.bh.a30dayskotlinfoodorder.model.CartModel
import com.bh.a30dayskotlinfoodorder.utils.MyApplication
import com.bh.a30dayskotlinfoodorder.utils.PreferenceHelper
import com.google.gson.GsonBuilder

class CartFragment : Fragment() , OnRecyclerItemClick {
    private lateinit var binding: FragmentCartBinding
    private lateinit var mContext: Context

    private var menuCategoryList = mutableListOf<CartModel>()
    private var menuCategoryAdapter: CartAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cart, container, false)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        mContext = activity as Context

        loadData()
        initClickListeners()

        return binding.root
    }

    private fun initClickListeners() {
        binding.edtSearchView.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(newText: Editable) {}

            override fun beforeTextChanged(newText: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(newText: CharSequence, start: Int, before: Int, count: Int) {

                filter(newText.toString())
            }
        })

        binding.btnOrder.setOnClickListener {

            if(menuCategoryList.size != 0){
                (activity as MainActivity).mainBinding.bottomNavigation.selectedItemId = R.id.navigation_orders
            } else{
                MyApplication.instance.showError(binding.linRoot, "Please Add item into cart")
            }
        }

        binding.btnCancel.setOnClickListener {
            PreferenceHelper.putValue(PreferenceHelper.CART_MODEL, "")
            loadData()
        }
    }

    fun filter(text: String) {

        val temp = ArrayList<CartModel>()

        for (item in menuCategoryList) {

            if (item.menuName?.contains(text, ignoreCase = true)!! ) {

                temp.add(item)
            }
        }
        menuCategoryAdapter?.setData(temp)
        menuCategoryAdapter?.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.title = resources.getString(R.string.menu_cart)
        loadData()
    }

    private fun loadData() {
        if(!TextUtils.isEmpty(PreferenceHelper.getString(PreferenceHelper.CART_MODEL, ""))){
            val gson = GsonBuilder().create()
            val list: MutableList<CartModel>
            list   = gson.fromJson(PreferenceHelper.getString(PreferenceHelper.CART_MODEL, ""),Array<CartModel>::class.java).toList() as MutableList<CartModel>
            menuCategoryList.clear()
            menuCategoryList.addAll(list)
        } else{
            menuCategoryList = ArrayList()
        }

        if(menuCategoryList.size > 0){
            binding.rlNoData.visibility=View.GONE
            binding.btnOrder.visibility=View.VISIBLE
            binding.btnCancel.visibility=View.VISIBLE
            binding.linTotalQty.visibility = View.VISIBLE
            binding.linTotalPrice.visibility = View.VISIBLE
        } else{
            binding.rlNoData.visibility=View.VISIBLE
            binding.btnOrder.visibility=View.GONE
            binding.btnCancel.visibility=View.GONE
            binding.linTotalQty.visibility = View.GONE
            binding.linTotalPrice.visibility = View.GONE
        }

        binding.rvMenuItemList.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        menuCategoryAdapter = CartAdapter(this, {pos ->
            menuCategoryList[pos].qty =  menuCategoryList[pos].qty + 1
            val total : Double = (menuCategoryList[pos].qty * menuCategoryList[pos].menuPrice!!.toInt()).toDouble()
            menuCategoryList[pos].total = total.toString()
            getCartData()
            loadData()
        },
            { pos ->
                if(menuCategoryList[pos].qty >1){
                    menuCategoryList[pos].qty =  menuCategoryList[pos].qty - 1
                    val total : Double = (menuCategoryList[pos].qty * menuCategoryList[pos].menuPrice!!.toInt()).toDouble()
                    menuCategoryList[pos].total = total.toString()
                } else{
                    menuCategoryList.remove(menuCategoryList[pos])
                }

                getCartData()
                loadData()
            })
        binding.rvMenuItemList.itemAnimator = DefaultItemAnimator()
        menuCategoryAdapter?.setData(menuCategoryList)
        binding.rvMenuItemList.setHasFixedSize(false)
        binding.rvMenuItemList.isNestedScrollingEnabled = true
        binding.rvMenuItemList.adapter = menuCategoryAdapter

        var totalQTY = 0
        var price = 0

        for(cart in menuCategoryList){
            totalQTY += cart.qty
            price += cart.menuPrice!!.toInt() * cart.qty
        }

        binding.txtToatlItem.text = totalQTY.toString()
        binding.txtTotalPrice.text = price.toString()
    }

    override fun onItemClick(view: View, position: Int, type: String) {
        loadData()
    }

    private fun getCartData(){
        if(menuCategoryList.size > 0){
            val gson = GsonBuilder().create()
            val cartString : String = gson.toJson(menuCategoryList)
            PreferenceHelper.putValue(PreferenceHelper.CART_MODEL, cartString)
        } else{
            PreferenceHelper.putValue(PreferenceHelper.CART_MODEL, "")
        }
    }
}
