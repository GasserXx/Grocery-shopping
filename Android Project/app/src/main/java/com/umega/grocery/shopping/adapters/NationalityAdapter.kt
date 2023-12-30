package com.umega.grocery.shopping.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.RadioButton
import com.umega.grocery.R
import com.umega.grocery.shopping.result.ResultViewModel

class NationalityAdapter(private val context: Context, private val nationalityList: List<String>, private val viewModel:ResultViewModel) : BaseAdapter() {

    override fun getCount(): Int {
        return nationalityList.size
    }

    override fun getItem(position: Int): Any {
        return nationalityList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemView = inflater.inflate(R.layout.brand_filter_item, parent, false)

        val nationalityItem: RadioButton = itemView.findViewById(R.id.brand_item)
        nationalityItem.text = nationalityList[position]

        nationalityItem.setOnClickListener {
            if (nationalityItem.isChecked) viewModel.addSubNationality(nationalityList[position])
            else  viewModel.removeSubNationality(nationalityList[position])
        }

        // Add other view bindings here if needed

        return itemView
    }
}
