package com.umega.grocery.shopping.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.umega.grocery.databinding.SearchItemBinding

class SearchAdapter (private val context: Context,
                     private val searchItems: List<String>,
                     private val searchTheItem: (search: String) -> Unit)
    : RecyclerView.Adapter<SearchAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            // Using ViewBinding to inflate the item layout
            val binding: SearchItemBinding =
                SearchItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val searchItem = searchItems[position]
            holder.binding.searchItemName.text  = searchItem // Replace with actual data
            holder.binding.itemCardView.setOnClickListener{searchTheItem(searchItem)}
        }

        override fun getItemCount(): Int = searchItems.size

        class ViewHolder(binding: SearchItemBinding) : RecyclerView.ViewHolder(binding.root) {
            // Using ViewBinding for easy access to views
            var binding: SearchItemBinding
            init {
                this.binding = binding
            }
        }
    }