package carsapp.douirimohamedtaha.com.nitday;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import carsapp.douirimohamedtaha.com.nitday.Adapters.StatAdapter;
import carsapp.douirimohamedtaha.com.nitday.Entities.Ticket;

import static carsapp.douirimohamedtaha.com.nitday.Home.SERVER_ADDRESS;

public class CheckStates extends AppCompatActivity {
    List<Ticket> taskList;
    Ticket task;
    RecyclerView rv ;
    StatAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_states);
        taskList = new ArrayList<>();
        rv = (RecyclerView)findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setItemAnimator(new DefaultItemAnimator());
        adapter=new StatAdapter(taskList,this);
        rv.setAdapter(adapter);
        SharedPreferences settings = CheckStates.this.getSharedPreferences("USER", 0);
        checkCarr(settings.getString("_id",null));
    }

    public void GetMyTickets(String s){
        ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Loading tickets list ..");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
        String TAG = "checkCar";
        // Tag used to cancel the request
        RequestQueue queue = Volley.newRequestQueue(CheckStates.this);
        StringRequest strReq = new StringRequest(Request.Method.POST,
                SERVER_ADDRESS+"ticket/mytickets", response -> {
            Log.d("ResOngoing",response);
            // Display the first 500 characters of the response string.
            JSONArray task_array = null;
            Ticket task = null;
            try {

                task_array = new JSONArray(response);
                taskList = new ArrayList<>();

                for (int i = 0; i < task_array.length(); i++) {

                    JSONObject obj = task_array.getJSONObject(i);
                    Ticket t = new Ticket();
                    String date = obj.getString("date");
                    String etat = obj.getString("etat");
                    if(etat!="0"){
                        String agent = obj.getString("agent");
                        t.setAgent(agent);
                    }
                    String id = obj.getString("_id");
                    String pic = obj.getString("picture");
                    String description = obj.getString("description");
                    t.setPic(pic);
                    t.setDate(date);
                    t.setDescription(description);
                    t.setEtat(etat);
                    Log.d("Task",task.toString());
                    taskList.add(task);
                }
                adapter=new StatAdapter(taskList,CheckStates.this);
                adapter.notifyDataSetChanged();
                rv.setAdapter(adapter);

            } catch (JSONException ex) {
                ex.printStackTrace();
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
                params.put("utilisateur", s);
                return params;
            }

        };

        queue.add(strReq);// Adding request to request queue
    }

    private void checkCarr(final String s) {
        ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while we look for your car ...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
        String TAG = "checkCar";
        // Tag used to cancel the request
        RequestQueue queue = Volley.newRequestQueue(CheckStates.this);
        StringRequest strReq = new StringRequest(Request.Method.POST,
                "http://10.13.0.64:8081/ticket/mytickets", response -> {
            Log.d("ResOngoing",response);
            // Display the first 500 characters of the response string.
            JSONArray task_array = null;
            try {

                task_array = new JSONArray(response);
                taskList = new ArrayList<>();

                for (int i = 0; i < task_array.length(); i++) {

                    JSONObject obj = task_array.getJSONObject(i);
                    Ticket t = new Ticket();
                    String date = obj.getString("date");
                    String etat = obj.getString("etat");
                    if (!etat.trim().contains("0")) {
                        String agent = obj.getString("agent");
                        t.setAgent(agent);
                    }
                    String id = obj.getString("_id");
                    String pic = obj.getString("picture");
                    String description = obj.getString("description");
                    t.setPic(pic);
                    t.setDate(date);
                    t.setDescription(description);
                    t.setEtat(etat);
                    taskList.add(t);
                }
                adapter = new StatAdapter(taskList, CheckStates.this);
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

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("utilisateur", s);
                return params;
            }

        };
        queue.add(strReq);// Adding request to request queue
    }

}
