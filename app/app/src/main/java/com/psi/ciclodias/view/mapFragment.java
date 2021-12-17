package com.psi.ciclodias.view;

import android.annotation.SuppressLint;

import android.icu.text.Transliterator;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.matching.v5.MapboxMapMatching;
import com.mapbox.api.matching.v5.models.MapMatchingResponse;
import com.mapbox.bindgen.Expected;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.utils.PolylineUtils;
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
import com.mapbox.navigation.core.directions.session.RoutesObserver;
import com.mapbox.navigation.core.directions.session.RoutesUpdatedResult;
import com.mapbox.navigation.core.trip.session.LocationMatcherResult;
import com.mapbox.navigation.core.trip.session.LocationObserver;
import com.mapbox.navigation.ui.maps.location.NavigationLocationProvider;
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi;
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView;
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineOptions;
import com.mapbox.navigation.ui.maps.route.line.model.RouteLine;
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineError;
import com.mapbox.navigation.ui.maps.route.line.model.RouteSetValue;
import com.psi.ciclodias.R;
import com.psi.ciclodias.databinding.ActivityInProgressTrainingBinding;
import com.psi.ciclodias.databinding.ActivityInProgressTrainingMapBinding;
import com.psi.ciclodias.databinding.ActivityPausedTrainingBinding;
import com.psi.ciclodias.databinding.ActivityResultsTrainingBinding;
import com.psi.ciclodias.databinding.ActivityStartTrainingBinding;
import com.psi.ciclodias.model.Chronometer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class mapFragment extends Fragment implements PermissionsListener {

    //Gestor de Permissões

    private PermissionsManager permissionsManager;
    private String accessToken;

    //Objetos necessarios para a gestão do mapa e da localização
    private MapView mapView;
    private MapboxMap mapboxMap;
    private NavigationLocationProvider navigationLocationProvider = new NavigationLocationProvider();
    private MapboxNavigation mapboxNavigation;

    //Objetos Bindings das Activities que necessitam dos dados do mapa
    public ActivityStartTrainingBinding startBinding = null;
    public ActivityInProgressTrainingBinding trainingBinding = null;
    public ActivityInProgressTrainingMapBinding mapBinding = null;
    public ActivityPausedTrainingBinding pausedBinding = null;
    private boolean isFinished = false;

    //Cronometro
    private boolean startTimer = true;
    public boolean resumeTimer = false;
    private Chronometer chronometer;


    // Variáveis para o cálculo da velocidade média
    private int count = 0;
    private float velocity = 0;
    private float mean = 0;

    // Variáveis para receberem a localização (anterior e atual)
    private Location loc1;
    private Location loc2;


    // Variável para verificar se existe algum valor na localização 1 (primeira vez que entra na função recebe)
    private boolean isLoc1 = false;

    //Variáveis que guardam os dados da sessão de treino
    public boolean isRunning = false;
    public float velocityInstant = 0;
    public float distance = 0;
    public float velocityMean = 0;
    public float velocityMax = 0;
    public int time = 0;


    private MapboxMapMatching mapMatching;
    private DirectionsRoute directionsRoute;
    private MapboxRouteLineOptions routeLineOptions;
    private MapboxRouteLineApi routeLineApi;
    private MapboxRouteLineView routeLineView;
    private RoutesObserver routesObserver;
    private RouteLine routeLine;
    private List<RouteLine> routes = new ArrayList<>();
    public ArrayList<Point> pointsList = new ArrayList<>();
    private ArrayList<DirectionsRoute> listDirections = new ArrayList<>();


    private mapFragment() {
        // Required empty public constructor
    }

    //Codigo responsavel por criar/devolver a instancia do fragmento
    private static mapFragment instancia = null;

    public static synchronized mapFragment getInstancia() {
        if (instancia == null) {
            instancia = new mapFragment();
        }
        return instancia;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    //Função responsavel por criar a view do fragmento e carregar o mapa
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        //Buscar o mapa ao fragmento
        mapView = view.findViewById(R.id.mapView);
        mapboxMap = mapView.getMapboxMap();

        if (startBinding != null) {
            isFinished = false;

        }

        //Responsavel por dar um estilo ao mapa e criar o puck de localização
        loadMap();

        return view;
    }

    //Responsavel por dar um estilo ao mapa e criar o puck de localização
    private void loadMap() {

        //Carregar o estilo no mapa
        mapboxMap.loadStyleUri("mapbox://styles/mapbox/outdoors-v11", new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {

                if (!isFinished) {
                    LocationComponentPlugin locationComponentPlugin = LocationComponentUtils.getLocationComponent(mapView);

                    //MapSDK  - LocationPlugin - NavigationProvider - MapBoxNavigation - NavigationSDK
                    locationComponentPlugin.setLocationProvider(navigationLocationProvider);


                    //Carregar o puck de localização
                    LocationPuck2D locationPuck2D = new LocationPuck2D();
                    locationPuck2D.setBearingImage(ContextCompat.getDrawable(getContext(), R.drawable.mapbox_navigation_puck_icon));

                    // Colocar o puck no mapa
                    locationComponentPlugin.setLocationPuck(locationPuck2D);


                    locationComponentPlugin.setEnabled(true);
                }
                // Verificar as permissões de localização e começar a navegação
                startNavigation();

            }
        });
    }

    // Verificar as permissões de localização e começar a navegação
    @SuppressLint("MissingPermission")
    private void startNavigation() {
        if (PermissionsManager.areLocationPermissionsGranted(getContext())) {

            if (mapboxNavigation == null) {
                mapboxNavigation = new MapboxNavigation(new NavigationOptions.Builder(getContext()).accessToken(getString(R.string.mapbox_access_token))
                        .deviceProfile(new DeviceProfile.Builder().deviceType(DeviceType.HANDHELD).build()).build());
                accessToken = getString(R.string.mapbox_access_token);
            }

            // Inicia a navegação
            if (!mapboxNavigation.isRunningForegroundService() && !isFinished) {
                mapboxNavigation.startTripSession();
                mapboxNavigation.registerLocationObserver(locationObs);
                mapboxNavigation.setRoutes(new ArrayList<DirectionsRoute>());
            } else if (isFinished) {
                if (pointsList.size() > 2) {
                    MapboxMapMatching mapMatchingTwo = MapboxMapMatching.builder().
                            accessToken(getString(R.string.mapbox_access_token)).
                            coordinates(pointsList).
                            steps(true).
                            profile(DirectionsCriteria.PROFILE_WALKING).
                            build();

                    pointsList = new ArrayList<>();
                    mapMatchingTwo.enqueueCall(new Callback<MapMatchingResponse>() {
                        @Override
                        public void onResponse(Call<MapMatchingResponse> call, Response<MapMatchingResponse> response) {
                            directionsRoute = response.body().matchings().get(0).toDirectionRoute();
                            listDirections.add(directionsRoute);

                            ArrayList<Point> resultGeometry = new ArrayList<>();
                            for (DirectionsRoute directions : listDirections) {

                                // Convert the polyline string into a list of Position objects
                                List<Point> routePoints = PolylineUtils.decode(directions.geometry(), 6);

                                resultGeometry.addAll(routePoints);
                            }

                            // Generate a polyline encoded string from the accumulated points.
                            String resultGeometryString = PolylineUtils.encode(resultGeometry, 6);


                            System.out.println(resultGeometryString);
                            System.out.println(resultGeometryString.length());

                            ArrayList<DirectionsRoute> list = new ArrayList<>();
                            list.add(DirectionsRoute.builder().geometry(resultGeometryString).duration(0.0).distance(0.0).build());

                            mapboxNavigation.setRoutes(list);

                            routeLineOptions = new MapboxRouteLineOptions.Builder(getContext()).withRouteLineBelowLayerId("road-label").build();
                            routeLineApi = new MapboxRouteLineApi(routeLineOptions);
                            routeLineView = new MapboxRouteLineView(routeLineOptions);

                            routeLine = new RouteLine(list.get(0), null);

                            routesObserver = new RoutesObserver() {
                                @Override
                                public void onRoutesChanged(@NonNull RoutesUpdatedResult routesUpdatedResult) {
                                    routes.add(routeLine);
                                    routeLineApi.setRoutes(routes, (Expected<RouteLineError, RouteSetValue> routeLineErrorRouteSetValueExpected) -> {
                                        System.out.println("Hello");
                                        routeLineView.renderRouteDrawData(Objects.requireNonNull(mapboxMap.getStyle()), routeLineErrorRouteSetValueExpected);
                                        System.out.println("Hello");
                                        MapAnimationOptions animationOptions = new MapAnimationOptions.Builder().duration(1500L).build();

                                        CameraAnimationsPlugin cameraAnimationsPlugin = CameraAnimationsUtils.getCamera(mapView);

                                        // Modifica o zoom na câmera automaticamente
                                        CameraOptions cameraOptions = (new CameraOptions.Builder())
                                                .center(Point.fromLngLat(loc1.getLongitude(), loc1.getLatitude()))
                                                .zoom(15.0)
                                                .padding(new EdgeInsets(500.0, 0.0, 0.0, 0.0))
                                                .build();

                                        cameraAnimationsPlugin.easeTo(cameraOptions, animationOptions);
                                    });
                                }
                            };

                            mapboxNavigation.registerRoutesObserver(routesObserver);

                        }

                        @Override
                        public void onFailure(Call<MapMatchingResponse> call, Throwable t) {

                        }
                    });

                } else {
                    ArrayList<Point> resultGeometry = new ArrayList<>();
                    for (DirectionsRoute directions : listDirections) {

                        // Convert the polyline string into a list of Position objects
                        List<Point> routePoints = PolylineUtils.decode(directions.geometry(), 6);

                        // Concatenate the route points, removing the first point if we're appendin
                        resultGeometry.addAll(routePoints);

                    }

                    // Generate a polyline encoded string from the accumulated points.
                    String resultGeometryString = PolylineUtils.encode(resultGeometry, 6);


                    ArrayList<DirectionsRoute> list = new ArrayList<>();
                    list.add(DirectionsRoute.builder().geometry(resultGeometryString).duration(0.0).distance(0.0).build());

                    mapboxNavigation.setRoutes(list);

                    routeLineOptions = new MapboxRouteLineOptions.Builder(getContext()).withRouteLineBelowLayerId("road-label").build();
                    routeLineApi = new MapboxRouteLineApi(routeLineOptions);
                    routeLineView = new MapboxRouteLineView(routeLineOptions);

                    routeLine = new RouteLine(list.get(0), null);

                    routesObserver = new RoutesObserver() {
                        @Override
                        public void onRoutesChanged(@NonNull RoutesUpdatedResult routesUpdatedResult) {
                            routes.add(routeLine);
                            routeLineApi.setRoutes(routes, (Expected<RouteLineError, RouteSetValue> routeLineErrorRouteSetValueExpected) -> {
                                System.out.println("Hello");
                                routeLineView.renderRouteDrawData(Objects.requireNonNull(mapboxMap.getStyle()), routeLineErrorRouteSetValueExpected);
                                System.out.println("Hello");

                                MapAnimationOptions animationOptions = new MapAnimationOptions.Builder().duration(1500L).build();

                                CameraAnimationsPlugin cameraAnimationsPlugin = CameraAnimationsUtils.getCamera(mapView);

                                // Modifica o zoom na câmera automaticamente
                                CameraOptions cameraOptions = (new CameraOptions.Builder())
                                        .center(Point.fromLngLat(loc1.getLongitude(), loc1.getLatitude()))
                                        .zoom(15.0)
                                        .padding(new EdgeInsets(500.0, 0.0, 0.0, 0.0))
                                        .build();

                                cameraAnimationsPlugin.easeTo(cameraOptions, animationOptions);
                            });
                        }
                    };

                    mapboxNavigation.registerRoutesObserver(routesObserver);

                }
            }
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(getActivity());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        startNavigation();
    }

    @Override
    public void onExplanationNeeded(List<String> list) {
        Toast.makeText(getContext(), R.string.txtPermissaoExplicacao, Toast.LENGTH_LONG).show();
    }


    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            loadMap();
        } else {
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
            //Altera a textview txtGPSAdquirido e o botão btComecarTreino no StartTrainingActivity
            if (startBinding != null) {
                startBinding.textView3.setText(R.string.txtGPSAdquirido);
                startBinding.btComecarTreino.setEnabled(true);
                startBinding = null;
            } else {
                Point point = Point.fromLngLat(location.getLongitude(), location.getLatitude());
                pointsList.add(point);

                if (pointsList.size() == 95) {
                    mapMatching = MapboxMapMatching.builder().
                            accessToken(accessToken).
                            coordinates(pointsList).
                            steps(true).
                            profile(DirectionsCriteria.PROFILE_WALKING).
                            build();

                    pointsList = new ArrayList<>();
                    mapMatching.enqueueCall(new Callback<MapMatchingResponse>() {
                        @Override
                        public void onResponse(Call<MapMatchingResponse> call, Response<MapMatchingResponse> response) {
                            directionsRoute = response.body().matchings().get(0).toDirectionRoute();
                            listDirections.add(directionsRoute);
                        }

                        @Override
                        public void onFailure(Call<MapMatchingResponse> call, Throwable t) {

                        }
                    });

                }


                // Atualiza as funções de velocidade instântanea e média e a distância percorrida)
                // No InProgressTrainingActivity
                if (trainingBinding != null) {
                    isRunning = true;
                    Chronometer.getInstancia().trainingBinding = trainingBinding;
                    if (startTimer) {
                        chronometer = Chronometer.getInstancia();
                        chronometer.start();
                        startTimer = false;
                    }
                    if (resumeTimer) {
                        chronometer.stopVariable = false;
                        resumeTimer = false;
                    }
                    setVelocity(location);
                    setVM(location);
                    setDistance(location);
                    // No InProgressTrainingMapActivity
                } else if (mapBinding != null) {
                    isRunning = true;
                    Chronometer.getInstancia().mapBinding = mapBinding;
                    setVelocity(location);
                    setVM(location);
                    setDistance(location);
                }
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
                .center(Point.fromLngLat(location.getLongitude(), location.getLatitude()))
                .zoom(17.0)
                .padding(new EdgeInsets(500.0, 0.0, 0.0, 0.0))
                .build();

        cameraAnimationsPlugin.easeTo(cameraOptions, animationOptions);
    }

    //Função para destruir as variaveis do mapa a pedido do código
    public void onMyDestroy() {
        super.onDestroy();
        isRunning = false;
        mapboxNavigation.stopTripSession();
        mapboxNavigation.unregisterLocationObserver(locationObs);
        trainingBinding = null;
        pausedBinding = null;
        mapBinding = null;
        if (chronometer != null) {
            chronometer.stop = true;
            time = chronometer.getTime();
        }
    }

    // Função para calcular o valor da velocidade instântanea
    private void setVelocity(Location location) {
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

        setMaxVelocity(nCurrentSpeed);

        // Atualiza na view a velocidade atual
        if (trainingBinding != null) {
            trainingBinding.tvVelInstantaneaTreino.setText(strCurrentSpeed + " " + strUnits);
        } else if (mapBinding != null) {
            mapBinding.tvVelMax.setText("Velocidade Instantanea:\n" + strCurrentSpeed + " " + strUnits);
        }
    }

    private void setMaxVelocity(float nCurrentSpeed) {
        if (nCurrentSpeed > velocityMax) {
            velocityMax = nCurrentSpeed;
        }
    }

    // Função para calcular o valor da velocidade média
    private void setVM(Location location) {
        // recebe o valor da velocidade e converte para metros por segundo
        float currentSpeed = location.getSpeed() * 3.6F;

        // Adiciona +1 no contador para fazer o cálculo da velocidade média
        count++;

        // Velocidade total de todas as vezes que passou na função
        velocity = velocity + currentSpeed;

        // Velocidade média (velocidade total a dividir pelas vezes que passou na função)
        mean = velocity / count;

        velocityMean = mean;

        // Formata os dados
        Formatter fmt = new Formatter(new StringBuilder());
        fmt.format(Locale.US, "%5.2f", mean);
        String strMeanVelocity = fmt.toString();
        strMeanVelocity = strMeanVelocity.replace(' ', '0');

        // Unidade de medida
        String strUnits = "Km/h";

        // Escreve o valor da velocidade no ecrâ em KM/H
        if (trainingBinding != null) {
            trainingBinding.tvVelMediaTreino.setText(strMeanVelocity + " " + strUnits);
        } else if (mapBinding != null) {
            mapBinding.tvVelMedia.setText("Velocidade Média:\n" + strMeanVelocity + " " + strUnits);
        }
    }

    // Função para calcular a distância percorrida
    private void setDistance(Location location) {
        // Caso seja o primeiro valor que recebe faz
        if (!isLoc1) {
            //
            loc1 = location;
            isLoc1 = true;
            // Se já existir um valor anterior faz
        } else {
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
        String strDistance = fmt.toString();
        strDistance = strDistance.replace(' ', '0');

        // Unidade de medida
        String strUnits = "m";

        // Atualiza na view o valor da distância
        if (trainingBinding != null) {
            trainingBinding.tvDistanciaTreino.setText(strDistance + " " + strUnits);
        } else if (mapBinding != null) {
            mapBinding.tvDistancia.setText("Distancia:\n" + strDistance + " " + strUnits);
        }


    }

    //Mostra os resultados do treino no ResultsTrainingActivity
    public void getResults(ActivityResultsTrainingBinding binding) {
        binding.tvVelMaxResumo.setText("Velocidade Máxima:\n" + velocityMax + " Km/h");
        binding.tvVelMediaResumo.setText("Velocidade Média:\n" + velocityMean + "Km/h");
        binding.tvDistanciaResumo.setText("Distancia:\n" + distance + "m");
        binding.tvTempoResumo.setText("Tempo: \n" + time + "s");
        isFinished = true;
    }


    public void setData() {
        if (trainingBinding != null) {
            trainingBinding.tvDuracaoTreino.setText(time + "s");
            trainingBinding.tvVelMediaTreino.setText(velocityMean + "Km/h");
            trainingBinding.tvDistanciaTreino.setText(distance + "m");
            trainingBinding.tvVelInstantaneaTreino.setText(velocityInstant + " Km/h");
        } else if (mapBinding != null) {
            mapBinding.tvTempo.setText("Tempo:\n" + time + "s");
            mapBinding.tvVelMax.setText("Velocidade Instantanea:\n" + velocityInstant + " Km/h");
            mapBinding.tvVelMedia.setText("Velocidade Média:\n" + velocityMean + "Km/h");
            mapBinding.tvDistancia.setText("Distancia:\n" + distance + "m");
        } else if (pausedBinding != null) {
            pausedBinding.tvDistanciaPausa.setText("Distancia:\n" + distance + "m");
            pausedBinding.tvVelMaxPausa.setText("Velocidade Máxima:\n" + velocityMax + " Km/h");
            pausedBinding.tvVelMediaPausa.setText("Velocidade Média:\n" + velocityMean + "Km/h");
            pausedBinding.tvTempoPausa.setText("Tempo:\n" + time + "s");
        }
    }

}