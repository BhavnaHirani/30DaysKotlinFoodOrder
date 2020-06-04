package com.bh.a30dayskotlinfoodorder.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bh.a30dayskotlinfoodorder.R
import com.bh.a30dayskotlinfoodorder.listener.OnRecyclerItemClick
import com.bh.a30dayskotlinfoodorder.model.CartModel
import kotlinx.android.synthetic.main.raw_menu_items.view.*
import kotlinx.android.synthetic.main.raw_menu_items.view.removeQty as removeQty1

class CartAdapter (private val onItemClickListener: OnRecyclerItemClick, private val addClickListener: (Int) -> Unit,
                   private val removeClickListener: (Int) -> Unit) :
    androidx.recyclerview.widget.RecyclerView.Adapter<CartAdapter.BaseViewHolder<*>>(){

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
            is ViewHolder -> holder.bind(element,onItemClickListener,position)
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

        private val txtItemName = itemView.txtItemName
        private val txtItemPrice = itemView.txtItemPrice

        override fun bind(item: CartModel, onItemClickListener: OnRecyclerItemClick, pos: Int) {

            txtItemName.text = item.menuName
            txtItemPrice.text = item.menuPrice.toString()

            itemView.addQty!!.visibility = View.VISIBLE
            itemView.txtQty!!.visibility = View.VISIBLE
            itemView.removeQty1!!.visibility = View.VISIBLE
            itemView.btnAdd!!.visibility = View.GONE
            itemView.categoryName!!.visibility = View.VISIBLE

            itemView.txtQty.text = item.qty.toString()
            itemView.categoryName.text = item.categoryName

            itemView.addQty.setOnClickListener{addClickListener(pos)}
            itemView.removeQty1.setOnClickListener{removeClickListener(pos)}
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
        abstract fun bind(item: T, onItemClickListener: OnRecyclerItemClick, pos:Int)
    }
}