package com.crosie.ezpath2.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Looper
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.transition.*
import com.crosie.ezpath2.ErrandActivity
import com.crosie.ezpath2.model.ErrandModel
import com.crosie.ezpath2.R
import com.crosie.ezpath2.dialog.EnableLocationDialog
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import jp.wasabeef.blurry.Blurry
import java.util.*
import kotlin.collections.ArrayList

class MapFragment : Fragment(), OnMapReadyCallback, EnableLocationDialog.LocationEnabledDialogListener {
    lateinit var toggleNoteButton : FloatingActionButton
    lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    lateinit var mapFragment : SupportMapFragment
    lateinit var map : GoogleMap
    lateinit var mapView : MapView
    lateinit var errandModel : ErrandModel
    lateinit var blurScrim : ImageView
    lateinit var editNote : EditText
    lateinit var gearButton : FloatingActionButton
    private var markers = ArrayList<Marker>()
    lateinit var fusedLocationClient: FusedLocationProviderClient
    var polyLine : Polyline? = null

    private val locationCallback = object: LocationCallback() {
        override fun onLocationResult(locResult: LocationResult) {
            //do stuff
        }
    }

    private fun startLocationUpdates(locationRequest: LocationRequest, locationCallback: LocationCallback) {
        if (checkPermission()) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        }
    }

    private fun createLocationRequest() : LocationRequest {
        return LocationRequest.create().apply {
            interval = 5000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.map_frag_layout, container, false)
    }

    override fun onResume() {
        super.onResume()
        if (map != null && map.isMyLocationEnabled) {
            startLocationUpdates(createLocationRequest(), locationCallback)
        }
    }

    override fun onPause() {
        fusedLocationClient?.removeLocationUpdates(locationCallback)
        super.onPause()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        initMap()
        errandModel = ViewModelProvider(requireActivity()).get(ErrandModel::class.java)
        initNote()
        setFragListeners()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

    }

    private fun setFragListeners() {
        parentFragmentManager.apply {
            setFragmentResultListener("errandRemoved", this@MapFragment, { _, result ->
                if (result.getBoolean("errandRemoved")) {
                    errandModel.updatePoly { dpoly ->
                        if (dpoly != null) {
                            polyLine?.remove()
                            markers[result.getInt("indexRemoved") + 1].remove()
                            markers.removeAt(result.getInt("indexRemoved") + 1)

                            polyLine = map.addPolyline(
                                PolylineOptions().addAll(dpoly).width(17F).color(
                                    Color.DKGRAY
                                )
                            )
                        } else {
                            polyLine?.remove()
                            markers[result.getInt("indexRemoved") + 1].remove()
                            markers.removeAt(result.getInt("indexRemoved") + 1)
                        }
                    }
                }
            })

            setFragmentResultListener("markerOrderChanged", this@MapFragment, { _, result ->
                if (result.getBoolean("markerOrderChanged")) {
                    Collections.swap(
                        markers,
                        result.getInt("fromPos") + 1,
                        result.getInt("toPos") + 1
                    )
                }
            })

            setFragmentResultListener("errandCheckChanged", this@MapFragment, { _, result ->
                when (result.getBoolean("checked")) {
                    true -> {
                        markers[result.getInt("index") + 1].setIcon(
                            BitmapDescriptorFactory.defaultMarker(
                                90.toFloat()
                            )
                        )
                    }
                    else -> {
                        markers[result.getInt("index") + 1].setIcon(BitmapDescriptorFactory.defaultMarker())
                    }
                }
            })

            setFragmentResultListener("errandReAdded", this@MapFragment, { _, result ->
                if (result.getBoolean("errandReAdded")) {
                    //Collections.swap(markers, result.getInt("fromPos") + 1, result.getInt("toPos") + 1)
                    errandModel.updatePoly { dpoly ->
                        val bestPlace = errandModel.bestResults.value!!.last()
                        val geometry = bestPlace.geometry["location"]!!
                        val latLng = LatLng(
                            geometry["lat"]!! as Double,
                            geometry["lng"]!! as Double
                        )
                        markers.add(map.addMarker(MarkerOptions().position(latLng).title(bestPlace.name))!!)
                        polyLine?.remove()
                        polyLine = map.addPolyline(
                            PolylineOptions().addAll(dpoly).width(17F).color(
                                Color.DKGRAY
                            )
                        )

                        val re = Bundle()
                        re.putBoolean("markerReAdded", true)
                        setFragmentResult("markerReAdded", re)
                    }

                }
            })

            setFragmentResultListener("locationChanged", this@MapFragment, { _, result ->
                if (result.getBoolean("locationChanged")) {
                    //Collections.swap(markers, result.getInt("fromPos") + 1, result.getInt("toPos") + 1)
                    for (i in 0 until markers.size) {
                        markers[i].remove()
                    }
                    markers.clear()
                    polyLine?.remove()

                    val latLng = LatLng(
                        (activity as ErrandActivity).currPlaceLatLng[0],
                        (activity as ErrandActivity).currPlaceLatLng[1]
                    )
                    map.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.Builder().target(latLng).zoom(10F).build()))
                    markers.add(
                        map.addMarker(
                            MarkerOptions().position(latLng).title("Starting Location")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                        )!!
                    )

                }
            })
            setFragmentResultListener("errandAdded", this@MapFragment, { _, result ->
                if (result.getBoolean("errandAdded")) {
                    errandModel.updatePoly { dpoly ->
                        val bestPlace = errandModel.bestResults.value!!.last()
                        val geometry = bestPlace.geometry["location"]!!
                        val latLng = LatLng(
                            geometry["lat"]!! as Double,
                            geometry["lng"]!! as Double
                        )
                        markers.add(
                            map.addMarker(
                                MarkerOptions().position(latLng).title(bestPlace.name)
                            )!!
                        )
                        polyLine?.remove()
                        polyLine = map.addPolyline(
                            PolylineOptions().addAll(dpoly).width(17F).color(
                                Color.DKGRAY
                            )
                        )
                    }
                }
            })
        }
    }

    private fun initViews(v: View) {
        bottomSheetBehavior = BottomSheetBehavior.from(v.findViewById(R.id.bottom_sheet))
        toggleNoteButton = v.findViewById(R.id.toggle_note_button)
        toggleNoteButton.setOnClickListener {
            when(bottomSheetBehavior.state) {
                BottomSheetBehavior.STATE_COLLAPSED -> {
                    (this@MapFragment.activity as ErrandActivity).toggleFragButton.visibility =
                        View.INVISIBLE
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                }

                BottomSheetBehavior.STATE_EXPANDED -> {
                    (this@MapFragment.activity as ErrandActivity).toggleFragButton.visibility =
                        View.VISIBLE
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }

            }
            //toggleNoteButton.toggleExpandSheet()
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        editNote.clearFocus()
                        (this@MapFragment.activity as ErrandActivity).toggleFragButton.visibility =
                            View.VISIBLE
                        blurScrim.visibility = View.INVISIBLE

                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        (this@MapFragment.activity as ErrandActivity).toggleFragButton.visibility =
                            View.INVISIBLE
                        map.snapshot { it ->
                            blurScrim.visibility = View.VISIBLE
                            blurScrim.setImageBitmap(it)
                            Blurry.with(requireContext()).radius(10).sampling(8).color(
                                Color.argb(
                                    99,
                                    0,
                                    0,
                                    0
                                )
                            ).capture(blurScrim).into(blurScrim)
                        }
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

        })

        editNote = v.findViewById(R.id.edit_note)
        editNote.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                //Log.d("hasfocus", "false")
                //Log.d("saveNote", "called")
                val prefs = requireContext().getSharedPreferences(requireContext().packageName + "_preferences", Context.MODE_PRIVATE)
                prefs.edit().putString("note", editNote.text.toString()).commit()
            } else {
                //Log.d("hasfocus", "true")
            }
        }

        blurScrim = v.findViewById(R.id.blur_scrim)
        blurScrim.setOnClickListener {
            toggleNoteButton.performClick()
        }
        mapView = v.findViewById(R.id.map_container)
        gearButton = v.findViewById(R.id.gear_button)
        gearButton.setOnClickListener {
            showEnableLocationDialog()
        }

    }

    private fun showEnableLocationDialog() {
        val dialog = EnableLocationDialog()
        dialog.show(childFragmentManager, "location dialog")
    }

    private fun initNote() {
        val prefs = requireContext().getSharedPreferences(requireContext().packageName + "_preferences", Context.MODE_PRIVATE)
        if (!prefs.getString("note", null).isNullOrBlank()) {
            val text = prefs.getString("note", null)!!
            editNote.setText(text)
        }
    }

    private fun initMap() {
        val options = GoogleMapOptions()
        options.apply {
            compassEnabled(true)
            zoomControlsEnabled(true)
            mapFragment = SupportMapFragment.newInstance(this)
        }
        childFragmentManager.beginTransaction().add(R.id.map_container, mapFragment).commit()
        mapFragment.getMapAsync(this)

    }

    private fun checkPermission() : Boolean {
        return ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }


    @SuppressLint("ResourceType")
    override fun onMapReady(p0: GoogleMap) {
        p0.moveCamera(CameraUpdateFactory.newLatLng(LatLng(0.toDouble(), 0.toDouble())))
        val zoomControls = mapFragment.view?.findViewById<View>(0x1)
        if (zoomControls != null && zoomControls.layoutParams is RelativeLayout.LayoutParams) {
            zoomControls.updateLayoutParams {
                val params = this as RelativeLayout.LayoutParams

                params.addRule(RelativeLayout.ALIGN_PARENT_TOP)
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                val margin = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    10F,
                    resources.displayMetrics
                ).toInt()
                params.setMargins(margin, 6*margin, margin, margin)

            }
        }

        map = p0

        if (checkPermission()) {
            map.isMyLocationEnabled = true
        }

        val latLng = LatLng(
            (activity as ErrandActivity).currPlaceLatLng[0],
            (activity as ErrandActivity).currPlaceLatLng[1]
        )
        map.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.Builder().target(latLng).zoom(10F).build()))
        markers.add(
            map.addMarker(
                MarkerOptions().position(latLng).title("Starting Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            )!!
        )
    }

    @SuppressLint("MissingPermission")
    override fun enableLocation(b: Boolean) {
        if (b) {
            checkForLocationPermissionAndLaunchRequest()
        } else {
            map.isMyLocationEnabled = false
        }
    }

    private fun checkForLocationPermissionAndLaunchRequest() {
        if (!checkPermission()) {
            mPermissionResult.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            map.isMyLocationEnabled = true
            startLocationUpdates(createLocationRequest(), locationCallback)
        }
    }

    @SuppressLint("MissingPermission")
    private val mPermissionResult: ActivityResultLauncher<String> = registerForActivityResult(
            ActivityResultContracts.RequestPermission()) { result ->
                if (result) {
                    map.isMyLocationEnabled = true
                    startLocationUpdates(createLocationRequest(), locationCallback)
                } else {
                    map.isMyLocationEnabled = false
                    fusedLocationClient.removeLocationUpdates(locationCallback)
                }
    }


}