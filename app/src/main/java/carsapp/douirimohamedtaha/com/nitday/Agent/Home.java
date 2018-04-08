package carsapp.douirimohamedtaha.com.nitday.Agent;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.request.JsonObjectRequest;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.nightonke.boommenu.BoomButtons.HamButton;
import com.nightonke.boommenu.BoomMenuButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import carsapp.douirimohamedtaha.com.nitday.Adapters.StatAdapter;
import carsapp.douirimohamedtaha.com.nitday.Adapters.StatOnHoldAdapter;
import carsapp.douirimohamedtaha.com.nitday.CheckStates;
import carsapp.douirimohamedtaha.com.nitday.Entities.Enum.EstabType;
import carsapp.douirimohamedtaha.com.nitday.Entities.Municipalite;
import carsapp.douirimohamedtaha.com.nitday.Entities.Ticket;
import carsapp.douirimohamedtaha.com.nitday.R;

import static carsapp.douirimohamedtaha.com.nitday.Home.MY_LOCATION_REQUEST_CODE;
import static carsapp.douirimohamedtaha.com.nitday.Home.SERVER_ADDRESS;

public class Home extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {
    List<Ticket> taskList;
    Ticket task;
    //RecyclerView rv ;
    //StatOnHoldAdapter adapter;
    GoogleMap mMap;
    private boolean perGranted;
    private boolean mapReady = false;
    private float zoomLevel = 10;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private BoomMenuButton bmb;
    private HamButton.Builder boomButtonDeposer;
    private HamButton.Builder boomButtonCheckCar;
    private HamButton.Builder boomButtonCheckState;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                GetLocations(0);
                return true;
            case R.id.navigation_dashboard:
                GetLocations(1);
                return true;
        }
        return false;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home2);
        taskList = new ArrayList<>();
        mFusedLocationProviderClient = LocationServices
                .getFusedLocationProviderClient(this);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        /*rv = (RecyclerView)findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setItemAnimator(new DefaultItemAnimator());
        adapter=new StatOnHoldAdapter(taskList,this);
        rv.setAdapter(adapter);
        checkOnHold();*/
    }

    private void checkOnHold() {
        String url = SERVER_ADDRESS + "parking";
        RequestQueue queue = Volley.newRequestQueue(Home.this);
        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.GET,
                "http://192.168.1.211:8081/ticket", null, response -> {
            Log.d("ResOngoing", response.toString());
            // Display the first 500 characters of the response string.
            JSONArray task_array = null;
            try {

                task_array = response.getJSONArray("tickets");
                taskList = new ArrayList<>();

                for (int i = 0; i < task_array.length(); i++) {

                    JSONObject obj = task_array.getJSONObject(i);
                    Ticket t = new Ticket();
                    String date = obj.getString("date");
                    String etat = obj.getString("etat");
                    String id = obj.getString("_id");
                    String pic = obj.getString("picture");
                    String description = obj.getString("description");
                    double lat = obj.getDouble("lat");
                    double longi = obj.getDouble("long");
                    if (etat.contains("0")) {
                        Marker m = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(lat, longi))
                                .snippet(date)
                                .title(description));
                        m.setTag(id);
                        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(m.getPosition(), zoomLevel);
                        mMap.moveCamera(update);
                    }
                    //t.setLat(Double.parseDouble(obj.getString("lat")));
                    //t.setLongi(Double.parseDouble(obj.getString("long")));
                }
            } catch (JSONException e) {
                // JSON error
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        " ERROR : Car not found !", Toast.LENGTH_LONG).show();

            }

        }, error -> {
            Toast.makeText(getApplicationContext(),
                    "Car not found", Toast.LENGTH_LONG).show();

        }) {
        };
        queue.add(strReq);// Adding request to request queue
    }


    private void CheckMy(String idd) {


        String url = SERVER_ADDRESS + "parking";
        RequestQueue queue = Volley.newRequestQueue(Home.this);
        JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.POST,
                "http://192.168.1.211:8081/ticket/agenttickets", null, response -> {
            Log.d("ResOngoing", response.toString());
            // Display the first 500 characters of the response string.
            try {
                JSONArray task_array = new JSONArray(response);
                taskList = new ArrayList<>();

                for (int i = 0; i < task_array.length(); i++) {

                    JSONObject obj = task_array.getJSONObject(i);
                    Ticket t = new Ticket();
                    String date = obj.getString("date");
                    String etat = obj.getString("etat");
                    String id = obj.getString("_id");
                    String pic = obj.getString("picture");
                    String description = obj.getString("description");
                    double lat = obj.getDouble("lat");
                    double longi = obj.getDouble("long");
                    Marker m = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(lat, longi))
                            .snippet(date)
                            .title(description));
                    m.setTag(id);
                    CameraUpdate update = CameraUpdateFactory.newLatLngZoom(m.getPosition(), zoomLevel);
                    mMap.moveCamera(update);
                    //t.setLat(Double.parseDouble(obj.getString("lat")));
                    //t.setLongi(Double.parseDouble(obj.getString("long")));
                }
            } catch (JSONException e) {
                // JSON error
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        " ERROR : Car not found !", Toast.LENGTH_LONG).show();

            }

        }, error -> {
            Toast.makeText(getApplicationContext(),
                    "Not found", Toast.LENGTH_LONG).show();
            error.printStackTrace();

        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("utilisateur", idd);
                return params;
            }
        };
        queue.add(strReq);// Adding request to request queue
    }

    /* private void checkOnHold() {
         ProgressDialog progress = new ProgressDialog(this);
         progress.setTitle("Loading");
         progress.setMessage("Wait while we look for your car ...");
         progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
         progress.show();
         String TAG = "checkCar";
         // Tag used to cancel the request
         RequestQueue queue = Volley.newRequestQueue(Home.this);
         JsonObjectRequest strReq = new JsonObjectRequest(Request.Method.GET,
                 "http://192.168.1.211:8081/ticket",null, response -> {
             Log.d("ResOngoing",response.toString());
             // Display the first 500 characters of the response string.
             JSONArray task_array = null;
             try {

                 task_array = response.getJSONArray("tickets");
                 taskList = new ArrayList<>();

                 for (int i = 0; i < task_array.length(); i++) {

                     JSONObject obj = task_array.getJSONObject(i);
                     Ticket t = new Ticket();
                     String date = obj.getString("date");
                     String etat = obj.getString("etat");
                     String id = obj.getString("_id");
                     String pic = obj.getString("picture");
                     String description = obj.getString("description");
                     t.setPic(pic);
                     t.setDate(date);
                     t.setDescription(description);
                     t.setEtat(etat);
                     //t.setLat(Double.parseDouble(obj.getString("lat")));
                     //t.setLongi(Double.parseDouble(obj.getString("long")));
                     taskList.add(t);
                 }
                 adapter = new StatOnHoldAdapter(taskList, Home.this);
                 adapter.notifyDataSetChanged();
                 rv.setAdapter(adapter);
                 progress.dismiss();
             }catch (JSONException e) {
                 // JSON error
                 e.printStackTrace();
                 progress.dismiss();
                 Toast.makeText(getApplicationContext(),
                         " ERROR : Car not found !", Toast.LENGTH_LONG).show();

             }

         }, error -> {
             Log.e(TAG, "Login Error: " + error.getMessage());
             progress.dismiss();
             Toast.makeText(getApplicationContext(),
                     "Car not found", Toast.LENGTH_LONG).show();

         }) {
         };
         queue.add(strReq);// Adding request to request queue
     }
 */
    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapReady = true;
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.setOnMarkerClickListener(this);
            mMap.getUiSettings().setMapToolbarEnabled(true);
            getDeviceLocation();
            GetLocations(0);

        } else {
            // Show rationale and request permission.
            ActivityCompat.requestPermissions(Home.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_LOCATION_REQUEST_CODE);
        }
        //Place Markers
    }

    private void getDeviceLocation() {
        try {
            Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        Location location = task.getResult();
                        LatLng currentLatLng = new LatLng(location.getLatitude(),
                                location.getLongitude());
                        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(currentLatLng, zoomLevel);
                        mMap.moveCamera(update);
                    }
                }
            });
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public void GetLocations(int i) {

        if (mapReady) {
            switch (i) {
                case 0: {
                    //Getting Municipalités
                    mMap.clear();
                    checkOnHold();
                    break;
                }
                case 1: {
                    //Getting Parkings
                    mMap.clear();
                    CheckMy("5ac9b8956a95250f100810cf");
                    break;
                }
            }
        }
    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
        builder.setPositiveButton(R.string.take_care, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Call
                HandleTicket(marker, "");
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Text
            }
        });
        builder.setTitle("Contacter");
        builder.setMessage("Voulez vous appeler ou envoyer un SMS ?");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void HandleTicket(Marker m, String id) {
        ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Assigning ticket in progress ...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
        String TAG = "checkCar";
        // Tag used to cancel the request
        RequestQueue queue = Volley.newRequestQueue(Home.this);
        StringRequest strReq = new StringRequest(Request.Method.POST,
                SERVER_ADDRESS + "priseencharge", response -> {
            Log.d(TAG, "Login Response: " + response.toString());
            try {
                JSONObject jObj = new JSONObject(response);
                // user successfully logged in
                // Create login session

                // Now store the user in SQLite
                String date = jObj.getString("date");
                String matricule = jObj.getString("matricule");
                JSONObject fourriere = jObj.getJSONObject("fourriere");
                int tel = fourriere.getInt("tel");
                String adresse = fourriere.getString("adresse");
                double lat = fourriere.getDouble("lat");
                double longi = fourriere.getDouble("long");
                String heureDebut = fourriere.getString("heureDebut");
                String heureFin = fourriere.getString("heureFin");
                m.remove();
                progress.dismiss();

            } catch (JSONException e) {
                // JSON error
                e.printStackTrace();
                progress.dismiss();
                Toast.makeText(getApplicationContext(),
                        " ERROR : Car not found !", Toast.LENGTH_LONG).show();

            }

        }, error -> {
            Log.e(TAG, "Login Error: " + error.getMessage());
            progress.dismiss();
            Toast.makeText(getApplicationContext(),
                    "Car not found", Toast.LENGTH_LONG).show();

        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("_id ", m.getTag().toString());
                params.put("etat  ", "1");
                params.put("agent ", id);
                return params;
            }

        };

        queue.add(strReq);// Adding request to request queue
    }

    private void LoadMyTickets(String id) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if (permissions.length == 1 &&
                    permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            } else {
                // Permission was denied. Display an error message.
            }
        }
    }

}
