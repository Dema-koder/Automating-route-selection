//package org.example.project.service;
//
//import org.springframework.stereotype.Service;
//
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//
//@Service
//public class StoncksPlotService {
//
//    public String getPlotImage(String period) throws URISyntaxException {
//        HttpClient client = HttpClient.newHttpClient();
//        String pythonServiceUrl = "http://localhost:8000/plot?period=" + period;
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(new URI(pythonServiceUrl))
//                .GET()
//                .build();
//        RestTemplate restTemplate = new RestTemplate();
//        return restTemplate.getForObject(pythonServiceUrl, String.class);
//    }
//}
