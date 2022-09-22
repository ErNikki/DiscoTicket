package com.hackerini.discoticket.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.hackerini.discoticket.R
import com.hackerini.discoticket.fragments.elements.DiscoElement
import com.hackerini.discoticket.room.RoomManager
import com.hackerini.discoticket.utils.ObjectLoader

class Favourite : Fragment() {

    lateinit var emptyScreenWarning: TextView
    lateinit var linearLayout: LinearLayout
    private var fragmentList: MutableList<DiscoElement> = mutableListOf()

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

        linearLayout = view.findViewById(R.id.FavoriteLinearLayout)
        emptyScreenWarning = view.findViewById(R.id.FavoriteClubEmptyWarning)
    }

    override fun onResume() {
        super.onResume()
        linearLayout.removeAllViews()
        loadContent()
    }

    private fun loadContent() {
        val favClubs = RoomManager(requireContext()).db.favoriteDao().getAll()
        val favClubsIds = favClubs.map { e -> e.id }
        fragmentList = ObjectLoader.getClubs(requireContext())
            .filter { club -> favClubsIds.contains(club.id) }
            .map { club ->
                DiscoElement.newInstance(club, true).apply {
                    onRemoveElement = { c -> removeElement(this) }
                }
            }.toMutableList()

        updateWarningTextStatus()
        if (fragmentList.isNotEmpty()) {
            val transaction = parentFragmentManager.beginTransaction()
            fragmentList.forEach {
                transaction.add(linearLayout.id, it)
            }
            transaction.commit()
        }
    }

    private fun removeElement(element: DiscoElement) {
        //Element are not removed really when deleted, but they are only hidden, in this way is easier
        //to recover a fragment after the delete
        element.hide { it.removeToFavorite(requireContext()) }
        updateWarningTextStatus()

        Snackbar.make(
            requireView().findViewById(R.id.FavoriteConstraintLayout),
            element.club?.name + " eliminato dai preferiti",
            Snackbar.LENGTH_SHORT
        ).setAction("Annulla") {
            element.show { element.club?.addToFavorite(requireContext()) }
            updateWarningTextStatus()
        }
            .show()
    }

    private fun updateWarningTextStatus() {
        if (fragmentList.all { e -> e.isHide }) {
            emptyScreenWarning.visibility = View.VISIBLE

        } else emptyScreenWarning.visibility = View.GONE
    }
}