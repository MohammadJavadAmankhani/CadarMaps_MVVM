package com.mjavad.rahpaprojects.viewModel;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.view.View;
import android.widget.TextView;

import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mjavad.rahpaprojects.model.DistanceRepository;

public class DistanceViewModel extends ViewModel {

    DistanceRepository distanceRepository;
    LiveData<String> liveDataStart;
    LiveData<String> liveDataEnd;
    LiveData<String> liveDataFull;

    public DistanceViewModel() {
        distanceRepository  = new DistanceRepository();
    }
    public void start(LatLng latLng){
       liveDataStart =  distanceRepository.reverseGeocodeLocationStart(latLng);
    }
    public void end(LatLng latLng  , View view1 , View view2){
        liveDataEnd =  distanceRepository.reverseGeocodeLocationEnd(latLng  , view1 , view2);
    }
    public void full(CameraPosition position  , TextView view1 ){
        liveDataFull =  distanceRepository.reverseGeocode(position  , view1 );
    }

    public LiveData<String> getLiveDataStart() {
        return liveDataStart;
    }
    public LiveData<String> getLiveDataEnd() {
        return liveDataEnd;
    }
    public LiveData<String> getLiveDataFull() {
        return liveDataFull;
    }
}
