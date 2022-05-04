package kr.ac.kumoh.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.*
import android.net.Uri
import android.os.Bundle
import android.widget.CompoundButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.app.ActivityCompat
import androidx.core.content.getSystemService
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.ar.core.*
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import com.google.maps.android.SphericalUtil
import com.google.maps.android.ktx.utils.sphericalHeading
import com.mapbox.android.core.location.*
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.navigation.base.internal.route.RouteUrl
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.MapboxNavigationProvider
import com.mapbox.navigation.core.directions.session.RoutesRequestCallback
import kr.ac.kumoh.map.MainActivity.Companion.mContext
import java.lang.Exception
import java.util.concurrent.CompletableFuture

import com.google.ar.sceneform.FrameTime
import com.google.ar.core.Pose
import com.google.ar.sceneform.math.Quaternion
import java.util.*
import kotlin.math.*


class ArActivity : AppCompatActivity(), SensorEventListener, LocationEngineCallback<LocationEngineResult> {
    //기본 변수
    private lateinit var arFragment: ArFragment

    private lateinit var mapboxNavigation: MapboxNavigation
    private lateinit var locationEngine: LocationEngine

    //가속도 센서 관련 변수
    private lateinit var sensorManager: SensorManager

    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)

    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    //모델링 선언
    private lateinit var guide3D : CompletableFuture<ModelRenderable>
    private lateinit var sign3D : CompletableFuture<ModelRenderable>
    private lateinit var locator3D : CompletableFuture<ModelRenderable>

    //경로 관련 변수
    private var originPoint: Point? = null
    private var destinationPoint: Point? = null
    private var destinationName: String? = null
    private var destinationBuildingName: String? = null
    private var isRoute: Boolean = false
    private lateinit var routeLineString : LineString
    private var wayposition = 0
    private var wayPoint: Point? = null
    private var anchorNode : AnchorNode? = null
    private var distance : Double? = null
    private var wayPointLatLng : LatLng? = null
    private var latLng : LatLng? = null
    private var wayPointChanged : Boolean = false
    private var isStart : Boolean = false
    private var preAngle : Float = 0f
    private var preDistance : Double? = null
    private var isSession : Boolean = false
    private var orientAngle : Float? = null
    private var angle : Float? = null
    private var preOrientAngle : Float? = null
    private var heading : Double? = null
    private var angleWeight : Float = -0.5f
    private var angleWeight2 : Float = -0.5f
    private var isLocatorOn : Boolean = false
    private var isLotation : Boolean = false
    private var startLotation : Float? = null

    //테스트 UI
    private lateinit var distanceText : TextView
    private lateinit var orientAngleText : TextView
    private lateinit var headingText : TextView

    private var routeList: MutableList<AnchorNode> = mutableListOf<AnchorNode>()
    private var route3DQueue: Queue<AnchorNode> = LinkedList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar)
        distanceText = findViewById(R.id.distance)
        orientAngleText = findViewById(R.id.orientAngle)
        headingText = findViewById(R.id.heading)
        //위치 제공자 초기화
        initLocationEngine()

        //mapboxNavigation 초기화
        initMapboxNavigation()

        //목적지 얻어오기
        if((mContext as MainActivity).IsRoute()){
            destinationPoint = Point.fromLngLat((mContext as MainActivity).getX(),(mContext as MainActivity).getY())
            destinationName = (mContext as MainActivity).getFacilityName()
            destinationBuildingName = (mContext as MainActivity).getBuildingName()
            headingText.text = "목적지 : " + destinationName
            orientAngleText.text = "소속건물 : " + destinationBuildingName
            isStart = true
        }

        sensorManager = getSystemService()!!

        guide3D = ModelRenderable.builder()
            .setSource(applicationContext, Uri.parse("arrow.sfb"))
            .build()
            .exceptionally {
                val builder: AlertDialog.Builder =
                    AlertDialog.Builder(applicationContext)
                builder.setMessage(it.localizedMessage)
                    .show()
                return@exceptionally null
            }

        sign3D = ModelRenderable.builder()
            .setSource(applicationContext, Uri.parse("sign2.sfb"))
            .build()
            .exceptionally {
                val builder: AlertDialog.Builder =
                    AlertDialog.Builder(applicationContext)
                builder.setMessage(it.localizedMessage)
                    .show()
                return@exceptionally null
            }

        locator3D = ModelRenderable.builder()
            .setSource(applicationContext, Uri.parse("location.sfb"))
            .build()
            .exceptionally {
                val builder: AlertDialog.Builder =
                    AlertDialog.Builder(applicationContext)
                builder.setMessage(it.localizedMessage)
                    .show()
                return@exceptionally null
            }

        //arFragment 객체 얻기
        arFragment = getArFragment()

        arFragment.getArSceneView().getScene().addOnUpdateListener(this::onSceneUpdate)

        arContext = this

        //toggle 스위치 설정
        val viewSwitch : SwitchCompat = findViewById(R.id.viewSwitch);
        viewSwitch.setOnCheckedChangeListener(viewSwitchListener())
    }

    private val routesReqCallback = object : RoutesRequestCallback {
        override fun onRoutesReady(routes: List<DirectionsRoute>) {
            if (routes.isNotEmpty() && originPoint != null && destinationPoint != null) {
                /*
                Handler(mainLooper).postDelayed({
                      getRoute(originPoint!!, destinationPoint!!)
                }, 2000)
                */
                routeLineString = LineString.fromPolyline(
                    routes[0].geometry()!!,
                    6
                )
                isRoute = true
                mapboxNavigation.setRoutes(routes)
                wayposition = 0
                wayPoint = routeLineString.coordinates().get(wayposition)
                //웨이포인트 및 현재 위치를 Point에서 LatLng객체로 변환
                wayPointLatLng = LatLng(wayPoint!!.latitude(), wayPoint!!.longitude())
                latLng = LatLng(originPoint!!.latitude(), originPoint!!.longitude())
                //현재 웨이포인트와의 거리
                distance = SphericalUtil.computeDistanceBetween(wayPointLatLng, latLng)
            } else {
                isRoute = false
            }
        }

        override fun onRoutesRequestCanceled(routeOptions: RouteOptions) {
            isRoute = false
            TODO("Not yet implemented")
        }

        override fun onRoutesRequestFailure(throwable: Throwable, routeOptions: RouteOptions) {
            isRoute = false
            TODO("Not yet implemented")
        }
    }

    //ArView상에 루트 추가
    private fun addRouteToScene(
        isLocator:Boolean,
        modelRenderable: ModelRenderable,
        position: Vector3,
        rotation: Quaternion,
        scale: Float,
        height: Float,
    ) {

        val cameraPos: Vector3 = arFragment.arSceneView.scene.camera.worldPosition
        val cameraPose = Pose.makeTranslation(cameraPos.x,cameraPos.y,cameraPos.z)
        var anchor: Anchor? = arFragment.arSceneView.session!!.createAnchor(cameraPose)
        val anchorNode2 = AnchorNode(anchor)

        val transformableNode = TransformableNode(arFragment?.transformationSystem)
        transformableNode.setParent(anchorNode2)
        transformableNode.renderable = modelRenderable
        transformableNode.scaleController.maxScale = scale
        transformableNode.scaleController.minScale = scale-0.001f

        var q1: Quaternion? = null
        var q2: Quaternion? = null
        if(!isLocator){
            q2 = Quaternion.axisAngle(Vector3(0f, 1f, 0f), floor(((180 * (startLotation!!-orientationAngles[0]+preOrientAngle!!)/ PI)-heading!!)).toFloat())
        }
        else{
            q2 = Quaternion.axisAngle(Vector3(0f, 0f, 0f), floor(((180 * (startLotation!!-orientationAngles[0]+preOrientAngle!!) / PI)-heading!!)).toFloat())
        }
        q1 = Quaternion.multiply(rotation, q2)
        transformableNode.localRotation = q1
        transformableNode.localPosition = Vector3(position.x, position.y+height, position.z)

        if(!isLocator) {
            if (route3DQueue.size >= 2) {
                route3DQueue.peek().anchor!!.detach()
                route3DQueue.peek().setParent(null)
                route3DQueue.poll()
            }
            route3DQueue.add(anchorNode2)
        }
        arFragment.arSceneView.scene.addChild(anchorNode2)
        transformableNode.select()
    }

    //위치를 성공적으로 획득했을 때
    override fun onSuccess(result: LocationEngineResult?) {
        val location = (result!!.lastLocation!!)
        originPoint = Point.fromLngLat(location.longitude, location.latitude)
        if(isStart){
            getRoute(originPoint!!,destinationPoint!!)
            preDistance = 10000.0
            preOrientAngle = 10.0f
            orientAngle = orientationAngles[0]
            angle = orientationAngles[0]
            isStart = false
        }
        if(isRoute)
        {
            //웨이포인트 및 현재 위치를 Point에서 LatLng객체로 변환
            wayPointLatLng = LatLng(wayPoint!!.latitude(), wayPoint!!.longitude())
            latLng = LatLng(originPoint!!.latitude(), originPoint!!.longitude())
            //현재 웨이포인트와의 거리
            distance = SphericalUtil.computeDistanceBetween(wayPointLatLng, latLng)
            distanceText.text = "남은거리 : " + floor(distance!!).toInt().toString()+"m"

            //바닥 경로 표시
            if(isSession && preOrientAngle != null){
                val rotation = Quaternion.axisAngle(Vector3(0f, -1f, 0f), 86f)
                addRouteToScene(false, sign3D.get(), getPositionVector(startLotation!!-orientationAngles[0]+preOrientAngle!!,wayPointLatLng!!,latLng!!, 3.5f,),rotation,0.2f, 0f)
                preDistance = distance
            }

            //목적지 표시
            if(!isLocatorOn &&(distance!! in 20.0..25.0) && isSession && preOrientAngle != null){
                val q1 = Quaternion.axisAngle(Vector3(0f, 0f, -1f), 87f)
                val q2 = Quaternion.axisAngle(Vector3(1f,0f,0f),90f)
                val rotation = Quaternion.multiply(q1,q2)
                addRouteToScene(true, locator3D.get(), getPositionVector(startLotation!!-orientationAngles[0]+preOrientAngle!!,wayPointLatLng!!,latLng!!, 30.0f),rotation,0.4f, 1.0f)
                isLocatorOn = true
            }

            if (distance!! in 1.0..3.5) {
                //웨이 포지션이 마지막이 아니라면 다음 웨이포인트로 이동
                if (wayposition < routeLineString.coordinates().size) {
                    wayposition += 1
                    wayPoint = Point.fromLngLat(
                        routeLineString.coordinates().get(wayposition).longitude(),
                        routeLineString.coordinates().get(wayposition).latitude()
                    )
                    wayPointChanged = true
                    preDistance = 10000.0
                }
                //웨이 포지션이 마지막이라면 안내 종료
                else {
                    isRoute = false
                }
            }
        }
    }

    override fun onFailure(p0: Exception) {
        TODO("Not yet implemented")
    }

    override fun onStop() {
        super.onStop()
        locationEngine.removeLocationUpdates(this)
    }

    private fun onSceneUpdate(frameTime: FrameTime) {
        // Let the fragment update its state first.
        arFragment.onUpdate(frameTime)

        // If there is no frame then don't process anything.
        if (arFragment.arSceneView.arFrame == null) {
            isSession = false
            return
        }

        // If ARCore is not tracking yet, then don't process anything.
        if (arFragment.arSceneView.arFrame!!.camera.trackingState != TrackingState.TRACKING) {
            isSession = false
            return
        }

        //단말의 각도가 안정되면 바닥 경로안내 시작
        if(angleWeight >= 0.0f){
            isSession = true
        }

        // Place the anchor 1m in front of the camera if anchorNgode is null.
        if (isRoute) {
            if(!isLotation) {
                startLotation = orientationAngles[0]
                isLotation = true
            }
            val cameraPos: Vector3 = arFragment.arSceneView.scene.camera.worldPosition
            val cameraForward: Vector3 = arFragment.arSceneView.scene.camera.forward
            val position = Vector3.add(cameraPos, cameraForward.scaled(1.0f))
            // Create an ARCore Anchor at the position.
            var pose = Pose.makeTranslation(position.x, position.y, position.z)
            val rotation = floatArrayOf(
                arFragment.arSceneView.scene.camera.localRotation.x,
                arFragment.arSceneView.scene.camera.localRotation.y,
                arFragment.arSceneView.scene.camera.localRotation.z,
                arFragment.arSceneView.scene.camera.localRotation.w)
            val pose2 = Pose(pose.translation,rotation)
            var anchor: Anchor? = arFragment.arSceneView.session!!.createAnchor(pose2)

            /*
            val position : FloatArray = floatArrayOf(0f, 0f, -1f)
            val rotation : FloatArray = floatArrayOf(0f, 0f, 0f, 1f)
            val pose = Pose(position, rotation)
            val anchor : Anchor = session!!.createAnchor(pose)
            */
            anchorNode = AnchorNode(anchor)
            val transformableNode = TransformableNode(arFragment?.transformationSystem)
            transformableNode.renderable = guide3D.get()
            transformableNode.scaleController.maxScale = 0.2f
            transformableNode.scaleController.minScale = 0.199f
            transformableNode.setParent(anchorNode)

            var q1 = Quaternion.axisAngle(Vector3(1f, 0f, 0f), 110f)

            if (isRoute) {
                heading = latLng!!.sphericalHeading(wayPointLatLng!!)
                orientAngle = ((1+angleWeight)*orientAngle!! + (1-angleWeight)*orientationAngles[0]) / 2
                angle = ((1+angleWeight2)*orientAngle!! + (1-angleWeight2)*orientationAngles[0]) / 2
                //각도 변화가 22.5도 이상이면 적용
                if(abs(preAngle!!-orientationAngles[0]!!) > 22.5*PI/180)
                {
                    preAngle=orientationAngles[0]
                    preOrientAngle = orientationAngles[0]
                    angleWeight = -0.2f
                    angleWeight2 = -0.2f
                }
                else
                {
                    //평균의 변화가 1.0이상이면 적용
                    if(abs(preOrientAngle!!-orientAngle!!) > 1.5*PI/180) {
                        preOrientAngle = orientAngle
                        if(angleWeight < 1.0f) {
                            angleWeight += 0.03f
                        }
                    }

                    //각도의 변화가 6.5도이상이면 적용 - 방향 지시
                    if(abs(preAngle-orientationAngles[0]!!) > 6.5*PI/180){
                        preAngle=orientationAngles[0]
                        angleWeight2 = 0.2f
                    }
                    //평균의 변화가 1.0 이상이면 적용 - 방향 지시
                    else if(abs(preAngle!!-angle!!) > 1.5*PI/180) {
                        preAngle = angle!!
                        if(angleWeight2 < 1.0f) {
                            angleWeight2 += 0.03f
                        }
                    }
                }
                var orientAngleToDegree = 180 * preAngle!!/PI
                //orientAngleText.text = "현재방향 : " + floor(orientAngleToDegree).toString()
                val q2 = Quaternion.axisAngle(Vector3(0f, 0f, 1f), floor((heading!!-orientAngleToDegree)).toFloat())
                q1 = Quaternion.multiply(q1,q2)
            }
            transformableNode.localRotation = q1
            if (routeList.isNotEmpty()) {
                routeList.get(0).anchor!!.detach()
                routeList.get(0).setParent(null)
                routeList.removeAt(0)
            }
            anchorNode!!.setParent(arFragment.arSceneView.scene)
            routeList.add(anchorNode!!)
        }
    }

    private fun initLocationEngine() {
        //권한 확인
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val locationEngineRequest = LocationEngineRequest.Builder(1000)
            .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
            .setMaxWaitTime(1000) // sets the maximum wait time in milliseconds for location updates. Locations determined at intervals but delivered in batch based on wait time. Batching is not supported by all engines.
            .build()

        locationEngine = LocationEngineProvider.getBestLocationEngine(this)

        locationEngine.requestLocationUpdates(
            locationEngineRequest,
            this,
            mainLooper // returns your project's main looper, which lives in your project's main thread.
        )
    }

    fun getPositionVector(azimuth: Float, nodeLatLng: LatLng, latLng: LatLng, distance:Float): Vector3 {
        val corretion = 0.0000
        val r = -distance
        val x = r!! * sin(azimuth - PI*heading!!/180+corretion).toFloat()
        val y = -1.5f
        val z = r!! * cos(azimuth - PI*heading!!/180+corretion).toFloat()
        return Vector3(x, y, z)
    }


    private fun initMapboxNavigation() {
        val navigationOptions =
            MapboxNavigation.defaultNavigationOptionsBuilder(
                this,
                getString(R.string.mapbox_access_token)
            )
                .build()

        mapboxNavigation = MapboxNavigationProvider.create(navigationOptions)
    }

    private fun getRoute(origin: Point, destination: Point) {
        mapboxNavigation.requestRoutes(
            RouteOptions.builder()
                .baseUrl(RouteUrl.BASE_URL)
                .user(RouteUrl.PROFILE_DEFAULT_USER)
                .profile(RouteUrl.PROFILE_WALKING)
                .geometries(RouteUrl.GEOMETRY_POLYLINE6)
                .requestUuid("")
                .accessToken(getString(R.string.mapbox_access_token))
                .coordinates(listOf(origin, destination))
                .alternatives(false)
                .build(), routesReqCallback
        )
    }


    override fun onResume() {
        super.onResume()
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.also {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) {
            return
        }
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(
                event.values,
                0,
                accelerometerReading,
                0,
                accelerometerReading.size
            )
        } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)
        }

        // Update rotation matrix, which is needed to update orientation angles.
        SensorManager.getRotationMatrix(
            rotationMatrix,
            null,
            accelerometerReading,
            magnetometerReading
        )
        SensorManager.getOrientation(rotationMatrix, orientationAngles)
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    fun getArFragment(): ArFragment {
        return (supportFragmentManager.findFragmentById(R.id.arFragment) as ArFragment)
    }

    internal class viewSwitchListener : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
            if (!isChecked) {
                (arContext as ArActivity).finish()
                (mContext as MainActivity).switchOff()
            }
            else {
                // Do something..
            }
        }
    }

    companion object{
        lateinit var arContext : Context
    }

    //버려진 코드
    /*
    //ArView상에 모델 추가
    private fun addModelToScene(anchor: Anchor, modelRenderable: ModelRenderable) {
        val anchorNode = AnchorNode(anchor)
        val transformableNode = TransformableNode(arFragment?.transformationSystem)
        transformableNode.setParent(anchorNode)
        transformableNode.renderable = modelRenderable
        arFragment.arSceneView.scene.addChild(anchorNode)
        transformableNode.select()
    }
     */
}