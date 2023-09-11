package com.hackerini.discoticket.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.hackerini.discoticket.R
import com.hackerini.discoticket.fragments.elements.CouponElement
import com.hackerini.discoticket.fragments.elements.DiscoElement
import com.hackerini.discoticket.objects.Discount
import com.hackerini.discoticket.objects.TypeOfDiscount
import com.hackerini.discoticket.objects.User
import com.hackerini.discoticket.room.RoomManager
import java.lang.Integer.min

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class MyCoupon : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    lateinit var username: TextView
    lateinit var pointsRecup: TextView
    lateinit var pointExplain: TextView
    lateinit var convertButton: Button
    lateinit var linearLayout: LinearLayout
    lateinit var convertPointExpandButton: ImageButton
    lateinit var convertPointConstraintLayout: ConstraintLayout
    lateinit var enterManualExpandButton: ImageButton
    lateinit var enterManualConstraintLayout: ConstraintLayout
    lateinit var manualCodeEditText: EditText
    lateinit var enterManualWrongMessage: TextView
    lateinit var enterManualConfirmButton: Button

    var valueOfConvertedPoints: Float = 0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        valueOfConvertedPoints =
            resources.getInteger(R.integer.pointToEuroThreshold) *
                    ResourcesCompat.getFloat(resources, R.dimen.pointToEuro)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_coupon, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val notLoggedWarning = view.findViewById<TextView>(R.id.MyCouponUserNotLogged)
        val constraintLayout = view.findViewById<ConstraintLayout>(R.id.MyCouponConstraintLayout)
        username = view.findViewById(R.id.MyCouponUserName)
        pointsRecup = view.findViewById(R.id.MyCouponPointRecup)
        pointExplain = view.findViewById(R.id.MyCouponPointExplain)
        convertButton = view.findViewById(R.id.MyCouponPointConvertButton)
        linearLayout = view.findViewById(R.id.MyCouponCouponsLinearLayout)
        convertPointExpandButton = view.findViewById(R.id.MyCouponExpandPointConvert)
        convertPointConstraintLayout = view.findViewById(R.id.MyCouponExpandPointCLayout)
        enterManualExpandButton = view.findViewById(R.id.MyCouponExpandManual)
        enterManualConstraintLayout = view.findViewById(R.id.MyCouponExpandManualCLayout)
        manualCodeEditText = view.findViewById(R.id.MyCouponInsertedCode)
        enterManualWrongMessage = view.findViewById(R.id.MyCouponWrongCode)
        enterManualConfirmButton = view.findViewById(R.id.MyCouponConvertCode)

        if (User.isLogged(requireContext())) {
            notLoggedWarning.visibility = View.GONE
            constraintLayout.visibility = View.VISIBLE
            convertButton.setOnClickListener { convertPoints() }
            convertPointExpandButton.setOnClickListener { toogleConvertCardView() }
            enterManualExpandButton.setOnClickListener { toogleManualCardView() }
            enterManualConfirmButton.setOnClickListener { convertCode() }
            manualCodeEditText.addTextChangedListener { watcher ->
                if (watcher != null) {
                    enterManualConfirmButton.isEnabled = (watcher.length > 5)
                }
            }
            updateValues()
        } else {
            notLoggedWarning.visibility = View.VISIBLE
            constraintLayout.visibility = View.GONE
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MyCoupon().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun updateValues() {
        val user = User.getLoggedUser(requireContext())
        username.text = user!!.name + " " + user!!.surname
        pointsRecup.text = HtmlCompat.fromHtml(
            "Hai un totale di <b>${user!!.points}</b> punti",
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )
        pointExplain.text =
            "Ogni ${resources.getInteger(R.integer.pointToEuroThreshold)} punti accumulati hai diritto a ricevere un buono di "
                .plus(String.format("%.2f", valueOfConvertedPoints))
                .plus("€")
        convertButton.isEnabled =
            user.points > resources.getInteger(R.integer.pointToEuroThreshold)

        //Fill linear layout with all discounts
        val userWithDiscount =
            RoomManager(requireContext()).db.userDao().getUserDiscount(user.id).first()
        linearLayout.removeAllViews()
        val transaction = parentFragmentManager.beginTransaction()
        userWithDiscount.items.sortedByDescending { it.id }.forEach { item ->
            val fragment = CouponElement.newInstance(item)
            fragment.onButtonClick = onDelete
            transaction.add(linearLayout.id, fragment)
        }
        transaction.commit()
    }

    fun toogleConvertCardView() {
        if (convertPointConstraintLayout.visibility == View.GONE) {
            convertPointConstraintLayout.visibility = View.VISIBLE
            enterManualConstraintLayout.visibility = View.GONE
            convertPointExpandButton.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24)
        } else {
            convertPointConstraintLayout.visibility = View.GONE
            convertPointExpandButton.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24)
        }
    }

    fun toogleManualCardView() {
        if (enterManualConstraintLayout.visibility == View.GONE) {
            enterManualConstraintLayout.visibility = View.VISIBLE
            convertPointConstraintLayout.visibility = View.GONE
            enterManualExpandButton.setImageResource(R.drawable.ic_baseline_arrow_drop_up_24)
        } else {
            enterManualConstraintLayout.visibility = View.GONE
            enterManualExpandButton.setImageResource(R.drawable.ic_baseline_arrow_drop_down_24)

        }
    }

    fun convertCode() {
        val insertedCode = manualCodeEditText.text.toString()
        val discountDao = RoomManager(requireContext()).db.discountDao()
        val user = User.getLoggedUser(requireContext())

        if (insertedCode.contains("[a-z]?sconto[0-9]+".toRegex())) {
            //Ok
            val amount = min(20, insertedCode.filter { it.isDigit() }.toInt())
            val discount = Discount(
                "Sconto del ${amount}%",
                amount.toFloat() / 100,
                TypeOfDiscount.Percentage,
                user!!.id
            )
            enterManualWrongMessage.visibility = View.GONE
            discountDao.insert(discount)
            manualCodeEditText.text.clear()
            toogleManualCardView()
            updateValues()
        } else if (insertedCode.contains("drink")) {
            val discount = Discount(
                "Un drink gratis",
                1F,
                TypeOfDiscount.FreeDrink,
                user!!.id
            )
            enterManualWrongMessage.visibility = View.GONE
            discountDao.insert(discount)
            manualCodeEditText.text.clear()
            toogleManualCardView()
            updateValues()
        } else if (insertedCode.contains("showx")) {
            val sharedPreferences =
                requireContext().getSharedPreferences("DiscoTicket", Context.MODE_PRIVATE)
            val oldValue =
                sharedPreferences.getBoolean(DiscoElement.KEY_DELETABLE_SHARED_PREFERENCES, false)
            val newValue = !oldValue
            sharedPreferences.edit().apply {
                putBoolean(DiscoElement.KEY_DELETABLE_SHARED_PREFERENCES, newValue)
                apply()
            }
            var confirmMessage = "Tasto eliminazione "
            if (newValue)
                confirmMessage = confirmMessage.plus("aggiunto ai preferiti")
            else
                confirmMessage = confirmMessage.plus("eliminato dai preferiti")

            Toast.makeText(
                requireContext(),
                confirmMessage,
                Toast.LENGTH_LONG
            ).show()
            enterManualWrongMessage.visibility = View.GONE
            manualCodeEditText.text.clear()
        } else {
            enterManualWrongMessage.visibility = View.VISIBLE
        }
    }

    private val onDelete: (discount: Discount) -> Unit = {
        val discountDao = RoomManager(requireContext()).db.discountDao()
        discountDao.delete(it)
        updateValues()
    }

    fun convertPoints() {
        //On convert button click
        val name = "Buono da ".plus(String.format("%.2f", valueOfConvertedPoints))
            .plus("€")
        val user = User.getLoggedUser(requireContext())
        val discount = Discount(name, valueOfConvertedPoints, TypeOfDiscount.NeatValue, user!!.id)
        val daos = RoomManager(requireContext()).db
        daos.discountDao().insert(discount)
        daos.userDao()
            .incrementsPoints(-resources.getInteger(R.integer.pointToEuroThreshold), user.id)
        updateValues()
    }
}