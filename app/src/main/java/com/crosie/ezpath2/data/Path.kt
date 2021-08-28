package com.crosie.ezpath2.data

import android.content.Context
import com.android.volley.VolleyError
import com.crosie.ezpath2.BuildConfig
import com.crosie.ezpath2.GetUrlContent
import com.crosie.ezpath2.IResult
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import org.json.JSONObject

data class Path(val bestResults: ArrayList<Result>, val context: Context) {

     fun getDecodedPoly(sourceId: String, onSuccess : (dpoly : List<LatLng>) -> Unit) {

        val mIResult : IResult = object : IResult {
            override fun notifySuccess(requestType: String, response: JSONObject) {
                //Log.d("Direction results ", "Direction results: $response")
                val routes = response.getJSONArray("routes")
                val route = routes.getJSONObject(0)
                val polyline = route.getJSONObject("overview_polyline")
                val overviewPoly = polyline.getString("points")
                //Log.d("overview_polyline", "overview polyline: $polyline")
                val decodedPoly = PolyUtil.decode(overviewPoly)
                //Log.d("decoded_poly", "decoded poly: $decodedPoly")
                onSuccess(decodedPoly)
            }

            override fun notifyError(requestType: String, error: VolleyError) {
                //Log.d("getpolyline", "error")
            }

        }

        val mGetUrlContent = GetUrlContent(mIResult, context)
        val origin = "origin=place_id:$sourceId"
        val destination = "&destination=place_id:$sourceId"
        var waypoints = "&waypoints=optimize:true|" + "place_id:" + bestResults[0].place_id
        val api_key = "&key=" + BuildConfig.PLACES_API_KEY
        for (i in 1 until bestResults.size) {
            waypoints += "|place_id:" + bestResults[i].place_id
        }
        val url =
            "https://maps.googleapis.com/maps/api/directions/json?$origin$destination$waypoints$api_key"
        mGetUrlContent.getDataVolley("GETCALL", url)

    }

}