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
import java.util.*

class Favourite : Fragment() {

    //Set this variable to false to disable the delete gesture
    private val ENABLE_GESTURE = true

    lateinit var emptyScreenWarning: TextView
    lateinit var linearLayout: LinearLayout
    private var fragmentList: List<DiscoElement> = LinkedList()
    private var runOnResume = false

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
        loadContent()

        /*
        Alert shown that explain the use of gesture, not needed for now
        val VALUE_KEY = "favoriteGesture"
        val sharedPreferences =
            requireContext().getSharedPreferences("DiscoTicket", Context.MODE_PRIVATE)
        val hideDialog = sharedPreferences.getBoolean(VALUE_KEY, true)
        if (hideDialog && fragmentList.isNotEmpty() && ENABLE_GESTURE) {
            val dialog = MaterialAlertDialogBuilder(requireContext())
            dialog.setTitle("Aiuto")
            dialog.setMessage("Trascina gli elementi verso sinistra per eliminarli dai preferiti")
            dialog.setPositiveButton("Ho capito") { dialog, _ ->
                sharedPreferences.edit().apply {
                    putBoolean(VALUE_KEY, false)
                }.apply()
                dialog.dismiss()
            }
            dialog.create().show()
        }
         */
    }

    override fun onResume() {
        super.onResume()

        //In the first run loadContent() must be called only from onViewCreated
        if (runOnResume) {
            linearLayout.removeAllViews()
            loadContent()
        }
        runOnResume = true
    }

    private fun loadContent() {
        fragmentList = getFavoriteClubs()
        updateWarningTextStatus()
        if (fragmentList.isNotEmpty()) {
            val transaction = parentFragmentManager.beginTransaction()
            fragmentList.forEach {
                transaction.add(linearLayout.id, it)
            }
            transaction.commit()
        }
    }

    private fun getFavoriteClubs(): List<DiscoElement> {
        val favClubs = RoomManager(requireContext()).db.favoriteDao().getAll()
        val favClubsIds = favClubs.map { e -> e.id }
        return ObjectLoader.getClubs(requireContext())
            .filter { club -> favClubsIds.contains(club.id) }
            .map { club ->
                DiscoElement.newInstance(club, ENABLE_GESTURE).apply {
                    onRemoveElement = { c -> removeElement(this) }
                }
            }.toMutableList()
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