package com.ghadeer.posassignment.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ghadeer.posassignment.R
import com.ghadeer.posassignment.data.model.Order
import com.ghadeer.posassignment.data.enums.OrderStatus
import com.ghadeer.posassignment.data.enums.PaymentStatus
import com.ghadeer.posassignment.databinding.ItemOrderBinding
import com.ghadeer.posassignment.util.asMoney
import com.ghadeer.posassignment.util.formatDate


class OrdersRecyclerAdapter(
    private val onOrderInteractListener: OnOrderInteractListener? = null,
) : PagingDataAdapter<Order, OrdersRecyclerAdapter.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Order>() {
            override fun areItemsTheSame(
                oldItem: Order,
                newItem: Order
            ): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: Order,
                newItem: Order
            ): Boolean =
                oldItem.orderId == newItem.orderId

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemCount(): Int {
        return this.snapshot().size
    }


    inner class ViewHolder(private val itemBinding: ItemOrderBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(item: Order?) {

            item?.let {
                itemBinding.apply {
                    tvOrderId.text = item.orderId
                    tvOrderStatus.text = OrderStatus.labels()[OrderStatus.values().indexOf(item.orderStatus)].uppercase()
                    tvOrderStatus.setBackgroundColor(ContextCompat.getColor(root.context, OrderStatus.indicatorColors()[OrderStatus.values().indexOf(item.orderStatus)]))
                    tvPaymentStatus.text = PaymentStatus.labels()[PaymentStatus.values().indexOf(item.paymentStatus)].uppercase()
                    tvPaymentStatus.setBackgroundColor(ContextCompat.getColor(root.context, PaymentStatus.indicatorColors()[PaymentStatus.values().indexOf(item.paymentStatus)]))
                    tvAmount.text = item.items.sumOf { it.total }.asMoney()
                    tvItemsCount.text = item.items.size.toString()
                    tvQty.text = item.items.sumOf { it.quantity }.toString()
                    tvCustomer.text = item.customer?.name?.uppercase() ?: root.context.getString(R.string.visitor)
                    tvCreateDate.text = item.createdAt.formatDate()

                    root.setOnClickListener { onOrderInteractListener?.onOrderClicked(item.orderId) }

                }
            }
        }
    }
}

interface OnOrderInteractListener{
    fun onOrderClicked(orderId: String)
}