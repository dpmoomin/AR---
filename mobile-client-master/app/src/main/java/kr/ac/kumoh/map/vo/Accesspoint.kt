package kr.ac.kumoh.map.vo

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Accesspoint (
    val id: Integer, //데이터베이스 상에서의 고유번호
    val name: String, //관리자 정의 AP 이름
    val bssid: String,
    val facility: Integer, //관련 시설물 ID
    @SerializedName("facility_name") val facilityName: String, //관련 시설물 이름
    val lat: Double, //latitude
    val long: Double, //longitude
    val floor: Integer //위치한 층 수
) : Serializable
