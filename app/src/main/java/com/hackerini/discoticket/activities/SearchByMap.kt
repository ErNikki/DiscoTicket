package com.hackerini.discoticket.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.location.LocationManager
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.hackerini.discoticket.R
import com.hackerini.discoticket.activities.SearchResult.Companion.getElementToShow
import com.hackerini.discoticket.fragments.views.Filter
import com.hackerini.discoticket.objects.Club
import com.hackerini.discoticket.objects.ElementToShow
import com.hackerini.discoticket.objects.Event
import com.hackerini.discoticket.objects.FilterCriteria
import com.hackerini.discoticket.utils.ClubsManager
import com.hackerini.discoticket.utils.EventsManager
import com.hackerini.discoticket.utils.ObjectLoader
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.infowindow.InfoWindow
import java.util.*


class SearchByMap : AppCompatActivity() {
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1
    private var map: MapView? = null
    private var filterCriteria = FilterCriteria()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val ctx = applicationContext
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx))
        setContentView(R.layout.activity_search_by_map)

        map = findViewById(R.id.SearchByMapMap)
        map?.setTileSource(TileSourceFactory.MAPNIK)
        map?.setBuiltInZoomControls(true)
        map?.setMultiTouchControls(true)
        map?.setOnTouchListener { v, event ->
            map?.overlays?.forEach { e ->
                (e as Marker).infoWindow.close()
            }
            false
        }

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
        val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (location != null) {
            val mapController = map?.controller
            mapController?.setZoom(11.5)
            val startPoint = GeoPoint(location.latitude, location.longitude)
            mapController?.setCenter(startPoint)
        }

        val discoChip = findViewById<Chip>(R.id.searchResultMapClubChip)
        val eventChip = findViewById<Chip>(R.id.searchResultMapEventChip)
        discoChip.setOnCheckedChangeListener { _, _ ->
            filterCriteria.elementToShow =
                getElementToShow(discoChip.isChecked, eventChip.isChecked)
            loadContent()
        }
        eventChip.setOnCheckedChangeListener { _, _ ->
            filterCriteria.elementToShow =
                getElementToShow(discoChip.isChecked, eventChip.isChecked)
            loadContent()
        }
        findViewById<Button>(R.id.SearchResultMapFilterButton).setOnClickListener {
            //Instead to pass the String ciao, you will pass and object with the current search criteria
            val filterFragment = Filter.newInstance(filterCriteria)
            filterFragment.onOkClicked = { a ->
                this.filterCriteria = a
                loadContent()
            }
            filterFragment.show(supportFragmentManager, "prova")
        }
        filterCriteria.elementToShow = ElementToShow.ALL
        loadContent()

    }


    private fun loadContent() {
        map?.overlays?.clear()
        val addedClubIds = LinkedList<Int>()
        val elementToShow = filterCriteria.elementToShow
        val elements = LinkedList<Any>()

        if (elementToShow == ElementToShow.ALL || elementToShow == ElementToShow.EVENTS) {
            for (e in EventsManager.getEvents()) {
                elements.add(e)
                addedClubIds.add(e.clubId)
            }
        }

        if (elementToShow == ElementToShow.ALL || elementToShow == ElementToShow.CLUBS) {
            for (e in ClubsManager.getClubs()) {
                if (!addedClubIds.contains(e.id)) {
                    elements.add(e)
                }
            }
        }

        val drawableEvent =
            ContextCompat.getDrawable(this, R.drawable.map_pin_icon_event)
        val drawableClub =
            ContextCompat.getDrawable(this, R.drawable.map_pin_icon_club)

        filterCriteria.filter(elements).forEach { item ->

            if (item is Event) {
                val marker = Marker(map)
                item.club?.let {
                    marker.position = GeoPoint(
                        it.gpsCords[0].toDouble(),
                        it.gpsCords[1].toDouble()
                    )
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                    marker.title = item.name
                    marker.icon = drawableEvent
                    marker.infoWindow = MyInfoWindows.create(this, item, map)
                    map?.overlays?.add(marker)
                }
            } else if (item is Club) {
                val marker = Marker(map)
                marker.position =
                    GeoPoint(item.gpsCords[0].toDouble(), item.gpsCords[1].toDouble())
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                marker.title = item.name
                marker.icon = drawableClub
                marker.infoWindow = MyInfoWindows.create(this, item, map)
                map?.overlays?.add(marker)
            }
        }

        map?.invalidate()


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
}

private class MyInfoWindows(view: View, mapView: MapView?) : InfoWindow(view, mapView) {
    override fun onOpen(item: Any?) {
    }

    override fun onClose() {
    }

    companion object {
        fun create(context: Context, event: Event, mapView: MapView?): MyInfoWindows {
            val shape = GradientDrawable()
            shape.cornerRadius = 20F
            shape.setColor(Color.WHITE)
            shape.setStroke(3, Color.BLACK)

            val ll = LinearLayout(context)
            ll.orientation = LinearLayout.VERTICAL
            val eventName = TextView(context)
            eventName.text = event.name
            eventName.textSize = 24.0F
            eventName.textAlignment = View.TEXT_ALIGNMENT_CENTER
            eventName.setTextColor(ContextCompat.getColor(context, R.color.eventColorStrong))
            val clubName = TextView(context)
            val spannableString = SpannableString("Presso " + event.club?.name)
            spannableString.setSpan(ForegroundColorSpan(Color.rgb(98, 0, 238)), 7, spannableString.length, 0)
            clubName.text = spannableString
            clubName.textSize = 18.0F
            clubName.textAlignment = View.TEXT_ALIGNMENT_CENTER
            val buttonEvent = MaterialButton(context)
            buttonEvent.text = "Info evento"
            val buttonClub = MaterialButton(context)
            buttonClub.text = "Info discoteca"
            ll.addView(eventName)
            ll.addView(clubName)
            ll.addView(buttonEvent)
            ll.addView(buttonClub)
            ll.background = shape
            ll.setPadding(10)

            ll.gravity = Gravity.CENTER_VERTICAL
            buttonEvent.setOnClickListener {
                val intent = Intent(context, EventDetails::class.java)
                intent.putExtra("event", event)
                context.startActivity(intent)
            }
            buttonClub.setOnClickListener {
                val intent = Intent(context, ClubDetails::class.java)
                intent.putExtra("club", event.club!!)
                context.startActivity(intent)
            }
            return MyInfoWindows(ll, mapView)
        }

        fun create(context: Context, club: Club, mapView: MapView?): MyInfoWindows {
            val shape = GradientDrawable()
            shape.cornerRadius = 20F
            shape.setColor(Color.WHITE)
            shape.setStroke(3, Color.BLACK)

            val ll = LinearLayout(context)
            ll.orientation = LinearLayout.VERTICAL
            val title = TextView(context)
            title.text = club.name
            title.textSize = 24F
            title.textAlignment = View.TEXT_ALIGNMENT_CENTER
            title.setTextColor(ContextCompat.getColor(context, R.color.purple_500))
            val button = MaterialButton(context)
            button.text = "Info discoteca"
            ll.addView(title)
            ll.addView(button)
            ll.background = shape
            ll.setPadding(10)
            ll.gravity = Gravity.CENTER_VERTICAL
            button.setOnClickListener {
                val intent = Intent(context, ClubDetails::class.java)
                intent.putExtra("club", club)
                context.startActivity(intent)
            }
            return MyInfoWindows(ll, mapView)
        }
    }

}