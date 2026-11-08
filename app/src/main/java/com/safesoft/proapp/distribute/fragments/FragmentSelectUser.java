package com.safesoft.proapp.distribute.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.eventsClasses.EtatZSelection_Event;
import com.safesoft.proapp.distribute.postData.PostData_Client;
import com.safesoft.proapp.distribute.postData.PostData_commune;
import com.safesoft.proapp.distribute.postData.PostData_wilaya;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

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
    private AutoCompleteTextView clientSpinner;
    private AutoCompleteTextView wilayaSpinner, communeSpinner;

    private int selectedWilayaIdx = 0;
    private int selectedCommuneIdx = 0;
    private List<String> wilayaNames = new ArrayList<>();
    private List<String> communeNames = new ArrayList<>();


    private String Date_From;
    private String Date_To;
    private String wilaya;
    private String commune;

    private TextView beginsession_date;
    private TextView endsession_date;

    private Context mContext;

    private TimePickerDialog mDialogAll_first;
    private TimePickerDialog mDialogAll_end;

    ArrayAdapter<String> clientAdapter;
    ArrayAdapter<String> adapterwilaya;
    ArrayAdapter<String> adaptercommune;
    Resources res;
    private ArrayList<PostData_wilaya> wilayas = new ArrayList<>();
    private final ArrayList<PostData_wilaya> wilayas_temp = new ArrayList<>();
    private ArrayList<PostData_commune> communes = new ArrayList<>();


    public FragmentSelectUser() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_select_user, container, false);
        code_user = new PostData_Client();

        mContext = getActivity();
        getDialog().setTitle("Sélectionner");
        valid = rootView.findViewById(R.id.valid);

        controller = DATABASE.getInstance(getActivity());

        clients = controller.select_clients_from_database("SELECT * FROM Client ORDER BY CLIENT");
        if (clients == null) clients = new ArrayList<>();
        int clientCount = clients.size();
        SPINNERLIST = new String[1 + clientCount];
        SPINNERLIST[0] = "Tous";
        for (int i = 0; i < clientCount; i++) {
            SPINNERLIST[i + 1] = clients.get(i).client;
        }

        clientSpinner = rootView.findViewById(R.id.client_spinner);
        wilayaSpinner = rootView.findViewById(R.id.wilaya_spinner);
        communeSpinner = rootView.findViewById(R.id.commune_spinner);

        clientAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, SPINNERLIST);
        clientSpinner.setAdapter(clientAdapter);
        clientSpinner.setText("Tous", false);
        selected_user = "%";

        clientSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selected_user = parent.getItemAtPosition(position).toString();
                if ("Tous".equals(selected_user)) {
                    selected_user = "%";
                }
            }
        });

        res = rootView.getResources();
        try {
            wilayas = controller.select_wilayas_from_database("SELECT * FROM WILAYAS ORDER BY ID");
        } catch (Exception e) {
            Log.v("TRACKKK", "FragmentSelectUser select_wilayas: " + e.getMessage());
            wilayas = new ArrayList<>();
        }
        if (wilayas == null) wilayas = new ArrayList<>();

        wilayaNames.clear();
        for (PostData_wilaya w : wilayas) wilayaNames.add(w == null ? "" : w.wilaya);
        if (wilayaNames.isEmpty()) wilayaNames.add("<Aucune>");
        adapterwilaya = new ArrayAdapter<>(mContext,
                android.R.layout.simple_dropdown_item_1line,
                android.R.id.text1, new ArrayList<>(wilayaNames));
        wilayaSpinner.setAdapter(adapterwilaya);

        if (!wilayas.isEmpty()) {
            PostData_wilaya firstW = wilayas.get(0);
            wilaya = firstW == null ? "<Aucune>" : firstW.wilaya;
            selectedWilayaIdx = 0;
            int firstWid = firstW == null ? 0 : firstW.id;
            wilayaSpinner.setText(wilayaNames.get(0), false);
            setRecyleCommune(res, firstWid);
            if (!communes.isEmpty()) {
                PostData_commune firstC = communes.get(0);
                commune = firstC == null ? "<Aucune>" : firstC.commune;
                selectedCommuneIdx = 0;
                if (!communeNames.isEmpty()) {
                    communeSpinner.setText(communeNames.get(0), false);
                }
            } else {
                commune = "<Aucune>";
            }
        } else {
            wilaya = "<Aucune>";
            commune = "<Aucune>";
            communes = new ArrayList<>();
            communeNames.clear();
            communeNames.add("<Aucune>");
            adaptercommune = new ArrayAdapter<>(mContext,
                    android.R.layout.simple_dropdown_item_1line,
                    android.R.id.text1, new ArrayList<>(communeNames));
            communeSpinner.setAdapter(adaptercommune);
            wilayaSpinner.setText("<Aucune>", false);
            communeSpinner.setText("<Aucune>", false);
        }

        wilayaSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (wilayas == null || position < 0 || position >= wilayas.size()) return;
                PostData_wilaya w = wilayas.get(position);
                selectedWilayaIdx = position;
                wilaya = w == null ? "<Aucune>" : w.wilaya;
                setRecyleCommune(res, w == null ? 0 : w.id);
                if (!communes.isEmpty()) {
                    PostData_commune c0 = communes.get(0);
                    commune = c0 == null ? "<Aucune>" : c0.commune;
                    selectedCommuneIdx = 0;
                    if (!communeNames.isEmpty()) {
                        communeSpinner.setText(communeNames.get(0), false);
                    }
                } else {
                    commune = "<Aucune>";
                }
            }
        });

        communeSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (communes == null || position < 0 || position >= communes.size()) return;
                PostData_commune c = communes.get(position);
                selectedCommuneIdx = position;
                commune = c == null ? "<Aucune>" : c.commune;
            }
        });

        if (getDialog() != null) {
            getDialog().setCanceledOnTouchOutside(true);
        }

        valid.setOnClickListener(v -> {
            try {
                String clientText = clientSpinner.getText() == null ? "Tous" : clientSpinner.getText().toString();
                if (clientText.equals("Tous")) {
                    selected_user = "%";
                    code_client = null;
                } else {
                    selected_user = clientText;
                    try {
                        code_user = controller.select_client_etat_from_database(selected_user);
                    } catch (Exception e) {
                        code_user = null;
                        Log.v("TRACKKK", "FragmentSelectUser select_client_etat: " + e.getMessage());
                    }
                    if (code_user == null) {
                        code_client = null;
                        Crouton.makeText(getActivity(), "Client introuvable, sélectionnez-en un autre.", Style.ALERT).show();
                        return;
                    }
                    code_client = code_user.code_client;
                }

                String wf = wilaya == null ? "<Aucune>" : wilaya;
                String cf = commune == null ? "<Aucune>" : commune;
                String df = Date_From == null ? new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()) : Date_From;
                String dt = Date_To == null ? df : Date_To;
                String su = selected_user == null ? "%" : selected_user;

                EventBus.getDefault().post(new EtatZSelection_Event(su, df, dt, code_client, wf, cf));
                if (getDialog() != null) getDialog().dismiss();
            } catch (Exception e) {
                Log.v("TRACKKK", "FragmentSelectUser valid onClick: " + e.getMessage());
                Crouton.makeText(getActivity(), "Erreur validation : " + e.getMessage(), Style.ALERT).show();
            }

        });

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df_affiche = new SimpleDateFormat("yyyy-MM-dd");


        c.add(Calendar.DAY_OF_YEAR, -1);
        Date newDate = c.getTime();
        String yesterday = df_affiche.format(newDate);

        beginsession_date = rootView.findViewById(R.id.beginsession_date);
        beginsession_date.setText(yesterday);
        Date_From = df_affiche.format(c.getTime());

        c.add(Calendar.DAY_OF_YEAR, +1);
        Date nextDate = c.getTime();
        String today = df_affiche.format(c.getTime());
        endsession_date = rootView.findViewById(R.id.endsession_date);
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

        long OneYears = 365 * 1000 * 60 * 60 * 24L;
        long oneday = 1000 * 60 * 60 * 24L;

        mDialogAll_first = new TimePickerDialog.Builder()
                .setType(Type.YEAR_MONTH_DAY)
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
                .setMinMillseconds(System.currentTimeMillis() - OneYears)
                .setMaxMillseconds(System.currentTimeMillis())
                .setCurrentMillseconds(System.currentTimeMillis() - oneday)
                .setThemeColor(getResources().getColor(R.color.nephritis))

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
                .setMinMillseconds(System.currentTimeMillis() - OneYears)
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
    public void onStart() {
        super.onStart();
        if (getDialog() == null || getDialog().getWindow() == null) return;
        android.view.Window window = getDialog().getWindow();
        if (window != null) {
            android.util.DisplayMetrics metrics = new android.util.DisplayMetrics();
            if (getActivity() != null && getActivity().getWindowManager() != null) {
                getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
            } else if (window.getWindowManager() != null) {
                window.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            }
            int targetWidth = (int) (metrics.widthPixels * 0.90f);
            int targetHeight = (int) (metrics.heightPixels * 0.80f);
            window.setLayout(targetWidth, targetHeight);
            window.setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
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
        SimpleDateFormat df_affiche = new SimpleDateFormat("yyyy-MM-dd");

        if (timePickerView.getTag().equals("begin")) {
            selected_date = new Date(millseconds);
            beginsession_date.setText(df_affiche.format(selected_date));
            Date_From = df_affiche.format(selected_date);

        } else if (timePickerView.getTag().equals("end")) {
            selected_date = new Date(millseconds);
            endsession_date.setText(df_affiche.format(selected_date));
            Date_To = df_affiche.format(selected_date);
        }
    }


    public void setRecyleCommune(Resources res, int wilaya_id) {
        communes.clear();
        communes = controller.select_communes_from_database("SELECT * FROM COMMUNES WHERE CODE_WILAYA = " + wilaya_id + " ORDER BY COMMUNE");
        if (communes == null) communes = new ArrayList<>();
        communeNames.clear();
        for (PostData_commune c : communes) communeNames.add(c == null ? "" : c.commune);
        if (communeNames.isEmpty()) communeNames.add("<Aucune>");
        adaptercommune = new ArrayAdapter<>(mContext,
                android.R.layout.simple_dropdown_item_1line,
                android.R.id.text1, new ArrayList<>(communeNames));
        if (communeSpinner != null) {
            communeSpinner.setAdapter(adaptercommune);
            communeSpinner.setText(communeNames.get(0), false);
        }
        selectedCommuneIdx = 0;
    }
}