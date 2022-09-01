package com.hackerini.discoticket

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.hackerini.discoticket.objects.Club

class SearchResult : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)

        val clubList = ArrayList<Club>()
        val club0 = Club()
        val club1 = Club()
        val club2 = Club()
        club0.name="DISCO 1"
        club0.rating=4.3F
        club0.address="Via della prova"
        club1.name="DISCO 2"
        club1.rating=4.0F
        club1.address="Via della vigna"
        club2.name="DISCO 3"
        club2.rating=3.3F
        club2.address="Via della roma"
        clubList.add(club0)
        clubList.add(club1)
        clubList.add(club2)

        val ft = supportFragmentManager.beginTransaction()
        for (e in clubList) {
            ft.add(R.id.SearchResultLinearLayout, DiscoElement.newInstance(e))
        }
        ft.commit()
    }
}