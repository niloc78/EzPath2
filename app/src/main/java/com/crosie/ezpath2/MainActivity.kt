package com.crosie.ezpath2

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.updateLayoutParams
import androidx.transition.Scene
import androidx.transition.Transition
import androidx.transition.TransitionInflater
import androidx.transition.TransitionManager
import com.google.android.gms.common.api.Status
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.material.card.MaterialCardView

class MainActivity : AppCompatActivity() {
    lateinit var searchButton : MaterialCardView
    lateinit var set : Transition
    private var defaultButtonWidth : Int = 0
    private var defaultButtonRadius : Float = 0F
    lateinit var autoCompleteSupportFrag : AutocompleteSupportFragment
    private var expanded = false
    private var placeSelected = false
    private val PLACES_API_KEY = BuildConfig.PLACES_API_KEY
    private lateinit var currPlaceId : String
    private lateinit var currPlaceAddress : String
    private lateinit var currPlaceLatLng : DoubleArray

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setTheme(R.style.splashTheme)
        super.onCreate(savedInstanceState)
        if (locationExists() && !isForResult()) {
            val prefs = this.getSharedPreferences(this.packageName + "_preferences", Context.MODE_PRIVATE)
            currPlaceId = prefs.getString("currPlaceId", null)!!
            currPlaceAddress = prefs.getString("currPlaceAddress", null)!!
            currPlaceLatLng = doubleArrayOf(prefs.getFloat("currPlaceLat", Float.NaN).toDouble(),
                    prefs.getFloat("currPlaceLng", Float.NaN).toDouble())
            launchErrandActivity()
        } else {
            setContentView(R.layout.activity_main)
            initViews()
        }
    }

    private fun locationExists() : Boolean {
        val prefs = this.getSharedPreferences(this.packageName + "_preferences", Context.MODE_PRIVATE)
        return !prefs.getString("currPlaceId", null).isNullOrBlank()
                && !prefs.getString("currPlaceAddress", null).isNullOrBlank()
                && !prefs.getFloat("currPlaceLat", Float.NaN).isNaN()
                && !prefs.getFloat("currPlaceLng", Float.NaN).isNaN()
    }

    private fun initViews() {
        searchButton = findViewById(R.id.search_location_button)
        set = TransitionInflater.from(this).inflateTransition(R.transition.animate)
        set.duration = 200L
        set.addListener(object : Transition.TransitionListener {
            override fun onTransitionStart(transition: Transition) {
            }

            override fun onTransitionEnd(transition: Transition) {
                if (expanded) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        autoCompleteSupportFrag.view?.findViewById<View>(R.id.places_autocomplete_search_input)?.performClick()}, 150L)
                } else if (placeSelected) {
                    launchErrandActivity()
                }
            }

            override fun onTransitionCancel(transition: Transition) {
            }

            override fun onTransitionPause(transition: Transition) {
            }

            override fun onTransitionResume(transition: Transition) {
            }

        })
        //Log.d("width", "" + searchButton.layoutParams.width)
        //Log.d("radius", "" + searchButton.radius)
        searchButton.setOnClickListener {
            searchButton.toggleExpand()
        }
        defaultButtonWidth = searchButton.layoutParams.width
        defaultButtonRadius = searchButton.radius
//        (findViewById<Button>(R.id.test_skip)).setOnClickListener {
//            launchErrandActivity()
//        }
        if (!Places.isInitialized()) {
            Places.initialize(this, PLACES_API_KEY)
        }

        Places.createClient(this)
        autoCompleteSupportFrag = supportFragmentManager.findFragmentById(R.id.place_search_autocomplete) as AutocompleteSupportFragment
        autoCompleteSupportFrag.apply {
            setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG))
            setHint("")
            view?.findViewById<View>(R.id.places_autocomplete_search_button)?.visibility = View.GONE
            view?.findViewById<View>(R.id.places_autocomplete_content)
            view?.findViewById<View>(R.id.places_autocomplete_search_input)?.isEnabled = false

            setOnPlaceSelectedListener(object : PlaceSelectionListener {
                override fun onPlaceSelected(p0: Place) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        searchButton.performClick()
                        placeSelected = true
                        currPlaceId = p0.id!!
                        currPlaceAddress = p0.name!!
                        currPlaceLatLng = doubleArrayOf(p0.latLng!!.latitude, p0.latLng!!.longitude)
                        val prefs = this@MainActivity.getSharedPreferences(this@MainActivity.packageName + "_preferences", Context.MODE_PRIVATE)
                        prefs.edit().apply {
                            putString("currPlaceId", currPlaceId)
                            putString("currPlaceAddress", currPlaceAddress)
                            putFloat("currPlaceLat", currPlaceLatLng[0].toFloat())
                            putFloat("currPlaceLng", currPlaceLatLng[1].toFloat())
                        }.commit()

                    }, 500L)

                }

                override fun onError(p0: Status) {
                    searchButton.performClick()
                }

            })

        }

    }

    private fun launchErrandActivity() {
        Intent(this, ErrandActivity::class.java).also { intent ->
            intent.putExtra("currPlaceId", currPlaceId)
            intent.putExtra("currPlaceAddress", currPlaceAddress)
            intent.putExtra("currPlaceLatLng", currPlaceLatLng)
            if (isForResult()) {
                setResult(Activity.RESULT_OK, intent)
                onBackPressed()
            } else {
                finish()
                startActivity(intent)
            }
        }
    }

    private fun isForResult() : Boolean {
        return callingActivity != null
    }

    override fun onBackPressed() {
        if (isForResult()) {
            finish()
        } else {
            moveTaskToBack(true)
        }
    }


    private fun MaterialCardView.toggleExpand () {
        val par = this.parent
        when (this.isChecked) {
            false -> {
                //Log.d("false", "called")
                TransitionManager.go(Scene(par as ViewGroup), set)
                this.apply {
                    val constraintLayout = par as ConstraintLayout
                    val constraintSet = ConstraintSet()
                    constraintSet.clone(constraintLayout)
                    constraintSet.clear(id, ConstraintSet.START)
                    constraintSet.clear(id, ConstraintSet.END)
                    constraintSet.connect(id, ConstraintSet.START, R.id.logo, ConstraintSet.START)
                    constraintSet.connect(id, ConstraintSet.END, R.id.logo, ConstraintSet.END)
                    constraintSet.setHorizontalBias(id, 0.1F)
                    constraintSet.applyTo(constraintLayout)
                }

                this.apply {
                    Handler(Looper.getMainLooper()).postDelayed({
                        TransitionManager.go(Scene(par), set)
                        updateLayoutParams {
                            width = 0
                        }
                        radius = 10F
                        val cL = this.getChildAt(0) as ConstraintLayout
                        val searchIcon = cL.getChildAt(0)
                        val cS = ConstraintSet()
                        cS.clone(cL)
                        cS.clear(searchIcon.id, ConstraintSet.END)
                        cS.connect(searchIcon.id, ConstraintSet.END, R.id.place_search_autocomplete, ConstraintSet.START)
                        cS.applyTo(cL)
                        expanded = true

                    }, 200L)
                }
                this.isChecked = true
            }
            true -> {
                //Log.d("true", "called")
                TransitionManager.go(Scene(par as ViewGroup), set)
                this.apply {
                    val constraintLayout = par as ConstraintLayout
                    val constraintSet = ConstraintSet()
                    constraintSet.clone(constraintLayout)
                    constraintSet.clear(id, ConstraintSet.START)
                    constraintSet.clear(id, ConstraintSet.END)
                    constraintSet.connect(id, ConstraintSet.START, R.id.myde, ConstraintSet.START)
                    constraintSet.connect(id, ConstraintSet.END, R.id.myde, ConstraintSet.END)
                    constraintSet.setHorizontalBias(id, 0.5F)
                    constraintSet.applyTo(constraintLayout)
                    updateLayoutParams {
                        width = defaultButtonWidth
                    }
                    radius = defaultButtonRadius
                    val cL = this.getChildAt(0) as ConstraintLayout
                    val searchIcon = cL.getChildAt(0)
                    val cS = ConstraintSet()
                    cS.clone(cL)
                    cS.clear(searchIcon.id, ConstraintSet.END)
                    //cS.clear(R.id.place_search_autocomplete, ConstraintSet.START)
                    cS.connect(searchIcon.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
                    //cS.connect(R.id.place_search_autocomplete, ConstraintSet.START, searchIcon.id, ConstraintSet.END)
                    cS.applyTo(cL)
                    expanded = false
                }
                this.isChecked = false
            }
        }

    }


}