package com.trasim.myapp.screens.issues.fragments.issuesDetailsViewPager.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.trasim.myapp.base.fragment.MyAppFragment

class ScreenSlidePagerAdapter(fm: FragmentManager, val fragments: MutableList<MyAppFragment<*, *, *, *>>) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getCount(): Int = fragments.size

    override fun getItem(position: Int): Fragment = fragments[position]
}