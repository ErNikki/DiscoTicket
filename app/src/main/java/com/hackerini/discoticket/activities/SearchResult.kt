package com.hackerini.discoticket.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.hackerini.discoticket.R
import com.hackerini.discoticket.adapters.SearchResultAdapter

val tabsElement = arrayOf(
    "Tutto",
    "Discoteche",
    "Eventi"
)

class SearchResult : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)

        val tabLayout = findViewById<TabLayout>(R.id.searchResultTabLayout)
        val viewerPage2 = findViewById<ViewPager2>(R.id.searchResultViewPager)
        val adapter = SearchResultAdapter(supportFragmentManager, lifecycle)

        viewerPage2.adapter = adapter

        TabLayoutMediator(tabLayout, viewerPage2) { tab, position ->
            tab.text = tabsElement[position]
        }.attach()
    }


}