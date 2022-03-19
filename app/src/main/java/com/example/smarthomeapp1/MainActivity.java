package com.example.smarthomeapp1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    EditText etUser, etPass;
    Button bInicio;
    SharedPreferences sesion; /**se almacena el nombre del usuario y el token**/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /**enlazar lo de arriba**/
        etUser = findViewById(R.id.etUser);
        etPass = findViewById(R.id.etPass);
        bInicio = findViewById(R.id.bInicio);
        sesion = getSharedPreferences("sesion", 0);/**se pueden guardar las variables que sean se le pone la misma variable**/
        bInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }
    private void login(){
        /**se pasan los parametros y la combierte en el url**/
        String url = Uri.parse(Config.URL + "login.php")
                .buildUpon()
                .appendQueryParameter("user",etUser.getText().toString())
                .appendQueryParameter("pass",etPass.getText().toString())
                .build().toString();
        JsonObjectRequest peticion = new JsonObjectRequest(Request.Method.GET,url,null,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        respuesta(response);
                    }
                },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){
                Toast.makeText(MainActivity.this, "Error"+error.getMessage(), Toast.LENGTH_SHORT).show();
                Log.d("Server", error.getMessage());
            }
        });
       MySingleton.getInstance(getApplicationContext()).addToRequestQueue(peticion);
    }
    private void respuesta(JSONObject response){
        try {
            if (response.getString("login").compareTo("y")==0){
                String jwt = response.getString("token");
                /*crear objeto preference  para guardar objetos*/
                SharedPreferences.Editor editor = sesion.edit();
                editor.putString("user",etUser.getText().toString());
                editor.putString("token",jwt);
                editor.commit();
                startActivity(new Intent(this,MainActivity2.class));
            }else{
                Toast.makeText(this,"Error de usuario o contraseña",Toast.LENGTH_SHORT).show();
            }
        }catch(Exception e){ }
    }
}