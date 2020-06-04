@file:Suppress("DEPRECATION")

package com.bh.a30dayskotlinfoodorder.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bh.a30dayskotlinfoodorder.R
import com.bh.a30dayskotlinfoodorder.databinding.ActivityMainBinding
import com.bh.a30dayskotlinfoodorder.fragment.CartFragment
import com.bh.a30dayskotlinfoodorder.fragment.CategoryFragment
import com.bh.a30dayskotlinfoodorder.fragment.MoreFragment
import com.bh.a30dayskotlinfoodorder.fragment.OrdersFragment

class MainActivity : BaseActivity() {

    lateinit var mainBinding: ActivityMainBinding
    override var mContext: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mContext = this

        loadData()
        initClickListeners()
    }

    private fun loadData() {
        val categoryFragment = CategoryFragment()
        categoryFragment.retainInstance = false
        title=resources.getString(R.string.menu_home)
        loadFragment(CategoryFragment())
    }

    private fun initClickListeners() {

        // Set the listener for item selection in the bottom navigation view.
        mainBinding.bottomNavigation.setOnNavigationItemSelectedListener {

            when(it.itemId){

                R.id.navigation_homeMenu -> {
                    title=resources.getString(R.string.menu_home)
                    loadFragment(CategoryFragment())
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.navigation_add_cart-> {
                    title=resources.getString(R.string.menu_cart)
                    loadFragment(CartFragment())
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.navigation_orders-> {
                    title=resources.getString(R.string.menu_order)
                    loadFragment(OrdersFragment())
                    return@setOnNavigationItemSelectedListener true
                }

                R.id.navigation_more-> {
                    title=resources.getString(R.string.menu_more)
                    loadFragment(MoreFragment())
                    return@setOnNavigationItemSelectedListener true
                }
            }
            false
        }
    }

    private fun loadFragment(fragment: Fragment) {
        // load fragment
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        Log.e("TAG", "onBackPressed:count $count")
        if (count == 1) {
            finish()
        } else {
            supportFragmentManager.popBackStack()
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK){
            //Update List
            mainBinding.bottomNavigation.selectedItemId = R.id.navigation_homeMenu
        }
    }
}
