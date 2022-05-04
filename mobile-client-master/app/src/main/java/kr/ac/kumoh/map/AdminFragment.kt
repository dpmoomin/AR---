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
import kr.ac.kumoh.map.R
import kr.ac.kumoh.map.vo.PutFacilityResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminFragment : Fragment(), View.OnClickListener {

    private lateinit var facilityBtn: Button
    private lateinit var buildingBtn: Button
    private lateinit var apBtn: Button
    private lateinit var phoneBtn: Button
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_admin, container, false)

        facilityBtn = view.findViewById(R.id.facilityBtn)
        facilityBtn.setOnClickListener(this)
        buildingBtn = view.findViewById(R.id.buildingBtn)
        buildingBtn.setOnClickListener(this)
        apBtn = view.findViewById(R.id.apBtn)
        apBtn.setOnClickListener(this)
        phoneBtn = view.findViewById(R.id.phoneBtn)
        phoneBtn.setOnClickListener(this)

        return view
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.buildingBtn -> {
                action_nav_main_to_building()
            }
            R.id.facilityBtn -> {
                action_nav_main_to_facility()
            }
            R.id.apBtn -> {
                action_nav_main_to_ap()
            }
            R.id.phoneBtn -> {
                action_nav_main_to_phone()
            }
        }
    }

    private fun action_nav_main_to_building() {
        val navController: NavController =
            Navigation.findNavController(activity as AdminActivity, R.id.admin_nav_host_fragment)
        navController.navigate(R.id.action_nav_main_to_building)
    }

    private fun action_nav_main_to_facility() {
        val navController: NavController =
            Navigation.findNavController(activity as AdminActivity, R.id.admin_nav_host_fragment)
        navController.navigate(R.id.action_nav_main_to_facility)
    }

    private fun action_nav_main_to_phone() {
        val navController: NavController =
            Navigation.findNavController(activity as AdminActivity, R.id.admin_nav_host_fragment)
        navController.navigate(R.id.action_nav_main_to_phone)
    }

    private fun action_nav_main_to_ap() {
        val navController: NavController =
            Navigation.findNavController(activity as AdminActivity, R.id.admin_nav_host_fragment)
        navController.navigate(R.id.action_nav_main_to_ap)
    }
}