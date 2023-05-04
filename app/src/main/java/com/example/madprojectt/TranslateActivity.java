package com.example.madprojectt;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TranslateActivity#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TranslateActivity extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    String dbname;
    String langs[] = {"English","Hindi","Kannada","German","French"};
    public TranslateActivity(String dbname) {
        // Required empty public constructor
        this.dbname = dbname;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TranslateActivity.
     */
    // TODO: Rename and change types and number of parameters


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View root = inflater.inflate(R.layout.fragment_translate_activity, container, false);
        Spinner s1 = root.findViewById(R.id.fromlangspin);
        Spinner s2 = root.findViewById(R.id.tolangspin);
        EditText et = root.findViewById(R.id.texttotranslate);

        Button b = root.findViewById(R.id.translateButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.setEnabled(false);
                String fromLang = stringToTranslateId(s1.getSelectedItem().toString());
                String toLang = stringToTranslateId(s2.getSelectedItem().toString());
                String text = et.getText().toString();
                TranslatorOptions options =
                        new TranslatorOptions.Builder()
                                .setSourceLanguage(fromLang)
                                .setTargetLanguage(toLang)
                                .build();

                final Translator generalTranslator =
                        Translation.getClient(options);

                DownloadConditions conditions = new DownloadConditions.Builder()
                        .build();

                Toast.makeText(getActivity().getApplicationContext(),"First time translation may take a while!",Toast.LENGTH_LONG).show();

                generalTranslator.downloadModelIfNeeded(conditions)
                        .addOnSuccessListener(
                                new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void v) {
                                        generalTranslator.translate(text)
                                                .addOnSuccessListener(
                                                        new OnSuccessListener<String>() {
                                                            @Override
                                                            public void onSuccess(@NonNull String translatedText) {
                                                                Intent i = new Intent(getActivity().getApplicationContext(), NewNoteActivity.class);
                                                                i.putExtra("title","Translated text");
                                                                i.putExtra("desc",translatedText);
                                                                i.putExtra("dbname",dbname);
                                                                startActivity(i);
                                                                b.setEnabled(true);

                                                            }
                                                        })
                                                .addOnFailureListener(
                                                        new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(getActivity().getApplicationContext(),"Translation failed!",Toast.LENGTH_LONG).show();
                                                                b.setEnabled(true);

                                                            }
                                                        });
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Model couldn’t be downloaded or other internal error.
                                        // ...
                                        Toast.makeText(getActivity().getApplicationContext(),"Failed to download translation models!",Toast.LENGTH_LONG).show();
                                        b.setEnabled(true);

                                    }
                                });
            }
        });
        s1.setAdapter(new ArrayAdapter<String>(getActivity(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,langs));
        s2.setAdapter(new ArrayAdapter<String>(getActivity(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,langs));

        return root;
    }

    String stringToTranslateId(String s)
    {
        switch (s) {
            case "English":
                return TranslateLanguage.ENGLISH;
            case "Hindi":
                return TranslateLanguage.HINDI;
            case "Kannada":
                return TranslateLanguage.KANNADA;
            case "German":
                return TranslateLanguage.GERMAN;
            case "French":
                return TranslateLanguage.FRENCH;
        }
        return TranslateLanguage.ENGLISH;
    }
}