package com.example.cuarentinistas_tp3;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MisCuentas extends AppCompatActivity {

    private LinearLayout container;
    private TextView cliente;
    private Button btnEditar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Mis Cuentas");
        setContentView(R.layout.activity_mis_cuentas);
        container = (LinearLayout) findViewById(R.id.containerCuentas);
        asyncCall getMisCuentas = new asyncCall();
        getMisCuentas.execute();

        cliente = findViewById(R.id.cliente);
        btnEditar = findViewById(R.id.btnEditar);
        asyncCallPerfil getPerfil = new asyncCallPerfil();
        getPerfil.execute();
    }


    private class asyncCall extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... args) {
            return RESTService.makeGetRequest(ServerAddress.value() + "/rest/cuentas/cliente/1");
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                // Si el resultado es un string que contiene el formato de un unico elemento JSON
                // le agrego los corchetes para que cumpla con el formato de un JSONArray
                if (result.charAt(0) != '[') {
                    result = "[" + result + "]";
                }
                JSONArray cuentas = new JSONArray(result);

                for (int i = 0, size = cuentas.length(); i < size; i++) {
                    final JSONObject cuenta = cuentas.getJSONObject(i);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    Button btn = new Button(getApplicationContext());
                    btn.setId(i);
                    final int id_ = btn.getId();
                    btn.setText(cuenta.getString("alias") + " [" + cuenta.get("cbu") + "]");
                    btn.setBackgroundColor(Color.rgb(0, 209, 178));
                    container.addView(btn, params);
                    btn = (Button) findViewById(id_);
                    btn.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            Intent alLobby = new Intent(getApplicationContext(), Lobby.class);
                            try {
                                alLobby.putExtra("cbuCuenta", cuenta.getString("cbu"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            startActivity(alLobby);
                        }
                    });
                }
            } catch (JSONException e) {
                Log.e("ERROR", "Se produjo el siguiente error:", e);
            }
        }
    }

    private class asyncCallPerfil extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... args) {
            return RESTService.makeGetRequest(ServerAddress.value() + "/rest/clientes/1");
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject datosPersonales = new JSONObject(result);
                final String id = datosPersonales.getString("id");
                final String nombreCliente = datosPersonales.getString("nombre");
                final String apellidoCliente = datosPersonales.getString("apellido");
                final String direccion = datosPersonales.getString("direccion");
                final String documento = datosPersonales.getString("documento");
                final String fechaNac = datosPersonales.getString("fechaNac");

                cliente.setText(nombreCliente+" "+apellidoCliente);

                btnEditar.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        Intent alPerfil = new Intent(getApplicationContext(), Perfil.class);
                        alPerfil.putExtra("id", id);
                        alPerfil.putExtra("nombre", nombreCliente);
                        alPerfil.putExtra("apellido", apellidoCliente);
                        //alPerfil.putExtra("direccion", direccion);
                        alPerfil.putExtra("documento", documento);
                        alPerfil.putExtra("fechaNac", fechaNac);
                        startActivity(alPerfil);
                    }
                });

            } catch (JSONException e) {
                Log.e("ERROR", "Se produjo el siguiente error:", e);
            }
        }
    }
}

