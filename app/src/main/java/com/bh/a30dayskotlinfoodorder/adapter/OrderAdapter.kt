package com.bh.a30dayskotlinfoodorder.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bh.a30dayskotlinfoodorder.R
import com.bh.a30dayskotlinfoodorder.listener.OnRecyclerItemClick
import com.bh.a30dayskotlinfoodorder.model.CartModel
import com.bh.a30dayskotlinfoodorder.utils.Constant
import kotlinx.android.synthetic.main.raw_order.view.*

class OrderAdapter (private val onItemClickListener: OnRecyclerItemClick) :
    androidx.recyclerview.widget.RecyclerView.Adapter<OrderAdapter.BaseViewHolder<*>>() {

    private var mContext: Context? = null
    private val dataList = ArrayList<CartModel>()

    /**
     * create view holder to hold reference
     *
     * @param parent  the parent
     * @param viewType the viewType
     * @return  view
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context
        val view = LayoutInflater.from(mContext).inflate(R.layout.raw_order, parent, false)
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
    fun setData(searchList: List<CartModel>?) {
        this.dataList.clear()
        this.dataList.addAll(searchList!!)
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

        private val txtItemName = itemView.txtItemName1
        private val txtItemPrice = itemView.txtItemPrice1
        private val txtItemQTY = itemView.txtItemQTY1
        private val txtItemTotal = itemView.txtItemTotal1

        override fun bind(item: CartModel, onItemClickListener: OnRecyclerItemClick) {

            txtItemName.text = item.menuName
            txtItemPrice.text = item.menuPrice.toString()
            txtItemQTY.text = item.qty.toString()
            val total : Int  =  item.menuPrice!!.toInt() * item.qty
            txtItemTotal.text = total.toString()

            itemView.setOnClickListener {
                onItemClickListener.onItemClick(itemView, adapterPosition, Constant.CONST_MENU_CATEGORY_CLICK)
            }
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