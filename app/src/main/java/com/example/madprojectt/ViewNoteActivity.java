package com.example.madprojectt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ViewNoteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_note);

        Intent i = getIntent();
        String title = i.getStringExtra("title");
        String desc = i.getStringExtra("desc");
        getSupportActionBar().setTitle(title);

        TextView tv = findViewById(R.id.titleText);
        TextView tv2 = findViewById(R.id.descText);

        tv.setText(title);
        tv2.setText(desc);
    }
}