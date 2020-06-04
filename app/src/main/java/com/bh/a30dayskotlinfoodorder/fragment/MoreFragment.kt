package com.bh.a30dayskotlinfoodorder.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.bh.a30dayskotlinfoodorder.R
import com.bh.a30dayskotlinfoodorder.activity.UserDetailActivity
import com.bh.a30dayskotlinfoodorder.databinding.FragmentMoreBinding

class MoreFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentMoreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_more, container, false)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        initClickListeners()

        return binding.root
    }

    private fun initClickListeners() {
        binding.cardAddress.setOnClickListener(this)
        binding.cardRestaurant.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.cardAddress -> {
                val n = Intent(activity, UserDetailActivity::class.java)
                startActivity(n)
            }

            R.id.cardRestaurant -> {
                val n = Intent(activity, UserDetailActivity::class.java)
                startActivity(n)
            }

            else -> {
            }
        }
    }
}
