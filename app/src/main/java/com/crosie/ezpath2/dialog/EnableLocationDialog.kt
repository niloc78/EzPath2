package com.crosie.ezpath2.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.content.ContextCompat
import com.crosie.ezpath2.fragment.MapFragment
import com.crosie.ezpath2.R

class EnableLocationDialog : AppCompatDialogFragment() {
    lateinit var listener : LocationEnabledDialogListener
    lateinit var text : TextView
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.enable_location_dialog, null)
        val par = parentFragment as MapFragment

        val dialog = builder.setView(view)
                .setNegativeButton("Cancel") { _, _ ->

                }
                .setPositiveButton(if (par.map.isMyLocationEnabled) "Disable" else "Enable") { _, _->
                    listener.enableLocation(!par.map.isMyLocationEnabled)
                }.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                ContextCompat.getColor(requireContext(),
                    R.color.white
                ))
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(
                ContextCompat.getColor(requireContext(),
                    R.color.buttonteal
                ))
//
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                ContextCompat.getColor(requireContext(),
                    R.color.black
                ))
        text = view.findViewById(R.id.enable_location_text)
        text.text = if (par.map.isMyLocationEnabled) "Disable location tracking?" else "Enable location tracking?"

        return dialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            listener = parentFragment as LocationEnabledDialogListener
        } catch(e: ClassCastException) {
            throw ClassCastException("Calling fragment must implement LocationEnabledDialogListener")
        }
    }

    interface LocationEnabledDialogListener {
        fun enableLocation(b : Boolean)
    }

}