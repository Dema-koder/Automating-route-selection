package org.example.project.service.job;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.example.project.data.leetcode.ProblemSet;
import org.example.project.data.leetcode.Question;
import org.example.project.data.leetcode.TopicTag;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
public class LeetcodeTags {

    @Getter
    private static final List<String>tags = new ArrayList<>();

    public static void getLeetcodeTags() {
        try {
            LocalDateTime start = LocalDateTime.now();
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://leetcode:3000/problems?limit=10000"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            ObjectMapper objectMapper = new ObjectMapper();

            ProblemSet problemset = objectMapper.readValue(response.body(), new TypeReference<>() {});

            Set<String> tagsSet = new TreeSet<>();
            for (Question question : problemset.getProblemsetQuestionList()) {
                for (TopicTag tag : question.getTopicTags()) {
                    tagsSet.add(tag.getSlug());
                }
            }

            tags.addAll(tagsSet);

            LocalDateTime finish = LocalDateTime.now();

            log.info("Время получения всех тэгов с литкода: {}с", Duration.between(start, finish).getSeconds());
            log.info("Количество тегов: {}", tags.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
