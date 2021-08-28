package com.example.ezpath2

import android.Manifest
import android.animation.Animator
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.RatingBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.updateLayoutParams
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Scene
import androidx.transition.Transition
import androidx.transition.TransitionInflater
import androidx.transition.TransitionManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.slider.Slider

class ErrandActivity : AppCompatActivity() , SaveSetDialog.SaveSetDialogListener, ConfirmLoadSetDialog.ConfirmLoadSetDialogListener {
    lateinit var currLocation : String
    lateinit var fragPager: ViewPager2
    lateinit var transitionSet : Transition
    lateinit var transitionSet2 : Transition
    lateinit var toggleFragButton : FloatingActionButton
    lateinit var drawerLayout : DrawerLayout
//    lateinit var drawerToggle : ActionBarDrawerToggle
    lateinit var frame : FrameLayout
    lateinit var drawerContainer : ConstraintLayout
    private var defaultMargin = 0
    lateinit var filterButton : ImageButton
    lateinit var folderButton : ImageButton
    lateinit var movingShapeBackground : MaterialCardView
    lateinit var currPlaceId : String
    lateinit var currPlaceAddress : String
    lateinit var currPlaceLatLng : DoubleArray
    lateinit var saveButton : ImageButton
    lateinit var savedContainer : ConstraintLayout
    lateinit var preferencesContainer : ConstraintLayout
    lateinit var price_level_1 : ImageButton
    lateinit var price_level_2 : ImageButton
    lateinit var price_level_3 : ImageButton
    lateinit var ratingBar : RatingBar
    lateinit var radiusSlider : Slider
    lateinit var chipGroup : ChipGroup
    lateinit var savedSetsRecyclerView : RecyclerView
    lateinit var savedSetsAdapter : SetAdapter
    lateinit var linearLayoutManager : LinearLayoutManager
    var setData : ArrayList<String> = ArrayList()
    var price_level = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.splashTheme)
//        if (savedInstanceState != null) {
//            Log.d(savedInstanceState.getString("hello"), "passed")
//        }
        super.onCreate(savedInstanceState)
        val i = intent
        currPlaceId = i.getStringExtra("currPlaceId")!!
        currPlaceAddress = i.getStringExtra("currPlaceAddress")!!
        currPlaceLatLng = i.getDoubleArrayExtra("currPlaceLatLng")!!

        Log.d("oncreate", "called")
        setContentView(R.layout.errand_activity_layout)
        initViews()
        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                val moveFactor = (drawerContainer.width * slideOffset)
                frame.translationX = moveFactor
            }

            override fun onDrawerOpened(drawerView: View) {
                (supportFragmentManager.findFragmentByTag("f0") as ErrandFragment).toggleSideBarButtonAnim()
            }

            override fun onDrawerClosed(drawerView: View) {
                (supportFragmentManager.findFragmentByTag("f0") as ErrandFragment).toggleSideBarButtonAnim()
                for (i in 0 until setData.size) {
                    (linearLayoutManager.findViewByPosition(i))?.performClick()
                }
            }

            override fun onDrawerStateChanged(newState: Int) {
            }

        })
        initSavedSets()
        checkForLocationPermissionAndLaunchRequest()
    }

    private fun showSaveSetDialog() {
        val dialog = SaveSetDialog()
        dialog.show(supportFragmentManager, "save set dialog")
    }





    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.d("restore instance state", "Called")
    }

    override fun onDestroy() {
        Log.d("isfinishing", "" + isFinishing)
        super.onDestroy()
        Log.d("ondestroy", "called")
    }

    override fun onPause() {
        super.onPause()
        Log.d("onpause", "called")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("onrestart", "Called")
    }

    override fun onStart() {
        super.onStart()
        Log.d("onstart", "called")
    }

    override fun onResume() {
        super.onResume()
        Log.d("onresume", "Called")
    }

    private fun mapFragExists() : Boolean {
        return supportFragmentManager.findFragmentByTag("f1") != null
    }

    private fun mapFrag() : MapFragment {
        return supportFragmentManager.findFragmentByTag("f1") as MapFragment
    }

    override fun onStop() {
        super.onStop()
        Log.d("onstop", "called")
    }

    private fun checkForLocationPermissionAndLaunchRequest() {
        if (!checkPermission()) {
            mPermissionResult.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private val mPermissionResult: ActivityResultLauncher<String> = registerForActivityResult(
            ActivityResultContracts.RequestPermission()) { result ->
        when (result) {
            true -> {}
            else -> {}
        }
    }

    private fun checkPermission() : Boolean {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    override fun onBackPressed() {
        if (fragPager.currentItem == 1) {
            val mapfrag = supportFragmentManager.findFragmentByTag("f" + fragPager.currentItem) as MapFragment
            if (mapfrag.bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                mapfrag.toggleNoteButton.performClick()
            } else {
                toggleFragButton.performClick()
            }

        } else if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers()
        } else {
            Log.d("moveTaskToBack", "called")
            moveTaskToBack(true)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("hello", "hello")
        super.onSaveInstanceState(outState)
        Log.d("onsaveinstancestate", "called")
    }

    fun openConfirmLoadSetDialog(setName : String) {
        val dialog = ConfirmLoadSetDialog(setName)
        dialog.show(supportFragmentManager, "confirm load set dialog")
    }

    private fun initViews() {
        fragPager = findViewById(R.id.frag_pager)
        val fragAdapter = FragmentPagerAdapter(this)
        fragPager.apply {
            this.adapter = fragAdapter
            this.isUserInputEnabled = false
            this.isNestedScrollingEnabled = false
            offscreenPageLimit = 2
        }
        transitionSet = TransitionInflater.from(this).inflateTransition(R.transition.animate)

        transitionSet.addListener(object : Transition.TransitionListener {
            override fun onTransitionStart(transition: Transition) {
                when(fragPager.currentItem) {
                    0 -> {
                        fragPager.setCurrentItem(1, true)
                    }
                    1 -> {
                        fragPager.setCurrentItem(0, true)
                    }
                }
            }

            override fun onTransitionEnd(transition: Transition) {}
            override fun onTransitionCancel(transition: Transition) {}
            override fun onTransitionPause(transition: Transition) {}
            override fun onTransitionResume(transition: Transition) {}

        })

        //preferences

        price_level_1 = findViewById(R.id.price_level_1)
        price_level_2 = findViewById(R.id.price_level_2)
        price_level_3 = findViewById(R.id.price_level_3)

        price_level_1.setOnClickListener {
            it.isSelected = !it.isSelected
            price_level_2.isSelected = false
            price_level_3.isSelected = false
            price_level =  if (it.isSelected) 1 else 0
        }
        price_level_2.setOnClickListener {
            it.isSelected = !it.isSelected
            price_level_1.isSelected = false
            price_level_3.isSelected = false
            price_level =  if (it.isSelected) 2 else 0
        }
        price_level_3.setOnClickListener {
            it.isSelected = !it.isSelected
            price_level_1.isSelected = false
            price_level_2.isSelected = false
            price_level =  if (it.isSelected) 3 else 0
        }

        ratingBar = findViewById(R.id.rating_bar)
        radiusSlider = findViewById(R.id.radius_slider)
        chipGroup = findViewById(R.id.priority_chip_group)
        chipGroup.apply {
            check(R.id.chip_distance)
        }



        transitionSet2 = TransitionInflater.from(this).inflateTransition(R.transition.animate)
        transitionSet2.apply {
            addListener(object : Transition.TransitionListener {

                override fun onTransitionStart(transition: Transition) {
                }

                override fun onTransitionEnd(transition: Transition) {
                }

                override fun onTransitionCancel(transition: Transition) {
                }

                override fun onTransitionPause(transition: Transition) {
                }

                override fun onTransitionResume(transition: Transition) {
                }

            })
            duration = 175L

        }

        savedContainer = findViewById(R.id.saved_container)
        preferencesContainer = findViewById(R.id.preferences_container)

        movingShapeBackground = findViewById(R.id.moving_shape_background)

        saveButton = findViewById(R.id.save_button)
        saveButton.setOnClickListener {
            if (setData.size >= 5) {
                Toast.makeText(this, "You cannot save more than 5 sets", Toast.LENGTH_SHORT).show()
            } else {
                showSaveSetDialog()
            }
        }

        toggleFragButton = findViewById(R.id.toggle_frag_button)
        defaultMargin = (toggleFragButton.layoutParams as ConstraintLayout.LayoutParams).bottomMargin
        toggleFragButton.setOnClickListener {
            toggleFragButton.toggleFrag()
        }
        drawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.setScrimColor(ContextCompat.getColor(this, android.R.color.transparent))
        frame = findViewById(R.id.content_frame)
        drawerContainer = findViewById(R.id.drawer_container)

        filterButton = findViewById(R.id.filter_button)
        folderButton = findViewById(R.id.folder_button)
        filterButton.apply {
            isSelected = true
            setOnClickListener {
                if (!isSelected) {
                    isSelected = true
                    moveIconBackground(this.id)
                    folderButton.isSelected = false

                    savedContainer.hide()
                    preferencesContainer.show()

                }
            }
        }
        folderButton.apply {
            setOnClickListener {
                if (!isSelected) {
                    isSelected = true
                    moveIconBackground(this.id)

                    preferencesContainer.hide()
                    savedContainer.show()

                }
                filterButton.isSelected= false
            }
        }

        //sets

        savedSetsRecyclerView = findViewById(R.id.saved_errands_recycler_view)
        savedSetsAdapter = SetAdapter(this, setData)
        linearLayoutManager = LinearLayoutManager(this)
        savedSetsRecyclerView.apply {
            layoutManager = linearLayoutManager
            adapter = savedSetsAdapter
        }

    }

    private fun ConstraintLayout.hide() {
        this.animate().apply {
            duration = 500L
            alpha(0.0f)
            setListener(object : Animator.AnimatorListener{
                override fun onAnimationStart(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    this@hide.visibility = View.INVISIBLE
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationRepeat(animation: Animator?) {
                }

            })
        }
    }

    private fun ConstraintLayout.show() {
        this.animate().apply {
            duration = 500L
            alpha(1.0f)
            setListener(object : Animator.AnimatorListener{
                override fun onAnimationStart(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    this@show.visibility = View.VISIBLE
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationRepeat(animation: Animator?) {
                }

            })
        }
    }

    private fun moveIconBackground(buttonId : Int) {
        val par = movingShapeBackground.parent
        val constraintLayout = par as ConstraintLayout
        when (buttonId) {
            R.id.filter_button -> {
                movingShapeBackground.apply {
                    TransitionManager.go(Scene(par as ViewGroup), transitionSet2)
                    val constraintSet = ConstraintSet()
                    constraintSet.clone(constraintLayout)
                    //constraintSet.clear(this.id, ConstraintSet.BOTTOM)
                    constraintSet.clear(this.id, ConstraintSet.TOP)
                    //constraintSet.connect(this.id, ConstraintSet.BOTTOM, R.id.filter_button, ConstraintSet.BOTTOM)
                    constraintSet.connect(this.id, ConstraintSet.TOP, buttonId, ConstraintSet.TOP)
                    constraintSet.applyTo(constraintLayout)
                }

                movingShapeBackground.apply {
                    Handler(Looper.getMainLooper()).postDelayed({
                        TransitionManager.go(Scene(par as ViewGroup), transitionSet2)
                        val constraintSet = ConstraintSet()
                        constraintSet.clone(constraintLayout)
                        constraintSet.clear(this.id, ConstraintSet.BOTTOM)
                        //constraintSet.clear(this.id, ConstraintSet.TOP)
                        constraintSet.connect(this.id, ConstraintSet.BOTTOM, R.id.filter_button, ConstraintSet.BOTTOM)
                        //constraintSet.connect(this.id, ConstraintSet.TOP, R.id.filter_button, ConstraintSet.TOP)
                        constraintSet.applyTo(constraintLayout)
                    }, 150L)
                }

            }
            R.id.folder_button -> {

                movingShapeBackground.apply {
                    TransitionManager.go(Scene(par as ViewGroup), transitionSet2)
                    val constraintSet = ConstraintSet()
                    constraintSet.clone(constraintLayout)
                    constraintSet.clear(this.id, ConstraintSet.BOTTOM)
                    //constraintSet.clear(this.id, ConstraintSet.TOP)
                    constraintSet.connect(this.id, ConstraintSet.BOTTOM, buttonId, ConstraintSet.BOTTOM)
                    //constraintSet.connect(this.id, ConstraintSet.TOP, R.id.folder_button, ConstraintSet.TOP)
                    constraintSet.applyTo(constraintLayout)
                }

                movingShapeBackground.apply {
                    Handler(Looper.getMainLooper()).postDelayed({
                        TransitionManager.go(Scene(par as ViewGroup), transitionSet2)
                        val constraintSet = ConstraintSet()
                        constraintSet.clone(constraintLayout)
                        //constraintSet.clear(this.id, ConstraintSet.BOTTOM)
                        constraintSet.clear(this.id, ConstraintSet.TOP)
                        //constraintSet.connect(this.id, ConstraintSet.BOTTOM, R.id.filter_button, ConstraintSet.BOTTOM)
                        constraintSet.connect(this.id, ConstraintSet.TOP, buttonId, ConstraintSet.TOP)
                        constraintSet.applyTo(constraintLayout)
                    }, 150L)
                }

            }
        }
    }

     fun FloatingActionButton.toggleFrag() {
        val par = this.parent
        when (fragPager.currentItem) {
            0 -> {
                Log.d("toggle", "0")
                //set to map
                //animate button to left
                TransitionManager.go(Scene(par as ViewGroup), transitionSet)
                val constraintLayout = par as ConstraintLayout
                val constraintSet = ConstraintSet()
                constraintSet.clone(constraintLayout)
                constraintSet.clear(this.id, ConstraintSet.START)
                constraintSet.connect(this.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
                constraintSet.applyTo(constraintLayout)
                // redo margins
                (this.layoutParams as ConstraintLayout.LayoutParams).setMargins(0, 0, defaultMargin, defaultMargin)
                this.setImageResource(R.drawable.ic_check_list_icon)
                //fragPager.setCurrentItem(1, true)

            }
            1 -> {
                Log.d("toggle", "1")
                //set to errand list
                //animate button to right
                TransitionManager.go(Scene(par as ViewGroup), transitionSet)
                Log.d("1", "1")
                val constraintLayout = par as ConstraintLayout
                Log.d("2", "1")
                val constraintSet = ConstraintSet()

                constraintSet.clone(constraintLayout)
                Log.d("3", "1")
                constraintSet.clear(this.id, ConstraintSet.END)
                Log.d("4", "1")
                constraintSet.connect(this.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
                Log.d("5", "1")
                constraintSet.applyTo(constraintLayout)
                Log.d("6", "1")
                //redo margins
                (this.layoutParams as ConstraintLayout.LayoutParams).setMargins(defaultMargin, 0, 0, defaultMargin)
                this.setImageResource(R.drawable.ic_map_icon)
                Log.d("7", "1")
                //fragPager.setCurrentItem(0, true)
            }
        }
    }

    private fun initSavedSets() {
        val prefs = this.getSharedPreferences(this.packageName + "_preferences", Context.MODE_PRIVATE)
        val keys = prefs.all

        if (!keys.isNullOrEmpty()) {
            for (entry in keys.entries) {
                Log.d("map values", "${entry.key} : ${entry.value.toString()}")
                if (entry.value is Set<*>) {
                    setData.add(entry.key)
                }
            }
            savedSetsAdapter.notifyDataSetChanged()
        }
    }

    override fun saveSet(name: String) {
        if (name.isNotBlank()) {
            (supportFragmentManager.findFragmentByTag("f0") as ErrandFragment).storeSet(name)
        } else {
            Toast.makeText(this, "Name cannot be blank", Toast.LENGTH_SHORT).show()
        }
    }

    override fun confirmLoadSet(setName: String) {
        (supportFragmentManager.findFragmentByTag("f0") as ErrandFragment).loadSet(setName)
    }
}