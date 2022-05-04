package kr.ac.kumoh.map

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import kr.ac.kumoh.map.vo.Building
import kr.ac.kumoh.map.vo.PostBuildingResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BuildingCreateFragment : Fragment(), View.OnClickListener {
    private lateinit var callback: OnBackPressedCallback
    lateinit var building : Building
    private lateinit var buildingCreateBtn: Button
    lateinit var id: EditText //데이터베이스 상에서의 고유번호
    lateinit var name: EditText //시설물 이름
    lateinit var identifier: EditText //식별자
    lateinit var lat: EditText //latitude
    lateinit var lon: EditText //longitude
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_building, container, false)

        buildingCreateBtn = view.findViewById(R.id.buildingCreateBtn)
        buildingCreateBtn.setOnClickListener(this)
        id = view.findViewById(R.id.building_id_input)
        name = view.findViewById(R.id.building_name_input)
        identifier = view.findViewById(R.id.building_identifier_input)
        lat = view.findViewById(R.id.building_lat_input)
        lon = view.findViewById(R.id.building_long_input)

        return view
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.buildingCreateBtn -> {

                val call: Call<PostBuildingResponse> = ApiClient.mgmtService
                    .createBuilding(
                        id.text.toString().toInt() as Integer,
                        name.text.toString(),
                        identifier.text.toString(),
                        lat.text.toString().toDouble(),
                        lon.text.toString().toDouble()
                    )
                call.enqueue(object: Callback<PostBuildingResponse> {
                    override fun onResponse(
                        call: Call<PostBuildingResponse>,
                        response: Response<PostBuildingResponse>
                    ) {
                        Log.d("Post", response.toString())
                        if (response.isSuccessful.not()) {
                            Log.d("Post", "Post failed")
                            return
                        }
                        response.body()?.let {
                        }
                    }

                    override fun onFailure(call: Call<PostBuildingResponse>, t: Throwable) {
                        Log.d("Post", t.toString())
                    }
                })

                action_nav_building_create_to_read()
            }
        }
    }

    private fun action_nav_building_create_to_read() {
        val navController: NavController =
            Navigation.findNavController(activity as AdminActivity, R.id.admin_nav_host_fragment)
        navController.navigate(R.id.action_nav_building_create_to_read)
    }
}