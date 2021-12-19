package com.raassh.simplecamera;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

public class LoadingDialog {
    private Activity activity;
    private String loadingText;
    private AlertDialog alertDialog;

    LoadingDialog(Activity myactivity)
    {
        activity= myactivity;
        loadingText = "Loading...";
    }

    LoadingDialog(Activity myactivity, String loadingText)
    {
        activity= myactivity;
        this.loadingText = loadingText;
    }

    void startLoadingDialog()
    {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_loading,null);
        ((TextView)view.findViewById(R.id.tvLoading)).setText(loadingText);
        builder.setView(view);
        builder.setCancelable(false);

        alertDialog = builder.create();
        alertDialog.show();
    }

    void dismisDialog()
    {
        alertDialog.dismiss();
    }
}
