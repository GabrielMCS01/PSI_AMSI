package com.psi.ciclodias.model;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.maps.MapView;
import com.mapbox.maps.MapboxMap;
import com.mapbox.maps.Style;
import com.mapbox.maps.plugin.LocationPuck2D;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentUtils;
import com.mapbox.navigation.base.options.NavigationOptions;
import com.mapbox.navigation.core.MapboxNavigation;
import com.mapbox.navigation.ui.maps.location.NavigationLocationProvider;
import com.psi.ciclodias.R;

import java.util.List;

public class SingletonLocation implements PermissionsListener
{
    // Permissões para aceder á localização do dispositivo
    PermissionsManager permissionsManager;
    Activity activity;


    MapboxMap mapboxMap;
    NavigationLocationProvider navigationLocationProvider = new NavigationLocationProvider();
    MapboxNavigation mapboxNavigation;

    private static SingletonLocation instancia = null;

    public static synchronized SingletonLocation getInstancia(){
        if (instancia == null){ instancia = new SingletonLocation();}
        return instancia;
    }

    private SingletonLocation() { }


    public void carregarMapa(MapView mapView){
        mapboxMap = mapView.getMapboxMap();
        loadMap(mapView);
    }

    private void loadMap(MapView mapView) {

        mapboxMap.loadStyleUri("mapbox://styles/mapbox/outdoors-v11", new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {

                LocationComponentPlugin locationComponentPlugin = LocationComponentUtils.getLocationComponent(mapView);


                //MapSDK  - LocationPlugin - NavigationProvider - MapBoxNavigation - NavigationSDK
                locationComponentPlugin.setLocationProvider(navigationLocationProvider);

                LocationPuck2D locationPuck2D = new LocationPuck2D();
                locationPuck2D.setBearingImage(ContextCompat.getDrawable(activity.getApplicationContext(),R.drawable.mapbox_navigation_puck_icon));

                // Colocar no mapa
                locationComponentPlugin.setLocationPuck(locationPuck2D);

                locationComponentPlugin.setEnabled(true);

                // Pedir permissões ao utilizador e começar a navegação
                enableLocationComponent();

            }
        });
    }

    private void enableLocationComponent() {
        if (PermissionsManager.areLocationPermissionsGranted(activity.getApplicationContext())) {

            mapboxNavigation = new MapboxNavigation(new NavigationOptions.Builder(activity.getApplicationContext()).accessToken(getString(R.string.mapbox_access_token)).build());

            // Inicia a navegação
            mapboxNavigation.startTripSession();
            mapboxNavigation.registerLocationObserver(locationObs);

            // Recebe o cronometro e inicia-o

            chronometer.start();
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(activity);
        }
    }


    @Override
    public void onExplanationNeeded(List<String> list) {
        Toast.makeText(activity.getApplicationContext(), R.string.txtPermissaoExplicacao, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if(granted){

        }else{
            Toast.makeText(activity.getApplicationContext(), R.string.txtPermissaoNãoDada, Toast.LENGTH_LONG).show();
        }
    }

    public void onDestroy(){

    }
}
