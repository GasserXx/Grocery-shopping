package com.umega.grocery.utill

data class Filter(
    var verbalSort:VerbalSortingType? = null,
    val brand : List<Brand>? = null,
    val priceRange: PriceRange? = null
)
