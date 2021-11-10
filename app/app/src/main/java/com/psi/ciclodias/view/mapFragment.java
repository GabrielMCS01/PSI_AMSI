package com.psi.ciclodias.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.EdgeInsets;
import com.mapbox.maps.MapView;
import com.mapbox.maps.MapboxMap;
import com.mapbox.maps.Style;
import com.mapbox.maps.plugin.LocationPuck2D;
import com.mapbox.maps.plugin.animation.CameraAnimationsPlugin;
import com.mapbox.maps.plugin.animation.CameraAnimationsUtils;
import com.mapbox.maps.plugin.animation.MapAnimationOptions;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentUtils;
import com.mapbox.navigation.base.options.NavigationOptions;
import com.mapbox.navigation.core.MapboxNavigation;
import com.mapbox.navigation.core.trip.session.LocationMatcherResult;
import com.mapbox.navigation.core.trip.session.LocationObserver;
import com.mapbox.navigation.ui.maps.location.NavigationLocationProvider;
import com.psi.ciclodias.R;

import java.util.ArrayList;
import java.util.List;

public class mapFragment extends Fragment implements PermissionsListener {

    //Gestor de Permissões
    private PermissionsManager permissionsManager;

    //Objetos necessarios para a gestão do mapa e da localização
    private MapView mapView;
    private MapboxMap mapboxMap;
    private NavigationLocationProvider navigationLocationProvider = new NavigationLocationProvider();
    private MapboxNavigation mapboxNavigation;


    private mapFragment() {
        // Required empty public constructor
    }

    private static mapFragment instancia = null;

    public static synchronized mapFragment getInstancia(){
        if (instancia == null){ instancia = new mapFragment();}
        return instancia;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        mapView = view.findViewById(R.id.mapView);
        mapboxMap = mapView.getMapboxMap();

        loadMap();

        return view;
    }

    private void loadMap() {

        mapboxMap.loadStyleUri("mapbox://styles/mapbox/outdoors-v11", new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {

                LocationComponentPlugin locationComponentPlugin = LocationComponentUtils.getLocationComponent(mapView);


                //MapSDK  - LocationPlugin - NavigationProvider - MapBoxNavigation - NavigationSDK
                locationComponentPlugin.setLocationProvider(navigationLocationProvider);

                LocationPuck2D locationPuck2D = new LocationPuck2D();
                locationPuck2D.setBearingImage(ContextCompat.getDrawable(getContext(),R.drawable.mapbox_navigation_puck_icon));

                // Colocar no mapa
                locationComponentPlugin.setLocationPuck(locationPuck2D);

                locationComponentPlugin.setEnabled(true);

                // Pedir permissões ao utilizador e começar a navegação
                enableLocationComponent();

            }
        });
    }

    @SuppressLint("MissingPermission")
    private void enableLocationComponent() {
        if (PermissionsManager.areLocationPermissionsGranted(getContext())) {

            mapboxNavigation = new MapboxNavigation(new NavigationOptions.Builder(getContext()).accessToken(getString(R.string.mapbox_access_token)).build());

            // Inicia a navegação
            mapboxNavigation.startTripSession();
            mapboxNavigation.registerLocationObserver(locationObs);

            // Recebe o cronometro e inicia-o
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(getActivity());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> list) {
        Toast.makeText(getContext(), R.string.txtPermissaoExplicacao, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if(granted){

        }else{
            Toast.makeText(getContext(), R.string.txtPermissaoNãoDada, Toast.LENGTH_LONG).show();
        }
    }

    // Ouve alterações da localização
    private LocationObserver locationObs = new LocationObserver() {
        // Quando tem uma nova alteração na localização
        @Override
        public void onNewRawLocation(@NonNull Location location) {
            // Lista para os keyPoints (vazia) rotas
            List<Location> lista = new ArrayList<>();
            navigationLocationProvider.changePosition(location, lista, null, null);
            // Função para atualizar a camera enviando a nova localização
            updateCamera(location);

            // Atualiza as funções de velocidade (Instântanea e média e a distância percorrida)
            // Envia a nova localização
            //setVelocity(location);
            //setVM(location);
            //setDistance(location);

        }

        @Override
        public void onNewLocationMatcherResult(@NonNull LocationMatcherResult locationMatcherResult) {

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapboxNavigation.stopTripSession();
        mapboxNavigation.unregisterLocationObserver(locationObs);

    }

    private void updateCamera(Location location) {
        // Animações na câmera
        MapAnimationOptions animationOptions = new MapAnimationOptions.Builder().duration(1500L).build();

        CameraAnimationsPlugin cameraAnimationsPlugin = CameraAnimationsUtils.getCamera(mapView);

        // Modifica o zoom na câmera automaticamente
        CameraOptions cameraOptions = (new CameraOptions.Builder())
                .center(Point.fromLngLat(location.getLongitude(),location.getLatitude()))
                .zoom(17.0)
                .padding(new EdgeInsets(500.0, 0.0, 0.0, 0.0))
                .build();

        cameraAnimationsPlugin.easeTo(cameraOptions, animationOptions);
    }



}