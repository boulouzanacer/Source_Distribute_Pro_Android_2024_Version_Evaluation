package com.safesoft.proapp.distribute.activities.vente;

import android.app.Activity;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsetsController;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.safesoft.proapp.distribute.adapters.RecyclerAdapterClientsVersement;
import com.safesoft.proapp.distribute.adapters.model.MyDataObject;
import com.safesoft.proapp.distribute.adapters.model.WrappedMyDataObject;
import com.safesoft.proapp.distribute.adapters.viewholder.advanced.AdvancedDataViewHolder;
import com.safesoft.proapp.distribute.adapters.viewholder.advanced.AdvancedDataViewHolderObjectif;
import com.safesoft.proapp.distribute.adapters.viewholder.advanced.HeaderViewHolder;
import com.safesoft.proapp.distribute.adapters.viewholder.advanced.HeaderViewHolderTotal;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.eventsClasses.EtatZSelection_Event;
import com.safesoft.proapp.distribute.fragments.FragmentSelectUser;
import com.safesoft.proapp.distribute.postData.PostData_Client;
import com.safesoft.proapp.distribute.postData.PostData_Etatv;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.printing.Printing;
import com.victor.loading.book.BookLoading;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import eu.inloop.simplerecycleradapter.ItemClickListener;
import eu.inloop.simplerecycleradapter.ItemLongClickListener;
import eu.inloop.simplerecycleradapter.SettableViewHolder;
import eu.inloop.simplerecycleradapter.SimpleRecyclerAdapter;

public class ActivityEtatV extends AppCompatActivity implements ItemClickListener<WrappedMyDataObject>, ItemLongClickListener<WrappedMyDataObject> {

    private RecyclerView mRecyclerView;
    private SimpleRecyclerAdapter<WrappedMyDataObject> mRecyclerAdapter;
    private RelativeLayout relative_error;
    private LinearLayout tite_session;
    private ImageView retry;
    private EtatZSelection_Event event_selection;
    private BookLoading bookloading;
    private RelativeLayout empty_data;
    private TextView txtv_debut, txtv_fin, txtv_user, txtv_wilaya, txtv_commune;
    private DATABASE controller;
    private Boolean printer_mode_integrate = true;
    private Thread thread;
    private Handler handler;
    private ArrayList<PostData_Etatv> result_etatzg;
    private ArrayList<PostData_Client> versement_clients;
    private final EventBus bus = EventBus.getDefault();
    private String c_client, client;
    private String from_d;
    private String to_d;
    private String wilaya;
    private String commune;
    private final String PREFS = "ALL_PREFS";
    private SharedPreferences prefs;
    private String errorMessage = "";
    private TextView txtv_etat_centre_status;
    private FloatingActionButton fab;
    private View popupContainer;
    private ImageView btnClosePopup;
    private RecyclerView recyclerClients;
    private RecyclerAdapterClientsVersement adapter;
    private ArrayList<PostData_Client> clients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_etat_v);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            getWindow().getInsetsController().hide(WindowInsetsController.BEHAVIOR_DEFAULT);
            getWindow().getInsetsController().setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            );
        }else {
            WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        }

        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        // Register as a subscriber
        bus.register(this);

    }

    @Override
    protected void onStart() {

        initViews();

        Toolbar toolbar = findViewById(R.id.myToolbar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Statistiques ventes");
            //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back_24);
        }

        empty_data.setVisibility(View.VISIBLE);
        retry = findViewById(R.id.retry);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get_etatzg();
                start_select_etatz(1);
            }
        });


        controller = new DATABASE(this);
        versement_clients = new ArrayList<>();

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);

        initAdapter();

        setupFAbButton();

        fab.setOnClickListener(v -> {
            if(!versement_clients.isEmpty()){

                popupContainer.setVisibility(View.VISIBLE);
                // init RecyclerView si pas encore fait
                recyclerClients.setLayoutManager(new LinearLayoutManager(this));
                recyclerClients.setHasFixedSize(true);
                recyclerClients.setNestedScrollingEnabled(true);
                adapter = new RecyclerAdapterClientsVersement(this, versement_clients);
                // adapter exemple
                recyclerClients.setAdapter(adapter);

                fab.setOnTouchListener(null);

            }else{
                Crouton.makeText(ActivityEtatV.this, "Aucun client versé dans la periode séléctionner !", Style.ALERT).show();
            }


        });

        btnClosePopup.setOnClickListener(v -> {
            popupContainer.setVisibility(View.GONE);
            setupFAbButton(); // your method
        });

        popupContainer.setOnClickListener(v -> {
            popupContainer.setVisibility(View.GONE);
        });


        SharedPreferences prefs1 = getSharedPreferences(PREFS, MODE_PRIVATE);
        printer_mode_integrate = prefs1.getString("PRINTER_CONX", "INTEGRATE").equals("INTEGRATE");


        txtv_etat_centre_status.setText("Etat de vente client...");
        super.onStart();
    }


    private void setupFAbButton(){

        final int[] parentW = new int[1];
        final int[] parentH = new int[1];

        fab.post(() -> {
            View parent = (View) fab.getParent();
            parentW[0] = parent.getWidth();
            parentH[0] = parent.getHeight();
        });

        float[] startX = new float[1];
        float[] startY = new float[1];
        boolean[] isClick = new boolean[]{true};

        VelocityTracker velocityTracker = VelocityTracker.obtain();

        fab.setOnTouchListener(new View.OnTouchListener() {

            float dX, dY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                velocityTracker.addMovement(event);

                switch (event.getActionMasked()) {

                    case MotionEvent.ACTION_DOWN:

                        velocityTracker.clear();

                        dX = v.getX() - event.getRawX();
                        dY = v.getY() - event.getRawY();

                        startX[0] = event.getRawX();
                        startY[0] = event.getRawY();

                        isClick[0] = true;
                        return true;

                    case MotionEvent.ACTION_MOVE:

                        float newX = event.getRawX() + dX;
                        float newY = event.getRawY() + dY;

                        // clamp écran
                        newX = Math.max(0, Math.min(newX, parentW[0] - v.getWidth()));
                        newY = Math.max(0, Math.min(newY, parentH[0] - v.getHeight()));

                        v.setX(newX);
                        v.setY(newY);

                        if (Math.abs(event.getRawX() - startX[0]) > 10 ||
                                Math.abs(event.getRawY() - startY[0]) > 10) {
                            isClick[0] = false;
                        }

                        return true;

                    case MotionEvent.ACTION_UP:

                        if (isClick[0]) {
                            v.performClick();
                            return true;
                        }

                        velocityTracker.computeCurrentVelocity(1000);

                        float velocityX = velocityTracker.getXVelocity();

                        float currentX = v.getX();
                        float targetX;

                        // 📌 SNAP logique : gauche ou droite
                        if (velocityX > 0 || currentX > parentW[0] / 2f) {
                            targetX = parentW[0] - v.getWidth() - 20; // droite
                        } else {
                            targetX = 20; // gauche
                        }

                        float targetY = v.getY();

                        // 🔵 SPRING ANIMATION X
                        SpringAnimation animX = new SpringAnimation(v, SpringAnimation.X, targetX);
                        SpringForce springX = new SpringForce(targetX)
                                .setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY)
                                .setStiffness(SpringForce.STIFFNESS_LOW);

                        animX.setSpring(springX);
                        animX.start();

                        // 🔵 SPRING ANIMATION Y (léger rebond vertical aussi)
                        SpringAnimation animY = new SpringAnimation(v, SpringAnimation.Y, targetY);
                        SpringForce springY = new SpringForce(targetY)
                                .setDampingRatio(SpringForce.DAMPING_RATIO_MEDIUM_BOUNCY)
                                .setStiffness(SpringForce.STIFFNESS_LOW);

                        animY.setSpring(springY);
                        animY.start();

                        return true;
                }

                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        result_etatzg = new ArrayList<>();

        //get_etatzg();
    }

    private void initViews() {

        //recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        bookloading = findViewById(R.id.bookloading);
        relative_error = findViewById(R.id.relative_error);
        ViewGroup mainView = findViewById(R.id.lempty_data);
        empty_data = findViewById(R.id.lempty_data);
        tite_session = findViewById(R.id.tite_session);

        //TextView
        txtv_debut = findViewById(R.id.debut);
        txtv_fin = findViewById(R.id.fin);
        txtv_user = findViewById(R.id.user);

        txtv_etat_centre_status = findViewById(R.id.txtv_etat_centre_status);

        txtv_wilaya = findViewById(R.id.txtv_wilaya);
        txtv_commune = findViewById(R.id.txtv_commune);

        fab = findViewById(R.id.fabDraggable);
        popupContainer = findViewById(R.id.popupContainer);
        btnClosePopup = findViewById(R.id.btnClosePopup);
        recyclerClients = findViewById(R.id.recyclerClients);

    }


    public ArrayList<PostData_Client> getItems(String qqry) {

        //clients.clear();

        if (!qqry.isEmpty()) {
            clients = new ArrayList<>();
            String querry = "SELECT * FROM CLIENT WHERE CODE_CLIENT LIKE '%" + qqry + "%' OR CLIENT LIKE '%" + qqry + "%' OR TEL LIKE '%" + qqry + "%' ORDER BY CLIENT";
            clients = controller.select_clients_from_database(querry);
        } else {
            clients = new ArrayList<>();
            String querry = "SELECT * FROM CLIENT ORDER BY CLIENT";
            clients = controller.select_clients_from_database(querry);
        }

        return clients;
    }

    public void show_select_etatz() {
        FragmentSelectUser dialogFragment = new FragmentSelectUser();
        dialogFragment.show(getSupportFragmentManager(), "Sample Fragment");
    }


    @Subscribe
    public void getEventSelection(EtatZSelection_Event event) {

        event_selection = event;
        txtv_debut.setText("De " + event_selection.getDate_f());
        txtv_fin.setText("Vers " + event_selection.getDate_t());
        txtv_wilaya.setText("W : " + event_selection.getWilaya());
        txtv_commune.setText("Com : " + event_selection.getCommune());

        if (event.getUser().equals("%")) {
            txtv_user.setText("Tous");
        } else {
            txtv_user.setText(" " + event_selection.getUser());
        }

        client = event_selection.getUser();
        c_client = event_selection.getCode_user();

        from_d = event_selection.getDate_f();
        to_d = event_selection.getDate_t();
        wilaya = event_selection.getWilaya();
        commune = event_selection.getCommune();

        get_etatzg();
    }

    public void startProgress() {
        bookloading.start();
    }

    public void stopProgress() {
        bookloading.stop();
    }

    public void stopAndError() {
        bookloading.stop();
        //with erreur image
    }

    @SuppressWarnings("unchecked")
    private void initAdapter() {
        mRecyclerAdapter = new SimpleRecyclerAdapter<>(this, new SimpleRecyclerAdapter.CreateViewHolder<WrappedMyDataObject>() {
            @NonNull
            @Override
            protected SettableViewHolder<WrappedMyDataObject> onCreateViewHolder(ViewGroup parent, int viewType) {
                return switch (viewType) {
                    case WrappedMyDataObject.ITEM_TYPE_NORMAL ->
                            new AdvancedDataViewHolder(ActivityEtatV.this, R.layout.item_mydata, parent);
                    case WrappedMyDataObject.ITEM_TYPE_HEADER ->
                            new HeaderViewHolder(ActivityEtatV.this, R.layout.item_header, parent);
                    case WrappedMyDataObject.ITEM_TYPE_HEADER_TOTAL ->
                            new HeaderViewHolderTotal(ActivityEtatV.this, R.layout.item_header_total, parent);
                    case WrappedMyDataObject.ITEM_TYPE_OBJECTIF ->
                            new AdvancedDataViewHolderObjectif(ActivityEtatV.this, R.layout.item_mydata_objectif, parent);
                    default -> throw new AssertionError("Wrong view type");
                };
            }

            @Override
            protected int getItemViewType(int position) {
                return mRecyclerAdapter.getItem(position).getType();
            }

            @Override
            protected int getItemViewType(@NonNull WrappedMyDataObject item, int position) {
                return item.getType();
            }
        });
        mRecyclerAdapter.setLongClickListener(this);
        mRecyclerView.setAdapter(mRecyclerAdapter);
    }

    public void initData(ArrayList<PostData_Etatv> result_etat_z) {

        //here we reset the parents and the children
        mRecyclerAdapter.clear();
        mRecyclerAdapter.addItem(WrappedMyDataObject.initHeaderItem("Produit", "QTE", "TOT"));

        //mRecyclerAdapter.addItem(WrappedMyDataObject.initHeaderItem(result_etat_z.get(i).produit, result_etat_z.get(i).quantite, result_etat_z.get(i).montant));

        for (int i = 0; i < result_etat_z.size(); i++) {
            switch (result_etat_z.get(i).code_parent) {
                case "1" ->
                        mRecyclerAdapter.addItem(WrappedMyDataObject.initDataItem(new MyDataObject(result_etat_z.get(i).produit, result_etat_z.get(i).quantite, result_etat_z.get(i).montant, result_etat_z.get(i).code_parent)));
                case "-6" -> {
                    mRecyclerAdapter.addItem(WrappedMyDataObject.initHeaderItemTotal("Conclusion Total : "));
                    for (int k = i, b = 1; k < result_etat_z.size() - 1; k++,b++) {
                        i = k;
                        if(b == 8){
                            if(prefs.getBoolean("AFFICHAGE_BENIFICE", false)){
                                mRecyclerAdapter.addItem(WrappedMyDataObject.initDataItem(new MyDataObject(result_etat_z.get(k).produit, result_etat_z.get(k).quantite, result_etat_z.get(k).montant, result_etat_z.get(i).code_parent)));
                            }
                        }else{
                            mRecyclerAdapter.addItem(WrappedMyDataObject.initDataItem(new MyDataObject(result_etat_z.get(k).produit, result_etat_z.get(k).quantite, result_etat_z.get(k).montant, result_etat_z.get(i).code_parent)));
                        }
                    }
                }
                case "-8" -> {
                    mRecyclerAdapter.addItem(WrappedMyDataObject.initHeaderItemTotal("Objectif : "));
                    mRecyclerAdapter.addItem(WrappedMyDataObject.initDataItemObjectif(new MyDataObject(result_etat_z.get(i).produit, result_etat_z.get(i).quantite, result_etat_z.get(i).montant, result_etat_z.get(i).code_parent)));
                }
            }
        }
        mRecyclerAdapter.notifyDataSetChanged();
    }


    @Override
    public void onItemClick(@NonNull WrappedMyDataObject item, @NonNull SettableViewHolder<WrappedMyDataObject> viewHolder, @NonNull View view) {
        if (item.getType() == WrappedMyDataObject.ITEM_TYPE_NORMAL) {

            MyDataObject dataObject = item.getDataObject();
            int itemPos = viewHolder.getAdapterPosition();

      /*  case R.id.btn_more:
                 //   setTitle("Action clicked on item: " + dataObject.getTitle());
                    break;
                case R.id.btn_remove:
                    mRecyclerAdapter.removeItem(item, true);
                    break;
                case R.id.btn_move_up:
                    mRecyclerAdapter.swapItem(itemPos, Math.max(0, itemPos - 1), true);
                    break;
                case R.id.btn_move_down:
                    int maxIndex = mRecyclerAdapter.getItemCount() - 1;
                    mRecyclerAdapter.swapItem(itemPos, Math.min(maxIndex, itemPos + 1), true);
                    break;*/
            //Actual item click
            // setTitle("Last clicked item: " + dataObject.getTitle());
        }
    }

    @Override
    public boolean onItemLongClick(@NonNull WrappedMyDataObject item, @NonNull SettableViewHolder<WrappedMyDataObject> viewHolder, @NonNull View view) {
        if (item.getType() == WrappedMyDataObject.ITEM_TYPE_NORMAL) {
            MyDataObject dataObject = item.getDataObject();

            //setTitle("Action LONG clicked on item: " + dataObject.getTitle());
            return view.getId() == -1;

        }
        return false;
    }

    public void start_select_etatz(Integer i) {
        empty_data.setVisibility(View.GONE);
        switch (i) {
            case 1:
                mRecyclerView.setVisibility(View.GONE);
                relative_error.setVisibility(View.GONE);
                bookloading.setVisibility(View.VISIBLE);
                tite_session.setVisibility(View.GONE);
                startProgress();
                break;
            case 2:
                bookloading.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
                tite_session.setVisibility(View.VISIBLE);
                stopProgress();
                break;
            case 3:
                mRecyclerView.setVisibility(View.GONE);
                bookloading.setVisibility(View.GONE);
                relative_error.setVisibility(View.VISIBLE);
                tite_session.setVisibility(View.VISIBLE);
                //visible erreur image
                stopAndError();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_etatv, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
        } else if (itemId == R.id.synchroniser) {

            show_select_etatz();

        } else if (itemId == R.id.print) {
            if (!result_etatzg.isEmpty()) {

                Activity bactivity;
                bactivity = ActivityEtatV.this;

                Printing printer = new Printing();
                printer.start_print_etat(bactivity, "ETAT_VENTE", result_etatzg, c_client, txtv_user.getText().toString(), from_d, to_d);
            }

        }

        return super.onOptionsItemSelected(item);
    }


    protected void get_etatzg() {
        //===========
        handler = new Handler() {
            public void handleMessage(Message msg) {
                try {
                    //=====================
                    switch (msg.what) {
                        case 0:
                            start_select_etatz(1);
                            break;
                        case 1:
                            start_select_etatz(2);
                            //here we reset the parents and the children
                            mRecyclerAdapter.clear();
                            if (result_etatzg.size() > 7) {
                                initData(result_etatzg);
                            }
                            break;
                        case 2:
                            new SweetAlertDialog(ActivityEtatV.this, SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Oops...")
                                    .setContentText("Vous avez un problem au niveau de la requette SQL!: " + errorMessage)
                                    .show();
                            start_select_etatz(3);
                            break;
                    }

                } catch (Exception ex) {

                }
            }
        };

        comunication();
    }

    public void comunication() {

        thread = new Thread(() -> {
            try {
                handler.sendEmptyMessage(0);
                int flag;

                //success
                if (result_etatzg != null) {
                    result_etatzg.clear();
                }


                flag = getEtatzgs(c_client, from_d, to_d);


                if (flag == 0) {
                    //failed
                    handler.sendEmptyMessage(3);
                } else if (flag == 1) {
                    handler.sendEmptyMessage(1);
                } else if (flag == 2) {
                    //problem
                    handler.sendEmptyMessage(2);
                }

            } catch (Exception e) {
                e.printStackTrace();
                errorMessage = e.getMessage();
                handler.sendEmptyMessage(3);
            }
        });

        thread.start();
    }

    public int getEtatzgs(String c_client, String from_d, String to_d) {
        int flag;

        try {
            result_etatzg = controller.select_etatv_from_database(wilaya, commune, c_client, from_d, to_d);
            versement_clients = controller.select_versed_list_client_from_database(wilaya, commune, c_client, from_d, to_d);
            flag = 1;
        } catch (Exception e) {
            Log.v("TRACKKK", e.getMessage());
            errorMessage = e.getMessage();
            flag = 2;
        }
        return flag;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {

        bus.unregister(this);

        super.onDestroy();
    }

}
