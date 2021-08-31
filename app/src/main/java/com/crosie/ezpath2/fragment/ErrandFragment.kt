package com.crosie.ezpath2.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionInflater
import androidx.transition.TransitionManager
import com.crosie.ezpath2.*
import com.crosie.ezpath2.adapter.ErrandAdapter
import com.crosie.ezpath2.dialog.AddErrandDialog
import com.crosie.ezpath2.model.ErrandModel
import com.google.android.material.card.MaterialCardView
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap

class ErrandFragment : Fragment(), AddErrandDialog.AddErrandDialogListener {
    lateinit var sideBarButton : ImageView
    lateinit var locationText : TextView
    lateinit var locationCard : MaterialCardView
    lateinit var errandAdapter : ErrandAdapter
    lateinit var errandRecyclerView : RecyclerView
    private var data : ArrayList<LinkedHashMap<String,String>> = ArrayList()
    lateinit var errandModel : ErrandModel
    var errArray : Array<String>? = null
    private lateinit var linearLayoutManager : LinearLayoutManager
    private var index = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.errand_frag_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        errandModel = ViewModelProvider(requireActivity()).get(ErrandModel::class.java)
        parentFragmentManager.apply {
            setFragmentResultListener("markerReAdded", this@ErrandFragment, { _, result ->
                if (result.getBoolean("markerReAdded")) {
                    index ++
                    if (index <= errArray!!.lastIndex) {
                        reAdd(errArray!![index])
                    } else {
                        index = 0
                    }
                }
            })
        }
    }


    private fun initViews(v : View) {
        linearLayoutManager = LinearLayoutManager(context)
        locationCard = v.findViewById(R.id.location_card)
        locationCard.setOnClickListener {
            startChangeLocationForResult()

        }
        locationText = v.findViewById(R.id.location_text)
        locationText.text = (activity as ErrandActivity).currPlaceAddress
//        addErrandButton = v.findViewById(R.id.add_errand_button)
        errandRecyclerView = v.findViewById(R.id.errand_recycler_view)
//        data = populateExampleData()
        errandAdapter = ErrandAdapter(this, data)
        errandRecyclerView.apply {
//            isNestedScrollingEnabled = false
            layoutManager = linearLayoutManager
            adapter = errandAdapter
        }
        ItemTouchHelper(itemTouchCallback).attachToRecyclerView(errandRecyclerView)
        sideBarButton = v.findViewById(R.id.side_bar_button)
        sideBarButton.setOnClickListener {
            (activity as ErrandActivity).drawerLayout.openDrawer(GravityCompat.START)
        }





    }

    private fun startChangeLocationForResult() {
        locationLauncher.launch(Intent(context, MainActivity::class.java))
    }

    private val locationLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) { // received result
            val intent = result.data
            (activity as ErrandActivity).apply {
                currPlaceId = intent?.getStringExtra("currPlaceId")!!
                currPlaceAddress = intent.getStringExtra("currPlaceAddress")!!
                currPlaceLatLng = intent.getDoubleArrayExtra("currPlaceLatLng")!!
                locationText.text = currPlaceAddress
            }
            locationChanged()
        } else {
         //   Log.d("ActivityForResult", "Location not selected")
        }
    }

    private fun locationChanged() {
        data.clear()
        errandRecyclerView.adapter?.notifyDataSetChanged()
        errandModel.bestResults.value!!.clear()
        parentFragmentManager.apply {
            val result = Bundle()
            result.putBoolean("locationChanged", true)
            setFragmentResult("locationChanged", result)
        }
        errArray = errandModel.errands.value!!.toTypedArray()
        errandModel.errands.value!!.clear()

        if (!errArray.isNullOrEmpty()) {
            reAdd(errArray!![index])
        }

    }



    fun toggleSideBarButtonAnim() {
        val set = TransitionInflater.from(context).inflateTransition(R.transition.animate)
        set.duration = 150
        sideBarButton.apply {
            TransitionManager.beginDelayedTransition(this.parent as ViewGroup, set)
            rotation = when (isSelected) {
                true -> {
                    0F
                }
                else -> {
                    90F
                }
            }
            isSelected = !isSelected
        }


    }

    fun storeSet(setName : String) {
        val c = requireContext()
        val prefs = c.getSharedPreferences(c.packageName + "_preferences", Context.MODE_PRIVATE)
        if (prefs.getStringSet(setName, null).isNullOrEmpty()) {
            if (errandModel.errands.value!!.isNotEmpty()) {
                prefs.edit().putStringSet(setName, errandModel.errands.value!!.toHashSet()).commit()
                (requireActivity() as ErrandActivity).apply {
                    setData.add(setName)
                    savedSetsAdapter.notifyItemInserted(setData.lastIndex)
                }
            } else {
                Toast.makeText(c, "You must add at least one errand", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(c, "Set name already exists", Toast.LENGTH_SHORT).show()
        }
    }

    fun loadSet(setName : String) {
        val c = requireContext()
        val prefs = c.getSharedPreferences(c.packageName + "_preferences", Context.MODE_PRIVATE)
        val set = prefs.getStringSet(setName, null)
        if (!set.isNullOrEmpty()) {
            //Log.d("loadset", "set exists")
            errandModel.errands.value = ArrayList(set.toTypedArray().toList())
            locationChanged()
        } else {
            //Log.d("loadset", "set was null or empty")
        }
    }

    private val itemTouchCallback : ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.START or ItemTouchHelper.END or
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {

            val fromPos = viewHolder.adapterPosition
            val toPosition = target.adapterPosition
            if (toPosition > data.lastIndex) return false
            Collections.swap(data, fromPos, toPosition)
            errandRecyclerView.adapter?.notifyItemMoved(fromPos, toPosition)
            Collections.swap(errandModel.errands.value, fromPos, toPosition)
            Collections.swap(errandModel.bestResults.value, fromPos, toPosition)
            parentFragmentManager.apply {
                val result = Bundle()
                result.putBoolean("markerOrderChanged", true)
                result.putInt("fromPos", fromPos)
                result.putInt("toPos", toPosition)
                setFragmentResult("markerOrderChanged", result)
            }
            //Log.d("itemmoved", "$fromPos to $toPosition")
            return true
        }

        override fun isLongPressDragEnabled(): Boolean {
            return true
        }
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            if (viewHolder is ErrandAdapter.FooterViewHolder) return makeMovementFlags(0,0)
            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END
            val swipeFlags = ItemTouchHelper.LEFT
            return makeMovementFlags(dragFlags, swipeFlags)
        }

        override fun getSwipeDirs(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            if(viewHolder is ErrandAdapter.FooterViewHolder) return 0
            return super.getSwipeDirs(recyclerView, viewHolder)
        }



        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val pos = viewHolder.adapterPosition
            data.removeAt(pos)
            errandRecyclerView.adapter?.notifyItemRemoved(pos)
            errandModel.errands.value!!.removeAt(pos)
            errandModel.bestResults.value!!.removeAt(pos)
            // call update poly
            parentFragmentManager.apply {
                val result = Bundle()
                result.putBoolean("errandRemoved", true)
                result.putInt("indexRemoved", pos)
                setFragmentResult("errandRemoved", result)
            }
        }
    }

//    private fun populateExampleData() : ArrayList<LinkedHashMap<String,String>> {
//        val arrList = ArrayList<LinkedHashMap<String,String>>()
//        for (i in 0..2) {
//            arrList.add(linkedMapOf("errandName" to "Buy Pencils", "storeName" to "CW Enterprise", "address" to "15 Orchard Street"))
//        }
//        return arrList
//    }
    fun openDialog() {
        //Log.d("openDialog", "called")
        val dialog = AddErrandDialog()
        dialog.show(childFragmentManager, "errand dialog")
    }

    private fun reAdd(errand : String) {
        val errActivity = activity as ErrandActivity
        errandModel.currPlaceInfo.value = hashMapOf("id" to errActivity.currPlaceId, "latLng" to errActivity.currPlaceLatLng)

        errandModel.errands.value!!.add(errand)
        errandModel.getPlaceResult(errActivity.radiusSlider.value.toInt(), errActivity.price_level, errActivity.ratingBar.rating.toDouble(),
            errActivity.chipGroup.checkedChipId == R.id.chip_rating
        ) { result ->
            if (result != null) {
                data.add(linkedMapOf("errandName" to errand, "storeName" to result.name, "address" to result.formatted_address))
                errandRecyclerView.adapter?.notifyItemInserted(data.lastIndex)
                //update poly
                parentFragmentManager.apply {
                    val r = Bundle()
                    r.putBoolean("errandReAdded", true)
                    setFragmentResult("errandReAdded", r)
                }
            } else {
                errandModel.errands.value!!.removeLast()
                parentFragmentManager.apply {
                    val r = Bundle()
                    r.putBoolean("errandReAdded", true)
                    setFragmentResult("errandReAdded", r)
                }
                Toast.makeText(context, "Could not find a result for the inputted errand: $errand" , Toast.LENGTH_SHORT).show()
            }

        }

    }

    override fun addErrand(errand: String) {
        val errActivity = activity as ErrandActivity
        errandModel.currPlaceInfo.value = hashMapOf("id" to errActivity.currPlaceId, "latLng" to errActivity.currPlaceLatLng)
        //errandModel.currLatLng.value = (activity as ErrandActivity).currPlaceLatLng

        errandModel.errands.value!!.add(errand)
        errandModel.getPlaceResult(errActivity.radiusSlider.value.toInt(), errActivity.price_level, errActivity.ratingBar.rating.toDouble(),
            errActivity.chipGroup.checkedChipId == R.id.chip_rating
        ) { result ->
            if (result != null) {
                data.add(linkedMapOf("errandName" to errand, "storeName" to result.name, "address" to result.formatted_address))
                errandRecyclerView.adapter?.notifyItemInserted(data.lastIndex)
                //update poly
                parentFragmentManager.apply {
                    val r = Bundle()
                    r.putBoolean("errandAdded", true)
                    setFragmentResult("errandAdded", r)
                }
            } else {
                errandModel.errands.value!!.removeLast()
                Toast.makeText(context, "Could not find a result for the inputted errand", Toast.LENGTH_SHORT).show()
            }

        }

    }


}