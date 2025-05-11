package org.example.project.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.example.project.data.leetcode.ProblemSet;
import org.example.project.data.leetcode.Question;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

@Slf4j
@Service
public class LeetcodeService {

    public String getProblemWithTagAndDifficulty(String difficulty, String userTag) {
        var problems = getProblemsWithTagAndDifficulty(difficulty, userTag);
        if (problems == null)
            return "Sorry, command did not performed";
        int randomNumber = new Random().nextInt(problems.size());
        var randomProblem = problems.get(randomNumber);
        return "Title: " + randomProblem.title + "\nLink: https://leetcode.com/problems/" + randomProblem.titleSlug;
    }

    private List<Problem> getProblemsWithTagAndDifficulty(String difficulty, String userTag) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://leetcode:3000/problems?tags=" + userTag + "&limit=10000"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper objectMapper = new ObjectMapper();

            ProblemSet problemset = objectMapper.readValue(response.body(), new TypeReference<>() {});

            List<Problem> problems = new ArrayList<>();
            for (Question question : problemset.getProblemsetQuestionList()) {
                if (question.getDifficulty().equals(difficulty))
                    problems.add(new Problem(question.getTitle(), question.getTitleSlug()));
            }
            return problems;
        } catch (Exception e) {
            log.error("Exception: {}", e.getMessage());
            return null;
        }
    }

    @Data
    @AllArgsConstructor
    class Problem{
        private String title;
        private String titleSlug;
    }
}
