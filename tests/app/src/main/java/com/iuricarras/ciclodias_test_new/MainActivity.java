package com.iuricarras.ciclodias_test_new;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.location.Location;
import android.os.Bundle;

import android.widget.Chronometer;
import android.widget.Toast;

import com.iuricarras.ciclodias_test_new.databinding.ActivityMainBinding;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.EdgeInsets;
import com.mapbox.maps.plugin.LocationPuck2D;

import com.mapbox.maps.plugin.animation.CameraAnimationsPlugin;
import com.mapbox.maps.plugin.animation.CameraAnimationsPluginImpl;
import com.mapbox.maps.plugin.animation.CameraAnimationsUtils;
import com.mapbox.maps.plugin.animation.MapAnimationOptions;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin;

import com.mapbox.maps.plugin.locationcomponent.LocationComponentUtils;


import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.maps.MapboxMap;
import com.mapbox.maps.Style;
import com.mapbox.navigation.base.options.NavigationOptions;
import com.mapbox.navigation.core.MapboxNavigation;
import com.mapbox.navigation.core.trip.session.LocationMatcherResult;
import com.mapbox.navigation.core.trip.session.LocationObserver;
import com.mapbox.navigation.ui.maps.location.NavigationLocationProvider;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements PermissionsListener {


    private PermissionsManager permissionsManager;
    private MapboxMap mapboxMap;

    // Fazer ligação entre Navigation e Map
    private NavigationLocationProvider navigationLocationProvider = new NavigationLocationProvider();
    private MapboxNavigation mapboxNavigation;
    private ActivityMainBinding binding;
    private Chronometer chronometer;


    private int count = 0;
    private float velocity = 0;
    private float mean = 0;

    private Location loc1;
    private Location loc2;
    private float distance = 0;
    private boolean isLoc1 = false;



    //Ouve alterações da localização
    private LocationObserver locationObs = new LocationObserver() {

        @Override
        public void onNewRawLocation(@NonNull Location location) {
            // Lista para os keyPoints (vazia)
            List<Location> lista = new ArrayList<>();
            navigationLocationProvider.changePosition(location, lista, null, null);
            updateCamera(location);

            setVelocity(location);
            setVM(location);
            setDistance(location);

        }

        @Override
        public void onNewLocationMatcherResult(@NonNull LocationMatcherResult locationMatcherResult) {

        }
    };

    private void updateCamera(Location location) {
        MapAnimationOptions animationOptions = new MapAnimationOptions.Builder().duration(1500L).build();

        CameraAnimationsPlugin cameraAnimationsPlugin = CameraAnimationsUtils.getCamera(binding.mapView);

        CameraOptions cameraOptions = (new CameraOptions.Builder())
                .center(Point.fromLngLat(location.getLongitude(),location.getLatitude()))
                .zoom(17.0)
                .padding(new EdgeInsets(500.0, 0.0, 0.0, 0.0))
                .build();

            cameraAnimationsPlugin.easeTo(cameraOptions, animationOptions);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_navigation_sdkactivity);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mapboxMap = binding.mapView.getMapboxMap();

        mapboxMap.loadStyleUri("mapbox://styles/mapbox/outdoors-v11", new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {

                LocationComponentPlugin locationComponentPlugin = LocationComponentUtils.getLocationComponent(binding.mapView);

                //MapSDK  - LocationPlugin - NavigationProvider - MapBoxNavigation - NavigationSDK
                locationComponentPlugin.setLocationProvider(navigationLocationProvider);


                LocationPuck2D locationPuck2D = new LocationPuck2D();
                locationPuck2D.setBearingImage(ContextCompat.getDrawable(getApplicationContext(),R.drawable.mapbox_navigation_puck_icon));
                //Colocar no mapa
                locationComponentPlugin.setLocationPuck(locationPuck2D);

                locationComponentPlugin.setEnabled(true);

                //pedir permissões ao utilizador e começar a navegação
                enableLocationComponent();
            }
        });



    }

    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent() {
// Check if permissions are enabled and if not requ
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            
            mapboxNavigation = new MapboxNavigation(new NavigationOptions.Builder(this).accessToken(getString(R.string.mapbox_access_token)).build());

            mapboxNavigation.startTripSession();
            mapboxNavigation.registerLocationObserver(locationObs);
            chronometer = binding.timer;

            chronometer.start();



        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        // Volta a colocqr o estilo no mapa caso tenha as permissões
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent();
                }
            });
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void setVelocity(Location location){

        float nCurrentSpeed = 0;

        nCurrentSpeed = location.getSpeed() * 3.6F;

        Formatter fmt = new Formatter(new StringBuilder());
        fmt.format(Locale.US, "%5.2f", nCurrentSpeed);
        String strCurrentSpeed = fmt.toString();
        strCurrentSpeed = strCurrentSpeed.replace(' ', '0');

        String strUnits = "Km/h";

        binding.txtCurrentSpeed.setText(strCurrentSpeed + " " + strUnits);

    }

    private void setVM(Location location){

        float speed = location.getSpeed() * 3.6F;

        count++;
        velocity = velocity + speed;

        mean = velocity / count;

        Formatter fmt = new Formatter(new StringBuilder());
        fmt.format(Locale.US, "%5.2f", mean);
        String strCurrentSpeed = fmt.toString();
        strCurrentSpeed = strCurrentSpeed.replace(' ', '0');

        String strUnits = "Km/h";

        binding.tvVM.setText(strCurrentSpeed + " " + strUnits);

    }

    private void setDistance(Location location){

        if(!isLoc1){
            loc1 = location;
            isLoc1 = true;
        }else{
            loc2 = location;
            float newDistance = loc1.distanceTo(loc2);
            loc1 = loc2;
            distance = distance + newDistance;
        }

        Formatter fmt = new Formatter(new StringBuilder());
        fmt.format(Locale.US, "%7.2f", distance);
        String strCurrentSpeed = fmt.toString();
        strCurrentSpeed = strCurrentSpeed.replace(' ', '0');

        String strUnits = "m";

       binding.tvDistance.setText(strCurrentSpeed + " " + strUnits);
    }



    @Override
    @SuppressWarnings( {"MissingPermission"})
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        chronometer.stop();
        mapboxNavigation.stopTripSession();
        mapboxNavigation.unregisterLocationObserver(locationObs);

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

    }


}