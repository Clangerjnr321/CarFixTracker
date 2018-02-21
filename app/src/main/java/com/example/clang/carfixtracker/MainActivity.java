package com.example.clang.carfixtracker;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by clang on 5/02/2018.
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#FFD91102"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
    }

    protected void displayMap(View view){
        Intent intent = new Intent(this, EnterDetailsActivity.class);
        startActivity(intent);
    }
}
