package com.safesoft.proapp.distribute.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.labo.kaji.swipeawaydialog.SwipeAwayDialogFragment;
import com.safesoft.proapp.distribute.adapters.ListViewAdapterListSelectedBon;
import com.safesoft.proapp.distribute.R;

import java.util.ArrayList;

/**
 * Created by UK2016 on 03/04/2017.
 */

public class FragmentSelectedBonTransfert extends SwipeAwayDialogFragment {

  private ListView listview;
  private ListViewAdapterListSelectedBon adapter;
  private ArrayList<String> list_bons_t;
  private String title;
  private TextView title_list_bon;



  public FragmentSelectedBonTransfert() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    list_bons_t = new ArrayList<>();
    list_bons_t = this.getArguments().getStringArrayList("LIST_SELECTED_TRANSFERT_BON");
    title = this.getArguments().getString("TITLE");

  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    View rootView = inflater.inflate(R.layout.fragment_selected_bon_transfert, container, false);

    listview = (ListView) rootView.findViewById(R.id.listview_bon_t);
    title_list_bon = (TextView) rootView.findViewById(R.id.title_list_bon);
    title_list_bon.setText(title);
    adapter = new ListViewAdapterListSelectedBon(getActivity(), list_bons_t, FragmentSelectedBonTransfert.this);
    listview.setAdapter(adapter);

    return rootView;
  }


  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
  }

  public void finich_fragment(){
    getActivity().getFragmentManager().beginTransaction().remove(this).commit();
  }

}
