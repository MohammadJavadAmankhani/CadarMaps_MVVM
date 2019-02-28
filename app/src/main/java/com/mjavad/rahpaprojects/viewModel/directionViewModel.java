package com.mjavad.rahpaprojects.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mjavad.rahpaprojects.model.DirectionRepository;

import java.util.ArrayList;

public class directionViewModel extends ViewModel {
    private DirectionRepository directionRepository;
    private LiveData<ArrayList<LatLng>> liveData;

    public directionViewModel() {
        this.directionRepository = new DirectionRepository();
    }

    public void dirLatLng(LatLng lat  , LatLng lng){
        this.liveData = directionRepository.computeDirection(lat , lng);
    }
    public LiveData<ArrayList<LatLng>> getLiveData() {
        return liveData;
    }
}
