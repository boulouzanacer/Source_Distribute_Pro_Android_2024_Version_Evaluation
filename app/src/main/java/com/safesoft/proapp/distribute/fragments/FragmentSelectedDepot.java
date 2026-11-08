package com.safesoft.proapp.distribute.fragments;

import android.os.Bundle;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.labo.kaji.swipeawaydialog.SwipeAwayDialogFragment;
import com.rilixtech.materialfancybutton.MaterialFancyButton;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.adapters.ListViewAdapterListDepot;
import com.safesoft.proapp.distribute.postData.PostData_Depot;

import java.util.ArrayList;

/**
 * Created by UK2016 on 03/04/2017.
 */

public class FragmentSelectedDepot extends SwipeAwayDialogFragment {

    private ListView listview;
    private ListViewAdapterListDepot adapter;
    private ArrayList<PostData_Depot> list_depots;
    private String title;
    private TextView title_list_bon;


    public FragmentSelectedDepot() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        list_depots = new ArrayList<>();
        list_depots = this.getArguments().getParcelableArrayList("LIST_SELECTED_DEPOTS");
        title = this.getArguments().getString("TITLE");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_selected_bon_transfert, container, false);

        listview = rootView.findViewById(R.id.listview_bon_t);
        title_list_bon = rootView.findViewById(R.id.title_list_bon);
        title_list_bon.setText(title);
        TextView subtitle = rootView.findViewById(R.id.subtitle_list_label);
        if (subtitle != null) subtitle.setText("LISTE DES DÉPÔTS");

        MaterialFancyButton btnValid = rootView.findViewById(R.id.btn_connect);
        MaterialFancyButton btnCancel = rootView.findViewById(R.id.btn_cancel);
        LinearLayout footerBar = (LinearLayout) btnCancel != null ? (LinearLayout) btnCancel.getParent() : null;
        if (btnValid != null) btnValid.setVisibility(View.GONE);
        if (footerBar != null) footerBar.setWeightSum(1);
        if (btnCancel != null) {
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) btnCancel.getLayoutParams();
            lp.weight = 1;
            btnCancel.setLayoutParams(lp);
            btnCancel.setBackgroundColor(Color.WHITE);
            btnCancel.setTextColor(Color.parseColor("#0F172A"));
            btnCancel.setOnClickListener(v -> finich_fragment());
        }

        adapter = new ListViewAdapterListDepot(getActivity(), list_depots, FragmentSelectedDepot.this);
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
