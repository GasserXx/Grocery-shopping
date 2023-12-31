package com.umega.grocery.utill

import android.os.Parcel
import android.os.Parcelable

data class Product(
    val id:Int,
    val name:String,
    val brandID:Int,
    val price:Double,
    val stockQuantity:Int,
    val subCategoryID: Int,
    val purchaseCount:Int,
    val imgName:String,
    var discount:Double = 0.0
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readDouble(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readDouble()
    )
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeInt(brandID)
        parcel.writeDouble(price)
        parcel.writeInt(stockQuantity)
        parcel.writeInt(subCategoryID)
        parcel.writeInt(purchaseCount)
        parcel.writeString(imgName)
        parcel.writeDouble(discount)
    }
    override fun describeContents(): Int {
        return 0
    }
    companion object CREATOR : Parcelable.Creator<Product> {
        override fun createFromParcel(parcel: Parcel): Product {
            return Product(parcel)
        }
        override fun newArray(size: Int): Array<Product?> {
            return arrayOfNulls(size)
        }
    }
}
