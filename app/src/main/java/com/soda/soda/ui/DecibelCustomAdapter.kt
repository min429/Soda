package com.soda.soda.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.soda.soda.R
import com.soda.soda.databinding.ItemDecibelCustomBinding
import com.soda.soda.fragments.DecibelItem
import com.soda.soda.fragments.OnDecibelItemClickListener
import com.soda.soda.helper.DECIBEL_THRESHOLD
import java.security.AccessController.getContext

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
        if(item.threshold == DECIBEL_THRESHOLD)
            holder.binding.dBLayout.setBackgroundResource(R.drawable.decibel_box_selected)
        else
            holder.binding.dBLayout.setBackgroundResource(R.drawable.decibel_box)
    }
    
    override fun getItemCount(): Int {
        return decibelItems.size
    }
}
