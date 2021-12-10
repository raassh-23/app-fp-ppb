package com.raassh.simplecamera;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    ImageButton btnCam;
    Button btnList;
    Button btnSend;
    Button btnLoad;
    ImageView imgView;

    private static final int kodeKamera = 222;
    private static final int pilihGambar = 223;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCam = findViewById(R.id.btnCam);
        btnList = findViewById(R.id.btnList);
        btnSend = findViewById(R.id.btnSend);
        btnLoad = findViewById(R.id.btnLoad);
        imgView = findViewById(R.id.imageView);

        btnCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(it, kodeKamera);
            }
        });

        btnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(MainActivity.this, ListFoto.class);
                startActivity(it);
            }
        });

        btnSend.setEnabled(false);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bm = ((BitmapDrawable)imgView.getDrawable()).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                String base64String = Base64.encodeToString(byteArray, Base64.NO_WRAP);

                JSONObject requestJson = new JSONObject();
                try {
                    requestJson.put("image", "data:image/png;base64,"+base64String);
                } catch (JSONException e) {
                    Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("MAIN", "JSONException: " + e.getMessage());
                }

                String url = getString(R.string.apiUrl, "image/upload");

                JsonObjectRequest uploadRequest = new JsonObjectRequest
                        (Request.Method.POST, url, requestJson, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("MAIN", "Response: " + response.toString());

                                Toast.makeText(getBaseContext(), "Gambar berhasil tersimpan di server", Toast.LENGTH_SHORT).show();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_LONG).show();
                                Log.e("MAIN", "VolleyError: " + error.toString());
                            }
                        });

                Toast.makeText(getBaseContext(), "Mengirim gambar ke server", Toast.LENGTH_SHORT).show();
                RequestQueueSingleton.getInstance(getBaseContext()).addToRequestQueue(uploadRequest);
            }
        });

        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), pilihGambar);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case kodeKamera:
                    prosesKamera(data);
                    break;
                case pilihGambar:
                    prosesLoadGambar(data);
                    break;
            }
        }
    }

    private void prosesKamera(Intent datanya) {
        Bitmap bm = (Bitmap) datanya.getExtras().get("data");
        imgView.setImageBitmap(bm);

        btnSend.setEnabled(true);

        Toast.makeText(this, "Gambar sudah terload ke Imageview", Toast.LENGTH_SHORT).show();
    }

    private void prosesLoadGambar(Intent data) {
        try {
            InputStream inputStream = this.getContentResolver().openInputStream(data.getData());
            Bitmap bm = BitmapFactory.decodeStream(inputStream);
            imgView.setImageBitmap(bm);

            btnSend.setEnabled(true);
            Toast.makeText(this, "Gambar sudah terload ke Imageview", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "Gambar gagal diload", Toast.LENGTH_SHORT).show();
        }
    }
}