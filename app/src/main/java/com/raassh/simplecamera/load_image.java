package com.raassh.simplecamera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
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

public class load_image extends Fragment {
    String selectedLangCode;
    Spinner spLangSelectImg;
    ImageButton btnCam,btnList,btnSend,btnLoad, btnTranslateFromImg;
    Bitmap loaded = null;
    Button detectTextBtn;
    EditText detectedTextView;
    String text;
    TextView tvTranslation;
    ImageView imageViewFix;
    private static final int kodeKamera = 222;
    private static final int pilihGambar = 223;

    public load_image() {
        // Required empty public constructor
    }

    public static load_image newInstance() {
        load_image fragment = new load_image();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_load_image, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnCam = getView().findViewById(R.id.btnCam);
        btnList = getView().findViewById(R.id.btnList);
        btnSend = getView().findViewById(R.id.btnSend);
        btnLoad = getView().findViewById(R.id.btnLoad);
        detectTextBtn = getView().findViewById(R.id.detect_text_image_btn);
        detectedTextView = getView().findViewById(R.id.detectedView);
        btnTranslateFromImg = getView().findViewById(R.id.btnTranslateFromImg);
        tvTranslation = (TextView) getActivity().findViewById(R.id.tvTranslation);

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
                Intent it = new Intent(getActivity(), ListFoto.class);
                startActivity(it);
            }
        });

        detectTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(loaded == null) {
                    Toast.makeText(getContext(), "Tidak ada gambar yang dipilih", Toast.LENGTH_SHORT).show();
                    return;
                }

                LoadingDialog loadingDialog = new LoadingDialog(getActivity(), "Mendeteksi Teks...");
                loadingDialog.startLoadingDialog();

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                loaded.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                String base64String = Base64.encodeToString(byteArray, Base64.NO_WRAP);

                JSONObject requestJson = new JSONObject();
                try {
                    requestJson.put("image", "data:image/png;base64,"+base64String);
                } catch (JSONException e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("MAIN", "JSONException: " + e.getMessage());
                }

                String url = getString(R.string.apiUrl, "image/upload");

                JsonObjectRequest uploadRequest = new JsonObjectRequest
                        (Request.Method.POST, url, requestJson, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("MAIN", "Response: " + response.toString());

                                try {
                                    JSONObject data = response.getJSONObject("data");

                                    if (data.isNull("text")) {
                                        Toast.makeText(getContext(), "Tidak ada teks terdeteksi", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getContext(), "Teks berhasil dideteksi", Toast.LENGTH_LONG).show();
                                        String text = data.getString("text");
                                        detectedTextView.setText(text.trim());
                                    }
                                } catch (JSONException e) {
                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                    Log.e("Load image", "JSONException: " + e.getMessage()); e.printStackTrace();
                                }

                                loadingDialog.dismisDialog();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
                                Log.e("MAIN", "VolleyError: " + error.toString());

                                loadingDialog.dismisDialog();
                            }
                        });

                RequestQueueSingleton.getInstance(getContext()).addToRequestQueue(uploadRequest);
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

        spLangSelectImg = getView().findViewById(R.id.spLangSelectImg);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.languages, R.layout.custom_spinner);
        spLangSelectImg.setAdapter(adapter);

        spLangSelectImg.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String strLangSelected = adapterView.getItemAtPosition(i).toString();
                if(strLangSelected.equals("Indonesia")) {
                    selectedLangCode="id";
                }
                if(strLangSelected.equals("Jerman")){
                    selectedLangCode="de";
                }
                if(strLangSelected.equals("Inggris")){
                    selectedLangCode="en";
                }
                if(strLangSelected.equals("Prancis")){
                    selectedLangCode="fr";
                }
                if(strLangSelected.equals("Spanyol")){
                    selectedLangCode="sp";
                }
                if(strLangSelected.equals("Italia")){
                    selectedLangCode="it";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnTranslateFromImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text = detectedTextView.getText().toString();

                if(TextUtils.isEmpty(text)) {
                    Toast.makeText(getContext(), "Tidak ada teks yang akan diterjemahkan", Toast.LENGTH_SHORT).show();
                    return;
                }

                LoadingDialog loadingDialog = new LoadingDialog(getActivity(), "Menerjemahkan...");
                loadingDialog.startLoadingDialog();

                String url = getString(R.string.apiUrl, "translation");

                JSONObject requestJson = new JSONObject();
                try {
                    requestJson.put("text", text);
                    requestJson.put("to", selectedLangCode);
                } catch (JSONException e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e("Translation", "JSONException: " + e.getMessage());
                }

                JsonObjectRequest translateRequest = new JsonObjectRequest
                        (Request.Method.POST, url, requestJson, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("MAIN", "Response: " + response.toString());

                                try {
                                    JSONObject data = response.getJSONObject("data");
                                    String translated = data.getString("translated_text");
                                    tvTranslation.setText(translated);
                                } catch (JSONException e) {
                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                    Log.e("Translation", "JSONException: " + e.getMessage());
                                }

                                Toast.makeText(getContext(), "Teks berhasil diterjemahkan", Toast.LENGTH_SHORT).show();
                                loadingDialog.dismisDialog();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
                                Log.e("MAIN", "VolleyError: " + error.toString());
                                loadingDialog.dismisDialog();
                            }
                        });

                RequestQueueSingleton.getInstance(getContext()).addToRequestQueue(translateRequest);
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case kodeKamera:
                    prosesKamera(data);
                    break;
                case pilihGambar:
                    prosesLoadDariFile(data);
                    break;
            }
        }
    }

    private void prosesKamera(Intent datanya) {
        loadImage((Bitmap)datanya.getExtras().get("data"));
    }

    private void prosesLoadDariFile(Intent data) {
        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(data.getData());
            loadImage(BitmapFactory.decodeStream(inputStream));
        } catch (FileNotFoundException e) {
            Toast.makeText(getContext(), "Gambar gagal diload", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadImage(Bitmap bm) {
        loaded = bm;
        imageViewFix = getView().findViewById(R.id.imageViewFix);
        imageViewFix.setImageBitmap(loaded);
        detectedTextView.setText("");
        Toast.makeText(getContext(), "Gambar sudah terload ke Imageview", Toast.LENGTH_SHORT).show();
    }
}