package com.example.madprojectt;

import static androidx.core.app.ActivityCompat.invalidateOptionsMenu;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DocumentListFragment} factory method to
 * create an instance of this fragment.
 */
public class DocumentListFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    View root;
    String dbname;
    ArrayList<Integer> ids;
    int sort = 0;
    public DocumentListFragment(String dbname) {
        // Required empty public constructor
        this.dbname = dbname;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DocumentListFragment.
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
    /*
    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.document_action, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mi)
    {
        switch (mi.getItemId())
        {
            case R.id.newdoc:
            {
                Intent intent = new Intent(getActivity().getApplicationContext(),NewNoteActivity.class);
                intent.putExtra("dbname",dbname);
                startActivity(intent);
                return true;
            }
        }

        return false;
    }
    */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        root = inflater.inflate(R.layout.fragment_document_list, container, false);

        Toolbar tb = root.findViewById(R.id.myToolbar);

        tb.inflateMenu(R.menu.document_action);
        tb.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem mi) {
                switch (mi.getItemId())
                {
                    case R.id.newdoc:
                    {
                        Intent intent = new Intent(getActivity().getApplicationContext(),NewNoteActivity.class);
                        intent.putExtra("dbname",dbname);
                        startActivity(intent);
                        return true;
                    }
                    case R.id.sort:
                    {
                        sort = (sort + 1) % 3;
                        if (sort == 0)
                            Toast.makeText(getActivity().getApplicationContext(),"Sort order: unsorted",Toast.LENGTH_LONG).show();
                        else if (sort == 1)
                            Toast.makeText(getActivity().getApplicationContext(),"Sort order: alphabetic ascending",Toast.LENGTH_LONG).show();
                        else if (sort == 2)
                            Toast.makeText(getActivity().getApplicationContext(),"Sort order: alphabetic descending",Toast.LENGTH_LONG).show();
                        populateAdapter();
                    }
                }
                return false;
            }
        });

        ListView l = root.findViewById(R.id.lv);
        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity().getApplicationContext(), ViewNoteActivity.class);
                NoteDBHelper h = new NoteDBHelper(getActivity().getApplicationContext(),dbname);
                SQLiteDatabase db = h.getWritableDatabase();

                Cursor c = db.rawQuery("select title, descr from notes where noteid=?",new String[]{ids.get(i)+""});
                c.moveToNext();

                intent.putExtra("title",c.getString(0));
                intent.putExtra("desc",c.getString(1));

                startActivity(intent);

            }
        });
        l.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                PopupMenu pm = new PopupMenu(getActivity().getApplicationContext(),view);
                pm.inflate(R.menu.note_menu);

                pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId())
                        {
                            case R.id.notedel:
                                NoteDBHelper h = new NoteDBHelper(getActivity().getApplicationContext(),dbname);
                                SQLiteDatabase db = h.getWritableDatabase();

                                db.delete("notes","noteid=?",new String[]{(ids.get(i)+"")});
                                populateAdapter();
                                return true;
                            case R.id.edit:
                                Intent intent = new Intent(getActivity().getApplicationContext(),NewNoteActivity.class);
                                NoteDBHelper h2 = new NoteDBHelper(getActivity().getApplicationContext(),dbname);
                                SQLiteDatabase db2 = h2.getWritableDatabase();

                                intent.putExtra("dbname",dbname);
                                intent.putExtra("id",ids.get(i));

                                Cursor c = db2.rawQuery("select title, descr from notes where noteid=?",new String[]{ids.get(i)+""});
                                c.moveToNext();

                                intent.putExtra("title",c.getString(0));
                                intent.putExtra("desc",c.getString(1));
                                startActivity(intent);
                                return true;
                        }
                        return false;
                    }
                });
                pm.show();
                return true;
            }
        });
        return root;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        populateAdapter();
    }

    void populateAdapter()
    {
        NoteDBHelper h = new NoteDBHelper(getActivity().getApplicationContext(), dbname);
        SQLiteDatabase db = h.getReadableDatabase();

        ArrayList<String> al = new ArrayList<String>();
        ArrayList<Integer> newids = new ArrayList<>();
        ArrayList<Note> al2 = new ArrayList<Note>();

        String suffix = "";
        if (sort == 1)
        {
            suffix = " order by title asc";
        }
        else if (sort == 0)
            suffix = " order by title desc";
        else
            suffix = "";
        Cursor c = db.rawQuery("select noteid,title, descr from notes"+ suffix,null);
        while (c.moveToNext())
        {
            al.add(c.getString(1));
            newids.add(c.getInt(0));
            Note n = new Note();
            n.title = c.getString(1);
            n.note = c.getString(2);
            al2.add(n);
        }

        ids = newids;
        //ArrayAdapter<String> ad = new ArrayAdapter<>(getActivity(), com.google.android.material.R.layout.support_simple_spinner_dropdown_item,al);

        NoteListItemAdapter ad = new NoteListItemAdapter(getActivity(),al2);
        ListView l = root.findViewById(R.id.lv);
        l.setAdapter(ad);




    }
}