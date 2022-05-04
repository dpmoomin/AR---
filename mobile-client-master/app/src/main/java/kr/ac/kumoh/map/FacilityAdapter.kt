package kr.ac.kumoh.map

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import kr.ac.kumoh.map.vo.Facility
import org.w3c.dom.Text

class FacilityAdapter(private val facilityList: List<Facility>, private val itemClickListener: OnItemClickListener, private val buttonClickListener: OnButtonClickListener) : RecyclerView.Adapter<FacilityAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.facility_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.facility_name.text = facilityList!![position].name
        holder.facility_idname.text = facilityList!![position].identifiername.toString()
    }

    override fun getItemCount(): Int {
        return facilityList!!.size
    }

    interface RecyclerViewClickListener {
        fun onClick(v : View, position : Int)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener{
        var facility_name: TextView = itemView.findViewById(R.id.facility_name)
        var facility_idname: TextView = itemView.findViewById(R.id.facility_idname)
        var destinationButton: ImageButton = itemView.findViewById(R.id.setDestinationButton)

        init {
            itemView.setOnClickListener(this)
            destinationButton.setOnClickListener{
                buttonClickListener.onButtonClick(absoluteAdapterPosition)
            }
        }

        override fun onClick(view: View) {
            val position : Int = absoluteAdapterPosition
            if(position != RecyclerView.NO_POSITION)
                itemClickListener.onItemClick(position)
        }

    }

    interface OnButtonClickListener {
        fun onButtonClick(position: Int)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}