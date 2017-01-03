package com.edgeman.test;

import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.graphics.Color.GRAY;
import static com.google.android.gms.internal.zzir.runOnUiThread;

/**
 * Created by WebberWu on 2017/1/3.
 */

public class PokeThread extends Thread {

//    String path_json = "http://nyapass.gear.host/getStop.php";
    private String path_json = "";
    private String result_json = null;
    private LatLng mylatlng;
    private PokeStop pokestops[];
    private GoogleMap mMap;
    private Marker marker[];
    public LatLng response[];
    public PokeThread(String path,LatLng latlng,GoogleMap googleMap,Marker marker){
        path_json = "http://nyapass.gear.host/"+path;
        mylatlng = latlng;
        mMap = googleMap;
    }

        /* This program downloads a URL and print its contents as a string.*/
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("lat",Double.toString(mylatlng.latitude))
                .add("lng",Double.toString(mylatlng.longitude))
                .build();

        String run(String url) throws IOException {
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();
        }

        Runnable task = new Runnable() {
            @Override
            public void run() {
                //使用 gson 解析 json 資料
                Log.d("result", result_json);
                if (result_json != null) {
                    Gson gson = new Gson();
                    pokestops = gson.fromJson(result_json, PokeStop[].class);
                    StringBuilder sb = new StringBuilder();

                    for (PokeStop pokestop : pokestops) {
                        LatLng t1 = new LatLng(pokestop.getLat() , pokestop.getLng());
                        //addPokeMarker(t1, pokestop.getStopID()+"", pokestop.getStopID()+"");
                    }
                }
            }
        };

        @Override
        public void run() {
            try {
                //1.抓資料
                result_json = run(path_json);
                //2.改變畫面內容只能用主執行緒(Android機制)
                runOnUiThread(task);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    /*
    public Marker addPokeMarker(LatLng latLng, String title, String stopid) {
        BitmapDescriptor descriptor = (
                BitmapDescriptorFactory.fromResource(
                        getResources().getIdentifier("pokestop", "drawable", getPackageName())
                )
        );

        ArrayList<LatLng> latLngs = new ArrayList<>();

        latLngs.add(new LatLng(25.035877438787313,121.43135905265808));
        latLngs.add(new LatLng(25.035896880358834,121.43243193626404));
        latLngs.add(new LatLng(25.03476926411677,121.43314003944397));
        latLngs.add(new LatLng(25.03445819574321,121.43213152885437));
        latLngs.add(new LatLng(25.035196981842528,121.4309298992157));

        for( int i = 1; i < latLngs.size(); i++ ){
            this.mMap.addPolyline(
                    new PolylineOptions()
                            .add(latLngs.get(i-1),latLngs.get(i))
                            .width(20)
                            .color(Color.rgb(204, 0, 204))
                            .geodesic(true)

            );
        }



        MarkerOptions mko = new MarkerOptions()
                .position(latLng)
                .title(title)
                .icon(descriptor);
        MapsActivity.infoAdapter adapter111 = new MapsActivity.infoAdapter();
        mMap.setInfoWindowAdapter(adapter111);
        mMap.addMarker(mko).showInfoWindow();
        return (
                this.mMap.addMarker(mko)
        );
    }*/
}
