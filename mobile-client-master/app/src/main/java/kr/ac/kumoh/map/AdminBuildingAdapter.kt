package kr.ac.kumoh.map

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import kr.ac.kumoh.map.vo.Building

class AdminBuildingAdapter(private val buildingList: List<Building>, private val buttonClickListener: OnButtonClickListener) : RecyclerView.Adapter<AdminBuildingAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.admin_building_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.admin_building_id.text = buildingList!![position].id.toString()
        holder.admin_building_name.text = buildingList!![position].name
        holder.admin_building_identifier.text = buildingList!![position].identifier
        holder.admin_building_lat.text = buildingList!![position].lat.toString()
        holder.admin_building_long.text = buildingList!![position].lon.toString()
    }

    override fun getItemCount(): Int {
        return buildingList!!.size
    }

    interface RecyclerViewClickListener {
        fun onClick(v : View, position : Int)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var admin_building_id : TextView
        var admin_building_name : TextView
        var admin_building_identifier : TextView
        var admin_building_lat : TextView
        var admin_building_long : TextView
        var modifyButton: ImageButton
        var deleteButton: ImageButton
        init {
            admin_building_id = itemView.findViewById(R.id.admin_building_id)
            admin_building_name = itemView.findViewById(R.id.admin_building_name)
            admin_building_identifier = itemView.findViewById(R.id.admin_building_identifier)
            admin_building_lat = itemView.findViewById(R.id.admin_building_lat)
            admin_building_long = itemView.findViewById(R.id.admin_building_long)
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