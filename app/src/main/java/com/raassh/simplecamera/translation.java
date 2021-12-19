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

import org.json.JSONException;
import org.json.JSONObject;

public class translation extends Fragment {
    ImageButton btnTranslate;
    Spinner spLangSelect;
    Button btnDetect;
    EditText inputText;
    TextView tvTranslation;

    String selectedLangCode;

    public Spinner get_spinner(){
        return getView().findViewById(R.id.spLangSelect);
    }

    public translation() {
        // Required empty public constructor
    }

    public String getLang(){
        return selectedLangCode;
    }

    public static translation newInstance() {
        translation fragment = new translation();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        inputText = (EditText) getActivity().findViewById(R.id.InputText);
        btnDetect = (Button) getActivity().findViewById(R.id.detect_text_image_btn);
        tvTranslation = getActivity().findViewById(R.id.tvTranslation);
        btnTranslate = getView().findViewById(R.id.btnTranslate);
        spLangSelect = getView().findViewById(R.id.spLangSelect);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.languages, R.layout.custom_spinner);
        spLangSelect.setAdapter(adapter);

        spLangSelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        btnTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadingDialog loadingDialog = new LoadingDialog(getActivity(), "Menerjemahkan...");
                loadingDialog.startLoadingDialog();

                String text = inputText.getText().toString();
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

                                loadingDialog.dismisDialog();
                                Toast.makeText(getContext(), "Teks berhasil diterjemahkan", Toast.LENGTH_SHORT).show();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_translation, container, false);
    }
}