package com.ghadeer.posassignment.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.ghadeer.posassignment.databinding.ItemHomeMenuOptionBinding
import com.ghadeer.posassignment.data.model.view.HomeMenuAction
import com.ghadeer.posassignment.data.model.view.HomeMenuItem



class HomeMenuItemsRecyclerAdapter(
    private val itemsList: MutableList<HomeMenuItem>,
    private val onMenuItemClickListener: OnMenuItemClickListener? = null,
) : RecyclerView.Adapter<HomeMenuItemsRecyclerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = ItemHomeMenuOptionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemsList[position])
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }

    fun updateItems(items: List<HomeMenuItem>){
        itemsList.clear()
        itemsList.addAll(items)
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val itemBinding: ItemHomeMenuOptionBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bind(item: HomeMenuItem) {

            itemBinding.apply {
                ivIcon.setImageDrawable(AppCompatResources.getDrawable(root.context, item.iconRes))
                tvTitle.text = root.context.getString(item.titleRes)
                root.setOnClickListener { onMenuItemClickListener?.onMenuItemClicked(item.action) }
            }
        }
    }
}

interface OnMenuItemClickListener{
    fun onMenuItemClicked(homeMenuAction: HomeMenuAction)
}