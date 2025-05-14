package org.example.project.service;

import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class StoncksPlotService {

    public String getPlotImage(String period) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            String pythonServiceUrl = "http://172.17.0.1:8000/plot?period=" + period;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(pythonServiceUrl))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
