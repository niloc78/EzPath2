package com.crosie.ezpath2.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.crosie.ezpath2.R
import java.lang.ClassCastException

class SaveSetDialog : AppCompatDialogFragment() {
    private lateinit var edit_set_name : EditText
    private lateinit var listener : SaveSetDialogListener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.save_set_dialog_layout, null)
        val dialog = builder.setView(view).apply {
            setNegativeButton("Cancel") { dialogInterface, i ->

            }
            setPositiveButton("Save") { dialogInterface, i ->
                val name = edit_set_name.text.toString()
                listener.saveSet(name)
            }
        }.show()
        edit_set_name = view.findViewById(R.id.edit_set_name)

        return dialog
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = activity as SaveSetDialogListener
        } catch (e : ClassCastException) {
            throw ClassCastException("must implement SaveSetDialogListener")
        }

    }

    interface SaveSetDialogListener {
        fun saveSet(name : String)
    }
}