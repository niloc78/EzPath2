package com.example.ezpath2

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.VolleyError
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import org.json.JSONObject

class ErrandModel(application: Application) : AndroidViewModel(application) {
    val errands = MutableLiveData<ArrayList<String>>(ArrayList())
    val currPlaceInfo = MutableLiveData<HashMap<String, Any>>()
    val bestResults = MutableLiveData<ArrayList<Result>>(ArrayList())
    private val context = getApplication<Application>().applicationContext
    private val key = BuildConfig.PLACES_API_KEY
    val pathDecodedPoly = MutableLiveData<List<LatLng>>()

    fun getPlaceResult(radius : Int, priceLevel : Int, rating : Double, chipRating : Boolean, onSuccess : (result : Result?) -> Unit) {
        val currErrand = errands.value!!.last()
        val lat = (currPlaceInfo.value!!["latLng"] as DoubleArray)[0]
        val lng = (currPlaceInfo.value!!["latLng"] as DoubleArray)[1]
        val location = "&location=$lat,$lng"
        val query = "&query=" + currErrand.replace(" ", "+")
        val key = "&key=$key"
        val rankby = "&rankby=distance"
        val url = "https://maps.googleapis.com/maps/api/place/textsearch/json?$query$location$rankby$key"

        val kIResult : IResult = object : IResult {
            override fun notifySuccess(requestType: String, response: JSONObject) {
                Log.d("response", response.toString())
                val objectMapper = Gson()
                val errandResults = objectMapper.fromJson(response.toString(), ErrandResults::class.java)

                if (errandResults.results.isNullOrEmpty()) {
                    onSuccess(null)
                } else {
                    val distMatrix = errandResults.calculateDistanceMatrix(currPlaceInfo.value!!["latLng"] as DoubleArray)
                    bestResults.value!!.add(errandResults.chooseBestPlace(radius, priceLevel, rating, chipRating))

                    onSuccess(bestResults.value!!.last())
                    Log.d("distMatrix", distMatrix.joinToString())
                }


//                updatePoly {
//                    onSuccess(it)
//                }


            }

            override fun notifyError(requestType: String, error: VolleyError) {
                Log.d("response", "error")
                onSuccess(null)
            }

        }

        val kGetUrlContent = GetUrlContent(kIResult, context)
        kGetUrlContent.getDataVolley("GETCALL", url)


    }


    fun updatePoly(onSuccess : (dpoly : List<LatLng>?) -> Unit) {
        if (bestResults.value!!.size > 0) {
            val path = Path(bestResults.value!!, context)
            path.getDecodedPoly(currPlaceInfo.value!!["id"] as String) { decodedPoly ->
                pathDecodedPoly.value = decodedPoly
                Log.d("decodedPoly", "success: " + pathDecodedPoly.value!!.joinToString())
                onSuccess(pathDecodedPoly.value!!)
            }
        } else {
            onSuccess(null)
        }
    }

//    fun getPlaceResults() {
//        val currErrand = errands.value!!.last()
//        val lat = (currPlaceInfo.value!!["latLng"] as DoubleArray)[0]
//        val lng = (currPlaceInfo.value!!["latLng"] as DoubleArray)[1]
//        val location = "&location=$lat,$lng"
//        val query = "&query=" + currErrand.replace(" ", "+")
//        val key = "&key=$key"
//        val rankby = "&rankby=distance"
//        val url = "https://maps.googleapis.com/maps/api/place/textsearch/json?$query$location$rankby$key"
//        //mGetUrlContent.getDataVolley("GETCALL", url)
//    }




}