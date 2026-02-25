package com.safesoft.proapp.distribute.scanner;

import android.app.Activity;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import androidx.activity.EdgeToEdge;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;
import com.journeyapps.barcodescanner.camera.CameraSettings;
import com.safesoft.proapp.distribute.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FullScannerActivity extends BaseScannerActivity
        implements MessageDialogFragment.MessageDialogListener,
        FormatSelectorDialogFragment.FormatSelectorDialogListener,
        CameraSelectorDialogFragment.CameraSelectorDialogListener {

    private static final String FLASH_STATE = "FLASH_STATE";
    private static final String AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE";
    private static final String SELECTED_FORMATS = "SELECTED_FORMATS";
    private static final String CAMERA_ID = "CAMERA_ID";

    private DecoratedBarcodeView mScannerView;
    private boolean mFlash = false;
    private boolean mAutoFocus = true;
    private ArrayList<BarcodeFormat> mSelectedFormats;
    private int mCameraId = -1;

    // Default barcode formats (adjust as needed)
    private static final List<BarcodeFormat> DEFAULT_FORMATS = Arrays.asList(
            BarcodeFormat.QR_CODE,
            BarcodeFormat.CODE_128,
            BarcodeFormat.CODE_39,
            BarcodeFormat.EAN_13,
            BarcodeFormat.EAN_8,
            BarcodeFormat.UPC_A,
            BarcodeFormat.DATA_MATRIX,
            BarcodeFormat.AZTEC,
            BarcodeFormat.PDF_417
    );

    // Barcode scan callback
    private final BarcodeCallback mCallback = new BarcodeCallback() {
        private boolean handled = false;

        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result == null || handled) return;
            handled = true;

            playBeep();

            Intent returnIntent = new Intent();
            returnIntent.putExtra("result", result.getText());
            setResult(Activity.RESULT_OK, returnIntent);

            mScannerView.pause();
            finish();
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {}
    };

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        // Restore saved settings
        if (state != null) {
            mFlash = state.getBoolean(FLASH_STATE, false);
            mAutoFocus = state.getBoolean(AUTO_FOCUS_STATE, true);
            mCameraId = state.getInt(CAMERA_ID, -1);
            ArrayList<String> savedFormats = state.getStringArrayList(SELECTED_FORMATS);
            if (savedFormats != null) {
                mSelectedFormats = new ArrayList<>();
                for (String name : savedFormats) {
                    try {
                        mSelectedFormats.add(BarcodeFormat.valueOf(name));
                    } catch (Exception ignored) {}
                }
            }
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_full_scanner);
        setupToolbar();

        // Scanner view
        ViewGroup contentFrame = findViewById(R.id.content_frame);
        mScannerView = new DecoratedBarcodeView(this);
        contentFrame.addView(mScannerView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        setupFormats();
        applyCameraSettings();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mScannerView.decodeContinuous(mCallback);
        mScannerView.resume();
        applyTorch();
        applyAutoFocus();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mScannerView.pause();
        closeMessageDialog();
        closeFormatsDialog();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FLASH_STATE, mFlash);
        outState.putBoolean(AUTO_FOCUS_STATE, mAutoFocus);
        outState.putInt(CAMERA_ID, mCameraId);
        if (mSelectedFormats != null) {
            ArrayList<String> formatNames = new ArrayList<>();
            for (BarcodeFormat f : mSelectedFormats) {
                formatNames.add(f.name());
            }
            outState.putStringArrayList(SELECTED_FORMATS, formatNames);
        }
    }

    // 🔔 Play beep sound
    private void playBeep() {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
            if (ringtone != null) ringtone.play();
        } catch (Exception ignored) {}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        addMenuItem(menu, R.id.menu_flash, mFlash ? R.string.flash_on : R.string.flash_off);
        addMenuItem(menu, R.id.menu_auto_focus, mAutoFocus ? R.string.auto_focus_on : R.string.auto_focus_off);
        addMenuItem(menu, R.id.menu_formats, R.string.formats);
        addMenuItem(menu, R.id.menu_camera_selector, R.string.select_camera);
        return super.onCreateOptionsMenu(menu);
    }

    private void addMenuItem(Menu menu, int id, int title) {
        MenuItem item = menu.add(Menu.NONE, id, 0, title);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.menu_flash) {
            mFlash = !mFlash;
            applyTorch();
            item.setTitle(mFlash ? R.string.flash_on : R.string.flash_off);
            return true;

        } else if (itemId == R.id.menu_auto_focus) {
            mAutoFocus = !mAutoFocus;
            applyAutoFocus();
            item.setTitle(mAutoFocus ? R.string.auto_focus_on : R.string.auto_focus_off);
            return true;

        } else if (itemId == R.id.menu_formats) {
            FormatSelectorDialogFragment formatsDialog = FormatSelectorDialogFragment.newInstance(this, mSelectedFormats);
            formatsDialog.show(getSupportFragmentManager(), "format_selector");
            return true;

        } else if (itemId == R.id.menu_camera_selector) {
            mScannerView.pause();
            CameraSelectorDialogFragment cameraDialog = CameraSelectorDialogFragment.newInstance(this, mCameraId);
            cameraDialog.show(getSupportFragmentManager(), "camera_selector");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ✅ Callback when formats are saved
    @Override
    public void onFormatsSaved(ArrayList<BarcodeFormat> selectedFormats) {
        this.mSelectedFormats = selectedFormats;
        setupFormats();
    }

    // ✅ Callback when camera is selected
    @Override
    public void onCameraSelected(int cameraId) {
        this.mCameraId = cameraId;
        applyCameraSettings();
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        mScannerView.resume();
    }

    // 📌 Setup barcode formats
    private void setupFormats() {
        List<BarcodeFormat> formats = (mSelectedFormats == null || mSelectedFormats.isEmpty())
                ? DEFAULT_FORMATS
                : mSelectedFormats;

        mSelectedFormats = new ArrayList<>(formats);
        mScannerView.getBarcodeView().setDecoderFactory(new DefaultDecoderFactory(formats));
        mScannerView.pause();
        mScannerView.resume();
    }

    // 📌 Apply selected camera
    private void applyCameraSettings() {
        CameraSettings settings = mScannerView.getBarcodeView().getCameraSettings();
        if (mCameraId >= 0) settings.setRequestedCameraId(mCameraId);
        mScannerView.getBarcodeView().setCameraSettings(settings);
        mScannerView.pause();
        mScannerView.resume();
    }

    // 📌 Toggle flash
    private void applyTorch() {
        if (mFlash) mScannerView.setTorchOn();
        else mScannerView.setTorchOff();
    }

    // 📌 Toggle autofocus
    private void applyAutoFocus() {
        CameraSettings settings = mScannerView.getBarcodeView().getCameraSettings();
        settings.setAutoFocusEnabled(mAutoFocus);
        mScannerView.getBarcodeView().setCameraSettings(settings);
    }

    // 📌 Dialog utilities
    public void showMessageDialog(String message) {
        MessageDialogFragment fragment = MessageDialogFragment.newInstance("Scan Result", message, this);
        fragment.show(getSupportFragmentManager(), "scan_results");
    }

    public void closeMessageDialog() {
        closeDialog("scan_results");
    }

    public void closeFormatsDialog() {
        closeDialog("format_selector");
    }

    public void closeDialog(String dialogName) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        DialogFragment fragment = (DialogFragment) fragmentManager.findFragmentByTag(dialogName);
        if (fragment != null) fragment.dismiss();
    }
}
