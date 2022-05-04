package kr.ac.kumoh.map

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import kr.ac.kumoh.map.vo.Facility
import kr.ac.kumoh.map.vo.Phone
import org.w3c.dom.Text

class FacilityDetailAdapter(private val facilityList: ArrayList<Facility>, private val phoneList: ArrayList<Phone>, private val listener: OnItemClickListener) : RecyclerView.Adapter<FacilityDetailAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.facility_detail_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.facility_name.text = facilityList!![position].name
        holder.facility_building_name.text = facilityList!![position].buildingName
        holder.facility_idname.text = facilityList!![position].identifiername
        holder.facility_department.text = facilityList!![position].department
        holder.facility_phoneNumber.text = ""
        for (i in phoneList) {
            if (facilityList!![position].id == i.facid) {
                holder.facility_phoneNumber.text = "054-478-" + i.number
            }
        }
        if (facilityList!![position].name == "오득환 교수실") {
            holder.profile.setImageResource(R.drawable.dho)
        } else if (facilityList!![position].name == "신윤식 교수실") {
            holder.profile.setImageResource(R.drawable.yss)
        } else if (facilityList!![position].name == "김병만 교수실") {
            holder.profile.setImageResource(R.drawable.bmk)
        } else if (facilityList!![position].name == "김시관 교수실") {
            holder.profile.setImageResource(R.drawable.sgk)
        } else if (facilityList!![position].name == "이현아 교수실") {
            holder.profile.setImageResource(R.drawable.hal)
        } else if (facilityList!![position].name == "김선명 교수실") {
            holder.profile.setImageResource(R.drawable.smk)
        } else if (facilityList!![position].name == "이해연 교수실") {
            holder.profile.setImageResource(R.drawable.hyl)
        } else if (facilityList!![position].name == "김성렬 교수실") {
            holder.profile.setImageResource(R.drawable.srk)
        }
    }
    override fun getItemCount(): Int {
        return facilityList!!.size
    }

    interface RecyclerViewClickListener {
        fun onClick(v : View, position : Int)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var facility_building_name: TextView = itemView.findViewById(R.id.facility_buildingName)
        var facility_idname : TextView = itemView.findViewById(R.id.facility_identifiername)
        var facility_name : TextView = itemView.findViewById(R.id.facility_name2)
        var facility_department : TextView = itemView.findViewById(R.id.facility_department)
        var facility_phoneNumber : TextView = itemView.findViewById(R.id.facility_phoneNumber)
        var profile : ImageView = itemView.findViewById(R.id.profile)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            val position : Int = absoluteAdapterPosition
            if(position != RecyclerView.NO_POSITION)
                listener.onItemClick(position)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}