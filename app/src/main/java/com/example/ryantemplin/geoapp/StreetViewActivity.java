package com.example.ryantemplin.geoapp;


/**
 * Created by ryantemplin on 3/4/15.
 */

        import android.content.Context;
        import android.content.Intent;
        import android.net.ConnectivityManager;
        import android.net.NetworkInfo;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.support.v4.app.FragmentActivity;
        import android.view.View;
        import android.widget.Button;
        import android.widget.EditText;

        import com.google.android.gms.maps.CameraUpdateFactory;
        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.MapFragment;
        import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
        import com.google.android.gms.maps.StreetViewPanorama;
        import com.google.android.gms.maps.StreetViewPanoramaFragment;
        import com.google.android.gms.maps.model.LatLng;
        import com.google.android.gms.maps.model.Marker;
        import com.google.android.gms.maps.model.MarkerOptions;

        import org.json.JSONException;
        import org.json.JSONObject;

        import java.io.IOException;
        import java.io.InputStream;
        import java.io.UnsupportedEncodingException;
        import java.net.HttpURLConnection;
        import java.net.MalformedURLException;
        import java.net.URL;
        import java.net.URLConnection;
        import java.util.Scanner;


public class StreetViewActivity extends FragmentActivity
        implements OnStreetViewPanoramaReadyCallback {

    MapFragment mapFrag;
    GoogleMap gmap;
    EditText box;
    //  double latitude = -33.87365;
    //   double longitude = 151.20689;
    double latitude;// = 40.74881;
    double longitude;// = -73.98542;
    final String API_KEY = "AIzaSyAIq2f7WNOx3vHP_ESjowG8FrhBMl_tuGQ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_street_view);


        StreetViewPanoramaFragment streetViewPanoramaFragment =
                (StreetViewPanoramaFragment) getFragmentManager()
                        .findFragmentById(R.id.streetviewpanorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);

        Button backButton = (Button)findViewById(R.id.back_Button);
        backButton.bringToFront();
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(StreetViewActivity.this, Main.class);
                startActivity(intent);
                finish();
            }
        });
    }



    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
  //      double latitude = Main.getLatitude();
  //      double longitude = Main.getLongitude();
  //      if (latitude != 0 && longitude != 0) {
        Bundle extras = getIntent().getExtras();
        Double lng = extras.getDouble("long");
        Double lat = extras.getDouble("lat");
            streetViewPanorama.setPosition(new LatLng(lat,lng));

    }
}


//public class StreetViewActivity extends FragmentActivity
//        implements OnStreetViewPanoramaReadyCallback {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_street_view);
//        StreetViewPanoramaFragment streetViewPanoramaFragment =
//                (StreetViewPanoramaFragment) getFragmentManager()
//                        .findFragmentById(R.id.streetviewpanorama);
//        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);
//
//        Button backButton = (Button)findViewById(R.id.back_Button);
//        backButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Intent intent = new Intent(StreetViewActivity.this, Main.class);
//                startActivity(intent);
//                finish();
//            }
//        });
//    }
//
//
//    @Override
//    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
//        //      double latitude = Main.getLatitude();
//        //      double longitude = Main.getLongitude();
//        //      if (latitude != 0 && longitude != 0) {
//        streetViewPanorama.setPosition(new LatLng(103,97));
//
//    }
//}