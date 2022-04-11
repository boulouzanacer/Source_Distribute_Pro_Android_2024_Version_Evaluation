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
import com.safesoft.proapp.distribute.adapters.ListViewAdapterListClient;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.postData.PostData_Client;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by UK2016 on 03/04/2017.
 */

public class FragmentListClient extends SwipeAwayDialogFragment {

  private ListView listview;
  private ListViewAdapterListClient adapter;
  private ArrayList<PostData_Client> fourniss;
  private DATABASE controller;
  private EditText editsearch;



  public FragmentListClient() {
    // Required empty public constructor
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    controller = new DATABASE(getActivity());

  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    View rootView = inflater.inflate(R.layout.fragment_all_clients, container, false);
    getDialog().setTitle("List Clients ");

    fourniss = new ArrayList<>();

    String querry = "SELECT * FROM Client";
    fourniss = controller.select_clients_from_database(querry);
    listview = (ListView) rootView.findViewById(R.id.list_produit);
    adapter = new ListViewAdapterListClient(getActivity(), fourniss, FragmentListClient.this);
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
      public void beforeTextChanged(CharSequence arg0, int arg1,
                                    int arg2, int arg3) {
        // TODO Auto-generated method stub
      }

      @Override
      public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                int arg3) {
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

  public void finich_fragment()
  {
    getActivity().getFragmentManager().beginTransaction().remove(this).commit();
  }

}
