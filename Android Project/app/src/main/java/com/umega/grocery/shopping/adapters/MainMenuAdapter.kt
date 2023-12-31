package com.umega.grocery.shopping.adapters

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umega.grocery.R
import com.umega.grocery.databinding.MainSideListItemBinding
import com.umega.grocery.databinding.ResultItemBinding

class MainMenuAdapter(
    private val resources: Resources,
    private val data: List<String> = resources.getStringArray(R.array.main_menu_items).toList(),
    private val navigateOnClick: (option: Int) -> Unit
):
    RecyclerView.Adapter<MainMenuAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Using ViewBinding to inflate the item layout
        val binding: MainSideListItemBinding =
            MainSideListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    private fun getImage(position: Int) = when (position) {
        0 -> R.drawable.group_2777
        1 -> R.drawable.group_2778
        2 -> R.drawable.group_2779
        3 -> R.drawable.group_2780
        4 -> R.drawable.group_2781
        5 -> R.drawable.group_278200
        6 -> R.drawable.group_278300
        7 -> R.drawable.group_2784
        else -> R.drawable.group_2781
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val option = data[position]
        holder.binding.itemName.text = option

        holder.binding.imageView2.setImageResource(getImage(position))

        holder.binding.root.setOnClickListener{ navigateOnClick(position) }
    }


    override fun getItemCount(): Int = data.size

    class ViewHolder(binding: MainSideListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        // Using ViewBinding for easy access to views
        var binding: MainSideListItemBinding
        init {
            this.binding = binding
        }
    }
}