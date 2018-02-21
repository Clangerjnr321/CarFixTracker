package com.example.clang.carfixtracker;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.GeoApiContext;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    private ArrayList<LatLng> markerPoints;

    private LatLng locationPickUp;
    private LatLng locationDropOff;
    private LatLng locationMechanic;

    private List<Address> pickUpAddress;
    private List<Address> dropOffAddress;
    private List<Address> mechanicAddress;


    /**
     * Create the main activity.
     * @param savedInstanceState previously saved instance data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#FFD91102"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);

        Geocoder geocoder = new Geocoder(this);

        markerPoints = new ArrayList<>();

        String pickup = getIntent().getStringExtra("PickUp").toString();
        String dropoff = getIntent().getStringExtra("DropOff").toString();
        String mechanic = getIntent().getStringExtra("Mechanic").toString();

        try {
            pickUpAddress = geocoder.getFromLocationName(pickup, 1);
            dropOffAddress = geocoder.getFromLocationName(dropoff, 1);
            mechanicAddress = geocoder.getFromLocationName(mechanic,1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        locationPickUp = new LatLng(pickUpAddress.get(0).getLatitude(), pickUpAddress.get(0).getLongitude());
        locationDropOff = new LatLng(dropOffAddress.get(0).getLatitude(), dropOffAddress.get(0).getLongitude());
        locationMechanic = new LatLng(mechanicAddress.get(0).getLatitude(), mechanicAddress.get(0).getLongitude());


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private GeoApiContext getGeoContext(){
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext.setQueryRateLimit(3).setApiKey(getString(R.string.google_app_id)).setConnectTimeout(1, TimeUnit.SECONDS).setReadTimeout(1, TimeUnit.SECONDS).setWriteTimeout(1, TimeUnit.SECONDS);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        UiSettings uiSettings = mMap.getUiSettings();

        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setAllGesturesEnabled(true);

        markerPoints.add(locationPickUp);
        markerPoints.add(locationMechanic);
        markerPoints.add(locationDropOff);

        MarkerOptions optionsP = new MarkerOptions();
        MarkerOptions optionsM = new MarkerOptions();
        MarkerOptions optionsD = new MarkerOptions();

        optionsP.position(locationPickUp);
        optionsM.position(locationMechanic);
        optionsD.position(locationDropOff);


        if(markerPoints.get(0) != null){
            optionsP.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        }
        if(markerPoints.get(1) != null){
            optionsM.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }
        if(markerPoints.get(2) != null){
            optionsD.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        }

        mMap.addMarker(optionsP);
        mMap.addMarker(optionsD);
        mMap.addMarker(optionsM);

        if(markerPoints.size() >= 3){
            LatLng origin = markerPoints.get(0);
            LatLng dest = markerPoints.get(2);
            LatLng mech = markerPoints.get(1);

            String url = getUrl(origin, mech, dest);
            FetchURL fetchURL = new FetchURL();
            fetchURL.execute(url);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(locationPickUp));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
        }


    }

    private String getUrl(LatLng origin, LatLng mech, LatLng dest) {

        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";


        String waypoints  = "waypoints=" + mech.latitude + "," + mech.longitude + "|";

        String parameters = str_origin + "&" + str_dest + "&"  + sensor + "&" + waypoints;
        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        Log.d("URL_TO_CONNECT", url.toString());
        return url;
    }


    private String downloadUrl(String string) throws IOException {
        String data = "";

        HttpURLConnection urlConnection = null;

        try{
            InputStream inputStream;
            //Creating and connecting to url
            URL url = new URL(string);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();

            //Reading data from url
            inputStream = url.openStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer sb = new StringBuffer();

            String line = "";
            while((line = bufferedReader.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();
            bufferedReader.close();
            inputStream.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } finally{
            urlConnection.disconnect();
        }
        return data;
    }


    private class FetchURL extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... strings) {
            String data = "";
            try{
                data = downloadUrl(strings[0]);
            }catch (Exception e){
                e.printStackTrace();
            }
            return data;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jsonObject = new JSONObject(strings[0]);
                DataParser parser = new DataParser();

                routes = parser.parse(jsonObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            ArrayList<LatLng> points = null;
            PolylineOptions polylineOptions = null;

            for(int i = 0; i < lists.size(); i++){
                points = new ArrayList<>();
                polylineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = lists.get(i);

                for(int j = 0; j < path.size(); j++){
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng pos = new LatLng(lat, lng);
                    points.add(pos);
                }

                polylineOptions.addAll(points);
                polylineOptions.width(10);
                polylineOptions.color(Color.BLUE);
            }
            mMap.addPolyline(polylineOptions);
        }
    }
}
