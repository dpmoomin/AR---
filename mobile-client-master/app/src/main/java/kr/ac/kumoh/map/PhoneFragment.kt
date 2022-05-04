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
import kr.ac.kumoh.map.vo.DeletePhoneResponse
import kr.ac.kumoh.map.vo.GetPhoneResponse
import kr.ac.kumoh.map.vo.Phone
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PhoneFragment : Fragment(), AdminPhoneAdapter.OnButtonClickListener, View.OnClickListener {
    private lateinit var phone_recyclerView: RecyclerView
    private lateinit var phoneCreateFormBtn: Button
    private var layoutManager: RecyclerView.LayoutManager? = null
    private lateinit var adapter: RecyclerView.Adapter<AdminPhoneAdapter.ViewHolder>
    private var phoneList: MutableList<Phone> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_phone, container, false)
        phone_recyclerView = view.findViewById(R.id.admin_phone_recyclerView)
        layoutManager = LinearLayoutManager(admin_mContext)
        phone_recyclerView.layoutManager = layoutManager


        phoneCreateFormBtn = view.findViewById(R.id.phoneCreateFormBtn)
        phoneCreateFormBtn.setOnClickListener(this)

        adapter = AdminPhoneAdapter(phoneList, this)
        phone_recyclerView.adapter = adapter

        val call: Call<GetPhoneResponse> = ApiClient.mgmtService.phone()

        call.enqueue(object: Callback<GetPhoneResponse> {
            override fun onResponse(
                call: Call<GetPhoneResponse>,
                response: Response<GetPhoneResponse>
            ) {
                Log.d("Search", response.toString())
                if (response.isSuccessful.not()) {
                    Log.d("Search", "Received empty response")
                    return
                }
                response.body()?.let {
                    phoneList.clear()
                    phoneList.addAll(it.phone)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<GetPhoneResponse>, t: Throwable) {
                Log.d("Search", t.toString())
            }
        })

        return view
    }
    override fun onButtonClick(position: Int, v: View) {

        when(v.id) {
            R.id.deleteButton -> {
                val call: Call<DeletePhoneResponse> =
                    ApiClient.mgmtService.deletePhone(phoneList[position].id)

                call.enqueue(object : Callback<DeletePhoneResponse> {
                    override fun onResponse(
                        call: Call<DeletePhoneResponse>,
                        response: Response<DeletePhoneResponse>
                    ) {
                        Log.d("Delete", response.toString())
                        if (response.isSuccessful.not()) {
                            Log.d("Delete", "Delete Fail")
                            return
                        }
                        response.body()?.let {
                            phoneList.removeAt(position)
                            adapter.notifyDataSetChanged()
                        }

                    }

                    override fun onFailure(call: Call<DeletePhoneResponse>, t: Throwable) {
                        Log.d("DeleteFragment", t.toString())
                    }
                })
            }

            R.id.modifyButton -> {
                val phone = phoneList[position]
                action_nav_phone_read_to_update(phone)
            }
        }
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.phoneCreateFormBtn -> {
                action_nav_phone_read_to_create()
            }
        }
    }

    private fun action_nav_phone_read_to_create() {
        val navController: NavController =
            Navigation.findNavController(activity as AdminActivity, R.id.admin_nav_host_fragment)
        navController.navigate(R.id.action_nav_phone_read_to_create)
    }

    private fun action_nav_phone_read_to_update(phone : Phone) {
        val navController: NavController =
            Navigation.findNavController(activity as AdminActivity, R.id.admin_nav_host_fragment)
        val bundle : Bundle = Bundle(1)
        bundle.putSerializable("phone", phone)
        navController.navigate(R.id.action_nav_phone_read_to_update, bundle)
    }
}