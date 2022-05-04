package kr.ac.kumoh.map

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.ac.kumoh.map.vo.Facility
import kr.ac.kumoh.map.vo.GetFacilityResponse
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        // 임시로 서버와 Facility 통신 Test -> 로그에 찍음
        val call: Call<GetFacilityResponse> = ApiClient.mgmtService.facility()

        call.enqueue(object: Callback<GetFacilityResponse> {
            override fun onResponse(
                call: Call<GetFacilityResponse>,
                response: Response<GetFacilityResponse>
            ) {
                if (response.isSuccessful.not()) {
                    return
                }
                response.body()?.let {
                    it.facility.forEach{
                        facility -> Log.d("SearchFragment", facility.toString())
                    }
                }
            }

            override fun onFailure(call: Call<GetFacilityResponse>, t: Throwable) {
                Log.d("SearchFragment", t.toString())
            }
        })

        return view
    }


}