package com.umega.grocery.shopping.adapters

import ImageHandle
import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.umega.grocery.R
import com.umega.grocery.utill.Product
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DailyDealsAdapter(private val context: Context,
                        private val onItemClick: (Product) -> Unit
) :
    RecyclerView.Adapter<DailyDealsAdapter.ViewHolder>() {

    private var itemList: List<Product> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newList: List<Product>) {
        itemList = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.result_item, parent, false)
        return ViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val deal = itemList[position]
        holder.bind(deal)

    }
    override fun getItemCount(): Int = itemList.size
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemImageView: ImageView = itemView.findViewById(R.id.item_image)
        private val itemNameTextView: TextView = itemView.findViewById(R.id.itemName_text)
        private val itemPriceTextView: TextView = itemView.findViewById(R.id.itemPrice_text)
        //Since we deleted the KG badge
//        private val itemQuantityTextView: TextView = itemView.findViewById(R.id.quantityValue_text)
        private val imageHandle :ImageHandle = ImageHandle(context)
        @SuppressLint("SetTextI18n", "DiscouragedApi")
        fun bind(deal: Product) {
            itemNameTextView.text = deal.name
            itemView.setOnClickListener {
                onItemClick(deal)
            }
            val coroutineScope = CoroutineScope(Dispatchers.IO)
            coroutineScope.launch {
                val cachedFilePath = imageHandle.getCachedFilePath(deal.imgName)
                Log.i("loladap",cachedFilePath.toString())
                if (cachedFilePath != null) {
                    Glide.with(context)
                        .load(cachedFilePath)
                        .into(itemImageView)
                } else {
                   Log.i("lol9","aa")
                }
            }
            val productPriceAfterDiscount = deal.price - deal.discount
            itemPriceTextView.text = "$productPriceAfterDiscount EGP"
//            itemQuantityTextView.text = "1KG"
        }
    }
}
