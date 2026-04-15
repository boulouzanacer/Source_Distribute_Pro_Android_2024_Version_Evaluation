package com.safesoft.proapp.distribute.activities.achats;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Android Java class to extract invoice data using Qwen-VL-Plus.
 * Extends AsyncTask as requested (Note: AsyncTask is deprecated in modern Android,
 * consider using ExecutorService or Coroutines for production apps).
 */
public class QwenInvoiceExtractor extends AsyncTask<byte[], Void, String> {

    private static final String TAG = "QwenInvoiceExtractor";

    // 🔐 DASH SCOPE API KEY
    private static final String API_KEY = "sk-fbb937fee4ce44879113fa25ae9b015c";

    // API Endpoint
    private static final String API_URL = "https://dashscope-intl.aliyuncs.com/compatible-mode/v1/chat/completions";

    // Model Name
    private static final String MODEL_NAME = "qwen3-vl-plus-2025-12-19";

    private final OnExtractionListener listener;

    /**
     * Interface to receive the result of the extraction.
     */

    public interface OnExtractionListener {
        void onExtractionComplete(String jsonResult);
        void onExtractionError(String errorMessage);
    }

    public QwenInvoiceExtractor(OnExtractionListener listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(byte[]... params) {
        if (params == null || params.length == 0) {
            return "Error: No image data provided";
        }

        byte[] imageData = params[0];

        try {
            // 1. Convert image byte[] to Base64 data URL
            String base64Image = Base64.encodeToString(imageData, Base64.NO_WRAP);
            String dataUrl = "data:image/png;base64," + base64Image;

            // 2. Construct JSON Payload
            JSONObject payload = new JSONObject();
            payload.put("model", MODEL_NAME);

            JSONArray messages = new JSONArray();

            // System message
            JSONObject systemMsg = new JSONObject();
            systemMsg.put("role", "system");
            systemMsg.put("content", "You extract structured invoice data.");
            messages.put(systemMsg);

            // User message with image and prompt
            JSONObject userMsg = new JSONObject();
            userMsg.put("role", "user");

            JSONArray content = new JSONArray();

            // Image part
            JSONObject imagePart = new JSONObject();
            imagePart.put("type", "image_url");
            JSONObject imageUrl = new JSONObject();
            imageUrl.put("url", dataUrl);
            imagePart.put("image_url", imageUrl);
            content.put(imagePart);

            // Text part
            JSONObject textPart = new JSONObject();
            textPart.put("type", "text");
            textPart.put("text", getInvoicePrompt());
            content.put(textPart);

            userMsg.put("content", content);
            messages.put(userMsg);

            payload.put("messages", messages);

            // Response format
            JSONObject responseFormat = new JSONObject();
            responseFormat.put("type", "json_object");
            payload.put("response_format", responseFormat);

            payload.put("temperature", 0);

            // 3. Send HTTP Request
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(60000);
            conn.setReadTimeout(120000);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = payload.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line.trim());
                }

                // Parse the response content
                JSONObject result = new JSONObject(response.toString());
                return result.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");
            } else {
                return "Error: API returned HTTP " + responseCode;
            }

        } catch (Exception e) {
            Log.e(TAG, "Extraction failed", e);
            return "Error: " + e.getMessage();
        }
    }

    @Override
    protected void onPostExecute(String result) {
        if (listener != null) {
            if (result.startsWith("Error:")) {
                listener.onExtractionError(result);
            } else {
                listener.onExtractionComplete(result);
            }
        }
    }

    private String getInvoicePrompt() {
        return "Extract all invoice data and return JSON only.\n" +
                "If a value is missing use null.\n" +
                "Amounts must be numeric.\n" +
                "Dates must be ISO format if possible (YYYY-MM-DD).\n" +
                "Structure:\n" +
                "{\n" +
                "  \"invoice_number\": string|null,\n" +
                "  \"invoice_date\": string|null,\n" +
                "  \"due_date\": string|null,\n" +
                "  \"seller_name\": string|null,\n" +
                "  \"buyer_name\": string|null,\n" +
                "  \"buyer_phone\": string|null,\n" +
                "  \"currency\": string|null,\n" +
                "  \"subtotal\": number|null,\n" +
                "  \"tax_total\": number|null,\n" +
                "  \"remise_total\": number|null,\n" +
                "  \"total\": number|null,\n" +
                "  \"line_items\": [\n" +
                "    {\n" +
                "      \"code\": string|null,\n" +
                "      \"codebarre\": string|null,\n" +
                "      \"description\": string|null,\n" +
                "      \"quantity\": number|null,\n" +
                "      \"unit_price\": number|null,\n" +
                "      \"remise\": number|null,\n" +
                "      \"tax_rate\": number|null,\n" +
                "      \"line_total\": number|null\n" +
                "    }\n" +
                "  ]\n" +
                "}";
    }
}
