package com.hackerini.discoticket.fragments.elements

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.hackerini.discoticket.R
import com.hackerini.discoticket.objects.Discount

private const val ARG_PARAM1 = "param1"

class CouponElement : Fragment() {
    private var discount: Discount? = null
    var onButtonClick: ((Discount) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            discount = it.getSerializable(ARG_PARAM1) as Discount?
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_coupon_element, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val couponName = view.findViewById<TextView>(R.id.CouponElementName)
        couponName.text = discount?.name
        view.findViewById<ImageButton>(R.id.CouponElementImageButton).setOnClickListener {
            discount?.let { it1 -> onButtonClick?.invoke(it1) }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(discount: Discount) =
            CouponElement().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, discount)
                }
            }
    }
}