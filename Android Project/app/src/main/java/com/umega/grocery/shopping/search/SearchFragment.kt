package com.umega.grocery.shopping.search

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.umega.grocery.R
import com.umega.grocery.dataBase.Repo
import com.umega.grocery.databinding.SearchPageBinding
import com.umega.grocery.shopping.adapters.SearchAdapter

class SearchFragment : Fragment() {
    lateinit var binding : SearchPageBinding
    lateinit var viewModel: SearchViewModel
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.search_page,container,false)

        viewModel = SearchViewModel(findNavController())
        binding.viewModel = viewModel
        viewModel.setRepo(Repo(requireContext()))

        //navigating to result
        viewModel.ids.observe(viewLifecycleOwner){
            if (viewModel.searchText.value != "")
                viewModel.afterSearchCallback()
            else
                viewModel.searchText.value = ""
        }
        viewModel.searchText.observe(viewLifecycleOwner){
            if (it != "")
            //search for keyWords
                viewModel.updateTexts()
            //TODO Test the above function
        }

        viewModel.searchResults.observe(viewLifecycleOwner){

            binding.noResultImage.visibility =
                if (it.size == 0) View.VISIBLE else View.INVISIBLE

            //update the adapter
            binding.searchItemsListRecycleView.adapter = SearchAdapter(requireContext(), it, viewModel::searchTextItem)
            binding.searchItemsListRecycleView.layoutManager =  LinearLayoutManager(requireContext())

        }

        //searching when pressing enter
        binding.searchText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                (event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER)
            ) {
                //hiding keyBoard
                hideKeyboard(binding.searchText)
                //Searching for products
                viewModel.search()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        return binding.root
    }

    private fun hideKeyboard(view: View) {
        val imm = view.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}