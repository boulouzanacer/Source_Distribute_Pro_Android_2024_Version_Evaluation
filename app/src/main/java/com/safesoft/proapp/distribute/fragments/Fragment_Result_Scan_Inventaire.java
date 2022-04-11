package com.safesoft.proapp.distribute.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.labo.kaji.swipeawaydialog.SwipeAwayDialogFragment;
import com.safesoft.proapp.distribute.R;
import com.safesoft.proapp.distribute.databases.DATABASE;
import com.safesoft.proapp.distribute.eventsClasses.MessageEvent;
import com.safesoft.proapp.distribute.postData.PostData_Inv2;

import org.greenrobot.eventbus.EventBus;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by UK2015 on 01/09/2016.
 */
public class Fragment_Result_Scan_Inventaire extends SwipeAwayDialogFragment {

    private Context context;
    private DATABASE controller;
    private String _produit;
    private String _reference;
    private String _codebarre;
    private String _pa_ttc;
    private String _stock_old;
    private String _tva;
    private String _num_inv;
    private Boolean checked_before;
    private String _quantity_added;
    private RelativeLayout message_show;

    private PostData_Inv2 _inv = new PostData_Inv2();

    private TextView prod;
    private TextView ref;
    private TextView code_b;
    private EditText stcok_ph;
    private Button save_inv;
    private TextView message_id;

    private EventBus bus = EventBus.getDefault();
    private MessageEvent event1 = null;

    public Fragment_Result_Scan_Inventaire() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        _produit = args.getString("PRODUIT");
        _reference = args.getString("REFERENCE");
        _codebarre = args.getString("CODEBARRE");
        _pa_ttc = args.getString("PA_TTC");
        _stock_old = args.getString("STOCK_OLD");
        _tva = args.getString("TVA");
        _num_inv = args.getString("NUM_INV");
        checked_before = args.getBoolean("CHECK_BEFORE");
        _quantity_added = args.getString("QUANTITY_ADDED");

        controller = new DATABASE(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_result_scan_inventaire, container, false);
        getDialog().setTitle("Resultat du scanner");

        message_show = (RelativeLayout) rootView.findViewById(R.id.relativeLayout2);
        message_id = (TextView) rootView.findViewById(R.id.message_id);

        if(checked_before){
            //afficher message
            message_show.setVisibility(View.VISIBLE);
            message_id.setText("Vous avez déja invontoré ce produit avec le stock " + _quantity_added);
        }else{
            // sans message
            message_show.setVisibility(View.GONE);
        }

        prod = (TextView) rootView.findViewById(R.id.produit);
        ref = (TextView) rootView.findViewById(R.id.reference);
        code_b = (TextView) rootView.findViewById(R.id.code_barre);
        stcok_ph = (EditText) rootView.findViewById(R.id.stock_ph);
        save_inv = (Button) rootView.findViewById(R.id.save_inv);

        prod.setText(_produit);
        ref.setText(_reference);
        code_b.setText(_codebarre);
        context = getActivity();

        save_inv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // sauvegarder inventaire
                //  String msg="Today we’d like to share a couple of simple styles and effects for android notifications.";

                //   NiftyNotificationView.build(getActivity() ,msg , effect, R.id.mLyout)
                //          .setIcon(R.mipmap.ic_launcher)         //You must call this method if you use ThumbSlider effect
                //          .show();

                if(stcok_ph.getText().length() > 0){
                    Boolean executed = false;
                    try {
                        if(checked_before){
                            _inv.produit = prod.getText().toString();
                            _inv.reference = ref.getText().toString();
                            _inv.codebarre = code_b.getText().toString();
                            _inv.quantity_new = String.valueOf(Integer.parseInt(stcok_ph.getText().toString()) + Integer.parseInt(_quantity_added));
                            _inv.quantity_old = _stock_old;
                            _inv.tva = _tva;
                            _inv.pa_ht = _pa_ttc;
                            _inv.num_inv = _num_inv;
                            executed = controller.Update_inventaire2(_inv);
                        }else{
                            _inv.produit = prod.getText().toString();
                            _inv.reference = ref.getText().toString();
                            _inv.codebarre = code_b.getText().toString();
                            _inv.quantity_new = stcok_ph.getText().toString();
                            _inv.quantity_old = _stock_old;
                            _inv.tva = _tva;
                            _inv.pa_ht = _pa_ttc;
                            _inv.num_inv = _num_inv;
                            executed = controller.Insert_into_inventaire2(_inv);
                        }

                        //insert into inventaire2

                        if(executed){
                            Crouton.showText(getActivity(), "Bien ajouté", Style.CONFIRM);
                            // someEventListener.someEvent("FROM_RESULT_SCAN_FRAGMENT", null);
                            event1 = new MessageEvent("OK");
                            bus.post(event1);
                            getDialog().dismiss();
                        }else{
                            //
                            Crouton.showText(getActivity(), "Erreur d'insertion inventaire", Style.ALERT);
                        }
                    }catch (Exception e){
                        Crouton.showText(getActivity(),"Problème lors de l'insertion", Style.ALERT);
                    }
                }else{
                    Toast.makeText(getActivity(), "Vous devez saisir le stock !", Toast.LENGTH_SHORT).show();
                }

            }
        });

        TextView.OnEditorActionListener exampleListener = new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if(stcok_ph.getText().length() > 0){
                        Boolean executed = false;
                        try {
                            if(checked_before){
                                _inv.produit = prod.getText().toString();
                                _inv.reference = ref.getText().toString();
                                _inv.codebarre = code_b.getText().toString();
                                _inv.quantity_new = String.valueOf(Integer.parseInt(stcok_ph.getText().toString()) + Integer.parseInt(_quantity_added));
                                _inv.quantity_old = _stock_old;
                                _inv.tva = _tva;
                                _inv.pa_ht = _pa_ttc;
                                _inv.num_inv = _num_inv;
                                executed = controller.Update_inventaire2(_inv);
                            }else{
                                _inv.produit = prod.getText().toString();
                                _inv.reference = ref.getText().toString();
                                _inv.codebarre = code_b.getText().toString();
                                _inv.quantity_new = stcok_ph.getText().toString();
                                _inv.quantity_old = _stock_old;
                                _inv.tva = _tva;
                                _inv.pa_ht = _pa_ttc;
                                _inv.num_inv = _num_inv;
                                executed = controller.Insert_into_inventaire2(_inv);
                            }

                            //insert into inventaire2

                            if(executed){
                                Crouton.showText(getActivity(), "Bien ajouté", Style.CONFIRM);
                                event1 = new MessageEvent("OK");
                                bus.post(event1);
                                getDialog().dismiss();
                            }else{
                                //
                                Crouton.showText(getActivity(), "Erreur d'insertion inventaire", Style.ALERT);
                            }

                        }catch (Exception e){
                            Crouton.showText(getActivity(),"Problème lors de l'insertion", Style.ALERT);
                        }

                    }else{
                        Toast.makeText(getActivity(), "Vous devez saisir le stock !", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
        };

        stcok_ph.setOnEditorActionListener(exampleListener);
        return rootView;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

}