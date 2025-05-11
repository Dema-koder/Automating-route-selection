package org.example.project.service.command.gpt;

public interface GPTCommandHandler {
    boolean canHandle(String command);
}
