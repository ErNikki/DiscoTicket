package com.hackerini.discoticket.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hackerini.discoticket.R
import com.hackerini.discoticket.fragments.elements.PurchaseElement
import com.hackerini.discoticket.room.RoomManager

class PurchaseHistory : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_purchase_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val transaction = parentFragmentManager.beginTransaction()
        val orderDao = RoomManager(requireContext()).db.orderDao()
        orderDao.getAllOrderWithOrderItem().forEach { a ->
            transaction.add(R.id.PurchaseHistoryLinearLayout, PurchaseElement.newInstance(a))
        }
        transaction.commit()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            PurchaseHistory()
    }
}