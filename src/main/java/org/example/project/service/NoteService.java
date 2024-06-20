package org.example.project.service;

import org.example.project.domain.Note;
import org.example.project.domain.Users;
import org.example.project.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {
    @Autowired
    private NoteRepository noteRepository;


    public List<Note> getNotesByUser(Users user) {
        return noteRepository.findByUsers(user);
    }

    public Note saveNote(Users users, String content) {
        Note note = new Note();
        note.setContent(content);
        note.setUsers(users);
        return noteRepository.save(note);
    }

    public void deleteNoteById(Long id) {
        noteRepository.deleteById(id);
    }
}
