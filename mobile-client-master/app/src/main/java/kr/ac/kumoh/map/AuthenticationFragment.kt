package kr.ac.kumoh.map

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import kr.ac.kumoh.map.vo.AuthenticationResponse
import kr.ac.kumoh.map.vo.PostPhoneResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthenticationFragment : Fragment(), View.OnClickListener {

    private lateinit var authenticationBtn: Button
    private lateinit var id: EditText
    private lateinit var password: EditText
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_authentication, container, false)

        authenticationBtn = view.findViewById(R.id.authenticationBtn)
        authenticationBtn.setOnClickListener(this)

        id = view.findViewById(R.id.id_input)
        password = view.findViewById(R.id.password_input)

        return view
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.authenticationBtn -> {

                val call: Call<AuthenticationResponse> = ApiClient.mgmtService
                    .authentication(
                        id.text.toString(),
                        password.text.toString()
                    )
                call.enqueue(object: Callback<AuthenticationResponse> {
                    override fun onResponse(
                        call: Call<AuthenticationResponse>,
                        response: Response<AuthenticationResponse>
                    ) {
                        Log.d("Authentication", response.toString())
                        if (response.isSuccessful.not()) {
                            Log.d("Authentication", "Authentication failed")
                            Toast.makeText(requireContext(), "통신 오류", Toast.LENGTH_LONG).show()
                            return
                        }
                        response.body()?.let {
                            if(it.result == "OK") {
                                action_nav_authentication_to_main()
                                Toast.makeText(requireContext(), "인증 성공", Toast.LENGTH_LONG).show()
                            }
                            else {
                                Toast.makeText(requireContext(), "인증 실패", Toast.LENGTH_LONG).show()
                                return
                            }
                        }
                    }

                    override fun onFailure(call: Call<AuthenticationResponse>, t: Throwable) {
                        Log.d("Authentication", t.toString())
                        Toast.makeText(requireContext(), "인증 오류", Toast.LENGTH_LONG).show()
                    }
                })
            }
        }
    }

    private fun action_nav_authentication_to_main() {
        val navController: NavController =
            Navigation.findNavController(activity as AdminActivity, R.id.admin_nav_host_fragment)
        navController.navigate(R.id.action_nav_authentication_to_main)
    }
}