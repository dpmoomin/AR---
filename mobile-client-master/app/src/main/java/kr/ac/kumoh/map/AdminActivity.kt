package kr.ac.kumoh.map

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kr.ac.kumoh.map.vo.Building
import kr.ac.kumoh.map.vo.Facility

class AdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        admin_mContext = this
        setContentView(R.layout.activity_admin)
    }
    companion object{
        lateinit var admin_mContext : Context
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home ->
            {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}