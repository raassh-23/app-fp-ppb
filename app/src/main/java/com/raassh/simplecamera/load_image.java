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
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link load_image#newInstance} factory method to
 * create an instance of this fragment.
 */
public class load_image extends Fragment {
    ImageButton btnCam;
    Button btnList;
    Button btnSend;
    Button btnLoad;
    Bitmap loaded = null;
    Button detectTextBtn;
    TextView detectedTextView;
    private Bitmap imageBitmap;
    private static final int kodeKamera = 222;
    private static final int pilihGambar = 223;


//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

    public load_image() {
        // Required empty public constructor
    }

//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment load_image.
//     */
    // TODO: Rename and change types and number of parameters
//    public static load_image newInstance(String param1, String param2) {
//        load_image fragment = new load_image();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    public static load_image newInstance() {
        load_image fragment = new load_image();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
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

        btnSend.setEnabled(false);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                                        ((MainActivity)getActivity()).setTranslationFragment(
                                                "Tidak ada teks terdeteksi",
                                                "Tidak ada bahasa terdeteksi",
                                                new ArrayList<String>()
                                        );
                                    } else {
                                        String text = data.getString("text");
                                        String lang = data.getString("language");

                                        JSONArray languages = data.getJSONArray("available-translation");

                                        ArrayList<String> availableLanguages = new ArrayList<>();

                                        for (int i = 0; i < languages.length(); i++) {
                                            JSONObject availLang = languages.getJSONObject(i);
                                            availableLanguages.add(availLang.getString("code"));
                                        }

                                        int itemPos = availableLanguages.indexOf("id");
                                        if(itemPos > 0) {
                                            availableLanguages.remove(itemPos);
                                            availableLanguages.add(0, "id" );
                                        }

                                        ((MainActivity)getActivity()).setTranslationFragment(
                                                text,
                                                lang,
                                                availableLanguages
                                        );
                                    }
                                } catch (JSONException e) {
                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                    Log.e("Load image", "JSONException: " + e.getMessage()); e.printStackTrace();
                                }


//

                                Toast.makeText(getContext(), "Gambar berhasil tersimpan di server", Toast.LENGTH_SHORT).show();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
                                Log.e("MAIN", "VolleyError: " + error.toString());
                            }
                        });

                Toast.makeText(getContext(), "Mengirim gambar ke server", Toast.LENGTH_SHORT).show();
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
        detectTextBtn = getView().findViewById(R.id.detect_text_image_btn);
        detectedTextView = getView().findViewById(R.id.detectedView);

        detectTextBtn.setOnClickListener(v -> detectText());

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
                    prosesLoadGambar(data);
                    break;
            }
        }
    }

    private void prosesKamera(Intent datanya) {
        loaded = (Bitmap) datanya.getExtras().get("data");
        ((MainActivity)getActivity()).loadImage(loaded);
        btnSend.setEnabled(true);

        Toast.makeText(getContext(), "Gambar sudah terload ke Imageview", Toast.LENGTH_SHORT).show();
    }

    private void prosesLoadGambar(Intent data) {
        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(data.getData());
            loaded = BitmapFactory.decodeStream(inputStream);
            ((MainActivity)getActivity()).loadImage(loaded);
            btnSend.setEnabled(true);
        } catch (FileNotFoundException e) {
            Toast.makeText(getContext(), "Gambar gagal diload", Toast.LENGTH_SHORT).show();
        }
    }
    private void detectText() {
        // this is a method to detect a text from image.
        // below line is to create variable for firebase
        // vision image and we are getting image bitmap.

        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(loaded);

        // below line is to create a variable for detector and we
        // are getting vision text detector from our firebase vision.
        FirebaseVisionTextDetector detector = FirebaseVision.getInstance().getVisionTextDetector();

        // adding on success listener method to detect the text from image.
        detector.detectInImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                // calling a method to process
                // our text after extracting.
                processTxt(firebaseVisionText);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // handling an error listener.
                Toast.makeText(getContext(),"Fail to detect the text from image..",  Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processTxt(FirebaseVisionText text) {
        // below line is to create a list of vision blocks which
        // we will get from our firebase vision text.
        List<FirebaseVisionText.Block> blocks = text.getBlocks();

        // checking if the size of the
        // block is not equal to zero.
        if (blocks.size() == 0) {
            // if the size of blocks is zero then we are displaying
            // a toast message as no text detected.
            Toast.makeText(getContext(), "No Text ", Toast.LENGTH_LONG).show();
            return;
        }
        // extracting data from each block using a for loop.
        for (FirebaseVisionText.Block block : text.getBlocks()) {
            // below line is to get text
            // from each block.
            String txt = block.getText();

            // below line is to set our
            // string to our text view.
            detectedTextView.setText(txt);
        }
    }
}