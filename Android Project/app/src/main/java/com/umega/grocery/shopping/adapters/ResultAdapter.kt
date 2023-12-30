package com.umega.grocery.shopping.adapters
import ImageHandle
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.umega.grocery.R
import com.umega.grocery.databinding.ResultItemBinding
import com.umega.grocery.utill.Product
import kotlinx.coroutines.runBlocking


class ResultAdapter (private val context: Context, private var products: MutableList<Product>,
                     private val navigateToDetail: (product: Product) -> Unit) :
    RecyclerView.Adapter<ResultAdapter.ViewHolder>() {
    private val imageHandle :ImageHandle = ImageHandle(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Using ViewBinding to inflate the item layout
        val binding: ResultItemBinding =
            ResultItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    private fun getImage(fileName:String, holder:ViewHolder) {
        runBlocking {
            //TODO find-out why image aren't to be retrieved
            val filePath = imageHandle.getCachedFilePath(fileName)
            Log.i("LOL", "with image path of $filePath, with filename: $fileName")
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
        val product = products[position]
        holder.binding.itemNameText.text = product.name // Replace with actual data

        //discount check
        if (product.discount == 0.0)
        //Building String For Price
        holder.binding.itemPriceText.text = buildString {
        append(product.price)
            //on Multiple Currencies modify the following
        append(" EGP")
    }
        else
        {
            //Building String For Price
            holder.binding.itemPriceText.text = buildString {
                append(product.price - product.discount)
                //on Multiple Currencies modify the following
                append(" EGP")
            }
            val beforePriceText = holder.binding.pricebefore
            beforePriceText.text = buildString {
                append(product.price)
                //on Multiple Currencies modify the following
                append(" EGP")
            }
            beforePriceText.visibility = View.VISIBLE
        }

        holder.binding.isFavorite = false

        holder.binding.loveButtonImage.setOnClickListener {
            holder.binding.isFavorite = holder.binding.isFavorite xor true
            holder.binding.loveButtonImage.setImageResource(
                if (holder.binding.isFavorite)R.drawable.dark_love else R.drawable.love)
        }

        getImage(product.imgName,holder)

        holder.binding.itemCardView.setOnClickListener{ navigateToDetail(product) }
    }


    override fun getItemCount(): Int = products.size

    class ViewHolder(binding: ResultItemBinding) : RecyclerView.ViewHolder(binding.root) {
        // Using ViewBinding for easy access to views
        var binding: ResultItemBinding
        init {
            this.binding = binding
        }
    }
}