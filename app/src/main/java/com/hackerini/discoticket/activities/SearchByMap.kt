package com.hackerini.discoticket.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.chip.Chip
import com.hackerini.discoticket.R
import com.hackerini.discoticket.activities.SearchResult.Companion.getElementToShow
import com.hackerini.discoticket.fragments.views.Filter
import com.hackerini.discoticket.objects.ElementToShow
import com.hackerini.discoticket.objects.FilterCriteria
import com.hackerini.discoticket.utils.ObjectLoader
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker


class SearchByMap : AppCompatActivity(), Marker.OnMarkerClickListener {
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    private var map: MapView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val ctx = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        setContentView(R.layout.activity_search_by_map)

        map = findViewById(R.id.SearchByMapMap)
        map?.setTileSource(TileSourceFactory.MAPNIK)
        map?.setBuiltInZoomControls(true)
        map?.setMultiTouchControls(true)


        requestPermissionsIfNecessary(
            arrayOf(
                // if you need to show the current location, uncomment the line below
                Manifest.permission.ACCESS_FINE_LOCATION,
                // WRITE_EXTERNAL_STORAGE is required in order to show the map
                Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE

            )
        )


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
            val mapController = map?.controller
            mapController?.setZoom(11.5)
            val startPoint = GeoPoint(location.latitude, location.longitude)
            mapController?.setCenter(startPoint)
        }

        val discoChip = findViewById<Chip>(R.id.searchResultMapClubChip)
        val eventChip = findViewById<Chip>(R.id.searchResultMapEventChip)
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
            val filterCriteria = FilterCriteria()
            val filterFragment = Filter.newInstance(filterCriteria)
            filterFragment.show(supportFragmentManager, "prova")
        }
        loadContent(ElementToShow.ALL)

    }


    private fun loadContent(elementToShow: ElementToShow) {
        map?.overlays?.clear()

        if (elementToShow == ElementToShow.ALL || elementToShow == ElementToShow.CLUBS) {
            val drawable =
                ContextCompat.getDrawable(this, R.drawable.ic_baseline_club_location_on_24)
            val clubs = ObjectLoader.getClubs(applicationContext)
            clubs.forEach { item ->
                val marker = Marker(map)
                marker.position = GeoPoint(item.gpsCords[0].toDouble(), item.gpsCords[1].toDouble())
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                marker.title = item.name
                marker.icon = drawable
                marker.setOnMarkerClickListener(this)
                map?.overlays?.add(marker)
            }
        }

        if (elementToShow == ElementToShow.ALL || elementToShow == ElementToShow.EVENTS) {
            val drawable =
                ContextCompat.getDrawable(this, R.drawable.ic_baseline_event_location_on_24)
            val events = ObjectLoader.getEvents(applicationContext)
            events.forEach { item ->
                val marker = Marker(map)
                item.club?.let {
                    marker.position = GeoPoint(
                        it.gpsCords[0].toDouble() + 0.0001,
                        it.gpsCords[1].toDouble() + 0.0001
                    )
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                    marker.icon = drawable
                    marker.title = item.name
                    marker.setOnMarkerClickListener(this)
                    map?.overlays?.add(marker)
                }

            }
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

    override fun onResume() {
        super.onResume()
        map?.onResume()
    }

    override fun onPause() {
        super.onPause()
        map?.onPause()
    }

    override fun onMarkerClick(marker: Marker?, mapView: MapView?): Boolean {
        if (marker != null) {
            val clubs = ObjectLoader.getClubs(this)
            val selectedClub = clubs.firstOrNull { club -> club.name == marker.title }
            if (selectedClub != null) {
                val intent = Intent(this, ClubDetails::class.java)
                intent.putExtra("club", selectedClub)
                startActivity(intent)
                return true
            }

            val events = ObjectLoader.getEvents(this)
            val selectedEvents = events.firstOrNull { event -> event.name == marker.title }
            if (selectedEvents != null) {
                val intent = Intent(this, EventDetails::class.java)
                intent.putExtra("event", selectedEvents)
                startActivity(intent)
                return true
            }
        }
        return false
    }

}