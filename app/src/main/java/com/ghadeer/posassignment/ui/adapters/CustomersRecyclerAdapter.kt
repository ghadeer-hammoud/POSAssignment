package com.ghadeer.posassignment.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ghadeer.posassignment.data.model.Customer
import com.ghadeer.posassignment.databinding.ItemCustomerBinding
import com.ghadeer.posassignment.util.asMoney
import com.ghadeer.posassignment.util.showIf


class CustomersRecyclerAdapter(
    private val isPickMode: Boolean = false,
    private val onCustomerInteractListener: OnCustomerInteractListener? = null,
) : PagingDataAdapter<Customer, CustomersRecyclerAdapter.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Customer>() {
            override fun areItemsTheSame(
                oldItem: Customer,
                newItem: Customer
            ): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: Customer,
                newItem: Customer
            ): Boolean = oldItem.name == newItem.name

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ItemCustomerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemCount(): Int {
        return this.snapshot().size
    }


    inner class ViewHolder(private val itemBinding: ItemCustomerBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(item: Customer?) {

            item?.let {
                itemBinding.apply {
                    tvName.text = item.name
                    root.setOnClickListener { onCustomerInteractListener?.onCustomerClicked(item) }
                    ivEdit.setOnClickListener { onCustomerInteractListener?.onCustomerEditClicked(item.id) }
                    ivDelete.setOnClickListener { onCustomerInteractListener?.onCustomerDeleteClicked(item.id) }

                    ivEdit.showIf(!isPickMode)
                    ivDelete.showIf(!isPickMode)
                }
            }
        }
    }
}

interface OnCustomerInteractListener{
    fun onCustomerClicked(customer: Customer)
    fun onCustomerEditClicked(customerId: Int)
    fun onCustomerDeleteClicked(customerId: Int)
}