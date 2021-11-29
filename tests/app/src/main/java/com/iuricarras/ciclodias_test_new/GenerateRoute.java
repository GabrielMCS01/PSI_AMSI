package com.iuricarras.ciclodias_test_new;

import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.matching.v5.MapboxMapMatching;
import com.mapbox.api.matching.v5.models.MapMatchingResponse;
import com.mapbox.geojson.Point;
import com.mapbox.navigation.core.MapboxNavigation;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GenerateRoute {

    public ArrayList<Point> points;
    private MapboxMapMatching mapMatching;
    private DirectionsRoute directionsRoute;
    private MapboxNavigation mapboxNavigation;

    public GenerateRoute() {

    }

    public void generate(){

        mapMatching = MapboxMapMatching.builder().
                accessToken(String.valueOf(R.string.mapbox_access_token)).
                coordinates(points).
                profile(DirectionsCriteria.PROFILE_CYCLING).
                build();

        mapMatching.enqueueCall(new Callback<MapMatchingResponse>() {
            @Override
            public void onResponse(Call<MapMatchingResponse> call, Response<MapMatchingResponse> response) {
                directionsRoute = response.body().matchings().get(0).toDirectionRoute();
                List<DirectionsRoute> list = new ArrayList<>();
                list.add(directionsRoute);
                mapboxNavigation.setRoutes(list);

            }

            @Override
            public void onFailure(Call<MapMatchingResponse> call, Throwable t) {

            }
        });
    }
}
