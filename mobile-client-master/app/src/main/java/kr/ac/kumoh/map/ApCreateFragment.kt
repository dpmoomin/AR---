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
import kr.ac.kumoh.map.vo.Accesspoint
import kr.ac.kumoh.map.vo.PostAccesspointResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApCreateFragment : Fragment(), View.OnClickListener {
    private lateinit var callback: OnBackPressedCallback
    lateinit var ap : Accesspoint
    private lateinit var apCreateBtn: Button
    lateinit var id: EditText //데이터베이스 상에서의 고유번호
    lateinit var name: EditText //ap 이름
    lateinit var bssid: EditText //bssid
    lateinit var facid: EditText //facid
    lateinit var lat: EditText //latitude
    lateinit var lon: EditText //longitude
    lateinit var floor: EditText //위치한 층 수

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_ap, container, false)

        apCreateBtn = view.findViewById(R.id.apCreateBtn)
        apCreateBtn.setOnClickListener(this)
        id = view.findViewById(R.id.ap_id_input)
        name = view.findViewById(R.id.ap_name_input)
        bssid = view.findViewById(R.id.ap_bssid_input)
        facid = view.findViewById(R.id.ap_facid_input)
        lat = view.findViewById(R.id.ap_lat_input)
        lon = view.findViewById(R.id.ap_long_input)
        floor = view.findViewById(R.id.ap_floor_input)

        return view
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.apCreateBtn -> {

                val call: Call<PostAccesspointResponse> = ApiClient.mgmtService
                    .createAp(
                        id.text.toString().toInt() as Integer,
                        name.text.toString(),
                        bssid.text.toString(),
                        facid.text.toString().toInt() as Integer,
                        floor.text.toString().toInt() as Integer,
                        lat.text.toString().toDouble(),
                        lon.text.toString().toDouble(),
                    )
                call.enqueue(object: Callback<PostAccesspointResponse> {
                    override fun onResponse(
                        call: Call<PostAccesspointResponse>,
                        response: Response<PostAccesspointResponse>
                    ) {
                        Log.d("Post", response.toString())
                        if (response.isSuccessful.not()) {
                            Log.d("Post", "Post failed")
                            return
                        }
                        response.body()?.let {
                        }
                    }

                    override fun onFailure(call: Call<PostAccesspointResponse>, t: Throwable) {
                        Log.d("Post", t.toString())
                    }
                })

                action_nav_ap_create_to_read()
            }
        }
    }

    private fun action_nav_ap_create_to_read() {
        val navController: NavController =
            Navigation.findNavController(activity as AdminActivity, R.id.admin_nav_host_fragment)
        navController.navigate(R.id.action_nav_ap_create_to_read)
    }
}