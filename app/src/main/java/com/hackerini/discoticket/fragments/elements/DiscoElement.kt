package com.hackerini.discoticket.fragments.elements

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.ActionBar
import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.*
import android.view.animation.DecelerateInterpolator
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.doOnEnd
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hackerini.discoticket.R
import com.hackerini.discoticket.activities.ClubDetails
import com.hackerini.discoticket.objects.Club
import com.hackerini.discoticket.objects.Review
import com.squareup.picasso.Picasso
import java.io.Serializable


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class DiscoElement : Fragment(), GestureDetector.OnGestureListener {
    var club: Club? = null
    private var gestureDetector: GestureDetectorCompat? = null
    private lateinit var card: CardView
    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var removeButton: ImageButton
    private lateinit var topRightRemoveButton: ImageButton
    private var cardIsAnimating = false
    var onRemoveElement: ((Club) -> Unit)? = null
    private var originalCardHeigth = -1
    var isHide: Boolean = false
    private var elementDeletable = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            club = it.getSerializable(ARG_PARAM1) as Club
            elementDeletable = it.getBoolean(ARG_PARAM2)
        }
        if (elementDeletable) {
            //Enable this to enable the gesture to remove the element
            //gestureDetector = GestureDetectorCompat(requireContext(), this)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_disco_element, container, false)
    }

    companion object {
        public val KEY_DELETABLE_SHARED_PREFERENCES = "deletableByX"

        @JvmStatic
        fun newInstance(param1: Serializable, elementDeletable: Boolean = false) =
            DiscoElement().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, param1)
                    putBoolean(ARG_PARAM2, elementDeletable)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        card = view.findViewById(R.id.DiscoElementCard)
        removeButton = view.findViewById(R.id.DiscoElementRemoveIcon)
        constraintLayout = view.findViewById(R.id.DiscoElementLayout)
        topRightRemoveButton = view.findViewById(R.id.discoElementDeleteButton)
        val discoName = view.findViewById<TextView>(R.id.discoElementName)
        val discoAddress = view.findViewById<TextView>(R.id.discoElementAddress)
        val discoRating = view.findViewById<RatingBar>(R.id.discoElementRating)
        val discoRatingAvg = view.findViewById<TextView>(R.id.discoElementReviewAvg)
        val image = view.findViewById<ImageView>(R.id.discoElementImage)
        val discoRatingAmount = view.findViewById<TextView>(R.id.discoElementReviewAmount)
        val labelLayout = view.findViewById<LinearLayout>(R.id.discoElementLabelsLayout)

        val reviews = club!!.reviews
        val average = reviews.sumOf { r: Review -> r.rating } / reviews.size.toFloat()

        discoName.text = club?.name
        discoAddress.text = club?.address
        //discoRating.rating = club?.rating!!
        discoRating.rating = average.toFloat()
        //discoRatingAvg.setText(club?.rating.toString())
        discoRatingAvg.text = String.format("%.1f", average)
        //discoRatingAmount.setText("(" + club?.reviewAmount.toString() + " Recensioni)")
        discoRatingAmount.text = "(${reviews.size} recensioni)"

        val imageSize = 250
        Picasso.get().load(club?.imgUrl).resize(imageSize, imageSize).into(image)

        card.setOnClickListener {
            val intent = Intent(activity, ClubDetails::class.java)
            intent.putExtra("club", club)
            startActivity(intent)
        }

        val params =
            LinearLayout.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT
            )
        params.setMargins(0, 0, 10, 0)

        club?.labels?.forEach { e ->
            val shape = GradientDrawable()
            shape.cornerRadius = 10f
            shape.setColor(Club.getLabelColorFromName(e))

            val textview = TextView(context)
            textview.setText(e)
            textview.background = shape
            textview.setPadding(8)

            textview.layoutParams = params

            labelLayout.addView(textview)
        }

        gestureDetector?.let {
            card.setOnTouchListener { v, event ->
                it.onTouchEvent(event)
            }
        }

        card.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (oldScrollX == 0 && scrollX == 200) cardIsAnimating = false
            if (oldScrollX == 200 && scrollX == 0) cardIsAnimating = false

        }

        removeButton.setOnClickListener {
            onRemoveElement?.invoke(club!!)
        }

        val sharedPref = requireContext().getSharedPreferences("DiscoTicket", Context.MODE_PRIVATE)
        if (elementDeletable && sharedPref.getBoolean(KEY_DELETABLE_SHARED_PREFERENCES, false)) {
            topRightRemoveButton.visibility = View.VISIBLE
            topRightRemoveButton.setOnClickListener {
                val alert = MaterialAlertDialogBuilder(requireContext())
                alert.setTitle("Conferma eliminazione")
                alert.setMessage("Sei sicuro di eliminare ${club!!.name} dai preferiti?")
                alert.setPositiveButton("Si") { dialog, _ ->
                    onRemoveElement?.invoke(club!!)
                    dialog.dismiss()
                }
                alert.setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                alert.create().show()
            }
        } else {
            topRightRemoveButton.visibility = View.GONE
        }
    }

    override fun onDown(e: MotionEvent?): Boolean {
        return true
    }

    override fun onShowPress(e: MotionEvent?) {
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        card.performClick()
        return true
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        val isLeftDirection = distanceX > 0 && e2?.action == MotionEvent.ACTION_MOVE
        if (!cardIsAnimating && isLeftDirection) {
            val offset = removeButton.width.toFloat()
            val cardAnimation = ObjectAnimator.ofFloat(constraintLayout, "translationX", -offset)
            cardAnimation.duration = 200

            val buttonAnimation = ObjectAnimator.ofFloat(removeButton, "translationX", -offset)
            buttonAnimation.duration = 200
            buttonAnimation.start()
            cardAnimation.start()
            return true
        } else if (!cardIsAnimating) {
            val cardAnimation = ObjectAnimator.ofFloat(constraintLayout, "translationX", 0f)
            cardAnimation.duration = 200

            val buttonAnimation = ObjectAnimator.ofFloat(removeButton, "translationX", 0f)
            buttonAnimation.duration = 200
            buttonAnimation.start()
            cardAnimation.start()
            return true
        }
        return false
    }

    override fun onLongPress(e: MotionEvent?) {
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        return true
    }

    fun hide(callback: (Club) -> Unit) {
        if (originalCardHeigth < 0)
            originalCardHeigth = requireView().height
        val valueAnimator = ValueAnimator.ofInt(originalCardHeigth, 0)
        valueAnimator.interpolator = DecelerateInterpolator()
        valueAnimator.addUpdateListener { animation ->
            requireView().layoutParams.height = animation.animatedValue as Int
            requireView().requestLayout()
        }
        valueAnimator.duration = 300
        valueAnimator.doOnEnd {
            callback.invoke(club!!)
        }
        valueAnimator.start()
        isHide = true
    }

    fun show(callback: () -> Unit) {
        constraintLayout.translationX = 0F
        removeButton.translationX = removeButton.width.toFloat()

        val valueAnimator = ValueAnimator.ofInt(0, originalCardHeigth)
        valueAnimator.interpolator = DecelerateInterpolator()
        valueAnimator.addUpdateListener { animation ->
            requireView().layoutParams.height = animation.animatedValue as Int
            requireView().requestLayout()
        }
        valueAnimator.duration = 300
        valueAnimator.doOnEnd {
            callback.invoke()
        }
        valueAnimator.start()
        isHide = false
    }

}