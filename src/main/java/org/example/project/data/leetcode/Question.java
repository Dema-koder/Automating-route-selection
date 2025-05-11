package org.example.project.data.leetcode;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Question {
    private String difficulty;
    private String title;
    private String titleSlug;
    private List<TopicTag> topicTags;
}