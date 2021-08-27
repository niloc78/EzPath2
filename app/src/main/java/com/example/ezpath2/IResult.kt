package com.example.ezpath2

import com.android.volley.VolleyError
import org.json.JSONObject

interface IResult {
    fun notifySuccess(requestType: String, response : JSONObject)
    fun notifyError(requestType: String, error : VolleyError)
}