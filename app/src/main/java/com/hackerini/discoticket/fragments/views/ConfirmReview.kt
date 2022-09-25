package com.hackerini.discoticket.fragments.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.hackerini.discoticket.R
import com.hackerini.discoticket.fragments.elements.ReviewElement
import com.hackerini.discoticket.objects.Review

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"

class ConfirmReview : DialogFragment() {
    private var review: Review? = null
    private lateinit var okButton: Button
    private lateinit var cancelButton: Button
    var onConfirmAction: ((Review) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            review = it.getSerializable(ARG_PARAM1) as Review
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_confirm_review, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val transaction = childFragmentManager.beginTransaction()
        transaction.add(R.id.ConfirmReviewFrameLayout, ReviewElement.newInstance(review!!))
        transaction.commit()
        okButton = view.findViewById(R.id.ConfirmReviewOk)
        cancelButton = view.findViewById(R.id.ConfirmReviewCancel)

        cancelButton.setOnClickListener { dismiss() }
        okButton.setOnClickListener { onConfirmAction?.invoke(review!!) }
    }

    companion object {
        @JvmStatic
        fun newInstance(review: Review) =
            ConfirmReview().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, review)
                }
            }
    }
}