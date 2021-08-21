package com.example.ezpath2

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.transition.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MapFragment : Fragment() {
    lateinit var toggleNoteButton : FloatingActionButton
    lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    //private lateinit var transitionSet : Transition
    //private var defaultMargin = 0
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.map_frag_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
    }

    private fun initViews(v : View) {
        bottomSheetBehavior = BottomSheetBehavior.from(v.findViewById(R.id.bottom_sheet))
        toggleNoteButton = v.findViewById(R.id.toggle_note_button)
        toggleNoteButton.setOnClickListener {
            when(bottomSheetBehavior.state) {
                BottomSheetBehavior.STATE_COLLAPSED -> {
                    (this@MapFragment.activity as ErrandActivity).toggleFragButton.visibility = View.INVISIBLE
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                }

                BottomSheetBehavior.STATE_EXPANDED -> {
                    (this@MapFragment.activity as ErrandActivity).toggleFragButton.visibility = View.VISIBLE
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                }

            }
            //toggleNoteButton.toggleExpandSheet()
        }
        //transitionSet = TransitionInflater.from(context).inflateTransition(R.transition.animate)
        //defaultMargin = (toggleNoteButton.layoutParams as ConstraintLayout.LayoutParams).bottomMargin
        //transitionSet.duration = 250L
//        transitionSet.addListener(object : Transition.TransitionListener {
//            override fun onTransitionStart(transition: Transition) {
//                when(bottomSheetBehavior.state) {
//                    BottomSheetBehavior.STATE_COLLAPSED -> {
//
//                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
//                    }
//                    BottomSheetBehavior.STATE_EXPANDED -> {
//                        Handler(Looper.getMainLooper()).postDelayed({bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED}, 100L)
//                    }
//                }
////                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
////                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
////                }
//            }
//
//            override fun onTransitionEnd(transition: Transition) {
//            }
//            override fun onTransitionCancel(transition: Transition) {}
//            override fun onTransitionPause(transition: Transition) {}
//            override fun onTransitionResume(transition: Transition) {}
//
//        })
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> (this@MapFragment.activity as ErrandActivity).toggleFragButton.visibility = View.VISIBLE
                    BottomSheetBehavior.STATE_EXPANDED -> (this@MapFragment.activity as ErrandActivity).toggleFragButton.visibility = View.INVISIBLE
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }

        })

    }

//    fun resetButtonPosition() {
//        val par = toggleNoteButton.parent
//        val constraintLayout = par as ConstraintLayout
//        val constraintSet = ConstraintSet()
//        constraintSet.clone(constraintLayout)
//        constraintSet.clear(toggleNoteButton.id, ConstraintSet.TOP)
//        constraintSet.connect(toggleNoteButton.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
//        constraintSet.applyTo(constraintLayout)
//    }


//    private fun FloatingActionButton.toggleExpandSheet() {
//        val par = this.parent
//        when(bottomSheetBehavior.state) {
//            BottomSheetBehavior.STATE_COLLAPSED -> {
//                (this@MapFragment.activity as ErrandActivity).toggleFragButton.visibility = View.INVISIBLE
//                //do button animation move up
//                TransitionManager.go(Scene(par as ViewGroup), transitionSet)
//                val constraintLayout = par as ConstraintLayout
//                val constraintSet = ConstraintSet()
//                constraintSet.clone(constraintLayout)
//                constraintSet.clear(this.id, ConstraintSet.BOTTOM)
//                constraintSet.connect(this.id, ConstraintSet.TOP, R.id.coord_layout, ConstraintSet.TOP)
//                constraintSet.applyTo(constraintLayout)
//
//                //bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
//                (this.layoutParams as ConstraintLayout.LayoutParams).setMargins(0, defaultMargin, defaultMargin, 0)
//            }
//            BottomSheetBehavior.STATE_EXPANDED -> {
//                (this@MapFragment.activity as ErrandActivity).toggleFragButton.visibility = View.VISIBLE
//                TransitionManager.go(Scene(par as ViewGroup), transitionSet)
//                val constraintLayout = par as ConstraintLayout
//                val constraintSet = ConstraintSet()
//                constraintSet.clone(constraintLayout)
//                constraintSet.clear(this.id, ConstraintSet.TOP)
//                constraintSet.connect(this.id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
//                constraintSet.applyTo(constraintLayout)
//
//                //bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
//
//                (this.layoutParams as ConstraintLayout.LayoutParams).setMargins(0, 0, defaultMargin, defaultMargin)
//            }
//        }
//    }

}