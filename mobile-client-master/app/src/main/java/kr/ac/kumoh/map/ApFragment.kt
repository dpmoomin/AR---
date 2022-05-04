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
import kr.ac.kumoh.map.vo.Accesspoint
import kr.ac.kumoh.map.vo.DeleteAccesspointResponse
import kr.ac.kumoh.map.vo.GetAccesspointResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApFragment : Fragment(), AdminApAdapter.OnButtonClickListener, View.OnClickListener {
    private lateinit var ap_recyclerView: RecyclerView
    private lateinit var apCreateFormBtn: Button
    private var layoutManager: RecyclerView.LayoutManager? = null
    private lateinit var adapter: RecyclerView.Adapter<AdminApAdapter.ViewHolder>
    private var apList: MutableList<Accesspoint> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ap, container, false)
        ap_recyclerView = view.findViewById(R.id.admin_ap_recyclerView)
        layoutManager = LinearLayoutManager(admin_mContext)
        ap_recyclerView.layoutManager = layoutManager


        apCreateFormBtn = view.findViewById(R.id.apCreateFormBtn)
        apCreateFormBtn.setOnClickListener(this)

        adapter = AdminApAdapter(apList, this)
        ap_recyclerView.adapter = adapter

        val call: Call<GetAccesspointResponse> = ApiClient.mgmtService.ap()

        call.enqueue(object: Callback<GetAccesspointResponse> {
            override fun onResponse(
                call: Call<GetAccesspointResponse>,
                response: Response<GetAccesspointResponse>
            ) {
                Log.d("Search", response.toString())
                if (response.isSuccessful.not()) {
                    Log.d("Search", "Received empty response")
                    return
                }
                response.body()?.let {
                    apList.clear()
                    apList.addAll(it.ap)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<GetAccesspointResponse>, t: Throwable) {
                Log.d("Search", t.toString())
            }
        })

        return view
    }
    override fun onButtonClick(position: Int, v: View) {

        when(v.id) {
            R.id.deleteButton -> {
                val call: Call<DeleteAccesspointResponse> =
                    ApiClient.mgmtService.deleteAp(apList[position].id)

                call.enqueue(object : Callback<DeleteAccesspointResponse> {
                    override fun onResponse(
                        call: Call<DeleteAccesspointResponse>,
                        response: Response<DeleteAccesspointResponse>
                    ) {
                        Log.d("Delete", response.toString())
                        if (response.isSuccessful.not()) {
                            Log.d("Delete", "Delete Fail")
                            return
                        }
                        response.body()?.let {
                            apList.removeAt(position)
                            adapter.notifyDataSetChanged()
                        }

                    }

                    override fun onFailure(call: Call<DeleteAccesspointResponse>, t: Throwable) {
                        Log.d("DeleteFragment", t.toString())
                    }
                })
            }

            R.id.modifyButton -> {
                val ap = apList[position]
                action_nav_ap_read_to_update(ap)
            }
        }
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.apCreateFormBtn -> {
                action_nav_ap_read_to_create()
            }
        }
    }

    private fun action_nav_ap_read_to_create() {
        val navController: NavController =
            Navigation.findNavController(activity as AdminActivity, R.id.admin_nav_host_fragment)
        navController.navigate(R.id.action_nav_ap_read_to_create)
    }

    private fun action_nav_ap_read_to_update(ap : Accesspoint) {
        val navController: NavController =
            Navigation.findNavController(activity as AdminActivity, R.id.admin_nav_host_fragment)
        val bundle : Bundle = Bundle(1)
        bundle.putSerializable("ap", ap)
        navController.navigate(R.id.action_nav_ap_read_to_update, bundle)
    }
}