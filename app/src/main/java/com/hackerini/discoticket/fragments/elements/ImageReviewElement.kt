package com.hackerini.discoticket.fragments.elements

import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.hackerini.discoticket.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ImageReviewElement.newInstance] factory method to
 * create an instance of this fragment.
 */
class ImageReviewElement : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: Int?= null
    var getBitmap: ((Int) -> Bitmap)? = null
    var deleteBitmap: ((Int)->Unit)? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getInt(ARG_PARAM1)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image_review, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val image= getBitmap?.let { param1?.let { it1 -> it(it1) } }
        val imageView=view.findViewById<ImageView>(R.id.fragmentImageReviewImage)
        val deleteButton=view.findViewById<ImageView>(R.id.fragmentImageReviewDeleteButton)

        imageView.setImageBitmap(param1?.let { getBitmap?.let { it1 -> it1(it) } })
        deleteButton.setOnClickListener{ deleteBitmap?.let { it1 -> param1?.let { it2 -> it1(it2) } } }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment imageReview.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: Int) =
            ImageReviewElement().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, param1)
                }
            }
    }
}