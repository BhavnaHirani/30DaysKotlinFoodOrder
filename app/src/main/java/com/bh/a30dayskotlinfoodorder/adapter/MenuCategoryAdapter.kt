package com.bh.a30dayskotlinfoodorder.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bh.a30dayskotlinfoodorder.R
import com.bh.a30dayskotlinfoodorder.listener.OnRecyclerItemClick
import com.bh.a30dayskotlinfoodorder.model.CartModel
import com.bh.a30dayskotlinfoodorder.utils.Constant
import com.bh.a30dayskotlinfoodorder.utils.PreferenceHelper
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.raw_menu_items.view.*

class MenuCategoryAdapter(private val onItemClickListener: OnRecyclerItemClick) :
    androidx.recyclerview.widget.RecyclerView.Adapter<MenuCategoryAdapter.BaseViewHolder<*>>() {

    private var mContext: Context? = null
    private val dataList = ArrayList<CartModel>()
    private val cartList = ArrayList<CartModel>()

    /**
     * create view holder to hold reference
     *
     * @param parent  the parent
     * @param viewType the viewType
     * @return  view
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context
        val view = LayoutInflater.from(mContext).inflate(R.layout.raw_menu_items, parent, false)
        return ViewHolder(view)
    }

    /**
     *   bind values
     *
     * @param holder the  holder
     * @param position the position
     */
    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val element = dataList[position]
        when (holder) {
            is ViewHolder -> holder.bind(element,onItemClickListener)
        }
    }

    /**
     *  Get total number of rows.
     *  @return  List size
     */
    override fun getItemCount(): Int {
        return dataList.size
    }

    /**
     * Set Data to  dataList .
     *
     */
    fun setData(searchList: List<CartModel>?, cartList :List<CartModel>) {
        this.dataList.clear()
        this.dataList.addAll(searchList!!)
        this.cartList.clear()
        this.cartList.addAll(cartList)
    }

    /**
     * stores and recycles views as they are scrolled off screen
     *
     * @constructor
     * bind data  in view
     *
     * @param itemView  the view
     */
    inner class ViewHolder(itemView: View) : BaseViewHolder<CartModel>(itemView) {

        private val txtItemName = itemView.txtItemName
        private val txtItemPrice = itemView.txtItemPrice

        override fun bind(item: CartModel, onItemClickListener: OnRecyclerItemClick) {

            txtItemName.text = item.menuName
            txtItemPrice.text = item.menuPrice.toString()

            itemView.setOnClickListener {
                onItemClickListener.onItemClick(itemView, adapterPosition, Constant.CONST_MENU_CATEGORY_CLICK)
            }

            if(item.qty != 0){
                itemView.addQty!!.visibility = View.VISIBLE
                itemView.txtQty!!.visibility = View.VISIBLE
                itemView.removeQty!!.visibility = View.VISIBLE
                itemView.btnAdd!!.visibility = View.GONE
                itemView.txtQty.text = item.qty.toString()
            }

            itemView.btnAdd!!.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View) {
                    Log.e("click", "click pressed")
                    showQtyFields()
                }

                private fun showQtyFields() {
                    itemView.addQty!!.visibility = View.VISIBLE
                    itemView.txtQty!!.visibility = View.VISIBLE
                    itemView.removeQty!!.visibility = View.VISIBLE
                    itemView.btnAdd!!.visibility = View.GONE
                    itemView.txtQty.text = "1"
                    val pos = adapterPosition
                    dataList[pos].qty = 1
                    getCartData(pos)
                }
            })

            itemView.addQty!!.setOnClickListener {
                var count = itemView.txtQty!!.text.toString().toInt()
                count++
                itemView.txtQty!!.text = count.toString()

                val pos = adapterPosition
                dataList[pos].qty = count
                getCartData(pos)
            }

            itemView.removeQty!!.setOnClickListener {
                var qty = itemView.txtQty!!.text.toString().toInt()

                if (qty > 1) {
                    qty--
                    itemView.txtQty!!.text = qty.toString()
                    val pos = adapterPosition
                    dataList[pos].qty = qty
                    getCartData(pos)
                } else {
                    itemView.btnAdd!!.visibility = View.VISIBLE
                    itemView.addQty!!.visibility = View.GONE
                    itemView.txtQty!!.visibility = View.GONE
                    itemView.removeQty!!.visibility = View.GONE
                    val pos = adapterPosition
                    dataList[pos].qty = 0
                    getCartData(pos)
                }
            }
        }
    }

    private fun getCartData(pos : Int){
        val cartModel : CartModel = dataList[pos]
        var isCheck = false
        for(i in cartList.indices){
            if(cartModel.menuId.equals(cartList[i].menuId)){
                isCheck = true
                cartList[i].qty = cartModel.qty
                cartList[i].menuPrice = cartModel.menuPrice
                val total : Double = (cartList[i].qty * cartList[i].menuPrice!!.toInt()).toDouble()
                cartList[i].total = total.toString()
                break
            }
        }

        if(!isCheck){
            cartList.add(cartModel)
        }

        if(cartList.size > 0){
            val gson = GsonBuilder().create()
            val cartString : String = gson.toJson(cartList)
            PreferenceHelper.putValue(PreferenceHelper.CART_MODEL, cartString)
        } else{
            PreferenceHelper.putValue(PreferenceHelper.CART_MODEL, "")
        }

    }

    /**
     *  set child view
     *
     * @constructor
     * holder class to hold reference
     *
     * @param itemView the view
     */
    abstract class BaseViewHolder<T>(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: T, onItemClickListener: OnRecyclerItemClick)
    }
}