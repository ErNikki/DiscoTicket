package com.hackerini.discoticket.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.hackerini.discoticket.R
import com.hackerini.discoticket.fragments.elements.DiscoElement
import com.hackerini.discoticket.room.RoomManager
import com.hackerini.discoticket.utils.ObjectLoader

class Favourite : Fragment() {

    lateinit var emptyScreenWarning: TextView

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

        emptyScreenWarning = view.findViewById(R.id.FavoriteClubEmptyWarning)

    }

    override fun onResume() {
        super.onResume()
        val favClubs = RoomManager(requireContext()).db.favoriteDao().getAll()
        view?.findViewById<LinearLayout>(R.id.FavoriteLinearLayout)?.removeAllViews()
        if (favClubs.isEmpty()) {
            emptyScreenWarning.visibility = View.VISIBLE
        }
        if (favClubs.isNotEmpty()) {
            emptyScreenWarning.visibility = View.GONE
            val ft = parentFragmentManager.beginTransaction()
            val clubs = ObjectLoader.getClubs(requireContext())
            clubs.forEach { club ->
                if (favClubs.map { e -> e.id }.contains(club.id)) {
                    ft.add(R.id.FavoriteLinearLayout, DiscoElement.newInstance(club))
                }
            }
            ft.commit()
        }
    }
}