package com.safesoft.pro.distribute.fragments;

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
import com.safesoft.pro.distribute.R;
import com.safesoft.pro.distribute.databases.DATABASE;
import com.safesoft.pro.distribute.eventsClasses.MessageEvent;
import com.safesoft.pro.distribute.postData.PostData_Achat2;

import org.greenrobot.eventbus.EventBus;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * Created by UK2015 on 01/09/2016.
 */
public class Fragment_Result_Scan_Achat extends SwipeAwayDialogFragment {

  private Context context;
  private DATABASE controller;
  private String _produit;
  private String _reference;
  private String _codebarre;
  private String _pa_ttc;
  private String _stock_old;
  private String _tva;
  private String _num_achat;
  private Boolean checked_before;
  private String _quantity_added;
  private String _price_added;
  private RelativeLayout message_show;

  private PostData_Achat2 _achat = new PostData_Achat2();

  private TextView prod;
  private TextView code_b;
  private EditText stcok_ph;
  private EditText prix_a;
  private Button save_achat;
  private TextView message_id;

  private EventBus bus = EventBus.getDefault();
  private MessageEvent event1 = null;

  public Fragment_Result_Scan_Achat() {
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
    _num_achat = args.getString("NUM_ACHAT");
    checked_before = args.getBoolean("CHECK_BEFORE");
    _quantity_added = args.getString("QUANTITY_ADDED");
    _price_added = args.getString("PRICE_ADDED");

    controller = new DATABASE(getActivity());

  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    View rootView = inflater.inflate(R.layout.fragment_result_scan_achat, container, false);
    getDialog().setTitle("Resultat du scanner ");

    message_show = (RelativeLayout) rootView.findViewById(R.id.relativeLayout2);
    message_id = (TextView) rootView.findViewById(R.id.message_id);

    if(checked_before){
      //afficher message
      message_show.setVisibility(View.VISIBLE);
      message_id.setText("Vous avez déja acheté ce produit avec le stock " + _quantity_added + " et avec un prix : " + _price_added);
    }else{
      // sans message
      message_show.setVisibility(View.GONE);
    }

    prod = (TextView) rootView.findViewById(R.id.produit);
    code_b = (TextView) rootView.findViewById(R.id.code_barre);
    stcok_ph = (EditText) rootView.findViewById(R.id.stock_ph);
    prix_a = (EditText) rootView.findViewById(R.id.prix_a);
    save_achat = (Button) rootView.findViewById(R.id.save_achat);

    prod.setText(_produit);
    code_b.setText(_codebarre);
    context = getActivity();

    save_achat.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        // sauvegarder inventaire
        //  String msg="Today we’d like to share a couple of simple styles and effects for android notifications.";

        //   NiftyNotificationView.build(getActivity() ,msg , effect, R.id.mLyout)
        //          .setIcon(R.mipmap.ic_launcher)         //You must call this method if you use ThumbSlider effect
        //          .show();

        if(stcok_ph.getText().length() > 0 || prix_a.getText().length() > 0){
          Boolean executed = false;
          try {
            _achat.produit = prod.getText().toString();
            _achat.reference = _reference;
            _achat.codebarre = code_b.getText().toString();
            _achat.qte = stcok_ph.getText().toString();
            _achat.tva = _tva;
            _achat.pa_ht = prix_a.getText().toString();
            _achat.num_achat = _num_achat;

            //insert into achat2
            executed = controller.Insert_into_achat2(_achat);

            if(executed){
              Crouton.showText(getActivity(), "Achat bien ajouté", Style.CONFIRM);
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
          Toast.makeText(getActivity(), "Vous devez saisir la quantité achétée ou le prix d'achat!", Toast.LENGTH_SHORT).show();
        }
      }
    });


    TextView.OnEditorActionListener exampleListener = new TextView.OnEditorActionListener() {

      @Override
      public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN) {

          if(stcok_ph.getText().length() > 0 || prix_a.getText().length() > 0){
            Boolean executed = false;
            try {
              _achat.produit = prod.getText().toString();
              _achat.reference = _reference;
              _achat.codebarre = code_b.getText().toString();
              _achat.qte = stcok_ph.getText().toString();
              _achat.tva = _tva;
              _achat.pa_ht = prix_a.getText().toString();
              _achat.num_achat = _num_achat;

              //insert into achat2
              executed = controller.Insert_into_achat2(_achat);

              if(executed){
                Crouton.showText(getActivity(), "Achat bien ajouté", Style.CONFIRM);
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
            Toast.makeText(getActivity(), "Vous devez saisir la quantité achétée ou le prix d'achat!", Toast.LENGTH_SHORT).show();
          }
        }
        return true;
      }
    };

    stcok_ph.setOnEditorActionListener(exampleListener);
    prix_a.setOnEditorActionListener(exampleListener);

    return rootView;

  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
  }

}