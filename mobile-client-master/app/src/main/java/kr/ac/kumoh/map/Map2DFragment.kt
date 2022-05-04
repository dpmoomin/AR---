package kr.ac.kumoh.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.geojson.Feature
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.expressions.Expression
import com.mapbox.mapboxsdk.style.layers.LineLayer
import com.mapbox.mapboxsdk.style.layers.Property
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.textField
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.navigation.base.internal.route.RouteUrl
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.MapboxNavigationProvider
import com.mapbox.navigation.core.directions.session.RoutesRequestCallback
import com.mapbox.navigation.core.trip.session.OffRouteObserver

class Map2DFragment : Fragment(), OnMapReadyCallback, PermissionsListener{

    private var permissionsManager: PermissionsManager = PermissionsManager(this)
    private lateinit var mapboxMap: MapboxMap
    private lateinit var mapView : MapView
    private lateinit var mapboxNavigation : MapboxNavigation
    private val ORIGIN_COLOR = "#32a852" // Green - 경로 색

    private var originPoint : Point = Point.fromLngLat(0.0, 0.0)
    private var destinationPoint : Point = Point.fromLngLat(0.0, 0.0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_map2d, container, false)

        mapView = view.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        val navigationOptions =
            MapboxNavigation.defaultNavigationOptionsBuilder(requireContext(), getString(R.string.mapbox_access_token))
                .build()
        mapboxNavigation = MapboxNavigationProvider.create(navigationOptions)

        return view;
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap
        mapboxMap.setStyle(getString(R.string.mapbox_style_mapbox_streets)) {
            // Map is set up and the style has loaded. Now you can add data or make other map adjustments
            enableLocationComponent(it)
            addOnDestinationIconSymbolLayer(it)
            it.addSource(
                GeoJsonSource(
                    "ROUTE_LINE_SOURCE_ID",
                    GeoJsonOptions().withLineMetrics(true)
                )
            )
            it.addLayerBelow(
                LineLayer("ROUTE_LAYER_ID", "ROUTE_LINE_SOURCE_ID")
                    .withProperties(
                        PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                        PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                        PropertyFactory.lineWidth(6f),
                        PropertyFactory.lineGradient(
                            Expression.interpolate(
                                Expression.linear(),
                                Expression.lineProgress(),
                                Expression.stop(
                                    0f,
                                    Expression.color(Color.parseColor(ORIGIN_COLOR))
                                ),
                                Expression.stop(
                                    1f,
                                    Expression.color(Color.parseColor(ORIGIN_COLOR))
                                )
                            )
                        )
                    ),
                "mapbox-location-shadow-layer"
            )

            // 지도 라벨을 한국어로 설정 (일부는 여전히 영어로 나옴)
            val labelLayer = it.getLayer("settlement-label")
            labelLayer?.setProperties(textField("{name_ko}"))

            // 나침반 표시 (온스크린 지도와 연동)
            mapboxMap.uiSettings.isCompassEnabled = true
        }
        val defaultZoom = CameraPosition.Builder().zoom(16.0).build()
        mapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(defaultZoom))
    }

    //목적지위에 심볼(마크)을 표시
    private fun addOnDestinationIconSymbolLayer(style: Style) {
        style.addImage("destination-icon-id", BitmapFactory.decodeResource(this.resources,R.drawable.mapbox_marker_icon_default))
        var geoJsonSource: GeoJsonSource = GeoJsonSource("destination-source-id")
        style.addSource(geoJsonSource)

        var destinationSymbolLayer: SymbolLayer = SymbolLayer("destination-symbol-layer-id", "destination-source-id")
        destinationSymbolLayer.withProperties(
            PropertyFactory.iconImage("destination-icon-id"),
            PropertyFactory.iconAllowOverlap(true),
            PropertyFactory.iconIgnorePlacement(true)
        )
        style.addLayer(destinationSymbolLayer)
    }


    fun onDestinationButtonClick(x: Double,y : Double){
        destinationPoint= Point.fromLngLat(x, y) // 목적지 좌표
        originPoint= Point.fromLngLat(
            mapboxMap.locationComponent.lastKnownLocation!!.longitude,
            mapboxMap.locationComponent.lastKnownLocation!!.latitude) // 사용자의 현재 위치

        val origin = originPoint
        val destination = destinationPoint

        var geoJsonSource: GeoJsonSource = mapboxMap.getStyle()!!.getSourceAs<GeoJsonSource>("destination-source-id")!!
        if(geoJsonSource != null) {
            geoJsonSource.setGeoJson(Feature.fromGeometry(destinationPoint))
        }

        getRoute(origin, destination)
    }

    //mapboxNavigation을 통해 경로를 구해줌 Route라는 객체로 반환(이것이 바로 그래픽으로 출력 되는 것은 아님 OnMapReady에서 경로를 그려주는 것)
    private fun getRoute(origin: Point, destination: Point){
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
            .build(), routesReqCallback)
    }


    //사용자의 현재 위치를 나타내기 위한 메소드
    @SuppressLint("MissingPermission")
    private fun enableLocationComponent(loadedMapStyle: Style) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(activity)) {

            // Create and customize the LocationComponent's options
            val customLocationComponentOptions = LocationComponentOptions.builder(requireContext())
                .trackingGesturesManagement(true)
                .accuracyColor(ContextCompat.getColor(requireContext(), R.color.mapbox_blue))
                .build()

            val locationComponentActivationOptions = LocationComponentActivationOptions.builder(requireContext(), loadedMapStyle)
                .locationComponentOptions(customLocationComponentOptions)
                .build()

            // Get an instance of the LocationComponent and then adjust its settings
            mapboxMap.locationComponent.apply {

                // Activate the LocationComponent with options
                activateLocationComponent(locationComponentActivationOptions)

                // Enable to make the LocationComponent visible
                isLocationComponentEnabled = true

                // Set the LocationComponent's camera mode
                cameraMode = CameraMode.TRACKING

                // Set the LocationComponent's render mode
                renderMode = RenderMode.COMPASS
            }
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(requireActivity())
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onExplanationNeeded(permissionsToExplain: List<String>) {
        Toast.makeText(requireContext(), "user_location_permission_explanation", Toast.LENGTH_LONG).show()
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            enableLocationComponent(mapboxMap.style!!)
        } else {
            Toast.makeText(requireContext(), "user_location_permission_not_granted", Toast.LENGTH_LONG).show()
            requireActivity().finish()
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
        mapboxNavigation.registerOffRouteObserver(offRouteObserver)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
        mapboxNavigation.unregisterOffRouteObserver(offRouteObserver)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    private val routesReqCallback = object : RoutesRequestCallback
    {
        override fun onRoutesReady(routes: List<DirectionsRoute>) {
            if (routes.isNotEmpty()) {
                Snackbar.make(
                    mapView,
                    String.format(
                        "경로 안내를 시작합니다.",
                        routes[0].legs()?.get(0)?.steps()?.size
                    ),
                    Snackbar.LENGTH_SHORT
                ).show()
                mapboxMap?.getStyle {
                    val clickPointSource = it.getSourceAs<GeoJsonSource>("ROUTE_LINE_SOURCE_ID")
                    val routeLineString = LineString.fromPolyline(
                        routes[0].geometry()!!,
                        6
                    )
                    clickPointSource?.setGeoJson(routeLineString)
                }
                mapboxNavigation.setRoutes(routes)
            } else {
                Snackbar.make(mapView, "no_routes", Snackbar.LENGTH_SHORT).show()
            }
        }

        override fun onRoutesRequestCanceled(routeOptions: RouteOptions) {
            TODO("Not yet implemented")
        }

        override fun onRoutesRequestFailure(throwable: Throwable, routeOptions: RouteOptions) {
            TODO("Not yet implemented")
        }
    }

    val offRouteObserver = object : OffRouteObserver {
        override fun onOffRouteStateChanged(offRoute: Boolean) {
            if(!originPoint.latitude().equals(0.0)) {
                originPoint= Point.fromLngLat(
                    mapboxMap.locationComponent.lastKnownLocation!!.longitude,
                    mapboxMap.locationComponent.lastKnownLocation!!.latitude) // 사용자의 현재 위치

                val origin = originPoint
                val destination = destinationPoint

                var geoJsonSource: GeoJsonSource =
                    mapboxMap.getStyle()!!.getSourceAs<GeoJsonSource>("destination-source-id")!!
                if (geoJsonSource != null) {
                    geoJsonSource.setGeoJson(Feature.fromGeometry(destinationPoint))
                }

                getRoute(origin, destination)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }
}