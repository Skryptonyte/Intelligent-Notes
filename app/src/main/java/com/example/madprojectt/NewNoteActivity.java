package com.example.madprojectt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.contentcapture.ContentCaptureCondition;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NewNoteActivity extends AppCompatActivity {
    String dbname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setTitle("Create new note!");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);

        Intent i = getIntent();

        Button addButton = findViewById(R.id.addNoteButton);
        EditText title = findViewById(R.id.titleText);
        EditText desc = findViewById(R.id.descText);

        TextView tv = findViewById(R.id.descheader);
        title.setText(i.getStringExtra("title"));
        desc.setText(i.getStringExtra("desc"));

        int id = (i.getIntExtra("id",-1));
        this.dbname = i.getStringExtra("dbname");

        tv.setText(String.format("Note: (%d characters left)",280 - desc.getText().length()  ));

        desc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                tv.setText(String.format("Note: (%d characters left)",280 - editable.length()  ));
            }
        });
        if (id != -1)
        {    getSupportActionBar().setTitle("Editing note!");

            addButton.setText("SAVE");
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (title.toString().isEmpty())
                    {
                        Toast.makeText(getApplicationContext(),"Please enter title!",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (desc.toString().isEmpty())
                    {
                        Toast.makeText(getApplicationContext(),"Please enter description!",Toast.LENGTH_SHORT).show();
                        return;
                    }

                    NoteDBHelper h = new NoteDBHelper(getApplicationContext(),dbname);
                    SQLiteDatabase db = h.getWritableDatabase();

                    ContentValues cv  = new ContentValues();
                    cv.put("title",title.getText().toString());
                    cv.put("descr",desc.getText().toString());

                    db.update("notes",cv,"noteid=?",new String[]{id+""});
                    db.close();
                    finish();
                }
            });
            return;
        }
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (title.toString().isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"Please enter title!",Toast.LENGTH_SHORT);
                    return;
                }
                if (desc.toString().isEmpty())
                {
                    Toast.makeText(getApplicationContext(),"Please enter description!",Toast.LENGTH_SHORT);
                    return;
                }

                NoteDBHelper h = new NoteDBHelper(getApplicationContext(),dbname);
                SQLiteDatabase db = h.getWritableDatabase();

                ContentValues cv  = new ContentValues();
                cv.put("title",title.getText().toString());
                cv.put("descr",desc.getText().toString());

                db.insert("notes",null,cv);
                db.close();
                finish();
            }
        });
    }
}