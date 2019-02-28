package com.mjavad.rahpaprojects.model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.cedarstudios.cedarmapssdk.CedarMaps;
import com.cedarstudios.cedarmapssdk.listeners.GeoRoutingResultListener;
import com.cedarstudios.cedarmapssdk.model.routing.GeoRouting;
import com.cedarstudios.cedarmapssdk.model.routing.Route;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mjavad.rahpaprojects.R;
import com.mjavad.rahpaprojects.app.Application;

import java.util.ArrayList;

public class DirectionRepository {



    public LiveData<ArrayList<LatLng>> computeDirection(LatLng departure, LatLng destination) {
        ArrayList<LatLng> coordinates = new ArrayList<>();
        final LiveData<ArrayList<LatLng>> data = new MutableLiveData<>();

        CedarMaps.getInstance().direction(departure, destination,
                new GeoRoutingResultListener() {
                    @Override
                    public void onSuccess(@NonNull GeoRouting result) {
                        Route route = result.getRoutes().get(0);
                        Double distance = route.getDistance();
                        if (distance > 1000) {
                            distance = distance / 1000.0;
                            distance = (double) Math.round(distance * 100d) / 100d;
                          //  distanceTextView.setText(" " + distance + " Km");
                        } else {
                            distance = (double) Math.round(distance);
                          //  distanceTextView.setText(" " + distance + " m");
                        }


                        for (int i = 0; i < route.getGeometry().getCoordinates().size(); i++) {
                            coordinates.add(route.getGeometry().getCoordinates().get(i));
                        }

                   //     drawCoordinatesInBound(coordinates, route.getBoundingBox());

                        ((MutableLiveData<ArrayList<LatLng>>) data).setValue(coordinates);
                    }

                    @Override
                    public void onFailure(@NonNull String error) {
                    //    resetToInitialState();
                        Toast.makeText(Application.getContext(),
                                "ERROR" + "\n" + error,
                                Toast.LENGTH_SHORT).show();
                    }
                });

        return data;
    }

}
