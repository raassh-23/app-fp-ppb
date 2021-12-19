package com.raassh.simplecamera;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
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
    GridView gvFoto;
    Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_foto);

        gvFoto = findViewById(R.id.gvFoto);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ArrayList<Foto> listFoto = new ArrayList<>();

        LoadingDialog loading = new LoadingDialog(this, "Mengambil foto...");
        loading.startLoadingDialog();

        String url = getString(R.string.apiUrl, "image/list");

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
                                        String link = foto.getString("url");
                                        String text = null;

                                        if (!foto.isNull("text")) {
                                            text = foto.getString("text");
                                        }

                                        String language = null;

                                        if (!foto.isNull("language")) {
                                            language = foto.getString("language");
                                        }

                                        listFoto.add(new Foto(name, link, text, language));
                                    }
                                } catch (JSONException e) {
                                    Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                    Log.e("LIST_FOTO", "JSONException: " + e.getMessage());
                                }

                                FotoAdapter adapter = new FotoAdapter(getBaseContext(), 0, listFoto);
                                gvFoto.setAdapter(adapter);

                                loading.dismisDialog();
                                Toast.makeText(getBaseContext(), "Daftar foto berhasil diambil", Toast.LENGTH_SHORT).show();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                loading.dismisDialog();

                                if(error.networkResponse != null && error.networkResponse.statusCode == 404) {
                                    Toast.makeText(getBaseContext(), "Tidak ditemukan foto", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_LONG).show();
                                }

                                Log.e("LIST_FOTO", "Error: " + error.toString());
                            }
                        });

        RequestQueueSingleton.getInstance(getBaseContext()).addToRequestQueue(jsonObjectRequest);

        gvFoto.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Foto selected = (Foto) gvFoto.getItemAtPosition(i);

                FragmentManager fm = getSupportFragmentManager();
                SelectedPhoto selectedPhoto = SelectedPhoto.newInstance(selected.getName(),
                        selected.getLink(), selected.getText(), selected.getLanguage());
                selectedPhoto.show(fm, "Selected Photo");
            }
        });
    }
}