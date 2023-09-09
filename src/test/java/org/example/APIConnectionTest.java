package org.example;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.*;
import java.util.Properties;
import static org.junit.jupiter.api.Assertions.*;

class APIConnectionTest {
    private static Properties SYSTEM_PROPERTIES = System.getProperties();

    @Test
    public void ensureThatUserAPI1CallReturnStatusCode200() throws Exception {
        HttpClient client = HttpClient.newBuilder().build();
        Field field = APIConnection.class.getDeclaredField("BASE_API_URL1");
        field.setAccessible(true);
        StringBuilder uri = new StringBuilder(field.get(new APIConnection()).toString());
        uri.append("list");
        uri.append("?api_key=");
        uri.append(SYSTEM_PROPERTIES.get("API_KEY1"));
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(uri.toString())).build();
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void ensureThatUserAPI2CallReturnStatusCode200() throws Exception {
        HttpClient client = HttpClient.newBuilder().build();
        Field field = APIConnection.class.getDeclaredField("BASE_API_URL2");
        field.setAccessible(true);
        StringBuilder uri = new StringBuilder(field.get(new APIConnection()).toString());
        uri.append("status?apikey=");
        uri.append(SYSTEM_PROPERTIES.getProperty("API_KEY2"));
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(uri.toString())).build();
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
    }
}