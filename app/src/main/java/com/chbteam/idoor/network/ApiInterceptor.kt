package com.chbteam.idoor.network

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.chbteam.idoor.R
import com.chbteam.idoor.views.MainActivity
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import okhttp3.RequestBody
import okhttp3.MediaType
import okhttp3.MultipartBody
import java.lang.Exception


class ApiInterceptor(context: Context) {

    val appContext = context

    var retrofit : Retrofit? = null

    fun getNewProfile() : MutableLiveData<String?>{

        val profileLiveData = MutableLiveData<String?>()

        initRetrofit()
        val request = buildService(iDoorApi::class.java)

        val body = JsonObject()
        body.addProperty("locale","en-us")

        request?.createProfile(appContext.resources.getString(R.string.API_KEY),body = body)?.enqueue(object :
            Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                if (response.isSuccessful){
                    val profileId = response.body()?.asJsonObject?.get("identificationProfileId")?.asString
                    profileLiveData.postValue(profileId)
                }else{
                    profileLiveData.postValue(null)
                }
            }
            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                profileLiveData.postValue(null)
            }
        })
        return profileLiveData
    }

    fun enrollProfile(fileUri : Uri, profile : String , activity: MainActivity) : MutableLiveData<String?>{
        val resultLiveData = MutableLiveData<String?>()

        try {
            val request = buildService(iDoorApi::class.java)

            val file = File(fileUri.path!!)

            val requestFile = RequestBody.create(
                MediaType.parse(activity.contentResolver.getType(fileUri)!!),
                file
            )

            val body = MultipartBody.Part.createFormData("audio", file.name, requestFile)

            request?.createEnrollement(userId = profile,contentType = "multipart/form-data",ApiKey =appContext.resources.getString(R.string.API_KEY),file = body )?.enqueue(object :
                Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful){
                        resultLiveData.postValue(response.body())
                    }else{
                        resultLiveData.postValue(null)
                    }
                }
                override fun onFailure(call: Call<String>, t: Throwable) {
                    resultLiveData.postValue(null)
                }
            })
        }catch (e : Exception){
            e.printStackTrace()
        }

        return resultLiveData
    }

    private fun initRetrofit(){
        retrofit = Retrofit.Builder()
            .baseUrl(appContext.resources.getString(R.string.API_ENDPOINT))
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().build())
            .build()
    }
    private fun<T> buildService(service: Class<T>): T?{
        return retrofit?.create(service)
    }

}