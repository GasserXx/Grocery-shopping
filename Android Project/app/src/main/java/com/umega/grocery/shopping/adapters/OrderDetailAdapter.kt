package com.umega.grocery.shopping.adapters

import ImageHandle
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.umega.grocery.R
import com.umega.grocery.databinding.OrderDetailItemBinding
import com.umega.grocery.utill.OrderItem
import com.umega.grocery.utill.Product
import kotlinx.coroutines.runBlocking

class OrderDetailAdapter (private val items: MutableList<OrderItem>,
                          private val products:MutableList<Product>,
                          private val context:Context,
                          private val navigateToDetail: (product: Product) -> Unit) :
    RecyclerView.Adapter<OrderDetailAdapter.ViewHolder>() {
    private val imageHandle :ImageHandle = ImageHandle(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Using ViewBinding to inflate the item layout
        val binding: OrderDetailItemBinding =
            OrderDetailItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    private fun getImage(fileName:String, holder: ViewHolder) {
        runBlocking {
            val filePath = imageHandle.getCachedFilePath(fileName)
            Log.i("LOL", "with image path of $filePath, with filename: $fileName")
            //getting the image
            if (filePath != null) {
                // Use Glide or another image loading library to load the image into ImageView
                Glide.with(context)
                    .load(filePath)
                    .into(holder.binding.image)
            } else {
                // Handle the case when the file does not exist
                // Setting broken image if failed
                holder.binding.image.setImageResource(R.drawable.corrupt)
            }
        }
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val orderItem = items[position]
        val product = products[position]
        holder.binding.itemNameText.text = product.name
        holder.binding.pricePerItem.text = " -${product.price - orderItem.discount} EGP"
        holder.binding.quantityText.text = "Quantity: ${orderItem.quantity}"
        holder.binding.priceText.text = "${(product.price - orderItem.discount)*orderItem.quantity} EGP"
        getImage(product.imgName, holder)

        holder.binding.root.setOnClickListener { navigateToDetail(product) }
    }


    override fun getItemCount(): Int = items.size

    class ViewHolder(binding: OrderDetailItemBinding) : RecyclerView.ViewHolder(binding.root) {
        // Using ViewBinding for easy access to views
        var binding: OrderDetailItemBinding
        init {
            this.binding = binding
        }
    }
}