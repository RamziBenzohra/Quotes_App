package com.app.quote.app.quotesapp.adapters

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v4.widget.SwipeRefreshLayout


class FragmentsAdapter(var fragmentsManager:FragmentManager,var fragments:ArrayList<Fragment>):FragmentPagerAdapter(fragmentsManager) {
    override fun getItem(p0: Int): Fragment {

        return fragments[p0]
    }
    override fun getCount(): Int {
        return fragments.size
    }
    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }


}