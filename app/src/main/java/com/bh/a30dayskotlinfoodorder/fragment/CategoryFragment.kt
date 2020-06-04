package com.bh.a30dayskotlinfoodorder.fragment

import android.os.Bundle
import android.text.Editable
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
import com.bh.a30dayskotlinfoodorder.adapter.CategoryAdapter
import com.bh.a30dayskotlinfoodorder.databinding.FragmentCategoryBinding
import com.bh.a30dayskotlinfoodorder.listener.OnRecyclerItemClick
import com.bh.a30dayskotlinfoodorder.model.CategoryModel
import com.bh.a30dayskotlinfoodorder.utils.Constant
import com.bh.a30dayskotlinfoodorder.utils.InternetConnection
import com.bh.a30dayskotlinfoodorder.utils.MyApplication
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CategoryFragment : Fragment(), View.OnClickListener, OnRecyclerItemClick {

    private lateinit var binding: FragmentCategoryBinding
    private val categoryList = mutableListOf<CategoryModel>()
    private var categoryAdapter: CategoryAdapter? = null
    private val db = Firebase.firestore
    private val _msg = "CategoryFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_category, container, false)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        initClickListeners()

        if (!InternetConnection.isInternetAvailable(activity)) {
            MyApplication.instance.showToast(getString(R.string.error_internet))
        }else{
            loadData()
        }

        return binding.root
    }

    private fun loadData() {

        binding.progressBar.visibility=View.VISIBLE

        db.collection("categories")
            .get()
            .addOnCompleteListener { task ->

                binding.progressBar.visibility = View.GONE

                if (task.isSuccessful) {

                    categoryList.clear()

                    for (document in task.result!!) {

                        val category = document.toObject(CategoryModel::class.java)

                        category.categoryId = document.id
                        category.categoryName = document.get("name").toString()

                        categoryList.add(category)

                        MyApplication.instance.log(_msg, document.id + " => " + document.data)
                    }

                    binding.rvCategoryList.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
                    categoryAdapter = CategoryAdapter(this)
                    binding.rvCategoryList.itemAnimator = DefaultItemAnimator()
                    categoryAdapter?.setData(categoryList)
                    binding.rvCategoryList.setHasFixedSize(false)
                    binding.rvCategoryList.isNestedScrollingEnabled = true
                    binding.rvCategoryList.adapter = categoryAdapter

                } else {
                    Log.d(_msg, "Error getting documents: ", task.exception)
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

        val temp = ArrayList<CategoryModel>()

        for (item in categoryList) {

            if (item.categoryName?.contains(text, ignoreCase = true)!! ) {

                temp.add(item)
            }
        }
        categoryAdapter?.setData(temp)
        categoryAdapter?.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.title = resources.getString(R.string.menu_home)
        loadData()
    }

    override fun onItemClick(view: View, position: Int, type: String) {
        when (type) {

            Constant.CONST_CATEGORY_CLICK -> {
                val menuItemFragment = MenuItemFragment()
                val bundle: Bundle? = Bundle()
                bundle?.putString(Constant.CATEGORY_ID,categoryList[position].categoryId)
                bundle?.putString(Constant.CATEGORY_NAME,categoryList[position].categoryName)
                menuItemFragment.arguments = bundle
                val transaction = activity?.supportFragmentManager?.beginTransaction()
                transaction?.replace(R.id.container, menuItemFragment)
                transaction?.addToBackStack(null)
                transaction?.commit()
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            else -> {
            }
        }
    }
}
