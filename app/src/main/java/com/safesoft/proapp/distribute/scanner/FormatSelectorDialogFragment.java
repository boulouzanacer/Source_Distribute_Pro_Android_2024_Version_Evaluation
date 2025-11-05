package com.safesoft.proapp.distribute.scanner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import com.google.zxing.BarcodeFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FormatSelectorDialogFragment extends DialogFragment {

    public interface FormatSelectorDialogListener {
        void onFormatsSaved(ArrayList<BarcodeFormat> selectedFormats);
    }

    private ArrayList<BarcodeFormat> mSelectedFormats;
    private FormatSelectorDialogListener mListener;

    // ✅ Liste des formats supportés par ZXing (tu peux en ajouter ou en retirer)
    private static final List<BarcodeFormat> SUPPORTED_FORMATS = Arrays.asList(
            BarcodeFormat.QR_CODE,
            BarcodeFormat.EAN_13,
            BarcodeFormat.EAN_8,
            BarcodeFormat.CODE_128,
            BarcodeFormat.CODE_39,
            BarcodeFormat.CODE_93,
            BarcodeFormat.UPC_A,
            BarcodeFormat.UPC_E,
            BarcodeFormat.ITF,
            BarcodeFormat.PDF_417,
            BarcodeFormat.AZTEC,
            BarcodeFormat.DATA_MATRIX
    );

    public static FormatSelectorDialogFragment newInstance(FormatSelectorDialogListener listener, ArrayList<BarcodeFormat> selectedFormats) {
        FormatSelectorDialogFragment fragment = new FormatSelectorDialogFragment();
        fragment.mListener = listener;
        fragment.mSelectedFormats = selectedFormats != null ? new ArrayList<>(selectedFormats) : new ArrayList<>();
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (mSelectedFormats == null || mListener == null) {
            dismiss();
            return null;
        }

        // Construction des noms et états cochés
        String[] formatNames = new String[SUPPORTED_FORMATS.size()];
        boolean[] checkedItems = new boolean[SUPPORTED_FORMATS.size()];

        for (int i = 0; i < SUPPORTED_FORMATS.size(); i++) {
            BarcodeFormat format = SUPPORTED_FORMATS.get(i);
            formatNames[i] = format.toString();
            checkedItems[i] = mSelectedFormats.contains(format);
        }

        // Création de la boîte de dialogue
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choisir formats à scanner")
                .setMultiChoiceItems(formatNames, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        BarcodeFormat selectedFormat = SUPPORTED_FORMATS.get(which);
                        if (isChecked) {
                            if (!mSelectedFormats.contains(selectedFormat)) {
                                mSelectedFormats.add(selectedFormat);
                            }
                        } else {
                            mSelectedFormats.remove(selectedFormat);
                        }
                    }
                })
                .setPositiveButton("✅ OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onFormatsSaved(mSelectedFormats);
                    }
                })
                .setNegativeButton("❌ Annuler", null);

        return builder.create();
    }
}
