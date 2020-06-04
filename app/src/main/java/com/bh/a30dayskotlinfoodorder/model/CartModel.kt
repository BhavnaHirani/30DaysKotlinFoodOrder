package com.bh.a30dayskotlinfoodorder.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class CartModel : Serializable {

    @SerializedName("categoryName")
    var categoryName: String? = null

    @SerializedName("categoryId")
    var categoryId: String? = null

    @SerializedName("menuName")
    var menuName: String? = null

    @SerializedName("menuId")
    var menuId: String? = null

    @SerializedName("menuPrice")
    var menuPrice: String? = null

    @SerializedName("qty")
    var qty: Int = 0

    @SerializedName("total")
    var total: String? = null
}