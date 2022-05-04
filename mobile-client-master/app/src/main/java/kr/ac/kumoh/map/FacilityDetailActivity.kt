package kr.ac.kumoh.map

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kr.ac.kumoh.map.vo.Facility
import kr.ac.kumoh.map.vo.Phone

class FacilityDetailActivity : AppCompatActivity(), FacilityDetailAdapter.OnItemClickListener {
    private var layoutManager: RecyclerView.LayoutManager? = null
    private var adapter: RecyclerView.Adapter<FacilityDetailAdapter.ViewHolder>? = null
    private var position : Int = 0
    private lateinit var recyclerView: RecyclerView
    private lateinit var facilityList : ArrayList<Facility>
    private lateinit var newArrayList : ArrayList<Facility>
    private lateinit var phoneList : ArrayList<Phone>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_facility_detail)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        facilityList = intent.getSerializableExtra("facilityList") as ArrayList<Facility>
        position = intent.getIntExtra("position", 0)
        phoneList = intent.getSerializableExtra("phoneList") as ArrayList<Phone>
        newArrayList = ArrayList<Facility>()
        newArrayList.add(facilityList[position])
        recyclerView = findViewById(R.id.facilityDetail_recyclerView)

        layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        adapter = FacilityDetailAdapter(newArrayList,phoneList, this)
        recyclerView.adapter =  adapter
    }

    override fun onItemClick(position: Int) {
        TODO("Not yet implemented")
    }
}