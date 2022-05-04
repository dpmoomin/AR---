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
import kr.ac.kumoh.map.vo.Building
import kr.ac.kumoh.map.vo.DeleteBuildingResponse
import kr.ac.kumoh.map.vo.GetBuildingResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BuildingFragment : Fragment(), AdminBuildingAdapter.OnButtonClickListener, View.OnClickListener {
    private lateinit var building_recyclerView: RecyclerView
    private lateinit var buildingCreateFormBtn: Button
    private var layoutManager: RecyclerView.LayoutManager? = null
    private lateinit var adapter: RecyclerView.Adapter<AdminBuildingAdapter.ViewHolder>
    private var bldnList: MutableList<Building> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_building, container, false)
        building_recyclerView = view.findViewById(R.id.admin_building_recyclerView)
        layoutManager = LinearLayoutManager(admin_mContext)
        building_recyclerView.layoutManager = layoutManager


        buildingCreateFormBtn = view.findViewById(R.id.buildingCreateFormBtn)
        buildingCreateFormBtn.setOnClickListener(this)

        adapter = AdminBuildingAdapter(bldnList, this)
        building_recyclerView.adapter = adapter

        val call: Call<GetBuildingResponse> = ApiClient.mgmtService.building()

        call.enqueue(object: Callback<GetBuildingResponse> {
            override fun onResponse(
                call: Call<GetBuildingResponse>,
                response: Response<GetBuildingResponse>
            ) {
                Log.d("Search", response.toString())
                if (response.isSuccessful.not()) {
                    Log.d("Search", "Received empty response")
                    return
                }
                response.body()?.let {
                    bldnList.clear()
                    bldnList.addAll(it.building)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<GetBuildingResponse>, t: Throwable) {
                Log.d("Search", t.toString())
            }
        })

        return view
    }
    override fun onButtonClick(position: Int, v: View) {

        when(v.id) {
            R.id.deleteButton -> {
                val call: Call<DeleteBuildingResponse> =
                    ApiClient.mgmtService.deleteBuilding(bldnList[position].id)

                call.enqueue(object : Callback<DeleteBuildingResponse> {
                    override fun onResponse(
                        call: Call<DeleteBuildingResponse>,
                        response: Response<DeleteBuildingResponse>
                    ) {
                        Log.d("Delete", response.toString())
                        if (response.isSuccessful.not()) {
                            Log.d("Delete", "Delete Fail")
                            return
                        }
                        response.body()?.let {
                            bldnList.removeAt(position)
                            adapter.notifyDataSetChanged()
                        }

                    }

                    override fun onFailure(call: Call<DeleteBuildingResponse>, t: Throwable) {
                        Log.d("DeleteFragment", t.toString())
                    }
                })
            }

            R.id.modifyButton -> {
                val building = bldnList[position]
                action_nav_building_read_to_update(building)
            }
        }
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.buildingCreateFormBtn -> {
                action_nav_building_read_to_create()
            }
        }
    }

    private fun action_nav_building_read_to_create() {
        val navController: NavController =
            Navigation.findNavController(activity as AdminActivity, R.id.admin_nav_host_fragment)
        navController.navigate(R.id.action_nav_building_read_to_create)
    }

    private fun action_nav_building_read_to_update(building : Building) {
        val navController: NavController =
            Navigation.findNavController(activity as AdminActivity, R.id.admin_nav_host_fragment)
        val bundle : Bundle = Bundle(1)
        bundle.putSerializable("building", building)
        navController.navigate(R.id.action_nav_building_read_to_update, bundle)
    }
}