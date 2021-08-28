package com.crosie.ezpath2.data

data class Result(val business_status : String,
                  val price_level : Int,
                  val formatted_address : String,
                  val name : String,
                  val place_id : String,
                  val rating : Double,
                  val opening_hours : HashMap<String,Boolean>,
                  val geometry : HashMap<String, HashMap<String, Any>>
                  )
//    lateinit var business_status : String
//    var price_level : Int = 0
//    lateinit var formatted_address : String
//    lateinit var name : String
//    lateinit var place_id : String
//    var rating : Double = 0.toDouble()
//    lateinit var opening_hours : HashMap<String, Boolean>
