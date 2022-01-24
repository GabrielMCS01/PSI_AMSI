package com.psi.ciclodias.view;

import android.annotation.SuppressLint;

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
import com.psi.ciclodias.listeners.RotaListener;
import com.psi.ciclodias.model.Chronometer;
import com.psi.ciclodias.utils.CiclismoJsonParser;
import com.psi.ciclodias.utils.Converter;

import java.util.ArrayList;
import java.util.List;
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
    public MapboxNavigation mapboxNavigation;


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

    public Location actualLocation = null;

    // Variável para verificar se existe algum valor na localização 1 (primeira vez que entra na função recebe)
    private boolean isLoc1 = false;

    //Variáveis que guardam os dados da sessão de treino
    public boolean isRunning = false;
    public float velocityInstant = 0;
    public float distance = 0;
    public float velocityMean = 0;
    public float velocityMax = 0;
    public int time = 0;
    public String routeString = null;
    public ArrayList<Float> arrayVelocity = new ArrayList<>();

    // Variáveis para guardar a rota, os pontos de localização
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

    private RotaListener rotaListener = null;
    public boolean isDetails = false;


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
                // Se o treino ainda não foi terminado carrega os estilos do mapa
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
    public void startNavigation() {
        // Cria uma nova navegação caso não exista
        if (mapboxNavigation == null) {
            mapboxNavigation = new MapboxNavigation(new NavigationOptions.Builder(getContext()).accessToken(getString(R.string.mapbox_access_token))
                    .deviceProfile(new DeviceProfile.Builder().deviceType(DeviceType.HANDHELD).build()).build());
            accessToken = getString(R.string.mapbox_access_token);
        }

        // Verifica se estamos a ver os detalhes de um treino
        if (isDetails) {
            rotaListener.setRoute();
            isDetails = false;
        }

        // Inicia a navegação
        if (!mapboxNavigation.isRunningForegroundService() && !isFinished) {
            mapboxNavigation.startTripSession();
            mapboxNavigation.registerLocationObserver(locationObs);
            mapboxNavigation.setRoutes(new ArrayList<DirectionsRoute>());
        }
        // Se acabou o treino
        else if (isFinished) {
            // Se existirem mais de 2 pontos de localização e se o utilizador tiver conexão á internet
            if (pointsList.size() > 2 && CiclismoJsonParser.isInternetConnection(getActivity().getApplicationContext())) {
                MapboxMapMatching mapMatchingTwo = MapboxMapMatching.builder().
                        accessToken(getString(R.string.mapbox_access_token)).
                        coordinates(pointsList).
                        steps(true).
                        profile(DirectionsCriteria.PROFILE_WALKING).
                        build();

                // Cria uma nova lista de pontos
                pointsList = new ArrayList<>();

                // Resposta da API com a rota dos pontos dados
                mapMatchingTwo.enqueueCall(new Callback<MapMatchingResponse>() {
                    @Override
                    public void onResponse(Call<MapMatchingResponse> call, Response<MapMatchingResponse> response) {
                        directionsRoute = response.body().matchings().get(0).toDirectionRoute();

                        // Adiciona a rota á lista de rotas
                        listDirections.add(directionsRoute);

                        ArrayList<Point> resultGeometry = new ArrayList<>();

                        // Para cada rota na lista de rotas converte a rota numa lista de points
                        for (DirectionsRoute directions : listDirections) {

                            // Converte a string de polyline em uma lista de pontos
                            List<Point> routePoints = PolylineUtils.decode(directions.geometry(), 6);

                            resultGeometry.addAll(routePoints);
                        }

                        // Gera uma string polyline encriptada de todos os pontos
                        String resultGeometryString = PolylineUtils.encode(resultGeometry, 6);

                        routeString = resultGeometryString;

                        // Limpa a variável com todas as rotas do treino
                        listDirections = new ArrayList<>();

                        ArrayList<DirectionsRoute> list = new ArrayList<>();
                        list.add(DirectionsRoute.builder().geometry(resultGeometryString).duration(0.0).distance(0.0).build());

                        mapboxNavigation.setRoutes(new ArrayList<>());
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
                                    routeLineView.renderRouteDrawData(Objects.requireNonNull(mapboxMap.getStyle()), routeLineErrorRouteSetValueExpected);
                                    MapAnimationOptions animationOptions = new MapAnimationOptions.Builder().duration(1500L).build();

                                    routes = new ArrayList<>();
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
            }
            // Faz o mesmo que em cima expecto que não faz pedido á API da ultima rota
            else {
                pointsList = new ArrayList<>();
                ArrayList<Point> resultGeometry = new ArrayList<>();
                for (DirectionsRoute directions : listDirections) {

                    // Convert the polyline string into a list of Position objects
                    List<Point> routePoints = PolylineUtils.decode(directions.geometry(), 6);

                    // Concatenate the route points, removing the first point if we're appendin
                    resultGeometry.addAll(routePoints);

                }

                // Generate a polyline encoded string from the accumulated points.
                String resultGeometryString = PolylineUtils.encode(resultGeometry, 6);

                listDirections = new ArrayList<>();

                routeString = resultGeometryString;

                ArrayList<DirectionsRoute> list = new ArrayList<>();
                list.add(DirectionsRoute.builder().geometry(resultGeometryString).duration(0.0).distance(0.0).build());

                mapboxNavigation.setRoutes(new ArrayList<>());
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
                            routeLineView.renderRouteDrawData(Objects.requireNonNull(mapboxMap.getStyle()), routeLineErrorRouteSetValueExpected);

                            routes = new ArrayList<>();
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
    }

    // ------------------------------------ Não é utilizado --------------------------------------------------------------
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
        startNavigation();
    }
    // -------------------------------------------------------------------------------------------------------------------

    // Devolve um Toast de que a aplicação necessita de acesso á localização para poder fazer treinos
    @Override
    public void onExplanationNeeded(List<String> list) {
        Toast.makeText(getContext(), R.string.txtPermissaoExplicacao, Toast.LENGTH_LONG).show();
    }

    // Resposta ao devolver a permissão
    @Override
    public void onPermissionResult(boolean granted) {
        // Caso tenha a permissão, carrega o mapa
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

            actualLocation = location;

            // Se o utilizador se escontrar na activity de iniciar o treino
            if (startBinding != null) {
                // atualiza a câmera enviando a nova localização
                updateCamera(location);

                // Altera a textview txtGPSAdquirido e o botão btComecarTreino no StartTrainingActivity
                startBinding.textView3.setText(R.string.txtGPSAdquirido);
                startBinding.btComecarTreino.setEnabled(true);
            }
            // Se o utilizador encontrar-se em outra activity de treino sem ser a de iniciar o treino
            else {
                // Recebe o valor da logintude e latidude do ponto atual da localização
                Point point = Point.fromLngLat(location.getLongitude(), location.getLatitude());

                // Adiciona o ponto á lista de pontos, para depois ser desenhada a rota
                pointsList.add(point);

                // Limite de 100 pontos de localização na lista
                if (pointsList.size() == 95) {
                        // Envia os pontos para a API dar uma rota
                        mapMatching = MapboxMapMatching.builder().
                                accessToken(accessToken).
                                coordinates(pointsList).
                                steps(true).
                                profile(DirectionsCriteria.PROFILE_WALKING).
                                build();

                        // Apaga os pontos e cria uma nova lista vazia
                        pointsList = new ArrayList<>();

                        // Resposta da API com a rota dos pontos dados
                        mapMatching.enqueueCall(new Callback<MapMatchingResponse>() {
                            @Override
                            public void onResponse(Call<MapMatchingResponse> call, Response<MapMatchingResponse> response) {
                                directionsRoute = response.body().matchings().get(0).toDirectionRoute();

                                // Adiciona a rota á lista de rotas
                                listDirections.add(directionsRoute);
                            }

                            @Override
                            public void onFailure(Call<MapMatchingResponse> call, Throwable t) {

                            }
                        });
                }
                // Se o utilizador se encontra na activity inicial do treino
                if (trainingBinding != null) {
                    // Atualiza o valor do tempo do treino
                    isRunning = true;
                    Chronometer.getInstancia(false).trainingBinding = trainingBinding;

                    // Inicia um novo cronometro caso não exista um
                    if (startTimer) {
                        chronometer = Chronometer.getInstancia(true);
                        Chronometer.getInstancia(false).trainingBinding = trainingBinding;
                        chronometer.start();
                        startTimer = false;
                    }
                    // Inicia novamente o cronômetro
                    if (resumeTimer) {
                        chronometer.stopVariable = false;
                        resumeTimer = false;
                    }

                    // Atualiza a câmera, velocidade, velocidade média e a distância
                    updateCamera(location);
                    setVelocity(location);
                    setVM(location);
                    setDistance(location);
                }
                // No InProgressTrainingMapActivity
                else if (mapBinding != null) {
                    // Atualiza os dados do treino
                    updateCamera(location);
                    isRunning = true;
                    Chronometer.getInstancia(false).mapBinding = mapBinding;
                    setVelocity(location);
                    setVM(location);
                    setDistance(location);
                }
                // Se o utilizador estiver na activity de pausa, atualiza apenas a câmera
                else if (pausedBinding != null) {
                    updateCamera(location);
                }
            }
        }

        @Override
        public void onNewLocationMatcherResult(@NonNull LocationMatcherResult locationMatcherResult) {
        }
    };

    // Atualiza a câmera para a localização atual
    public void updateCamera(Location location) {
        // Animações na câmera
        if (location != null) {
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
    }

    // Função para destruir as variaveis do mapa a pedido do código
    public void onMyDestroy() {
        super.onDestroy();
        isRunning = false;
        mapboxNavigation.resetTripSession();
        mapboxNavigation.stopTripSession();
        mapboxNavigation.setRoutes(new ArrayList<>());
        if (routesObserver != null) {
            mapboxNavigation.unregisterRoutesObserver(routesObserver);
        }
        mapboxNavigation.unregisterLocationObserver(locationObs);
        startBinding = null;
        trainingBinding = null;
        pausedBinding = null;
        mapBinding = null;
        // Se existir algum cronometro a correr
        if (chronometer != null) {
            startTimer = true;
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

        arrayVelocity.add((float) Math.round(velocityInstant * 100) / 100);

        setMaxVelocity(nCurrentSpeed);

        // Atualiza a velocidade atual na página principal do treino
        if (trainingBinding != null) {
            trainingBinding.tvVelInstantaneaTreino.setText(Converter.velocityFormat(velocityInstant));
        }
        // Atualiza a velocidade atual na view do mapa durante o treino
        else if (mapBinding != null) {
            mapBinding.tvVelInstantanea.setText(Converter.velocityFormat(velocityInstant));
        }
    }

    // Substitui o valor da velocidade máxima anterior com o da nova
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

        // Atualiza a velocidade média na página principal do treino
        if (trainingBinding != null) {
            trainingBinding.tvVelMediaTreino.setText(Converter.velocityFormat(mean));
        }
        // Atualiza a velocidade média na view do mapa durante o treino
        else if (mapBinding != null) {
            mapBinding.tvVelMedia.setText(Converter.velocityFormat(mean));
        }
    }

    // Função para calcular a distância percorrida
    private void setDistance(Location location) {
        // Caso seja o primeiro valor que recebe faz
        if (!isLoc1) {
            // Recebe a localização
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

        // Atualiza a distância na página principal do treino
        if (trainingBinding != null) {
            trainingBinding.tvDistanciaTreino.setText(Converter.distanceFormat(distance));
        }
        // Atualiza a distância na view do mapa durante o treino
        else if (mapBinding != null) {
            mapBinding.tvDistancia.setText(Converter.distanceFormat(distance));
        }
    }

    // Mostra os resultados do treino no ResultsTrainingActivity
    public void getResults(ActivityResultsTrainingBinding binding) {
        binding.tvVelMaxResumo.setText(Converter.velocityFormat(velocityMax));
        binding.tvVelMediaResumo.setText(Converter.velocityFormat(velocityMean));
        binding.tvDistanciaResumo.setText(Converter.distanceFormat(distance));
        binding.tvTempoResumo.setText(Converter.hourFormat(time));
        // Indica que o treino foi terminado
        isFinished = true;
    }

    // Atribui os dados na devida activity cada vez que se entra nela
    public void setData() {
        // Insere os dados da atividade na página principal do treino
        if (trainingBinding != null) {
            trainingBinding.tvDuracaoTreino.setText(Converter.hourFormat(time));
            trainingBinding.tvVelMediaTreino.setText(Converter.velocityFormat(velocityMean));
            trainingBinding.tvDistanciaTreino.setText(Converter.distanceFormat(distance));
            trainingBinding.tvVelInstantaneaTreino.setText(Converter.velocityFormat(velocityInstant));
        }
        // Insere os dados da atividade na view do mapa do treino
        else if (mapBinding != null) {
            mapBinding.tvTempo.setText(Converter.hourFormat(time));
            mapBinding.tvVelInstantanea.setText(Converter.velocityFormat(velocityInstant));
            mapBinding.tvVelMedia.setText(Converter.velocityFormat(velocityMean));
            mapBinding.tvDistancia.setText(Converter.distanceFormat(distance));
        }
        // Insere os dados da atividade na página de pausa do treino
        else if (pausedBinding != null) {
            pausedBinding.tvDistanciaPausa.setText(Converter.distanceFormat(distance));
            pausedBinding.tvVelMaxPausa.setText(Converter.velocityFormat(velocityMax));
            pausedBinding.tvVelMediaPausa.setText(Converter.velocityFormat(velocityMean));
            pausedBinding.tvTempoPausa.setText(Converter.hourFormat(time));
        }
    }

    private RouteLine routeLineDetails;

    // Coloca a informação dentro das classes necessárias para desenhar a rota
    public void setRoute(String route, Context context) {
        // Se existir uma rota
        if (!route.equals("null")) {
            ArrayList<DirectionsRoute> list = new ArrayList<>();

            // Gera uma directions route, precisamos colocar valor inicial
            list.add(DirectionsRoute.builder().geometry(route).duration(0.0).distance(0.0).build());

            List<Point> resultGeometry = new ArrayList<>();
            resultGeometry = PolylineUtils.decode(route, 6);

            // Ponto central do treino para poder dar o zoom
            int resultGeometryIndex = resultGeometry.size() / 2;

            // Desenha a rota com os pontos da lista
            mapboxNavigation.setRoutes(new ArrayList<>());
            mapboxNavigation.setRoutes(list);

            // Opções a desenhar a rota
            routeLineOptions = new MapboxRouteLineOptions.Builder(context).withRouteLineBelowLayerId("road-label").build();
            routeLineApi = new MapboxRouteLineApi(routeLineOptions);
            routeLineView = new MapboxRouteLineView(routeLineOptions);

            routeLineDetails = new RouteLine(list.get(0), null);

            list = null;

            List<Point> finalResultGeometry = resultGeometry;
            routesObserver = new RoutesObserver() {
                @Override
                public void onRoutesChanged(@NonNull RoutesUpdatedResult routesUpdatedResult) {
                    // IF devido a um problema que fazia no mapa aparecer várias rotas
                    if (routeLineDetails != null) {
                        ArrayList<RouteLine> routesDetails = new ArrayList<>();
                        routesDetails.add(routeLineDetails);
                        routeLineApi.setRoutes(routesDetails, (Expected<RouteLineError, RouteSetValue> routeLineErrorRouteSetValueExpected) -> {
                            routeLineView.renderRouteDrawData(Objects.requireNonNull(mapboxMap.getStyle()), routeLineErrorRouteSetValueExpected);

                            MapAnimationOptions animationOptions = new MapAnimationOptions.Builder().duration(1500L).build();

                            CameraAnimationsPlugin cameraAnimationsPlugin = CameraAnimationsUtils.getCamera(mapView);

                            // Modifica o zoom na câmera automaticamente
                            CameraOptions cameraOptions = (new CameraOptions.Builder())
                                    .center(finalResultGeometry.get(resultGeometryIndex))
                                    .zoom(15.0)
                                    .padding(new EdgeInsets(500.0, 0.0, 0.0, 0.0))
                                    .build();

                            cameraAnimationsPlugin.easeTo(cameraOptions, animationOptions);
                        });
                    }
                    routeLineDetails = null;
                }
            };
            mapboxNavigation.registerRoutesObserver(routesObserver);
        }
    }

    public void setRotaListener(RotaListener rotaListener) {
        this.rotaListener = rotaListener;
    }
}