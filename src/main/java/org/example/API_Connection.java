package org.example;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class API_Connection {
    private static final String API_URL;

    static {
        API_URL = "http://data.fixer.io/api/latest?access_key=" + System.getenv("API_KEY");
    }

    public static void connect() {
        try {
            URI uri = new URI(API_URL);
            HttpClient client = HttpClient
                    .newBuilder()
                    .build();
            HttpRequest request = HttpRequest
                    .newBuilder()
                    .GET()
                    .uri(uri)
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
