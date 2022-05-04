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
import kr.ac.kumoh.map.vo.Building
import kr.ac.kumoh.map.vo.PutBuildingResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BuildingUpdateFragment : Fragment(), View.OnClickListener {
    lateinit var building : Building
    private lateinit var buildingUpdateBtn: Button
    lateinit var id: EditText //데이터베이스 상에서의 고유번호
    lateinit var name: EditText //시설물 이름
    lateinit var identifier: EditText //식별자
    lateinit var lat: EditText //latitude
    lateinit var lon: EditText //longitude
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_update_building, container, false)

        buildingUpdateBtn = view.findViewById(R.id.buildingUpdateBtn)
        buildingUpdateBtn.setOnClickListener(this)

        building = arguments?.getSerializable("building") as Building
        id = view.findViewById(R.id.building_id_input)
        name = view.findViewById(R.id.building_name_input)
        identifier = view.findViewById(R.id.building_identifier_input)
        lat = view.findViewById(R.id.building_lat_input)
        lon = view.findViewById(R.id.building_long_input)

        id.setText(building.id.toString())
        name.setText(building.name)
        identifier.setText(building.identifier)
        lat.setText(building.lat.toString())
        lon.setText(building.lon.toString())

        return view
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.buildingUpdateBtn -> {
                val call: Call<PutBuildingResponse> = ApiClient.mgmtService
                    .updateBuilding(
                        id.text.toString().toInt() as Integer,
                        name.text.toString(),
                        identifier.text.toString(),
                        lat.text.toString().toDouble(),
                        lon.text.toString().toDouble()
                    )
                call.enqueue(object: Callback<PutBuildingResponse> {
                    override fun onResponse(
                        call: Call<PutBuildingResponse>,
                        response: Response<PutBuildingResponse>
                    ) {
                        Log.d("Put", response.toString())
                        if (response.isSuccessful.not()) {
                            Log.d("Put", "Put failed")
                            return
                        }
                        response.body()?.let {
                        }
                    }

                    override fun onFailure(call: Call<PutBuildingResponse>, t: Throwable) {
                        Log.d("Put", t.toString())
                    }
                })

                action_nav_building_update_to_read()
            }
        }
    }

    private fun action_nav_building_update_to_read() {
        val navController: NavController =
            Navigation.findNavController(activity as AdminActivity, R.id.admin_nav_host_fragment)
        navController.navigate(R.id.action_nav_building_update_to_read)
    }
}