package carsapp.douirimohamedtaha.com.nitday;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
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
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomMenuButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import carsapp.douirimohamedtaha.com.nitday.Entities.Enum.EstabType;
import carsapp.douirimohamedtaha.com.nitday.Entities.Municipalite;

public class Home extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    public static final String SERVER_ADDRESS = "http://10.13.0.64:8081/";
    GoogleMap mMap;
    private boolean perGranted;
    private boolean mapReady = false;
    private float zoomLevel = 10;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private BoomMenuButton bmb;
    private HamButton.Builder boomButtonDeposer;
    private HamButton.Builder boomButtonCheckCar;
    private HamButton.Builder boomButtonCheckState;

    private List<Municipalite> parkingList;

    public static final int MY_LOCATION_REQUEST_CODE = 0;
    public static final int CALL_PHONE_REQUEST_CODE = 1;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                GetLocations(0);
                return true;
            case R.id.navigation_dashboard:
                GetLocations(1);
                return true;
            case R.id.navigation_notifications:
                GetLocations(2);
                return true;
        }
        return false;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mFusedLocationProviderClient = LocationServices
                .getFusedLocationProviderClient(this);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        bmb = (BoomMenuButton) findViewById(R.id.bmb);
        bmb.setNormalColor(getResources().getColor(R.color.primary));
        setUpBoomMenu();
        parkingList = new ArrayList<>();
    }

    private void setUpBoomMenu() {
      /*  if (Login.type.equals("Babysitter")) {

        } else {*/
        boomButtonDeposer = new HamButton.Builder()
                .normalImageRes(R.drawable.ic_dashboard_black_24dp)
                .normalTextRes(R.string.ham_deposer)
                .subNormalTextRes(R.string.ham_deposer_sub)
                .normalColorRes(R.color.mtlcred);
        bmb.addBuilder(boomButtonDeposer);
        boomButtonDeposer.listener(index -> {
            Intent i = new Intent(Home.this, DeposerPlainte.class);
            startActivity(i);
        });

        boomButtonCheckCar = new HamButton.Builder()
                .normalImageRes(R.drawable.ic_home_black_24dp)
                .normalTextRes(R.string.ham_checkcar)
                .subNormalTextRes(R.string.ham_checkcar_sub)
                .normalColorRes(R.color.mtlcblue);

        bmb.addBuilder(boomButtonCheckCar);
        boomButtonCheckCar.listener(index -> {
            //Alert with one field
            //**Handle it here ** //

            AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
            final EditText input = new EditText(Home.this);
            input.setHint("123tunis0123");
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            builder.setView(input);
            builder.setPositiveButton(R.string.check, (dialog, id) -> {
                // Call
                if(input.getText().toString().length()>6)
                checkCarr(input.getText().toString());
                else Toast.makeText(Home.this,"Please enter a valid sequence",Toast.LENGTH_LONG).show();
            });
            builder.setNegativeButton(R.string.cancel, (dialog, id) -> {
                // Text
                dialog.dismiss();
            });
            builder.setTitle("Rechercher voiture");
            builder.setMessage("Veuillez entrer l'immatriculation de votre voiture");
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        boomButtonCheckState = new HamButton.Builder()
                .normalImageRes(R.drawable.ic_notifications_black_24dp)
                .normalTextRes(R.string.ham_checkstate)
                .subNormalTextRes(R.string.ham_checkstate_sub)
                .normalColorRes(R.color.mtlcBlack);
        bmb.addBuilder(boomButtonCheckState);
        boomButtonCheckState.listener(new OnBMClickListener() {
            @Override
            public void onBoomButtonClick(int index) {
                Intent i = new Intent(Home.this, CheckStates.class);
                startActivity(i);
            }
        });
        //}

    }


    private void checkCarr(final String s) {
        ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while we look for your car ...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
        String TAG = "checkCar";
        // Tag used to cancel the request
        RequestQueue queue = Volley.newRequestQueue(Home.this);
        StringRequest strReq = new StringRequest(Request.Method.POST,
                "http://192.168.1.211:8081/mise/check", response -> {
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
                //String heureDebut = fourriere.getString("heureDebut");
                //String heureFin = fourriere.getString("heureFin");
                mMap.clear();
                Marker m = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lat, longi))
                        .snippet("Date : " + date)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car_location_marker))
                        .title(adresse));
                m.setTag(tel);
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(m.getPosition(), 15);
                mMap.moveCamera(update);
                progress.dismiss();

            } catch (JSONException e) {
                // JSON error
                e.printStackTrace();
                progress.dismiss();
                Toast.makeText(getApplicationContext(),
                        "Car not found !", Toast.LENGTH_LONG).show();

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
                params.put("matricule", s);
                return params;
            }

        };

        queue.add(strReq);// Adding request to request queue
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
                    GetMunicipalites();
                    break;
                }
                case 1: {
                    //Getting Parkings
                    mMap.clear();
                    GetParkings();
                    break;
                }
                case 2: {
                    //Getting Fourrieres
                    mMap.clear();
                    GetFourrieres();
                    break;
                }
            }
        }
    }

    public void GetParkings() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = SERVER_ADDRESS + "parking";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, response -> {
                    JSONArray parkingArray = null;
                    Municipalite parking = null;
                    try {
                        parkingArray = response.getJSONArray("parkings");
                        parkingList = new ArrayList<>();

                        for (int i = 0; i < parkingArray.length(); i++) {

                            JSONObject obj = parkingArray.getJSONObject(i);
                            parking = new Municipalite();

                            parking.setNumber(obj.getInt("tel"));
                            parking.setLat((float) obj.getDouble("lat"));
                            parking.setLongi((float) obj.getDouble("long"));
                            parking.setHeureDebut("08:00");
                            parking.setHeureFin("18:00");
                            parking.setType(EstabType.Parking);
                            parking.setAddress(obj.getString("adresse"));
                            parking.setTarif(obj.getString("tarif"));
                            parking.setCapacite(obj.getInt("capacite"));
                            Log.d("Parking", parking.toString());

                            Marker m = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(parking.getLat(), parking.getLongi()))
                                    .snippet("Capacité : " + parking.getCapacite())
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pointer_map_parking_place_512))
                                    .title(parking.getAddress()));
                            m.setTag(parking.getNumber());
                            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(m.getPosition(), zoomLevel);
                            mMap.moveCamera(update);
                            parkingList.add(parking);
                        }


                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                }, error -> {
                    Log.d("Error", "" + error.toString());

                });
        queue.add(jsonObjectRequest);
    }

    public void GetMunicipalites() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = SERVER_ADDRESS + "municipalite";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, response -> {
                    JSONArray parkingArray = null;
                    Municipalite parking = null;
                    try {

                        parkingArray = response.getJSONArray("municipalites");
                        parkingList = new ArrayList<>();

                        for (int i = 0; i < parkingArray.length(); i++) {

                            JSONObject obj = parkingArray.getJSONObject(i);
                            parking = new Municipalite();

                            parking.setNumber(obj.getInt("tel"));
                            parking.setLat((float) obj.getDouble("lat"));
                            parking.setLongi((float) obj.getDouble("long"));
                            //parking.setHeureDebut(obj.getString("heureDebut"));
                            //parking.setHeureFin(obj.getString("heureFin"));
                            parking.setHeureDebut("08:00");
                            parking.setHeureFin("17:00");
                            parking.setType(EstabType.Municipalite);
                            parking.setAddress(obj.getString("adresse"));
                            parking.setTarif("");
                            parking.setCapacite(0);
                            Log.d("Parking", parking.toString());

                            Marker m = mMap.addMarker(new MarkerOptions()
                                    .position(new LatLng(parking.getLat(), parking.getLongi()))
                                    .snippet("Horaires : " + parking.getHeureDebut() + "-" + parking.getHeureFin())
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home_location_marker))
                                    .title(parking.getAddress()));
                            m.setTag(parking.getNumber());
                            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(m.getPosition(), zoomLevel);
                            mMap.moveCamera(update);
                            parkingList.add(parking);
                        }


                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                }, error -> {
                    // TODO: Handle error
                    Log.d("Error", "" + error.toString());

                });
        queue.add(jsonObjectRequest);
    }

    public void GetFourrieres() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = SERVER_ADDRESS + "fourriere";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray parkingArray = null;
                        Municipalite parking = null;
                        try {

                            parkingArray = response.getJSONArray("fourriere");
                            parkingList = new ArrayList<>();

                            for (int i = 0; i < parkingArray.length(); i++) {

                                JSONObject obj = parkingArray.getJSONObject(i);
                                parking = new Municipalite();

                                parking.setNumber(obj.getInt("tel"));
                                parking.setLat((float) obj.getDouble("lat"));
                                parking.setLongi((float) obj.getDouble("long"));
                                parking.setHeureDebut("08:00");
                                parking.setHeureFin("18:00");
                                parking.setType(EstabType.Fourriere);
                                parking.setAddress(obj.getString("adresse"));
                                parking.setTarif("");
                                parking.setCapacite(0);
                                Log.d("Parking", parking.toString());

                                Marker m = mMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(parking.getLat(), parking.getLongi()))
                                        .snippet("Horaires : " + parking.getHeureDebut() + "-" + parking.getHeureFin())
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car_location_marker))
                                        .title(parking.getAddress()));
                                m.setTag(parking.getNumber());
                                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(m.getPosition(), zoomLevel);
                                mMap.moveCamera(update);
                                parkingList.add(parking);
                            }


                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                }, error -> {
                    // TODO: Handle error
                    Log.d("Error", "" + error.toString());

                });
        queue.add(jsonObjectRequest);
    }

    private void TextNumber(String numb) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", numb, null)));
    }

    private void CallNumber(String numb) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.fromParts("tel", numb, null));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Home.this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    CALL_PHONE_REQUEST_CODE);
            return;
        }
        startActivity(intent);
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

    @Override
    public boolean onMarkerClick(final Marker marker) {
        Snackbar mySnackbar = Snackbar.make(findViewById(R.id.container),
                marker.getTitle(), Snackbar.LENGTH_SHORT);
        mySnackbar.setAction(R.string.contact, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                builder.setPositiveButton(R.string.call, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Call
                        CallNumber(marker.getTag().toString());
                    }
                });
                builder.setNegativeButton(R.string.text, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Text
                        TextNumber(marker.getTag().toString());
                    }
                });
                builder.setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        dialog.dismiss();
                    }
                });
                builder.setTitle("Contacter");
                builder.setMessage("Voulez vous appeler ou envoyer un SMS ?");
                AlertDialog dialog = builder.create();
                /*Button button = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                if (marker.getTag().toString().charAt(0) == '7') {
                    button.setEnabled(true);
                } else
                    button.setEnabled(false);*/
                dialog.show();
            }
        });
        mySnackbar.show();
        return false;
    }
}
