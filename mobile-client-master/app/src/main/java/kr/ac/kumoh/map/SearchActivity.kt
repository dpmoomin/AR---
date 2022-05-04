package kr.ac.kumoh.map

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.ac.kumoh.map.MainActivity.Companion.mContext
import kr.ac.kumoh.map.vo.Facility
import kr.ac.kumoh.map.vo.GetFacilityResponse
import kr.ac.kumoh.map.vo.GetPhoneResponse
import kr.ac.kumoh.map.vo.Phone
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class SearchActivity : AppCompatActivity(), FacilityAdapter.OnItemClickListener, FacilityAdapter.OnButtonClickListener, View.OnClickListener {

    private lateinit var adapter: RecyclerView.Adapter<FacilityAdapter.ViewHolder>

    private lateinit var recyclerView: RecyclerView
    private lateinit var adminPageBtn: Button
    private var facList: MutableList<Facility> = ArrayList()
    private var newfacList: MutableList<Facility> = ArrayList()
    private var phoneList: MutableList<Phone> = ArrayList()

    lateinit var name : Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        recyclerView = findViewById(R.id.recyclerView)
        adminPageBtn = findViewById(R.id.adminPageBtn)
        adminPageBtn.setOnClickListener(this)

        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = FacilityAdapter(facList, this, this)
        recyclerView.adapter = adapter

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
                    newfacList.clear()
                    facList.addAll(it.facility)
                    newfacList.addAll(it.facility)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<GetFacilityResponse>, t: Throwable) {
                Log.d("Search", t.toString())
            }
        })


        val call2: Call<GetPhoneResponse> = ApiClient.mgmtService.phone()

        call2.enqueue(object: Callback<GetPhoneResponse> {
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
                }
            }

            override fun onFailure(call: Call<GetPhoneResponse>, t: Throwable) {
                Log.d("Search", t.toString())
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search, menu)
        val item = menu?.findItem((R.id.search))
        val searchView = item?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                facList.clear()
                val searchText = newText!!.lowercase(Locale.getDefault())
                if(searchText.isNotEmpty()){
                    newfacList.forEach {
                        if (it.name.lowercase(Locale.getDefault()).contains(searchText)){
                            facList.add(it)
                        }
                    }
                    recyclerView.adapter!!.notifyDataSetChanged()
                }else{
                    facList.clear()
                    facList.addAll(newfacList)
                    recyclerView.adapter!!.notifyDataSetChanged()
                }
                return false
            }
        })
        return super.onCreateOptionsMenu(menu)
    }


    override fun onItemClick(position: Int) {
        val clickedItem : Facility = facList[position]
        val nextIntent = Intent(this, FacilityDetailActivity::class.java)
        nextIntent.putExtra("facilityList", ArrayList(facList))
        nextIntent.putExtra("position", position)
        nextIntent.putExtra("phoneList", ArrayList(phoneList))
        startActivity(nextIntent)
    }

    override fun onButtonClick(position: Int) {
        when(facList[position].buildingName[0])
        {
            '디' -> (mContext as MainActivity).setDestination(128.392950, 36.145816)
            '글' -> (mContext as MainActivity).setDestination(128.392621, 36.146909)
            '테' -> (mContext as MainActivity).setDestination(128.393916, 36.146509)
            '도' -> (mContext as MainActivity).setDestination(128.393930,36.145673)
        }
        (mContext as MainActivity).setFacilityName(facList[position].name)
        (mContext as MainActivity).setBuildingName(facList[position].buildingName)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return false
    }

    override fun onClick(v: View?) {
        when(v!!.id){
            R.id.adminPageBtn -> {
                val nextIntent = Intent(this, AdminActivity::class.java)
                startActivity(nextIntent)
            }
        }
    }

}