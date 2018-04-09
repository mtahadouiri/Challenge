package carsapp.douirimohamedtaha.com.nitday;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.android.volley.toolbox.Volley;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;

import static com.basgeekball.awesomevalidation.ValidationStyle.BASIC;

public class RegisterActivity extends AppCompatActivity {
    String gender = "homme";
    EditText inputFirstName;
    EditText inputLastName;
    EditText IDnumber;
    EditText inputEmail;
    EditText phoneNumber;
    EditText inputBirthDate;
    String date_time;
    String passwordConfirm;
    String password;
    TextView previous;
    private SessionManager session;
    private SQLiteHandler db;

    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        final ImageView imgMale = (ImageView) findViewById(R.id.imgMale);
        final ImageView imgFemale = (ImageView) findViewById(R.id.imgFemale);
        imgMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                imgMale.setImageResource(R.drawable.male_selected);
                imgFemale.setImageResource(R.drawable.female);
                gender = "homme";
            }
        });

        imgFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgMale.setImageResource(R.drawable.male);
                imgFemale.setImageResource(R.drawable.female_selected);
                gender = "femme";
            }
        });
        inputFirstName = (EditText) findViewById(R.id.txtFirstName);
        previous = (TextView) findViewById(R.id.previous);
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        inputLastName = (EditText) findViewById(R.id.txtLastName);
        IDnumber = (EditText) findViewById(R.id.IDnum);
        inputEmail = (EditText) findViewById(R.id.adress);
        phoneNumber = (EditText) findViewById(R.id.txtPhoneNumber);
        inputBirthDate = (EditText) findViewById(R.id.txtBirthDate);
        inputBirthDate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    datePicker();

                }
            }
        });

        //  EditText inputPassword = (EditText) findViewById(R.id.password);
        //    EditText inputPasswordConfirm = (EditText) findViewById(R.id.passwordConfirmation);

        ButterKnife.bind(this);
        final AwesomeValidation mAwesomeValidation = new AwesomeValidation(BASIC);
        final AwesomeValidation dialogAwesomeValidation = new AwesomeValidation(BASIC);
        mAwesomeValidation.addValidation(inputEmail, Patterns.EMAIL_ADDRESS, "please enter an email adress");
        mAwesomeValidation.addValidation(phoneNumber, RegexTemplate.TELEPHONE, "please enter a valid phone number");
        mAwesomeValidation.addValidation(inputFirstName, "[a-zA-Z\\s]+", "please enter a valid name");
        mAwesomeValidation.addValidation(inputLastName, "[a-zA-Z\\s]+", "please enter a valid name");
        mAwesomeValidation.addValidation(IDnumber, "[0-9]{8}", "please enter a valid IDnumber");
        mAwesomeValidation.addValidation(inputBirthDate, "^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]))\\1|(?:(?:29|30)(\\/|-|\\.)(?:0?[1,3-9]|1[0-2])\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\/|-|\\.)0?2\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9])|(?:1[0-2]))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$", "please enter a valid date");
        Button btnSignUp = (Button) findViewById(R.id.btnNext);

        // Progress dialog
        ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        SessionManager session = new SessionManager(getApplicationContext());

        // SQLite database handler
        SQLiteHandler db = new SQLiteHandler(getApplicationContext());
/*
        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(RegisterActivity.this, Home.class);
            startActivity(intent);
            finish();
        }*/

        // Register Button Click event
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                final String firstname = inputFirstName.getText().toString().trim();
                final String lastname = inputLastName.getText().toString().trim();
                final String cin = IDnumber.getText().toString().trim();
                final String birthdate = inputBirthDate.getText().toString().trim();
                final String email = inputEmail.getText().toString().trim();
                if (!(mAwesomeValidation.validate())) {
                    Toast.makeText(getApplicationContext(), "Please check your details!", Toast.LENGTH_LONG).show();
                } else {

                    // custom dialog
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.custom_dialog_password);
                    dialog.setTitle("Sign Up ");
                    Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                    final TextView text = (TextView) dialog.findViewById(R.id.passwordyesOrNo);
                    final EditText inputpassword = (EditText) dialog.findViewById(R.id.Password);
                    final EditText inputpasswordConfirm = (EditText) dialog.findViewById(R.id.checkPassword);

                    dialogAwesomeValidation.addValidation(inputpassword, "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$", "Your password must have Minimum eight characters, at least one uppercase letter, one number and one special character. ");
                    // if button is clicked, close the custom dialog
                    dialogButton.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            password = inputpassword.getText().toString().trim();
                            passwordConfirm = inputpasswordConfirm.getText().toString().trim();
                            if (!(dialogAwesomeValidation.validate())) {
                                Toast.makeText(getApplicationContext(), "Enter a valide password.", Toast.LENGTH_LONG).show();

                            } else {
                                if ((password.equals(passwordConfirm))) {

                                    registerUser(firstname, lastname, birthdate, cin, email, gender, password);
                                    Toast.makeText(getApplicationContext(), "You have been registered.", Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Your password confirmation do not match!", Toast.LENGTH_LONG).show();
                                    text.setText("Your two passwords do not match");

                                }
                            }
                        }
                    });

                    dialog.show();
                }
            }
        });


    }

    private void registerUser(final String firstname, final String lastname, final String birthdate, final String cin, final String email, final String gender, final String password) {
        // Tag used to cancel the request
        RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
        StringRequest strReq = new StringRequest(Request.Method.POST, "http://10.13.0.64:8081/user/register", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                // Inserting row in users table sqlLite
                // db = new SQLiteHandler(RegisterActivity.this);

                Toast.makeText(getApplicationContext(), "You have been successfully registered. Try to log in now.", Toast.LENGTH_LONG).show();

                // Launch login activity
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("firstname", firstname);
                params.put("lastname", lastname);
                params.put("birthdate", birthdate);
                params.put("email", email);
                params.put("gender", gender);
                params.put("cin", cin);
                params.put("password", password);

                return params;
            }

        };
        // Adding request to request queue
        queue.add(strReq);
    }


    private void datePicker() {

        // Get Current Date
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        final int mMonth = c.get(Calendar.MONTH);
        final int mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        {
                            if ((dayOfMonth < 10) && (monthOfYear < 10)) {
                                date_time = "0" + dayOfMonth + "/" + "0" + (monthOfYear + 1) + "/" + year;
                            }
                            if (monthOfYear > 10 && dayOfMonth < 10) {
                                date_time = "0" + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                            }
                            if (monthOfYear < 10 && dayOfMonth > 10) {
                                date_time = dayOfMonth + "/" + "0" + (monthOfYear + 1) + "/" + year;
                            }
                            if (dayOfMonth > 10 && monthOfYear > 10) {
                                date_time = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                            }
                        }


                        inputBirthDate.setText(date_time);
                        //*************Call Time Picker Here ********************

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

}
