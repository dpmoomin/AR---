package kr.ac.kumoh.map

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.gson.annotations.SerializedName
import kr.ac.kumoh.map.vo.Facility
import kr.ac.kumoh.map.vo.GetFacilityResponse
import kr.ac.kumoh.map.vo.PostFacilityResponse
import kr.ac.kumoh.map.vo.PutFacilityResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FacilityUpdateFragment : Fragment(), View.OnClickListener {
    lateinit var facility : Facility
    private lateinit var facilityUpdateBtn: Button
    lateinit var id: EditText //데이터베이스 상에서의 고유번호
    lateinit var name: EditText //시설물 이름
    lateinit var building: EditText //빌딩 고유번호
    lateinit var lat: EditText //latitude
    lateinit var lon: EditText //longitude
    lateinit var floor: EditText //위치한 층 수
    lateinit var department: EditText //학과 (학과 사무실이라면)
    lateinit var identifiername : EditText
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_update_facility, container, false)

        facilityUpdateBtn = view.findViewById(R.id.facilityUpdateBtn)
        facilityUpdateBtn.setOnClickListener(this)

        facility = arguments?.getSerializable("facility") as Facility
        id = view.findViewById(R.id.facility_id_input)
        name = view.findViewById(R.id.facility_name_input)
        building = view.findViewById(R.id.facility_building_input)
        lat = view.findViewById(R.id.facility_lat_input)
        lon = view.findViewById(R.id.facility_long_input)
        floor = view.findViewById(R.id.facility_floor_input)
        department = view.findViewById(R.id.facility_department_input)
        identifiername = view.findViewById(R.id.facility_identifiername_input)

        id.setText(facility.id.toString())
        name.setText(facility.name)
        building.setText(facility.building.toString())
        lat.setText(facility.lat.toString())
        lon.setText(facility.lon.toString())
        floor.setText(facility.floor.toString())
        department.setText(facility.department)
        identifiername.setText(facility.identifiername)

        return view
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.facilityUpdateBtn -> {
                val call: Call<PutFacilityResponse> = ApiClient.mgmtService
                    .updateFacility(
                        id.text.toString().toInt() as Integer,
                        name.text.toString(),
                        building.text.toString().toInt() as Integer,
                        lat.text.toString().toDouble(),
                        lon.text.toString().toDouble(),
                        floor.text.toString().toInt() as Integer,
                        department.text.toString(),
                        identifiername.text.toString()
                    )
                call.enqueue(object: Callback<PutFacilityResponse> {
                    override fun onResponse(
                        call: Call<PutFacilityResponse>,
                        response: Response<PutFacilityResponse>
                    ) {
                        Log.d("Put", response.toString())
                        if (response.isSuccessful.not()) {
                            Log.d("Put", "Put failed")
                            return
                        }
                        response.body()?.let {
                        }
                    }

                    override fun onFailure(call: Call<PutFacilityResponse>, t: Throwable) {
                        Log.d("Put", t.toString())
                    }
                })

                action_nav_facility_update_to_read()
            }
        }
    }

    private fun action_nav_facility_update_to_read() {
        val navController: NavController =
            Navigation.findNavController(activity as AdminActivity, R.id.admin_nav_host_fragment)
        navController.navigate(R.id.action_nav_facility_update_to_read)
    }
}