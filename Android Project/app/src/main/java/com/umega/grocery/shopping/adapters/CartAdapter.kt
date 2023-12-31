package com.umega.grocery.shopping.adapters

import ImageHandle
import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.umega.grocery.R
import com.umega.grocery.dataBase.Repo
import com.umega.grocery.databinding.CartItemBinding
import com.umega.grocery.shopping.main.HomeViewModel
import com.umega.grocery.utill.CartItem
import kotlinx.coroutines.runBlocking

class CartAdapter(private val context: Context,private val viewModel: HomeViewModel) :
    RecyclerView.Adapter<CartAdapter.ViewHolder>() {
    private var cartItems: List<CartItem> = emptyList()
    private val imageHandle :ImageHandle = ImageHandle(context)
    private val repo: Repo = Repo(context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Using ViewBinding to inflate the item layout
        val binding: CartItemBinding =
            CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(this,binding)
    }

    private fun getImage(fileName:String, holder:ViewHolder) {
        runBlocking {
            val filePath = imageHandle.getCachedFilePath(fileName)
            //getting the image
            if (filePath != null) {
                // Use Glide or another image loading library to load the image into ImageView
                Glide.with(context)
                    .load(filePath)
                    .into(holder.binding.itemImage)
            } else {
                // Handle the case when the file does not exist
                // Setting broken image if failed
                holder.binding.itemImage.setImageResource(R.drawable.corrupt)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cartItem = cartItems[position]
        holder.binding.itemName.text = cartItem.itemName
        holder.binding.itemUnitPrice.text = cartItem.price.toString()
        holder.binding.totalPrice.text = cartItem.totalPrice.toString()
        holder.binding.itemCount.text = cartItem.itemQuantity.toString()
        getImage(cartItem.imgName,holder)
    }
    private fun handleDeleteItem(position: Int) {
        cartItems.toMutableList().apply {
            removeAt(position)
        }.let {
            cartItems = it
            notifyItemRemoved(position)
        }
    }

    override fun getItemCount(): Int = cartItems.size
    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newList: List<CartItem>) {
        cartItems = newList
        notifyDataSetChanged()
    }
    class  ViewHolder(private val adapter: CartAdapter, binding: CartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        // Using ViewBinding for easy access to views
        var binding: CartItemBinding
        init {
            this.binding = binding
            binding.imageView3.setOnClickListener {
                handleQuantityChange(true)
            }

            binding.imageView5.setOnClickListener {
                handleQuantityChange(false)
            }
            binding.trash.setOnClickListener {
                handleDeleteItem()
            }
        }
        private fun handleQuantityChange(increase: Boolean) {
            val currentPosition = adapterPosition
            if (currentPosition != RecyclerView.NO_POSITION) {
                val currentItem = adapter.cartItems[currentPosition]
                val newQuantity = if (increase) currentItem.itemQuantity + 1 else currentItem.itemQuantity - 1
                // Update the item quantity
                currentItem.itemQuantity = newQuantity
                currentItem.totalPrice = String.format("%.2f", newQuantity * currentItem.price).toDouble()
                adapter.viewModel.updateTotalPrice()
                adapter.repo.updateCartItemQuantity(currentItem.productId,newQuantity)
                adapter.notifyItemChanged(currentPosition)
            }
        }
        @SuppressLint("NotifyDataSetChanged")
        private fun handleDeleteItem() {
            val currentPosition = adapterPosition
            if (currentPosition != RecyclerView.NO_POSITION) {
                adapter.repo.deleteCartItem(adapter.cartItems[currentPosition].productId)
                adapter.handleDeleteItem(currentPosition)
                adapter.notifyDataSetChanged()
                adapter.viewModel.totalPrice.value = "0.00 EGP"
            }
        }
    }

}