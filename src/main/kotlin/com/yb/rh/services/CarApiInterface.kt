package com.yb.rh.services

import com.yb.rh.services.ilcarapi.IlCarJson
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CarApiInterface {

    // Israeli Car Api
    @GET("datastore_search?resource_id=053cea08-09bc-40ec-8f7a-156f0677aff3")
    fun getData(@Query("q") plateNumber: String): Call<IlCarJson>

}