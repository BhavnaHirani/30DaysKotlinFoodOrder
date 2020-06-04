package com.bh.a30dayskotlinfoodorder.listener

import android.view.View

interface OnRecyclerItemClick {

    fun onItemClick(view: View, position: Int, type: String = "") { /* default implementation */
    }

    fun onItemClick(view: View, any: Any, position: Int, type: String = "") { /* default implementation */
    }

    fun onItemDataClick(view: View, position: Int, type: String = "",any: Any) { /* default implementation */
    }
}

interface OnClickListener {
    fun onClick(view: View)
}