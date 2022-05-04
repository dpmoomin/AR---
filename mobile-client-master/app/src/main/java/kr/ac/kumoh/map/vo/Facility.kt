package kr.ac.kumoh.map.vo

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

data class Facility(
    val id: Integer, //데이터베이스 상에서의 고유번호
    val name: String, //시설물 이름
    val building: Integer, //빌딩 고유번호
    @SerializedName("building_name") val buildingName: String, //빌딩 이름
    val lat: Double, //latitude
    @SerializedName("long") val lon: Double, //longitude
    val floor: Integer, //위치한 층 수
    val department: String, //학과 (학과 사무실이라면)
    val identifiername: String
) : java.io.Serializable
