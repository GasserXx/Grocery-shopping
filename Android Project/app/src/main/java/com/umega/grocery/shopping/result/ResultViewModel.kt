package com.umega.grocery.shopping.result

import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.umega.grocery.R
import com.umega.grocery.dataBase.Repo
import com.umega.grocery.utill.Brand
import com.umega.grocery.utill.Filter
import com.umega.grocery.utill.Product
import com.umega.grocery.utill.SubCategory
import com.umega.grocery.utill.VerbalSortingType
import kotlinx.coroutines.runBlocking

class ResultViewModel(private val navController: NavController) : ViewModel() {
    //we only get 50 products
    val products = MutableLiveData<MutableList<Product>>()
    //we get all ids
    private val productsIDs = mutableListOf<Int>()
    var title = "title"

    private val _filterFlag = MutableLiveData(false)
    val filterFlag: LiveData<Boolean>
        get() = _filterFlag

    private val _pageNumber = MutableLiveData(1)
    val pageNumber: String
        get() = _pageNumber.value.toString()

    private var repo: Repo? = null

    private val _loading: MutableLiveData<Boolean> = MutableLiveData(false)
    val loading: LiveData<Boolean>
        get() = _loading

    private val _nextButtonVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    val nextButtonVisible:Int
            get() = if (_nextButtonVisible.value!!) VISIBLE else GONE

    private val _prevButtonVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    val prevButtonVisible: Int
        get() = if (_prevButtonVisible.value!!) VISIBLE else GONE


    //filter variables
    private var filter:Filter = Filter()
    var nationalities :Set<String> = setOf()
    var brands :Set<Brand> = setOf()
    var subNationalities :MutableList<String> = mutableListOf()
    private var subBrands :MutableList<Brand> = mutableListOf()
    var initPriceRange :Double = 0.00
    var maxPriceRange :Double = 99999.00

    //Value Setters
    //setting repo on launch
    fun setRepoValue(repoObject: Repo){
        repo = repoObject
        runBlocking {
            repo!!.refreshBrands()
        }
    }
    private fun setTitleName(titleName:String){
        title = titleName
    }
    private fun setProductsIds(ids:ArrayList<Int>?){
        if (!ids.isNullOrEmpty())
            productsIDs.addAll(ids)
    }

    fun initialization(title:String, ids:ArrayList<Int>?) {

        Log.i("LOL","Calling INITIALIZATION")
        //init values
        setTitleName(title)
        setProductsIds(ids)
        buttonsVisibilityCheck()
        refresh()
    }

    private fun refresh(){
        showLoading()
        //updating filter nationality
        updateBrandBasedOnNationality()
        //Request 50 items if more than 50s
        requestItems()
    }
    fun incrementPage(){
        _pageNumber.value = _pageNumber.value?.plus(1)
    }
    private fun requestItems() {
        if (productsIDs.size == 0)
            return
        val pageNumber = _pageNumber.value!!
        val initPointer = (pageNumber - 1 )* 50
        val finalPointer = if ((pageNumber)* 50 < productsIDs.size) (pageNumber) * 50 - 1 else productsIDs.size - 1
        val productsIdsToBeRequested:MutableList<Int> = productsIDs.subList(initPointer,finalPointer+1)
        //no request the previous products from the repo
        repo!!.retrieveProducts(productsIdsToBeRequested, filter, products)
    }

    //FILTER PAGE FNs
    fun addSubBrand(brand: Brand){
        subBrands.add(brand)
    }
    fun removeSubBrand(brand: Brand){
        subBrands.remove(brand)
    }
    fun addSubNationality(nationality: String){
        subNationalities.add(nationality)
    }
    fun removeSubNationality(nationality: String){
        subNationalities.remove(nationality)
    }
    fun ascendingOrder(){
        filter.verbalSort = VerbalSortingType.Ascending
    }
    fun descendingOrder(){
        filter.verbalSort = VerbalSortingType.Descending
    }
    fun setBrandsList(brandsId:MutableList<Int>){
        brands = repo!!.getBrandById(brandsId.toMutableList()).toSet()
        nationalities = brands.map { it.nationality }.toMutableList().toSet()
    }

    //nationality liveData fn
    private fun updateBrandBasedOnNationality(){
        subBrands.clear()
        brands.forEach { if(it.nationality !in subNationalities) subBrands.add(it)}
    }

    //mapping
    fun nextPage(){
        _pageNumber.value!!.inc()
        buttonsVisibilityCheck()
    }
    fun previousPage(){
        _pageNumber.value!!.dec()
        buttonsVisibilityCheck()
    }
    private fun buttonsVisibilityCheck(){
        _nextButtonVisible.value =
            _pageNumber.value!! * 50 < productsIDs.size
        _prevButtonVisible.value =
            _pageNumber.value!! != 1
    }

    //Loading functions
    private fun showLoading() {
        _loading.value = true
    }

    fun hideLoading() {
        _loading.value = false
    }


    //navigation
    fun showFilter(){
        _filterFlag.value = true
    }
    fun hideFilter(){
        _filterFlag.value = false
        refresh()
    }
    fun navigateUp(){
        navController.navigateUp()
    }
    fun navigateToDetail(product: Product){
        val bundle = Bundle().apply {
            putInt("productId", product.id)
            putString("productName", product.name)
            putDouble("productName", product.price)
            putString("productName", product.imgName)
            putInt("productName", product.brandID)
            putDouble("productName", product.discount)
            putInt("productName", product.purchaseCount)
            putInt("productName", product.stockQuantity)
            putInt("productName", product.subCategoryID)
        }
        navController.navigate(R.id.action_resultContainer_to_detailItemFragment,bundle)
    }

}