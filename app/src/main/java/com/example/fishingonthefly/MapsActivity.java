package com.example.fishingonthefly;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleMap.OnPolylineClickListener, GoogleMap.OnPolygonClickListener {

    /*
    Variables for google map, data received from the MainActivity
     */
    GoogleMap mMap;
    double lat;
    double lon;
    int view;
    int river;


    /*
        Polyline Click ID'S, variables when the river is clicked
     */

    //Stocked Rivers
    String muskieID;
    String southBranchID;
    String flatbrookID;
    String flatbrookTopID;
    String rockawayID;

    //TCA
    String flatbrookTCAID;

    //Wild Trout Streams
    String parkerBrookID;

    //Button to change map to elevation or Satelite View
    private Button changeView;
    boolean elevationView = false;

    //Variable to get current location of the user
    private FusedLocationProviderClient fusedLocationClient;

    private TextView waterTemp;

    //Button to Launch Shopping Page
    private Button shoping;




    /*
        onCreate()
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        /*
        If data is received from MainActivity it is store to get the river information,
        zoom in on river when a river is clicked on the home page
         */

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            lat = bundle.getDouble("latitude");
            lon = bundle.getDouble("longitude");
            view = bundle.getInt("view");
            river = bundle.getInt("id");

        }



        /*
            Button to change the view between Elevation and Satelite View
         */
        changeView = (Button) findViewById(R.id.btnMap);

        changeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (elevationView == false) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                    changeView.setText("Satellite View");
                    elevationView = true;
                } else if (elevationView == true) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    changeView.setText("Elevation View");
                    elevationView = false;
                }

            }
        });

        /*
            Button to Launch Shopping Page when clicked through
            the river information page
         */

        shoping = findViewById(R.id.btnShop);

        shoping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openShoppingActivity();
            }
        });






    }

    /*
        Map Start Up
     */
    @SuppressLint("SetTextI18n")
    @Override
    public void onMapReady(GoogleMap googleMap ) {
        //Declare google map vatiable
        mMap = googleMap;
        //Default to Satelite Map View
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        //Set how far the user can zoom in and out
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMinZoomPreference(1.0f);
        mMap.setMaxZoomPreference(20.0f);


        /*
            Resets the layout when the user clicks on the map
            after selecting a river on the home page or by clicking
            on a river on the map.
         */
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
        {
            @Override
            public void onMapClick(LatLng arg0)
            {
                TextView riverInfo = findViewById(R.id.txtClickedRiverBackground);

                LinearLayout mapLayout = findViewById(R.id.llMapLayout);

                LinearLayout horizontal = findViewById(R.id.llMapKey);

                //Set Map back to full size
                LinearLayout.LayoutParams mapParams = new LinearLayout.LayoutParams(1080, 1700);
                mapLayout.setLayoutParams(mapParams);

                LinearLayout.LayoutParams mapKeyParams = new LinearLayout.LayoutParams(1080,320);
                horizontal.setLayoutParams(mapKeyParams);

                //Resets the selected river out of view
                ScrollView scrollView = findViewById(R.id.svSelectedRiver);
                scrollView.scrollTo(0,0);
                ScrollView wildStreamScroll = findViewById(R.id.scrollWildStreamInfo);
                LinearLayout verticalView = findViewById(R.id.llVertical2);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(1, 1);
                scrollView.setLayoutParams(params);
                wildStreamScroll.setLayoutParams(params);



            }
        });

        Log.d("tag", "Value: " + lat);
        Log.d("tag", "Value: " + lon);
        Log.d("tag", "Value: " + view);
        Log.d("tag", "Value: " + river);





        /*
            If River is selected on the home page it passes the information for camera
            and river information
         */
        if (lat > 0){

             /*
                Layout for the selected river from the home page
             */

            ScrollView selectedRiver = findViewById(R.id.svSelectedRiver);

            LinearLayout vertical = findViewById(R.id.llVertical2);

            LinearLayout mapKey = findViewById(R.id.llMapKey);

            LinearLayout mapLayout = findViewById(R.id.llMapLayout);

            LinearLayout.LayoutParams selectedRiverParams = new LinearLayout.LayoutParams(1080, 1300);
            selectedRiver.setLayoutParams(selectedRiverParams);


            LinearLayout.LayoutParams mapKeyParams = new LinearLayout.LayoutParams(1,1);
            mapKey.setLayoutParams(mapKeyParams);

            LinearLayout.LayoutParams mapParams = new LinearLayout.LayoutParams(1080, 900);
            mapLayout.setLayoutParams(mapParams);

            updateCamera(lat, lon ,view);

            //setInfo(riverID);

            TextView troutSpecies = findViewById(R.id.txtTroutSpecies);

            //Decides which river was selected on the home page
            if(river == 1){
                TextView riverTitle = findViewById(R.id.txtClickedRiverTitle);
                riverTitle.setText("Musky River NJ");

                TextView clickRiverBackground = findViewById(R.id.txtClickedRiverBackground);
                clickRiverBackground.setBackgroundResource(R.drawable.muskie2);

                TextView rainbowTroutImage = findViewById(R.id.txtRainbowTroutImage);
                rainbowTroutImage.setBackgroundResource(R.drawable.rainbow2);

                TextView brownTroutImage = findViewById(R.id.txtBrownTroutImage);
                brownTroutImage.setBackgroundResource(R.drawable.brown_trout3);

                TextView brookTroutImage = findViewById(R.id.txtBrookTroutImage);
                brookTroutImage.setBackgroundResource(R.drawable.brook_trout3);

                TextView riverTemp = findViewById(R.id.txtTemp);

                /*
                try {
                    Document document = Jsoup.connect("https://dashboard.waterdata.usgs.gov/app/nwd/?aoi=state-nj").get();
                    Elements waterTemp = document.select("Element ID Goes Here");
                    riverTemp.setText(waterTemp.text());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                 */



                /*
                    Drops a marker on every public fishing access point
                    on the river.
                 */

                LatLng hopatcongStatePark = new LatLng(40.916210, -74.663690);
                mMap.addMarker(new MarkerOptions()
                        .position(hopatcongStatePark)
                        .title("Fishing Access Point: " +
                                "Hopatcong State Park"));

                LatLng seberPark = new LatLng(40.8553475, -74.8135556);
                mMap.addMarker(new MarkerOptions()
                        .position(seberPark)
                        .title("Fishing Access Point: " +
                                "Seber's Park"));

                LatLng allamuchyStatePark = new LatLng(40.922237, -74.739913);
                mMap.addMarker(new MarkerOptions()
                        .position(allamuchyStatePark)
                        .title("Fishing Access Point: " +
                                "Allamuchy State Park"));
            }

                else if(river == 2){
                TextView riverTitle = findViewById(R.id.txtClickedRiverTitle);
                riverTitle.setText("Raritan River South Branch");

                TextView clickRiverBackground = findViewById(R.id.txtClickedRiverBackground);
                clickRiverBackground.setBackgroundResource(R.drawable.southbranch);

                TextView rainbowTroutImage = findViewById(R.id.txtRainbowTroutImage);
                rainbowTroutImage.setBackgroundResource(R.drawable.rainbow4);

                TextView brownTroutImage = findViewById(R.id.txtBrownTroutImage);
                brownTroutImage.setBackgroundResource(R.drawable.brown_trout2);

                TextView brookTroutImage = findViewById(R.id.txtBrookTroutImage);
                brookTroutImage.setBackgroundResource(R.drawable.brook_trout2);
            }

            else if(river == 3){
                TextView riverTitle = findViewById(R.id.txtClickedRiverTitle);
                riverTitle.setText("Big Flatbrook River NJ");

                TextView clickRiverBackground = findViewById(R.id.txtClickedRiverBackground);
                clickRiverBackground.setBackgroundResource(R.drawable.bigflat);

                TextView riverTemp = findViewById(R.id.txtTemp);

                LatLng point1 = new LatLng(41.232584, -74.725030);
                mMap.addMarker(new MarkerOptions()
                        .position(point1)
                        .title("Fishing Access Point: " +
                                "Crigger Rd. Bridge, Stokes State Forest"));

                LatLng point2 = new LatLng(41.225205, -74.760349);
                mMap.addMarker(new MarkerOptions()
                        .position(point2)
                        .title("Fishing Access Point: " +
                                "Stokes State Forest - Lk. Ocquittunk Campground"));

                LatLng point3 = new LatLng(41.208499, -74.803565);
                mMap.addMarker(new MarkerOptions()
                        .position(point3)
                        .title("Fishing Access Point: " +
                                "\tStokes State Forest - Flatbrookville Rd."));

                LatLng point4 = new LatLng(41.208499, -74.803565);
                mMap.addMarker(new MarkerOptions()
                        .position(point4)
                        .title("Fishing Access Point: " +
                                "\tStokes State Forest - RT. 206 Bridge."));

                LatLng point9 = new LatLng(41.200065, -74.815510);
                mMap.addMarker(new MarkerOptions()
                        .position(point9)
                        .title("Fishing Access Point: " +
                                "\tFlatbrook-Roy WMA - Three Bridges Parking Lot"));

                LatLng point10 = new LatLng(41.185234, -74.850239);
                mMap.addMarker(new MarkerOptions()
                        .position(point10)
                        .title("Fishing Access Point: " +
                                "\tCounty Rt. 560 bridge (Schaffers bridge) & adjacent roads"));

                LatLng point8 = new LatLng(41.178613, -74.861450);
                mMap.addMarker(new MarkerOptions()
                        .position(point8)
                        .title("Fishing Access Point: " +
                                "\tFlatbrook-Roy WMA - parking lot at Roy Bridge"));

                LatLng point5 = new LatLng(41.157291, -74.877329);
                mMap.addMarker(new MarkerOptions()
                        .position(point5)
                        .title("Fishing Access Point: " +
                                "\tMain St. Bridge"));

                LatLng point6 = new LatLng(41.128850, -74.910375);
                mMap.addMarker(new MarkerOptions()
                        .position(point6)
                        .title("Fishing Access Point: " +
                                "\tWalpack Valley Campground"));

                LatLng point7 = new LatLng(41.101299, -74.962548);
                mMap.addMarker(new MarkerOptions()
                        .position(point7)
                        .title("Fishing Access Point: " +
                                "\t\tAlong Rt. 615, between Walpack & Flatbrookville Rd. bridge"));


                /*


                try {
                    Document document = Jsoup.connect("https://dashboard.waterdata.usgs.gov/app/nwd/?aoi=state-nj").timeout(6000).get();
                    Elements waterTemp = document.getElementsByClass("me-3");
                    riverTemp.setText(waterTemp.text());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                */



            }

        }
        else {

            /*
                If the user does not select a river on the home page
                and loads the map directly, the app will see if the
                user gave permission to share current location. After
                the permission is granted the camera zooms over the
                coordinates gathered from the user.
             */


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                if(getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(MapsActivity.this, "Permission granted", Toast.LENGTH_SHORT);

                    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

                    fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    // Got last known location. In some rare situations this can be null.
                                    if (location != null) {

                                        lat = location.getLatitude();
                                        lon = location.getLatitude();

                                        currentLocation(lat, lon, 1);

                                    }
                                }
                            });


                }

                else{
                    requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
            }
        }



        /*
            After the map is loaded with either a selected river
            or directly calling the map this section of code is used
            to highlight the rivers and store the id's to load information
            when the polyline is clicked directly.
         */


        //Stocked Trout Rivers

        //Muskie

                Polyline muskieStart = mMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .width(20)
                .color(Color.BLUE)
                .add(
                        new LatLng(40.917646, -74.664273),
                        new LatLng(40.916933, -74.665603),
                        new LatLng(40.915376, -74.668049),
                        new LatLng(40.914711, -74.669036),
                        new LatLng(40.914387, -74.670302),
                        new LatLng(40.913576, -74.670903),
                        new LatLng(40.913463, -74.672105),
                        new LatLng(40.912665, -74.672524),
                        new LatLng(40.912377, -74.673109),
                        new LatLng(40.911954, -74.673338),
                        new LatLng(40.911509, -74.674230),
                        new LatLng(40.911426, -74.674860),
                        new LatLng(40.910970, -74.675244),
                        new LatLng(40.910628, -74.677024),
                        new LatLng(40.909949, -74.677577),
                        new LatLng(40.909667, -74.678023),
                        new LatLng(40.909489, -74.678781),
                        new LatLng(40.909249, -74.679245),
                        new LatLng(40.909013, -74.679558),
                        new LatLng(40.909107, -74.680105),
                        new LatLng(40.909004, -74.680451),
                        new LatLng(40.909154, -74.681110),
                        new LatLng(40.909101, -74.681956),
                        new LatLng(40.909255, -74.682169),
                        new LatLng(40.909989, -74.682322),
                        new LatLng(40.910111, -74.682502),
                        new LatLng(40.910220, -74.683643),
                        new LatLng(40.910085, -74.684119),
                        new LatLng(40.901638, -74.704695),
                        new LatLng(40.901693, -74.705633),
                        new LatLng(40.901504, -74.706265),
                        new LatLng(40.901072, -74.708487),
                        new LatLng(40.900092, -74.709513),
                        new LatLng(40.899928, -74.709954),
                        new LatLng(40.899809, -74.710434),
                        new LatLng(40.899272, -74.711361),
                        new LatLng(40.899317, -74.712137),
                        new LatLng(40.899928, -74.713676),
                        new LatLng(40.901171, -74.714235),
                        new LatLng(40.901916, -74.714807),
                        new LatLng(40.902289, -74.714642),
                        new LatLng(40.902647, -74.713991),
                        new LatLng(40.903199, -74.713663),
                        new LatLng(40.903703, -74.712460),
                        new LatLng(40.903966, -74.712450),
                        new LatLng(40.904271, -74.712399),
                        new LatLng(40.904687, -74.712240),
                        new LatLng(40.905192, -74.712217),
                        new LatLng(40.905865, -74.712375),
                        new LatLng(40.906353, -74.712641),
                        new LatLng(40.906568, -74.712791),
                        new LatLng(40.907513, -74.712885),
                        new LatLng(40.907959, -74.713030),
                        new LatLng(40.908320, -74.713330),
                        new LatLng(40.908401, -74.713556),
                        new LatLng(40.908447, -74.714274),
                        new LatLng(40.909424, -74.715165),
                        new LatLng(40.909909, -74.715616),
                        new LatLng(40.910127, -74.715947),
                        new LatLng(40.910297, -74.716368),
                        new LatLng(40.910399, -74.716769),
                        new LatLng(40.910512, -74.717083),
                        new LatLng(40.910648, -74.717253),
                        new LatLng(40.910765, -74.717372),
                        new LatLng(40.911161, -74.717675),
                        new LatLng(40.911419, -74.717855),
                        new LatLng(40.911885, -74.718148),
                        new LatLng(40.912495, -74.718696),
                        new LatLng(40.912731, -74.719005),
                        new LatLng(40.912874, -74.719290),
                        new LatLng(40.913123, -74.719652),
                        new LatLng(40.913150, -74.719755),
                        new LatLng(40.913193, -74.720210),
                        new LatLng(40.913293, -74.720416),
                        new LatLng(40.914177, -74.721476),
                        new LatLng(40.914599, -74.721911),
                        new LatLng(40.914681, -74.722210),
                        new LatLng(40.914707, -74.722456),
                        new LatLng(40.914787, -74.722798),
                        new LatLng(40.914902, -74.723595),
                        new LatLng(40.914887, -74.724539),
                        new LatLng(40.915194, -74.725064),
                        new LatLng(40.915437, -74.725688),
                        new LatLng(40.915490, -74.725804),
                        new LatLng(40.915598, -74.725977),
                        new LatLng(40.915638, -74.726369),
                        new LatLng(40.915776, -74.727093),
                        new LatLng(40.916042, -74.727439),
                        new LatLng(40.916075, -74.728053),
                        new LatLng(40.916374, -74.728662),
                        new LatLng(40.917335, -74.728833),
                        new LatLng(40.918124, -74.729215),
                        new LatLng(40.918956, -74.729911),
                        new LatLng(40.919374, -74.730099),
                        new LatLng(40.919831, -74.730613),
                        new LatLng(40.919986, -74.730892),
                        new LatLng(40.920452, -74.731212),
                        new LatLng(40.920900, -74.731948),
                        new LatLng(40.920589, -74.732851),
                        new LatLng(40.919888, -74.734760),
                        new LatLng(40.919632, -74.734782),
                        new LatLng(40.919628, -74.735307),
                        new LatLng(40.919762, -74.735458),
                        new LatLng(40.918899, -74.737163),
                        new LatLng(40.919575, -74.739518),
                        new LatLng(40.919657, -74.740548),
                        new LatLng(40.920378, -74.741080),
                        new LatLng(40.919940, -74.743992),
                        new LatLng(40.919507, -74.745076),
                        new LatLng(40.919981, -74.747833),
                        new LatLng(40.918639, -74.749475),
                        new LatLng(40.917208, -74.751658),
                        new LatLng(40.915493, -74.751336),
                        new LatLng(40.914797, -74.753657),
                        new LatLng(40.913656, -74.755269),
                        new LatLng(40.913277, -74.757342),
                        new LatLng(40.912940, -74.758222),
                        new LatLng(40.913378, -74.759466),
                        new LatLng(40.913136, -74.760209),
                        new LatLng(40.912036, -74.761148),
                        new LatLng(40.913133, -74.761868),
                        new LatLng(40.913328, -74.762783),
                        new LatLng(40.912086, -74.764332),
                        new LatLng(40.912074, -74.765279),
                        new LatLng(40.912521, -74.766101),
                        new LatLng(40.911796, -74.768385),
                        new LatLng(40.910407, -74.769958),
                        new LatLng(40.909136, -74.770854),
                        new LatLng(40.909023, -74.771890),
                        new LatLng(40.907311, -74.773338),
                        new LatLng(40.905966, -74.775294),
                        new LatLng(40.905141, -74.776694),
                        new LatLng(40.906398, -74.778458),
                        new LatLng(40.906342, -74.779143),
                        new LatLng(40.904905, -74.779436),
                        new LatLng(40.903354, -74.779628),
                        new LatLng(40.902373, -74.782900),
                        new LatLng(40.896140, -74.790594),
                        new LatLng(40.892030, -74.790306),
                        new LatLng(40.889010, -74.793501),
                        new LatLng(40.887668, -74.799360),
                        new LatLng(40.886863, -74.800647),
                        new LatLng(40.886359, -74.802423),
                        new LatLng(40.885185, -74.804553),
                        new LatLng(40.883591, -74.806351),
                        new LatLng(40.882299, -74.806506),
                        new LatLng(40.881762, -74.807283),
                        new LatLng(40.881074, -74.807372),
                        new LatLng(40.879497, -74.806861),
                        new LatLng(40.878423, -74.806928),
                        new LatLng(40.876007, -74.805996),
                        new LatLng(40.875033, -74.805596),
                        new LatLng(40.873422, -74.805996),
                        new LatLng(40.871610, -74.807194),
                        new LatLng(40.870402, -74.807682),
                        new LatLng(40.869596, -74.808193),
                        new LatLng(40.867616, -74.806795),
                        new LatLng(40.866592, -74.805197),
                        new LatLng(40.866005, -74.804664),
                        new LatLng(40.864461, -74.801979),
                        new LatLng(40.861037, -74.803754),
                        new LatLng(40.861423, -74.804664),
                        new LatLng(40.860919, -74.805108),
                        new LatLng(40.861070, -74.807860),
                        new LatLng(40.860315, -74.808637),
                        new LatLng(40.860164, -74.809768),
                        new LatLng(40.858586, -74.810146),
                        new LatLng(40.856941, -74.809369),
                        new LatLng(40.855599, -74.810279),
                        new LatLng(40.854440, -74.810079),
                        new LatLng(40.851016, -74.814939),
                        new LatLng(40.850815, -74.817403),
                        new LatLng(40.849841, -74.818579),
                        new LatLng(40.849455, -74.821442),
                        new LatLng(40.845040, -74.822507),
                        new LatLng(40.844343, -74.823820),
                        new LatLng(40.843726, -74.823627),
                        new LatLng(40.841746, -74.820816),
                        new LatLng(40.839943, -74.819689),
                        new LatLng(40.838969, -74.819560),
                        new LatLng(40.837817, -74.818209),
                        new LatLng(40.836745, -74.818380),
                        new LatLng(40.836063, -74.819689),
                        new LatLng(40.836140, -74.820863),
                        new LatLng(40.835078, -74.822111),
                        new LatLng(40.834184, -74.824095),
                        new LatLng(40.832498, -74.826212),
                        new LatLng(40.832312, -74.827527),
                        new LatLng(40.831165, -74.828285),
                        new LatLng(40.828214, -74.831628),
                        new LatLng(40.826578, -74.831383),
                        new LatLng(40.825432, -74.829956),
                        new LatLng(40.824791, -74.830335),
                        new LatLng(40.823138, -74.833545),
                        new LatLng(40.822649, -74.833990),
                        new LatLng(40.821435, -74.835060),
                        new LatLng(40.820035, -74.834637),
                        new LatLng(40.819107, -74.835194),
                        new LatLng(40.816425, -74.839451),
                        new LatLng(40.814367, -74.841345),
                        new LatLng(40.813541, -74.841835),
                        new LatLng(40.811854, -74.842905),
                        new LatLng(40.811112, -74.844086),
                        new LatLng(40.811095, -74.846828),
                        new LatLng(40.810066, -74.846471),
                        new LatLng(40.808295, -74.847073),
                        new LatLng(40.806709, -74.848187),
                        new LatLng(40.804752, -74.849836),
                        new LatLng(40.803639, -74.851508),
                        new LatLng(40.804103, -74.854711),
                        new LatLng(40.803410, -74.856017),
                        new LatLng(40.801415, -74.858823),
                        new LatLng(40.801119, -74.859824),
                        new LatLng(40.801341, -74.860312),
                        new LatLng(40.803466, -74.861386),
                        new LatLng(40.803613, -74.861972),
                        new LatLng(40.802856, -74.864852),
                        new LatLng(40.802006, -74.865584),
                        new LatLng(40.800325, -74.865657),
                        new LatLng(40.799143, -74.866023),
                        new LatLng(40.797498, -74.866731),
                        new LatLng(40.796427, -74.867170),
                        new LatLng(40.794856, -74.869855),
                        new LatLng(40.794450, -74.872735),
                        new LatLng(40.794505, -74.873540),
                        new LatLng(40.793914, -74.874638),
                        new LatLng(40.793341, -74.876396),
                        new LatLng(40.792584, -74.876688),
                        new LatLng(40.791031, -74.879080),
                        new LatLng(40.790625, -74.880886),
                        new LatLng(40.791419, -74.883839),
                        new LatLng(40.791290, -74.884498),
                        new LatLng(40.788888, -74.889087),
                        new LatLng(40.788038, -74.889575),
                        new LatLng(40.787724, -74.889916),
                        new LatLng(40.786301, -74.892796),
                        new LatLng(40.785488, -74.894017),
                        new LatLng(40.784157, -74.895310),
                        new LatLng(40.782716, -74.900411),
                        new LatLng(40.780849, -74.904340),
                        new LatLng(40.780184, -74.904413),
                        new LatLng(40.778207, -74.903144),
                        new LatLng(40.778207, -74.903144),
                        new LatLng(40.778041, -74.903049)));
                muskieID = muskieStart.getId();



        Polyline muskieEnd = mMap.addPolyline(new PolylineOptions()
            .clickable(true)
            .width(20)
            .color(Color.BLUE)
            .add(
                    new LatLng(40.767283, -74.911723),
                    new LatLng(40.766396, -74.913053),
                    new LatLng(40.765019, -74.913968),
                    new LatLng(40.764123, -74.915189),
                    new LatLng(40.763392, -74.915250),
                    new LatLng(40.762496, -74.915237),
                    new LatLng(40.761932, -74.915518),
                    new LatLng(40.760749, -74.917458),
                    new LatLng(40.759437, -74.918190),
                    new LatLng(40.758438, -74.920509),
                    new LatLng(40.757551, -74.921778),
                    new LatLng(40.756488, -74.922852),
                    new LatLng(40.755878, -74.924914),
                    new LatLng(40.755212, -74.926232),
                    new LatLng(40.754242, -74.927428),
                    new LatLng(40.754118, -74.927488),
                    new LatLng(40.753419, -74.927804),
                    new LatLng(40.752405, -74.928239),
                    new LatLng(40.752172, -74.928925),
                    new LatLng(40.752160, -74.929405),
                    new LatLng(40.751301, -74.930920),
                    new LatLng(40.750781, -74.931286),
                    new LatLng(40.749606, -74.931086),
                    new LatLng(40.749131, -74.931876),
                    new LatLng(40.748386, -74.931919),
                    new LatLng(40.748206, -74.933898),
                    new LatLng(40.746739, -74.934622),
                    new LatLng(40.746125, -74.934806),
                    new LatLng(40.745183, -74.935984),
                    new LatLng(40.744896, -74.936676),
                    new LatLng(40.743807, -74.937130),
                    new LatLng(40.742070, -74.939368),
                    new LatLng(40.740809, -74.940471),
                    new LatLng(40.739973, -74.940774),
                    new LatLng(40.738892, -74.941012),
                    new LatLng(40.738425, -74.941542),
                    new LatLng(40.737950, -74.942926),
                    new LatLng(40.736975, -74.943736),
                    new LatLng(40.735517, -74.945520),
                    new LatLng(40.734427, -74.946742),
                    new LatLng(40.732838, -74.948018),
                    new LatLng(40.731945, -74.948440),
                    new LatLng(40.731166, -74.949196),
                    new LatLng(40.730584, -74.949456),
                    new LatLng(40.728700, -74.950840),
                    new LatLng(40.727668, -74.951986),
                    new LatLng(40.592341, -75.188834)));



        mMap.setOnPolylineClickListener(this);



        //South Branch Raritan
        Polyline southBranchRaritan = mMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .width(20)
                .color(Color.BLUE)
                .add(
                        new LatLng(40.863471, -74.751900),
                        new LatLng(40.862939, -74.754514),
                        new LatLng(40.859277, -74.761201),
                        new LatLng(40.853146, -74.766433),
                        new LatLng(40.849551, -74.757352),
                        new LatLng(40.843137, -74.752446),
                        new LatLng(40.838145, -74.745193),
                        new LatLng(40.834265, -74.738586),
                        new LatLng(40.829331, -74.723868),
                        new LatLng(40.819876, -74.723169),
                        new LatLng(40.811570, -74.733480),
                        new LatLng(40.803250, -74.741475),
                        new LatLng(40.800389, -74.740883),
                        new LatLng(40.796934, -74.746004),
                        new LatLng(40.793928, -74.756072),
                        new LatLng(40.794680, -74.766854),
                        new LatLng(40.790784, -74.769855),
                        new LatLng(40.786853, -74.773916),
                        new LatLng(40.785142, -74.780109),
                        new LatLng(40.779966, -74.790923),
                        new LatLng(40.774355, -74.796629),
                        new LatLng(40.772730, -74.805173),
                        new LatLng(40.770373, -74.811692),
                        new LatLng(40.764525, -74.812754),
                        new LatLng(40.761257, -74.821250),
                        new LatLng(40.749341, -74.830277),
                        new LatLng(40.748238, -74.827992),
                        new LatLng(40.744754, -74.831590),
                        new LatLng(40.743950, -74.831421),
                        new LatLng(40.744397, -74.825306),
                        new LatLng(40.742406, -74.824072),
                        new LatLng(40.735968, -74.827194),
                        new LatLng(40.733870, -74.830219),
                        new LatLng(40.727480, -74.832902),
                        new LatLng(40.725016, -74.833556),
                        new LatLng(40.723862, -74.835981),
                        new LatLng(40.721040, -74.836828),
                        new LatLng(40.719324, -74.841764),
                        new LatLng(40.718145, -74.841989),
                        new LatLng(40.715096, -74.849907),
                        new LatLng(40.713290, -74.851355),
                        new LatLng(40.708159, -74.858587),
                        new LatLng(40.704873, -74.864852),
                        new LatLng(40.703450, -74.865035),
                        new LatLng(40.701668, -74.870710),
                        new LatLng(40.697691, -74.872319),
                        new LatLng(40.695682, -74.871719),
                        new LatLng(40.695397, -74.874401),
                        new LatLng(40.690768, -74.878360),
                        new LatLng(40.689337, -74.880892),
                        new LatLng(40.687108, -74.877727),
                        new LatLng(40.682584, -74.878381),
                        new LatLng(40.681901, -74.877255),
                        new LatLng(40.680837, -74.876819),
                        new LatLng(40.677636, -74.879308),
                        new LatLng(40.677181, -74.883335),
                        new LatLng(40.672585, -74.887803),
                        new LatLng(40.669154, -74.887679),
                        new LatLng(40.668353, -74.888176),
                        new LatLng(40.667224, -74.891051),
                        new LatLng(40.665749, -74.891651),
                        new LatLng(40.664943, -74.892299),
                        new LatLng(40.664519, -74.896512),
                        new LatLng(40.663625, -74.897588),
                        new LatLng(40.660853, -74.899416),
                        new LatLng(40.661041, -74.901884),
                        new LatLng(40.657923, -74.904167),
                        new LatLng(40.656281, -74.908201),
                        new LatLng(40.655449, -74.909690),
                        new LatLng(40.652995, -74.906097),
                        new LatLng(40.651332, -74.902525),
                        new LatLng(40.649537, -74.903277),
                        new LatLng(40.649046, -74.907601),
                        new LatLng(40.646210, -74.907973),
                        new LatLng(40.644483, -74.908083),
                        new LatLng(40.642694, -74.910538),
                        new LatLng(40.639078, -74.909021),
                        new LatLng(40.636951, -74.912825),
                        new LatLng(40.632513, -74.910694),
                        new LatLng(40.629751, -74.912218),
                        new LatLng(40.623985, -74.908976),
                        new LatLng(40.619205, -74.911388),
                        new LatLng(40.616999, -74.911645),
                        new LatLng(40.616916, -74.913806),
                        new LatLng(40.616063, -74.914514),
                        new LatLng(40.615136, -74.913671),
                        new LatLng(40.611902, -74.913134),
                        new LatLng(40.611494, -74.907151),
                        new LatLng(40.609584, -74.907066),
                        new LatLng(40.604679, -74.901728)));
        southBranchID = southBranchRaritan.getId();

        //flatbrook Start
        Polyline flatbrook = mMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .width(20)
                .color(Color.BLUE)
                .add(
                        new LatLng(41.239054, -74.735797),
                        new LatLng(41.239151, -74.736546),
                        new LatLng(41.238973, -74.736685),
                        new LatLng(41.239554, -74.738831),
                        new LatLng(41.240700, -74.741234),
                        new LatLng(41.240579, -74.741792),
                        new LatLng(41.238320, -74.743080),
                        new LatLng(41.237973, -74.744131),
                        new LatLng(41.237586, -74.744506),
                        new LatLng(41.236190, -74.746288),
                        new LatLng(41.235601, -74.746776),
                        new LatLng(41.234955, -74.746894),
                        new LatLng(41.233870, -74.748010),
                        new LatLng(41.232797, -74.749490),
                        new LatLng(41.231042, -74.751454),
                        new LatLng(41.231038, -74.751862),
                        new LatLng(41.230546, -74.752028),
                        new LatLng(41.229610, -74.752921),
                        new LatLng(41.228520, -74.753855),
                        new LatLng(41.228811, -74.754944),
                        new LatLng(41.228811, -74.756907),
                        new LatLng(41.228169, -74.757578),
                        new LatLng(41.227076, -74.757765),
                        new LatLng(41.226487, -74.758736),
                        new LatLng(41.226148, -74.760094),
                        new LatLng(41.225107, -74.760662),
                        new LatLng(41.225035, -74.761284),
                        new LatLng(41.225353, -74.762282),
                        new LatLng(41.225515, -74.763017),
                        new LatLng(41.225999, -74.764101),
                        new LatLng(41.226007, -74.764444),
                        new LatLng(41.226257, -74.764648),
                        new LatLng(41.226414, -74.765066),
                        new LatLng(41.226406, -74.766295),
                        new LatLng(41.226176, -74.766751),
                        new LatLng(41.225285, -74.767952),
                        new LatLng(41.224256, -74.768382),
                        new LatLng(41.224224, -74.768918),
                        new LatLng(41.223747, -74.769975),
                        new LatLng(41.223889, -74.770484),
                        new LatLng(41.223925, -74.771353),
                        new LatLng(41.223481, -74.772340),
                        new LatLng(41.223053, -74.773805),
                        new LatLng(41.222339, -74.774964),
                        new LatLng(41.221742, -74.776037),
                        new LatLng(41.221859, -74.776423),
                        new LatLng(41.222037, -74.776589),
                        new LatLng(41.222255, -74.777447),
                        new LatLng(41.221795, -74.777877),
                        new LatLng(41.221117, -74.778333),
                        new LatLng(41.220080, -74.779212),
                        new LatLng(41.218538, -74.779936),
                        new LatLng(41.217699, -74.780956),
                        new LatLng(41.216065, -74.782431),
                        new LatLng(41.215233, -74.783552),
                        new LatLng(41.215282, -74.785344),
                        new LatLng(41.215306, -74.786883),
                        new LatLng(41.214967, -74.788423),
                        new LatLng(41.214551, -74.788616),
                        new LatLng(41.213825, -74.789383),
                        new LatLng(41.213720, -74.790000),
                        new LatLng(41.213430, -74.790982),
                        new LatLng(41.213688, -74.791722),
                        new LatLng(41.213389, -74.793042),
                        new LatLng(41.212808, -74.794093),
                        new LatLng(41.212736, -74.795289),
                        new LatLng(41.212958, -74.796255),
                        new LatLng(41.212974, -74.796985),
                        new LatLng(41.212635, -74.797462),
                        new LatLng(41.211783, -74.797934),
                        new LatLng(41.211388, -74.798717),
                        new LatLng(41.210984, -74.799667),
                        new LatLng(41.211089, -74.800037),
                        new LatLng(41.210653, -74.800740),
                        new LatLng(41.209870, -74.801228),
                        new LatLng(41.209463, -74.800997),
                        new LatLng(41.209132, -74.801131),
                        new LatLng(41.208607, -74.801759),
                        new LatLng(41.208325, -74.802446),
                        new LatLng(41.208220, -74.803304),
                        new LatLng(41.207445, -74.803100),
                        new LatLng(41.206850, -74.803636)));
                flatbrookTopID = flatbrook.getId();


        mMap.setOnPolylineClickListener(this);

        Polyline flatbrookEnd = mMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .width(20)
                .color(Color.BLUE)
                .add(
                      new LatLng(41.097053, -74.969198),
                        new LatLng(41.098185, -74.967479),
                        new LatLng(41.098819, -74.966223),
                        new LatLng(41.099313, -74.964818),
                        new LatLng(41.099656, -74.963595),
                        new LatLng(41.100529, -74.963192),
                        new LatLng(41.101083, -74.962801),
                        new LatLng(41.101766, -74.961460),
                        new LatLng(41.102603, -74.961267),
                        new LatLng(41.103088, -74.961122),
                        new LatLng(41.104026, -74.960516),
                        new LatLng(41.104652, -74.959904),
                        new LatLng(41.104855, -74.959335),
                        new LatLng(41.104818, -74.958681),
                        new LatLng(41.104612, -74.958257),
                        new LatLng(41.104523, -74.957546),
                        new LatLng(41.104620, -74.956873),
                        new LatLng(41.104879, -74.955763),
                        new LatLng(41.105089, -74.954961),
                        new LatLng(41.105784, -74.953620),
                        new LatLng(41.106188, -74.952364),
                        new LatLng(41.106892, -74.950685),
                        new LatLng(41.107538, -74.949156),
                        new LatLng(41.108331, -74.947933),
                        new LatLng(41.108880, -74.946732),
                        new LatLng(41.108911, -74.945573),
                        new LatLng(41.109186, -74.944827),
                        new LatLng(41.109905, -74.943958),
                        new LatLng(41.110269, -74.943186),
                        new LatLng(41.110366, -74.942252),
                        new LatLng(41.110592, -74.941032),
                        new LatLng(41.111393, -74.940673),
                        new LatLng(41.111950, -74.940179),
                        new LatLng(41.112981, -74.938564),
                        new LatLng(41.114335, -74.937432),
                        new LatLng(41.115673, -74.936188),
                        new LatLng(41.116259, -74.935523),
                        new LatLng(41.117406, -74.934109),
                        new LatLng(41.117859, -74.933433),
                        new LatLng(41.117972, -74.933026),
                        new LatLng(41.117609, -74.932296),
                        new LatLng(41.118700, -74.931057),
                        new LatLng(41.119650, -74.930528),
                        new LatLng(41.120131, -74.929225),
                        new LatLng(41.119779, -74.928823),
                        new LatLng(41.119007, -74.928597),
                        new LatLng(41.118336, -74.928401),
                        new LatLng(41.118983, -74.927436),
                        new LatLng(41.120195, -74.926288),
                        new LatLng(41.121440, -74.924013),
                        new LatLng(41.121618, -74.922876),
                        new LatLng(41.122151, -74.921637),
                        new LatLng(41.122119, -74.920591),
                        new LatLng(41.122167, -74.919829),
                        new LatLng(41.123291, -74.918906),
                        new LatLng(41.123897, -74.918021),
                        new LatLng(41.125837, -74.914524),
                        new LatLng(41.126532, -74.913429),
                        new LatLng(41.126047, -74.911326),
                        new LatLng(41.127841, -74.910683),
                        new LatLng(41.128520, -74.910812),
                        new LatLng(41.129328, -74.909567),
                        new LatLng(41.130184, -74.908322),
                        new LatLng(41.130702, -74.908451),
                        new LatLng(41.130896, -74.909782),
                        new LatLng(41.131542, -74.909524),
                        new LatLng(41.131542, -74.906584),
                        new LatLng(41.132092, -74.905662),
                        new LatLng(41.132005, -74.904447),
                        new LatLng(41.132000, -74.905259),
                        new LatLng(41.132000, -74.904469),
                        new LatLng(41.131907, -74.904281),
                        new LatLng(41.131869, -74.903121),
                        new LatLng(41.131902, -74.903087),
                        new LatLng(41.132063, -74.903095),
                        new LatLng(41.132476, -74.903403),
                        new LatLng(41.132661, -74.903400),
                        new LatLng(41.132819, -74.903274),
                        new LatLng(41.132973, -74.902971),
                        new LatLng(41.133090, -74.902488),
                        new LatLng(41.133092, -74.902282),
                        new LatLng(41.133007, -74.901850),
                        new LatLng(41.132870, -74.901649),
                        new LatLng(41.132704, -74.901547),
                        new LatLng(41.132425, -74.901434),
                        new LatLng(41.132356, -74.901338),
                        new LatLng(41.132358, -74.901185),
                        new LatLng(41.132459, -74.901091),
                        new LatLng(41.132608, -74.901035),
                        new LatLng(41.132756, -74.901002),
                        new LatLng(41.132938, -74.901002),
                        new LatLng(41.133101, -74.900952),
                        new LatLng(41.133229, -74.900831),
                        new LatLng(41.133348, -74.900595),
                        new LatLng(41.133390, -74.900297),
                        new LatLng(41.133425, -74.899943),
                        new LatLng(41.133510, -74.899525),
                        new LatLng(41.133679, -74.898865),
                        new LatLng(41.133679, -74.898602),
                        new LatLng(41.133655, -74.898441),
                        new LatLng(41.133568, -74.898192),
                        new LatLng(41.133604, -74.898003),
                        new LatLng(41.133715, -74.897750),
                        new LatLng(41.133830, -74.897576),
                        new LatLng(41.133929, -74.897426),
                        new LatLng(41.134043, -74.897255),
                        new LatLng(41.134133, -74.897123),
                        new LatLng(41.134202, -74.897017),
                        new LatLng(41.134249, -74.896911),
                        new LatLng(41.134396, -74.896685),
                        new LatLng(41.134476, -74.896563),
                        new LatLng(41.134626, -74.896368),
                        new LatLng(41.134783, -74.896188),
                        new LatLng(41.134942, -74.896008),
                        new LatLng(41.135039, -74.895889),
                        new LatLng(41.135171, -74.895771),
                        new LatLng(41.135348, -74.895613),
                        new LatLng(41.135395, -74.895587),
                        new LatLng(41.135589, -74.895414),
                        new LatLng(41.135752, -74.895267),
                        new LatLng(41.135909, -74.895131),
                        new LatLng(41.136141, -74.894925),
                        new LatLng(41.136309, -74.894788),
                        new LatLng(41.136507, -74.894634),
                        new LatLng(41.136678, -74.894490),
                        new LatLng(41.136775, -74.894406),
                        new LatLng(41.136923, -74.894313),
                        new LatLng(41.137133, -74.894116),
                        new LatLng(41.137196, -74.894057),
                        new LatLng(41.137340, -74.893848),
                        new LatLng(41.137457, -74.893596),
                        new LatLng(41.137514, -74.893445),
                        new LatLng(41.137524, -74.893290),
                        new LatLng(41.137456, -74.893205),
                        new LatLng(41.137402, -74.893178),
                        new LatLng(41.137360, -74.893106),
                        new LatLng(41.137437, -74.892831),
                        new LatLng(41.137500, -74.892673),
                        new LatLng(41.137547, -74.892560),
                        new LatLng(41.137620, -74.892436),
                        new LatLng(41.137728, -74.892317),
                        new LatLng(41.137868, -74.892233),
                        new LatLng(41.137960, -74.892199),
                        new LatLng(41.138168, -74.892061),
                        new LatLng(41.138237, -74.892012),
                        new LatLng(41.138314, -74.891931),
                        new LatLng(41.138475, -74.891719),
                        new LatLng(41.138616, -74.891477),
                        new LatLng(41.138660, -74.891402),
                        new LatLng(41.138903, -74.891100),
                        new LatLng(41.139195, -74.890964),
                        new LatLng(41.139500, -74.890972),
                        new LatLng(41.139708, -74.891042),
                        new LatLng(41.139837, -74.891066),
                        new LatLng(41.139995, -74.891064),
                        new LatLng(41.140268, -74.890967),
                        new LatLng(41.140589, -74.890736),
                        new LatLng(41.140732, -74.890626),
                        new LatLng(41.141098, -74.890471),
                        new LatLng(41.141478, -74.890374),
                        new LatLng(41.141617, -74.890369),
                        new LatLng(41.142009, -74.890296),
                        new LatLng(41.142158, -74.890275),
                        new LatLng(41.142372, -74.890181),
                        new LatLng(41.142415, -74.890160),
                        new LatLng(41.142764, -74.890095),
                        new LatLng(41.143257, -74.890085),
                        new LatLng(41.143597, -74.890074),
                        new LatLng(41.143669, -74.890044),
                        new LatLng(41.143788, -74.889883),
                        new LatLng(41.143807, -74.889827),
                        new LatLng(41.143879, -74.889739),
                        new LatLng(41.143938, -74.889717),
                        new LatLng(41.144065, -74.889722),
                        new LatLng(41.144085, -74.889739),
                        new LatLng(41.144190, -74.889763),
                        new LatLng(41.144314, -74.889760),
                        new LatLng(41.144388, -74.889706),
                        new LatLng(41.144600, -74.889454),
                        new LatLng(41.144814, -74.889258),
                        new LatLng(41.145041, -74.889100),
                        new LatLng(41.145768, -74.888585),
                        new LatLng(41.146327, -74.888668),
                        new LatLng(41.147121, -74.886952),
                        new LatLng(41.147729, -74.885404),
                        new LatLng(41.148097, -74.884597),
                        new LatLng(41.147711, -74.883789),
                        new LatLng(41.147723, -74.883422),
                        new LatLng(41.146751, -74.883291),
                        new LatLng(41.146556, -74.882738),
                        new LatLng(41.146602, -74.881644),
                        new LatLng(41.147147, -74.880034),
                        new LatLng(41.147313, -74.879042),
                        new LatLng(41.147620, -74.878774),
                        new LatLng(41.148327, -74.879096),
                        new LatLng(41.148707, -74.879071),
                        new LatLng(41.148812, -74.878854),
                        new LatLng(41.148826, -74.878682),
                        new LatLng(41.149599, -74.878610),
                        new LatLng(41.150316, -74.879071),
                        new LatLng(41.150670, -74.879195),
                        new LatLng(41.150835, -74.879082),
                        new LatLng(41.151655, -74.879420),
                        new LatLng(41.152324, -74.880021),
                        new LatLng(41.152552, -74.880463),
                        new LatLng(41.153394, -74.880938),
                        new LatLng(41.153693, -74.881142),
                        new LatLng(41.154139, -74.880737),
                        new LatLng(41.155165, -74.879484),
                        new LatLng(41.155884, -74.878725),
                        new LatLng(41.156187, -74.878650),
                        new LatLng(41.156456, -74.878090),
                        new LatLng(41.156775, -74.877996),
                        new LatLng(41.157199, -74.877282),
                        new LatLng(41.157403, -74.876891),
                        new LatLng(41.157613, -74.876679),
                        new LatLng(41.157912, -74.875743),
                        new LatLng(41.158536, -74.875311),
                        new LatLng(41.158661, -74.875032),
                        new LatLng(41.158543, -74.874348),
                        new LatLng(41.159322, -74.873535),
                        new LatLng(41.159973, -74.873812),
                        new LatLng(41.160746, -74.873981),
                        new LatLng(41.161317, -74.873498),
                        new LatLng(41.161402, -74.872867),
                        new LatLng(41.161182, -74.872599),
                        new LatLng(41.161723, -74.871540),
                        new LatLng(41.162337, -74.870845),
                        new LatLng(41.162672, -74.870510),
                        new LatLng(41.163167, -74.870813),
                        new LatLng(41.163611, -74.870486),
                        new LatLng(41.163932, -74.870703),
                        new LatLng(41.164023, -74.871033),
                        new LatLng(41.164292, -74.871234),
                        new LatLng(41.164258, -74.871682),
                        new LatLng(41.164585, -74.871840),
                        new LatLng(41.164657, -74.872141),
                        new LatLng(41.165108, -74.872288),
                        new LatLng(41.165780, -74.872020),
                        new LatLng(41.166255, -74.871003),
                        new LatLng(41.166307, -74.870526),
                        new LatLng(41.166992, -74.869869),
                        new LatLng(41.167389, -74.869343),
                        new LatLng(41.167460, -74.868871),
                        new LatLng(41.167878, -74.868608),
                        new LatLng(41.168165, -74.868243),
                        new LatLng(41.168165, -74.867739),
                        new LatLng(41.168682, -74.867664),
                        new LatLng(41.169001, -74.868136),
                        new LatLng(41.169457, -74.868372),
                        new LatLng(41.170002, -74.867959),
                        new LatLng(41.170030, -74.867471),
                        new LatLng(41.169752, -74.866575),
                        new LatLng(41.170115, -74.864858),
                        new LatLng(41.170361, -74.863544),
                        new LatLng(41.171460, -74.863340),
                        new LatLng(41.172603, -74.862739),
                        new LatLng(41.173527, -74.862541),
                        new LatLng(41.174864, -74.861919),
                        new LatLng(41.175683, -74.861734),
                        new LatLng(41.176588, -74.861897),
                        new LatLng(41.177299, -74.861747),
                        new LatLng(41.177563, -74.861567),
                        new LatLng(41.177997, -74.861819),
                        new LatLng(41.178681, -74.861618)));
        flatbrookID = flatbrookEnd.getId();


        //Rockaway
        Polyline rockaway = mMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .width(20)
                .color(Color.BLUE)
                .add(
                        new LatLng(40.978038, -74.546561),
                        new LatLng(40.977795, -74.546556),
                        new LatLng(40.977611, -74.546635),
                        new LatLng(40.977111, -74.546833),
                        new LatLng(40.977015, -74.546775),
                        new LatLng(40.976700, -74.546389),
                        new LatLng(40.976133, -74.546697),
                        new LatLng(40.975308, -74.547610),
                        new LatLng(40.974717, -74.548468),
                        new LatLng(40.974597, -74.549005),
                        new LatLng(40.973834, -74.549482),
                        new LatLng(40.973631, -74.550120),
                        new LatLng(40.973680, -74.550544),
                        new LatLng(40.972907, -74.550914),
                        new LatLng(40.972263, -74.550828),
                        new LatLng(40.972113, -74.551821),
                        new LatLng(40.971218, -74.552851),
                        new LatLng(40.971303, -74.553194),
                        new LatLng(40.969905, -74.554804),
                        new LatLng(40.969111, -74.555458),
                        new LatLng(40.968342, -74.555855),
                        new LatLng(40.968321, -74.556289),
                        new LatLng(40.967844, -74.556263),
                        new LatLng(40.967232, -74.554927),
                        new LatLng(40.967179, -74.554643),
                        new LatLng(40.966879, -74.554680),
                        new LatLng(40.966227, -74.555512),
                        new LatLng(40.966114, -74.556048),
                        new LatLng(40.965737, -74.556289),
                        new LatLng(40.965741, -74.556794),
                        new LatLng(40.966588, -74.557148),
                        new LatLng(40.966717, -74.557534),
                        new LatLng(40.967046, -74.557647),
                        new LatLng(40.967204, -74.558591),
                        new LatLng(40.966969, -74.558993),
                        new LatLng(40.967021, -74.559819),
                        new LatLng(40.966726, -74.560495),
                        new LatLng(40.966450, -74.561767),
                        new LatLng(40.965814, -74.562700),
                        new LatLng(40.964364, -74.564079),
                        new LatLng(40.963416, -74.564476),
                        new LatLng(40.962488, -74.564502),
                        new LatLng(40.962055, -74.564701),
                        new LatLng(40.961873, -74.564594),
                        new LatLng(40.961589, -74.565688),
                        new LatLng(40.961249, -74.565881),
                        new LatLng(40.961087, -74.565763),
                        new LatLng(40.960990, -74.566080),
                        new LatLng(40.961014, -74.566364),
                        new LatLng(40.960783, -74.566578),
                        new LatLng(40.959718, -74.566412),
                        new LatLng(40.959888, -74.566970),
                        new LatLng(40.959365, -74.567528),
                        new LatLng(40.959171, -74.567018),
                        new LatLng(40.958794, -74.567748),
                        new LatLng(40.958004, -74.568064),
                        new LatLng(40.957818, -74.567603),
                        new LatLng(40.957408, -74.567989),
                        new LatLng(40.957336, -74.568451),
                        new LatLng(40.956695, -74.568746),
                        new LatLng(40.956144, -74.568695),
                        new LatLng(40.955800, -74.569065),
                        new LatLng(40.955273, -74.569086),
                        new LatLng(40.955314, -74.569746),
                        new LatLng(40.954755, -74.569784),
                        new LatLng(40.954220, -74.570401),
                        new LatLng(40.954204, -74.570792),
                        new LatLng(40.954182, -74.571477),
                        new LatLng(40.953935, -74.571917),
                        new LatLng(40.953274, -74.572040),
                        new LatLng(40.952764, -74.572303),
                        new LatLng(40.952683, -74.572705),
                        new LatLng(40.953440, -74.573537),
                        new LatLng(40.952922, -74.574357),
                        new LatLng(40.952440, -74.574041),
                        new LatLng(40.951706, -74.574765),
                        new LatLng(40.951289, -74.575717),
                        new LatLng(40.950831, -74.576060),
                        new LatLng(40.949551, -74.576586),
                        new LatLng(40.949251, -74.576216),
                        new LatLng(40.948202, -74.576393),
                        new LatLng(40.948311, -74.576964),
                        new LatLng(40.948024, -74.576991),
                        new LatLng(40.947569, -74.576835)));
                        /*new LatLng(x,x),
                        new LatLng(x,x),
                        new LatLng(x,x),
                        new LatLng(x,x),
                        new LatLng(x,x),
                        new LatLng(x,x),
                        new LatLng(x,x),
                        new LatLng(x,x),
                        new LatLng(x,x),
                        new LatLng(x,x),
                        new LatLng(x,x),
                        new LatLng(x,x),
                        new LatLng(x,x),
                        new LatLng(x,x),
                        new LatLng(x,x),
                        new LatLng(x,x),
                        new LatLng(x,x),
                        new LatLng(x,x),
                        new LatLng(x,x),
                        new LatLng(x,x),
                        new LatLng(x,x),
                        new LatLng(x,x),
                        new LatLng(x,x),
                        new LatLng(x,x),
                        new LatLng(x,x),
                        new LatLng(x,x),
                        new LatLng(x,x),
                        new LatLng(x,x),
                        new LatLng(x,x),
                        new LatLng(x,x),
                        new LatLng(x,x),
                        new LatLng(x,x),
                        new LatLng(x,x),
                        new LatLng(x,x),
                        new LatLng(x,x),
                        new LatLng(x,x),
                        new LatLng(x,x)*/
        //Paulinskill


        //pequest





        //Holdover Trout Lakes

        Polygon lakeOcquittunk = mMap.addPolygon(new PolygonOptions()
                .clickable(true)
                .fillColor(Color.RED)
                .add(new LatLng(41.227093, -74.766066),
                        new LatLng(41.227625, -74.765647),
                        new LatLng(41.228400, -74.765250),
                        new LatLng(41.228965, -74.763984),
                        new LatLng(41.228150, -74.763545),
                        new LatLng(41.227762, -74.763008),
                        new LatLng(41.226963, -74.762804),
                        new LatLng(41.226980, -74.763502),
                        new LatLng(41.227714, -74.764113),
                        new LatLng(41.227625, -74.764489),
                        new LatLng(41.227327, -74.764553),
                        new LatLng(41.226923, -74.765572),
                        new LatLng(41.227076, -74.766077)));

        //Lake Waywayyanda



        //TCA Rivers

        //Point Mountain TCA
        Polyline pointMountainTCA = mMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .width(20)
                .color(Color.GREEN)
                .add(new LatLng(40.778041, -74.903049),
                        new LatLng(40.777264, -74.902388),
                        new LatLng(40.776562, -74.903120),
                        new LatLng(40.775675, -74.903462),
                        new LatLng(40.774862, -74.903315),
                        new LatLng(40.773623, -74.903510),
                        new LatLng(40.771849, -74.902754),
                        new LatLng(40.771442, -74.903096),
                        new LatLng(40.770999, -74.904462),
                        new LatLng(40.770167, -74.906122),
                        new LatLng(40.768836, -74.907647),
                        new LatLng(40.768716, -74.907977),
                        new LatLng(40.768078, -74.909990),
                        new LatLng(40.767283, -74.911723)));

        Polyline flatbrookTCA = mMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .width(20)
                .color(Color.GREEN)
                .add(
                        new LatLng(41.178681, -74.861618),
                        new LatLng(41.179248, -74.861285),
                        new LatLng(41.179507, -74.860904),
                        new LatLng(41.179820, -74.860759),
                        new LatLng(41.179975, -74.860427),
                        new LatLng(41.180627, -74.860113),
                        new LatLng(41.181663, -74.859375),
                        new LatLng(41.181915, -74.859145),
                        new LatLng(41.181921, -74.858898),
                        new LatLng(41.182105, -74.858568),
                        new LatLng(41.182390, -74.858018),
                        new LatLng(41.182955, -74.857618),
                        new LatLng(41.183445, -74.857388),
                        new LatLng(41.183914, -74.856862),
                        new LatLng(41.184200, -74.856009),
                        new LatLng(41.184330, -74.855180),
                        new LatLng(41.184386, -74.854526),
                        new LatLng(41.184291, -74.854081),
                        new LatLng(41.184602, -74.853072),
                        new LatLng(41.184844, -74.852479),
                        new LatLng(41.184959, -74.852442),
                        new LatLng(41.185026, -74.852214),
                        new LatLng(41.185212, -74.852176),
                        new LatLng(41.185625, -74.851363),
                        new LatLng(41.185583, -74.851031),
                        new LatLng(41.185373, -74.850618),
                        new LatLng(41.184980, -74.850358),
                        new LatLng(41.184923, -74.850146),
                        new LatLng(41.184806, -74.849743),
                        new LatLng(41.184996, -74.849405),
                        new LatLng(41.185016, -74.849153),
                        new LatLng(41.185254, -74.848982),
                        new LatLng(41.185502, -74.849057),
                        new LatLng(41.185741, -74.849196),
                        new LatLng(41.186150, -74.849215),
                        new LatLng(41.186376, -74.848998),
                        new LatLng(41.187192, -74.848314),
                        new LatLng(41.188124, -74.847702),
                        new LatLng(41.188770, -74.847219),
                        new LatLng(41.189041, -74.846978),
                        new LatLng(41.189321, -74.846431),
                        new LatLng(41.189673, -74.846391),
                        new LatLng(41.189981, -74.846603),
                        new LatLng(41.190119, -74.846187),
                        new LatLng(41.190139, -74.845492),
                        new LatLng(41.189963, -74.844773),
                        new LatLng(41.189981, -74.844245),
                        new LatLng(41.190274, -74.843789),
                        new LatLng(41.190734, -74.843623),
                        new LatLng(41.191138, -74.843006),
                        new LatLng(41.191873, -74.842845),
                        new LatLng(41.192623, -74.842346),
                        new LatLng(41.192934, -74.841874),
                        new LatLng(41.193176, -74.841987),
                        new LatLng(41.193556, -74.841472),
                        new LatLng(41.193931, -74.840710),
                        new LatLng(41.194529, -74.840635),
                        new LatLng(41.194722, -74.839953),
                        new LatLng(41.194690, -74.838746),
                        new LatLng(41.195417, -74.838317),
                        new LatLng(41.195691, -74.838387),
                        new LatLng(41.195844, -74.838129),
                        new LatLng(41.195873, -74.837700),
                        new LatLng(41.195731, -74.836901),
                        new LatLng(41.195735, -74.836305),
                        new LatLng(41.195582, -74.835742),
                        new LatLng(41.195540, -74.835659),
                        new LatLng(41.195362, -74.835546),
                        new LatLng(41.195273, -74.835069),
                        new LatLng(41.195273, -74.834395),
                        new LatLng(41.195219, -74.834135),
                        new LatLng(41.195273, -74.833977),
                        new LatLng(41.195400, -74.833961),
                        new LatLng(41.195443, -74.833736),
                        new LatLng(41.195413, -74.833011),
                        new LatLng(41.195350, -74.832665),
                        new LatLng(41.195493, -74.832244),
                        new LatLng(41.195530, -74.831579),
                        new LatLng(41.195413, -74.831493),
                        new LatLng(41.195616, -74.830479),
                        new LatLng(41.195897, -74.829345),
                        new LatLng(41.196135, -74.829012),
                        new LatLng(41.196236, -74.828438),
                        new LatLng(41.196387, -74.827870),
                        new LatLng(41.196515, -74.827151),
                        new LatLng(41.196490, -74.826915),
                        new LatLng(41.196099, -74.826850),
                        new LatLng(41.195921, -74.826389),
                        new LatLng(41.195903, -74.825367),
                        new LatLng(41.196171, -74.823972),
                        new LatLng(41.196444, -74.822824),
                        new LatLng(41.196879, -74.822005),
                        new LatLng(41.197103, -74.821168),
                        new LatLng(41.197527, -74.820141),
                        new LatLng(41.197967, -74.818918),
                        new LatLng(41.198493, -74.817692),
                        new LatLng(41.198954, -74.816877),
                        new LatLng(41.199416, -74.816126),
                        new LatLng(41.199999, -74.815493),
                        //
                        new LatLng(41.200150, -74.815339),
                        new LatLng(41.200360, -74.815060),
                        new LatLng(41.200780, -74.814298),
                        new LatLng(41.200937, -74.813869),
                        new LatLng(41.201139, -74.813488),
                        new LatLng(41.201446, -74.812871),
                        new LatLng(41.201696, -74.811933),
                        new LatLng(41.201890, -74.811235),
                        new LatLng(41.202168, -74.810672),
                        new LatLng(41.202285, -74.810420),
                        new LatLng(41.202721, -74.809948),
                        new LatLng(41.203444, -74.809224),
                        new LatLng(41.203577, -74.809079),
                        new LatLng(41.203823, -74.808950),
                        new LatLng(41.204041, -74.808821),
                        new LatLng(41.204457, -74.808510),
                        new LatLng(41.204917, -74.807955),
                        new LatLng(41.205442, -74.807300),
                        new LatLng(41.205583, -74.807134),
                        new LatLng(41.206011, -74.807196),
                        new LatLng(41.206459, -74.806514),
                        new LatLng(41.206604, -74.806217),
                        new LatLng(41.206045, -74.805342),
                        new LatLng(41.205813, -74.804538),
                        new LatLng(41.206162, -74.803964),
                        new LatLng(41.206850, -74.803636)));
        flatbrookTCAID = flatbrookTCA.getId();





        //Wild Trout Streams

        //Criss Brook
        Polyline crissBrook = mMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .width(15)
                .color(Color.argb(255, 148,0, 211))
                .add(new LatLng(41.223959, -74.770681),
                        new LatLng(41.224605, -74.770479),
                        new LatLng(41.225128, -74.770351),
                        new LatLng(41.225731, -74.770157),
                        new LatLng(41.225949, -74.769889),
                        new LatLng(41.226039, -74.769881),
                        new LatLng(41.226318, -74.769624),
                        new LatLng(41.226608, -74.769511),
                        new LatLng(41.226762, -74.769342),
                        new LatLng(41.227070, -74.769345),
                        new LatLng(41.227212, -74.769154),
                        new LatLng(41.227732, -74.769401),
                        new LatLng(41.228073, -74.769363),
                        new LatLng(41.228097, -74.769519),
                        new LatLng(41.228398, -74.769672),
                        new LatLng(41.228595, -74.769409),
                        new LatLng(41.228973, -74.769473),
                        new LatLng(41.229055, -74.769540),
                        new LatLng(41.229184, -74.769111),
                        new LatLng(41.229350, -74.769310),
                        new LatLng(41.229418, -74.769567),
                        new LatLng(41.229897, -74.769591),
                        new LatLng(41.230381, -74.769200),
                        new LatLng(41.230451, -74.768961),
                        new LatLng(41.230631, -74.769044),
                        new LatLng(41.230748, -74.768787),
                        new LatLng(41.230764, -74.768679),
                        new LatLng(41.230966, -74.768594),
                        new LatLng(41.230962, -74.768076),
                        new LatLng(41.231052, -74.767880),
                        new LatLng(41.231397, -74.767786),
                        new LatLng(41.231496, -74.767556),
                        new LatLng(41.232000, -74.766841),
                        new LatLng(41.232874, -74.766594),
                        new LatLng(41.233219, -74.766232),
                        new LatLng(41.233517, -74.766136),
                        new LatLng(41.233971, -74.765618),
                        new LatLng(41.234253, -74.765363),
                        new LatLng(41.235786, -74.763668),
                        new LatLng(41.234850, -74.764816),
                        new LatLng(41.235770, -74.763732),
                        new LatLng(41.236189, -74.762563),
                        new LatLng(41.237166, -74.762016),
                        new LatLng(41.237464, -74.761404),
                        new LatLng(41.238327, -74.760953),
                        new LatLng(41.238327, -74.760610),
                        new LatLng(41.240094, -74.758979),
                        new LatLng(41.241740, -74.757971)));

        //Parker Brook
        Polyline parkerBrook = mMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .width(15)
                .color(Color.argb(255, 148,0, 211))
                .add(new LatLng(41.240781, -74.741337),
                        new LatLng(41.241632, -74.740458),
                        new LatLng(41.241749, -74.739680),
                        new LatLng(41.242225, -74.739117),
                        new LatLng(41.242378, -74.738028),
                        new LatLng(41.242624, -74.737030),
                        new LatLng(41.242439, -74.735791),
                        new LatLng(41.242249, -74.735233),
                        new LatLng(41.242733, -74.734138),
                        new LatLng(41.243048, -74.733452),
                        new LatLng(41.243645, -74.732990),
                        new LatLng(41.244266, -74.732207),
                        new LatLng(41.244322, -74.731982),
                        new LatLng(41.245129, -74.731612),
                        new LatLng(41.245653, -74.731231),
                        new LatLng(41.246311, -74.730576),
                        new LatLng(41.247077, -74.730603),
                        new LatLng(41.247360, -74.730437),
                        new LatLng(41.247557, -74.729981),
                        new LatLng(41.248295, -74.729589),
                        new LatLng(41.248489, -74.729182),
                        new LatLng(41.248735, -74.728795),
                        new LatLng(41.249775, -74.728500),
                        new LatLng(41.250050, -74.728570),
                        new LatLng(41.250235, -74.728291),
                        new LatLng(41.251502, -74.729900),
                        new LatLng(41.252014, -74.730040),
                        new LatLng(41.252502, -74.729986),
                        new LatLng(41.253244, -74.730324),
                        new LatLng(41.255357, -74.730522),
                        new LatLng(41.257979, -74.729686),
                        new LatLng(41.259745, -74.729557),
                        new LatLng(41.261059, -74.729954),
                        new LatLng(41.262430, -74.730581),
                        new LatLng(41.263402, -74.730705)));
            parkerBrookID = parkerBrook.getId();

        Polyline stonyBrook = mMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .width(15)
                .color(Color.argb(255, 148,0, 211))
                .add(new LatLng(41.215228, -74.783358),
                        new LatLng(41.214912, -74.783084),
                        new LatLng(41.214837, -74.782448),
                        new LatLng(41.214617, -74.782247),
                        new LatLng(41.214353, -74.781925),
                        new LatLng(41.214329, -74.781732),
                        new LatLng(41.214183, -74.781657),
                        new LatLng(41.214074, -74.781260),
                        new LatLng(41.213943, -74.781177),
                        new LatLng(41.213848, -74.780616),
                        new LatLng(41.213895, -74.780187),
                        new LatLng(41.213810, -74.779675),
                        new LatLng(41.213669, -74.778932),
                        new LatLng(41.213511, -74.777886),
                        new LatLng(41.213144, -74.777666),
                        new LatLng(41.212914, -74.777089),
                        new LatLng(41.212612, -74.776285),
                        new LatLng(41.212154, -74.775727),
                        new LatLng(41.211611, -74.774996),
                        new LatLng(41.210964, -74.774881),
                        new LatLng(41.210403, -74.774570),
                        new LatLng(41.209571, -74.774827),
                        new LatLng(41.209149, -74.774891),
                        new LatLng(41.208387, -74.775736),
                        new LatLng(41.207947, -74.775895),
                        new LatLng(41.207668, -74.776420),
                        new LatLng(41.207220, -74.775854),
                        new LatLng(41.206648, -74.774832),
                        new LatLng(41.205268, -74.773688),
                        new LatLng(41.204941, -74.773334),
                        new LatLng(41.203791, -74.774015),
                        new LatLng(41.203335, -74.774423),
                        new LatLng(41.202290, -74.774144)));

        Polyline forkedBrook = mMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .width(15)
                .color(Color.argb(255, 148,0, 211))
                .add(new LatLng(41.237888, -74.744228),
                        new LatLng(41.238662, -74.744989),
                        new LatLng(41.239421, -74.745246),
                        new LatLng(41.239372, -74.745718),
                        new LatLng(41.239957, -74.745997),
                        new LatLng(41.240578, -74.746609),
                        new LatLng(41.240982, -74.746351),
                        new LatLng(41.241425, -74.746732),
                        new LatLng(41.241962, -74.746426),
                        new LatLng(41.242652, -74.746641),
                        new LatLng(41.242825, -74.746898),
                        new LatLng(41.243204, -74.746882),
                        new LatLng(41.243224, -74.746636),
                        new LatLng(41.244265, -74.746678),
                        new LatLng(41.244600, -74.746292),
                        new LatLng(41.244689, -74.746104),
                        new LatLng(41.245310, -74.745938),
                        new LatLng(41.245471, -74.745734),
                        new LatLng(41.246496, -74.745944),
                        new LatLng(41.246633, -74.746458),
                        new LatLng(41.247407, -74.746389),
                        new LatLng(41.248428, -74.746609),
                        new LatLng(41.248569, -74.747129),
                        new LatLng(41.249105, -74.747596),
                        new LatLng(41.249315, -74.748111),
                        new LatLng(41.249246, -74.748620),
                        new LatLng(41.249702, -74.748653),
                        new LatLng(41.250956, -74.748626),
                        new LatLng(41.251731, -74.749125),
                        new LatLng(41.252876, -74.748824),
                        new LatLng(41.253642, -74.748658),
                        new LatLng(41.254501, -74.747896),
                        new LatLng(41.255243, -74.747322),
                        new LatLng(41.256788, -74.746925),
                        new LatLng(41.257582, -74.745922),
                        new LatLng(41.258107, -74.745219),
                        new LatLng(41.258615, -74.744731),
                        new LatLng(41.259421, -74.744667),
                        new LatLng(41.260325, -74.742741),
                        new LatLng(41.261180, -74.739581),
                        new LatLng(41.262511, -74.738530),
                        new LatLng(41.264519, -74.737639),
                        new LatLng(41.265438, -74.737371)));
    }



    /*
        After the rivers are created this next section of code
        updates the camarea when called, sets the current loaction
        if the user loads the map directly and sets the river information
        when a polyline is clicked
     */

    public void updateCamera(double lat, double lon, int view){


        mMap.setMinZoomPreference(9.0f);
        mMap.setMaxZoomPreference(20.0f);
        LatLng riverClicked = new LatLng(lat, lon);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(riverClicked, view));


    }

    public void currentLocation(double lat, double lon, int view){
        mMap.setMinZoomPreference(1.0f);
        mMap.setMaxZoomPreference(20.0f);
        LatLng current = new LatLng(lat, lon);
        mMap.addMarker(new MarkerOptions().position(current).title("Current Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, view));
    }

    @SuppressLint("SetTextI18n")
    public void setInfo(int ID){
        TextView riverInfo = findViewById(R.id.txtClickedRiverTitle);

        if(ID == 3){
            riverInfo.setText("Big Flatbrook");


        }
    }

    /*
        Method to Open Shopping Activity
     */

    public void openShoppingActivity(){
        Intent intent = new Intent(this, ShoppingHome.class);
        startActivity(intent);
    }



    @SuppressLint("SetTextI18n")
    public void onPolylineClick(Polyline polyline) {


        TextView riverInfo = findViewById(R.id.txtClickedRiverTitle);

        TextView riverBackground = findViewById(R.id.txtClickedRiverBackground);

        TextView waterLevel = findViewById(R.id.txtTroutSpecies);

        TextView riverTemp = findViewById(R.id.txtTemp);

        TextView wildTitle = findViewById(R.id.txtWildTitle);

        TextView wildInfo = findViewById(R.id.txtNjWild);

        TextView wildDescription = findViewById(R.id.txtWildDescription);

        TextView wildSpecies = findViewById(R.id.txtSpecies);

        ScrollView wildStreamScroll = findViewById(R.id.scrollWildStreamInfo);

        ScrollView selectedRiver = findViewById(R.id.svSelectedRiver);


        LinearLayout vertical = findViewById(R.id.llVertical2);

        LinearLayout mapKey = findViewById(R.id.llMapKey);

        LinearLayout mapLayout = findViewById(R.id.llMapLayout);



        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(1080, 1300);
        selectedRiver.setLayoutParams(params);


        LinearLayout.LayoutParams mapKeyParams = new LinearLayout.LayoutParams(1, 1);
        mapKey.setLayoutParams(mapKeyParams);


        LinearLayout.LayoutParams mapParams = new LinearLayout.LayoutParams(1080, 800);
        mapLayout.setLayoutParams(mapParams);



        //Trout Stocked Polylines Clicked
        if(polyline.getId().equals(muskieID)){

            riverInfo.setText("Muskie");


            updateCamera(40.8733, -74.8060,9);

        }
        else if(polyline.getId().equals(southBranchID)){
            riverInfo.setText("South Branch Raritan River");

            updateCamera(40.720618, -74.837659,10);


        }

        else if(polyline.getId().equals(flatbrookTopID)){
            riverInfo.setText("Big Flatbrook");



            riverBackground.setBackgroundResource(R.drawable.flatbrook_info_background);

            String url = "https://stackoverflow.com/questions/2835505";
            try {
                Document document = Jsoup.connect(url).get();
                String question = document.select("#question .post-text").text();
                riverTemp.setText(question);

            } catch (IOException e) {
                e.printStackTrace();
            }

            updateCamera(41.228811, -74.756907, 12);
        }
        else if(polyline.getId().equals(flatbrookID)){
            riverInfo.setText("Big Flatbrook");





            riverBackground.setBackgroundResource(R.drawable.bigflat);

            updateCamera(41.130184, -74.908322, 11);
        }

        //TCA Polylines Clicked
        else if(polyline.getId().equals(flatbrookTCAID)){
            riverInfo.setText("Flatbrook Catch and Release Area TCA");



            riverBackground.setBackgroundResource(R.drawable.flatbrook_cr);



            updateCamera(41.199810, -74.834599, 12);
        }

        //Wild Trout Streams Clicked
        else if(polyline.getId().equals(parkerBrookID)){

            wildTitle.setText("Parker Brook");

            wildDescription.setText(
                    "New Jersey offers anglers a wide variety of streams  \n" +
                    "to catch wild and native trout species. Most  \n" +
                    "relgnergknreglkrenglrknrelkgnergnreglregnerklgnerlgn \n" +
                    "erljgbregknreglklnerlgnerlngreglknrelgnrelgjnrlgknerkn \n" +
                    "ergblregknrelgnkerlgnrelkgnerlkgrenglkernglgknerlgknrel \n");

            wildSpecies.setText("Wild Trout Species: Brook");

            LinearLayout.LayoutParams wildInfoLayout = new LinearLayout.LayoutParams(1080, 900);
            wildStreamScroll.setLayoutParams(wildInfoLayout);

            selectedRiver.setLayoutParams(params);

            updateCamera(41.257979, -74.729686, 13);


        }


    }

    /*
        Section for if a user clicks on a holdover trout lake
     */

    @Override
    public void onPolygonClick(Polygon polygon) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(MapsActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
            }

            else {
                Toast.makeText(MapsActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

}


