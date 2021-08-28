package com.crosie.ezpath2.data

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil

data class ErrandResults(val html_attributions : Array<String>,
                         val next_page_token : String,
                         val results : Array<Result>,
                         var bestPlace : Result,
                         val errand : String,
                         var distMatrix : IntArray
                        ) {

    fun calculateDistanceMatrix(srcLatLng : DoubleArray) : IntArray {
        val latLng = LatLng(srcLatLng[0], srcLatLng[1])
        distMatrix = IntArray(results.size)
        for (i in 0..results.size-1) {
            val distance = SphericalUtil.computeDistanceBetween(latLng, LatLng(results[i].geometry["location"]!!["lat"]!! as Double, results[i].geometry["location"]!!["lng"]!! as Double))
            distMatrix[i] = distance.toInt()
        }
        return distMatrix
    }

    fun chooseBestPlace(r : Int, price_level : Int, rating : Double, chip_rating : Boolean) : Result {
        bestPlace = results[0]
        for (i in 0..results.size - 2) {
            if (distMatrix[i] > r) {
                continue
            }
            if (results[i].price_level != price_level && price_level != 0) {
                continue
            }
            if (results[i].rating < rating) {
                continue
            }
            if (chip_rating && results[i + 1].rating > bestPlace.rating) {
                bestPlace = results[i+1]
            }
        }
        return bestPlace

    }
}
//    lateinit var html_attributions : Array<String>
//    lateinit var next_page_token : String
//    lateinit var results : Array<Result>
//    lateinit var bestPlace : Result
//    lateinit var errand : String
//    lateinit var distMatrix : Array<IntArray>
