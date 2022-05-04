package kr.ac.kumoh.map
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.ac.kumoh.map.AdminActivity.Companion.admin_mContext
import kr.ac.kumoh.map.vo.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FacilityFragment : Fragment(), AdminFacilityAdapter.OnButtonClickListener, View.OnClickListener {
    private lateinit var facility_recyclerView: RecyclerView
    private lateinit var facilityCreateFormBtn: Button
    private var layoutManager: RecyclerView.LayoutManager? = null
    private lateinit var adapter: RecyclerView.Adapter<AdminFacilityAdapter.ViewHolder>
    private var facList: MutableList<Facility> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_facility, container, false)
        facility_recyclerView = view.findViewById(R.id.admin_facility_recyclerView)
        layoutManager = LinearLayoutManager(admin_mContext)
        facility_recyclerView.layoutManager = layoutManager


        facilityCreateFormBtn = view.findViewById(R.id.facilityCreateFormBtn)
        facilityCreateFormBtn.setOnClickListener(this)

        adapter = AdminFacilityAdapter(facList, this)
        facility_recyclerView.adapter = adapter

        val call: Call<GetFacilityResponse> = ApiClient.mgmtService.facility()

        call.enqueue(object: Callback<GetFacilityResponse> {
            override fun onResponse(
                call: Call<GetFacilityResponse>,
                response: Response<GetFacilityResponse>
            ) {
                Log.d("Search", response.toString())
                if (response.isSuccessful.not()) {
                    Log.d("Search", "Received empty response")
                    return
                }
                response.body()?.let {
                    facList.clear()
                    facList.addAll(it.facility)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<GetFacilityResponse>, t: Throwable) {
                Log.d("Search", t.toString())
            }
        })

        return view
    }

    override fun onButtonClick(position: Int, v: View) {

        when(v.id) {
            R.id.deleteButton -> {
                val call: Call<DeleteFacilityResponse> =
                    ApiClient.mgmtService.deleteFacility(facList[position].id)

                call.enqueue(object : Callback<DeleteFacilityResponse> {
                    override fun onResponse(
                        call: Call<DeleteFacilityResponse>,
                        response: Response<DeleteFacilityResponse>
                    ) {
                        Log.d("Delete", response.toString())
                        if (response.isSuccessful.not()) {
                            Log.d("Delete", "Delete Fail")
                            return
                        }
                        response.body()?.let {
                            facList.removeAt(position)
                            adapter.notifyDataSetChanged()
                        }

                    }

                    override fun onFailure(call: Call<DeleteFacilityResponse>, t: Throwable) {
                        Log.d("DeleteFragment", t.toString())
                    }
                })
            }

            R.id.modifyButton -> {
                val facility = facList[position]
                action_nav_facility_read_to_update(facility)
            }
        }
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.facilityCreateFormBtn -> {
                action_nav_facility_read_to_create()
            }
        }
    }

    private fun action_nav_facility_read_to_create() {
        val navController: NavController =
            Navigation.findNavController(activity as AdminActivity, R.id.admin_nav_host_fragment)
        navController.navigate(R.id.action_nav_facility_read_to_create)
    }

    private fun action_nav_facility_read_to_update(facility : Facility) {
        val navController: NavController =
            Navigation.findNavController(activity as AdminActivity, R.id.admin_nav_host_fragment)
        val bundle : Bundle = Bundle(1)
        bundle.putSerializable("facility", facility)
        navController.navigate(R.id.action_nav_facility_read_to_update, bundle)
    }
}