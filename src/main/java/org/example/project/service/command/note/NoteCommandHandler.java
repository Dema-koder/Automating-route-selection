package org.example.project.service.command.note;

public interface NoteCommandHandler {
    boolean canHandle(String command, boolean isPreviousAddNote);
}
