package com.example.krhotels.Fragment;

import static com.mapbox.core.constants.Constants.PRECISION_6;
import static com.mapbox.mapboxsdk.style.expressions.Expression.eq;
import static com.mapbox.mapboxsdk.style.expressions.Expression.get;
import static com.mapbox.mapboxsdk.style.expressions.Expression.literal;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.iconImage;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineColor;
import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.lineWidth;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.krhotels.GpsTracker;
import com.example.krhotels.Model.IndividualLocation;
import com.example.krhotels.Utils.LinearLayoutManagerWithSmoothScroller;
import com.example.krhotels.Adapter.LocationRecyclerViewAdapter;
import com.example.myapplication.R;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.MapboxDirections;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.building.BuildingPlugin;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.turf.TurfConstants;
import com.mapbox.turf.TurfConversion;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantMapFragment extends Fragment implements
        LocationRecyclerViewAdapter.ClickListener {
    private LatLng MOCK_DEVICE_LOCATION_LAT_LNG;
    private static final int MAPBOX_LOGO_OPACITY = 75;
    private static final int CAMERA_MOVEMENT_SPEED_IN_MILSECS = 1200;
    private static final float NAVIGATION_LINE_WIDTH = 9;
    private static final float BUILDING_EXTRUSION_OPACITY = .8f;
    private static final String PROPERTY_SELECTED = "selected";
    private static final String BUILDING_EXTRUSION_COLOR = "#c4dbed";
    private DirectionsRoute currentRoute;
    private FeatureCollection featureCollection;
    public MapboxMap mapboxMap2;
    private MapView mapView;
    private RecyclerView locationsRecyclerView;
    private ArrayList<IndividualLocation> listOfIndividualLocationsRestaurant;
    private ArrayList<IndividualLocation> listOfIndividualLocationsHotel;
    private CustomThemeManager customThemeManager;
    private LocationRecyclerViewAdapter styleRvAdapter;
    private int chosenTheme;
    private String TAG = "MapActivity";
    private GpsTracker gpsTracker;
    View v;

    double latitude;
    double longitude;
    String location_restaurant = "list_of_locations.geojson";
    String location_hotel = "list_of_locations_hotel.geojson";

    TextView showRestaurant, showHotel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Mapbox.getInstance(getContext(), getString(R.string.access_token));



        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
         v = inflater.inflate(R.layout.fragment_map, container, false);

        try {
            getFeatureCollectionFromJson(location_restaurant);


        } catch (Exception exception) {
            Log.e("MapActivity", "onCreate: " + exception);
            Toast.makeText(getContext(), R.string.failure_to_load_file, Toast.LENGTH_LONG).show();
        }


        listOfIndividualLocationsRestaurant = new ArrayList<>();
        listOfIndividualLocationsHotel = new ArrayList<>();


        chosenTheme = R.style.AppTheme_Neutral;


        mapView = v.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        try {
            if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();

        }
        getLocation();

        MOCK_DEVICE_LOCATION_LAT_LNG = new LatLng(latitude,longitude);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final MapboxMap mapboxMap) {

                // Initialize the custom class that handles marker icon creation and map styling based on the selected theme
                customThemeManager = new CustomThemeManager(chosenTheme, getContext());

                mapboxMap.setStyle(new Style.Builder().fromUrl(customThemeManager.getMapStyle()), new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {

                        // Setting the returned mapboxMap object (directly above) equal to the "globally declared" one
                     //   this.mapboxMap2 = mapboxMap;
                        mapboxMap2 = mapboxMap;


                        // Adjust the opacity of the Mapbox logo in the lower left hand corner of the map

                        getLocation();
                        // Set bounds for the map camera so that the user can't pan the map outside of the NYC area


                        // Set up the SymbolLayer which will show the icons for each store location
                        initStoreLocationIconSymbolLayer();

                        // Set up the SymbolLayer which will show the selected store icon
                        initSelectedStoreSymbolLayer();

                        // Set up the LineLayer which will show the navigation route line to a particular store location
                        initNavigationPolylineLineLayer();

                        // Create a list of features from the feature collection


                        List<Feature> featureList = featureCollection.features();

                        // Retrieve and update the source designated for showing the store location icons
                        GeoJsonSource source = mapboxMap.getStyle().getSourceAs("store-location-source-id");
                        if (source != null) {
                            source.setGeoJson(FeatureCollection.fromFeatures(featureList));
                        }

                        if (featureList != null) {

                            for (int x = 0; x < featureList.size(); x++) {

                                Feature singleLocation = featureList.get(x);

                                // Get the single location's String properties to place in its map marker
                                String singleLocationName = singleLocation.getStringProperty("name");
                                String singleLocationHours = singleLocation.getStringProperty("hours");
                                String singleLocationDescription = singleLocation.getStringProperty("description");
                                String singleLocationPhoneNum = singleLocation.getStringProperty("phone");


                                // Add a boolean property to use for adjusting the icon of the selected store location
                                singleLocation.addBooleanProperty(PROPERTY_SELECTED, false);

                                // Get the single location's LatLng coordinates
                                Point singleLocationPosition = (Point) singleLocation.geometry();

                                // Create a new LatLng object with the Position object created above
                                LatLng singleLocationLatLng = new LatLng(singleLocationPosition.latitude(),
                                        singleLocationPosition.longitude());

                                // Add the location to the Arraylist of locations for later use in the recyclerview
                                listOfIndividualLocationsRestaurant.add(new IndividualLocation(
                                        singleLocationName,
                                        singleLocationDescription,
                                        singleLocationHours,
                                        singleLocationPhoneNum,
                                        singleLocationLatLng
                                ));

                                // Call getInformationFromDirectionsApi() to eventually display the location's
                                // distance from mocked device location
                                getInformationFromDirectionsApi(singleLocationPosition, false, x);
                            }
                            // Add the fake device location marker to the map. In a real use case scenario,
                            // the Maps SDK's LocationComponent can be used to easily display and customize
                            // the device location's puck
                            addMockDeviceLocationMarkerToMap();

                            setUpRecyclerViewOfLocationCards(chosenTheme);

                            //mapboxMap.addOnMapClickListener(this);

                            mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                                @Override
                                public boolean onMapClick(@NonNull LatLng point) {
                                    handleClickIcon(mapboxMap2.getProjection().toScreenLocation(point));
                                    return true;
                                }
                            });


                            Toast.makeText(getContext(), "Click on a card", Toast.LENGTH_SHORT).show();

                            // Show 3d buildings if the blue theme is being used
                            if (customThemeManager.getNavigationLineColor() == R.color.navigationRouteLine_blue) {
                                showBuildingExtrusions();
                            }
                        }
                    }

                });

            }
        });
        return v;
    }






    public void getLocation(){
        gpsTracker = new GpsTracker(getContext());
        if(gpsTracker.canGetLocation()){
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();

        }else{
            gpsTracker.showSettingsAlert();
        }
    }

    private void showBuildingExtrusions() {
        // Use the Mapbox building plugin to display and customize the opacity/color of building extrusions
        BuildingPlugin buildingPlugin = new BuildingPlugin(mapView, mapboxMap2, mapboxMap2.getStyle());
        buildingPlugin.setVisibility(true);
        buildingPlugin.setOpacity(BUILDING_EXTRUSION_OPACITY);
        buildingPlugin.setColor(Color.parseColor(BUILDING_EXTRUSION_COLOR));
    }



    private boolean handleClickIcon(PointF screenPoint) {
        List<Feature> features = mapboxMap2.queryRenderedFeatures(screenPoint, "store-location-layer-id");
        if (!features.isEmpty()) {
            String name = features.get(0).getStringProperty("name");
            List<Feature> featureList = featureCollection.features();
            for (int i = 0; i < featureList.size(); i++) {

                if (featureList.get(i).getStringProperty("name").equals(name)) {
                    Point selectedFeaturePoint = (Point) featureList.get(i).geometry();

                    if (featureSelectStatus(i)) {
                        setFeatureSelectState(featureList.get(i), false);
                    } else {
                        setSelected(i);
                    }
                    if (selectedFeaturePoint.latitude() != MOCK_DEVICE_LOCATION_LAT_LNG.getLatitude()) {
                        for (int x = 0; x < featureCollection.features().size(); x++) {

                            if (listOfIndividualLocationsRestaurant.get(x).getLocation().getLatitude() == selectedFeaturePoint.latitude()) {
                                // Scroll the recyclerview to the selected marker's card. It's "x-1" below because
                                // the mock device location marker is part of the marker list but doesn't have its own card
                                // in the actual recyclerview.
                                locationsRecyclerView.smoothScrollToPosition(x);
                            }
                        }
                    }
                } else {
                    setFeatureSelectState(featureList.get(i), false);
                }
            }
            return true;
        } else {
            return false;
        }
    }


    @Override
    public void onItemClick(int position) {
        // Get the selected individual location via its card's position in the recyclerview of cards
        IndividualLocation selectedLocation = listOfIndividualLocationsRestaurant.get(position);

        // Evaluate each Feature's "select state" to appropriately style the location's icon
        List<Feature> featureList = featureCollection.features();
        Point selectedLocationPoint = (Point) featureCollection.features().get(position).geometry();
        for (int i = 0; i < featureList.size(); i++) {
            if (featureList.get(i).getStringProperty("name").equals(selectedLocation.getName())) {
                if (featureSelectStatus(i)) {
                    setFeatureSelectState(featureList.get(i), false);
                } else {
                    setSelected(i);
                }
            } else {
                setFeatureSelectState(featureList.get(i), false);
            }
        }

        // Reposition the map camera target to the selected marker
        if (selectedLocation != null) {
            repositionMapCamera(selectedLocationPoint);
        }

        // Check for an internet connection before making the call to Mapbox Directions API
        if (deviceHasInternetConnection()) {
            // Start call to the Mapbox Directions API
            if (selectedLocation != null) {
                getInformationFromDirectionsApi(selectedLocationPoint, true, null);
            }
        } else {
            Toast.makeText(getContext(), R.string.no_internet_message, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Adds a SymbolLayer which will show all of the location's icons
     */
    private void initStoreLocationIconSymbolLayer() {
        Style style = mapboxMap2.getStyle();
        if (style != null) {
            // Add the icon image to the map
            style.addImage("store-location-icon-id", customThemeManager.getUnselectedMarkerIcon());

            // Create and add the GeoJsonSource to the map
            GeoJsonSource storeLocationGeoJsonSource = new GeoJsonSource("store-location-source-id");
            style.addSource(storeLocationGeoJsonSource);

            // Create and add the store location icon SymbolLayer to the map
            SymbolLayer storeLocationSymbolLayer = new SymbolLayer("store-location-layer-id",
                    "store-location-source-id");
            storeLocationSymbolLayer.withProperties(
                    iconImage("store-location-icon-id"),
                    iconAllowOverlap(true),
                    iconIgnorePlacement(true)
            );
            style.addLayer(storeLocationSymbolLayer);

        } else {
            Log.d("StoreFinderActivity", "initStoreLocationIconSymbolLayer: Style isn't ready yet.");

            throw new IllegalStateException("Style isn't ready yet.");
        }
    }

    /**
     * Adds a SymbolLayer which will show the selected location's icon
     */
    private void initSelectedStoreSymbolLayer() {
        Style style = mapboxMap2.getStyle();
        if (style != null) {

            // Add the icon image to the map
            style.addImage("selected-store-location-icon-id", customThemeManager.getSelectedMarkerIcon());

            // Create and add the store location icon SymbolLayer to the map
            SymbolLayer selectedStoreLocationSymbolLayer = new SymbolLayer("selected-store-location-layer-id",
                    "store-location-source-id");
            selectedStoreLocationSymbolLayer.withProperties(
                    iconImage("selected-store-location-icon-id"),
                    iconAllowOverlap(true)
            );
            selectedStoreLocationSymbolLayer.withFilter(eq((get(PROPERTY_SELECTED)), literal(true)));
            style.addLayer(selectedStoreLocationSymbolLayer);
        } else {
            Log.d("StoreFinderActivity", "initSelectedStoreSymbolLayer: Style isn't ready yet.");
            throw new IllegalStateException("Style isn't ready yet.");
        }
    }


    private boolean featureSelectStatus(int index) {
        if (featureCollection == null) {
            return false;
        }
        return featureCollection.features().get(index).getBooleanProperty(PROPERTY_SELECTED);
    }

    /**
     * Set a feature selected state.
     *
     * @param index the index of selected feature
     */
    private void setSelected(int index) {
        Feature feature = featureCollection.features().get(index);
        setFeatureSelectState(feature, true);
        refreshSource();
    }

    /**
     * Selects the state of a feature
     *
     * @param feature the feature to be selected.
     */
    private void setFeatureSelectState(Feature feature, boolean selectedState) {
        feature.properties().addProperty(PROPERTY_SELECTED, selectedState);
        refreshSource();
    }


    /**
     * Updates the display of data on the map after the FeatureCollection has been modified
     */
    private void refreshSource() {
        GeoJsonSource source = mapboxMap2.getStyle().getSourceAs("store-location-source-id");
        if (source != null && featureCollection != null) {
            source.setGeoJson(featureCollection);
        }
    }

    private void getInformationFromDirectionsApi(Point destinationPoint,
                                                 final boolean fromMarkerClick, @Nullable final Integer listIndex) {
        // Set up origin and destination coordinates for the call to the Mapbox Directions API
        Point mockCurrentLocation = Point.fromLngLat(MOCK_DEVICE_LOCATION_LAT_LNG.getLongitude(),
                MOCK_DEVICE_LOCATION_LAT_LNG.getLatitude());

        Point destinationMarker = Point.fromLngLat(destinationPoint.longitude(), destinationPoint.latitude());

        // Initialize the directionsApiClient object for eventually drawing a navigation route on the map
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
                // Check that the response isn't null and that the response has a route
                if (response.body() == null) {
                    Log.e("MapActivity", "No routes found, make sure you set the right user and access token.");
                } else if (response.body().routes().size() < 1) {
                    Log.e("MapActivity", "No routes found");
                } else {
                    if (fromMarkerClick) {
                        // Retrieve and draw the navigation route on the map
                        currentRoute = response.body().routes().get(0);
                        drawNavigationPolylineRoute(currentRoute);
                    } else {
                        // Use Mapbox Turf helper method to convert meters to miles and then format the mileage number
                        DecimalFormat df = new DecimalFormat("#.#");
                        String finalConvertedFormattedDistance = String.valueOf(df.format(TurfConversion.convertLength(
                                response.body().routes().get(0).distance(), TurfConstants.UNIT_METERS,
                                TurfConstants.UNIT_KILOMETERS)));

                        int x = response.body().routes().get(0).duration().intValue();



                        String finalConvertedFormattedDuration =
                                String.valueOf( formatSeconds(x));






                        // Set the distance for each location object in the list of locations
                        if (listIndex != null) {
                            listOfIndividualLocationsRestaurant.get(listIndex).setDistance(finalConvertedFormattedDistance);
                            // Refresh the displayed recyclerview when the location's distance is set
                            styleRvAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                Toast.makeText(getContext(), R.string.failure_to_retrieve, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void repositionMapCamera(Point newTarget) {
        CameraPosition newCameraPosition = new CameraPosition.Builder()
                .target(new LatLng(newTarget.latitude(), newTarget.longitude()))
                .build();
        mapboxMap2.animateCamera(CameraUpdateFactory.newCameraPosition(newCameraPosition), CAMERA_MOVEMENT_SPEED_IN_MILSECS);
    }

    public static String formatSeconds(int timeInSeconds)
    {
        int hours = timeInSeconds / 3600;
        int secondsLeft = timeInSeconds - hours * 3600;
        int minutes = secondsLeft / 60;
        int seconds = secondsLeft - minutes * 60;

        String formattedTime = "";
        if (hours < 10)
            formattedTime += "0";
        formattedTime += hours + ":";

        if (minutes < 10)
            formattedTime += "0";
        formattedTime += minutes + ":";

        if (seconds < 10)
            formattedTime += "0";
        formattedTime += seconds ;

        return formattedTime;
    }

    private void addMockDeviceLocationMarkerToMap() {
        // Add the fake user location marker to the map
        Style style = mapboxMap2.getStyle();
        if (style != null) {
            // Add the icon image to the map
            style.addImage("mock-device-location-icon-id", customThemeManager.getMockLocationIcon());

            style.addSource(new GeoJsonSource("mock-device-location-source-id", Feature.fromGeometry(
                    Point.fromLngLat(MOCK_DEVICE_LOCATION_LAT_LNG.getLongitude(), MOCK_DEVICE_LOCATION_LAT_LNG.getLatitude()))));

            style.addLayer(new SymbolLayer("mock-device-location-layer-id",
                    "mock-device-location-source-id").withProperties(
                    iconImage("mock-device-location-icon-id"),
                    iconAllowOverlap(true),
                    iconIgnorePlacement(true)
            ));
        } else {
            throw new IllegalStateException("Style isn't ready yet.");
        }
    }

    private void getFeatureCollectionFromJson(String object) throws IOException {
        try {
            // Use fromJson() method to convert the GeoJSON file into a usable FeatureCollection object
            featureCollection = FeatureCollection.fromJson(loadGeoJsonFromAsset(object));

        } catch (Exception exception) {
            Log.e("MapActivity", "getFeatureCollectionFromJson: " + exception);
        }
    }

    private String loadGeoJsonFromAsset(String filename) {
        try {
            // Load the GeoJSON file from the local asset folder
            InputStream is = getContext().getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            return new String(buffer, "UTF-8");
        } catch (Exception exception) {
            Log.e("MapActivity", "Exception Loading GeoJSON: " + exception.toString());
            exception.printStackTrace();
            return null;
        }
    }

    private void setUpRecyclerViewOfLocationCards(int chosenTheme) {
        // Initialize the recyclerview of location cards and a custom class for automatic card scrolling
        locationsRecyclerView = v.findViewById(R.id.map_layout_rv);
        locationsRecyclerView.setHasFixedSize(true);
        locationsRecyclerView.setLayoutManager(new LinearLayoutManagerWithSmoothScroller(getContext()));
        styleRvAdapter = new LocationRecyclerViewAdapter(listOfIndividualLocationsRestaurant,
                getContext(), this, chosenTheme);
        locationsRecyclerView.setAdapter(styleRvAdapter);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(locationsRecyclerView);
    }

    private void drawNavigationPolylineRoute(DirectionsRoute route) {
        // Retrieve and update the source designated for showing the store location icons
        GeoJsonSource source = mapboxMap2.getStyle().getSourceAs("navigation-route-source-id");
        if (source != null) {
            source.setGeoJson(FeatureCollection.fromFeature(Feature.fromGeometry(
                    LineString.fromPolyline(route.geometry(), PRECISION_6))));
        }
    }

    private void initNavigationPolylineLineLayer() {
        // Create and add the GeoJsonSource to the map
        GeoJsonSource navigationLineLayerGeoJsonSource = new GeoJsonSource("navigation-route-source-id");
        mapboxMap2.getStyle().addSource(navigationLineLayerGeoJsonSource);

        // Create and add the LineLayer to the map to show the navigation route line
        LineLayer navigationRouteLineLayer = new LineLayer("navigation-route-layer-id",
                navigationLineLayerGeoJsonSource.getId());
        navigationRouteLineLayer.withProperties(
                lineColor(customThemeManager.getNavigationLineColor()),
                lineWidth(NAVIGATION_LINE_WIDTH)
        );
        mapboxMap2.getStyle().addLayerBelow(navigationRouteLineLayer, "store-location-layer-id");
    }

    // Add the mapView's lifecycle to the activity's lifecycle methods
    @Override
    public void onResume() {
        super.onResume();
        getLocation();
        mapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    private boolean deviceHasInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getContext().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }



    /**
     * Custom class which creates marker icons and colors based on the selected theme
     */
    class CustomThemeManager {
        private int selectedTheme;
        private Context context;
        private Bitmap unselectedMarkerIcon;
        private Bitmap selectedMarkerIcon;
        private Bitmap mockLocationIcon;
        private int navigationLineColor;
        private String mapStyle;

        CustomThemeManager(int selectedTheme, Context context) {
            this.selectedTheme = selectedTheme;
            this.context = context;
            initializeTheme();
        }

        private void initializeTheme() {
            switch (selectedTheme) {
                case R.style.AppTheme_Neutral:
                    mapStyle = Style.MAPBOX_STREETS;
                    navigationLineColor = getResources().getColor(R.color.navigationRouteLine_neutral);
                    unselectedMarkerIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.restaurant_icon);
                    selectedMarkerIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.restaurant_icon);
                    mockLocationIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.neutral_orange_user_location);
                    break;

            }
        }

        public Bitmap getUnselectedMarkerIcon() {
            return unselectedMarkerIcon;
        }

        public Bitmap getMockLocationIcon() {
            return mockLocationIcon;
        }

        public Bitmap getSelectedMarkerIcon() {
            return selectedMarkerIcon;
        }

        int getNavigationLineColor() {
            return navigationLineColor;
        }

        public String getMapStyle() {
            return mapStyle;
        }
    }
}