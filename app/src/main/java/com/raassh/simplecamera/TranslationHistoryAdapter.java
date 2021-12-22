package com.raassh.simplecamera;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;
import java.util.Locale;

public class TranslationHistoryAdapter extends ArrayAdapter<TranslationHistory> {
    private static class ViewHolder{
        TextView langSrc;
        TextView langDest;
        TextView source;
        TextView to;
    }

    public TranslationHistoryAdapter(@NonNull Context context, int resource, @NonNull List<TranslationHistory> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TranslationHistory dtTrans = getItem(position);

        TranslationHistoryAdapter.ViewHolder viewHolder;
        if(convertView == null) {
            viewHolder = new TranslationHistoryAdapter.ViewHolder();

            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.translation_item, parent, false);

            viewHolder.langSrc = convertView.findViewById(R.id.tvTranslationHistorySrcLang);
            viewHolder.langDest =  convertView.findViewById(R.id.tvTranslationHistoryDestLang);
            viewHolder.source = convertView.findViewById(R.id.tvTranslateSource);
            viewHolder.to = convertView.findViewById(R.id.tvTranslateTo);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (TranslationHistoryAdapter.ViewHolder) convertView.getTag();
        }

        Locale ind = new Locale("id");
        Locale sourceLang = new Locale(dtTrans.getSourceLang());
        Locale toLang = new Locale(dtTrans.getToLang());

        String sourceLangName = sourceLang.getDisplayLanguage(ind);
        String toLangName = toLang.getDisplayLanguage(ind);

        viewHolder.langSrc.setText(sourceLangName);
        viewHolder.langDest.setText(toLangName);
        viewHolder.source.setText(dtTrans.getSourceText());
        viewHolder.to.setText(dtTrans.getToText());

        return convertView;
    }
}
