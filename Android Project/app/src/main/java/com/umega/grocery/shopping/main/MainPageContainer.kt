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
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.umega.grocery.databinding.HomePageContainerBinding
import com.umega.grocery.R


class MainPageContainer : Fragment() {

    private lateinit var icons :MutableList<Drawable?>
    private lateinit var binding: HomePageContainerBinding
    private lateinit var drawableArray: TypedArray
    private lateinit var view: View
    private lateinit var viewPager: ViewPager2
    private lateinit var fragments: List<Fragment>
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //init variables
        fragments = listOf(HomeFragment(), CartFragment(), FavoriteFragment(), ProfileFragment())
        icons = mutableListOf()

        binding = HomePageContainerBinding.inflate(inflater, container, false)
        view = binding.root

        viewPager = view.findViewById(R.id.viewpager)
        tabLayout = view.findViewById(R.id.tab_layout)

        val supportFragment = this.childFragmentManager



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
        return view
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