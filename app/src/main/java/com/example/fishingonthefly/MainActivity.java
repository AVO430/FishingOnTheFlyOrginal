package com.example.fishingonthefly;

import androidx.annotation.RequiresApi;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

public class MainActivity extends MapsActivity {

    private EditText searchBar;
    String riverSearched;
    private Button button;
    private Button muskie;
    private Button southBranch;
    private Button flatbrook;
    private Button searched;

    //TCA Buttons
    private Button pointMountainTCA;
    private Button flatbrookCatchRelease;

    private Button search;

    private Button gear;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchBar = findViewById(R.id.txtSearchBar);

        search = findViewById(R.id.btnSearch);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Get the River Searched for here
                String river = searchBar.getText().toString();

                if(river.equals("Flatbrook")){

                    createFlatbrookView();

                    searchBar.setText("Search Rivers Here");

                }

                else if(river.equals("flatbrook")){
                    createFlatbrookView();

                    searchBar.setText("Search Rivers Here");
                }

                else if(river.equals("Flatbrook River")){
                    createFlatbrookView();

                    searchBar.setText("Search Rivers Here");
                }

                else if(river.equals("flatbrook river")){
                    createFlatbrookView();

                    searchBar.setText("Search Rivers Here");
                }

                else if(river.equals("flatbrook river, nj")){
                    createFlatbrookView();

                    searchBar.setText("Search Rivers Here");
                }

                else if(river.equals("Flatbrook River, NJ")){
                    createFlatbrookView();

                    searchBar.setText("Search Rivers Here");
                }

                else{
                    Toast.makeText(MainActivity.this, "River Not Found", Toast.LENGTH_SHORT).show();
                }

            }
        });

        button = (Button) findViewById(R.id.btnMap);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               openMapActivity();
            }
        });

        muskie = (Button) findViewById(R.id.btnMuskie);
        muskie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMuskieView();
            }
        });

        southBranch = (Button) findViewById(R.id.btnSouthBranch);
        southBranch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createSouthBranchView();
            }
        });

        flatbrook = (Button) findViewById(R.id.btnFlatbrook);
        flatbrook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createFlatbrookView();
            }
        });

        //TCA Buttons

        flatbrookCatchRelease = (Button) findViewById(R.id.btnFlatbrookTCA);
        flatbrook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createFlatbrookTCAView();
            }
        });

        pointMountainTCA = (Button) findViewById(R.id.btnPointMountainTCA);
        pointMountainTCA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPointMountainTCAView();
            }
        });

        gear = (Button) findViewById(R.id.btnGear);
        gear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createShoppingView();
            }
        });


    }




    public void openMapActivity(){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    //Create Stocked River Views

    public void createMuskieView(){

        Intent intent = new Intent(this, MapsActivity.class);

        intent.putExtra("latitude",40.8733); //40.8733, -74.8060
        intent.putExtra("longitude",-74.8060);
        intent.putExtra("view",9);
        intent.putExtra("id", 1);
        startActivity(intent);
    }

    public void createSouthBranchView(){
        Intent intent = new Intent(this, MapsActivity.class);

        intent.putExtra("latitude",40.720618);
        intent.putExtra("longitude",-74.837659);
        intent.putExtra("view",10);
        intent.putExtra("id", 2);
        startActivity(intent);
    }

    public void createFlatbrookView(){
        Intent intent = new Intent(this, MapsActivity.class);

        intent.putExtra("latitude",41.178681);
        intent.putExtra("longitude",-74.861618);
        intent.putExtra("view",10);
        intent.putExtra("id", 3);

        startActivity(intent);
    }

    //Create TCA Views

    public void createPointMountainTCAView(){
        Intent intent = new Intent(this, MapsActivity.class);

        intent.putExtra("latitude",40.769977);
        intent.putExtra("longitude", -74.906316);
        intent.putExtra("view",14);

        startActivity(intent);
    }

    public void createFlatbrookTCAView(){
        Intent intent = new Intent(this, MapsActivity.class);

        intent.putExtra("latitude",41.178681);
        intent.putExtra("longitude", -74.861618);
        intent.putExtra("view",10);

        startActivity(intent);
    }

    public void createShoppingView(){

        Intent intent = new  Intent(this, ShoppingHome.class);
        startActivity(intent);
    }

}



