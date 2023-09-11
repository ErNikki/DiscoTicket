package com.hackerini.discoticket.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.OnSuccessListener
import com.hackerini.discoticket.R
import com.hackerini.discoticket.databinding.FragmentHomeBinding
import com.hackerini.discoticket.fragments.elements.ElementToShow
import com.hackerini.discoticket.fragments.elements.HomePageDiscoElement
import com.hackerini.discoticket.fragments.elements.HomePageEventElement
import com.hackerini.discoticket.objects.Club
import com.hackerini.discoticket.objects.Event
import com.hackerini.discoticket.utils.ClubsManager
import com.hackerini.discoticket.utils.EventsManager
import com.hackerini.discoticket.utils.MyLocation
import com.hackerini.discoticket.utils.ObjectLoader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class HomeFragment : Fragment(){

    private var _binding: FragmentHomeBinding? = null
    private var currentLocation: Location? = null
    private lateinit var locationManager: LocationManager


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var clubs: Array<Club>
    private lateinit var lastViewedLL: LinearLayout
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        askLocationPermissions()


        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ){
            locationManager=
                requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager


            fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

            /*
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY,null).addOnSuccessListener {
                    location : Location? -> locationByGps=location
            }
            Log.d("GPS","entrato")
            */

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location : Location? ->
                    // Got last known location. In some rare situations this can be null.
                    CoroutineScope(Dispatchers.Main).launch {

                        location?.let { MyLocation.setLocation(it) }
                        ClubsManager.computeDistance(location)

                        view?.findViewById<LinearLayout>(R.id.HomeNearYouLinearLayout)
                            ?.removeAllViews()
                        var transaction = parentFragmentManager.beginTransaction()
                        clubs.sortBy { club -> club.distanceFromYou }
                        clubs.take(5).forEach { club ->
                            transaction.add(
                                R.id.HomeNearYouLinearLayout,
                                HomePageDiscoElement.newInstance(club, true)
                            )
                        }
                        transaction.commit()
                    }

                }



        }
        else{

            val alertDialogBuilder = AlertDialog.Builder(requireActivity())
            alertDialogBuilder.setMessage("Per far funzionare l'app è necessario attivare i permessi del gps!!")
            alertDialogBuilder.show()

        }

        ClubsManager.downloadClubs()
        EventsManager.downloadEvents()
        //ClubsManager.computeDistance(locationByGps)

        clubs = ClubsManager.getClubs()
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lastViewedLL = view.findViewById(R.id.HomeLastViewedLinearLayout)

        var transaction = parentFragmentManager.beginTransaction()
        clubs.sortBy { club -> club.distanceFromYou }
        clubs.take(5).forEach { club ->
            transaction.add(
                R.id.HomeNearYouLinearLayout,
                HomePageDiscoElement.newInstance(club, true)
            )
        }

        val events = EventsManager.getEvents()
        val elementToShow = ElementToShow.Date.or(ElementToShow.Location)
        events.filter { event -> event.date.after(Date()) }
            .sortedBy { event -> event.date.time }
            .take(5)
            .forEach { event ->
                transaction.add(
                    R.id.HomeNextEventLinearLayout,
                    HomePageEventElement.newInstance(event, elementToShow)
                )
            }

        transaction.commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        lastViewedLL.removeAllViews()
        var transaction = parentFragmentManager.beginTransaction()

        Club.getLastSeen(requireContext())
            .forEach { item ->
                if (item is Club)
                    transaction.add(
                        R.id.HomeLastViewedLinearLayout,
                        HomePageDiscoElement.newInstance(item)
                    )
                else if (item is Event) {
                    val elementToShow = ElementToShow.Date.or(ElementToShow.Location)
                    transaction.add(
                        R.id.HomeLastViewedLinearLayout,
                        HomePageEventElement.newInstance(item, elementToShow)
                    )
                }
            }
        transaction.commit()
    }


    private fun askLocationPermissions(){
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            val locationPermissionRequest = registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { permissions ->
                when {
                    permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                        // Precise location access granted.

                    }
                    permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                        // Only approximate location access granted.

                    } else -> {
                    // No location access granted.

                }
                }
            }
            locationPermissionRequest.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION))

        }



    }

    companion object{
        var locationByGps: Location? = null
    }


}