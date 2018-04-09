package carsapp.douirimohamedtaha.com.nitday;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.basgeekball.awesomevalidation.AwesomeValidation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnLogin;
    private EditText inputcin;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        inputcin = (EditText) findViewById(R.id.etEmail);
        inputPassword = (EditText) findViewById(R.id.etPassword);
        btnLogin = (Button) findViewById(R.id.btnNext);
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        ButterKnife.bind(this);
        final AwesomeValidation mAwesomeValidation = new AwesomeValidation(BASIC);
        mAwesomeValidation.addValidation(inputcin, "[0-9]{8}", "please enter a valid IDnumber");

        // Session manager
        session = new SessionManager(getApplicationContext());
/*
        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, Home.class);
            startActivity(intent);
            finish();
        }*/

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cin = inputcin.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                // Check for empty data in the form
                if (mAwesomeValidation.validate() && !password.isEmpty()) {
                    // login user
                    checkLogin(cin, password);
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Please enter the right credentials!", Toast.LENGTH_LONG).show();
                }
            }
        });
        TextView registerTxtView = (TextView) findViewById(R.id.txtSignUp);
        registerTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    /**
     * function to verify login details in mysql db
     * */
    private void checkLogin(final String cin, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Logging in ...");
        showDialog();
        RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
        StringRequest strReq = new StringRequest(Request.Method.POST,
                "http://10.13.0.64:8081/user/authenticate", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    // Check for error node in json
                    // user successfully logged in
                    // Create login session
                    session.setLogin(true);

                    // Now store the user in SQLite
                    String firstname = jObj.getString("firstname");
                    String lastname = jObj.getString("lastname");
                    String birthDate = jObj.getString("birthdate");
                    String type = jObj.getString("type");
                    String cin = jObj.getString("cin");
                    String id = jObj.getString("_id");
                    // String img = user.getString("image");
                    SharedPreferences settings = LoginActivity.this.getSharedPreferences("USER", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("firstname", firstname);
                    editor.putString("lastname", lastname);
                    editor.putString("birthdate", birthDate);
                    editor.putString("cin", cin);
                    editor.putString("_id", id);
                    // editor.putString("img", img);
                    editor.commit();
                    //db.deleteUsers();
                    // Inserting row in users table
                    //db.addUser(cin,firstname,lastname,birthDate);

                    // Launch main activity
                    if (type.equals("citizent")){
                        Intent intent = new Intent(LoginActivity.this, Home.class);
                        startActivity(intent);}
                    else{
                        Log.d("agent","agent");
                        Intent intent = new Intent(LoginActivity.this, carsapp.douirimohamedtaha.com.nitday.Agent.Home.class);
                        startActivity(intent);
                    }
                    finish();
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "This user isnt registered.", Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("cin", cin);
                params.put("password", password);
                return params;
            }

        };

        queue.add(strReq);// Adding request to request queue
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
