package com.soda.soda.ui

import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.soda.soda.R
import com.soda.soda.databinding.ItemWarningCustomBinding
import com.soda.soda.fragments.OnItemClickedListener

data class Item(val label: String, var isChecked: Boolean = false)

class WarningCustomAdapter(
    private var items: List<Item>,
    private val listener: OnItemClickedListener
) : RecyclerView.Adapter<WarningCustomAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding =
            ItemWarningCustomBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount() = items.size

    inner class ItemViewHolder(private val binding: ItemWarningCustomBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item) {
            binding.warningText.text = item.label
            if(item.isChecked)
                binding.checkImage.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.check_black))
            else
                binding.checkImage.setImageDrawable(ColorDrawable(ContextCompat.getColor(itemView.context, R.color.white)))
            binding.decibelLayout.setOnClickListener {
                item.isChecked = !item.isChecked
                if(item.isChecked)
                    binding.checkImage.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.check_black))
                else
                    binding.checkImage.setImageDrawable(ColorDrawable(ContextCompat.getColor(itemView.context, R.color.white)))
                listener.onItemClicked(item)
            }
        }
    }

    fun updateList(newList: List<Item>) {
        val diffCallback = ItemDiffCallback(items, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        items = newList
        diffResult.dispatchUpdatesTo(this)
    }

    class ItemDiffCallback(
        private val oldList: List<Item>,
        private val newList: List<Item>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].label == newList[newItemPosition].label
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }

}
