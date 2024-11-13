package com.safesoft.proapp.distribute.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import com.labo.kaji.swipeawaydialog.SwipeAwayDialogFragment;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.adapters.ListViewAdapterDatabases;
import com.safesoft.proapp.distribute.adapters.ListViewAdapterListDepot;

import java.util.ArrayList;

/**
 * Created by UK2016 on 03/04/2017.
 */

public class FragmentListDatabases extends SwipeAwayDialogFragment {

    private ListView listview;
    private ListViewAdapterDatabases adapter;
    private ArrayList<String> list_databases;
    private String title;
    private TextView title_list_bon;


    public FragmentListDatabases() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        list_databases = new ArrayList<>();
        list_databases = this.getArguments().getStringArrayList("LIST_DATABASES");
        title = this.getArguments().getString("TITLE");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_selected_bon_transfert, container, false);

        listview = rootView.findViewById(R.id.listview_bon_t);
        title_list_bon = rootView.findViewById(R.id.title_list_bon);
        title_list_bon.setText(title);
        adapter = new ListViewAdapterDatabases(getActivity(), list_databases, FragmentListDatabases.this);
        listview.setAdapter(adapter);

        return rootView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        //Specify the length and width through constants
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(getDialog().getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        //layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes(layoutParams);
    }

    public void finich_fragment() {
        getActivity().getFragmentManager().beginTransaction().remove(this).commit();
    }

}
