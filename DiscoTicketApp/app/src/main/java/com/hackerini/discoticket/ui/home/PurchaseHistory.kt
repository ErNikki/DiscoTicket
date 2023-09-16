package com.hackerini.discoticket.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.hackerini.discoticket.R
import com.hackerini.discoticket.fragments.elements.PurchaseElement
import com.hackerini.discoticket.objects.User
import com.hackerini.discoticket.room.RoomManager
import com.hackerini.discoticket.utils.OrderManager
import com.hackerini.discoticket.utils.UserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.log

class PurchaseHistory : Fragment() {

    lateinit var scrollView: ScrollView
    lateinit var progressBar: ProgressBar
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
        progressBar = view.findViewById(R.id.PurchaseHistoryProgressBar)
        scrollView= view.findViewById(R.id.PurchaseHistoryScrollView)

        CoroutineScope(Dispatchers.Default).launch {
            if (User.isLogged(requireContext())) {
                val orders=OrderManager.getOrders(UserManager.getUser())

                if(isAdded) {
                    requireActivity().runOnUiThread {

                        if (orders.isNotEmpty()) {
                            view.findViewById<TextView>(R.id.PurchaseHystoryNoPurchaseWarning).visibility =
                                View.GONE
                            //if (isAdded) {
                                val transaction = parentFragmentManager.beginTransaction()
                                orders.forEach { a ->
                                    transaction.add(
                                        R.id.PurchaseHistoryLinearLayout,
                                        PurchaseElement.newInstance(a)
                                    )
                                }
                                scrollView.visibility = View.VISIBLE
                                progressBar.visibility = View.GONE
                                transaction.commit()
                            //}

                        } else {
                            //if (isAdded) {
                                scrollView.visibility = View.VISIBLE
                                progressBar.visibility = View.GONE
                            //}
                        }
                    }
                }

            }
            else{
                if(isAdded) {
                    requireActivity().runOnUiThread {
                        //if (isAdded) {
                            scrollView.visibility = View.VISIBLE
                            progressBar.visibility = View.GONE
                        //}
                    }
                }
            }
        }

    }

    companion object {
        @JvmStatic
        fun newInstance() =
            PurchaseHistory()
    }
}