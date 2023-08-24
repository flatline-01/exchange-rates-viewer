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
        BASE_API_URL1.append("list");
        BASE_API_URL1.append("?api_key=");
        BASE_API_URL1.append(SYSTEM_PROPERTIES.get("API_KEY1"));
        return getResponse(BASE_API_URL1.toString());
    }

    public static JSONObject convert(double amount, String from, String to) {
        BASE_API_URL1.append("convert");
        BASE_API_URL1.append("?api_key=");
        BASE_API_URL1.append(SYSTEM_PROPERTIES.get("API_KEY1"));
        BASE_API_URL1.append("&from=");
        BASE_API_URL1.append(from);
        BASE_API_URL1.append("&to=");
        BASE_API_URL1.append(to);
        BASE_API_URL1.append("&amount=");
        BASE_API_URL1.append(amount);
        return getResponse(BASE_API_URL1.toString());
    }

    public static JSONObject getRates(String base, String[] currencies) {
        BASE_API_URL2.append("latest?apikey=");
        BASE_API_URL2.append(SYSTEM_PROPERTIES.getProperty("API_KEY2"));
        BASE_API_URL2.append("&currencies=");
        if (currencies != null) {
            BASE_API_URL2.append(String.join(",", currencies));
        }
        BASE_API_URL2.append("&base_currency=");
        BASE_API_URL2.append(base);
        return getResponse(BASE_API_URL2.toString());
    }
}
