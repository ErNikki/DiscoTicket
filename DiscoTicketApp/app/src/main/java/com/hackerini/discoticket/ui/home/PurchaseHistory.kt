package com.hackerini.discoticket.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.hackerini.discoticket.R
import com.hackerini.discoticket.fragments.elements.PurchaseElement
import com.hackerini.discoticket.objects.User
import com.hackerini.discoticket.room.RoomManager
import com.hackerini.discoticket.utils.OrderManager
import com.hackerini.discoticket.utils.UserManager

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

        //val orderDao = RoomManager(requireContext()).db.orderDao()
        //val orders = orderDao.getAllOrderWithOrderItem()
        if (User.isLogged(requireContext())) {
            val orders=OrderManager.getOrders(UserManager.getUser())
            //Log.d("purchaseHisoty",orders.first().toString())
            if(orders.isNotEmpty()) {
                view.findViewById<TextView>(R.id.PurchaseHystoryNoPurchaseWarning).visibility =
                    View.GONE
                val transaction = parentFragmentManager.beginTransaction()
                orders.forEach { a ->
                    transaction.add(
                        R.id.PurchaseHistoryLinearLayout,
                        PurchaseElement.newInstance(a)
                    )
                }
                transaction.commit()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            PurchaseHistory()
    }
}