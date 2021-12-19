package com.raassh.simplecamera;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.android.volley.toolbox.NetworkImageView;

import java.util.Locale;

public class SelectedPhoto extends DialogFragment {

    SelectedPhoto() {
        // Empty Constructor
    }

    public static SelectedPhoto newInstance(String name, String link, String text, String lang) {
        SelectedPhoto frag = new SelectedPhoto();
        Bundle args = new Bundle();
        args.putString("name", name.trim());
        args.putString("link", link.trim());
        args.putString("text", text.trim());
        args.putString("lang", lang.trim());
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.selected_photo, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NetworkImageView nivSelected = getView().findViewById(R.id.nivSelectedFoto);
        nivSelected.setImageUrl(getArguments().getString("link"),
                RequestQueueSingleton.getInstance(getContext()).getImageLoader());

        String langCode = getArguments().getString("lang");
        Locale loc = new Locale(langCode);
        String langName = loc.getDisplayLanguage(new Locale("id"));

        TextView tvLang = getView().findViewById(R.id.tvSelectedLang);
        tvLang.setText(langName);

        String selectedText = getArguments().getString("text");        TextView tvText = getView().findViewById(R.id.tvSelectedText);
        tvText.setText(selectedText);

        Button btnClose = getView().findViewById(R.id.btnCloseSelected);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        Button btnCopy = getView().findViewById(R.id.btnCopyText);
        btnCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Text", selectedText);
                clipboardManager.setPrimaryClip(clipData);

                Toast.makeText(getContext(), "Teks disalin", Toast.LENGTH_SHORT).show();
            }
        });

        getDialog().setTitle(getArguments().getString("name"));
    }
}
