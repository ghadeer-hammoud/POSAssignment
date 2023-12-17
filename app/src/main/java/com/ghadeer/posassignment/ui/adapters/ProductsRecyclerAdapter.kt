package com.ghadeer.posassignment.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ghadeer.posassignment.data.model.Product
import com.ghadeer.posassignment.databinding.ItemProductBinding
import com.ghadeer.posassignment.util.asMoney


class ProductsRecyclerAdapter(
    private val onProductInteractListener: OnProductInteractListener? = null,
) : PagingDataAdapter<Product, ProductsRecyclerAdapter.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(
                oldItem: Product,
                newItem: Product
            ): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: Product,
                newItem: Product
            ): Boolean =
                oldItem.barcode == newItem.barcode
                        && oldItem.name == newItem.name
                        && oldItem.price == newItem.price
                        && oldItem.category.id == newItem.category.id
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemCount(): Int {
        return this.snapshot().size
    }


    inner class ViewHolder(private val itemBinding: ItemProductBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(item: Product?) {

            item?.let {
                itemBinding.apply {
                    tvBarcode.text = item.barcode
                    tvName.text = item.name
                    tvPrice.text = item.price.asMoney()
                    tvCategory.text = item.category.name
                    root.setOnClickListener { onProductInteractListener?.onProductClicked(item.id) }
                    ivEdit.setOnClickListener { onProductInteractListener?.onProductEditClicked(item.id) }
                    ivDelete.setOnClickListener { onProductInteractListener?.onProductDeleteClicked(item.id) }
                }
            }
        }
    }
}

interface OnProductInteractListener{
    fun onProductClicked(productId: Int)
    fun onProductEditClicked(productId: Int)
    fun onProductDeleteClicked(productId: Int)
}