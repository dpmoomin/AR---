package kr.ac.kumoh.map

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import kr.ac.kumoh.map.vo.Phone
import org.w3c.dom.Text

class AdminPhoneAdapter(private val phoneList: List<Phone>, private val buttonClickListener: OnButtonClickListener) : RecyclerView.Adapter<AdminPhoneAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.admin_phone_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.admin_phone_id.text = phoneList!![position].id.toString()
        holder.admin_phone_facid.text = phoneList!![position].facid.toString()
        holder.admin_phone_name.text = phoneList!![position].name
        holder.admin_phone_number.text = phoneList!![position].number
    }

    override fun getItemCount(): Int {
        return phoneList!!.size
    }

    interface RecyclerViewClickListener {
        fun onClick(v : View, position : Int)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var admin_phone_id : TextView
        var admin_phone_facid : TextView
        var admin_phone_name : TextView
        var admin_phone_number : TextView
        var modifyButton: ImageButton
        var deleteButton: ImageButton
        init {
            admin_phone_id = itemView.findViewById(R.id.admin_phone_id)
            admin_phone_facid = itemView.findViewById(R.id.admin_phone_facid)
            admin_phone_name = itemView.findViewById(R.id.admin_phone_name)
            admin_phone_number = itemView.findViewById(R.id.admin_phone_number)
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