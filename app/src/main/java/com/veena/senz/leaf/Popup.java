package com.veena.senz.leaf;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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

import java.util.HashMap;
import java.util.Map;

public class Popup extends AppCompatActivity {

    private String lname;
    private String dis;
    private String baseUrl;
    private boolean issend = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        baseUrl = "http://10.91.150.250:5000/api/auth/signup";
        Button mShowDialog = (Button) findViewById(R.id.btnShowDialog);}
    public void Dialog (View view){


        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Popup.this);
        View mView = getLayoutInflater().inflate(R.layout.activity_popup, null);

//            final EditText lname = (EditText) mView.findViewById(R.id.etName);
//            final EditText dis = (EditText) mView.findViewById(R.id.etDis);



        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();}

    public void send (View view){
        View mView = getLayoutInflater().inflate(R.layout.activity_popup, null);
        final EditText lname = (EditText) mView.findViewById(R.id.etName);
        final EditText dis = (EditText) mView.findViewById(R.id.etDis);

        String name = lname.getText().toString();
        String Dis = dis.getText().toString();


        JSONObject js = new JSONObject();
        try {
            js.put("lname", name);
            js.put("Discription", Dis);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST, baseUrl, js, new Response.Listener<JSONObject>()
        {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    send();
                    issend = true;
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

        if (validatedis(name, Dis)) {
//                    //do login
            // Adding request to request queue
            Volley.newRequestQueue(this).add(jsonObjReq);

        }


        if (issend) {
            succ();
            goRegpege();
        } else {
            unsucc();
        }
    }

    private boolean validatedis (String lname, String dis){
        //adding validation to edittexts

        boolean invalid = false;

        if (lname.equals("")) {
            invalid = true;
            Toast.makeText(getApplicationContext(), "Enter your Given username", Toast.LENGTH_SHORT).show();
        } else if (dis.equals("")) {
            invalid = true;
            Toast.makeText(getApplicationContext(), "Please enter your name", Toast.LENGTH_SHORT)
                    .show();
        }

//                    else {
//                        Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
//                    }


        // buttonSubmit.setOnClickListener(this);
        return invalid;
    }


    private void succ () {
        Toast.makeText(this, "send successful", Toast.LENGTH_SHORT).show();
    }

    private void unsucc () {
        Toast.makeText(this, "send fail", Toast.LENGTH_SHORT).show();
    }
    private void goRegpege () {
        Intent i = new Intent(getApplicationContext(), Register.class);
        startActivity(i);
    }


    private void send(){
        Photo ph=new Photo().sendImage();



//                RequestQueue mQueue = Volley.newRequestQueue(context);
//                PhotoMultipartRequest imageUploadReq = new PhotoMultipartRequest(url, ErrorListener, Listener, imageFile);
//                mQueue.add(imageUploadReq);
    }
}




