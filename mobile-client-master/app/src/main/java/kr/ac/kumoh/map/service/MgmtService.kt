package kr.ac.kumoh.map.service

import kr.ac.kumoh.map.vo.*
import retrofit2.Call
import retrofit2.http.*

interface MgmtService {
    @GET("building")
    fun building(): Call<GetBuildingResponse>

    @FormUrlEncoded
    @POST("building/create")
    fun createBuilding(
        @Field("uid") uid:Integer,
        @Field("name") name:String,
        @Field("idnt") idnt:String,
        @Field("x") x:Double,
        @Field("y") y:Double
    ) : Call<PostBuildingResponse>

    @FormUrlEncoded
    @POST("building/modify")
    fun updateBuilding(
        @Field("uid") uid:Integer,
        @Field("name") name:String,
        @Field("idnt") idnt:String,
        @Field("x") x:Double,
        @Field("y") y:Double
    ) : Call<PutBuildingResponse>

    @FormUrlEncoded
    @POST("building/delete")
    fun deleteBuilding(
        @Field("uid") uid:Integer
    ) : Call<DeleteBuildingResponse>

    @GET("facility")
    fun facility(): Call<GetFacilityResponse>

    @FormUrlEncoded
    @POST("facility/create")
    fun createFacility(
        @Field("uid") uid:Integer,
        @Field("name") name:String,
        @Field("bldnid") bldnid:Integer,
        @Field("x") x:Double,
        @Field("y") y:Double,
        @Field("floor") floor:Integer,
        @Field("dept") dept:String,
        @Field("idname") idname:String
    ) : Call<PostFacilityResponse>

    @FormUrlEncoded
    @POST("facility/modify")
    fun updateFacility(
        @Field("uid") uid:Integer,
        @Field("name") name:String,
        @Field("bldnid") bldnid:Integer,
        @Field("x") x:Double,
        @Field("y") y:Double,
        @Field("floor") floor:Integer,
        @Field("dept") dept:String,
        @Field("idname") idname: String
    ) : Call<PutFacilityResponse>
 
    @FormUrlEncoded
    @POST("facility/delete")
    fun deleteFacility(
        @Field("uid") uid:Integer
    ) : Call<DeleteFacilityResponse>

    @GET("phone")
    fun phone(): Call<GetPhoneResponse>

    @FormUrlEncoded
    @POST("phone/create")
    fun createPhone(
        @Field("uid") uid:Integer,
        @Field("facid") facid:Integer,
        @Field("num") num:String
    ) : Call<PostPhoneResponse>

    @FormUrlEncoded
    @POST("phone/modify")
    fun updatePhone(
        @Field("uid") uid:Integer,
        @Field("facid") facid:Integer,
        @Field("num") num:String
    ) : Call<PutPhoneResponse>

    @FormUrlEncoded
    @POST("phone/delete")
    fun deletePhone(
        @Field("uid") uid:Integer
    ) : Call<DeletePhoneResponse>

    @GET("ap")
    fun ap(): Call<GetAccesspointResponse>

    @FormUrlEncoded
    @POST("ap/create")
    fun createAp(
        @Field("uid") uid:Integer,
        @Field("name") name:String,
        @Field("bssid") bssid:String,
        @Field("facid") facid:Integer,
        @Field("floor") floor:Integer,
        @Field("x") x:Double,
        @Field("y") y:Double
    ) : Call<PostAccesspointResponse>

    @FormUrlEncoded
    @POST("ap/modify")
    fun updateAp(
        @Field("uid") uid:Integer,
        @Field("name") name:String,
        @Field("bssid") bssid:String,
        @Field("facid") facid:Integer,
        @Field("floor") floor:Integer,
        @Field("x") x:Double,
        @Field("y") y:Double
    ) : Call<PutAccesspointResponse>

    @FormUrlEncoded
    @POST("ap/delete")
    fun deleteAp(
        @Field("uid") uid:Integer
    ) : Call<DeleteAccesspointResponse>

    @FormUrlEncoded
    @POST("account/login")
    fun authentication(
        @Field("id") id:String,
        @Field("password") password:String
    ) : Call<AuthenticationResponse>
}