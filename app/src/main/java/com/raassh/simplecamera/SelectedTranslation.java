package com.raassh.simplecamera;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.android.volley.toolbox.NetworkImageView;

import java.util.Locale;

public class SelectedTranslation extends DialogFragment {

    SelectedTranslation() {
        // Empty Constructor
    }

    public static SelectedTranslation newInstance(String sourceLang, String sourceText, String toLang, String toText) {
        SelectedTranslation frag = new SelectedTranslation();
        Bundle args = new Bundle();
        args.putString("sourceLang", sourceLang);
        args.putString("sourceText", sourceText);
        args.putString("toLang", toLang);
        args.putString("toText", toText);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.selected_translation, container);
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String sourceLang = getArguments().getString("sourceLang");
        String sourceText = getArguments().getString("sourceText");
        String toLang = getArguments().getString("toLang");
        String toText = getArguments().getString("toText");

        Locale ind = new Locale("id");
        Locale sourceLangCode = new Locale(sourceLang);
        Locale toLangCode = new Locale(toLang);

        String sourceLangName = sourceLangCode.getDisplayLanguage(ind);
        String toLangName = toLangCode.getDisplayLanguage(ind);

        TextView tvLang = getView().findViewById(R.id.tvSelectedTranslateLang);
        TextView tvSource = getView().findViewById(R.id.tvSelectedTranslateSource);
        TextView tvTo = getView().findViewById(R.id.tvSelectedTranslateTo);

        tvLang.setText(sourceLangName + " -> " + toLangName);
        tvSource.setText(sourceText);
        tvTo.setText(toText);

        Button btnClose = getView().findViewById(R.id.btnSelectedTranslateClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        Button btnCopySource = getView().findViewById(R.id.btnCopySource);
        btnCopySource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Text", sourceText);
                clipboardManager.setPrimaryClip(clipData);

                Toast.makeText(getContext(), "Teks disalin", Toast.LENGTH_SHORT).show();
            }
        });

        Button btnCopyTo = getView().findViewById(R.id.btnCopyTo);
        btnCopyTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Text", toText);
                clipboardManager.setPrimaryClip(clipData);

                Toast.makeText(getContext(), "Teks disalin", Toast.LENGTH_SHORT).show();
            }
        });

        getDialog().setTitle("Terjemahan");
    }
}