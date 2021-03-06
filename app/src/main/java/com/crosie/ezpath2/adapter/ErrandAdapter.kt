package com.crosie.ezpath2.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.crosie.ezpath2.R
import com.crosie.ezpath2.fragment.ErrandFragment
import com.google.android.material.card.MaterialCardView

class ErrandAdapter(val parFrag : ErrandFragment, var data : ArrayList<LinkedHashMap<String, String>>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ErrandViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val errandName : TextView = itemView.findViewById(R.id.errand_name)
        private val storeName : TextView = itemView.findViewById(R.id.store_name)
        private val address : TextView = itemView.findViewById(R.id.address)
        private val checkBox : CheckBox = itemView.findViewById(R.id.errand_check_box)
        private val errandCard : MaterialCardView = itemView.findViewById(R.id.errand_card)

        fun bind(errName : String, storName : String, addr : String ) {
            errandName.text = errName
            storeName.text = storName
            address.text = addr
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                when(isChecked) {
                    true -> {
                        errandCard.isChecked = true
                        parFrag.parentFragmentManager.apply {
                            val result = Bundle()
                            result.putBoolean("checked", true)
                            result.putInt("index", adapterPosition)
                            setFragmentResult("errandCheckChanged", result)
                        }
                    }
                    else -> {
                        errandCard.isChecked = false
                        parFrag.parentFragmentManager.apply {
                            val result = Bundle()
                            result.putBoolean("checked", false)
                            result.putInt("index", adapterPosition)
                            setFragmentResult("errandCheckChanged", result)
                        }
                    }
                }
            }
        }

    }

    inner class FooterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val addButton : MaterialCardView = itemView.findViewById(R.id.add_errand_button)

        fun bind() {
            addButton.setOnClickListener {
                if (data.size == 10) {
                    Toast.makeText(parFrag.context, "You've reached the limit of 10 errands", Toast.LENGTH_SHORT).show()
                } else {
                    parFrag.openDialog()
                }
//                data.add(linkedMapOf("errandName" to "Buy Pencils", "storeName" to "CW Enterprise", "address" to "15 Orchard Street"))
//                this@ErrandAdapter.notifyItemInserted(data.lastIndex)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.errand_item_layout -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.errand_item_layout, parent, false)
                ErrandViewHolder(view)
            }
            R.layout.errand_footer -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.errand_footer, parent, false)
                FooterViewHolder(view)
            }
            else -> throw IllegalArgumentException("unknown view type $viewType")
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position)) {
            R.layout.errand_item_layout -> (holder as ErrandViewHolder).bind(data[position]["errandName"]!!, data[position]["storeName"]!!,
                data[position]["address"]!!)
            R.layout.errand_footer -> (holder as FooterViewHolder).bind()
        }



//        holder.apply {
//            errandName.text = data[position]["errandName"]
//            storeName.text = data[position]["storeName"]
//            address.text = data[position]["address"]
//            checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
//                when(isChecked) {
//                    true -> {
//                        errandCard.isChecked = true
//                    }
//                    else -> {
//                        errandCard.isChecked = false
//                    }
//                }
//            }
//        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            data.size -> R.layout.errand_footer
            else -> R.layout.errand_item_layout
        }
    }

    override fun getItemCount(): Int {
       return data.size + 1
    }


}