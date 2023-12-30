package com.umega.grocery.shopping.result.fragments

import ImageHandle
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.umega.grocery.R
import com.umega.grocery.dataBase.Repo
import com.umega.grocery.databinding.ResultPageContainerBinding
import com.umega.grocery.shopping.result.ResultViewModel
import com.umega.grocery.shopping.result.ResultViewModelFactory
import kotlinx.coroutines.runBlocking
import java.util.ArrayList

class ResultContainer : Fragment() {
    lateinit var binding : ResultPageContainerBinding

    private lateinit var title : String
    private var productsIds : ArrayList<Int>? = null

    private lateinit var navController: NavController
    private val viewModel: ResultViewModel by viewModels { ResultViewModelFactory(navController) }

    private lateinit var filterFragment :FilterFragment
    private lateinit var resultFragment :ResultFragment

    // Inside your destination fragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Assigning the nav Controller object to activity navController
        navController = findNavController()

        // Retrieve arguments
        val args = arguments
        if (args != null) {
            val pageTitle = args.getString(title_bundle_key, "")
            val ids: ArrayList<Int>? = args.getIntegerArrayList(ids_bundle_key)
            title = pageTitle
            productsIds = ids
        }
        else
        {
            title = "error"
            productsIds = ArrayList()
        }

        //fragments init
        filterFragment = FilterFragment(viewModel)
        resultFragment = ResultFragment(viewModel)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.result_page_container,container,false)
        binding.viewModel = viewModel
        //init
        viewModel.setRepoValue(Repo(requireContext()))
        viewModel.initialization(title,productsIds)

        // Example: Replace Result with Filter on a button click
        viewModel.filterFlag.observe(viewLifecycleOwner) {
            if (it)
            childFragmentManager.beginTransaction()
                .replace(R.id.container_layout, filterFragment)
                .addToBackStack(null)
                .commit()
            else
                childFragmentManager.beginTransaction()
                    .replace(R.id.container_layout, resultFragment)
                    .commit()
        }

        return binding.root
    }
    companion object{
        const val title_bundle_key = "title_string"
        const val ids_bundle_key = "products_ids"
    }
}