package com.umega.grocery.shopping.main

import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.umega.grocery.databinding.HomePageContainerBinding
import com.umega.grocery.R
import com.umega.grocery.shopping.HomeViewModel
import com.umega.grocery.shopping.HomeViewModelFactory
import com.umega.grocery.shopping.adapters.MainMenuAdapter

class MainPageContainer : Fragment() {

    private lateinit var icons :MutableList<Drawable?>
    private lateinit var binding: HomePageContainerBinding
    private lateinit var drawableArray: TypedArray
    private lateinit var viewPager: ViewPager2
    private lateinit var fragments: List<Fragment>
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var tabLayout: TabLayout

    private val navController by lazy { findNavController() }
    private val viewModel: HomeViewModel by activityViewModels { HomeViewModelFactory(navController,requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //init variables
        fragments = listOf(HomeFragment(), CartFragment(), FavoriteFragment(), ProfileFragment())
        icons = mutableListOf()

        binding = HomePageContainerBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel

        viewPager = binding.viewpager
        tabLayout = binding.tabLayout

        val supportFragment = this.childFragmentManager

        binding.sideListItems.adapter = MainMenuAdapter(resources = resources, navigateOnClick = viewModel::mainListCallBack)
        binding.sideListItems.layoutManager =  LinearLayoutManager(requireContext())

        viewPagerAdapter = ViewPagerAdapter(supportFragment,lifecycle)
        viewPager.adapter = viewPagerAdapter

        drawableArray = resources.obtainTypedArray(R.array.drawable_array);

        //assigning the static list of icons
        for (i in 0 until drawableArray.length()) {
            val drawable = drawableArray.getDrawable(i)
            icons.add(drawable)
        }

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            // Set tab text or icon here if needed
            tab.icon = icons[position]
            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    tab.icon?.setTint(Color.WHITE)
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {
                    tab.icon?.setTint(Color.DKGRAY)
                }

                override fun onTabReselected(tab: TabLayout.Tab) {
                    tab.icon?.setTint(Color.DKGRAY)
                }
            })

        }.attach()

        drawableArray.recycle()

        //handle sidebar visibility
        viewModel.sideItemVisible.observe(viewLifecycleOwner){
            binding.sideListLayout.visibility =
                if(it) View.VISIBLE else View.GONE
        }

        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding.unbind()
    }

    class ViewPagerAdapter(private val fragmentManager: FragmentManager, private val lifecycle: Lifecycle) :
        FragmentStateAdapter(fragmentManager, lifecycle) {

        override fun getItemCount(): Int = 4

        override fun createFragment(position: Int): Fragment = when (position){
                0-> HomeFragment()
                1-> CartFragment()
                2-> FavoriteFragment()
                3-> ProfileFragment()
            else -> Fragment()
        }
    }
}