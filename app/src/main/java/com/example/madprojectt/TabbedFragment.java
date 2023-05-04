package com.example.madprojectt;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TabbedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabbedFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    String dbname = "";
    public TabbedFragment(String name) {
        // Required empty public constructor
        dbname = name;
    }



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
        String tabs[] = {"Notes","Translate","Scan"};
        int icons[] = {R.drawable.baseline_notes_24,  R.drawable.baseline_translate_24,R.drawable.ic_menu_camera};

        View root =  inflater.inflate(R.layout.fragment_tabbed, container, false);
        TabLayout tb = root.findViewById(R.id.tablayout);
        ViewPager2 vp = root.findViewById(R.id.vp);

        vp.setAdapter(new ViewPagerAdapter(requireActivity(),dbname));
        new TabLayoutMediator(tb, vp,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override public void onConfigureTab(TabLayout.Tab tab, int position) {

                        tab.setText(tabs[position]);
                        tab.setIcon(icons[position]);
                    }
                }).attach();
        return root;
    }
}