package com.ghadeer.posassignment.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ghadeer.posassignment.data.model.Product
import com.ghadeer.posassignment.data.model.SalesProduct
import com.ghadeer.posassignment.databinding.ItemProductBinding
import com.ghadeer.posassignment.databinding.ItemSalesProductBinding
import com.ghadeer.posassignment.util.asMoney


class SalesProductsRecyclerAdapter(
    private val onSalesProductInteractListener: OnSalesProductInteractListener? = null,
) : PagingDataAdapter<SalesProduct, SalesProductsRecyclerAdapter.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SalesProduct>() {
            override fun areItemsTheSame(
                oldItem: SalesProduct,
                newItem: SalesProduct
            ): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: SalesProduct,
                newItem: SalesProduct
            ): Boolean =
                oldItem.barcode == newItem.barcode
                        && oldItem.name == newItem.name
                        && oldItem.price == newItem.price
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ItemSalesProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemCount(): Int {
        return this.snapshot().size
    }


    inner class ViewHolder(private val itemBinding: ItemSalesProductBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(item: SalesProduct?) {

            item?.let {
                itemBinding.apply {
                    tvProductName.text = item.name
                    tvProductPrice.text = item.price.asMoney()
                    root.setOnClickListener { onSalesProductInteractListener?.onSalesProductClicked(item) }
                    root.setOnLongClickListener {
                        onSalesProductInteractListener?.onSalesProductLongClicked(item)
                        true
                    }
                }
            }
        }
    }
}

interface OnSalesProductInteractListener{
    fun onSalesProductClicked(salesProduct: SalesProduct)
    fun onSalesProductLongClicked(salesProduct: SalesProduct)
}