package com.example.clang.carfixtracker;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by clang on 15/02/2018.
 */

public class DataParser {

    public List<List<HashMap<String, String>>> parse(JSONObject jsonObject){
        List<List<HashMap<String, String>>> routes = new ArrayList<>();
        JSONArray jsonRoutes = null;
        JSONArray jsonLegs = null;
        JSONArray jsonSteps = null;

        try {
            jsonRoutes = jsonObject.getJSONArray("routes");

            for(int i = 0; i < jsonRoutes.length(); i++){
                jsonLegs = ((JSONObject) jsonRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList<HashMap<String,String>>();

                for(int j = 0; j < jsonLegs.length(); j++){
                    jsonSteps = ((JSONObject) jsonLegs.get(i)).getJSONArray("steps");

                    for (int k = 0; k < jsonSteps.length(); k++){
                        String polyline = "";

                        polyline = (String)((JSONObject)((JSONObject)(jsonSteps.get(k))).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);

                        for(int l = 0; l < list.size(); l++){
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("lat", Double.toString(list.get(l).latitude));
                            hashMap.put("lng", Double.toString(list.get(l).longitude));
                            path.add(hashMap);
                        }
                    }
                    routes.add(path);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return routes;
    }

    private List<LatLng> decodePoly(String polyline) {
        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, lat = 0, lng = 0;
        int len = polyline.length();

        while(index < len){
            int a, shift = 0, result = 0;

            do {
                a = polyline.charAt(index++) - 63;
                result |= (a & 0x1f) << shift;
                shift += 5;
            } while (a >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                a = polyline.charAt(index++) - 63;
                result |= (a & 0x1f) << shift;
                shift += 5;
            } while (a >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng point = new LatLng((((double) lat / 1E5)),(((double) lng / 1E5)));
            poly.add(point);
        }
        return poly;
    }
}
