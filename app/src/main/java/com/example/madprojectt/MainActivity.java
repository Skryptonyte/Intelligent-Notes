package com.example.madprojectt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    String tabs[] = {"DOCS","SCAN","KEK"};
    Hashtable<String, Integer> ht = new Hashtable<>();
    LinkedList<String> folders;
    DrawerLayout dl;
    NavigationView nv;
    int folderid = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dl = findViewById(R.id.drawer_layout);
        nv = findViewById(R.id.nav_view);

        loadFolders();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setLogo(R.drawable.baseline_view_list_24);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_view_list_24);

        nv.setNavigationItemSelectedListener(this::onNavigationItemSelected);
        nv.setCheckedItem(R.id.nav_home);



        /*
        TabLayout tb = findViewById(R.id.tablayout);
        ViewPager2 vp = findViewById(R.id.vp);

        vp.setAdapter(new ViewPagerAdapter(this));
        new TabLayoutMediator(tb, vp,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override public void onConfigureTab(TabLayout.Tab tab, int position) {

                        tab.setText(tabs[position]);
                    }
                }).attach();

         */
    }

    @Override
    protected void onActivityResult(int req, int res, Intent data)
    {
        super.onActivityResult(req,res,data);
        if (req == 1337)
        {
            Uri uri = data.getData();

            Toast.makeText(getApplicationContext(),"Added file!: "+uri.getPath(),Toast.LENGTH_LONG).show();

        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                if (dl.isDrawerOpen(Gravity.LEFT))
                    dl.closeDrawer(Gravity.LEFT);
                else {
                    dl.openDrawer(Gravity.LEFT);
                    nv.bringToFront();

                }return true;
        }
        return false;
    }
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.addnewfolder:
            {
                AlertDialog.Builder ad = new AlertDialog.Builder(this);
                EditText tv = new EditText(this);

                ad.setTitle("Enter name of new folder: ");
                ad.setView(tv);

                ad.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        addFolder(tv.getText().toString());
                        saveFolders();

                    }
                });

                AlertDialog a = ad.create();
                a.show();
                return true;
            }

            case  R.id.deletefolder:
            {
                AlertDialog.Builder ad = new AlertDialog.Builder(this);
                EditText tv = new EditText(this);

                ad.setTitle("Folder to Delete");
                ad.setView(tv);

                ad.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        removeFolder(tv.getText().toString());
                        saveFolders();


                    }
                });

                AlertDialog a = ad.create();
                a.show();
                return true;
            }
        }

        dl.closeDrawer(Gravity.LEFT);
        Toast.makeText(getApplicationContext(),"Selected folder:"+item.toString(),Toast.LENGTH_LONG).show();
        getSupportActionBar().setTitle(item.toString());
        getSupportFragmentManager().beginTransaction().replace(R.id.fl, new TabbedFragment(item.toString().toLowerCase())).commit();
        nv.setCheckedItem(item.getItemId());
        return true;
    }

    void addFolder(String name)
    {
        if (!name.isEmpty()) {
            if (ht.getOrDefault(name,-1) != -1)
            {
                Toast.makeText(getApplicationContext(),"Folder already exists!",Toast.LENGTH_LONG);
                return;
            }
            MenuItem it = nv.getMenu().findItem(R.id.folderitemwrap).getSubMenu().add(R.id.folderlist, folderid, 1000, name);
            it.setIcon(R.drawable.foldericon).setCheckable(true);

            folders.add(name);
            ht.put(name, folderid);
            folderid++;

        }
    }
    void removeFolder(String name)
    {
        if (!name.isEmpty())
        {
            if (ht.getOrDefault(name,-1) != -1) {
                nv.getMenu().findItem(R.id.folderitemwrap).getSubMenu().removeItem(ht.get(name));
                ht.remove(name);
                folders.remove(name);
            }

        }
    }

    void saveFolders()
    {
        SharedPreferences sp = getSharedPreferences("folders",MODE_PRIVATE);
        SharedPreferences.Editor et = sp.edit();

        Gson gson = new Gson();
        et.putString("array",gson.toJson(folders));

        et.commit();
    }

    void loadFolders()
    {
        SharedPreferences sp = getSharedPreferences("folders",MODE_PRIVATE);

        Gson gson = new Gson();
        if (sp.getString("array","").isEmpty())
        {
            folders = new LinkedList<String>();
        }
        else
        {

            folders = gson.fromJson(sp.getString("array",""),LinkedList.class);

            for (String name: folders)
            {
                MenuItem it = nv.getMenu().findItem(R.id.folderitemwrap).getSubMenu().add(R.id.folderlist, folderid, 1000, name);
                it.setIcon(R.drawable.foldericon).setCheckable(true);
                ht.put(name, folderid);
                folderid++;
            }
        }
    }

}