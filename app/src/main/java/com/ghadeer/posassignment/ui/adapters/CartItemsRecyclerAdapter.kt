package com.ghadeer.posassignment.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ghadeer.posassignment.R
import com.ghadeer.posassignment.databinding.ItemCartRowBinding
import com.ghadeer.posassignment.data.model.CartItem
import com.ghadeer.posassignment.util.asMoney


class CartItemsRecyclerAdapter(private val itemsList: MutableList<CartItem>,
                               private val onCartItemInteractListener: OnCartItemInteractListener? = null
                               )
    : RecyclerView.Adapter<CartItemsRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ItemCartRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemsList[position])
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }


    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(newItemsList: List<CartItem>){

        itemsList.clear()
        itemsList.addAll(newItemsList)
        notifyDataSetChanged()
    }


    inner class ViewHolder(private val itemBinding: ItemCartRowBinding) : RecyclerView.ViewHolder(itemBinding.root){
        fun bind(item: CartItem){

            itemBinding.apply {
                tvName.text = item.productName
                tvPrice.text = item.price.asMoney(showCurrency = false)
                tvTax.text = item.tax.asMoney(showCurrency = false)
                tvDiscount.text = item.discount.asMoney(showCurrency = false)
                tvQty.text = item.quantity.toString()
                tvTotal.text = item.total.asMoney()

                tvTotal.setTextColor(
                    ContextCompat.getColor(itemBinding.root.context,
                        when(item.hasDiscount()){
                            true -> R.color.red_900
                            false -> R.color.black
                        }
                    )
                )

                root.setBackgroundColor(itemBinding.root.context.getColor(
                    if(layoutPosition % 2 == 0) R.color.white else R.color.grey_200
                ))

                root.setOnClickListener { onCartItemInteractListener?.onCartItemClicked(item.productId) }

                ivRemove.setOnClickListener {
                    onCartItemInteractListener?.onCartItemDeleteClicked(item.productId)
                }
            }
        }
    }
}

interface OnCartItemInteractListener{
    fun onCartItemClicked(productId: Int)
    fun onCartItemDeleteClicked(productId: Int)
}
