package com.example.fishingonthefly;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class ShoppingHome extends AppCompatActivity {

    private Button adams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_home);

        adams = findViewById(R.id.btnAdams);

        adams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://www.amazon.com/Produce-Parachute-Fishing-Assortment-Flies/dp/B08HCX1LZ6/ref=sr_1_13?crid=18TUZVH2DY7IY&dchild=1&keywords=adams+parachute+dry+fly&qid=1616342240&sprefix=adams+par%2Caps%2C181&sr=8-13"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

    }




}