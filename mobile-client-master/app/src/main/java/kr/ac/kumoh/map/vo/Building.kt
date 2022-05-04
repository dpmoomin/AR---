package kr.ac.kumoh.map.vo

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Building (
    val id: Integer, //데이터베이스 상에서 고유 번호
    val name: String, //빌딩 이름
    val identifier: String, //빌딩 건물 내부 방 식별자
    val lat: Double, //latitude
    @SerializedName("long") val lon: Double, //longitude
) : Serializable
