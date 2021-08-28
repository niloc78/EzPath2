package com.example.ezpath2

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.content.ContextCompat

class ConfirmLoadSetDialog(val setName : String) : AppCompatDialogFragment() {
    lateinit var load_this_set : TextView
    lateinit var set_list_text : TextView
    lateinit var listener : ConfirmLoadSetDialogListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.confirm_load_path_dialog_layout, null)

        val dialog = builder.setView(view)
            .setNegativeButton("No") { _, _ ->

            }
            .setPositiveButton("Yes") { _, _->
                listener.confirmLoadSet(setName)
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

        load_this_set = view.findViewById(R.id.load_this_errand_Set)
        set_list_text = view.findViewById(R.id.set_list_text)

        load_this_set.text = "Load $setName?"
        set_list_text.apply {
            text = requireActivity().getSharedPreferences(requireActivity().packageName + "_preferences", Context.MODE_PRIVATE).getStringSet(setName, null)?.joinToString(",")
            //getSharedPreferences(this.packageName + "_preferences", Context.MODE_PRIVATE )
        }

        return dialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            listener = activity as ConfirmLoadSetDialogListener
        } catch(e: ClassCastException) {
            throw ClassCastException("Calling fragment must implement CancelServiceDialogListener")
        }
    }

    interface ConfirmLoadSetDialogListener {
        fun confirmLoadSet(setName : String)
    }

}