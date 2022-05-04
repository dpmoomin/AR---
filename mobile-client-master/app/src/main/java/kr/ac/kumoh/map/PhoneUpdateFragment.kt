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
import kr.ac.kumoh.map.vo.Phone
import kr.ac.kumoh.map.vo.PutPhoneResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PhoneUpdateFragment : Fragment(), View.OnClickListener {
    lateinit var phone : Phone
    private lateinit var phoneUpdateBtn: Button
    lateinit var id: EditText //데이터베이스 상에서의 고유번호
    lateinit var facid: EditText //전화번호의 시설물 id
    lateinit var number: EditText //전화번호

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_update_phone, container, false)

        phoneUpdateBtn = view.findViewById(R.id.phoneUpdateBtn)
        phoneUpdateBtn.setOnClickListener(this)

        phone = arguments?.getSerializable("phone") as Phone
        id = view.findViewById(R.id.phone_id_input)
        facid = view.findViewById(R.id.phone_facid_input)
        number = view.findViewById(R.id.phone_number_input)

        id.setText(phone.id.toString())
        facid.setText(phone.facid.toString())
        number.setText(phone.number)

        return view
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.phoneUpdateBtn -> {
                val call: Call<PutPhoneResponse> = ApiClient.mgmtService
                    .updatePhone(
                        id.text.toString().toInt() as Integer,
                        facid.text.toString().toInt() as Integer,
                        number.text.toString()
                    )
                call.enqueue(object: Callback<PutPhoneResponse> {
                    override fun onResponse(
                        call: Call<PutPhoneResponse>,
                        response: Response<PutPhoneResponse>
                    ) {
                        Log.d("Put", response.toString())
                        if (response.isSuccessful.not()) {
                            Log.d("Put", "Put failed")
                            return
                        }
                        response.body()?.let {
                        }
                    }

                    override fun onFailure(call: Call<PutPhoneResponse>, t: Throwable) {
                        Log.d("Put", t.toString())
                    }
                })

                action_nav_phone_update_to_read()
            }
        }
    }

    private fun action_nav_phone_update_to_read() {
        val navController: NavController =
            Navigation.findNavController(activity as AdminActivity, R.id.admin_nav_host_fragment)
        navController.navigate(R.id.action_nav_phone_update_to_read)
    }
}