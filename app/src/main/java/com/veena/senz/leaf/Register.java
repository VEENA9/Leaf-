package com.veena.senz.leaf;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;


public class Register extends AppCompatActivity {
    // public String baseUrl;
    //The view objects
    private EditText editTextName;
    private EditText Name;
    private EditText emailET;
    private EditText editTextPassword;
    private EditText editTextPassword1;
    private String baseUrl;
    private Button buttonSubmit;
    private boolean isReg = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //new SendPostRequest.doInBackground().execute();
        baseUrl = "http://10.91.150.250:5000/api/auth/signup";

        //initializing view objects
        editTextName = (EditText) findViewById(R.id.editText5);
        Name = (EditText) findViewById(R.id.editText7);
        emailET = (EditText) findViewById(R.id.editText8);
        editTextPassword = (EditText) findViewById(R.id.editText6);
        editTextPassword1 = (EditText) findViewById(R.id.editText9);
        buttonSubmit = (Button) findViewById(R.id.button2);

    }

    public void register(View view) {
        String username = editTextName.getText().toString();
        String name = Name.getText().toString();
        String email = emailET.getText().toString();
        String password = editTextPassword.getText().toString();
        String confirmPassword = editTextPassword1.getText().toString();

        JSONObject js = new JSONObject();
        try {
            js.put("username", username);
            js.put("name", name);
            js.put("email", email);
            js.put("password", password);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST, baseUrl, js, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    isReg = true;
                } catch (Exception e) {
                    Log.e("Responce", response.toString());
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("error response", "Error: " + error.getMessage());
                    }
                }) {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

        };

        if (validateReg(username, name, email, password, confirmPassword)) {
//                    //do login
            // Adding request to request queue
            Volley.newRequestQueue(this).add(jsonObjReq);

        }


        if (isReg) {
            succ();
            goRegpege();
        } else {
            unsucc();
        }
    }

    private boolean validateReg(String username, String name, String email, String password, String confirmPassword) {
        //adding validation to edittexts

        boolean invalid = false;

        if (username.equals("")) {
            invalid = true;
            Toast.makeText(getApplicationContext(), "Enter your Given username", Toast.LENGTH_SHORT).show();
        } else if (name.equals("")) {
            invalid = true;
            Toast.makeText(getApplicationContext(), "Please enter your name", Toast.LENGTH_SHORT)
                    .show();
        } else if (email.equals("")) {
            invalid = true;
            Toast.makeText(getApplicationContext(), "Please enter the email", Toast.LENGTH_SHORT)
                    .show();
        } else if (password.equals("")) {
            invalid = true;
            Toast.makeText(getApplicationContext(), "Please enter the password", Toast.LENGTH_SHORT)
                    .show();
        } else if (confirmPassword.equals("")) {
            invalid = true;
            Toast.makeText(getApplicationContext(), "Please enter the conform  password", Toast.LENGTH_SHORT)
                    .show();
        }

//                        if(!editTextPassword.getText().toString().equals(editTextPassword1.getText().toString())){
//                            Toast.makeText(getApplicationContext(), "Password Not matched", Toast.LENGTH_LONG).show();

        else if (!password.equals(confirmPassword)) {
            Toast.makeText(getApplicationContext(), "Password Not matched", Toast.LENGTH_LONG).show();
        } else if

                (password.length() < 6 || password.length() > 10) {
            Toast.makeText(getApplicationContext(), "Please enter the password 6 to 10", Toast.LENGTH_SHORT)
                    .show();
            editTextPassword.requestFocus();
            return false;
        } else if
                (name.length() == 10) {
            Toast.makeText(getApplicationContext(), "Please enter the name", Toast.LENGTH_SHORT)
                    .show();
            editTextPassword.requestFocus();
            return false;
        }

//                    else {
//                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
//                    }


        // buttonSubmit.setOnClickListener(this);
        return invalid;
    }


    private void goRegpege() {
        Intent i = new Intent(getApplicationContext(), Register.class);
        startActivity(i);
    }

    private void succ() {
        Toast.makeText(this, "Reg succes", Toast.LENGTH_SHORT).show();
    }

    private void unsucc() {
        Toast.makeText(this, "Reg fail", Toast.LENGTH_SHORT).show();
    }


    public void setPostRequestContent(HttpURLConnection conn, JSONObject jsonObject) throws IOException {

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(jsonObject.toString());
        Log.i(Login.class.toString(), jsonObject.toString());
        writer.flush();
        writer.close();
        os.close();
    }

    public void go() {
        Intent intent = new Intent(Register.this, Photo.class);
        startActivity(intent);
    }


}
