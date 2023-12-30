package com.umega.grocery.shopping.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.RadioButton
import android.widget.TextView
import com.umega.grocery.R
import com.umega.grocery.shopping.result.ResultViewModel
import com.umega.grocery.utill.Brand

class BrandAdapter(private val context: Context, private val brandList: List<Brand>, private val viewModel:ResultViewModel) : BaseAdapter() {

    override fun getCount(): Int {
        return brandList.size
    }

    override fun getItem(position: Int): Any {
        return brandList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemView = inflater.inflate(R.layout.brand_filter_item, parent, false)

        val brandNameTextView: RadioButton = itemView.findViewById(R.id.brand_item)
        brandNameTextView.text = brandList[position].name

        brandNameTextView.setOnClickListener {
            if (brandNameTextView.isChecked) viewModel.addSubBrand(brandList[position])
            else  viewModel.removeSubBrand(brandList[position])
        }

        // Add other view bindings here if needed

        return itemView
    }
}
