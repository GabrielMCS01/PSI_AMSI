package com.iuricarras.ciclodias_test_new;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.location.Location;
import android.os.Bundle;

import android.view.View;
import android.widget.Chronometer;
import android.widget.Toast;

import com.iuricarras.ciclodias_test_new.databinding.ActivityMainBinding;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.matching.v5.MapboxMapMatching;
import com.mapbox.api.matching.v5.models.MapMatchingResponse;
import com.mapbox.bindgen.Expected;
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
import com.mapbox.navigation.core.directions.session.RoutesObserver;
import com.mapbox.navigation.core.directions.session.RoutesUpdatedResult;
import com.mapbox.navigation.core.trip.session.LocationMatcherResult;
import com.mapbox.navigation.core.trip.session.LocationObserver;
import com.mapbox.navigation.ui.base.util.MapboxNavigationConsumer;
import com.mapbox.navigation.ui.maps.location.NavigationLocationProvider;
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi;
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView;
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineOptions;
import com.mapbox.navigation.ui.maps.route.line.model.RouteLine;
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineError;
import com.mapbox.navigation.ui.maps.route.line.model.RouteSetValue;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements PermissionsListener {
    // Permissões para aceder á localização do dispositivo
    private PermissionsManager permissionsManager;
    private MapboxMap mapboxMap;

    // Fazer ligação entre Navigation e Mapa
    private NavigationLocationProvider navigationLocationProvider = new NavigationLocationProvider();
    private MapboxNavigation mapboxNavigation;
    private ActivityMainBinding binding;
    private Chronometer chronometer;
    private List<Point> pointlist = new ArrayList<>();

    private MapboxMapMatching mapMatching;
    private DirectionsRoute directionsRoute;
    private MapboxRouteLineOptions routeLineOptions;
    private MapboxRouteLineApi routeLineApi;
    private MapboxRouteLineView routeLineView;
    private RoutesObserver routesObserver;
    private RouteLine routeLine;
    private List<RouteLine> routes = new ArrayList<>();

    // Variáveis para o cálculo da velocidade média
    private int count = 0;
    private float velocity = 0;
    private float mean = 0;

    // Variáveis para receberem a localização (anterior e atual)
    private Location loc1;
    private Location loc2;

    // Distância total percorrida na sessão de treino
    private float distance = 0;

    // Variável para verificar se existe algum valor na localização 1 (primeira vez que entra na função recebe)
    private boolean isLoc1 = false;

    // ------------------------------------------ Anotações ---------------------------------------------------
    // location.getSpeed (Biblioteca de Android não está relacionado com o mapbox)


    // --------------------------------------- Fim de Anotações -----------------------------------------------

    // Ouve alterações da localização
    private LocationObserver locationObs = new LocationObserver() {
        // Quando tem uma nova alteração na localização
        @Override
        public void onNewRawLocation(@NonNull Location location) {
            // Lista para os keyPoints (vazia) rotas
            List<Location> lista = new ArrayList<>();
            pointlist.add(Point.fromLngLat(location.getLongitude(), location.getLatitude()));
            navigationLocationProvider.changePosition(location, lista, null, null);
            // Função para atualizar a camera enviando a nova localização
            updateCamera(location);

            // Atualiza as funções de velocidade (Instântanea e média e a distância percorrida)
            // Envia a nova localização
            setVelocity(location);
            setVM(location);
            setDistance(location);

        }

        @Override
        public void onNewLocationMatcherResult(@NonNull LocationMatcherResult locationMatcherResult) {

        }
    };

    // Função para atualizar a câmera automaticamente consoante a localização
    private void updateCamera(Location location) {
        // Animações na câmera
        MapAnimationOptions animationOptions = new MapAnimationOptions.Builder().duration(1500L).build();

        CameraAnimationsPlugin cameraAnimationsPlugin = CameraAnimationsUtils.getCamera(binding.mapView);

        // Modifica o zoom na câmera automaticamente
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

        // binding do layout
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Recebe o mapa na view para escolher o tipo de mapa e carregar os estilos
        mapboxMap = binding.mapView.getMapboxMap();

        // Carrega o tipo de mapa "Outdoors"
        mapboxMap.loadStyleUri("mapbox://styles/mapbox/outdoors-v11", new Style.OnStyleLoaded() {
            // Carrega o estilo no mapa
            @Override
            public void onStyleLoaded(@NonNull Style style) {

                LocationComponentPlugin locationComponentPlugin = LocationComponentUtils.getLocationComponent(binding.mapView);

                //MapSDK  - LocationPlugin - NavigationProvider - MapBoxNavigation - NavigationSDK
                locationComponentPlugin.setLocationProvider(navigationLocationProvider);

                LocationPuck2D locationPuck2D = new LocationPuck2D();
                locationPuck2D.setBearingImage(ContextCompat.getDrawable(getApplicationContext(),R.drawable.mapbox_navigation_puck_icon));

                // Colocar no mapa
                locationComponentPlugin.setLocationPuck(locationPuck2D);

                locationComponentPlugin.setEnabled(true);

                // Pedir permissões ao utilizador e começar a navegação
                enableLocationComponent();
            }
        });

        binding.btGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mapMatching = MapboxMapMatching.builder().
                        accessToken(getString(R.string.mapbox_access_token)).
                        coordinates(pointlist).
                        steps(true).
                        profile(DirectionsCriteria.PROFILE_CYCLING).
                        build();

                mapMatching.enqueueCall(new Callback<MapMatchingResponse>() {
                    @Override
                    public void onResponse(Call<MapMatchingResponse> call, Response<MapMatchingResponse> response) {
                        directionsRoute = response.body().matchings().get(0).toDirectionRoute();
                        List<DirectionsRoute> list = new ArrayList<>();
                        list.add(directionsRoute);

                        for(DirectionsRoute directionsRoute : list){
                            System.out.println(directionsRoute);
                        }
                        mapboxNavigation.setRoutes(list);

                        routeLineOptions = new MapboxRouteLineOptions.Builder(getApplicationContext()).withRouteLineBelowLayerId("road-layer").build();
                        routeLineApi = new MapboxRouteLineApi(routeLineOptions);
                        routeLineView = new MapboxRouteLineView(routeLineOptions);

                        routeLine = new RouteLine(directionsRoute, null);

                        routesObserver = new RoutesObserver() {
                            @Override
                            public void onRoutesChanged(@NonNull RoutesUpdatedResult routesUpdatedResult) {
                                routes.add(routeLine);
                                routeLineApi.setRoutes(routes, (Expected<RouteLineError, RouteSetValue> routeLineErrorRouteSetValueExpected) -> {
                                    System.out.println("Hello");
                                    routeLineView.renderRouteDrawData(Objects.requireNonNull(mapboxMap.getStyle()), routeLineErrorRouteSetValueExpected);
                                    System.out.println("Hello");
                                });
                            }
                        };

                        mapboxNavigation.registerRoutesObserver(routesObserver);
                    }

                    @Override
                    public void onFailure(Call<MapMatchingResponse> call, Throwable t) {

                    }
                });

            }
        });

    }

    // Obtêm as permissões de localização
    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationComponent() {
        // Check if permissions are enabled and if not required
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            
            mapboxNavigation = new MapboxNavigation(new NavigationOptions.Builder(this).accessToken(getString(R.string.mapbox_access_token)).build());

            // Inicia a navegação
            mapboxNavigation.startTripSession();
            mapboxNavigation.registerLocationObserver(locationObs);

            // Recebe o cronometro e inicia-o
            chronometer = binding.timer;

            chronometer.start();
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    // Pede e atribui as permissões
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
        // Volta a colocar o estilo no mapa caso tenha as permissões
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent();
                }
            });
        // Caso não tenha permissões, mostra uma mensagem ao utilizador
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    // Função para calcular o valor da velocidade instântanea
    private void setVelocity(Location location){
        // Reset á velocidade instântanea
        float nCurrentSpeed = 0;

        // Recebe a velocidade e converte-a para kilometros/hora
        nCurrentSpeed = location.getSpeed() * 3.6F;

        Formatter fmt = new Formatter(new StringBuilder());
        fmt.format(Locale.US, "%5.2f", nCurrentSpeed);
        String strCurrentSpeed = fmt.toString();
        strCurrentSpeed = strCurrentSpeed.replace(' ', '0');

        // Unidade de medida
        String strUnits = "Km/h";

        // Atualiza na view a velocidade atual
        binding.txtCurrentSpeed.setText(strCurrentSpeed + " " + strUnits);
    }

    // Função para calcular o valor da velocidade média
    private void setVM(Location location){
        // recebe o valor da velocidade e converte para metros por segundo
        float speed = location.getSpeed() * 3.6F;

        // Adiciona +1 no contador para fazer o cálculo da velocidade média
        count++;

        // Velocidade total de todas as vezes que passou na função
        velocity = velocity + speed;

        // Velocidade média (velocidade total a dividir pelas vezes que passou na função)
        mean = velocity / count;

        // Formata os dados
        Formatter fmt = new Formatter(new StringBuilder());
        fmt.format(Locale.US, "%5.2f", mean);
        String strCurrentSpeed = fmt.toString();
        strCurrentSpeed = strCurrentSpeed.replace(' ', '0');

        // Unidade de medida (Pode ser )
        String strUnits = "Km/h";

        // Escreve o valor da velocidade no ecrâ em KM/H
        binding.tvVM.setText(strCurrentSpeed + " " + strUnits);

    }

    // Função para calcular a distância percorrida
    private void setDistance(Location location){
        // Caso seja o primeiro valor que recebe faz
        if(!isLoc1){
            //
            loc1 = location;
            isLoc1 = true;
        // Se já existir um valor anterior faz
        }else{
            // Recebe a localização atual e coloca na segunda variável
            loc2 = location;
            // Calcula a diferença entre as duas localizações (anterior e atual)
            float newDistance = loc1.distanceTo(loc2);
            // Atribui a nova localização á anterior para ser utilizada na proxima vez que se entrar na função
            loc1 = loc2;
            // Adiciona a distância entre as duas localizações ao contador de distância total
            distance = distance + newDistance;
        }

        // Formata os dados da distancia
        Formatter fmt = new Formatter(new StringBuilder());
        // 7 casas e 2 decimais
        fmt.format(Locale.US, "%7.2f", distance);
        String strCurrentSpeed = fmt.toString();
        strCurrentSpeed = strCurrentSpeed.replace(' ', '0');

        // Unidade de medida
        String strUnits = "m";

        // Atualiza na view o valor da distância
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