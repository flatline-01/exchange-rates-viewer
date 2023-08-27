package org.example;

import org.json.JSONObject;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Properties;

public class API_Connection {
    /*
    * API1 - https://currency.getgeoapi.com/
    * API2 - https://app.currencyapi.com/
    * */
    private static final StringBuilder BASE_API_URL1 = new StringBuilder("https://api.getgeoapi.com/v2/currency/");
    private static final StringBuilder BASE_API_URL2 = new StringBuilder("https://api.currencyapi.com/v3/");
    private static final Properties SYSTEM_PROPERTIES = System.getProperties();

    private static JSONObject getResponse(String url) {
        JSONObject jObj;
        try {
            URI uri = new URI(url);
            HttpClient client = HttpClient
                    .newBuilder()
                    .build();
            HttpRequest request = HttpRequest
                    .newBuilder()
                    .GET()
                    .uri(uri)
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            jObj = new JSONObject(response.body());
        } catch (IOException | InterruptedException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return jObj;
    }

    public static JSONObject getAvailableCurrencies() {
        StringBuilder URL = new StringBuilder(BASE_API_URL1.toString());
        URL.append("list");
        URL.append("?api_key=");
        URL.append(SYSTEM_PROPERTIES.get("API_KEY1"));
        return getResponse(URL.toString());
    }

    public static JSONObject convert(double amount, String from, String to) {
        StringBuilder URL = new StringBuilder(BASE_API_URL1.toString());
        URL.append("convert");
        URL.append("?api_key=");
        URL.append(SYSTEM_PROPERTIES.get("API_KEY1"));
        URL.append("&from=");
        URL.append(from);
        URL.append("&to=");
        URL.append(to);
        URL.append("&amount=");
        URL.append(amount);
        return getResponse(URL.toString());
    }

    public static JSONObject getRates(String base, String[] currencies) {
        StringBuilder URL = new StringBuilder(BASE_API_URL2);
        URL.append("latest?apikey=");
        URL.append(SYSTEM_PROPERTIES.getProperty("API_KEY2"));
        URL.append("&currencies=");
        if (currencies != null) {
            URL.append(String.join(",", currencies));
        }
        URL.append("&base_currency=");
        URL.append(base);
        return getResponse(URL.toString());
    }
}
