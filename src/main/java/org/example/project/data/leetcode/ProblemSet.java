package org.example.project.data.leetcode;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.example.project.service.job.LeetcodeTags;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProblemSet {
    private List<Question> problemsetQuestionList;
}
