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
import kr.ac.kumoh.map.vo.Phone
import kr.ac.kumoh.map.vo.PostBuildingResponse
import kr.ac.kumoh.map.vo.PostPhoneResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PhoneCreateFragment : Fragment(), View.OnClickListener {
    private lateinit var callback: OnBackPressedCallback
    lateinit var phone : Phone
    private lateinit var phoneCreateBtn: Button
    lateinit var id: EditText //데이터베이스 상에서의 고유번호
    lateinit var facid: EditText //전화번호의 시설물 id
    lateinit var name: EditText //전화번호 이름
    lateinit var number: EditText //전화번호
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_create_phone, container, false)

        phoneCreateBtn = view.findViewById(R.id.phoneCreateBtn)
        phoneCreateBtn.setOnClickListener(this)
        id = view.findViewById(R.id.phone_id_input)
        facid = view.findViewById(R.id.phone_facid_input)
        number = view.findViewById(R.id.phone_number_input)

        return view
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.phoneCreateBtn -> {

                val call: Call<PostPhoneResponse> = ApiClient.mgmtService
                    .createPhone(
                        id.text.toString().toInt() as Integer,
                        facid.text.toString().toInt() as Integer,
                        number.text.toString()
                    )
                call.enqueue(object: Callback<PostPhoneResponse> {
                    override fun onResponse(
                        call: Call<PostPhoneResponse>,
                        response: Response<PostPhoneResponse>
                    ) {
                        Log.d("Post", response.toString())
                        if (response.isSuccessful.not()) {
                            Log.d("Post", "Post failed")
                            return
                        }
                        response.body()?.let {
                        }
                    }

                    override fun onFailure(call: Call<PostPhoneResponse>, t: Throwable) {
                        Log.d("Post", t.toString())
                    }
                })

                action_nav_phone_create_to_read()
            }
        }
    }

    private fun action_nav_phone_create_to_read() {
        val navController: NavController =
            Navigation.findNavController(activity as AdminActivity, R.id.admin_nav_host_fragment)
        navController.navigate(R.id.action_nav_phone_create_to_read)
    }
}