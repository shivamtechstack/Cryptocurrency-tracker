package com.sycodes.cryptotrack.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.sycodes.cryptotrack.fragments.CryptoFragment
import com.sycodes.cryptotrack.fragments.FavouriteCryptoFragment

class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> CryptoFragment()
                1 -> FavouriteCryptoFragment()
                else -> throw IllegalStateException("Unexpected position $position")
            }
        }
}