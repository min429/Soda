package com.soda.soda.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.soda.soda.databinding.ItemDecibelCustomBinding
import com.soda.soda.fragments.DecibelItem
import com.soda.soda.fragments.OnDecibelItemClickListener

class DecibelCustomAdapter(
    private val decibelItems: List<DecibelItem>,
    private val clickListener: OnDecibelItemClickListener
) : RecyclerView.Adapter<DecibelCustomAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(val binding: ItemDecibelCustomBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemDecibelCustomBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = decibelItems[position]
        holder.binding.dBText.text = item.dBText
        holder.binding.infoText.text = item.infoText
        holder.itemView.setOnClickListener {
            clickListener.onDecibelItemClick(item.threshold)
        }
    }
    
    override fun getItemCount(): Int {
        return decibelItems.size
    }
}
