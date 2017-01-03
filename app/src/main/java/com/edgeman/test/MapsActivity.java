package com.edgeman.test;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.R.id.button1;
import static android.R.id.input;
import static android.graphics.Color.GRAY;
import static android.graphics.Color.LTGRAY;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private final static String TAG = "MapsActivity";
    private GoogleMap mMap;
    String query = "";
    LatLng mylatlng;
    PokeStop[] pokestops;
    Marker[] marker = new Marker[65060];

    LocationManager lm;
    LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            mylatlng = new LatLng(location.getLatitude(), location.getLongitude());
            Toast.makeText(MapsActivity.this,
                    Double.toString(mylatlng.latitude) + " , " + Double.toString(mylatlng.longitude), Toast.LENGTH_SHORT).show();
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        final Button restart;
        Button redrawline;
        restart = (Button) findViewById(R.id.button_restart);
        redrawline = (Button) findViewById(R.id.button_drawline);
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        checkPermission();
        lm.requestLocationUpdates(lm.GPS_PROVIDER, 200, 5, locationListener);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //new RunWork().start();
        restart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //query = "SELECT * FROM `stop` WHERE lat >" + (mylatlng.latitude) + "-0.0045 && lat <" + (mylatlng.latitude) + "+0.0045 && lng > " + (mylatlng.longitude) + "-0.0064&&lng<" + (mylatlng.longitude) + "+0.0064";
                //Log.i("debug", query);

                mMap.clear();
                new RunWork().start();
            }
        });
        redrawline.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //query = "SELECT * FROM `stop` WHERE lat >" + (mylatlng.latitude) + "-0.0045 && lat <" + (mylatlng.latitude) + "+0.0045 && lng > " + (mylatlng.longitude) + "-0.0064&&lng<" + (mylatlng.longitude) + "+0.0064";
                mMap.clear();
                new findline().start();
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public Marker addPokeMarker(LatLng latLng, String title, String stopid, String pic) {
        BitmapDescriptor descriptor = (
                BitmapDescriptorFactory.fromResource(
                        getResources().getIdentifier(pic, "drawable", getPackageName())
                )
        );

        MarkerOptions mko = new MarkerOptions()
                .position(latLng)
                .title(title)
                .icon(descriptor);
        infoAdapter adapter111 = new infoAdapter();
        mMap.setInfoWindowAdapter(adapter111);
        mMap.addMarker(mko).showInfoWindow();
        return (
                this.mMap.addMarker(mko)
        );
    }


    public LatLng getLastKnownLocation() {
        LocationManager mLocationManager;
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        if (!checkPermission()) return null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        return new LatLng(bestLocation.getLatitude(), bestLocation.getLongitude());
    }

    private boolean checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.e("ERR", "No Permission");
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    10
            );
            return false;
        } else
            return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mylatlng = getLastKnownLocation();
        Toast.makeText(MapsActivity.this,
                Double.toString(mylatlng.latitude) + " , " + Double.toString(mylatlng.longitude), Toast.LENGTH_SHORT).show();

        Marker marker = addPokeMarker(mylatlng, "開起來的時候", "100", "eevee64");

        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        mMap.setMyLocationEnabled(true); // 右上角的定位功能；這行會出現紅色底線，不過仍可正常編譯執行
        mMap.getUiSettings().setZoomControlsEnabled(true);  // 右下角的放大縮小功能
        mMap.getUiSettings().setCompassEnabled(true);       // 左上角的指南針，要兩指旋轉才會出現
        mMap.getUiSettings().setMapToolbarEnabled(true);    // 右下角的導覽及開啟 Google Map功能



        /*mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(16));     // 放大地圖到 16 倍大
                */
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mylatlng, 16));
        infoAdapter adapter = new infoAdapter();
        mMap.setInfoWindowAdapter(adapter);

    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Maps Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    class infoAdapter implements GoogleMap.InfoWindowAdapter {
        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            View infoWindow = getLayoutInflater().inflate(R.layout.stopinfo, null);
            Log.i("test","testtttttttttt");
            ImageView iv =  (ImageView)findViewById(R.id.imageView1);
            TextView tv1 = (TextView) findViewById(R.id.text1);
            TextView tv2 = (TextView) findViewById(R.id.text2);

            return infoWindow;
        }
    }

    /*上網抓資料，需要另外開執行緒做處理(Android機制)*/
    class RunWork extends Thread {
        String path_json = "http://nyapass.gear.host/getStop.php";
        String result_json = null;

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
                        marker[pokestop.getStopID()] = addPokeMarker(t1, pokestop.getStopID()+"", pokestop.getStopID()+"", "pokestop");
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
    }
    /*上網抓資料，需要另外開執行緒做處理(Android機制)*/
    class findline extends Thread {
        String path_json = "http://nyapass.gear.host/find_shortest.php";
        String result_json = null;

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

                    ArrayList<LatLng> line_link = new ArrayList<>();
                    for (PokeStop pokestop : pokestops) {
                        LatLng t1 = new LatLng(pokestop.getLat(), pokestop.getLng());
                        marker[pokestop.getStopID()] = addPokeMarker(t1, pokestop.getStopID()+"", pokestop.getStopID()+"", "pokestop2");
                        line_link.add(t1);
                    }
                    drawLine(line_link);
                }
            }
            public void drawLine(ArrayList<LatLng> line_link){
                // 畫線囉，線的點在findline順便存起來了喔
                for(int i=0; i<line_link.size(); i++){
                    if (i == 0){
                        mMap.addPolyline(
                                new PolylineOptions()
                                        .add(mylatlng, line_link.get(i))
                                        .width(10)
                                        .color(Color.rgb(204, 0, 204))
                                        .geodesic(true)
                        );
                    }else {
                        mMap.addPolyline(
                                new PolylineOptions()
                                        .add(line_link.get(i-1),line_link.get(i))
                                        .width(10)
                                        .color(Color.rgb(204, 0, 204))
                                        .geodesic(true)
                        );
                    }
                }
                //連起來囉
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
    }

}