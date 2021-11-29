package com.iuricarras.ciclodias_test_new



import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.iuricarras.ciclodias_test_new.databinding.ActivityMainBinding
import com.mapbox.api.directions.v5.DirectionsCriteria
import com.mapbox.api.matching.v5.MapboxMapMatching
import com.mapbox.api.matching.v5.models.MapMatchingResponse
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.EdgeInsets
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.navigation.base.options.NavigationOptions
import com.mapbox.navigation.core.MapboxNavigation
import com.mapbox.navigation.core.MapboxNavigationProvider
import com.mapbox.navigation.core.directions.session.RoutesObserver
import com.mapbox.navigation.core.trip.session.LocationMatcherResult
import com.mapbox.navigation.core.trip.session.LocationObserver
import com.mapbox.navigation.ui.maps.location.NavigationLocationProvider
import com.mapbox.navigation.ui.maps.route.line.MapboxRouteLineApiExtensions.setRoutes
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineOptions
import com.mapbox.navigation.ui.maps.route.line.model.RouteLine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * The example demonstrates how to listen to your own location updates and represent it on the map.
 *
 * Before running the example make sure you do the following:
 * - Put your access_token in the correct place inside [app/src/main/res/values/mapbox_access_token.xml].
 *   If not present then add this file at the location mentioned above and add the following
 *   content to it.
 *   <?xml version="1.0" encoding="utf-8"?>
 *   <resources xmlns:tools="http://schemas.android.com/tools">
 *       <string name="mapbox_access_token">YOUR_ACCESS_TOKEN_HERE</string>
 *   </resources>
 * - Add MAPBOX_DOWNLOADS_TOKEN to your USER_HOME»/.gradle/gradle.properties file.
 *   To find out how to get your MAPBOX_DOWNLOADS_TOKEN follow these steps.
 *   https://docs.mapbox.com/android/beta/navigation/guides/install/#configure-credentials
 *
 * The example assumes that you have granted location permissions and does not enforce it. Since,
 * it's a standard procedure to ask for runtime permissions the example doesn't implements that
 * piece of code. However, this permission is essential for the proper functioning of this example.
 *
 * How to use this example:
 * - Click on the example with title (Render current location on a map) from the list of examples.
 * - You should see a map view with the camera transitioning to your current location.
 * - A blue circular puck should be visible at your current location.
 */
class ShowCurrentLocationActivity : AppCompatActivity() {

    val routeLineOptions = MapboxRouteLineOptions.Builder(this).build()
    val routeLineApi = MapboxRouteLineApi(routeLineOptions)
    val routeLineView = MapboxRouteLineView(routeLineOptions)


    /**
     * [NavigationLocationProvider] is a utility class that helps to provide location updates generated by the Navigation SDK
     * to the Maps SDK in order to update the user location indicator on the map.
     */
    private val navigationLocationProvider = NavigationLocationProvider()

    /**
     * Gets notified with location updates.
     *
     * Exposes raw updates coming directly from the location services
     * and the updates enhanced by the Navigation SDK (cleaned up and matched to the road).
     */
    private val locationObserver = object : LocationObserver {
        /**
         * Invoked as soon as the [Location] is available.
         */
        override fun onNewRawLocation(rawLocation: Location) {
// Not implemented in this example. However, if you want you can also
// use this callback to get location updates, but as the name suggests
// these are raw location updates which are usually noisy.
        }

        /**
         * Provides the best possible location update, snapped to the route or
         * map-matched to the road if possible.
         */
        override fun onNewLocationMatcherResult(locationMatcherResult: LocationMatcherResult) {
            val enhancedLocation = locationMatcherResult.enhancedLocation
            navigationLocationProvider.changePosition(
                    enhancedLocation,
                    locationMatcherResult.keyPoints,
            )
// Invoke this method to move the camera to your current location.
            updateCamera(enhancedLocation)
        }
    }

    /**
     * Mapbox Maps entry point obtained from the [MapView].
     * You need to get a new reference to this object whenever the [MapView] is recreated.
     */
    private lateinit var mapboxMap: MapboxMap

    /**
     * Mapbox Navigation entry point. There should only be one instance of this object for the app.
     * You can use [MapboxNavigationProvider] to help create and obtain that instance.
     */
    private lateinit var mapboxNavigation: MapboxNavigation

    /**
     * Bindings to the example layout.
     */
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val routesObserver: RoutesObserver = RoutesObserver { routeUpdateResult ->
// RouteLine: wrap the DirectionRoute objects and pass them
// to the MapboxRouteLineApi to generate the data necessary to draw the route(s)
// on the map.
            val routeLines = routeUpdateResult.routes.map { RouteLine(it, null) }

            val routeLineApi =
            routeLineApi.setRoutes(
                    routeLines
            ) { value ->
// RouteLine: The MapboxRouteLineView expects a non-null reference to the map style.
// the data generated by the call to the MapboxRouteLineApi above must be rendered
// by the MapboxRouteLineView in order to visualize the changes on the map.
                mapboxMap.getStyle()?.apply {
                    routeLineView.renderRouteDrawData(this, value)
                }
            }
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mapboxMap = binding.mapView.getMapboxMap()
// Instantiate the location component which is the key component to fetch location updates.
        binding.mapView.location.apply {
            setLocationProvider(navigationLocationProvider)

// Uncomment this block of code if you want to see a circular puck with arrow.
/*
locationPuck = LocationPuck2D(
bearingImage = ContextCompat.getDrawable(
this@ShowCurrentLocationActivity,
R.drawable.mapbox_navigation_puck_icon
)
)
*/

// When true, the blue circular puck is shown on the map. If set to false, user
// location in the form of puck will not be shown on the map.
            enabled = true
        }

        init()
    }

    private fun init() {
        initStyle()
        initNavigation()
    }

    @SuppressLint("MissingPermission")
    private fun initNavigation() {
        mapboxNavigation = MapboxNavigation(
                NavigationOptions.Builder(this)
                        .accessToken(getString(R.string.mapbox_access_token))
                        .build()
        ).apply {
// This is important to call as the [LocationProvider] will only start sending
// location updates when the trip session has started.
            startTripSession()
// Register the location observer to listen to location updates received from the
// location provider
            registerLocationObserver(locationObserver)
        }
    }

    private fun initStyle() {
        mapboxMap.loadStyleUri(Style.MAPBOX_STREETS)
    }

    private fun updateCamera(location: Location) {
        val mapAnimationOptions = MapAnimationOptions.Builder().duration(1500L).build()
        binding.mapView.camera.easeTo(
                CameraOptions.Builder()
// Centers the camera to the lng/lat specified.
                        .center(Point.fromLngLat(location.longitude, location.latitude))
// specifies the zoom value. Increase or decrease to zoom in or zoom out
                        .zoom(12.0)
// specify frame of reference from the center.
                        .padding(EdgeInsets(500.0, 0.0, 0.0, 0.0))
                        .build(),
                mapAnimationOptions
        )
    }

    override fun onDestroy() {
        super.onDestroy()
// make sure to stop the trip session. In this case it is being called inside `onDestroy`.
        mapboxNavigation.stopTripSession()
// make sure to unregister the observer you have registered.
        mapboxNavigation.unregisterLocationObserver(locationObserver)
    }
}