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
import kr.ac.kumoh.map.vo.Accesspoint
import kr.ac.kumoh.map.vo.PutAccesspointResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApUpdateFragment : Fragment(), View.OnClickListener {
    lateinit var ap : Accesspoint
    private lateinit var apUpdateBtn: Button
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
        val view = inflater.inflate(R.layout.fragment_update_ap, container, false)

        apUpdateBtn = view.findViewById(R.id.apUpdateBtn)
        apUpdateBtn.setOnClickListener(this)

        ap = arguments?.getSerializable("ap") as Accesspoint

        apUpdateBtn = view.findViewById(R.id.apUpdateBtn)
        apUpdateBtn.setOnClickListener(this)
        id = view.findViewById(R.id.ap_id_input)
        name = view.findViewById(R.id.ap_name_input)
        bssid = view.findViewById(R.id.ap_bssid_input)
        facid = view.findViewById(R.id.ap_facid_input)
        lat = view.findViewById(R.id.ap_lat_input)
        lon = view.findViewById(R.id.ap_long_input)
        floor = view.findViewById(R.id.ap_floor_input)

        id.setText(ap.id.toString())
        name.setText(ap.name)
        bssid.setText(ap.bssid)
        facid.setText(ap.facility.toString())
        lat.setText(ap.lat.toString())
        lon.setText(ap.long.toString())
        floor.setText(ap.floor.toString())

        return view
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.apUpdateBtn -> {
                val call: Call<PutAccesspointResponse> = ApiClient.mgmtService
                    .updateAp(
                        id.text.toString().toInt() as Integer,
                        name.text.toString(),
                        bssid.text.toString(),
                        facid.text.toString().toInt() as Integer,
                        floor.text.toString().toInt() as Integer,
                        lat.text.toString().toDouble(),
                        lon.text.toString().toDouble(),
                    )
                call.enqueue(object: Callback<PutAccesspointResponse> {
                    override fun onResponse(
                        call: Call<PutAccesspointResponse>,
                        response: Response<PutAccesspointResponse>
                    ) {
                        Log.d("Put", response.toString())
                        if (response.isSuccessful.not()) {
                            Log.d("Put", "Put failed")
                            return
                        }
                        response.body()?.let {
                        }
                    }

                    override fun onFailure(call: Call<PutAccesspointResponse>, t: Throwable) {
                        Log.d("Put", t.toString())
                    }
                })

                action_nav_ap_update_to_read()
            }
        }
    }

    private fun action_nav_ap_update_to_read() {
        val navController: NavController =
            Navigation.findNavController(activity as AdminActivity, R.id.admin_nav_host_fragment)
        navController.navigate(R.id.action_nav_ap_update_to_read)
    }
}