package com.example.krhotels.Fragment;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.krhotels.Adapter.hotelAdapter;
import com.example.krhotels.GpsTracker;
import com.example.krhotels.Model.IndividualLocation2;
import com.example.krhotels.Model.Restaurant;
import com.example.myapplication.R;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.turf.TurfConstants;
import com.mapbox.turf.TurfConversion;

import java.text.DecimalFormat;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HotelFragment extends Fragment implements TextWatcher {
    private LatLng MOCK_DEVICE_LOCATION_LAT_LNG;
    private GpsTracker gpsTracker;


    RecyclerView rv_duration;

    ArrayList<IndividualLocation2> list;
    int indexList = 0;

    EditText editText;



    hotelAdapter resAdapter;


    static String finalDuration;
    private static final String SPREAD_SHEET_ID = "1BTLGpK0Y0r7r4MMuUHcLxbhePXW2yPyL_Usx8Mj4YHU";

    double latitude2;
    double longitude2;
    DatabaseReference mbase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_hotel, container, false);

        indexList = 0;

        Mapbox.getInstance(getContext(), getString(R.string.access_token));

        try {
            if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        rv_duration = v.findViewById(R.id.rv_hotel);

        editText = v.findViewById(R.id.et_search_hotel);

        rv_duration.setLayoutManager(new LinearLayoutManager(getContext()));

        list = new ArrayList<>();




        LatLng singleLocationLatLng = new LatLng(59.610685,
                42.4600098);

        getLocation();
        MOCK_DEVICE_LOCATION_LAT_LNG = new LatLng(latitude2,longitude2);


        FirebaseShowRestaurant();

        editText.addTextChangedListener(this);




        return v;
    }
    private void filter(String text) {
        ArrayList<IndividualLocation2> filteredList = new ArrayList<>();


        for (IndividualLocation2 item: list) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }
        if(resAdapter != null){
            resAdapter.filterList(filteredList);}
    }

    private void getInformationFromDirectionsApiDriving(Point destinationPoint, int index) {

        Point mockCurrentLocation = Point.fromLngLat(MOCK_DEVICE_LOCATION_LAT_LNG.getLongitude(),
                MOCK_DEVICE_LOCATION_LAT_LNG.getLatitude());

        Point destinationMarker = Point.fromLngLat(destinationPoint.longitude(), destinationPoint.latitude());




        MapboxDirections directionsApiClient = MapboxDirections.builder()
                .origin(mockCurrentLocation)
                .destination(destinationMarker)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .accessToken(getString(R.string.access_token))
                .build();

        directionsApiClient.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {


                if(response == null){
                    Toast.makeText(getContext(), "null", Toast.LENGTH_SHORT).show();
                }



                DecimalFormat df = new DecimalFormat("#.#");
                String finalConvertedFormattedDistance = String.valueOf(df.format(TurfConversion.convertLength(
                        response.body().routes().get(0).distance(), TurfConstants.UNIT_METERS,
                        TurfConstants.UNIT_KILOMETERS)));

                int x = response.body().routes().get(0).duration().intValue();




                finalDuration =
                        String.valueOf(formatSeconds(x));




                list.get(index-1).setDurationDriving(finalDuration + " " + "min");

                list.get(index-1).setDistance(finalConvertedFormattedDistance + "km,");

                resAdapter.notifyDataSetChanged();


            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                Toast.makeText(getContext(), "call " + call.toString(), Toast.LENGTH_SHORT).show();
            }


        });




    }

    private void getInformationFromDirectionsApiWalking(Point destinationPoint, int index) {

        Point mockCurrentLocation = Point.fromLngLat(MOCK_DEVICE_LOCATION_LAT_LNG.getLongitude(),
                MOCK_DEVICE_LOCATION_LAT_LNG.getLatitude());

        Point destinationMarker = Point.fromLngLat(destinationPoint.longitude(), destinationPoint.latitude());




        MapboxDirections directionsApiClient = MapboxDirections.builder()
                .origin(mockCurrentLocation)
                .destination(destinationMarker)
                .overview(DirectionsCriteria.OVERVIEW_FULL)
                .profile(DirectionsCriteria.PROFILE_WALKING)
                .accessToken(getString(R.string.access_token))
                .build();

        directionsApiClient.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {


                if(response == null){
                    Toast.makeText(getContext(), "null", Toast.LENGTH_SHORT).show();
                }



                DecimalFormat df = new DecimalFormat("#.#");
                String finalConvertedFormattedDistance = String.valueOf(df.format(TurfConversion.convertLength(
                        response.body().routes().get(0).distance(), TurfConstants.UNIT_METERS,
                        TurfConstants.UNIT_KILOMETERS)));

                int x = response.body().routes().get(0).duration().intValue();




                finalDuration =
                        String.valueOf(formatSeconds(x));




                list.get(index-1).setDurationWalking(finalDuration + " " + "min");

                resAdapter.notifyDataSetChanged();


            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable t) {
                Toast.makeText(getContext(), "call " + call.toString(), Toast.LENGTH_SHORT).show();
            }


        });




    }
    public static String formatSeconds(int timeInSeconds)
    {
        int hours = timeInSeconds / 3600;
        int secondsLeft = timeInSeconds - hours * 3600;
        int minutes = secondsLeft / 60;
        int seconds = secondsLeft - minutes * 60;

        String formattedTime = "";

        if(hours>=1) {
            if (hours < 10)
                formattedTime += "0";
            formattedTime += hours + ":";

            if (minutes < 10)
                formattedTime += "0";
            formattedTime += minutes + ":";

            if (seconds < 10)
                formattedTime += "0";
            formattedTime += seconds;
        }
        else{
            if (minutes < 10)
                formattedTime += "0";
            formattedTime += minutes + ":";

            if (seconds < 10)
                formattedTime += "0";
            formattedTime += seconds;
        }

        return formattedTime;
    }

    public void getLocation(){
        gpsTracker = new GpsTracker(getContext());
        if(gpsTracker.canGetLocation()){
            latitude2 = gpsTracker.getLatitude();
            longitude2 = gpsTracker.getLongitude();

        }else{
            gpsTracker.showSettingsAlert();
        }
    }
    private void FirebaseShowRestaurant() {
        mbase = FirebaseDatabase.getInstance().getReference().child(SPREAD_SHEET_ID).child("Hotel");
        FirebaseRecyclerOptions<Restaurant> options
                = new FirebaseRecyclerOptions.Builder<Restaurant>()
                .setQuery(mbase, Restaurant.class)
                .build();

        mbase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    indexList++;

                    String name = snapshot1.child("name").getValue().toString();
                    String address = snapshot1.child("address").getValue().toString();
                    String imageUrl = snapshot1.child("image1").getValue().toString();

                    LatLng singleLocationLatLng = new LatLng(Double.valueOf(snapshot1.child("latitude").getValue().toString()),
                            Double.valueOf(snapshot1.child("longitude").getValue().toString()));


                    Point destinationMarker = Point.fromLngLat(singleLocationLatLng.getLongitude(), singleLocationLatLng.getLatitude());





                    list.add(new IndividualLocation2(name, address, imageUrl, singleLocationLatLng));

                    getInformationFromDirectionsApiDriving(destinationMarker, indexList);

                    getInformationFromDirectionsApiWalking(destinationMarker, indexList);

                }
                indexList = 0;


                resAdapter = new hotelAdapter(getContext(), list);
                rv_duration.setAdapter(resAdapter);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "error = " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        mbase.keepSynced(true);


    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        filter(editable.toString());
    }
}