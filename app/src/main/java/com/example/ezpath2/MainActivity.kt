package com.example.ezpath2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.updateLayoutParams
import androidx.transition.Scene
import androidx.transition.Transition
import androidx.transition.TransitionInflater
import androidx.transition.TransitionManager
import com.google.android.material.card.MaterialCardView

class MainActivity : AppCompatActivity() {
    lateinit var searchButton : MaterialCardView
    lateinit var set : Transition
    private var defaultButtonWidth : Int = 0
    private var defaultButtonRadius : Float = 0F
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
    }

    private fun initViews() {
        searchButton = findViewById(R.id.search_location_button)
        set = TransitionInflater.from(this).inflateTransition(R.transition.animate)
        set.duration = 200L
        Log.d("width", "" + searchButton.layoutParams.width)
        Log.d("radius", "" + searchButton.radius)
        searchButton.setOnClickListener {
            searchButton.toggleExpand()
        }
        defaultButtonWidth = searchButton.layoutParams.width
        defaultButtonRadius = searchButton.radius
        (findViewById<Button>(R.id.test_skip)).setOnClickListener {
            launchErrandActivity()
        }

    }

    private fun launchErrandActivity() {
        Intent(this, ErrandActivity::class.java).also { intent ->
            finish()
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        moveTaskToBack(false)
    }



    fun MaterialCardView.toggleExpand () {
        val par = this.parent
        when (this.isChecked) {
            false -> {
                Log.d("false", "called")
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
                        TransitionManager.go(Scene(par as ViewGroup), set)
                        updateLayoutParams {
                            width = 0
                        }
                        radius = 10F
                        val params = this.getChildAt(0).layoutParams as FrameLayout.LayoutParams
                        params.gravity = Gravity.LEFT or Gravity.CENTER_VERTICAL
                        params.setMargins(60, 0 , 0, 0)
                    }, 200L)
                }
                this.isChecked = true
            }
            true -> {
                Log.d("true", "called")
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
                    val params = this.getChildAt(0).layoutParams as FrameLayout.LayoutParams
                    params.gravity = Gravity.CENTER
                    params.setMargins(0, 0 , 0, 0)
                }
                this.isChecked = false
            }
        }

    }


}