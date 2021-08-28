package com.crosie.ezpath2.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.crosie.ezpath2.fragment.ErrandFragment
import com.crosie.ezpath2.fragment.MapFragment

class FragmentPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        when(position) {
            0 -> return ErrandFragment()
            1 -> return MapFragment()
        }
        return ErrandFragment()
    }

}