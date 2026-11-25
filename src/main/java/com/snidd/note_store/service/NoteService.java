package com.snidd.note_store.service;


import com.snidd.note_store.entity.Note;
import com.snidd.note_store.CreateNoteRequest;
import com.snidd.note_store.entity.User;
import com.snidd.note_store.repository.NoteRepository;
import com.snidd.note_store.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

/**
 * Handle Note/s.
 */

@Service
public class NoteService {
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    public NoteService(NoteRepository noteRepository, UserRepository userRepository) {
        this.noteRepository = noteRepository;
        this.userRepository = userRepository;
    }


    /**
     * Create a note belonging to a user.
     * @param noteRequest request note.
     * @param username username.
     * @return note.
     */
    public Note createNote(CreateNoteRequest noteRequest, String username) {

        // Find current authenticated user in DB.
        User user = userRepository.findByUsername(username);

        // Create new Note.
        Note note = new Note();
        note.setTitle(noteRequest.getTitle());
        note.setContent(noteRequest.getContent());

        // Create connection between the user & the note.
        // user id will be saved in FK column 'user_id' in 'note' table.
        // User field in Note object will hold the connection between user and note.
        note.setUser(user);

        // Save the note to the DB.
        return noteRepository.save(note);
    }


    /**
     * Fetch a user's all notes.
     * @param username username.
     * @return all notes belonging to one user.
     */
    public List<Note> getNotes(String username) {
        User user = userRepository.findByUsername(username);
        return noteRepository.findByUserId(user.getId());
    }


    /**
     * Delete note based on user and note id.
     * @param noteId note Id.
     * @param username username.
     */
    public void deleteNote(Long noteId, String username) {

        // Find the note.
        Note note = noteRepository.findById(noteId).orElseThrow();

        // Find the user.
        User user = userRepository.findByUsername(username);

        // Check if the user owns the note.
        // get user id on note, then user id on current logged-in user.
        if(!note.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        // Delete the note.
        noteRepository.delete(note);
    }


    /**
     * Update note.
     * @param noteId note id.
     * @param username username.
     * @param noteRequest new note title and content.
     */
    public void updateNote(Long noteId, String username, CreateNoteRequest noteRequest) {
        // Find note to update.
        Note note = noteRepository.findById(noteId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND));

        // Find user.
        User user = userRepository.findByUsername(username);

        // Check that the user owns the note.
        if(!note.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        note.setTitle(noteRequest.getTitle());
        note.setContent(noteRequest.getContent());

        noteRepository.save(note);
    }

}
