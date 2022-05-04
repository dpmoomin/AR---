package kr.ac.kumoh.map

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.gson.annotations.SerializedName
import kr.ac.kumoh.map.vo.Facility
import kr.ac.kumoh.map.vo.GetFacilityResponse
import kr.ac.kumoh.map.vo.PostFacilityResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FacilityCreateFragment : Fragment(), View.OnClickListener {
    private lateinit var callback: OnBackPressedCallback
    lateinit var facility : Facility
    private lateinit var facilityCreateBtn: Button
    lateinit var id: EditText //데이터베이스 상에서의 고유번호
    lateinit var name: EditText //시설물 이름
    lateinit var building: EditText //빌딩 고유번호
    lateinit var lat: EditText //latitude
    lateinit var lon: EditText //longitude
    lateinit var floor: EditText //위치한 층 수
    lateinit var department: EditText //학과 (학과 사무실이라면)
    lateinit var idname : EditText
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_facility, container, false)

        facilityCreateBtn = view.findViewById(R.id.facilityCreateBtn)
        facilityCreateBtn.setOnClickListener(this)
        id = view.findViewById(R.id.facility_id_input)
        name = view.findViewById(R.id.facility_name_input)
        building = view.findViewById(R.id.facility_building_input)
        lat = view.findViewById(R.id.facility_lat_input)
        lon = view.findViewById(R.id.facility_long_input)
        floor = view.findViewById(R.id.facility_floor_input)
        department = view.findViewById(R.id.facility_department_input)
        idname = view.findViewById(R.id.facility_idname_input)

        return view
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.facilityCreateBtn -> {

                val call: Call<PostFacilityResponse> = ApiClient.mgmtService
                    .createFacility(
                    id.text.toString().toInt() as Integer,
                    name.text.toString(),
                        building.text.toString().toInt() as Integer,
                    lat.text.toString().toDouble(),
                    lon.text.toString().toDouble(),
                        floor.text.toString().toInt() as Integer,
                        department.text.toString(),
                        idname.text.toString()
                    )
                call.enqueue(object: Callback<PostFacilityResponse> {
                    override fun onResponse(
                        call: Call<PostFacilityResponse>,
                        response: Response<PostFacilityResponse>
                    ) {
                        Log.d("Post", response.toString())
                        if (response.isSuccessful.not()) {
                            Log.d("Post", "Post failed")
                            return
                        }
                        response.body()?.let {
                        }
                    }

                    override fun onFailure(call: Call<PostFacilityResponse>, t: Throwable) {
                        Log.d("Post", t.toString())
                    }
                })

                action_nav_facility_create_to_read()
            }
        }
    }

    private fun action_nav_facility_create_to_read() {
        val navController: NavController =
            Navigation.findNavController(activity as AdminActivity, R.id.admin_nav_host_fragment)
        navController.navigate(R.id.action_nav_facility_create_to_read)
    }
}