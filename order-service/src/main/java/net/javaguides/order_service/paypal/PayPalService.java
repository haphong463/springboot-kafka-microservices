package net.javaguides.order_service.paypal;

import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class PayPalService {

    @Value("${paypal.client-id}")
    public static String CLIENT_ID;

    @Value("${paypal.client-secret}")
    public static String CLIENT_SECRET;
    public void refundPayment(String captureId) {
       try {
           URL url = new URL("https://api.sandbox.paypal.com/v2/payments/captures/" + captureId + "/refund");
           int responseCode = getResponseCode(url);
           if (responseCode == HttpURLConnection.HTTP_CREATED) {
           } else {
               throw new RuntimeException("Refund failed with response code: " + responseCode);
           }
       }catch(Exception e){
           throw new RuntimeException("Refund failed: " + e.getMessage());
       }
    }

    private int getResponseCode(URL url) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + getAccessToken());
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = "{}".getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        return responseCode;
    }

    // Lấy access token
    private String getAccessToken() throws Exception {
        URL url = new URL("https://api-m.sandbox.paypal.com/v1/oauth2/token");
        String credentials = CLIENT_ID + ":" + CLIENT_SECRET;
        String authHeader = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", authHeader);
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            String params = "grant_type=client_credentials";
            os.write(params.getBytes("utf-8"));
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                JSONObject jsonResponse = new JSONObject(response.toString());
                return jsonResponse.getString("access_token");
            }
        } else {
            throw new RuntimeException("Failed to get access token.");
        }
    }
}
