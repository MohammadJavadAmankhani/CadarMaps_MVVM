package com.mjavad.rahpaprojects.model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.cedarstudios.cedarmapssdk.CedarMaps;
import com.cedarstudios.cedarmapssdk.listeners.ReverseGeocodeResultListener;
import com.cedarstudios.cedarmapssdk.model.geocoder.reverse.ReverseGeocode;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mjavad.rahpaprojects.R;
import com.mjavad.rahpaprojects.app.Application;

public class DistanceRepository {


    String start , end , full;

    public LiveData<String> reverseGeocodeLocationStart(LatLng latLng) {

        LiveData<String> liveData = new MutableLiveData<>();
        CedarMaps.getInstance().reverseGeocode(
                latLng,
                new ReverseGeocodeResultListener() {
                    @Override
                    public void onSuccess(@NonNull ReverseGeocode result) {
                        start = (fullAddressForItem(result));
                        Log.e("TAG" , "REPO Start : "+start);
                    }

                    @Override
                    public void onFailure(@NonNull String errorMessage) {

                       start = ("Error");
                    }
                });
        ((MutableLiveData<String>) liveData).setValue(start);
        return liveData;
    }

    public LiveData<String> reverseGeocodeLocationEnd(LatLng latLng ,  View view1 , View view2) {
        view1.setVisibility(View.GONE);

        view2.setVisibility(View.VISIBLE);
        view2.startAnimation(AnimationUtils.loadAnimation(Application.getContext(), R.anim.animation_origin_distance_start));
        LiveData<String> liveData = new MutableLiveData<>();
        CedarMaps.getInstance().reverseGeocode(
                latLng,
                new ReverseGeocodeResultListener() {
                    @Override
                    public void onSuccess(@NonNull ReverseGeocode result) {
                    //    view.setVisibility(View.VISIBLE);

                        end = (fullAddressForItem(result));
                        Log.e("TAG" , "REPO End : "+end);
                    }

                    @Override
                    public void onFailure(@NonNull String errorMessage) {
                      //  view.setVisibility(View.VISIBLE);

                       end = ("Error");
                    }
                });
        ((MutableLiveData<String>) liveData).setValue(end);
        return liveData;
    }


    public LiveData<String> reverseGeocode(CameraPosition position , TextView mTextView) {

        LiveData<String> liveData = new MutableLiveData<>();
        if (TextUtils.isEmpty(mTextView.getText())) {
            mTextView.setVisibility(View.GONE);
        } else {
            mTextView.setVisibility(View.VISIBLE);
        }

        CedarMaps.getInstance().reverseGeocode(
                position.target,
                new ReverseGeocodeResultListener() {
                    @Override
                    public void onSuccess(@NonNull ReverseGeocode result) {
                        mTextView.setVisibility(View.VISIBLE);

                        full = (fullAddressForItem(result));
                        Log.e("TAG" , "REPO Full : "+full);
                    }

                    @Override
                    public void onFailure(@NonNull String errorMessage) {
                        mTextView.setVisibility(View.VISIBLE);

                        full = ("درحال جستوجو..");
                    }
                });
        ((MutableLiveData<String>) liveData).setValue(full);
        return liveData;
    }


    private String fullAddressForItem(ReverseGeocode item) {
        String result = "";

        if (!TextUtils.isEmpty(item.getProvince())) {
            result += (Application.getContext().getString(R.string.province) + " " + item.getProvince());
        }

        if (!TextUtils.isEmpty(item.getCity())) {
            if (TextUtils.isEmpty(result)) {
                result = item.getCity();
            } else {
                result = result + Application.getContext().getString(R.string.comma) + " " + item.getCity();
            }
        }

        if (!TextUtils.isEmpty(item.getLocality())) {
            if (TextUtils.isEmpty(result)) {
                result = item.getLocality();
            } else {
                result = result + Application.getContext().getString(R.string.comma) + " " + item.getLocality();
            }
        }

        if (!TextUtils.isEmpty(item.getAddress())) {
            if (TextUtils.isEmpty(result)) {
                result = item.getAddress();
            } else {
                result = result + Application.getContext().getString(R.string.comma) + " " + item.getAddress();
            }
        }

        if (!TextUtils.isEmpty(item.getPlace())) {
            if (TextUtils.isEmpty(result)) {
                result = item.getPlace();
            } else {
                result = result + Application.getContext().getString(R.string.comma) + " " + item.getPlace();
            }
        }

        return result;
    }

}
