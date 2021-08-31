package com.crosie.ezpath2.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.crosie.ezpath2.R
import java.lang.ClassCastException

class AddErrandDialog : AppCompatDialogFragment() {
    private lateinit var edit_errand : EditText
    private lateinit var listener : AddErrandDialogListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.add_errand_dialog, null)
        val dialog = builder.setView(view).apply {
            setNegativeButton("Cancel") { _, _ ->

            }
            setPositiveButton("Add") { _, _ ->
                val errand = edit_errand.text.toString().replaceFirst(edit_errand.text.toString().first(), edit_errand.text.toString().first().toUpperCase())
                listener.addErrand(errand)
            }
        }.show()
        edit_errand = view.findViewById(R.id.edit_errand)

        return dialog
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = parentFragment as AddErrandDialogListener
        } catch (e : ClassCastException) {
            throw ClassCastException("must implement AddErrandDialog")
        }

    }

    interface AddErrandDialogListener {
        fun addErrand(errand : String)
    }

}