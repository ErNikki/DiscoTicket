package com.hackerini.discoticket.adapters

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hackerini.discoticket.fragments.ElementToShow
import com.hackerini.discoticket.fragments.SearchResultList

class SearchResultAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {

        Log.d("CALLING FROM POS", position.toString())
        when (position) {
            0 -> return SearchResultList.newInstance(ElementToShow.ALL)
            1 -> return SearchResultList.newInstance(ElementToShow.CLUBS)
        }
        return SearchResultList.newInstance(ElementToShow.EVENTS)
    }
}
