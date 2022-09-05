package com.hackerini.discoticket.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.hackerini.discoticket.R
import com.hackerini.discoticket.activities.ClubDetails
import com.hackerini.discoticket.fragments.elements.DiscoElement
import com.hackerini.discoticket.room.RoomManager
import com.hackerini.discoticket.utils.ObjectLoader

class Favourite : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favourite, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            Favourite()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onResume() {
        super.onResume()

        val favClubs = RoomManager(requireContext()).db.favoriteDao().getAll()

        val ft = parentFragmentManager.beginTransaction()
        view?.findViewById<LinearLayout>(R.id.FavoriteLinearLayout)?.removeAllViews()
        val clubs = ObjectLoader.getClubs(requireContext())
        clubs.forEach { club ->
            if (favClubs.map { e->e.id }.contains(club.id)) {
                ft.add(R.id.FavoriteLinearLayout, DiscoElement.newInstance(club))
            }
        }
        ft.commit()
    }
}