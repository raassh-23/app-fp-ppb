package com.raassh.simplecamera;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListFoto extends AppCompatActivity {
    ListView lvFoto;
    Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_foto);

        lvFoto = findViewById(R.id.lvFoto);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ArrayList<Foto> listFoto = new ArrayList<>();

        String url = getIntent().getExtras().getString("url");

        Toast.makeText(this, "Mengambil list foto", Toast.LENGTH_SHORT).show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("LIST_FOTO", "Response: " + response.toString());
                                
                                try {
                                    JSONArray data = response.getJSONArray("data");

                                    for (int i = 0; i < data.length(); i++) {
                                        JSONObject foto = data.getJSONObject(i);

                                        String name = foto.getString("name");
                                        String link = foto.getString("link");
                                        String base64 = foto.getString("base64");

                                        listFoto.add(new Foto(name, link, base64));
                                    }
                                } catch (JSONException e) {
                                    Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                    Log.e("LIST_FOTO", "JSONException: " + e.getMessage());
                                }

                                FotoAdapter adapter = new FotoAdapter(getBaseContext(), 0, listFoto);
                                lvFoto.setAdapter(adapter);

                                Toast.makeText(getBaseContext(), "List foto berhasil diambil", Toast.LENGTH_SHORT).show();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                if(error.networkResponse != null && error.networkResponse.statusCode == 404) {
                                    Toast.makeText(getBaseContext(), "Tidak ditemukan foto", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_LONG).show();
                                }

                                Log.e("LIST_FOTO", "Error: " + error.toString());
                            }
                        });

        RequestQueueSingleton.getInstance(getBaseContext()).addToRequestQueue(jsonObjectRequest);
    }
}