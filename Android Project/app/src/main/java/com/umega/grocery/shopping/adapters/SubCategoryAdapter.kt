package com.umega.grocery.shopping.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.umega.grocery.R
import com.umega.grocery.utill.Category
import com.umega.grocery.utill.SubCategory

class SubCategoryAdapter (private val onItemClick:  (SubCategory) -> Unit): BaseAdapter() {
    private var itemList: List<SubCategory> = emptyList()

    fun submitList(newList: List<SubCategory>) {
        itemList = newList
        notifyDataSetChanged()
    }
    override fun getCount(): Int = itemList.size
    override fun getItem(position: Int): Any = itemList[position]
    override fun getItemId(position: Int): Long = position.toLong()
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val subCategory = getItem(position) as SubCategory
        val inflater = LayoutInflater.from(parent?.context)
        Log.i("loladapter",subCategory.toString())
        val itemView: View = convertView ?: inflater.inflate(R.layout.category_list_item, parent, false)
        val subCategoryNameTextView: TextView = itemView.findViewById(R.id.item_name_text)
        subCategoryNameTextView.text = subCategory.name
        itemView.setOnClickListener {
            // Call the onItemClick listener, passing the selected subCategory
            onItemClick(subCategory)
        }
        return itemView
    }
}