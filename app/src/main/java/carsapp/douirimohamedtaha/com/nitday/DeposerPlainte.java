package carsapp.douirimohamedtaha.com.nitday;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.NoConnectionError;
import com.android.volley.error.TimeoutError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.SimpleMultiPartRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.Permission;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

import static carsapp.douirimohamedtaha.com.nitday.Home.SERVER_ADDRESS;


public class DeposerPlainte extends AppCompatActivity implements OnMapReadyCallback, LocationListener {
    Button button;
    // ImageView imageView;
    EditText txtDesc;
    Intent intent;
    public static final int RequestPermissionCode = 1;
    private GoogleMap map;
    private LatLng position;
    private File mFile;
    private OutputStream outStream;
    private ArrayList<File> photos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposer_plainte);
        button = (Button) findViewById(R.id.btnPic);
        txtDesc = (EditText) findViewById(R.id.txtDesc);
        //imageView = (ImageView) findViewById(R.id.im);
        EnableRuntimePermission();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Nammu.init(getApplicationContext());
        if (Nammu.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //Its ok, do stuff here...
        } else {
            //Asking for permission
            //Third parameter is *this* Fragment. So the callbacks should fire here, in this class instance. Which is a Fragment.
            Nammu.askForPermission(DeposerPlainte.this, Manifest.permission.READ_EXTERNAL_STORAGE, new PermissionCallback() {
                @Override
                public void permissionGranted() {

                }

                @Override
                public void permissionRefused() {

                }
            });
        }
        if (Nammu.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //Its ok, do stuff here...
        } else {
            //Asking for permission
            //Third parameter is *this* Fragment. So the callbacks should fire here, in this class instance. Which is a Fragment.
            Nammu.askForPermission(DeposerPlainte.this, Manifest.permission.READ_EXTERNAL_STORAGE, new PermissionCallback() {
                @Override
                public void permissionGranted() {

                }

                @Override
                public void permissionRefused() {

                }
            });
        }
        if (Nammu.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            //Its ok, do stuff here...
        } else {
            //Asking for permission
            //Third parameter is *this* Fragment. So the callbacks should fire here, in this class instance. Which is a Fragment.
            Nammu.askForPermission(DeposerPlainte.this, Manifest.permission.ACCESS_FINE_LOCATION, new PermissionCallback() {
                @Override
                public void permissionGranted() {

                }

                @Override
                public void permissionRefused() {

                }
            });
        }
        button.setOnClickListener(v -> {
            EasyImage.openCamera(DeposerPlainte.this, 0);

        });
    }

    public void AddComplaint(File f, double lat, double longi) {
        SimpleMultiPartRequest smr = new SimpleMultiPartRequest(Request.Method.POST, SERVER_ADDRESS + "ticket/add",
                response -> {
                    Log.d("Response", response);
                    Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_LONG).show();
                }, error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show());
        smr.addStringParam("description", txtDesc.getText().toString());
        smr.addStringParam("lat", "" + lat);
        smr.addStringParam("long", "" + longi);
        smr.addStringParam("etat", "0");
        smr.addStringParam("utilisateur", "0");
        smr.addStringParam("agent", "");
        smr.addFile("picture", f.getAbsolutePath());
        RequestQueue mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        mRequestQueue.add(smr);
        Intent i = new Intent(DeposerPlainte.this, Home.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
            @Override
            public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                //Some error handling
            }

            @Override
            public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                Log.d("Image", imageFile.getAbsolutePath());
                //AddTicket(imageFile, txtDesc.getText().toString(), position);
                saveProfileAccount(imageFile,SERVER_ADDRESS+"ticket/add","5ac9835ab13d400410737fb5");
            }


        });
    }

    public void AddTicket(File f, String description, LatLng position) {
        SimpleMultiPartRequest smr = new SimpleMultiPartRequest(Request.Method.POST, SERVER_ADDRESS + "ticket/add",
                response -> Log.d("Response", response),
                error -> Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show());
        smr.addStringParam("lat",position.latitude+"");
        smr.addStringParam("long",position.longitude+"");
        smr.addStringParam("description",description);
        //smr.addFile("image", f.getAbsolutePath());
        RequestQueue queue = Volley.newRequestQueue(DeposerPlainte.this);
        Log.d("request", smr.getBodyContentType());
        queue.add(smr);

    }

    public void EnableRuntimePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(DeposerPlainte.this,
                Manifest.permission.CAMERA)) {

            Toast.makeText(DeposerPlainte.this, "CAMERA permission allows us to Access CAMERA app", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(DeposerPlainte.this, new String[]{
                    Manifest.permission.CAMERA}, RequestPermissionCode);

        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Double lat = location.getLatitude();
        Double lng = location.getLongitude();
        position = new LatLng(lat, lng);
        Log.i("Location info: Lat", lat.toString());
        Log.i("Location info: Lng", lng.toString());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        googleMap.setMyLocationEnabled(true);
        map.setMyLocationEnabled(true);

        googleMap.clear();
        googleMap.setOnMapClickListener(point -> {
            position = point;
            googleMap.clear();
            googleMap.addMarker(new MarkerOptions().position(point));
        });
    }






    public static byte[] getFileDataFromDrawable(Context context, int id) {
        Drawable drawable = ContextCompat.getDrawable(context, id);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * Turn drawable into byte array.
     *
     * @param drawable data
     * @return byte array
     */
    public static byte[] getFileDataFromDrawable(Context context, Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void saveProfileAccount(File f , String url , String id) {
        // loading or check internet connection or something...
        // ... then
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, response -> {
            String resultResponse = new String(response.data);
            Intent i = new Intent(DeposerPlainte.this,Home.class);
            startActivity(i);
        }, error -> {
            NetworkResponse networkResponse = error.networkResponse;
            String errorMessage = "Unknown error";
            if (networkResponse == null) {
                if (error.getClass().equals(TimeoutError.class)) {
                    errorMessage = "Request timeout";
                } else if (error.getClass().equals(NoConnectionError.class)) {
                    errorMessage = "Failed to connect server";
                }
            } else {
                String result = new String(networkResponse.data);
                try {
                    JSONObject response = new JSONObject(result);
                    String status = response.getString("status");
                    String message = response.getString("message");

                    Log.e("Error Status", status);
                    Log.e("Error Message", message);

                    if (networkResponse.statusCode == 404) {
                        errorMessage = "Resource not found";
                    } else if (networkResponse.statusCode == 401) {
                        errorMessage = message+" Please login again";
                    } else if (networkResponse.statusCode == 400) {
                        errorMessage = message+ " Check your inputs";
                    } else if (networkResponse.statusCode == 500) {
                        errorMessage = message+" Something is getting wrong";
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Log.i("Error", errorMessage);
            error.printStackTrace();
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("lat",position.latitude+"");
                params.put("long",position.latitude+"");
                params.put("description",txtDesc.getText().toString());
                params.put("etat","0");
                params.put("utilisateur",id);
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                params.put("picture", new DataPart("file_avatar.jpg",getFileDataFromDrawable(getBaseContext(), Drawable.createFromPath(f.getAbsolutePath())), "image/jpeg"));

                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(DeposerPlainte.this);
        Log.d("request", multipartRequest.getBodyContentType());
        queue.add(multipartRequest);
    }
}
