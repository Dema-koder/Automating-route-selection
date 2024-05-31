package org.example.project.service;

import org.example.project.domain.Note;
import org.example.project.domain.Users;
import org.example.project.repository.NoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private NoteService noteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetNotesByUser() {
        Users users = new Users();
        Note note1 = new Note();
        note1.setUsers(users);
        note1.setContent("Note 1");

        Note note2 = new Note();
        note2.setUsers(users);
        note2.setContent("Note 2");

        List<Note> notes = Arrays.asList(note1, note2);

        when(noteRepository.findByUsers(users)).thenReturn(notes);

        List<Note> result = noteService.getNotesByUser(users);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(noteRepository, times(1)).findByUsers(users);
    }

    @Test
    void testSaveNote() {
        Users users = new Users();
        String content = "New note";
        Note note = new Note();
        note.setUsers(users);
        note.setContent(content);

        when(noteRepository.save(any(Note.class))).thenReturn(note);

        Note result = noteService.saveNote(users, content);

        assertNotNull(result);
        assertEquals(content, result.getContent());
        assertEquals(users, result.getUsers());
        verify(noteRepository, times(1)).save(any(Note.class));
    }

    @Test
    void testDeleteNoteById() {
        Long id = 1L;
        doNothing().when(noteRepository).deleteById(id);

        noteService.deleteNoteById(id);

        verify(noteRepository, times(1)).deleteById(id);
    }
}