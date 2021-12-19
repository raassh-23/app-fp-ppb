package com.raassh.simplecamera;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

public class FotoAdapter extends ArrayAdapter<Foto> {
    private static class ViewHolder{
        NetworkImageView nivFoto;
    }

    public FotoAdapter(@NonNull Context context, int resource, @NonNull List<Foto> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Foto dtFoto = getItem(position);

        ViewHolder viewHolder;
        if(convertView == null) {
            viewHolder = new ViewHolder();

            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.foto_item, parent, false);
            viewHolder.nivFoto = convertView.findViewById(R.id.nivSelectedFoto);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.nivFoto.setImageUrl(dtFoto.getLink(), RequestQueueSingleton.getInstance(getContext()).getImageLoader());

        return convertView;
    }
}
