package kr.ac.kumoh.map

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import kr.ac.kumoh.map.vo.Facility

class AdminFacilityAdapter(private val facilityList: List<Facility>, private val buttonClickListener: OnButtonClickListener) : RecyclerView.Adapter<AdminFacilityAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.admin_facility_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.admin_facility_building.text = facilityList!![position].building.toString()
        holder.admin_facility_building_name.text = facilityList!![position].buildingName
        holder.admin_facility_department.text = facilityList!![position].department
        holder.admin_facility_floor.text = facilityList!![position].floor.toString()
        holder.admin_facility_id.text = facilityList!![position].id.toString()
        holder.admin_facility_lat.text = facilityList!![position].lat.toString()
        holder.admin_facility_long.text = facilityList!![position].lon.toString()
        holder.admin_facility_name.text = facilityList!![position].name
        holder.admin_facility_idname.text = facilityList!![position].identifiername
    }

    override fun getItemCount(): Int {
        return facilityList!!.size
    }

    interface RecyclerViewClickListener {
        fun onClick(v : View, position : Int)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var admin_facility_building : TextView
        var admin_facility_building_name : TextView
        var admin_facility_department : TextView
        var admin_facility_floor : TextView
        var admin_facility_id : TextView
        var admin_facility_lat : TextView
        var admin_facility_long : TextView
        var admin_facility_name : TextView
        var admin_facility_idname : TextView
        var modifyButton: ImageButton
        var deleteButton: ImageButton
        init {
            admin_facility_building = itemView.findViewById(R.id.admin_facility_building)
            admin_facility_building_name = itemView.findViewById(R.id.admin_facility_building_name)
            admin_facility_department = itemView.findViewById(R.id.admin_facility_department)
            admin_facility_floor = itemView.findViewById(R.id.admin_facility_floor)
            admin_facility_id = itemView.findViewById(R.id.admin_facility_id)
            admin_facility_lat = itemView.findViewById(R.id.admin_facility_lat)
            admin_facility_long = itemView.findViewById(R.id.admin_facility_long)
            admin_facility_name = itemView.findViewById(R.id.admin_facility_name)
            admin_facility_idname = itemView.findViewById(R.id.admin_facility_idname)
            deleteButton = itemView.findViewById(R.id.deleteButton)
            modifyButton = itemView.findViewById(R.id.modifyButton)
            deleteButton.setOnClickListener{
                buttonClickListener.onButtonClick(absoluteAdapterPosition, deleteButton)
            }
            modifyButton.setOnClickListener {
                buttonClickListener.onButtonClick(absoluteAdapterPosition, modifyButton)
            }
        }
    }

    interface OnButtonClickListener {
        fun onButtonClick(position: Int, v: View)
    }
}