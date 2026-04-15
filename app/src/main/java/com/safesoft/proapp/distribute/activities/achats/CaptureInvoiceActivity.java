package com.safesoft.proapp.distribute.activities.achats;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.safesoft.proapp.distribute.R;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.Executor;

public class CaptureInvoiceActivity extends AppCompatActivity {

    private static final String TAG = "CaptureInvoiceActivity";
    private FrameLayout loadingOverlay;
    private PreviewView previewView;
    private Button btnCapture;

    private ImageCapture imageCapture;
    private Executor mainExecutor;

    private final ActivityResultLauncher<String> cameraPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) startCamera();
                else Toast.makeText(this, "Permission caméra refusée", Toast.LENGTH_LONG).show();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_invoice);

        previewView = findViewById(R.id.previewView);
        btnCapture = findViewById(R.id.btnCapture);
        mainExecutor = ContextCompat.getMainExecutor(this);

        loadingOverlay = findViewById(R.id.loadingOverlay);

        btnCapture.setOnClickListener(v -> captureAndSend());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }

    }

    private void startCamera() {

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {

            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).build();

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

            } catch (Exception e) {
                Log.e(TAG, "startCamera failed", e);
                Toast.makeText(this, "Erreur caméra: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }, mainExecutor);
    }

    private void captureAndSend() {
        if (imageCapture == null) {
            Toast.makeText(this, "Caméra non prête", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);

        imageCapture.takePicture(mainExecutor, new ImageCapture.OnImageCapturedCallback() {
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy image) {
                try {
                    byte[] bytes = imageProxyToJpegBytes(image);

                    new QwenInvoiceExtractor(new QwenInvoiceExtractor.OnExtractionListener() {
                        @Override
                        public void onExtractionComplete(String jsonResult) {
                            showLoading(false);

                            // ✅ Si tu veux renvoyer le JSON à l'Activity précédente:
                            // Intent i = new Intent();
                            // i.putExtra("invoice_json", jsonResult);
                            // setResult(RESULT_OK, i);
                            Log.v("JSON AI RESULT", jsonResult);

                            android.content.Intent intent = new android.content.Intent(CaptureInvoiceActivity.this, InvoiceResultActivity.class);
                            intent.putExtra("invoice_json", jsonResult);
                            startActivity(intent);

                            Toast.makeText(CaptureInvoiceActivity.this, "Extraction OK", Toast.LENGTH_SHORT).show();
                            finish(); // ✅ Fermer la fenêtre
                        }

                        @Override
                        public void onExtractionError(String errorMessage) {
                            showLoading(false);

                            // setResult(RESULT_CANCELED);

                            Toast.makeText(CaptureInvoiceActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                            finish(); // ✅ Fermer la fenêtre même si erreur
                        }
                    }).execute(bytes);

                } catch (Exception e) {
                    showLoading(false);
                    Toast.makeText(CaptureInvoiceActivity.this, "Erreur capture: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    finish();
                } finally {
                    image.close();
                }
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                showLoading(false);
                Toast.makeText(CaptureInvoiceActivity.this, "Capture échouée: " + exception.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void showLoading(boolean show) {
        loadingOverlay.setVisibility(show ? View.VISIBLE : View.GONE);
        btnCapture.setEnabled(!show);
    }

    /**
     * Convertit ImageProxy (format YUV) -> JPEG bytes
     * (simple et fonctionne bien pour envoyer au serveur)
     */

    private byte[] imageProxyToJpegBytes(ImageProxy image) {

        ImageProxy.PlaneProxy[] planes = image.getPlanes();

        // ✅ Cas 1 : déjà JPEG (1 seul plane)
        if (planes.length == 1) {
            ByteBuffer buffer = planes[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            return bytes;
        }

        // ✅ Cas 2 : YUV_420_888 (3 planes)
        if (planes.length < 3) {
            throw new IllegalStateException("Unsupported ImageProxy format. planes=" + planes.length);
        }

        ByteBuffer yBuffer = planes[0].getBuffer();
        ByteBuffer uBuffer = planes[1].getBuffer();
        ByteBuffer vBuffer = planes[2].getBuffer();

        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();

        byte[] nv21 = new byte[ySize + uSize + vSize];

        // Y
        yBuffer.get(nv21, 0, ySize);

        // VU (NV21 = Y + V + U)
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        int width = image.getWidth();
        int height = image.getHeight();

        android.graphics.YuvImage yuvImage = new android.graphics.YuvImage(nv21, android.graphics.ImageFormat.NV21, width, height, null);

        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        yuvImage.compressToJpeg(new android.graphics.Rect(0, 0, width, height), 90, out);
        return out.toByteArray();
    }



}