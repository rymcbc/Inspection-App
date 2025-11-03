package com.qaassist.inspection.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.qaassist.inspection.fragments.HistoryFragment
import com.qaassist.inspection.fragments.InspectionFragment

class MainPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    
    override fun getItemCount(): Int = 2
    
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> InspectionFragment()
            1 -> HistoryFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}