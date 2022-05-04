package kr.ac.kumoh.map

import kr.ac.kumoh.map.service.MgmtService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    val BASE_URL: String = "http://map-api.0xffff.host/api/"

    private fun retrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val mgmtService : MgmtService by lazy {
        retrofit().create(MgmtService::class.java)
    }
}