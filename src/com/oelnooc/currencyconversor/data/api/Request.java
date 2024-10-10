package com.oelnooc.currencyconversor.data.api;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;

import com.google.gson.Gson;
import com.oelnooc.currencyconversor.data.model.Currency;
import com.oelnooc.currencyconversor.data.model.CurrencyType;

public class Request {

    private static final String CONFIG_FILE_PATH = "src/resources/config.properties";
    private static Request instance;
    private final Gson gson;
    private final String apiKey;

    private Request() throws IOException {
        this.gson = new Gson();
        Properties properties = new Properties();

        try (FileInputStream fis = new FileInputStream(CONFIG_FILE_PATH)) {
            properties.load(fis);
        }

        this.apiKey = properties.getProperty("api.key");
        if (this.apiKey == null || this.apiKey.isEmpty()) {
            throw new IOException("API key no encontrada en " + CONFIG_FILE_PATH);
        }
    }

    public static Request getInstance() throws IOException {
        if (instance == null) {
            synchronized (Request.class) {
                if (instance == null) {
                    instance = new Request();
                }
            }
        }
        return instance;
    }

    public Currency fetchExchangeRates(CurrencyType baseCurrency) throws IOException {
        String urlString = "https://v6.exchangerate-api.com/v6/" + this.apiKey + "/latest/" + baseCurrency.getCode();
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder responseBuilder = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    responseBuilder.append(inputLine);
                }

                String jsonResponse = responseBuilder.toString();
                return gson.fromJson(jsonResponse, Currency.class);
            }
        } else {
            throw new IOException("Solicitud GET fallida. Código de respuesta: " + responseCode);
        }
    }
}
