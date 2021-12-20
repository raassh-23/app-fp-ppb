package com.raassh.simplecamera;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

public class ListTranslation extends AppCompatActivity {
    ListView lvTranslation;
    Button btnTranslationBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_translation);

        lvTranslation = findViewById(R.id.lvTranslations);
        btnTranslationBack = findViewById(R.id.btnTranslationBack);

        btnTranslationBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ArrayList<TranslationHistory> listTrans = new ArrayList<>();

        LoadingDialog loading = new LoadingDialog(this, "Mengambil riwayat terjemahan...");
        loading.startLoadingDialog();

        String url = getString(R.string.apiUrl, "translation/history");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("LIST_TRANSLATION", "Response: " + response.toString());

                        try {
                            JSONArray data = response.getJSONArray("data");

                            for (int i = 0; i < data.length(); i++) {
                                JSONObject tranlation = data.getJSONObject(i);

                                String sourceLang = tranlation.getString("source_language_code");
                                String sourceText = tranlation.getString("source_text");
                                String toLang = tranlation.getString("translated_language_code");
                                String toText = tranlation.getString("translated_text");

                                listTrans.add(new TranslationHistory(sourceLang, sourceText, toLang, toText));
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            Log.e("LIST_TRANSLATION", "JSONException: " + e.getMessage());
                        }

                        TranslationHistoryAdapter adapter = new TranslationHistoryAdapter(getBaseContext(), 0, listTrans);
                        lvTranslation.setAdapter(adapter);

                        loading.dismisDialog();
                        Toast.makeText(getBaseContext(), "Riwayat terjemahan berhasil diambil", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismisDialog();

                        if(error.networkResponse != null && error.networkResponse.statusCode == 404) {
                            Toast.makeText(getBaseContext(), "Tidak ditemukan riwayat terjemahan", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_LONG).show();
                        }

                        Log.e("LIST_TRANSLATION", "Error: " + error.toString());
                    }
                });

        RequestQueueSingleton.getInstance(getBaseContext()).addToRequestQueue(jsonObjectRequest);

        lvTranslation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TranslationHistory selected = (TranslationHistory) lvTranslation.getItemAtPosition(i);
                Log.d("banana", "onItemClick: " + selected.toString());
            }
        });
    }
}