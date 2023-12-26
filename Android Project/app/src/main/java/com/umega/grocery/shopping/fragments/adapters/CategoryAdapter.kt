package com.umega.grocery.shopping.fragments.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.umega.grocery.R
import com.umega.grocery.utill.Category

class CategoryAdapter(private val context: Context) : BaseAdapter() {
    private var itemList: List<Category> = emptyList()

    fun submitList(newList: List<Category>) {
        itemList = newList
        notifyDataSetChanged()
    }
    override fun getCount(): Int = itemList.size
    override fun getItem(position: Int): Any = itemList[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val category = getItem(position) as Category
        val inflater = LayoutInflater.from(parent?.context)
        val itemView: View = convertView ?: inflater.inflate(R.layout.main_category_item, parent, false)
        val categoryImageView: ImageView = itemView.findViewById(R.id.icon)
        val categoryNameTextView: TextView = itemView.findViewById(R.id.category)
        categoryNameTextView.text = category.name
        val imageName = category.name.toLowerCase().replace(" ", "")
        val imageResourceId = context.resources.getIdentifier(imageName, "drawable", context.packageName)
        categoryImageView.setImageResource(imageResourceId)
        return itemView
    }
}
