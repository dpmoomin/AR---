package kr.ac.kumoh.map


import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.mapbox.mapboxsdk.Mapbox
import android.widget.CompoundButton
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.Navigation

class MainActivity : AppCompatActivity() {
    private lateinit var navHostFragment : NavHostFragment
    private lateinit var viewSwitch : SwitchCompat
    private var isRoute : Boolean = false
    private var x : Double = 0.0
    private var y : Double = 0.0
    private var facilityName : String? = null
    private var buildingName : String? = null
    //mapView 생성을 위한 기본적인 형태의 onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 스플레시 스크린
        val splashScreen = installSplashScreen()

        // Mapbox 엑세스 토큰 설정
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))

        // This contains the MapView in XML and needs to be called after the access token is configured.
        setContentView(R.layout.activity_main)

        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        //toggle 스위치 설정
        viewSwitch = findViewById(R.id.viewSwitch);
        viewSwitch.setOnCheckedChangeListener(viewSwitchListener())

        // 필요한 권한
        val PERMISSIONS = arrayOf (
            android.Manifest.permission.CAMERA, // AR뷰 카메라 뷰포인트
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE, // ??
            android.Manifest.permission.ACCESS_COARSE_LOCATION, // 유저 위치 (대략적)
            android.Manifest.permission.ACCESS_FINE_LOCATION // 유저 위치 (정확)
        )

        // 권한 요청
        if (!hasPermissions(this, PERMISSIONS)) {
            requestPermissions(PERMISSIONS, 1000)
        }

        mContext = this
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_search ->
            {
                startActivity(Intent(this, SearchActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun setDestination(x:Double, y:Double) {
        (navHostFragment.childFragmentManager.fragments.get(0) as Map2DFragment).onDestinationButtonClick(x, y)
        isRoute = true
        this.x = x
        this.y = y
    }

    private fun action_nav_2D_to_AR() {
        val navController: NavController =
            Navigation.findNavController(this, R.id.nav_host_fragment)
        navController.navigate(R.id.action_nav_2D_to_AR)
    }

    fun getX() : Double {
        return x
    }

    fun getY() : Double {
        return y
    }

    fun getFacilityName() : String {
        return facilityName!!
    }

    fun setFacilityName(name : String) {
        this.facilityName = name
    }

    fun getBuildingName() : String {
        return buildingName!!
    }

    fun setBuildingName(buildingName : String) {
        this.buildingName = buildingName
    }

    fun IsRoute() : Boolean {
        return isRoute
    }

    internal class viewSwitchListener : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
            if (isChecked) {
                (mContext as MainActivity).action_nav_2D_to_AR()
            }
            else {
                // Do something..
            }
        }
    }

    fun switchOff(){
        viewSwitch.isChecked = false
    }

    companion object{
        lateinit var mContext : Context
    }

    fun hasPermissions(context: Context, permissions: Array<String>): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }
}