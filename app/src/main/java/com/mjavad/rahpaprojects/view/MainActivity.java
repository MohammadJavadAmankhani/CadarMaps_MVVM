package com.mjavad.rahpaprojects.view;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cedarstudios.cedarmapssdk.CedarMaps;
import com.cedarstudios.cedarmapssdk.MapView;
import com.cedarstudios.cedarmapssdk.listeners.ForwardGeocodeResultsListener;
import com.cedarstudios.cedarmapssdk.listeners.OnTilesConfigured;
import com.cedarstudios.cedarmapssdk.listeners.ReverseGeocodeResultListener;
import com.cedarstudios.cedarmapssdk.model.geocoder.forward.ForwardGeocode;
import com.cedarstudios.cedarmapssdk.model.geocoder.reverse.ReverseGeocode;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdate;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mjavad.rahpaprojects.R;
import com.mjavad.rahpaprojects.adapters.SearchViewAdapter;
import com.mjavad.rahpaprojects.app.app;
import com.mjavad.rahpaprojects.viewModel.DistanceViewModel;
import com.mjavad.rahpaprojects.viewModel.directionViewModel;

import java.util.ArrayList;
import java.util.List;


import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity implements LocationListener , View.OnClickListener {


    //  Dark Them For Maps
    //  mapView.setStyleUrl("https://api.cedarmaps.com/v1/styles/cedarmaps.dark.json");

    private directionViewModel viewModel;
    private DistanceViewModel distanceViewModel;
    private MapView mapView;
    private MapboxMap mMap;

    private LatLng lanLngStart;
    private Marker userMarker, destinationMarker;
    public static final int PERMISSION_LOCATION_REQUEST_CODE = 105;
    private ArrayList<Marker> markers = new ArrayList<>();
    private AppCompatTextView mTextView, resultLocStrat, resultLocEnd , distanceTextView;
    private FloatingActionButton fab_location;
    private AppCompatImageView markerClose , ic_search_close;
    private ImageView imageMarkerMap;
    private CardView card_distance , card_search , parent_search;
    private LinearLayout card_origin_distance;
    private AppCompatEditText edit_search;

    private SearchViewAdapter mRecyclerAdapter;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private LinearLayout mLinearLayout;
    private LinearLayoutManager mLinearLayoutManager;
    private DividerItemDecoration mDividerItemDecoration;
    private State state = State.MAP;


    private enum State {
        MAP,
        MAP_PIN,
        SEARCHING,
        RESULTS
    }
    private void setState(State state) {
        this.state = state;
        switch (state) {
            case MAP:
            case MAP_PIN:
                mLinearLayout.setVisibility(View.GONE);
                break;
            case SEARCHING:
                mLinearLayout.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.INVISIBLE);
                mProgressBar.setVisibility(View.VISIBLE);
                break;
            case RESULTS:
                mLinearLayout.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        CedarMaps.getInstance().prepareTiles(new OnTilesConfigured() {
            @Override
            public void onSuccess() {

                setContentView(R.layout.activity_main);
                initViews();



                mapView.getMapAsync(mapboxMap -> {
                    mMap = mapboxMap;
                    Toast.makeText(MainActivity.this, "Map Ok", Toast.LENGTH_SHORT).show();

                    mapboxMap.setMaxZoomPreference(17);
                    mapboxMap.setMinZoomPreference(6);

                    viewModel = ViewModelProviders.of(MainActivity.this).get(directionViewModel.class);
                    distanceViewModel = ViewModelProviders.of(MainActivity.this).get(DistanceViewModel.class);
                    // Select Origin and destination OnClick Marker in Maps
                    imageMarkerMap.setOnClickListener(v -> {
                        if (markers.size() == 0) {
                            addMarkerToMapViewAtPosition(mapboxMap.getCameraPosition().target, R.drawable.marker_start);    distanceViewModel.start(mapboxMap.getCameraPosition().target);
                            distanceViewModel.getLiveDataStart().observe(MainActivity.this, s -> {
                                Log.e("TAG" , "MAIN Start : "+s);
                                resultLocStrat.setText(s);
                            });
                            imageMarkerMap.setImageResource(R.drawable.marker_end);
                           // reverseGeocodeLocationStart(mapboxMap.getCameraPosition().target);


                        } else if (markers.size() == 1) {
                            addMarkerToMapViewAtPosition(mapboxMap.getCameraPosition().target, R.drawable.marker_end); distanceViewModel.end(mapboxMap.getCameraPosition().target  , card_search , card_origin_distance);
                            distanceViewModel.getLiveDataEnd().observe(MainActivity.this, s -> {
                                Log.e("TAG" , "MAIN End : "+s);
                                resultLocEnd.setText(s);
                            });
                            viewModel.dirLatLng(markers.get(0).getPosition() , markers.get(1).getPosition());
                            viewModel.getLiveData().observe(MainActivity.this, latLngs -> drawCoordinatesInBound(latLngs));
                            imageMarkerMap.setVisibility(View.GONE);
                          //  reverseGeocodeLocationEnd(mapboxMap.getCameraPosition().target);

                        }
                    });

                    // Setup Locations
                    setupLocationManager();

                    // Text Address Marker Location
                   // reverseGeocode(mapboxMap.getCameraPosition());
                    distanceViewModel.full(mapboxMap.getCameraPosition() , mTextView);
                    distanceViewModel.getLiveDataFull().observe(MainActivity.this, s -> {
                        Log.e("TAG" , "MAIN Full : "+s);
                        mTextView.setText(s);
                    });

                    mapboxMap.addOnCameraIdleListener(() -> {
                        distanceViewModel.full(mapboxMap.getCameraPosition() , mTextView);
                        distanceViewModel.getLiveDataFull().observe(MainActivity.this, s -> {
                            Log.e("TAG" , "MAIN Full : "+s);
                            mTextView.setText(s);
                        });
                    });

                });
            }

            @Override
            public void onFailure(@NonNull String s) {
                Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // init Views
    private void initViews() {
        mapView                 = findViewById(R.id.maps                   );
        mTextView               = findViewById(R.id.result                 );
        resultLocStrat          = findViewById(R.id.resultLocStart         );
        resultLocEnd            = findViewById(R.id.resultLocEnd           );
        fab_location            = findViewById(R.id.fab_location           );
        markerClose             = findViewById(R.id.markerClose            );
        imageMarkerMap          = findViewById(R.id.imageMarkerMap         );
        distanceTextView        = findViewById(R.id.distanceTextView       );
        card_distance           = findViewById(R.id.card_Distance          );
        card_origin_distance    = findViewById(R.id.card_origin_distance   );
        card_search             = findViewById(R.id.card_search            );
        ic_search_close         = findViewById(R.id.ic_search_close        );
        parent_search           = findViewById(R.id.parent_search          );
        edit_search             = findViewById(R.id.edit_search            );
        mRecyclerView           = findViewById(R.id.recyclerView           );
        mLinearLayout           = findViewById(R.id.search_results_linear_layout);
        mProgressBar            = findViewById(R.id.search_progress_bar    );


        searchCode();
        fab_location.setOnClickListener   (this::onClick);
        markerClose.setOnClickListener    (this::onClick);
        card_search.setOnClickListener    (this::onClick);
        ic_search_close.setOnClickListener(this::onClick);
    }

    // Add Marker To ArrayList
    private void addMarkerToMapViewAtPosition(LatLng coordinate, int markerImageID) {
        if (mMap != null && getContext() != null) {
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(coordinate)
                    .icon(IconFactory.getInstance(getApplicationContext()).fromResource(markerImageID))
            );
            markers.add(marker);

            app.log("Marker List "+markers.toString());
        }
    }


    // Setup Location
    private void setupLocationManager() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager != null) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                            , Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_LOCATION_REQUEST_CODE);
                }
                return;
            }
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                onLocationChanged(location);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 30000, 5, this);

            }
        }
    }


    // init SearchBox
    private void searchCode() {

        mLinearLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mDividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(), mLinearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(mDividerItemDecoration);

        edit_search.clearFocus();
        mLinearLayout.setVisibility(View.GONE);
        edit_search.setText("");
        search();

    }

    // Edit Text Searching
    private void search(){
        edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    setState(State.MAP);
                }else {
                    CedarMaps.getInstance().forwardGeocode(s.toString(), new ForwardGeocodeResultsListener() {
                        @Override
                        public void onSuccess(@NonNull List<ForwardGeocode> results) {
                            setState(State.RESULTS);
                            if (results.size() > 0) {
                                mRecyclerAdapter = new SearchViewAdapter(results);
                                mRecyclerView.setAdapter(mRecyclerAdapter);
                            }
                        }

                        @Override
                        public void onFailure(@NonNull String errorMessage) {
                            mLinearLayout.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
                        }
                    });
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    // Show area searched in Map
    public void showItemOnMap(final ForwardGeocode item) {
        setState(State.MAP_PIN);
        app.closeKeybored(edit_search);
        edit_search.clearFocus();
        if (item.getLocation().getCenter() != null) {
            mMap.easeCamera(CameraUpdateFactory.newLatLng(item.getLocation().getCenter()), 1000);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null && mMap != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            if (userMarker != null) {
                userMarker.remove();
            }

            userMarker = addMarkerToMapView(latLng, R.drawable.icon);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 16);
            mMap.animateCamera(cameraUpdate);
            lanLngStart = new LatLng(location.getLatitude(), location.getLongitude());
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, R.string.enabled, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, R.string.disabled, Toast.LENGTH_SHORT).show();
    }

    // Reset Map
    private void resetToInitialState() {
        mMap.clear();
        markers.clear();
        if (lanLngStart != null){

            userMarker = addMarkerToMapView(lanLngStart, R.drawable.icon);
        }
        imageMarkerMap.setVisibility(View.VISIBLE);
        imageMarkerMap.setImageResource(R.drawable.marker_start);
    }


    // Add Marker To Map
    private Marker addMarkerToMapView(LatLng coordinate, int markerImageID) {
        Marker marker;
        if (mMap != null && getContext() != null) {
            marker = mMap.addMarker(new MarkerOptions()
                    .position(coordinate)
                    .icon(IconFactory.getInstance(getApplicationContext()).fromResource(markerImageID))
            );
            return marker;
        } else return null;
    }


    // Draw a Routing
    private void drawCoordinatesInBound(ArrayList<LatLng> coordinates, LatLngBounds bounds) {
        if (mMap == null || getContext() == null) {
            return;
        }
        mMap.addPolyline(new PolylineOptions()
                .addAll(coordinates)
                .color(ContextCompat.getColor(getApplicationContext(), R.color.primaryDarkColor))
                .width(6)
                .alpha((float) 0.9));

        mMap.easeCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150), 1000);
    }
    // Draw a Routing
    private void drawCoordinatesInBound(ArrayList<LatLng> coordinates) {
        if (mMap == null || getContext() == null) {
            return;
        }
        mMap.addPolyline(new PolylineOptions()
                .addAll(coordinates)
                .color(ContextCompat.getColor(getApplicationContext(), R.color.primaryDarkColor))
                .width(6)
                .alpha((float) 0.9));

    }



    // RequestPermissions GPS with code 105
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_LOCATION_REQUEST_CODE){
            if (grantResults[0] >= 0){
                setupLocationManager();
            }
            else
            {
                Toast.makeText(this, "اجازه دسترسی به مکان یاب داده نشد", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // All OnClicks
    @Override
    public void onClick(View v) {

        if (v == fab_location){
            setupLocationManager();
        }
        else if (v == markerClose){
            resetToInitialState();

            card_distance.startAnimation(AnimationUtils.loadAnimation(MainActivity.this , R.anim.animation_show_mile_stop));
            card_origin_distance.startAnimation(AnimationUtils.loadAnimation(MainActivity.this , R.anim.animation_origin_distance_stop));
            card_distance.setVisibility(View.GONE);
            card_origin_distance.setVisibility(View.GONE);
            card_search.setVisibility(View.VISIBLE);
        }
        else if (v == card_search){
            parent_search.setVisibility(View.VISIBLE);
            parent_search.startAnimation(AnimationUtils.loadAnimation(MainActivity.this , R.anim.animation_origin_distance_start));
            card_search.setVisibility(View.GONE);
            card_search.setAnimation(AnimationUtils.loadAnimation(MainActivity.this , android.R.anim.fade_out));
        }
        else if (v == ic_search_close){
           hideSearchBox();
           if (mLinearLayout.getVisibility() == View.VISIBLE){
               hideLiner();
           }
        }
    }

    // Animation SearchBox hide
    private void hideSearchBox(){

        Animation animation = AnimationUtils.loadAnimation(this ,R.anim.animation_origin_distance_stop);
        parent_search.startAnimation(animation);
        edit_search.setText("");
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                parent_search.setVisibility(View.GONE);
                app.closeKeybored(edit_search);
                if (card_distance.getVisibility() == View.VISIBLE && card_origin_distance.getVisibility() == View.VISIBLE){
                    card_search.setVisibility(View.GONE);
                }else{
                    card_search.setVisibility(View.VISIBLE);
                    card_search.setAnimation(AnimationUtils.loadAnimation(MainActivity.this , android.R.anim.fade_in));
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    // Animation UnShow LinerLayout
    private void hideLiner(){

        Animation animation = AnimationUtils.loadAnimation(this ,R.anim.animation_show_mile_stop);
        mLinearLayout.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mLinearLayout.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

}
