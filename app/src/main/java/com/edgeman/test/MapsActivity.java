package com.edgeman.test;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.R.id.input;
import static android.graphics.Color.GRAY;
import static android.graphics.Color.LTGRAY;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback
{
    private final static String TAG = "MapsActivity";
    private GoogleMap mMap;
    String query="SELECT * FROM `TABLE 1` LIMIT 20";
    LatLng mylatlng ;
    PokeStop[] pokestops;
    Marker[] m = new Marker[65060];
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        final Button restart ;
        restart= (Button)findViewById(R.id.button_restart);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        new RunWork().start();
        restart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //mylatlng = getmyloc();
                mylatlng = new LatLng(25.035494,  121.431618);
                query = "SELECT * FROM `TABLE 1` WHERE lat >"+ mylatlng.latitude+"-0.0045 && lat <"+mylatlng.latitude+"+0.0045 && lng > "+mylatlng.longitude+"-0.0064&&lng<"+mylatlng.longitude+"+0.0064";
                Log.i("debug",query);

                mMap.clear();
                new RunWork().start();
            }
        });
    }
    

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
/* testing input

    void mapping(PokeStop[] input,GoogleMap mMap){
        int i;
        LatLng fju[ ];
        for(i=0;i<10;i++)
        {
                fju[i]=new LatLng(Pokestop[i].getLat,Pokestop[i].getLng);
        }
        mMap.addPolyline(new PolylineOptions().
                add(fju[0],fju[1],fju[2],fju[3],fju[4],fju[5],fju[6],fju[7],fju[8],fju[9]).
                width(5).
                color(GRAY).
                geodesic(true)
        );

    };

 */


    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        // LatLng sydney = new LatLng(-34, 151);  原本的座標值是雪梨某處
        // 替換上輔大的座標25.035494, 121.431618

        LatLng fju= new LatLng(25.035494,  121.431000);
        LatLng fju1 = new LatLng(25.035494,  121.431618);
        mMap.addMarker(new MarkerOptions().position(fju1).title("輔仁大學"));
        mMap.addMarker(new MarkerOptions().position(fju).title("誰知道"));
        mMap.addPolyline(new PolylineOptions().
                add(fju,fju1).
                width(5).
                color(GRAY).
                geodesic(true)
        );

        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        mMap.setMyLocationEnabled(true); // 右上角的定位功能；這行會出現紅色底線，不過仍可正常編譯執行
        mMap.getUiSettings().setZoomControlsEnabled(true);  // 右下角的放大縮小功能
        mMap.getUiSettings().setCompassEnabled(true);       // 左上角的指南針，要兩指旋轉才會出現
        mMap.getUiSettings().setMapToolbarEnabled(true);    // 右下角的導覽及開啟 Google Map功能



        /*mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(16));     // 放大地圖到 16 倍大
                */
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(fju,16));
    }


    /*上網抓資料，需要另外開執行緒做處理(Android機制)*/
    class RunWork extends Thread
    {
        String path_json ="http://nyapass.gear.host/";
        String result_json = null;

        /* This program downloads a URL and print its contents as a string.*/
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("query_string",query)
                .build();
        String run(String url) throws IOException {
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();

            Response response = client.newCall(request).execute();
            return response.body().string();
        }

        Runnable task = new Runnable()
        {
            @Override
            public void run() {
                //使用 gson 解析 json 資料
                Gson gson = new Gson();
                pokestops = gson.fromJson(result_json,PokeStop[].class);

                StringBuilder sb = new StringBuilder();
                for(PokeStop pokestop :pokestops){

                    LatLng t1 = new LatLng(pokestop.getLat(),  pokestop.getLng());
                    //25.031756, 121.426571             25.040756, 121.439397    && t1.longitude>121.426571 &&t1.longitude<121.439397
                    //if(t1.latitude > 25.035494 && t1.latitude <25.040756 && t1.longitude>121.426571 &&t1.longitude<121.439397) {
                        m[Integer.parseInt(pokestop.getStopID())] = mMap.addMarker(new MarkerOptions().position(t1).title(pokestop.getStopID()).visible(true));
                    //}
                }

            }
        };

        @Override
        public void run()
        {
            try {
                //1.抓資料
                result_json = run(path_json);
                //2.改變畫面內容只能用主執行緒(Android機制)
                runOnUiThread(task);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}