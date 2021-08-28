package com.crosie.ezpath2.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.crosie.ezpath2.ErrandActivity
import com.crosie.ezpath2.R
import com.google.android.material.card.MaterialCardView

class SetAdapter(val errActivity : ErrandActivity, var data : ArrayList<String>) : RecyclerView.Adapter<SetAdapter.ViewHolder>() {


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val setName : TextView = itemView.findViewById(R.id.set_name)
        val setContainer : ConstraintLayout = itemView.findViewById(R.id.set_item_container)
        val setCard : MaterialCardView = itemView.findViewById(R.id.set_card)
        val deleteButton : ImageButton = itemView.findViewById(R.id.delete_button)

        fun bind(pos : Int) {
            setName.text = data[pos]
            setName.apply {
                setOnClickListener {
                    errActivity.openConfirmLoadSetDialog(setName.text as String)
                }
                setOnLongClickListener {
                    setContainer.performLongClick()
                    true
                }
            }

            setContainer.apply {
                setOnLongClickListener {
                    deleteButton.visibility = View.VISIBLE
                    this.isSelected = true
                    setCard.isSelected = true
                    setName.isSelected = true
                    setName.isEnabled = false
                    true
                }
                setOnClickListener {
                    deleteButton.visibility = View.INVISIBLE
                    this.isSelected = false
                    setCard.isSelected = false
                    setName.isSelected = false
                    setName.isEnabled = true
                }
            }
            deleteButton.setOnClickListener {
                setContainer.performClick()
                val prefs = errActivity.getSharedPreferences(errActivity.packageName + "_preferences", Context.MODE_PRIVATE)

                if (!prefs.getStringSet(setName.text as String, null).isNullOrEmpty()) {
                    prefs.edit().remove(setName.text as String).commit()
                    data.removeAt(this.adapterPosition)
                    notifyItemRemoved(this.adapterPosition)
                    //Log.d("removing", "success")
                } else {
                    //Log.d("removing", "error")
                }


            }

        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.set_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return data.size
    }
}