package com.safesoft.proapp.distribute.fragments;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.labo.kaji.swipeawaydialog.SwipeAwayDialogFragment;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.adapters.ListViewAdapter_ListProduits_main;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.eventsClasses.MessageEvent;
import com.safesoft.proapp.distribute.postData.PostData_Produit;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by UK2015 on 01/09/2016.
 */
public class Fragment_All_Product extends SwipeAwayDialogFragment {

  private ListView listview;
  private ListViewAdapter_ListProduits_main adapter;
  private ArrayList<PostData_Produit> produits;
  private DATABASE controller;
  private String PREFS_DATA = "DataConfig";
  private Boolean switch_mode = false;
  private EditText editsearch;

  private EventBus bus;

  public Fragment_All_Product() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    controller = new DATABASE(getActivity());
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    View rootView = inflater.inflate(R.layout.fragment_all_product, container, false);
    getDialog().setTitle("List produits ");

    produits = new ArrayList<>();
    bus = EventBus.getDefault();

    String querry = "SELECT * FROM Produit";
    produits = controller.select_produits_from_database(querry);
    listview = (ListView) rootView.findViewById(R.id.list_produit);
    adapter = new ListViewAdapter_ListProduits_main(getActivity(),produits);
    listview.setAdapter(adapter);

    editsearch = (EditText) rootView.findViewById(R.id.search_field);

    // Capture Text in EditText
    editsearch.addTextChangedListener(new TextWatcher() {

      @Override
      public void afterTextChanged(Editable arg0) {
        // TODO Auto-generated method stub
        String text = editsearch.getText().toString().toLowerCase(Locale.getDefault());
        adapter.filter(text);
      }

      @Override
      public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub
      }

      @Override
      public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub
      }
    });
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

  @Subscribe
  public void onOperationEnded(MessageEvent event){
    finich_fragment();
  }

  @Override
  public void onStart() {
    super.onStart();
    // Register as a subscriber
    bus.register( this);
  }

  @Override
  public void onDestroy() {
    // Unregister
    bus.unregister(this);
    super.onDestroy();
  }
}