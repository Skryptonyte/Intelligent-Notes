package com.example.madprojectt;

import static java.lang.Math.min;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class NoteListItemAdapter extends ArrayAdapter<Note> {

    // invoke the suitable constructor of the ArrayAdapter class
    public NoteListItemAdapter(@NonNull Context context, ArrayList<Note> arrayList) {

        // pass the context and arrayList for the super
        // constructor of the ArrayAdapter class
        super(context, 0, arrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // convertView which is recyclable view
        View currentItemView = convertView;

        // of the recyclable view is null then inflate the custom layout for the same
        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.notelistviewitem, parent, false);
        }

        TextView tv = currentItemView.findViewById(R.id.textView4);
        TextView tv2 = currentItemView.findViewById(R.id.textView5);

        tv.setText(getItem(position).title);
        tv2.setText(getItem(position).note.substring(0,min(getItem(position).note.length(),10))+"...");

        // then return the recyclable view
        return currentItemView;
    }
}