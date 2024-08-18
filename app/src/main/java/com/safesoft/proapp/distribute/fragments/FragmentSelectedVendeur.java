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
import com.safesoft.proapp.distribute.adapters.ListViewAdapterListVendeur;
import com.safesoft.proapp.distribute.postData.PostData_Vendeur;

import java.util.ArrayList;

/**
 * Created by UK2016 on 03/04/2017.
 */

public class FragmentSelectedVendeur extends SwipeAwayDialogFragment {

    private ListView listview;
    private ListViewAdapterListVendeur adapter;
    private ArrayList<PostData_Vendeur> list_vendeurs;
    private String title;
    private TextView title_list_bon;


    public FragmentSelectedVendeur() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        list_vendeurs = new ArrayList<>();
        list_vendeurs = this.getArguments().getParcelableArrayList("LIST_SELECTED_VENDEURS");
        title = this.getArguments().getString("TITLE");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_selected_bon_transfert, container, false);

        listview = rootView.findViewById(R.id.listview_bon_t);
        title_list_bon = rootView.findViewById(R.id.title_list_bon);
        title_list_bon.setText(title);
        adapter = new ListViewAdapterListVendeur(getActivity(), list_vendeurs, FragmentSelectedVendeur.this);
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
