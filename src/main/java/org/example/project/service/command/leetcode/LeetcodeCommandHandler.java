package org.example.project.service.command.leetcode;

public interface LeetcodeCommandHandler {
    boolean canHandle(String command, LeetcodeMode mode);
}
