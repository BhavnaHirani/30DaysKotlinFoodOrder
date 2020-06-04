package com.bh.a30dayskotlinfoodorder.activity

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.bh.a30dayskotlinfoodorder.R
import com.bh.a30dayskotlinfoodorder.databinding.ActivitySplashBinding
import com.bh.a30dayskotlinfoodorder.listener.onPermisionChecked
import com.bh.a30dayskotlinfoodorder.utils.MyApplication
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SplashActivity : BaseActivity() {

    private var handle: Handler? = null
    private var r: Runnable? = null
    private lateinit var binding: ActivitySplashBinding
    override var mContext: Context? = null
    var isPermissionDone = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        mContext = this

        binding.txtVersionInfo.text = "AppVersion - " + MyApplication.instance.getAppVersionName()

        askForAppPermission(object : onPermisionChecked {
            override fun onChecked(flag: Boolean) {
                isPermissionDone = true
            }
        })

        handle = Handler()

        r = Runnable {
            if(isPermissionDone)
                makeInit()
            else
                handle!!.postDelayed(r, 3000)
        }

        handle!!.postDelayed(r, 3000)
    }

    private fun makeInit() {
        GlobalScope.launch {
            openPageWithFinish(MainActivity::class.java)
        }
    }
}
