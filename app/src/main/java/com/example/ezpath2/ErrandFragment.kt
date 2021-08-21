package com.example.ezpath2

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashMap

class ErrandFragment : Fragment() {
    lateinit var sideBarButton : ImageButton
    lateinit var addErrandButton : MaterialCardView
    lateinit var locationCard : MaterialCardView
    lateinit var errandAdapter : ErrandAdapter
    lateinit var errandRecyclerView : RecyclerView
    private var data : ArrayList<LinkedHashMap<String,String>> = ArrayList()
    private lateinit var linearLayoutManager : LinearLayoutManager

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
    }


    private fun initViews(v : View) {
        linearLayoutManager = LinearLayoutManager(context)
        locationCard = v.findViewById(R.id.location_card)
//        addErrandButton = v.findViewById(R.id.add_errand_button)
        errandRecyclerView = v.findViewById(R.id.errand_recycler_view)
        data = populateExampleData()
        errandAdapter = ErrandAdapter(data)
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
//        addErrandButton.setOnClickListener {
//            data.add(linkedMapOf("errandName" to "Buy Pencils", "storeName" to "CW Enterprise", "address" to "15 Orchard Street"))
//            errandRecyclerView.adapter?.notifyItemInserted(data.lastIndex)
//        }
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
            Collections.swap(data, fromPos, toPosition)
            errandRecyclerView.adapter?.notifyItemMoved(fromPos, toPosition)

            return true
        }
        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            // call update all here
            //Log.d("clearview", "called")
            super.clearView(recyclerView, viewHolder)
//            for (ec in data) { // sort and update in ui list first
//                //Log.d("previous prio: ", "" + ec.priority)
//                ec.priority = data.indexOf(ec)
//                //Log.d("updated prio: ",  "to " + ec.priority)
//            }
//            runBlocking {
//                val dao = (requireActivity() as MainActivity).db.econtactDAO()
//                dao.updateAll(*data.toTypedArray())
//                parentFragmentManager.apply {
//                    val result = Bundle()
//                    result.putStringArray("eContactNums", buildNumsArray())
//                    result.putStringArray("eContactNames", buildNameArray())
//                    this.setFragmentResult("eContactsChanged", result)
//                }
//                //econtactRecyclerView.adapter?.notifyDataSetChanged()
//            }

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
            // call delete here
//            runBlocking {
//                val dao = (requireActivity() as MainActivity).db.econtactDAO()
//                dao.deleteAll(data.removeAt(pos))
//                econtactRecyclerView.adapter?.notifyItemRemoved(pos)
//            }

        }
    }

    private fun populateExampleData() : ArrayList<LinkedHashMap<String,String>> {
        val arrList = ArrayList<LinkedHashMap<String,String>>()
        for (i in 0..2) {
            arrList.add(linkedMapOf("errandName" to "Buy Pencils", "storeName" to "CW Enterprise", "address" to "15 Orchard Street"))
        }
        return arrList
    }
}