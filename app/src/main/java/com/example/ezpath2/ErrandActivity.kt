package com.example.ezpath2

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.updateLayoutParams
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Scene
import androidx.transition.Transition
import androidx.transition.TransitionInflater
import androidx.transition.TransitionManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ErrandActivity : AppCompatActivity() {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            Log.d(savedInstanceState.getString("hello"), "passed")
        }
        super.onCreate(savedInstanceState)
        Log.d("oncreate", "called")
        setContentView(R.layout.errand_activity_layout)
        initViews()
        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                val moveFactor = (drawerContainer.width * slideOffset)
                frame.translationX = moveFactor
            }

            override fun onDrawerOpened(drawerView: View) {
            }

            override fun onDrawerClosed(drawerView: View) {
            }

            override fun onDrawerStateChanged(newState: Int) {
            }

        })
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

    override fun onStop() {
        super.onStop()
        Log.d("onstop", "called")
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
            moveTaskToBack(true)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("hello", "hello")
        super.onSaveInstanceState(outState)
        Log.d("onsaveinstancestate", "called")
    }


    private fun initViews() {
        fragPager = findViewById(R.id.frag_pager)
        val fragAdapter = FragmentPagerAdapter(this)
        fragPager.apply {
            this.adapter = fragAdapter
            this.isUserInputEnabled = false
            this.isNestedScrollingEnabled = false
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

        movingShapeBackground = findViewById(R.id.moving_shape_background)

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
                }
            }
        }
        folderButton.apply {
            setOnClickListener {
                if (!isSelected) {
                    isSelected = true
                    moveIconBackground(this.id)
                }
                filterButton.isSelected= false
            }
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
                constraintSet.clear(this.id, ConstraintSet.END)
                constraintSet.connect(this.id, ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START)
                constraintSet.applyTo(constraintLayout)
                // redo margins
                (this.layoutParams as ConstraintLayout.LayoutParams).setMargins(defaultMargin, 0, 0, defaultMargin)
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
                constraintSet.clear(this.id, ConstraintSet.START)
                Log.d("4", "1")
                constraintSet.connect(this.id, ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END)
                Log.d("5", "1")
                constraintSet.applyTo(constraintLayout)
                Log.d("6", "1")
                //redo margins
                (this.layoutParams as ConstraintLayout.LayoutParams).setMargins(0, 0, defaultMargin, defaultMargin)
                this.setImageResource(R.drawable.ic_map_icon)
                Log.d("7", "1")
                //fragPager.setCurrentItem(0, true)
            }
        }
    }
}