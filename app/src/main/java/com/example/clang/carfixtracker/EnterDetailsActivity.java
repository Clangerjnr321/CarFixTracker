package com.example.clang.carfixtracker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by clang on 5/02/2018.
 */

public class EnterDetailsActivity extends AppCompatActivity{

    public EditText emailAddress;
    public EditText numberPlate;
    public EditText name;

    private String dropoff = "";
    private String pickup = "";
    private String mechanic = "";
    private String email = "";
    private String carNumberPlate = "";

    private String firebaseName = "carfixtracker-7be2b";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enter_details_activity);

        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#FFD91102"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
    }

    protected void displayMap(View view){

        final Context context = this;

        emailAddress = findViewById(R.id.editText);
        numberPlate = findViewById(R.id.editText2);
        name = findViewById(R.id.editText3);

        if(numberPlate.getText().toString().isEmpty() || emailAddress.getText().toString().isEmpty()){
            Toast.makeText(this, "Both fields must be filled out", Toast.LENGTH_SHORT);
            return;
        }

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference reference = firebaseDatabase.getReference(name.getText().toString());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    dropoff = dataSnapshot.child("dropoff").getValue().toString();
                    pickup = dataSnapshot.child("pickup").getValue().toString();
                    email = dataSnapshot.child("email").getValue().toString();
                    mechanic = dataSnapshot.child("mechanic").getValue().toString();
                    carNumberPlate = dataSnapshot.child("numberplate").getValue().toString();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG", "Failed to read value");
            }
        });


        if(!emailAddress.getText().toString().equals(email)){
            Toast.makeText(context, "Please Enter correct Email Address", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!numberPlate.getText().toString().equalsIgnoreCase(carNumberPlate)){
            Toast.makeText(context, "Please Enter correct Number Plate", Toast.LENGTH_SHORT).show();
            return;
        }

        Bundle bundle = new Bundle();
        bundle.putString("DropOff", dropoff.toString());
        bundle.putString("PickUp", pickup.toString());
        bundle.putString("Mechanic", mechanic.toString());

        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtras(bundle);

        startActivity(intent);
    }

}
