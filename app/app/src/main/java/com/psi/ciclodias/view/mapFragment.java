package com.psi.ciclodias.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
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
import com.mapbox.navigation.base.options.DeviceProfile;
import com.mapbox.navigation.base.options.DeviceType;
import com.mapbox.navigation.base.options.NavigationOptions;
import com.mapbox.navigation.core.MapboxNavigation;
import com.mapbox.navigation.core.trip.session.LocationMatcherResult;
import com.mapbox.navigation.core.trip.session.LocationObserver;
import com.mapbox.navigation.ui.maps.location.NavigationLocationProvider;
import com.psi.ciclodias.R;
import com.psi.ciclodias.databinding.ActivityInProgressTrainingBinding;
import com.psi.ciclodias.databinding.ActivityStartTrainingBinding;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

public class mapFragment extends Fragment implements PermissionsListener {

    //Gestor de Permissões
    private PermissionsManager permissionsManager;

    //Objetos necessarios para a gestão do mapa e da localização
    private MapView mapView;
    private MapboxMap mapboxMap;
    private NavigationLocationProvider navigationLocationProvider = new NavigationLocationProvider();
    private MapboxNavigation mapboxNavigation;


    public ActivityStartTrainingBinding startBinding = null;
    public ActivityInProgressTrainingBinding binding = null;
    // Variáveis para o cálculo da velocidade média
    private int count = 0;
    private float velocity = 0;
    private float mean = 0;

    // Variáveis para receberem a localização (anterior e atual)
    private Location loc1;
    private Location loc2;


    // Variável para verificar se existe algum valor na localização 1 (primeira vez que entra na função recebe)
    private boolean isLoc1 = false;

    public float velocityInstant = 0;
    public float distance = 0;
    public float velocityMean = 0;

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

            if(mapboxNavigation == null){
                mapboxNavigation = new MapboxNavigation(new NavigationOptions.Builder(getContext()).accessToken(getString(R.string.mapbox_access_token))
                        .deviceProfile(new DeviceProfile.Builder().deviceType(DeviceType.HANDHELD).build()).build());

            }
            // Inicia a navegação
            if(!mapboxNavigation.isRunningForegroundService()){
                mapboxNavigation.startTripSession();
                mapboxNavigation.registerLocationObserver(locationObs);
            }
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
            List<Location> list = new ArrayList<>();
            navigationLocationProvider.changePosition(location, list, null, null);
            // Função para atualizar a camera enviando a nova localização
            updateCamera(location);

            if(startBinding != null){
                startBinding.textView3.setText(R.string.txtGPSAdquirido);
                startBinding.btComecarTreino.setEnabled(true);
                startBinding = null;
            }
            // Atualiza as funções de velocidade (Instântanea e média e a distância percorrida)
            // Envia a nova localização
            if(binding != null) {
                setVelocity(location);
                setVM(location);
                setDistance(location);
            }
        }

        @Override
        public void onNewLocationMatcherResult(@NonNull LocationMatcherResult locationMatcherResult) {


        }
    };


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

    //Função para destruir as variaveis do mapa a pedido do código
    public void onMyDestroy(){
            super.onDestroy();
            mapboxNavigation.stopTripSession();
            mapboxNavigation.unregisterLocationObserver(locationObs);
            binding = null;
    }

    // Função para calcular o valor da velocidade instântanea
    private void setVelocity(Location location){
        // Reset á velocidade instântanea
        float nCurrentSpeed = 0;

        // Recebe a velocidade e converte-a para kilometros/hora
        nCurrentSpeed = location.getSpeed() * 3.6F;

        velocityInstant = nCurrentSpeed;


        Formatter fmt = new Formatter(new StringBuilder());
        fmt.format(Locale.US, "%5.2f", nCurrentSpeed);
        String strCurrentSpeed = fmt.toString();
        strCurrentSpeed = strCurrentSpeed.replace(' ', '0');

        // Unidade de medida
        String strUnits = "Km/h";

        // Atualiza na view a velocidade atual
        binding.tvVelInstantaneaTreino.setText(strCurrentSpeed + " " + strUnits);
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

        velocityMean = mean;


        // Formata os dados
        Formatter fmt = new Formatter(new StringBuilder());
        fmt.format(Locale.US, "%5.2f", mean);
        String strCurrentSpeed = fmt.toString();
        strCurrentSpeed = strCurrentSpeed.replace(' ', '0');

        // Unidade de medida (Pode ser )
        String strUnits = "Km/h";

        // Escreve o valor da velocidade no ecrâ em KM/H
        binding.tvVelMediaTreino.setText(strCurrentSpeed + " " + strUnits);
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
        binding.tvDistanciaTreino.setText(strCurrentSpeed + " " + strUnits);


    }

}