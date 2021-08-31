package com.crosie.ezpath2

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import java.lang.Exception

class GetUrlContent(val resultCallBack : IResult, val context : Context) {

    fun getDataVolley(requestType: String, url : String) {
        try {
            val queue = Volley.newRequestQueue(context)
            val jsonObj = JsonObjectRequest(Request.Method.GET, url, null, { response ->
                    resultCallBack.notifySuccess(requestType, response)
            }, { error ->
                    resultCallBack.notifyError(requestType, error)
            })

            queue.add(jsonObj)

        } catch (e : Exception) {
            e.printStackTrace()
        }
    }

}