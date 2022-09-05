package com.hackerini.discoticket.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.google.android.material.chip.Chip
import com.hackerini.discoticket.R
import com.hackerini.discoticket.activities.SearchResult.Companion.getElementToShow
import com.hackerini.discoticket.fragments.views.Filter
import com.hackerini.discoticket.objects.Club
import com.hackerini.discoticket.utils.ObjectLoader
import com.microsoft.maps.*


class SearchByMap : AppCompatActivity() {
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    private var mMapView: MapView? = null

    private val pinLayer = MapElementLayer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        requestPermissionsIfNecessary(
            arrayOf(
                // if you need to show the current location, uncomment the line below
                Manifest.permission.ACCESS_FINE_LOCATION,
                // WRITE_EXTERNAL_STORAGE is required in order to show the map
                Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE

            )
        )


        setContentView(R.layout.activity_search_by_map)

        mMapView = MapView(this, MapRenderMode.VECTOR)
        mMapView?.setCredentialsKey("AhkGU_SdJLTmYKYZortAP7pRMmQU_Rt_VQV6Q9b-XxWa9aepqjcgCo_BcXbO4wMm")
        findViewById<LinearLayout>(R.id.SearchResultByMapMap).addView(mMapView)
        mMapView?.onCreate(savedInstanceState)
        mMapView?.layers?.add(pinLayer)

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionsIfNecessary(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION))
            return
        }
        val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        if (location != null) {
            val geopoint = Geopoint(location.latitude, location.longitude)
            mMapView?.setScene(
                MapScene.createFromLocationAndZoomLevel(geopoint, 10.0),
                MapAnimationKind.NONE
            )
        }

        val discoChip = findViewById<Chip>(R.id.searchResultMapClubChip)
        val eventChip = findViewById<Chip>(R.id.searchResultMapEventChip)
        discoChip.setOnClickListener {
            Log.d("CIAO", "CIAO")
        }
        discoChip.setOnCheckedChangeListener { _, _ ->
            val elementToShow = getElementToShow(discoChip.isChecked, eventChip.isChecked)
            loadContent(elementToShow)
        }
        eventChip.setOnCheckedChangeListener { _, _ ->
            val elementToShow = getElementToShow(discoChip.isChecked, eventChip.isChecked)
            loadContent(elementToShow)
        }
        findViewById<Button>(R.id.SearchResultMapFilterButton).setOnClickListener {
            //Instead to pass the String ciao, you will pass and object with the current search criteria
            val filterFragment = Filter.newInstance("ciao")
            filterFragment.show(supportFragmentManager, "prova")
        }
        loadContent(ElementToShow.ALL)
    }

    private fun loadContent(elementToShow: ElementToShow) {
        pinLayer.elements.clear()

        if (elementToShow == ElementToShow.ALL || elementToShow == ElementToShow.CLUBS) {
            val drawable = ContextCompat.getDrawable(this,R.drawable.ic_baseline_club_location_on_24)
            val clubs = ObjectLoader.getClubs(applicationContext)
            clubs.forEach { item ->
                val pin = MapIcon()
                pin.location = Geopoint(item.gpsCords[0].toDouble(), item.gpsCords[1].toDouble())
                pin.title = item.name
                pin.tag = item
                pin.image = MapImage(drawable!!.toBitmap())
                pinLayer.elements.add(pin)
            }
        }
        if (elementToShow == ElementToShow.ALL || elementToShow == ElementToShow.EVENTS) {
            val drawable = ContextCompat.getDrawable(this,R.drawable.ic_baseline_event_location_on_24)
            val clubs = ObjectLoader.getClubs(applicationContext)
            clubs.forEach { item ->
                val pin = MapIcon()
                pin.location = Geopoint(item.gpsCords[0].toDouble(), item.gpsCords[1].toDouble())
                pin.title = item.name
                pin.tag = item
                pin.image = MapImage(drawable!!.toBitmap())
                pinLayer.elements.add(pin)
            }
        }

        mMapView?.addOnMapTappedListener { mapTappedEventArgs ->
            val position = mapTappedEventArgs.position
            val elements = mMapView?.findMapElementsAtOffset(position)
            if (elements != null) {
                for (mapElement in elements) {
                    if (mapElement is MapIcon) {
                        val intent = Intent(this, ClubDetails::class.java)
                        intent.putExtra("club", mapElement.tag as Club)
                        startActivity(intent)
                        break
                    }
                }
            }
            false
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val permissionsToRequest: ArrayList<String?> = ArrayList()
        for (i in grantResults.indices) {
            permissionsToRequest.add(permissions[i])
        }
        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toArray(arrayOfNulls(0)),
                REQUEST_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    private fun requestPermissionsIfNecessary(permissions: Array<String>) {
        val permissionsToRequest: ArrayList<String> = ArrayList()
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                // Permission is not granted
                permissionsToRequest.add(permission)
            }
        }
        if (permissionsToRequest.size > 0) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toArray(arrayOfNulls(0)),
                REQUEST_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    override fun onStart() {
        super.onStart()
        mMapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mMapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMapView?.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mMapView?.onSaveInstanceState(outState);
    }

    override fun onStop() {
        super.onStop()
        mMapView?.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMapView?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        //mMapView?.onLowMemory()
    }

}