package com.safesoft.proapp.distribute.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.eventsClasses.EtatZSelection_Event;
import com.safesoft.proapp.distribute.postData.PostData_Client;
import com.safesoft.proapp.distribute.R;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by UK2015 on 27/09/2016.
 */
public class FragmentSelectUser extends DialogFragment implements OnDateSetListener {

  private String[] SPINNERLIST;
  private DATABASE controller;
  private ArrayList<PostData_Client> clients;
  private Button valid;
  private String selected_user;
  private PostData_Client code_user;
  private String code_client;


  private String Date_From;
  private String Date_To;

  private TextView beginsession_date;
  private TextView endsession_date;


  private Context mContext;

  private TimePickerDialog mDialogAll_first;
  private TimePickerDialog mDialogAll_end;


  public FragmentSelectUser() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    View rootView = inflater.inflate(R.layout.fragment_select_user, container, false);
    code_user = new PostData_Client();

    mContext = getActivity();
    getDialog().setTitle("Sélectionner");
    valid = (Button) rootView.findViewById(R.id.valid);
    valid.setBackgroundColor(getResources().getColor(R.color.emerald));
    controller = new DATABASE(getActivity());
    clients = controller.select_clients_from_database("SELECT * FROM Client ORDER BY CLIENT");
    SPINNERLIST = new String[clients.size()];
    for (int i = 0; i < clients.size(); i++) {
      SPINNERLIST[i] = clients.get(i).client;
    }
    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, SPINNERLIST);
    final MaterialBetterSpinner materialDesignSpinner = (MaterialBetterSpinner) rootView.findViewById(R.id.android_material_design_spinner);
    materialDesignSpinner.setAdapter(arrayAdapter);
    materialDesignSpinner.setText("Tous");

    materialDesignSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selected_user = parent.getItemAtPosition(position).toString();
      }
    });

    getDialog().setCanceledOnTouchOutside(true);

    valid.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        // do some work
        valid.setBackgroundColor(getResources().getColor(R.color.nephritis));
        if(materialDesignSpinner.getText().toString().equals("Tous")){
          selected_user = "%";
          code_client= null;
        }else{
          selected_user = materialDesignSpinner.getText().toString();
          code_user = controller.select_client_etat_from_database(selected_user);
          code_client = code_user.code_client;


        }
        EventBus.getDefault().post(new EtatZSelection_Event(selected_user, Date_From, Date_To, code_client));
        getDialog().dismiss();
      }
    });

    Calendar c = Calendar.getInstance();
    SimpleDateFormat df_affiche = new SimpleDateFormat("dd/MM/yyyy");
//

    c.add(Calendar.DAY_OF_YEAR, -1);
    Date newDate = c.getTime();
    String yesterday = df_affiche.format(newDate);

    beginsession_date = (TextView) rootView.findViewById(R.id.beginsession_date);
    beginsession_date.setText(yesterday);
    Date_From = df_affiche.format(c.getTime());

    c.add(Calendar.DAY_OF_YEAR, +1);
    Date nextDate = c.getTime();
    String today = df_affiche.format(c.getTime());
    endsession_date = (TextView) rootView.findViewById(R.id.endsession_date);
    endsession_date.setText(today);
    Date_To = df_affiche.format(nextDate);


    beginsession_date.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mDialogAll_first.show(getFragmentManager(), "begin");
      }
    });

    endsession_date.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        assert getFragmentManager() != null;
        mDialogAll_end.show(getFragmentManager(), "end");
      }
    });

    long tenYears = 10L * 365 * 1000 * 60 * 60 * 24L;
    long oneday = 1000 * 60 * 60 * 24L;

    mDialogAll_first = new TimePickerDialog.Builder()
            .setCallBack(this)
            .setCancelStringId("Annuler")
            .setSureStringId("Valider")
            .setTitleStringId("Date debut")
            .setYearText("")
            .setMonthText("")
            .setDayText("")
            //.setHourText("H")
            //.setMinuteText("mn")
            .setCyclic(false)
            .setMinMillseconds(System.currentTimeMillis() - tenYears)
            .setMaxMillseconds(System.currentTimeMillis()+ oneday)
            .setCurrentMillseconds(System.currentTimeMillis())
            .setThemeColor(getResources().getColor(R.color.nephritis))
            .setType(Type.YEAR_MONTH_DAY)
            .setWheelItemTextNormalColor(getResources().getColor(R.color.timetimepicker_default_text_color))
            .setWheelItemTextSelectorColor(getResources().getColor(R.color.timepicker_toolbar_bg))
            .setWheelItemTextSize(12)
            .build();


    mDialogAll_end = new TimePickerDialog.Builder()
            .setCallBack(this)
            .setCancelStringId("Annuler")
            .setSureStringId("Valider")
            .setTitleStringId("Date fin")
            .setYearText("")
            .setMonthText("")
            .setDayText("")
           // .setHourText("H")
           // .setMinuteText("mn")
            .setCyclic(false)
            .setMinMillseconds(System.currentTimeMillis() - tenYears)
            .setMaxMillseconds(System.currentTimeMillis())
            .setCurrentMillseconds(System.currentTimeMillis())
            .setThemeColor(getResources().getColor(R.color.nephritis))
            .setType(Type.YEAR_MONTH_DAY)
            .setWheelItemTextNormalColor(getResources().getColor(R.color.timetimepicker_default_text_color))
            .setWheelItemTextSelectorColor(getResources().getColor(R.color.timepicker_toolbar_bg))
            .setWheelItemTextSize(12)
            .build();

    return rootView;
  }


  @Override
  @Subscribe
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    EventBus.getDefault().register(this);

  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    EventBus.getDefault().unregister(this);

  }

  @Override
  public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
    Date selected_date;
    SimpleDateFormat df_affiche = new SimpleDateFormat("dd/MM/yyyy");

    if(timePickerView.getTag().equals("begin")){
      selected_date = new Date(millseconds);
      beginsession_date.setText(df_affiche.format(selected_date));
      Date_From = df_affiche.format(selected_date);

    }else if(timePickerView.getTag().equals("end")){
      selected_date = new Date(millseconds);
      endsession_date.setText(df_affiche.format(selected_date));
      Date_To = df_affiche.format(selected_date);
    }
  }
}