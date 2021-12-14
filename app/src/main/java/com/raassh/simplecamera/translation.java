package com.raassh.simplecamera;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link translation#newInstance} factory method to
 * create an instance of this fragment.
 */
public class translation extends Fragment {
    TextView tvText, tvLang, tvTranslation, tvLangSelect;
    ImageButton btnTranslate, btnBackLoad;
    Spinner spLangSelect;
    Button btnDetect;

    ArrayList<String> availableLang;
    String selectedLangCode = "id";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "text";
    private static final String ARG_PARAM2 = "lang";
    private static final String ARG_PARAM3 = "available_lang";

    // TODO: Rename and change types of parameters
    TextView tv1;
    private String text;
    private String detectedLang = "en";

    public translation() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment translation.
     */
    // TODO: Rename and change types and number of parameters
    public static translation newInstance(String param1, String param2, ArrayList<String> param3) {
        translation fragment = new translation();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putStringArrayList(ARG_PARAM3, param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        availableLang = new ArrayList<String>();
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
             tv1 = (EditText) getView().findViewById(R.id.detectedView);
            text = tv1.getText().toString();
//            text = getArguments().getString(ARG_PARAM1);
            detectedLang = getArguments().getString(ARG_PARAM2);
            availableLang = getArguments().getStringArrayList(ARG_PARAM3);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvText = getView().findViewById(R.id.tvText);
        tvLang = getView().findViewById(R.id.tvLang);
        tvTranslation = (TextView) getActivity().findViewById(R.id.tvTranslation);
        tvLangSelect = getView().findViewById(R.id.tvLangSelect);


        btnDetect = (Button) getActivity().findViewById(R.id.detect_text_image_btn);
        tv1 = (TextView) getActivity().findViewById(R.id.detectedView);
        text = "test";
//        btnDetect.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                tv1 = (TextView) getActivity().findViewById(R.id.detectedView);
//                text = tv1.getText().toString();
//                tvText.setText(text);
//            }
//        });

        Locale detectedLangLoc = new Locale(detectedLang);
        Locale id = new Locale("id");
//        tvText.setText(text);
        tvLang.setText(detectedLangLoc.getDisplayLanguage(id));

        btnTranslate = getView().findViewById(R.id.btnTranslate);
        btnBackLoad = getView().findViewById(R.id.btnBackLoad);

        spLangSelect = getView().findViewById(R.id.spLangSelect);


        if(text.equals("Tidak ada teks terdeteksi") || text==null) {
            tvTranslation.setVisibility(View.GONE);
            tvLangSelect.setVisibility(View.GONE);
            btnTranslate.setVisibility(View.GONE);
            spLangSelect.setVisibility(View.GONE);
        }

        ArrayList<String> languagesName = new ArrayList<>();

        for (int i = 0; i < availableLang.size(); i++) {
            Locale langLocale = new Locale(availableLang.get(i));
            languagesName.add(langLocale.getDisplayLanguage(id));
        }

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, languagesName);
        spLangSelect.setAdapter(arrayAdapter);
        spLangSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedLangCode = availableLang.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv1 = (EditText) getActivity().findViewById(R.id.detectedView);
                text = tv1.getText().toString();
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
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getContext(), error.toString(), Toast.LENGTH_LONG).show();
                                Log.e("MAIN", "VolleyError: " + error.toString());
                            }
                        });

                Toast.makeText(getContext(), "Menerjemahkan", Toast.LENGTH_SHORT).show();
                RequestQueueSingleton.getInstance(getContext()).addToRequestQueue(translateRequest);
            }
        });

        btnBackLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_translation, container, false);
    }
}