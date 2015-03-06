package com.example.ryantemplin.geoapp;


        import android.app.DownloadManager;
        import android.content.Context;
        import android.content.Intent;
        import android.location.Geocoder;
        import android.net.ConnectivityManager;
        import android.net.NetworkInfo;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.app.Activity;
        import android.util.Log;
        import android.view.KeyEvent;
        import android.view.View;
        import android.view.inputmethod.EditorInfo;
        import android.view.inputmethod.InputMethodManager;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.TextView;

        import com.google.android.gms.maps.*;
        import com.google.android.gms.maps.model.LatLng;
        import com.google.android.gms.maps.model.Marker;
        import com.google.android.gms.maps.model.MarkerOptions;

        import org.json.JSONException;
        import org.json.JSONObject;

        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.io.Reader;
        import java.io.UnsupportedEncodingException;
        import java.net.HttpURLConnection;
        import java.net.MalformedURLException;
        import java.net.URL;
        import java.net.URLConnection;
        import java.util.Scanner;
        import java.util.StringTokenizer;

public class Main extends Activity implements OnMapReadyCallback {
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
        setContentView(R.layout.activity_main);

        mapFrag = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFrag.getMapAsync(this);
        gmap = mapFrag.getMap();
        box = (EditText) findViewById(R.id.enter_address);

        gmap.setMyLocationEnabled(true);
        gmap.getUiSettings().setMyLocationButtonEnabled(true);
        gmap.setBuildingsEnabled(true);
        gmap.setIndoorEnabled(false);
  //      gmap.setStreetView(true);






        Button button = (Button) findViewById(R.id.testButton);
        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(gmap.isBuildingsEnabled()){
                    gmap.setBuildingsEnabled(false);
                }else{
                    gmap.setBuildingsEnabled(true);
                }
                return true;
            }
        });
        Button streetButton = (Button)findViewById(R.id.street_Button);
        streetButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Main.this, StreetViewActivity.class);
                intent.putExtra("lat",latitude);
                intent.putExtra("long",longitude);
                startActivity(intent);
                finish();
            }
        });

    }


    private void hideSoftKeyboard(){
        if(getCurrentFocus()!=null && getCurrentFocus() instanceof EditText){
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(box.getWindowToken(), 0);
        }
    }

    public void getAddress(View view) {
        String address = box.getText().toString();
        String parsedAddress = address.replaceAll(" ", "+");
        hideSoftKeyboard();


        String stringUrl = String.format("https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=%s", parsedAddress, API_KEY);
        System.out.println(stringUrl);

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask().execute(stringUrl);
        } else {

        }
        try {
            URL url = new URL(stringUrl);
            URLConnection connection = url.openConnection();
            connection.setDoOutput(true);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void changeView(View view) {
        if (gmap.getMapType() == GoogleMap.MAP_TYPE_SATELLITE) {
            gmap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        } else if (gmap.getMapType() == GoogleMap.MAP_TYPE_NORMAL){
            gmap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        } else {
            gmap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }
        //https://maps.googleapis.com/maps/api/geocode/json?address
    }

    public void onMapReady(GoogleMap map) {

    }

    public void onMapReadyCallback() {

    }

    /*
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            //noinspection SimplifiableIfStatement
            if (id == R.id.action_settings) {
                return true;
            }

            return super.onOptionsItemSelected(item);
        }
    */
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            EditText myEditText = (EditText) findViewById(R.id.enter_address);
            myEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event)
                {
                    boolean handled=false;

                    // Some phones disregard the IME setting option in the xml, instead
                    // they send IME_ACTION_UNSPECIFIED so we need to catch that
                    if(EditorInfo.IME_ACTION_DONE==actionId || EditorInfo.IME_ACTION_UNSPECIFIED==actionId)
                    {
                        // do things here

                        handled=true;
                    }

                    return handled;
                }
            });
            try {
                JSONObject obj = new JSONObject(result);

                if (! obj.getString("status").equals("OK"))
                    return;
                // get the first result
                JSONObject res = obj.getJSONArray("results").getJSONObject(0);


                String formattedAddress = res.getString("formatted_address");
                JSONObject loc = res.getJSONObject("geometry").getJSONObject("location");
                latitude = loc.getDouble("lat");
                longitude = loc.getDouble("lng");
                LatLng nMarker = new LatLng(latitude, longitude);
                MarkerOptions options = new MarkerOptions();
                Marker marker = gmap.addMarker(options.position(nMarker).title(formattedAddress));
                // Show the current location in Google Map
                gmap.moveCamera(CameraUpdateFactory.newLatLng(nMarker));
                gmap.animateCamera(CameraUpdateFactory.zoomTo(16));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 5000;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //conn.setReadTimeout(100000 /* milliseconds */);
            //conn.setConnectTimeout(150000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    /*
        public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
        }

    */
    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Scanner reader = new Scanner(stream);
        String input = "";
        while(reader.hasNext()){
            input = input + reader.nextLine() + "\n";
        }
        return input;
    }
}