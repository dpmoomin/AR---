package kr.ac.kumoh.map.vo

import java.io.Serializable

data class Phone (
    val id: Integer, //데이터베이스 상에서의 고유번호
    val facid: Integer, //시설물 아이디
    val name: String, //전화번호 이름 (담당자 이름, 혹은 부서 등)
    val number: String //전화번호
) : Serializable
