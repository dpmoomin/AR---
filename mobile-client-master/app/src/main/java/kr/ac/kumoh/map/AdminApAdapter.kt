package kr.ac.kumoh.map

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import kr.ac.kumoh.map.vo.Accesspoint
import org.w3c.dom.Text

class AdminApAdapter(private val apList: List<Accesspoint>, private val buttonClickListener: OnButtonClickListener) : RecyclerView.Adapter<AdminApAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.admin_ap_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.admin_ap_id.text = apList!![position].id.toString()
        holder.admin_ap_name.text = apList!![position].name
        holder.admin_ap_bssid.text = apList!![position].bssid
        holder.admin_ap_facid.text = apList!![position].facility.toString()
        holder.admin_ap_facName.text = apList!![position].facilityName
        holder.admin_ap_lat.text = apList!![position].lat.toString()
        holder.admin_ap_long.text = apList!![position].long.toString()
        holder.admin_ap_floor.text = apList!![position].floor.toString()
    }

    override fun getItemCount(): Int {
        return apList!!.size
    }

    interface RecyclerViewClickListener {
        fun onClick(v : View, position : Int)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var admin_ap_id : TextView
        var admin_ap_name : TextView
        var admin_ap_bssid : TextView
        var admin_ap_facid : TextView
        var admin_ap_facName : TextView
        var admin_ap_lat : TextView
        var admin_ap_long : TextView
        var admin_ap_floor : TextView
        var modifyButton: ImageButton
        var deleteButton: ImageButton
        init {
            admin_ap_id = itemView.findViewById(R.id.admin_ap_id)
            admin_ap_name = itemView.findViewById(R.id.admin_ap_name)
            admin_ap_bssid = itemView.findViewById(R.id.admin_ap_bssid)
            admin_ap_facid = itemView.findViewById(R.id.admin_ap_facid)
            admin_ap_facName = itemView.findViewById(R.id.admin_ap_facName)
            admin_ap_lat = itemView.findViewById(R.id.admin_ap_lat)
            admin_ap_long = itemView.findViewById(R.id.admin_ap_long)
            admin_ap_floor = itemView.findViewById(R.id.admin_ap_floor)
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