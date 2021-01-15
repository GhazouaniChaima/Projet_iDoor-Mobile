package com.chbteam.idoor.network

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface iDoorApi {

    @POST("spid/v1.0/identificationProfiles")
    fun createProfile(@Header("Ocp-Apim-Subscription-Key") ApiKey : String, @Body body : JsonObject): Call<JsonElement>

    @POST("spid/v1.0/identificationProfiles/{profile_id}/enroll")
    fun createEnrollement(@Path(value = "profile_id", encoded = true) userId : String, @Header("Content-Type") contentType : String , @Header("Ocp-Apim-Subscription-Key") ApiKey : String, @Part  file : MultipartBody.Part): Call<String>

}